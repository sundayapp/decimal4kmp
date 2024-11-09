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

import org.decimal4j.arithmetic.Add.addLongUnscaled
import org.decimal4j.arithmetic.BigDecimalConversion.bigDecimalToLong
import org.decimal4j.arithmetic.FloatConversion.floatToLong
import org.decimal4j.arithmetic.FloatConversion.longToFloat
import org.decimal4j.arithmetic.Invert.invertLong
import org.decimal4j.arithmetic.Pow.powLong
import org.decimal4j.arithmetic.Round.round
import org.decimal4j.arithmetic.Shift.shiftLeft
import org.decimal4j.arithmetic.Shift.shiftRight
import org.decimal4j.arithmetic.Sqrt.sqrtLong
import org.decimal4j.arithmetic.StringConversion.parseLong
import org.decimal4j.arithmetic.Sub.subtractLongUnscaled
import org.decimal4j.truncate.DecimalRounding
import org.decimal4j.truncate.UncheckedRounding
import java.math.BigDecimal
import org.decimal4j.api.RoundingMode

/**
 * Arithmetic implementation without rounding for the special case with
 * [Scale0f], that is, for longs. If an operation leads to an overflow the
 * result is silently truncated.
 */
class UncheckedScale0fTruncatingArithmetic : AbstractUncheckedScale0fArithmetic() {
    override val roundingMode: RoundingMode = RoundingMode.DOWN

    override val truncationPolicy: UncheckedRounding =  UncheckedRounding.DOWN

    override fun addUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return addLongUnscaled(uDecimal, unscaled, scale)
    }

    override fun subtractUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return subtractLongUnscaled(uDecimal, unscaled, scale)
    }

    override fun multiplyByUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return Mul.multiplyByUnscaled(uDecimal, unscaled, scale)
    }

    override fun divide(uDecimalDividend: Long, uDecimalDivisor: Long): Long {
        return uDecimalDividend / uDecimalDivisor
    }

    override fun divideByLong(uDecimalDividend: Long, lDivisor: Long): Long {
        return uDecimalDividend / lDivisor
    }

    override fun divideByUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        return Div.divideByUnscaled(uDecimal, unscaled, scale)
    }

    override fun multiplyByPowerOf10(uDecimal: Long, positions: Int): Long {
        return Pow10.multiplyByPowerOf10(uDecimal, positions)
    }

    override fun divideByPowerOf10(uDecimal: Long, positions: Int): Long {
        return Pow10.divideByPowerOf10(uDecimal, positions)
    }

    override fun invert(uDecimal: Long): Long {
        return invertLong(uDecimal)
    }

    override fun sqrt(uDecimal: Long): Long {
        return sqrtLong(uDecimal)
    }

    override fun pow(uDecimal: Long, exponent: Int): Long {
        return powLong(this, DecimalRounding.DOWN, uDecimal, exponent)
    }

    override fun shiftLeft(uDecimal: Long, positions: Int): Long {
        return shiftLeft(DecimalRounding.DOWN, uDecimal, positions)
    }

    override fun shiftRight(uDecimal: Long, positions: Int): Long {
        return shiftRight(DecimalRounding.DOWN, uDecimal, positions)
    }

    override fun avg(a: Long, b: Long): Long {
        return Avg.avg(a, b)
    }

    override fun round(uDecimal: Long, precision: Int): Long {
        return round(this, uDecimal, precision)
    }

    override fun toUnscaled(uDecimal: Long, scale: Int): Long {
        return UnscaledConversion.unscaledToUnscaled(scale, this, uDecimal)
    }

    override fun toDouble(uDecimal: Long): Double {
        return DoubleConversion.longToDouble(this, uDecimal)
    }

    override fun toFloat(uDecimal: Long): Float {
        return longToFloat(this, uDecimal)
    }

    override fun fromUnscaled(unscaledValue: Long, scale: Int): Long {
        return UnscaledConversion.unscaledToLong(this, unscaledValue, scale)
    }

    override fun fromFloat(value: Float): Long {
        return floatToLong(value)
    }

    override fun fromDouble(value: Double): Long {
        return DoubleConversion.doubleToLong(value)
    }

    override fun fromBigDecimal(value: BigDecimal): Long {
        return bigDecimalToLong(RoundingMode.DOWN, value)
    }

    override fun parse(value: String): Long {
        return parseLong(this, DecimalRounding.DOWN, value, 0, value.length)
    }

    override fun parse(value: CharSequence, start: Int, end: Int): Long {
        return parseLong(this, DecimalRounding.DOWN, value, start, end)
    }

    companion object {
        /**
         * The singleton instance.
         */
		@JvmField
		val INSTANCE: UncheckedScale0fTruncatingArithmetic = UncheckedScale0fTruncatingArithmetic()
    }
}
