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
import org.decimal4j.op.AbstractDecimalDecimalToAnyTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.ArithmeticResult
import org.decimal4j.test.ArithmeticResult.Companion.forException
import org.decimal4j.test.ArithmeticResult.Companion.forResult
import org.decimal4j.test.TestSettings
import org.decimal4j.truncate.OverflowMode
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import org.decimal4j.truncate.RoundingMode
import org.decimal4j.arithmetic.toJavaRoundingMode

/**
 * Unit test for [Decimal.divideAndRemainder]
 */
@RunWith(Parameterized::class)
class DivideAndRemainderTest(scaleMetrics: ScaleMetrics?, overflowMode: OverflowMode?, arithmetic: DecimalArithmetic) :
    AbstractDecimalDecimalToAnyTest<Array<out Any?>>(arithmetic) {
    override fun operation(): String {
        return "divideAndRemainder"
    }

    override fun expectedResult(a: BigDecimal, b: BigDecimal): Array<BigDecimal> {
        val res = a.divideAndRemainder(b, mathContextLong64)
        res[0] = res[0].setScale(getArithmeticScale(), RoundingMode.UNNECESSARY.toJavaRoundingMode())
        res[1] = res[1].setScale(getArithmeticScale(), RoundingMode.UNNECESSARY.toJavaRoundingMode())
        return res
    }

    override fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Decimal<S>): Array<out Decimal<S>?> {
        if (isUnchecked && RND.nextBoolean()) {
            return a.divideAndRemainder(b)
        }
        return a.divideAndRemainder(b, overflowMode)
    }

    override fun <S : ScaleMetrics> runTest(scaleMetrics: S, name: String, dOpA: Decimal<S>, dOpB: Decimal<S>) {
        val messagePrefix0 = javaClass.simpleName + name + ": " + dOpA + " " + operation() + "[0] " + dOpB
        val messagePrefix1 = javaClass.simpleName + name + ": " + dOpA + " " + operation() + "[1] " + dOpB

        val bdOpA = toBigDecimal(dOpA)
        val bdOpB = toBigDecimal(dOpB)

        //expected
        var expected0: ArithmeticResult<Long>
        var expected1: ArithmeticResult<Long>
        try {
            val exp: Array<BigDecimal> = expectedResult(bdOpA, bdOpB)
            expected0 = forResult(arithmetic, exp[0])
            expected1 = forResult(arithmetic, exp[1])
        } catch (e: ArithmeticException) {
            expected0 = forException(e)
            expected1 = forException(e)
        }

        //actual
        var actual0: ArithmeticResult<Long>
        var actual1: ArithmeticResult<Long>
        try {
            val act = actualResult(dOpA, dOpB)
            actual0 = forResult(act[0]!!)
            actual1 = forResult(act[1]!!)
        } catch (e: ArithmeticException) {
            actual0 = forException(e)
            actual1 = forException(e)
        }

        //assert
        actual0.assertEquivalentTo(expected0, messagePrefix0)
        actual1.assertEquivalentTo(expected1, messagePrefix1)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0} {1}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                data.add(arrayOf(s, OverflowMode.UNCHECKED, s.getRoundingDownArithmetic()))
                data.add(arrayOf(s, OverflowMode.CHECKED, s.getDefaultCheckedArithmetic()))
            }
            return data
        }
    }
}
