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
import org.decimal4j.arithmetic.Add.addUnscaledUnscaled
import org.decimal4j.arithmetic.Div.divide
import org.decimal4j.arithmetic.FloatConversion.floatToUnscaled
import org.decimal4j.arithmetic.FloatConversion.unscaledToFloat
import org.decimal4j.arithmetic.Invert.invert
import org.decimal4j.arithmetic.LongConversion.longToUnscaled
import org.decimal4j.arithmetic.Mul.multiply
import org.decimal4j.arithmetic.Pow.pow
import org.decimal4j.arithmetic.Round.round
import org.decimal4j.arithmetic.Shift.shiftLeft
import org.decimal4j.arithmetic.Shift.shiftRight
import org.decimal4j.arithmetic.Sqrt.sqrt
import org.decimal4j.arithmetic.Square.square
import org.decimal4j.arithmetic.StringConversion.parseUnscaledDecimal
import org.decimal4j.arithmetic.Sub.subtractUnscaledUnscaled
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.DecimalRounding
import org.decimal4j.truncate.UncheckedRounding
import org.decimal4j.truncate.RoundingMode

/**
 * Arithmetic implementation without rounding for scales other than zero. If an
 * operation leads to an overflow the result is silently truncated.
 */
class UncheckedScaleNfTruncatingArithmetic
/**
 * Constructor for silent decimal arithmetic with given scale, truncating
 * [DOWN][RoundingMode.DOWN] rounding mode and
 * [SILENT][OverflowMode.UNCHECKED] overflow mode.
 *
 * @param scaleMetrics
 * the scale, a non-negative integer denoting the number of
 * digits to the right of the decimal point
 * @throws IllegalArgumentException
 * if scale is negative or uneven
 */
    (scaleMetrics: ScaleMetrics) : AbstractUncheckedScaleNfArithmetic(scaleMetrics), DecimalArithmetic {
    override val  roundingMode: RoundingMode =  RoundingMode.DOWN

    override val truncationPolicy: UncheckedRounding = UncheckedRounding.DOWN

    override fun addUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return addUnscaledUnscaled(scaleMetrics, uDecimal, unscaled, scale)
    }

    override fun subtractUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return subtractUnscaledUnscaled(scaleMetrics, uDecimal, unscaled, scale)
    }

    override fun multiply(uDecimal1: Long, uDecimal2: Long): Long {
        return multiply(this, uDecimal1, uDecimal2)
    }

    override fun multiplyByUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return Mul.multiplyByUnscaled(uDecimal, unscaled, scale)
    }

    override fun square(uDecimal: Long): Long {
        return square(scaleMetrics, uDecimal)
    }

    override fun sqrt(uDecimal: Long): Long {
        return sqrt(this, uDecimal)
    }

    override fun divide(uDecimalDividend: Long, uDecimalDivisor: Long): Long {
        return divide(this, uDecimalDividend, uDecimalDivisor)
    }

    override fun divideByLong(uDecimalDividend: Long, lDivisor: Long): Long {
        return uDecimalDividend / lDivisor
    }

    override fun divideByUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return Div.divideByUnscaled(uDecimal, unscaled, scale)
    }

    override fun invert(uDecimal: Long): Long {
        return invert(this, uDecimal)
    }

    override fun avg(a: Long, b: Long): Long {
        return Avg.avg(a, b)
    }

    override fun multiplyByPowerOf10(uDecimal: Long, positions: Int): Long {
        return Pow10.multiplyByPowerOf10(uDecimal, positions)
    }

    override fun divideByPowerOf10(uDecimal: Long, positions: Int): Long {
        return Pow10.divideByPowerOf10(uDecimal, positions)
    }

    override fun pow(uDecimal: Long, exponent: Int): Long {
        return pow(this, DecimalRounding.DOWN, uDecimal, exponent)
    }

    override fun shiftLeft(uDecimal: Long, positions: Int): Long {
        return shiftLeft(DecimalRounding.DOWN, uDecimal, positions)
    }

    override fun shiftRight(uDecimal: Long, positions: Int): Long {
        return shiftRight(DecimalRounding.DOWN, uDecimal, positions)
    }

    override fun round(uDecimal: Long, precision: Int): Long {
        return round(this, uDecimal, precision)
    }

    override fun fromLong(value: Long): Long {
        return longToUnscaled(scaleMetrics, value)
    }

    override fun fromUnscaled(unscaledValue: Long, scale: Int): Long {
        return UnscaledConversion.unscaledToUnscaled(this, unscaledValue, scale)
    }

    override fun fromFloat(value: Float): Long {
        return floatToUnscaled(this, value)
    }

    override fun fromDouble(value: Double): Long {
        return DoubleConversion.doubleToUnscaled(this, value)
    }

    override fun toLong(uDecimal: Long): Long {
        return scaleMetrics.divideByScaleFactor(uDecimal)
    }

    override fun toUnscaled(uDecimal: Long, scale: Int): Long {
        return UnscaledConversion.unscaledToUnscaled(scale, this, uDecimal)
    }

    override fun toFloat(uDecimal: Long): Float {
        return unscaledToFloat(this, uDecimal)
    }

    override fun toDouble(uDecimal: Long): Double {
        return DoubleConversion.unscaledToDouble(this, uDecimal)
    }

    override fun parse(value: String): Long {
        return parseUnscaledDecimal(this, DecimalRounding.DOWN, value, 0, value.length)
    }

    override fun parse(value: CharSequence, start: Int, end: Int): Long {
        return parseUnscaledDecimal(this, DecimalRounding.DOWN, value, start, end)
    }
}
