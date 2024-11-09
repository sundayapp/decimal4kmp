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
import org.decimal4j.arithmetic.Checked.add
import org.decimal4j.arithmetic.Checked.addLong
import org.decimal4j.arithmetic.Checked.divideByLong
import org.decimal4j.arithmetic.Checked.divideLong
import org.decimal4j.arithmetic.Checked.isDivideOverflow
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales
import org.decimal4j.truncate.DecimalRounding
import kotlin.math.abs

/**
 * Provides static methods to calculate division results.
 */
internal object Div {
    private const val LONG_MASK = 0xffffffffL

    /**
     * Calculates unchecked division by a long value with rounding.
     *
     * @param rounding
     * the decimal rounding to apply if rounding is necessary
     * @param uDecimalDividend
     * the unscaled decimal dividend
     * @param lDivisor
     * the long divisor
     * @return the division result with rounding and no overflow checks
     */
    @JvmStatic
    fun divideByLong(rounding: DecimalRounding, uDecimalDividend: Long, lDivisor: Long): Long {
        val quotient = uDecimalDividend / lDivisor
        val remainder = uDecimalDividend - quotient * lDivisor
        return quotient + Rounding.calculateRoundingIncrementForDivision(rounding, quotient, remainder, lDivisor)
    }

    /**
     * Calculates checked division by a long value with rounding.
     *
     * @param arith
     * the arithmetic used to format numbers when throwing exceptions
     * @param rounding
     * the decimal rounding to apply if rounding is necessary
     * @param uDecimalDividend
     * the unscaled decimal dividend
     * @param lDivisor
     * the long divisor
     * @return the division result with rounding and overflow checks
     */
    fun divideByLongChecked(
        arith: DecimalArithmetic,
        rounding: DecimalRounding,
        uDecimalDividend: Long,
        lDivisor: Long
    ): Long {
        if (lDivisor == 0L) {
            throw ArithmeticException("Division by zero: " + arith.toString(uDecimalDividend) + " / " + lDivisor)
        }
        try {
            val quotient = divideByLong(arith, uDecimalDividend, lDivisor)
            val remainder = uDecimalDividend - quotient * lDivisor
            val inc = Rounding.calculateRoundingIncrementForDivision(rounding, quotient, remainder, lDivisor).toLong()
            return add(arith, quotient, inc)
        } catch (e: ArithmeticException) {
            Exceptions.rethrowIfRoundingNecessary(e)
            throw Exceptions.newArithmeticExceptionWithCause(
                ("Overflow: " + arith.toString(uDecimalDividend) + " / "
                        + lDivisor), e
            )
        }
    }

    /**
     * Calculates `(uDecimalDividend * scaleFactor) / uDecimalDivisor`
     * without rounding and overflow checks.
     *
     * @param arith
     * the arithmetic with scale metrics and overflow mode
     * @param uDecimalDividend
     * the unscaled decimal dividend
     * @param uDecimalDivisor
     * the unscaled decimal divisor
     * @return the division result without rounding and without overflow checks.
     */
    @JvmStatic
    fun divide(arith: DecimalArithmetic, uDecimalDividend: Long, uDecimalDivisor: Long): Long {
        // special cases first
        val special = SpecialDivisionResult.getFor(arith, uDecimalDividend, uDecimalDivisor)
        if (special != null) {
            return special.divide(arith, uDecimalDividend, uDecimalDivisor)
        }
        // div by power of 10
        val scaleMetrics = arith.scaleMetrics
        val pow10 = Scales.findByScaleFactor(abs(uDecimalDivisor))
        if (pow10 != null) {
            return Pow10.divideByPowerOf10(uDecimalDividend, scaleMetrics, uDecimalDivisor > 0, pow10)
        }
        return divide(uDecimalDividend, scaleMetrics, uDecimalDivisor)
    }

    /**
     * Calculates unchecked division by an unscaled value with the given scale
     * without rounding and overflow checks.
     *
     * @param uDecimalDividend
     * the unscaled decimal dividend
     * @param unscaledDivisor
     * the long divisor
     * @param scale
     * the scale of the divisor
     * @return the division result without rounding and without overflow checks
     */
    @JvmStatic
    fun divideByUnscaled(uDecimalDividend: Long, unscaledDivisor: Long, scale: Int): Long {
        require(scale <= Scales.MAX_SCALE) { "Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale }
        if ((unscaledDivisor == 0L) or (scale == 0)) {
            return uDecimalDividend / unscaledDivisor
        } else if (scale < 0) {
            if (isDivideOverflow(uDecimalDividend, unscaledDivisor)) {
                return -Pow10.multiplyByPowerOf10(uDecimalDividend, scale)
            }
            return Pow10.multiplyByPowerOf10(uDecimalDividend / unscaledDivisor, scale)
        }
        val divisorMetrics = Scales.getScaleMetrics(scale)
        return divide(uDecimalDividend, divisorMetrics, unscaledDivisor)
    }

    /**
     * Calculates `(uDecimalDividend * scaleFactor) / uDecimalDivisor`
     * without rounding and overflow checks.
     *
     * @param uDecimalDividend
     * the unscaled decimal dividend
     * @param divisorMetrics
     * the metrics associated with the divisor
     * @param uDecimalDivisor
     * the unscaled decimal divisor
     * @return the division result without rounding and without overflow checks.
     */
    private fun divide(uDecimalDividend: Long, divisorMetrics: ScaleMetrics, uDecimalDivisor: Long): Long {
        // WE WANT: uDecimalDividend * 10^scale / unscaledDivisor
        if (divisorMetrics.isValidIntegerValue(uDecimalDividend)) {
            // just do it, multiplication result fits in long
            return divisorMetrics.multiplyByScaleFactor(uDecimalDividend) / uDecimalDivisor
        }
        if (divisorMetrics.isValidIntegerValue(uDecimalDivisor)) {
            // perform component wise division (reminder fits in long after scaling)
            val integralPart = uDecimalDividend / uDecimalDivisor
            val remainder = uDecimalDividend - integralPart * uDecimalDivisor
            val fractionalPart = divisorMetrics.multiplyByScaleFactor(remainder) / uDecimalDivisor
            return divisorMetrics.multiplyByScaleFactor(integralPart) + fractionalPart
        }
        return scaleTo128divBy64(divisorMetrics, DecimalRounding.DOWN, uDecimalDividend, uDecimalDivisor)
    }

    /**
     * Calculates `(uDecimalDividend * scaleFactor) / uDecimalDivisor`
     * with rounding and without overflow checks.
     *
     * @param arith
     * the arithmetic with scale metrics and overflow mode
     * @param rounding
     * the decimal rounding to apply if rounding is necessary
     * @param uDecimalDividend
     * the unscaled decimal dividend
     * @param uDecimalDivisor
     * the unscaled decimal divisor
     * @return the division result with rounding and without overflow checks
     */
    @JvmStatic
    fun divide(
        arith: DecimalArithmetic,
        rounding: DecimalRounding,
        uDecimalDividend: Long,
        uDecimalDivisor: Long
    ): Long {
        // special cases first
        val special = SpecialDivisionResult.getFor(arith, uDecimalDividend, uDecimalDivisor)
        if (special != null) {
            return special.divide(arith, uDecimalDividend, uDecimalDivisor)
        }
        // div by power of 10
        val scaleMetrics = arith.scaleMetrics
        val pow10 = Scales.findByScaleFactor(abs(uDecimalDivisor))
        if (pow10 != null) {
            return Pow10.divideByPowerOf10(rounding, uDecimalDividend, scaleMetrics, uDecimalDivisor > 0, pow10)
        }
        return divide(rounding, uDecimalDividend, scaleMetrics, uDecimalDivisor)
    }

    /**
     * Calculates unchecked division by an unscaled value with the given scale
     * without rounding.
     *
     * @param rounding
     * the rounding to apply
     * @param uDecimalDividend
     * the unscaled decimal dividend
     * @param unscaledDivisor
     * the long divisor
     * @param scale
     * the scale of the divisor
     * @return the division result without rounding and without overflow checks
     */
    @JvmStatic
    fun divideByUnscaled(rounding: DecimalRounding, uDecimalDividend: Long, unscaledDivisor: Long, scale: Int): Long {
        require(scale <= Scales.MAX_SCALE) { "Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale }
        if ((unscaledDivisor == 0L) or (scale == 0)) {
            return divideByLong(rounding, uDecimalDividend, unscaledDivisor)
        } else if (scale < 0) {
            if (isDivideOverflow(uDecimalDividend, unscaledDivisor)) {
                return -Pow10.multiplyByPowerOf10(
                    RoundingInverse.SIGN_REVERSION.invert(rounding),
                    uDecimalDividend,
                    scale
                )
            }
            //NOTE: rounding twice could be a problem here, e.g. consider HALF_UP with 10.51 and 10.49
            val quot = when (rounding) {
                DecimalRounding.HALF_UP -> uDecimalDividend / unscaledDivisor //DOWN
                DecimalRounding.HALF_DOWN -> divideByLong(
                    DecimalRounding.UP,
                    uDecimalDividend,
                    unscaledDivisor
                )

                DecimalRounding.HALF_EVEN -> {
                    //try HALF_UP first
                    val quotD = uDecimalDividend / unscaledDivisor //DOWN
                    val powHU = Pow10.multiplyByPowerOf10(DecimalRounding.HALF_UP, quotD, scale)
                    if (0L == (powHU and 0x1L)) {
                        //even, we're done
                        return powHU
                    }
                    //odd, HALF_DOWN may be even in which case it should win
                    val quotU =
                        divideByLong(DecimalRounding.UP, uDecimalDividend, unscaledDivisor)
                    val powHD = Pow10.multiplyByPowerOf10(DecimalRounding.HALF_DOWN, quotU, scale)
                    return powHD //either even or the same as powHU
                }

                else -> divideByLong(rounding, uDecimalDividend, unscaledDivisor)
            }
            return Pow10.multiplyByPowerOf10(rounding, quot, scale)
        }
        val divisorMetrics = Scales.getScaleMetrics(scale)
        return divide(rounding, uDecimalDividend, divisorMetrics, unscaledDivisor)
    }

    /**
     * Calculates `(uDecimalDividend * scaleFactor) / uDecimalDivisor`
     * with rounding and without overflow checks.
     *
     * @param rounding
     * the decimal rounding to apply if rounding is necessary
     * @param uDecimalDividend
     * the unscaled decimal dividend
     * @param divisorMetrics
     * the metrics associated with the divisor
     * @param uDecimalDivisor
     * the unscaled decimal divisor
     * @return the division result with rounding and without overflow checks
     */
    private fun divide(
        rounding: DecimalRounding,
        uDecimalDividend: Long,
        divisorMetrics: ScaleMetrics,
        uDecimalDivisor: Long
    ): Long {
        if (divisorMetrics.isValidIntegerValue(uDecimalDividend)) {
            // just do it, multiplication result fits in long
            val scaledDividend = divisorMetrics.multiplyByScaleFactor(uDecimalDividend)
            val quot = scaledDividend / uDecimalDivisor
            val rem = scaledDividend - quot * uDecimalDivisor
            return quot + Rounding.calculateRoundingIncrementForDivision(rounding, quot, rem, uDecimalDivisor)
        }
        if (divisorMetrics.isValidIntegerValue(uDecimalDivisor)) {
            // perform component wise division (reminder fits in long after
            // scaling)
            val integralPart = uDecimalDividend / uDecimalDivisor
            val remainder = uDecimalDividend - integralPart * uDecimalDivisor
            val scaledReminder = divisorMetrics.multiplyByScaleFactor(remainder)
            val fractionalPart = scaledReminder / uDecimalDivisor
            val subFractionalPart = scaledReminder - fractionalPart * uDecimalDivisor
            val truncated = divisorMetrics.multiplyByScaleFactor(integralPart) + fractionalPart
            return truncated + Rounding.calculateRoundingIncrementForDivision(
                rounding,
                truncated,
                subFractionalPart,
                uDecimalDivisor
            )
        }
        return scaleTo128divBy64(divisorMetrics, rounding, uDecimalDividend, uDecimalDivisor)
    }

    /**
     * Calculates `(uDecimalDividend * scaleFactor) / uDecimalDivisor`
     * without rounding and with overflow checks.
     *
     * @param arith
     * the arithmetic with scale metrics and overflow mode
     * @param uDecimalDividend
     * the unscaled decimal dividend
     * @param uDecimalDivisor
     * the unscaled decimal divisor
     * @return the division result without rounding and with overflow checks
     */
    fun divideChecked(arith: DecimalArithmetic, uDecimalDividend: Long, uDecimalDivisor: Long): Long {
        // special cases first
        val special = SpecialDivisionResult.getFor(arith, uDecimalDividend, uDecimalDivisor)
        if (special != null) {
            return special.divide(arith, uDecimalDividend, uDecimalDivisor)
        }
        // div by power of 10
        val scaleMetrics = arith.scaleMetrics
        val pow10 = Scales.findByScaleFactor(abs(uDecimalDivisor))
        if (pow10 != null) {
            return Pow10.divideByPowerOf10Checked(arith, uDecimalDividend, scaleMetrics, uDecimalDivisor > 0, pow10)
        }
        return divideChecked(scaleMetrics, uDecimalDividend, scaleMetrics, uDecimalDivisor)
    }

    /**
     * Calculates unchecked division by an unscaled value with the given scale
     * without rounding and with overflow checks.
     *
     * @param arith
     * the arithmetic associated with the dividend
     * @param uDecimalDividend
     * the unscaled decimal dividend
     * @param unscaledDivisor
     * the long divisor
     * @param scale
     * the scale of the divisor
     * @return the division result without rounding and with overflow checks
     */
    fun divideByUnscaledChecked(
        arith: DecimalArithmetic,
        uDecimalDividend: Long,
        unscaledDivisor: Long,
        scale: Int
    ): Long {
        require(scale <= Scales.MAX_SCALE) { "Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale }
        if ((uDecimalDividend == 0L) and (unscaledDivisor != 0L)) {
            return 0
        } else if (scale == 0) {
            return divideByLong(arith, uDecimalDividend, unscaledDivisor)
        } else if (scale < 0) {
            if (isDivideOverflow(uDecimalDividend, unscaledDivisor)) {
                return -Pow10.multiplyByPowerOf10Checked(arith, uDecimalDividend, scale)
            }
            return Pow10.multiplyByPowerOf10Checked(arith, uDecimalDividend / unscaledDivisor, scale)
        }
        val divisorMetrics = Scales.getScaleMetrics(scale)
        return divideChecked(arith.scaleMetrics, uDecimalDividend, divisorMetrics, unscaledDivisor)
    }

    /**
     * Calculates `(uDecimalDividend * scaleFactor) / uDecimalDivisor`
     * without rounding and with overflow checks.
     *
     * @param dividendMetrics
     * the metrics associated with the dividend
     * @param uDecimalDividend
     * the unscaled decimal dividend
     * @param divisorMetrics
     * the scale metrics associated with the divisor
     * @param uDecimalDivisor
     * the unscaled decimal divisor
     * @return the division result without rounding and with overflow checks
     */
    private fun divideChecked(
        dividendMetrics: ScaleMetrics,
        uDecimalDividend: Long,
        divisorMetrics: ScaleMetrics,
        uDecimalDivisor: Long
    ): Long {
        try {
            // WE WANT: uDecimalDividend * 10^divisorScale / unscaledDivisor
            if (divisorMetrics.isValidIntegerValue(uDecimalDividend)) {
                // just do it, multiplication result fits in long (division can only overflow for scale=1)
                return divisorMetrics.multiplyByScaleFactor(uDecimalDividend) / uDecimalDivisor
            }
            // perform component wise division
            val integralPart = divideLong(uDecimalDividend, uDecimalDivisor)
            val remainder = uDecimalDividend - integralPart * uDecimalDivisor
            val fractionalPart = if (divisorMetrics.isValidIntegerValue(remainder)) {
                // scaling and result can't overflow because of the above condition
                divisorMetrics.multiplyByScaleFactor(remainder) / uDecimalDivisor
            } else {
                // result can't overflow because reminder is smaller than
                // divisor, i.e. -1 < result < 1
                scaleTo128divBy64(
                    divisorMetrics,
                    DecimalRounding.DOWN,
                    remainder,
                    uDecimalDivisor
                )
            }
            return addLong(divisorMetrics.multiplyByScaleFactorExact(integralPart), fractionalPart)
        } catch (e: ArithmeticException) {
            throw Exceptions.newArithmeticExceptionWithCause(
                ("Overflow: " + dividendMetrics.toString(uDecimalDividend) + " / "
                        + divisorMetrics.toString(uDecimalDivisor)), e
            )
        }
    }

    /**
     * Calculates `(uDecimalDividend * scaleFactor) / uDecimalDivisor`
     * with rounding and with overflow checks.
     *
     * @param arith
     * the arithmetic with scale metrics and overflow mode
     * @param rounding
     * the decimal rounding to apply if rounding is necessary
     * @param uDecimalDividend
     * the unscaled decimal dividend
     * @param uDecimalDivisor
     * the unscaled decimal divisor
     * @return the division result with rounding and with overflow checks
     */
    fun divideChecked(
        arith: DecimalArithmetic,
        rounding: DecimalRounding,
        uDecimalDividend: Long,
        uDecimalDivisor: Long
    ): Long {
        // special cases first
        val special = SpecialDivisionResult.getFor(arith, uDecimalDividend, uDecimalDivisor)
        if (special != null) {
            return special.divide(arith, uDecimalDividend, uDecimalDivisor)
        }
        // div by power of 10
        val scaleMetrics = arith.scaleMetrics
        val pow10 = Scales.findByScaleFactor(abs(uDecimalDivisor))
        if (pow10 != null) {
            return Pow10.divideByPowerOf10Checked(
                arith,
                rounding,
                uDecimalDividend,
                scaleMetrics,
                uDecimalDivisor > 0,
                pow10
            )
        }
        return divideChecked(rounding, scaleMetrics, uDecimalDividend, scaleMetrics, uDecimalDivisor)
    }

    /**
     * Calculates unchecked division by an unscaled value with the given scale
     * without rounding and with overflow checks.
     *
     * @param arith
     * the arithmetic associated with the dividend
     * @param rounding
     * the ronuding to apply
     * @param uDecimalDividend
     * the unscaled decimal dividend
     * @param unscaledDivisor
     * the long divisor
     * @param scale
     * the scale of the divisor
     * @return the division result without rounding and with overflow checks
     */
    fun divideByUnscaledChecked(
        arith: DecimalArithmetic,
        rounding: DecimalRounding,
        uDecimalDividend: Long,
        unscaledDivisor: Long,
        scale: Int
    ): Long {
        require(scale <= Scales.MAX_SCALE) { "Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale }
        if ((uDecimalDividend == 0L) and (unscaledDivisor != 0L)) {
            return 0
        } else if (scale == 0) {
            return divideByLongChecked(arith, rounding, uDecimalDividend, unscaledDivisor)
        } else if (scale < 0) {
            if (isDivideOverflow(uDecimalDividend, unscaledDivisor)) {
                return -Pow10.multiplyByPowerOf10Checked(
                    arith,
                    RoundingInverse.SIGN_REVERSION.invert(rounding),
                    uDecimalDividend,
                    scale
                )
            }
            //NOTE: rounding twice could be a problem here, e.g. consider HALF_UP with 10.51 and 10.49
            val quot = when (rounding) {
                DecimalRounding.HALF_UP -> divideByLongChecked(
                    arith,
                    DecimalRounding.DOWN,
                    uDecimalDividend,
                    unscaledDivisor
                )

                DecimalRounding.HALF_DOWN -> divideByLongChecked(
                    arith,
                    DecimalRounding.UP,
                    uDecimalDividend,
                    unscaledDivisor
                )

                DecimalRounding.HALF_EVEN -> {
                    //try HALF_UP first
                    val quotD = divideByLongChecked(
                        arith,
                        DecimalRounding.DOWN,
                        uDecimalDividend,
                        unscaledDivisor
                    )
                    val powHU = Pow10.multiplyByPowerOf10Checked(arith, DecimalRounding.HALF_UP, quotD, scale)
                    if (0L == (powHU and 0x1L)) {
                        //even, we're done
                        return powHU
                    }
                    //odd, HALF_DOWN may be even in which case it should win
                    val quotU = divideByLongChecked(
                        arith,
                        DecimalRounding.UP,
                        uDecimalDividend,
                        unscaledDivisor
                    )
                    val powHD = Pow10.multiplyByPowerOf10Checked(arith, DecimalRounding.HALF_DOWN, quotU, scale)
                    return powHD //either even or the same as powHU
                }

                else -> divideByLongChecked(
                    arith,
                    rounding,
                    uDecimalDividend,
                    unscaledDivisor
                )
            }
            return Pow10.multiplyByPowerOf10Checked(arith, rounding, quot, scale)
        }
        val divisorMetrics = Scales.getScaleMetrics(scale)
        return divideChecked(rounding, arith.scaleMetrics, uDecimalDividend, divisorMetrics, unscaledDivisor)
    }

    /**
     * Calculates `(uDecimalDividend * scaleFactor) / uDecimalDivisor`
     * with rounding and with overflow checks.
     *
     * @param rounding
     * the ronuding to apply
     * @param dividendMetrics
     * the matrics associated with the dividend
     * @param uDecimalDividend
     * the unscaled decimal dividend
     * @param divisorMetrics
     * the scale metrics associated with the divisor
     * @param uDecimalDivisor
     * the unscaled decimal divisor
     * @return the division result with rounding and with overflow checks
     */
    private fun divideChecked(
        rounding: DecimalRounding,
        dividendMetrics: ScaleMetrics,
        uDecimalDividend: Long,
        divisorMetrics: ScaleMetrics,
        uDecimalDivisor: Long
    ): Long {
        try {
            // WE WANT: uDecimalDividend * 10^divisorScale / unscaledDivisor
            if (divisorMetrics.isValidIntegerValue(uDecimalDividend)) {
                // just do it, multiplication result fits in long
                val scaledDividend = divisorMetrics.multiplyByScaleFactor(uDecimalDividend)
                val quot = scaledDividend / uDecimalDivisor //cannot overflow for scale>1
                val rem = scaledDividend - quot * uDecimalDivisor

                //cannot overflow for scale > 1 because of quot
                return quot + Rounding.calculateRoundingIncrementForDivision(rounding, quot, rem, uDecimalDivisor)
            }

            // perform component wise division
            val integralPart = divideLong(uDecimalDividend, uDecimalDivisor)
            val remainder = uDecimalDividend - integralPart * uDecimalDivisor

            if (divisorMetrics.isValidIntegerValue(remainder)) {
                val scaledReminder = divisorMetrics.multiplyByScaleFactor(remainder)
                val fractionalPart = scaledReminder / uDecimalDivisor //cannot overflow for scale>1
                val subFractionalPart = scaledReminder - fractionalPart * uDecimalDivisor

                val result = addLong(divisorMetrics.multiplyByScaleFactorExact(integralPart), fractionalPart)
                val inc =
                    Rounding.calculateRoundingIncrementForDivision(rounding, result, subFractionalPart, uDecimalDivisor)
                        .toLong()
                return addLong(result, inc)
            } else {
                val fractionalPart = scaleTo128divBy64(divisorMetrics, rounding, remainder, uDecimalDivisor)
                return addLong(divisorMetrics.multiplyByScaleFactorExact(integralPart), fractionalPart)
            }
        } catch (e: ArithmeticException) {
            Exceptions.rethrowIfRoundingNecessary(e)
            throw Exceptions.newArithmeticExceptionWithCause(
                "Overflow: " + dividendMetrics.toString(uDecimalDividend) + " / " + divisorMetrics.toString(
                    uDecimalDivisor
                ), e
            )
        }
    }

    /**
     * Calculates `uDecimalDividend * scaleFactor / uDecimalDivisor` performing the multiplication into a 128 bit product
     * and then performing a 128 by 64 bit division.
     *
     * @param scaleMetrics        the metrics with scale factor to apply when scaling the dividend
     * @param rounding            the rounding to apply if necessary
     * @param uDecimalDividend    the dividend
     * @param uDecimalDivisor    the divisor
     * @return the unscaled decimal result of the division, rounded if necessary and overflow checked if
     */
    private fun scaleTo128divBy64(
        scaleMetrics: ScaleMetrics,
        rounding: DecimalRounding,
        uDecimalDividend: Long,
        uDecimalDivisor: Long
    ): Long {
        val negative = (uDecimalDividend xor uDecimalDivisor) < 0
        val absDividend = abs(uDecimalDividend)
        val absDivisor = abs(uDecimalDivisor)

        // multiply by scale factor into a 128bit integer
        // HD + Knuth's Algorithm M from [Knu2] section 4.3.1.
        val lFactor = (absDividend and LONG_MASK).toInt()
        val hFactor = (absDividend ushr 32).toInt()
        val w1: Long
        val w2: Long
        val w3: Long
        var k: Long

        var t = scaleMetrics.mulloByScaleFactor(lFactor)
        w3 = t and LONG_MASK
        k = t ushr 32

        t = scaleMetrics.mulloByScaleFactor(hFactor) + k
        w2 = t and LONG_MASK
        w1 = t ushr 32

        t = scaleMetrics.mulhiByScaleFactor(lFactor) + w2
        k = t ushr 32

        val hScaled = scaleMetrics.mulhiByScaleFactor(hFactor) + w1 + k
        val lScaled = (t shl 32) + w3

        // divide 128 bit product by 64 bit divisor
        val hQuotient: Long
        val lQuotient: Long
        if (Unsigned.isLess(hScaled, absDivisor)) {
            hQuotient = 0
            lQuotient = div128by64(rounding, negative, hScaled, lScaled, absDivisor)
        } else {
            hQuotient = Unsigned.divide(hScaled, absDivisor)
            val rem = hScaled - hQuotient * absDivisor
            lQuotient = div128by64(rounding, negative, rem, lScaled, absDivisor)
        }
        return lQuotient
    }

    /**
     * PRECONDITION: Unsigned.isLess(u1, v0)
     *
     *
     * Divides a 128 bit divisor by a 64 bit dividend and returns the signed 64
     * bit result. Rounding is applied if `rounding != null`, otherwise
     * the value is truncated.
     *
     *
     * From [www.codeproject.com](http://www.codeproject.com/Tips/785014/UInt-Division-Modulus).
     *
     * @param neg
     * true if result is negative
     * @param u1
     * high order 64 bits of dividend
     * @param u0
     * low order 64 bits of dividend
     * @param v0
     * 64 bit divisor
     * @param rounding
     * rounding to apply, or null to truncate result
     * @return the signed quotient, rounded if `rounding != null`
     */
    @JvmStatic
    fun div128by64(rounding: DecimalRounding, neg: Boolean, u1: Long, u0: Long, v0: Long): Long {
        val q: Long
        val r: Long

        val un1: Long
        val un0: Long
        val vn1: Long
        val vn0: Long
        val un32: Long
        val un21: Long
        val un10: Long

        val s = java.lang.Long.numberOfLeadingZeros(v0)

        val v = v0 shl s
        vn1 = v ushr 32
        vn0 = v and LONG_MASK

        un32 = (u1 shl s) or ((u0 ushr (64 - s)) and (-s shr 63).toLong())
        un10 = u0 shl s

        un1 = un10 ushr 32
        un0 = un10 and LONG_MASK

        val q1 = div128by64part(un32, un1, vn1, vn0)
        un21 = (un32 shl 32) + (un1 - (q1 * v))
        val q0 = div128by64part(un21, un0, vn1, vn0)
        q = (q1 shl 32) or q0

        // apply sign and rounding
        if (rounding === DecimalRounding.DOWN) {
            return if (neg) -q else q
        }

        r = ((un21 shl 32) + un0 - q0 * v) ushr s
        val truncatedPart = Rounding.truncatedPartFor(abs(r), v0)
        val inc = rounding.calculateRoundingIncrement(if (neg) -1 else 1, q, truncatedPart)
        return (if (neg) -q else q) + inc
    }

    private fun div128by64part(unCB: Long, unA: Long, vn1: Long, vn0: Long): Long {
        // quotient and reminder, first guess
        var q = unsignedDiv64by32(unCB, vn1)
        var rhat = unCB - q * vn1

        // correct, first attempt
        while (q > LONG_MASK) {
            q--
            rhat += vn1
            if (rhat > LONG_MASK) {
                return q
            }
        }
        // correct, second attempt
        var left = q * vn0
        var right = (rhat shl 32) or unA
        while (Unsigned.isGreater(left, right)) {
            q--
            rhat += vn1
            if (rhat > LONG_MASK) {
                return q
            }
            left -= vn0
            right = (rhat shl 32) or unA
        }
        return q
    }

    /**
     * Returns dividend / divisor, where the dividend and divisor are treated as
     * unsigned 64-bit quantities.
     *
     *
     * From Guava's [UnsignedLongs](http://docs.guava-libraries.googlecode.com/git/javadoc/src-html/com/google/common/primitives/UnsignedLongs.html).
     *
     * @param dividend
     * the dividend (numerator)
     * @param divisor
     * the divisor (denominator)
     * @return the unsigned quotient `dividend / divisor`
     * @throws ArithmeticException
     * if divisor is 0
     */
    private fun unsignedDiv64by32(dividend: Long, divisor: Long): Long {
        // Optimization - use signed division if dividend < 2^63
        if (dividend >= 0) {
            return dividend / divisor
        }
        // Optimization if divisor is even
        if (0L == (divisor and 0x1L)) {
            return (dividend ushr 1) / (divisor ushr 1)
        }

        /*
		 * Otherwise, approximate the quotient, check, and correct if necessary.
		 * Our approximation is guaranteed to be either exact or one less than
		 * the correct value. This follows from fact that floor(floor(x)/i) ==
		 * floor(x/i) for any real x and integer i != 0. The proof is not quite
		 * trivial.
		 */
        val quotient = ((dividend ushr 1) / divisor) shl 1
        val rem = dividend - quotient * divisor
        return quotient + (if ((rem >= divisor) or (rem < 0)) 1 else 0)
    }
}
