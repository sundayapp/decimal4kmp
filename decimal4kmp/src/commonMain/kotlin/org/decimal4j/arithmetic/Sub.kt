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
import org.decimal4j.arithmetic.Checked.isSubtractOverflow
import org.decimal4j.arithmetic.Exceptions.newArithmeticExceptionWithCause
import org.decimal4j.arithmetic.Pow10.divideByPowerOf10
import org.decimal4j.arithmetic.Pow10.divideByPowerOf10Checked
import org.decimal4j.arithmetic.Pow10.multiplyByPowerOf10
import org.decimal4j.arithmetic.Rounding.calculateRoundingIncrement
import org.decimal4j.scale.Scale0f
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales
import org.decimal4j.truncate.DecimalRounding
import kotlin.math.sign

/**
 * Provides static methods to calculate subtractions.
 */
internal object Sub {
    /**
     * Calculates unchecked unrounded subtraction of a long value and an unscaled
     * value with the given scale.
     *
     * @param lValue
     * the long value
     * @param unscaled
     * the unscaled value
     * @param scale
     * the scale of the second value
     * @return the subtraction result without rounding and without overflow checks
     */
	
	fun subtractLongUnscaled(lValue: Long, unscaled: Long, scale: Int): Long {
        return subtractUnscaledUnscaled(Scale0f.INSTANCE, lValue, unscaled, scale)
    }

    /**
     * Calculates unchecked rounded subtraction of a long value and an unscaled
     * value with the given scale.
     *
     * @param rounding
     * the rounding to apply
     * @param lValue
     * the long value
     * @param unscaled
     * the unscaled value
     * @param scale
     * the scale of the second value
     * @return the subtraction result with rounding but without overflow checks
     */
	
	fun subtractLongUnscaled(rounding: DecimalRounding, lValue: Long, unscaled: Long, scale: Int): Long {
        return subtractUnscaledUnscaled(Scale0f.INSTANCE, rounding, lValue, unscaled, scale)
    }

    /**
     * Calculates unchecked subtraction of an unscaled value and a long value.
     *
     * @param arith
     * the arithmetic associated with the first value
     * @param uDecimal
     * the unscaled value
     * @param lValue
     * the long value
     * @return the subtraction result without overflow checks
     */
	
	fun subtractUnscaledLong(arith: DecimalArithmetic, uDecimal: Long, lValue: Long): Long {
        return uDecimal - multiplyByPowerOf10(lValue, arith.scale)
    }

    /**
     * Calculates checked subtraction of an unscaled value and a long value.
     *
     * @param arith
     * the arithmetic associated with the first value
     * @param uDecimal
     * the unscaled value
     * @param lValue
     * the long value
     * @return the subtraction result performed with overflow checks
     */
	
	fun subtractUnscaledLongChecked(arith: DecimalArithmetic, uDecimal: Long, lValue: Long): Long {
        val scale = arith.scale
        if ((lValue == 0L) or (scale == 0)) {
            return arith.subtract(uDecimal, lValue)
        }
        try {
            return subtractForNegativeScaleDiff(arith, uDecimal, lValue, -scale)
        } catch (e: ArithmeticException) {
            throw newArithmeticExceptionWithCause("Overflow: " + arith.toString(uDecimal) + " - " + lValue, e)
        }
    }

    /**
     * Calculates unchecked unrounded subtraction of an unscaled value and another
     * unscaled value with the given `scaleMetrics` and `scale`,
     * respectively.
     *
     * @param scaleMetrics
     * the scaleMetrics associated with the first value
     * @param uDecimal
     * the first unscaled value
     * @param unscaled
     * the second unscaled value
     * @param scale
     * the scale of the second value
     * @return the subtraction result without rounding and without overflow checks
     */
	
	fun subtractUnscaledUnscaled(scaleMetrics: ScaleMetrics, uDecimal: Long, unscaled: Long, scale: Int): Long {
        require(scale <= Scales.MAX_SCALE) { "Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale }
        val scaleDiff = scale - scaleMetrics.getScale()
        if ((unscaled == 0L) or (scaleDiff == 0)) {
            return uDecimal - unscaled
        } else if (scaleDiff < 0) {
            return uDecimal - divideByPowerOf10(unscaled, scaleDiff) //multiplication
        }
        return subtractForPositiveScaleDiff(uDecimal, unscaled, scaleDiff)
    }

    /**
     * Calculates unchecked rounded subtraction of an unscaled value and another
     * unscaled value with the given `scaleMetrics` and `scale`,
     * respectively.
     *
     * @param scaleMetrics
     * the scaleMetrics associated with the first value
     * @param rounding
     * the rounding to apply
     * @param uDecimal
     * the first unscaled value
     * @param unscaled
     * the second unscaled value
     * @param scale
     * the scale of the second value
     * @return the subtraction result with rounding but without overflow checks
     */
	
	fun subtractUnscaledUnscaled(
        scaleMetrics: ScaleMetrics,
        rounding: DecimalRounding,
        uDecimal: Long,
        unscaled: Long,
        scale: Int
    ): Long {
        require(scale <= Scales.MAX_SCALE) { "Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale }
        val scaleDiff = scale - scaleMetrics.getScale()
        if ((unscaled == 0L) or (scaleDiff == 0)) {
            return uDecimal - unscaled
        } else if (scaleDiff < 0) {
            return uDecimal - divideByPowerOf10(unscaled, scaleDiff) //multiplication
        }
        //scale > 0
        return subtractForPositiveScaleDiff(rounding, uDecimal, unscaled, scaleDiff)
    }

    /**
     * Calculates checked unrounded subtraction of an unscaled value and another
     * unscaled value with the given `scaleMetrics` and `scale`,
     * respectively.
     *
     * @param arith
     * the arithmetic associated with the first value
     * @param uDecimal
     * the first unscaled value
     * @param unscaled
     * the second unscaled value
     * @param scale
     * the scale of the second value
     * @return the subtraction result without rounding but with overflow checks
     */
    fun subtractUnscaledUnscaledChecked(arith: DecimalArithmetic, uDecimal: Long, unscaled: Long, scale: Int): Long {
        require(scale <= Scales.MAX_SCALE) { "Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale }
        val scaleDiff = scale - arith.scale
        if ((unscaled == 0L) or (scaleDiff == 0)) {
            return arith.subtract(uDecimal, unscaled)
        } else if (scaleDiff < 0) {
            try {
                return subtractForNegativeScaleDiff(arith, uDecimal, unscaled, scaleDiff)
            } catch (e: ArithmeticException) {
                throw newArithmeticExceptionWithCause(
                    "Overflow: " + arith.toString(uDecimal) + " - " + unscaled + "*10^" + (-scale),
                    e
                )
            }
        }
        val diff = subtractForPositiveScaleDiff(uDecimal, unscaled, scaleDiff)
        if (!isSubtractOverflow(uDecimal, unscaled, diff)) {
            return diff
        }
        throw ArithmeticException("Overflow: " + arith.toString(uDecimal) + " - " + unscaled + "*10^" + (-scale) + "=" + diff)
    }

    /**
     * Calculates checked rounded subtraction of an unscaled value and another
     * unscaled value with the given `arith` and `scale`,
     * respectively.
     *
     * @param arith
     * the arithmetic associated with the first value
     * @param rounding
     * the rounding to apply
     * @param uDecimal
     * the first unscaled value
     * @param unscaled
     * the second unscaled value
     * @param scale
     * the scale of the second value
     * @return the subtraction result with rounding and overflow checks
     */
    fun subtractUnscaledUnscaledChecked(
        arith: DecimalArithmetic,
        rounding: DecimalRounding,
        uDecimal: Long,
        unscaled: Long,
        scale: Int
    ): Long {
        require(scale <= Scales.MAX_SCALE) { "Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale }
        val scaleDiff = scale - arith.scale
        if ((unscaled == 0L) or (scaleDiff == 0)) {
            return arith.subtract(uDecimal, unscaled)
        } else if (scaleDiff < 0) {
            try {
                return subtractForNegativeScaleDiff(arith, uDecimal, unscaled, scaleDiff)
            } catch (e: ArithmeticException) {
                throw newArithmeticExceptionWithCause(
                    "Overflow: " + arith.toString(uDecimal) + " - " + unscaled + "*10^" + (-scale),
                    e
                )
            }
        }
        val diff = subtractForPositiveScaleDiff(rounding, uDecimal, unscaled, scaleDiff)
        if (!isSubtractOverflow(uDecimal, unscaled, diff)) {
            return diff
        }
        throw ArithmeticException("Overflow: " + arith.toString(uDecimal) + " - " + unscaled + "*10^" + (-scale) + "=" + diff)
    }

    /**
     * Calculates unchecked unrounded subtraction of an unscaled value and another
     * unscaled value with the given `scaleDiff=scale2-scale1 > 0`.
     *
     * @param uDecimal
     * the first unscaled value
     * @param unscaled
     * the second unscaled value
     * @param scaleDiff
     * scale2 - scale1, must be positive
     * @return the subtraction result without rounding and without overflow checks
     */
    //PRECONDITION: scaleDiff > 0
    private fun subtractForPositiveScaleDiff(uDecimal: Long, unscaled: Long, scaleDiff: Int): Long {
        //scaleDiff > 0
        val diffMetrics = Scales.getScaleMetrics(scaleDiff)
        val trunc = diffMetrics.divideByScaleFactor(unscaled)
        val diff = uDecimal - trunc
        if ((uDecimal == 0L) or (diff == 0L) or ((uDecimal xor unscaled) < 0) or ((diff xor unscaled) < 0)) {
            return diff
        }
        val remainder = unscaled - diffMetrics.multiplyByScaleFactor(trunc)
        return diff - remainder.sign
    }

    /**
     * Calculates unchecked rounded subtraction of an unscaled value and another
     * unscaled value with the given `scaleDiff=scale2-scale1 > 0`.
     *
     * @param rounding
     * the rounding to apply
     * @param uDecimal
     * the first unscaled value
     * @param unscaled
     * the second unscaled value
     * @param scaleDiff
     * scale2 - scale1, must be positive
     * @return the subtraction result with rounding but without overflow checks
     */
    //PRECONDITION: scaleDiff > 0
    private fun subtractForPositiveScaleDiff(
        rounding: DecimalRounding,
        uDecimal: Long,
        unscaled: Long,
        scaleDiff: Int
    ): Long {
        //scaleDiff > 0
        val diffMetrics = Scales.getScaleMetrics(scaleDiff)
        val trunc = diffMetrics.divideByScaleFactor(unscaled)
        val remainder = unscaled - diffMetrics.multiplyByScaleFactor(trunc)
        val diff = uDecimal - trunc
        if ((uDecimal == 0L) or (diff == 0L) or ((uDecimal xor unscaled) < 0) or ((diff xor unscaled) < 0)) {
            return diff + calculateRoundingIncrement(rounding, diff, -remainder, diffMetrics.getScaleFactor())
        }
        return diff + calculateRoundingIncrement(
            RoundingInverse.ADDITIVE_REVERSION.invert(rounding),
            diff,
            -remainder,
            diffMetrics.getScaleFactor()
        )
    }

    /**
     * Calculates checked subtraction of an unscaled value and another
     * unscaled value with the given `scaleDiff = scal2 - scale1 > 0` which must be negative, such that the subtracted
     * value can be rescaled through multiplication.
     *
     * @param arith
     * the arithmetic associated with the first value
     * @param uDecimal
     * the first unscaled value
     * @param unscaled
     * the second unscaled value
     * @param scaleDiff
     * the scale of the second value
     * @return the subtraction result with overflow checks
     */
    //PRECONDITION: scaleDiff < 0
    private fun subtractForNegativeScaleDiff(
        arith: DecimalArithmetic,
        uDecimal: Long,
        unscaled: Long,
        scaleDiff: Int
    ): Long {
        //NOTE: multiplication by power of 10 may lead to an overflow but the result may still be valid if signs are same
        //		--> therefore we multiply only half of the value with pow10 and subtract it twice
        //		--> then we subtract the remainder 1 (x pow10) if the value was odd (again in halves to avoid overflow)
        val half = divideByPowerOf10Checked(arith, unscaled / 2, scaleDiff) //multiplication;
        val halfReminder = if ((unscaled and 0x1L) == 0L) 0 else divideByPowerOf10Checked(
            arith,
            (if (unscaled > 0) 5 else -5).toLong(),
            scaleDiff + 1
        )
        var result = uDecimal
        result = arith.subtract(result, half)
        result = arith.subtract(result, half)
        result = arith.subtract(result, halfReminder)
        result = arith.subtract(result, halfReminder)
        return result
    }
}
