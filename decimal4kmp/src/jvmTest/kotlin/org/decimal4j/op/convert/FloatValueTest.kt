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
package org.decimal4j.op.convert

import org.decimal4j.api.Decimal
import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.op.AbstractDecimalToAnyTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.decimal4j.truncate.OverflowMode
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import org.decimal4j.truncate.RoundingMode
import kotlin.math.nextTowards

/**
 * Unit test for [Decimal.floatValue]
 */
@RunWith(Parameterized::class)
class FloatValueTest(scaleMetrics: ScaleMetrics?, rounding: RoundingMode?, arithmetic: DecimalArithmetic) :
    AbstractDecimalToAnyTest<Float?>(arithmetic) {
    override fun operation(): String {
        return "floatValue"
    }

    override fun expectedResult(operand: BigDecimal): Float {
        val fval = operand.toFloat()
        if (roundingMode == RoundingMode.HALF_EVEN) {
            return fval
        }
        val halfEven = BigDecimal(fval.toDouble())
        val cmp = halfEven.compareTo(operand)
        if (cmp == 0) {
            return fval
        }
        val ceil: Float
        val floor: Float
        if (cmp > 0) {
            ceil = fval
            floor = fval.nextTowards(Float.NEGATIVE_INFINITY)
        } else {
            floor = fval
            ceil = fval.nextTowards(Float.POSITIVE_INFINITY)
        }
        when (roundingMode) {
            RoundingMode.FLOOR -> return floor
            RoundingMode.CEILING -> return ceil
            RoundingMode.DOWN -> return if (operand.signum() >= 0) floor else ceil
            RoundingMode.UP -> return if (operand.signum() >= 0) ceil else floor
            RoundingMode.UNNECESSARY -> throw ArithmeticException("Rounding necessary: $operand")
            RoundingMode.HALF_EVEN -> throw IllegalArgumentException("Unsupported rounding mode: $roundingMode")
            else -> {}
        }
        //HALF_DOWN/HALF_UP
        val upperHalf = BigDecimal(ceil.toDouble()).subtract(operand).abs()
        val lowerHalf = operand.subtract(BigDecimal(floor.toDouble())).abs()
        val halfCmp = upperHalf.compareTo(lowerHalf)
        if (halfCmp != 0) {
            return if (halfCmp < 0) ceil else floor
        }
        //exactly HALF
        if (roundingMode == RoundingMode.HALF_UP) {
            return if (operand.signum() > 0) ceil else floor
        }
        //HALF_DOWN: opposite
        return if (operand.signum() > 0) floor else ceil
    }

    override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Float {
        if (roundingMode == RoundingMode.HALF_EVEN && RND.nextBoolean()) {
            return operand.toFloat()
        }
        if (RND.nextBoolean()) {
            return operand.floatValue(roundingMode)
        }
        //use arithmetic
        return if (RND.nextBoolean()) {
            arithmetic.toFloat(operand.unscaledValue())
        } else {
            arithmetic.deriveArithmetic(OverflowMode.CHECKED).toFloat(operand.unscaledValue())
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0} {1}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                for (rm in TestSettings.UNCHECKED_ROUNDING_MODES) {
                    data.add(arrayOf(s, rm, s.getArithmetic(rm)))
                }
                if (!TestSettings.UNCHECKED_ROUNDING_MODES.contains(RoundingMode.HALF_EVEN)) {
                    data.add(arrayOf(s, RoundingMode.HALF_EVEN, s.getArithmetic(RoundingMode.HALF_EVEN)))
                }
            }
            return data
        }
    }
}
