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
import org.decimal4j.arithmetic.BigDecimalConversion.bigDecimalToUnscaled
import org.decimal4j.arithmetic.Checked.divideByLong
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.CheckedRounding
import org.decimal4j.truncate.DecimalRounding
import java.math.BigDecimal
import org.decimal4j.api.RoundingMode

/**
 * Arithmetic implementation without rounding but with overflow check for scales
 * other than zero. An exception is thrown if an operation leads to an overflow.
 */
class CheckedScaleNfTruncatingArithmetic
/**
 * Constructor with scale metrics for this arithmetic.
 *
 * @param scaleMetrics
 * the scale metrics
 */
    (scaleMetrics: ScaleMetrics) : AbstractCheckedScaleNfArithmetic(scaleMetrics) {
    override val roundingMode: RoundingMode = RoundingMode.DOWN

    override val truncationPolicy: CheckedRounding = CheckedRounding.DOWN

    override fun addUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return addUnscaledUnscaledChecked(this, uDecimal, unscaled, scale)
    }

    override fun subtractUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return Sub.subtractUnscaledUnscaledChecked(this, uDecimal, unscaled, scale)
    }

    override fun multiplyByUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return Mul.multiplyByUnscaledChecked(this, uDecimal, unscaled, scale)
    }

    override fun divideByUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return Div.divideByUnscaledChecked(this, uDecimal, unscaled, scale)
    }

    override fun multiply(uDecimal1: Long, uDecimal2: Long): Long {
        return Mul.multiplyChecked(this, uDecimal1, uDecimal2)
    }

    override fun square(uDecimal: Long): Long {
        return Square.squareChecked(this, uDecimal)
    }

    override fun divide(uDecimalDividend: Long, uDecimalDivisor: Long): Long {
        return Div.divideChecked(this, uDecimalDividend, uDecimalDivisor)
    }

    override fun pow(uDecimal: Long, exponent: Int): Long {
        return Pow.pow(this, DecimalRounding.DOWN, uDecimal, exponent)
    }

    override fun avg(a: Long, b: Long): Long {
        return Avg.avg(a, b)
    }

    override fun sqrt(uDecimal: Long): Long {
        return Sqrt.sqrt(this, uDecimal)
    }

    override fun divideByLong(uDecimalDividend: Long, lDivisor: Long): Long {
        return divideByLong(this, uDecimalDividend, lDivisor)
    }

    override fun divideByPowerOf10(uDecimal: Long, positions: Int): Long {
        return Pow10.divideByPowerOf10Checked(this, uDecimal, positions)
    }

    override fun invert(uDecimal: Long): Long {
        return Invert.invert(this, uDecimal)
    }

    override fun multiplyByPowerOf10(uDecimal: Long, positions: Int): Long {
        return Pow10.multiplyByPowerOf10Checked(this, uDecimal, positions)
    }

    override fun shiftLeft(uDecimal: Long, positions: Int): Long {
        return Shift.shiftLeftChecked(this, DecimalRounding.DOWN, uDecimal, positions)
    }

    override fun shiftRight(uDecimal: Long, positions: Int): Long {
        return Shift.shiftRightChecked(this, DecimalRounding.DOWN, uDecimal, positions)
    }

    override fun round(uDecimal: Long, precision: Int): Long {
        return Round.round(this, uDecimal, precision)
    }

    override fun fromLong(value: Long): Long {
        return LongConversion.longToUnscaled(scaleMetrics, value)
    }

    override fun fromFloat(value: Float): Long {
        return FloatConversion.floatToUnscaled(this, DecimalRounding.DOWN, value)
    }

    override fun fromDouble(value: Double): Long {
        return DoubleConversion.doubleToUnscaled(this, DecimalRounding.DOWN, value)
    }

    override fun fromUnscaled(unscaledValue: Long, scale: Int): Long {
        return UnscaledConversion.unscaledToUnscaled(this, unscaledValue, scale)
    }

    override fun fromBigDecimal(value: BigDecimal): Long {
        return bigDecimalToUnscaled(scaleMetrics, RoundingMode.DOWN, value)
    }

    override fun toLong(uDecimal: Long): Long {
        return LongConversion.unscaledToLong(scaleMetrics, uDecimal)
    }

    override fun toFloat(uDecimal: Long): Float {
        return FloatConversion.unscaledToFloat(this, uDecimal)
    }

    override fun toDouble(uDecimal: Long): Double {
        return DoubleConversion.unscaledToDouble(this, uDecimal)
    }

    override fun toUnscaled(uDecimal: Long, scale: Int): Long {
        return UnscaledConversion.unscaledToUnscaled(scale, this, uDecimal)
    }

    override fun parse(value: String): Long {
        return StringConversion.parseUnscaledDecimal(this, DecimalRounding.DOWN, value, 0, value.length)
    }

    override fun parse(value: CharSequence, start: Int, end: Int): Long {
        return StringConversion.parseUnscaledDecimal(this, DecimalRounding.DOWN, value, start, end)
    }
}
