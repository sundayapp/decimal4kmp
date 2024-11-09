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
package org.decimal4j.op.arith

import org.decimal4j.api.Decimal
import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.op.AbstractDecimalUnscaledToDecimalTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.TruncationPolicy
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import org.decimal4j.api.RoundingMode

/**
 * Unit test for [Decimal.addUnscaled]
 */
@RunWith(Parameterized::class)
class DivideUnscaledTest(sm: ScaleMetrics?, tp: TruncationPolicy?, scale: Int, arithmetic: DecimalArithmetic) :
    AbstractDecimalUnscaledToDecimalTest(sm, tp, scale, arithmetic) {
    override fun operation(): String {
        return "/ 10^" + (-scale) + " /"
    }

    @Test
    fun testProblem1() {
        if (getArithmeticScale() == 17 && isUnchecked && scale == 16) {
            runTest(
                scaleMetrics, "problem1", newDecimal(scaleMetrics, 1000000000000000L),
                Int.MIN_VALUE.toLong()
            )
        }
    }

    @Test
    fun testProblem2() {
        if (getArithmeticScale() == 17 && isUnchecked && scale == 17) {
            runTest(scaleMetrics, "problem2", newDecimal(scaleMetrics, -9223372036854775807L), 100000000000000000L)
        }
    }

    @Test
    fun testDivisionOverflow() {
        //test division overflow
        runTest(scaleMetrics, "DivisionOverflow", newDecimal(scaleMetrics, Long.MIN_VALUE), -1)
    }

    @Test
    fun testHalfEven() {
        if (roundingMode == RoundingMode.HALF_EVEN) {
            runTest(scaleMetrics, "HalfEven", newDecimal(scaleMetrics, Long.MIN_VALUE), Long.MIN_VALUE)
        }
    }

    override fun expectedResult(a: BigDecimal, b: Long): BigDecimal {
        return a.divide(toBigDecimal(b), mathContextLong128)
    }

    override fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Long): Decimal<S> {
        if (isStandardTruncationPolicy && RND.nextBoolean()) {
            if (scale == getArithmeticScale() && RND.nextBoolean()) {
                return a.divideUnscaled(b)
            }
            return a.divideUnscaled(b, scale)
        }
        if (isUnchecked && RND.nextBoolean()) {
            if (scale == getArithmeticScale() && RND.nextBoolean()) {
                return a.divideUnscaled(b, roundingMode)
            }
            return a.divideUnscaled(b, scale, roundingMode)
        }
        if (scale == getArithmeticScale() && RND.nextBoolean()) {
            return a.divideUnscaled(b, truncationPolicy)
        }
        return a.divideUnscaled(b, scale, truncationPolicy)
    }
}
