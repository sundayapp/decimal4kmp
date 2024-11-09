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

import org.decimal4j.arithmetic.Add.addUnscaledUnscaled
import org.decimal4j.arithmetic.Avg.avg
import org.decimal4j.arithmetic.BigDecimalConversion.bigDecimalToUnscaled
import org.decimal4j.arithmetic.Div.divide
import org.decimal4j.arithmetic.Div.divideByLong
import org.decimal4j.arithmetic.Div.divideByUnscaled
import org.decimal4j.arithmetic.FloatConversion.floatToUnscaled
import org.decimal4j.arithmetic.FloatConversion.unscaledToFloat
import org.decimal4j.arithmetic.Invert.invert
import org.decimal4j.arithmetic.LongConversion.longToUnscaled
import org.decimal4j.arithmetic.LongConversion.unscaledToLong
import org.decimal4j.arithmetic.Mul.multiply
import org.decimal4j.arithmetic.Mul.multiplyByUnscaled
import org.decimal4j.arithmetic.Pow.pow
import org.decimal4j.arithmetic.Pow10.divideByPowerOf10
import org.decimal4j.arithmetic.Pow10.multiplyByPowerOf10
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
import java.math.BigDecimal
import org.decimal4j.api.RoundingMode

/**
 * Arithmetic implementation with rounding for scales other than zero. If an
 * operation leads to an overflow the result is silently truncated.
 */
class UncheckedScaleNfRoundingArithmetic
/**
 * Constructor for decimal arithmetic with given scale, rounding mode and
 * [SILENT][OverflowMode.UNCHECKED] overflow mode.
 *
 * @param scaleMetrics
 * the scale metrics for this decimal arithmetic
 * @param rounding
 * the rounding mode to use for all decimal arithmetic
 */(scaleMetrics: ScaleMetrics, private val rounding: DecimalRounding) :
    AbstractUncheckedScaleNfArithmetic(scaleMetrics) {
    /**
     * Constructor for decimal arithmetic with given scale, rounding mode and
     * [SILENT][OverflowMode.UNCHECKED] overflow mode.
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

    override val  roundingMode: RoundingMode = rounding.getRoundingMode()

    override val truncationPolicy: UncheckedRounding = UncheckedRounding.valueOf(roundingMode)


    override fun addUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return addUnscaledUnscaled(scaleMetrics, rounding, uDecimal, unscaled, scale)
    }

    override fun subtractUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return subtractUnscaledUnscaled(scaleMetrics, rounding, uDecimal, unscaled, scale)
    }

    override fun avg(uDecimal1: Long, uDecimal2: Long): Long {
        return avg(this, rounding, uDecimal1, uDecimal2)
    }

    override fun multiply(uDecimal1: Long, uDecimal2: Long): Long {
        return multiply(this, rounding, uDecimal1, uDecimal2)
    }

    override fun multiplyByUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return multiplyByUnscaled(rounding, uDecimal, unscaled, scale)
    }

    override fun square(uDecimal: Long): Long {
        return square(scaleMetrics, rounding, uDecimal)
    }

    override fun sqrt(uDecimal: Long): Long {
        return sqrt(this, rounding, uDecimal)
    }

    override fun divideByLong(uDecimalDividend: Long, lDivisor: Long): Long {
        return divideByLong(rounding, uDecimalDividend, lDivisor)
    }

    override fun divideByUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return divideByUnscaled(rounding, uDecimal, unscaled, scale)
    }

    override fun divide(uDecimalDividend: Long, uDecimalDivisor: Long): Long {
        return divide(this, rounding, uDecimalDividend, uDecimalDivisor)
    }

    override fun invert(uDecimal: Long): Long {
        return invert(this, rounding, uDecimal)
    }

    override fun pow(uDecimal: Long, exponent: Int): Long {
        return pow(this, rounding, uDecimal, exponent)
    }

    override fun shiftLeft(uDecimal: Long, positions: Int): Long {
        return shiftLeft(rounding, uDecimal, positions)
    }

    override fun shiftRight(uDecimal: Long, positions: Int): Long {
        return shiftRight(rounding, uDecimal, positions)
    }

    override fun multiplyByPowerOf10(uDecimal: Long, n: Int): Long {
        return multiplyByPowerOf10(rounding, uDecimal, n)
    }

    override fun divideByPowerOf10(uDecimal: Long, n: Int): Long {
        return divideByPowerOf10(rounding, uDecimal, n)
    }

    override fun round(uDecimal: Long, precision: Int): Long {
        return round(this, rounding, uDecimal, precision)
    }

    override fun fromLong(value: Long): Long {
        return longToUnscaled(scaleMetrics, value)
    }

    override fun fromUnscaled(unscaledValue: Long, scale: Int): Long {
        return UnscaledConversion.unscaledToUnscaled(this, rounding, unscaledValue, scale)
    }

    override fun fromFloat(value: Float): Long {
        return floatToUnscaled(this, rounding, value)
    }

    override fun fromDouble(value: Double): Long {
        return DoubleConversion.doubleToUnscaled(this, rounding, value)
    }

    override fun fromBigDecimal(value: BigDecimal): Long {
        return bigDecimalToUnscaled(scaleMetrics, roundingMode, value)
    }

    override fun toLong(uDecimal: Long): Long {
        return unscaledToLong(scaleMetrics, rounding, uDecimal)
    }

    override fun toUnscaled(uDecimal: Long, scale: Int): Long {
        return UnscaledConversion.unscaledToUnscaled(rounding, scale, this, uDecimal)
    }

    override fun toFloat(uDecimal: Long): Float {
        return unscaledToFloat(this, rounding, uDecimal)
    }

    override fun toDouble(uDecimal: Long): Double {
        return DoubleConversion.unscaledToDouble(this, rounding, uDecimal)
    }

    override fun parse(value: String): Long {
        return parseUnscaledDecimal(this, rounding, value, 0, value.length)
    }

    override fun parse(value: CharSequence, start: Int, end: Int): Long {
        return parseUnscaledDecimal(this, rounding, value, start, end)
    }
}
