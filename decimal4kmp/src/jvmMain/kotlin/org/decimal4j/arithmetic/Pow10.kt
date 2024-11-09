/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2024 decimal4j (tools4j), Marco Terzer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.decimal4j.arithmetic

import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.scale.Scale18f
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales
import org.decimal4j.truncate.DecimalRounding
import org.decimal4j.truncate.TruncatedPart
import kotlin.math.abs

/**
 * Contains methods for multiplications and divisions with powers of ten.
 */
internal object Pow10 {
    /**
     * Calculates the multiple by a power of 10 truncating the result if
     * necessary for negative `n`. Overflows are silently truncated.
     *
     * @param uDecimal
     * the value to multiply
     * @param n
     * the power-ten exponent
     * @return `round<sub>DOWN</sub>(uDecimal * 10<sup>n</sup>)`
     */
	@JvmStatic
	fun multiplyByPowerOf10(uDecimal: Long, n: Int): Long {
        if ((uDecimal == 0L) or (n == 0)) {
            return uDecimal
        }
        if (n > 0) {
            var pos = n
            var result = uDecimal
            // NOTE: result will be 0 after at most 1+64/18 rounds
            // because 10^64 contains 2^64 which is a shift left by 64
            while (pos > 18) {
                result = Scale18f.INSTANCE.multiplyByScaleFactor(result)
                if (result == 0L) {
                    return 0
                }
                pos -= 18
            }
            val scaleMetrics = Scales.getScaleMetrics(pos)
            return scaleMetrics.multiplyByScaleFactor(result)
        } else {
            if (n >= -18) {
                val scaleMetrics = Scales.getScaleMetrics(-n)
                return scaleMetrics.divideByScaleFactor(uDecimal)
            }
            // truncated result is 0
            return 0
        }
    }

    /**
     * Calculates the multiple by a power of 10 rounding the result if necessary
     * for negative `n`. Overflows are silently truncated.
     *
     * @param rounding
     * the rounding to apply if necessary
     * @param uDecimal
     * the value to multiply
     * @param n
     * the power-ten exponent
     * @return `round(uDecimal * 10<sup>n</sup>)`
     */
	@JvmStatic
	fun multiplyByPowerOf10(rounding: DecimalRounding, uDecimal: Long, n: Int): Long {
        if ((uDecimal == 0L) or (n == 0)) {
            return uDecimal
        }
        if (n > 0) {
            var pos = n
            var result = uDecimal
            // NOTE: result will be 0 after at most 1+64/18 rounds
            // because 10^64 contains 2^64 which is a shift left by 64
            while (pos > 18) {
                result = Scale18f.INSTANCE.multiplyByScaleFactor(result)
                if (result == 0L) {
                    return 0
                }
                pos -= 18
            }
            val scaleMetrics = Scales.getScaleMetrics(pos)
            return scaleMetrics.multiplyByScaleFactor(result)
        } else {
            if (n >= -18) {
                val scaleMetrics = Scales.getScaleMetrics(-n)
                val truncated = scaleMetrics.divideByScaleFactor(uDecimal)
                val rem = uDecimal - scaleMetrics.multiplyByScaleFactor(truncated)
                val inc = Rounding.calculateRoundingIncrement(
                    rounding, truncated, rem,
                    scaleMetrics.getScaleFactor()
                ).toLong()
                return truncated + inc
            } else if (n == -19) {
                return rounding.calculateRoundingIncrement(
                    java.lang.Long.signum(uDecimal), 0,
                    Rounding.truncatedPartForScale19(uDecimal)
                ).toLong()
            }
            // truncated part is always larger 0 (see first if)
            // and less than 0.5 because abs(Long.MIN_VALUE) / 10^20 < 0.5
            return rounding.calculateRoundingIncrement(
                java.lang.Long.signum(uDecimal), 0,
                TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO
            ).toLong()
        }
    }

    /**
     * Calculates the multiple by a power of 10 truncating the result if
     * necessary for negative `n`. An exception is thrown if an overflow
     * occurs.
     *
     * @param arith
     * the arithmetic associated with the value
     * @param uDecimal
     * the value to multiply
     * @param n
     * the power-ten exponent
     * @return `round<sub>DOWN</sub>(uDecimal * 10<sup>n</sup>)`
     * @throws ArithmeticException
     * if an overflow occurs and the arithmetic's
     * `OverflowMode` is set to throw an exception
     */
	@JvmStatic
	fun multiplyByPowerOf10Checked(arith: DecimalArithmetic, uDecimal: Long, n: Int): Long {
        if ((uDecimal == 0L) or (n == 0)) {
            return uDecimal
        }

        if (n > 0) {
            if (n > 18) {
                throw ArithmeticException("Overflow: " + arith.toString(uDecimal) + " * 10^" + n)
            }

            val scaleMetrics = Scales.getScaleMetrics(n)
            return scaleMetrics.multiplyByScaleFactorExact(uDecimal)
        } else {
            if (n >= -18) {
                val scaleMetrics = Scales.getScaleMetrics(-n)
                return scaleMetrics.divideByScaleFactor(uDecimal)
            }
            return 0
        }
    }

    /**
     * Calculates the multiple by a power of 10 rounding the result if necessary
     * for negative `n`. An exception is thrown if an overflow occurs.
     *
     * @param arith
     * the arithmetic associated with the value
     * @param rounding
     * the rounding to apply if necessary
     * @param uDecimal
     * the value to multiply
     * @param n
     * the power-ten exponent
     * @return `round(uDecimal * 10<sup>n</sup>)`
     * @throws ArithmeticException
     * if an overflow occurs and the arithmetic's
     * `OverflowMode` is set to throw an exception
     */
	@JvmStatic
	fun multiplyByPowerOf10Checked(arith: DecimalArithmetic, rounding: DecimalRounding, uDecimal: Long, n: Int): Long {
        if ((uDecimal == 0L) or (n == 0)) {
            return uDecimal
        }

        if (rounding === DecimalRounding.DOWN) {
            return multiplyByPowerOf10Checked(arith, uDecimal, abs(n))
        }

        if (n > 0) {
            if (n > 18) {
                throw ArithmeticException("Overflow: " + arith.toString(uDecimal) + " * 10^" + n)
            }

            val scaleMetrics = Scales.getScaleMetrics(n)
            return scaleMetrics.multiplyByScaleFactorExact(uDecimal)
        } else {
            if (n >= -18) {
                val scaleMetrics = Scales.getScaleMetrics(-n)
                val truncated = scaleMetrics.divideByScaleFactor(uDecimal)
                val rem = uDecimal - scaleMetrics.multiplyByScaleFactor(truncated)
                val inc = Rounding.calculateRoundingIncrement(
                    rounding, truncated, rem,
                    scaleMetrics.getScaleFactor()
                ).toLong()
                return truncated + inc
            } else if (n == -19) {
                return rounding.calculateRoundingIncrement(
                    java.lang.Long.signum(uDecimal), 0,
                    Rounding.truncatedPartForScale19(uDecimal)
                ).toLong()
            }
            // truncated part is always larger 0 (see first if)
            // and less than 0.5 because abs(Long.MIN_VALUE) / 10^20 < 0.5
            return rounding.calculateRoundingIncrement(
                java.lang.Long.signum(uDecimal), 0,
                TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO
            ).toLong()
        }
    }

    /**
     * Divides the given value by a power of 10 truncating the result if
     * necessary. Overflows are silently truncated.
     *
     * @param uDecimal
     * the value to divide
     * @param n
     * the power-ten exponent
     * @return `round<sub>DOWN</sub>(uDecimal / 10<sup>n</sup>)`
     */
	@JvmStatic
	fun divideByPowerOf10(uDecimal: Long, n: Int): Long {
        if ((uDecimal == 0L) or (n == 0)) {
            return uDecimal
        }

        if (n > 0) {
            if (n > 18) {
                return 0 // truncated result is 0
            }

            val scaleMetrics = Scales.getScaleMetrics(n)
            return scaleMetrics.divideByScaleFactor(uDecimal)
        } else {
            var pos = n
            var result = uDecimal
            // NOTE: result will be 0 after at most 1+64/18 rounds
            // because 10^64 contains 2^64 which is a shift left by 64
            while (pos < -18) {
                result = Scale18f.INSTANCE.multiplyByScaleFactor(result)
                if (result == 0L) {
                    return 0
                }
                pos += 18
            }
            val scaleMetrics = Scales.getScaleMetrics(-pos)
            return scaleMetrics.multiplyByScaleFactor(result)
        }
    }

    /**
     * Divides the given value by a power of 10 rounding the result if
     * necessary. Overflows are silently truncated.
     *
     * @param rounding
     * the rounding to apply if necessary
     * @param uDecimal
     * the value to divide
     * @param n
     * the power-ten exponent
     * @return `round(uDecimal / 10<sup>n</sup>)`
     */
	@JvmStatic
	fun divideByPowerOf10(rounding: DecimalRounding, uDecimal: Long, n: Int): Long {
        if ((uDecimal == 0L) or (n == 0)) {
            return uDecimal
        }

        if (rounding === DecimalRounding.DOWN) {
            return divideByPowerOf10(uDecimal, n)
        }

        if (n > 0) {
            if (n > 19) {
                // truncated part is always larger 0 (see first if)
                // and less than 0.5 because abs(Long.MIN_VALUE) / 10^20 < 0.5
                return rounding.calculateRoundingIncrement(
                    java.lang.Long.signum(uDecimal), 0,
                    TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO
                ).toLong()
            } else if (n == 19) {
                return rounding.calculateRoundingIncrement(
                    java.lang.Long.signum(uDecimal), 0,
                    Rounding.truncatedPartForScale19(uDecimal)
                ).toLong()
            }

            val scaleMetrics = Scales.getScaleMetrics(n)
            val truncated = scaleMetrics.divideByScaleFactor(uDecimal)
            val rem = uDecimal - scaleMetrics.multiplyByScaleFactor(truncated)
            val inc = Rounding.calculateRoundingIncrement(
                rounding, truncated, rem,
                scaleMetrics.getScaleFactor()
            ).toLong()
            return truncated + inc
        } else {
            var pos = n
            var result = uDecimal
            // NOTE: result will be 0 after at most 1+64/18 rounds
            // because 10^64 contains 2^64 which is a shift left by 64
            while (pos < -18) {
                result = Scale18f.INSTANCE.multiplyByScaleFactor(result)
                if (result == 0L) {
                    return 0
                }
                pos += 18
            }
            val scaleMetrics = Scales.getScaleMetrics(-pos)
            return scaleMetrics.multiplyByScaleFactor(result)
        }
    }

    /**
     * Divides the given value by a power of 10 truncating the result if
     * necessary. An exception is thrown if an overflow occurs.
     *
     * @param arith
     * the arithmetic associated with the value
     * @param uDecimal
     * the value to divide
     * @param n
     * the power-ten exponent
     * @return `round<sub>DOWN</sub>(uDecimal / 10<sup>n</sup>)`
     * @throws ArithmeticException
     * if an overflow occurs and the arithmetic's
     * `OverflowMode` is set to throw an exception
     */
	@JvmStatic
	fun divideByPowerOf10Checked(arith: DecimalArithmetic, uDecimal: Long, n: Int): Long {
        if ((uDecimal == 0L) or (n == 0)) {
            return uDecimal
        }

        if (n > 0) {
            if (n > 18) {
                return 0
            }

            val scaleMetrics = Scales.getScaleMetrics(n)
            return scaleMetrics.divideByScaleFactor(uDecimal)
        } else {
            if (n >= -18) {
                val scaleMetrics = Scales.getScaleMetrics(-n)
                return scaleMetrics.multiplyByScaleFactorExact(uDecimal)
            }
            throw ArithmeticException("Overflow: " + arith.toString(uDecimal) + " / 10^" + n)
        }
    }

    /**
     * Divides the given value by a power of 10 rounding the result if
     * necessary. An exception is thrown if an overflow occurs.
     *
     * @param arith
     * the arithmetic associated with the value
     * @param rounding
     * the rounding to apply if necessary
     * @param uDecimal
     * the value to divide
     * @param n
     * the power-ten exponent
     * @return `round(uDecimal / 10<sup>n</sup>)`
     * @throws ArithmeticException
     * if an overflow occurs and the arithmetic's
     * `OverflowMode` is set to throw an exception
     */
	@JvmStatic
	fun divideByPowerOf10Checked(arith: DecimalArithmetic, rounding: DecimalRounding, uDecimal: Long, n: Int): Long {
        if ((uDecimal == 0L) or (n == 0)) {
            return uDecimal
        }

        if (rounding === DecimalRounding.DOWN) {
            return divideByPowerOf10Checked(arith, uDecimal, abs(n))
        }

        if (n > 0) {
            if (n > 19) {
                // truncated part is always larger 0 (see first if)
                // and less than 0.5 because abs(Long.MIN_VALUE) / 10^20 < 0.5
                return rounding.calculateRoundingIncrement(
                    java.lang.Long.signum(uDecimal), 0,
                    TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO
                ).toLong()
            } else if (n == 19) {
                return rounding.calculateRoundingIncrement(
                    java.lang.Long.signum(uDecimal), 0,
                    Rounding.truncatedPartForScale19(uDecimal)
                ).toLong()
            }

            val scaleMetrics = Scales.getScaleMetrics(n)
            val truncated = scaleMetrics.divideByScaleFactor(uDecimal)
            val rem = uDecimal - scaleMetrics.multiplyByScaleFactor(truncated)
            val inc = Rounding.calculateRoundingIncrement(
                rounding, truncated, rem,
                scaleMetrics.getScaleFactor()
            ).toLong()
            return truncated + inc
        } else {
            if (n < -18) {
                throw ArithmeticException("Overflow: " + arith.toString(uDecimal) + " / 10^" + n)
            }

            val scaleMetrics = Scales.getScaleMetrics(-n)
            return scaleMetrics.multiplyByScaleFactorExact(uDecimal)
        }
    }

    /**
     * Divides the specified dividend by a power of ten truncating the result if
     * necessary. Overflows are silently truncated.
     *
     * @param uDecimalDividend
     * the dividend to divide
     * @param dividendMetrics
     * the arithmetics associated with the dividend
     * @param pow10divisorIsPositive
     * true if the divisor is positive
     * @param pow10divisorMetrics
     * the metrics reflecting the power-ten-division
     * @return `round<sub>DOWN</sub>(uDecimalDividend / 10<sup>(scale - scale10)</sup>)`
     * where scale is the `scale` of the dividend and
     * `scale10` is the exponent of the power-ten divisor (negated
     * if `pow10divisorIsPositive==false`)
     */
    fun divideByPowerOf10(
        uDecimalDividend: Long,
        dividendMetrics: ScaleMetrics,
        pow10divisorIsPositive: Boolean,
        pow10divisorMetrics: ScaleMetrics
    ): Long {
        val scaleDiff = dividendMetrics.getScale() - pow10divisorMetrics.getScale()
        val quot: Long
        if (scaleDiff <= 0) {
            // divide
            val scaleMetrics = Scales.getScaleMetrics(-scaleDiff)
            quot = scaleMetrics.divideByScaleFactor(uDecimalDividend)
        } else {
            // multiply
            val scaleMetrics = Scales.getScaleMetrics(scaleDiff)
            quot = scaleMetrics.multiplyByScaleFactor(uDecimalDividend)
        }
        return if (pow10divisorIsPositive) quot else -quot
    }

    /**
     * Divides the specified dividend by a power of ten rounding the result if
     * necessary. Overflows are silently truncated.
     *
     * @param rounding
     * the rounding to apply if rounding is necessary
     * @param uDecimalDividend
     * the dividend to divide
     * @param dividendMetrics
     * the arithmetics associated with the dividend
     * @param pow10divisorIsPositive
     * true if the divisor is positive
     * @param pow10divisorMetrics
     * the metrics reflecting the power-ten-division
     * @return `round(uDecimalDividend / 10<sup>(scale - scale10)</sup>)`
     * where scale is the `scale` of the dividend and
     * `scale10` is the exponent of the power-ten divisor (negated
     * if `pow10divisorIsPositive==false`)
     */
    fun divideByPowerOf10(
        rounding: DecimalRounding,
        uDecimalDividend: Long,
        dividendMetrics: ScaleMetrics,
        pow10divisorIsPositive: Boolean,
        pow10divisorMetrics: ScaleMetrics
    ): Long {
        val scaleDiff = dividendMetrics.getScale() - pow10divisorMetrics.getScale()
        if (scaleDiff <= 0) {
            // divide
            val scaler = Scales.getScaleMetrics(-scaleDiff)
            val truncatedValue = scaler.divideByScaleFactor(uDecimalDividend)
            val truncatedDigits = uDecimalDividend - scaler.multiplyByScaleFactor(truncatedValue)
            if (pow10divisorIsPositive) {
                return truncatedValue + Rounding.calculateRoundingIncrementForDivision(
                    rounding, truncatedValue,
                    truncatedDigits, scaler.getScaleFactor()
                )
            }
            return -truncatedValue + Rounding.calculateRoundingIncrementForDivision(
                rounding, -truncatedValue,
                -truncatedDigits, scaler.getScaleFactor()
            )
        } else {
            // multiply
            val scaler = Scales.getScaleMetrics(scaleDiff)
            val quot = scaler.multiplyByScaleFactor(uDecimalDividend)
            return if (pow10divisorIsPositive) quot else -quot
        }
    }

    /**
     * Divides the specified dividend by a power of ten truncating the result if
     * necessary. An exception is thrown if an overflow occurs.
     *
     * @param arith
     * the arithmetic associated with the dividend value
     * @param uDecimalDividend
     * the dividend to divide
     * @param dividendMetrics
     * the arithmetics associated with the dividend
     * @param pow10divisorIsPositive
     * true if the divisor is positive
     * @param pow10divisorMetrics
     * the metrics reflecting the power-ten-division
     * @return `round<sub>DOWN</sub>(uDecimalDividend / 10<sup>(scale - scale10)</sup>)`
     * where scale is the `scale` of the dividend and
     * `scale10` is the exponent of the power-ten divisor (negated
     * if `pow10divisorIsPositive==false`)
     * @throws ArithmeticException
     * if an overflow occurs and the arithmetic's
     * `OverflowMode` is set to throw an exception
     */
    fun divideByPowerOf10Checked(
        arith: DecimalArithmetic,
        uDecimalDividend: Long,
        dividendMetrics: ScaleMetrics,
        pow10divisorIsPositive: Boolean,
        pow10divisorMetrics: ScaleMetrics
    ): Long {
        val scaleDiff = dividendMetrics.getScale() - pow10divisorMetrics.getScale()
        val quot: Long
        if (scaleDiff <= 0) {
            // divide
            val scaleMetrics = Scales.getScaleMetrics(-scaleDiff)
            quot = scaleMetrics.divideByScaleFactor(uDecimalDividend)
        } else {
            // multiply
            val scaleMetrics = Scales.getScaleMetrics(scaleDiff)
            quot = scaleMetrics.multiplyByScaleFactorExact(uDecimalDividend)
        }
        return if (pow10divisorIsPositive) quot else arith.negate(quot)
    }

    /**
     * Divides the specified dividend by a power of ten rounding the result if
     * necessary. An exception is thrown if an overflow occurs.
     *
     * @param arith
     * the arithmetic associated with the dividend value
     * @param rounding
     * the rounding to apply if rounding is necessary
     * @param uDecimalDividend
     * the dividend to divide
     * @param dividendMetrics
     * the arithmetics associated with the dividend
     * @param pow10divisorIsPositive
     * true if the divisor is positive
     * @param pow10divisorMetrics
     * the metrics reflecting the power-ten-division
     * @return `round(uDecimalDividend / 10<sup>(scale - scale10)</sup>)`
     * where scale is the `scale` of the dividend and
     * `scale10` is the exponent of the power-ten divisor (negated
     * if `pow10divisorIsPositive==false`)
     * @throws ArithmeticException
     * if an overflow occurs and the arithmetic's
     * `OverflowMode` is set to throw an exception
     */
    fun divideByPowerOf10Checked(
        arith: DecimalArithmetic,
        rounding: DecimalRounding,
        uDecimalDividend: Long,
        dividendMetrics: ScaleMetrics,
        pow10divisorIsPositive: Boolean,
        pow10divisorMetrics: ScaleMetrics
    ): Long {
        val scaleDiff = dividendMetrics.getScale() - pow10divisorMetrics.getScale()
        val quot: Long
        if (scaleDiff <= 0) {
            // divide
            val scaleMetrics = Scales.getScaleMetrics(-scaleDiff)
            quot = scaleMetrics.divideByScaleFactor(uDecimalDividend)

            val truncatedDigits = uDecimalDividend - scaleMetrics.multiplyByScaleFactor(quot)
            if (pow10divisorIsPositive) {
                return quot + Rounding.calculateRoundingIncrementForDivision(
                    rounding, quot, truncatedDigits,
                    scaleMetrics.getScaleFactor()
                )
            }
            return -quot + Rounding.calculateRoundingIncrementForDivision(
                rounding, -quot, -truncatedDigits,
                scaleMetrics.getScaleFactor()
            )
        } else {
            // multiply
            val scaleMetrics = Scales.getScaleMetrics(scaleDiff)
            quot = scaleMetrics.multiplyByScaleFactorExact(uDecimalDividend)
        }
        return if (pow10divisorIsPositive) quot else arith.negate(quot)
    }
}
