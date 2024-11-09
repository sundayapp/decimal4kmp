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

import org.decimal4j.arithmetic.Add.addUnscaledUnscaledChecked
import org.decimal4j.arithmetic.Avg.avg
import org.decimal4j.arithmetic.BigDecimalConversion.bigDecimalToUnscaled
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.CheckedRounding
import org.decimal4j.truncate.DecimalRounding
import java.math.BigDecimal
import org.decimal4j.api.RoundingMode

/**
 * Arithmetic implementation with rounding and overflow check for scales other
 * than zero. An exception is thrown if an operation leads to an overflow.
 */
class CheckedScaleNfRoundingArithmetic
/**
 * Constructor for decimal arithmetic with given scale, rounding mode and
 * [OverflowMode.CHECKED] overflow mode.
 *
 * @param scaleMetrics
 * the scale metrics for this decimal arithmetic
 * @param rounding
 * the rounding mode to use for all decimal arithmetic
 */(scaleMetrics: ScaleMetrics, private val rounding: DecimalRounding) :
    AbstractCheckedScaleNfArithmetic(scaleMetrics) {
    /**
     * Constructor for decimal arithmetic with given scale, rounding mode and
     * [OverflowMode.CHECKED] overflow mode.
     *
     * @param scaleMetrics
     * the scale metrics for this decimal arithmetic
     * @param roundingMode
     * the rounding mode to use for all decimal arithmetic
     */
    constructor(scaleMetrics: ScaleMetrics, roundingMode: RoundingMode) : this(
        scaleMetrics,
        DecimalRounding.valueOf(roundingMode)
    )

    override val roundingMode: RoundingMode = rounding.getRoundingMode()

    override val truncationPolicy: CheckedRounding = CheckedRounding.valueOf(roundingMode)

    override fun addUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return addUnscaledUnscaledChecked(this, rounding, uDecimal, unscaled, scale)
    }

    override fun subtractUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return Sub.subtractUnscaledUnscaledChecked(this, rounding, uDecimal, unscaled, scale)
    }

    override fun multiplyByUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return Mul.multiplyByUnscaledChecked(this, rounding, uDecimal, unscaled, scale)
    }

    override fun divideByUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return Div.divideByUnscaledChecked(this, rounding, uDecimal, unscaled, scale)
    }

    override fun avg(uDecimal1: Long, uDecimal2: Long): Long {
        return avg(this, rounding, uDecimal1, uDecimal2)
    }

    override fun invert(uDecimal: Long): Long {
        return Invert.invert(this, rounding, uDecimal)
    }

    override fun multiply(uDecimal1: Long, uDecimal2: Long): Long {
        return Mul.multiplyChecked(this, rounding, uDecimal1, uDecimal2)
    }

    override fun multiplyByPowerOf10(uDecimal: Long, n: Int): Long {
        return Pow10.multiplyByPowerOf10Checked(this, rounding, uDecimal, n)
    }

    override fun divide(uDecimalDividend: Long, uDecimalDivisor: Long): Long {
        return Div.divideChecked(this, rounding, uDecimalDividend, uDecimalDivisor)
    }

    override fun divideByLong(uDecimalDividend: Long, lDivisor: Long): Long {
        return Div.divideByLongChecked(this, rounding, uDecimalDividend, lDivisor)
    }

    override fun divideByPowerOf10(uDecimal: Long, n: Int): Long {
        return Pow10.divideByPowerOf10Checked(this, rounding, uDecimal, n)
    }

    override fun square(uDecimal: Long): Long {
        return Square.squareChecked(this, rounding, uDecimal)
    }

    override fun sqrt(uDecimal: Long): Long {
        return Sqrt.sqrt(this, rounding, uDecimal)
    }

    override fun pow(uDecimalBase: Long, exponent: Int): Long {
        return Pow.pow(this, rounding, uDecimalBase, exponent)
    }

    override fun shiftLeft(uDecimal: Long, n: Int): Long {
        return Shift.shiftLeftChecked(this, rounding, uDecimal, n)
    }

    override fun shiftRight(uDecimal: Long, n: Int): Long {
        return Shift.shiftRightChecked(this, rounding, uDecimal, n)
    }

    override fun round(uDecimal: Long, precision: Int): Long {
        return Round.round(this, rounding, uDecimal, precision)
    }

    override fun fromLong(value: Long): Long {
        return LongConversion.longToUnscaled(scaleMetrics, value)
    }

    override fun fromFloat(value: Float): Long {
        return FloatConversion.floatToUnscaled(this, rounding, value)
    }

    override fun fromDouble(value: Double): Long {
        return DoubleConversion.doubleToUnscaled(this, rounding, value)
    }

    override fun fromUnscaled(unscaledValue: Long, scale: Int): Long {
        return UnscaledConversion.unscaledToUnscaled(this, rounding, unscaledValue, scale)
    }

    override fun fromBigDecimal(value: BigDecimal): Long {
        return bigDecimalToUnscaled(scaleMetrics, roundingMode, value)
    }

    override fun toLong(uDecimal: Long): Long {
        return LongConversion.unscaledToLong(scaleMetrics, rounding, uDecimal)
    }

    override fun toFloat(uDecimal: Long): Float {
        return FloatConversion.unscaledToFloat(this, rounding, uDecimal)
    }

    override fun toDouble(uDecimal: Long): Double {
        return DoubleConversion.unscaledToDouble(this, rounding, uDecimal)
    }

    override fun toUnscaled(uDecimal: Long, scale: Int): Long {
        return UnscaledConversion.unscaledToUnscaled(rounding, scale, this, uDecimal)
    }

    override fun parse(value: String): Long {
        return StringConversion.parseUnscaledDecimal(this, rounding, value, 0, value.length)
    }

    override fun parse(value: CharSequence, start: Int, end: Int): Long {
        return StringConversion.parseUnscaledDecimal(this, rounding, value, start, end)
    }
}
