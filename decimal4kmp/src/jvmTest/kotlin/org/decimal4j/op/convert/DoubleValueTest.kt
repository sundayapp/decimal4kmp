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
import org.decimal4j.factory.Factories
import org.decimal4j.op.AbstractDecimalToAnyTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.decimal4j.truncate.OverflowMode
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import org.decimal4j.api.RoundingMode
import kotlin.math.nextTowards

/**
 * Unit test for [Decimal.doubleValue]
 */
@RunWith(Parameterized::class)
class DoubleValueTest(scaleMetrics: ScaleMetrics?, rounding: RoundingMode?, arithmetic: DecimalArithmetic) :
    AbstractDecimalToAnyTest<Double?>(arithmetic) {
    override fun operation(): String {
        return "doubleValue"
    }

    @Test
    fun scaleFactorPlusUlpTest() {
        runTest(
            scaleMetrics, "scaleFactorPlusUlpTest", Factories.getDecimalFactory(
                scaleMetrics
            ).valueOfUnscaled(1 + scaleMetrics.getScaleFactor())
        )
    }

    override fun expectedResult(operand: BigDecimal): Double {
        val dval = operand.toDouble()
        if (roundingMode == RoundingMode.HALF_EVEN) {
            return dval
        }
        val halfEven = BigDecimal(dval)
        val cmp = halfEven.compareTo(operand)
        if (cmp == 0) {
            return dval
        }
        val ceil: Double
        val floor: Double
        if (cmp > 0) {
            ceil = dval
            floor = dval.nextTowards(Double.NEGATIVE_INFINITY)
        } else {
            floor = dval
            ceil = dval.nextTowards(Double.POSITIVE_INFINITY)
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
        val upperHalf = BigDecimal(ceil).subtract(operand).abs()
        val lowerHalf = operand.subtract(BigDecimal(floor)).abs()
        val halfCmp = upperHalf.compareTo(lowerHalf)
        if (halfCmp != 0) {
            return if (halfCmp < 0) ceil else floor
        }
        // exactly HALF
        if (roundingMode == RoundingMode.HALF_UP) {
            return if (operand.signum() > 0) ceil else floor
        }
        //HALF_DOWN: opposite
        return if (operand.signum() > 0) floor else ceil
    }

    override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Double {
        if (roundingMode == RoundingMode.HALF_EVEN && RND.nextBoolean()) {
            return operand.doubleValue()
        }
        if (RND.nextBoolean()) {
            return operand.doubleValue(roundingMode)
        }
        //use arithmetic
        return if (RND.nextBoolean()) {
            arithmetic.toDouble(operand.unscaledValue())
        } else {
            arithmetic.deriveArithmetic(OverflowMode.CHECKED).toDouble(operand.unscaledValue())
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
