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
package org.decimal4j.op

import org.decimal4j.api.Decimal
import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.ArithmeticResult
import org.decimal4j.test.ArithmeticResult.Companion.forException
import org.decimal4j.test.ArithmeticResult.Companion.forResult
import java.math.BigDecimal

/**
 * Base class for tests comparing the result of some binary operation of the
 * [Decimal] with the expected result produced by the equivalent operation
 * of the [BigDecimal].
 */
abstract class AbstractDecimalDecimalToDecimalTest
/**
 * Constructor with arithemtics determining scale, rounding mode and
 * overflow policy.
 *
 * @param arithmetic
 * the arithmetic determining scale, rounding mode and overlfow
 * policy
 */
    (arithmetic: DecimalArithmetic) : AbstractRandomAndSpecialValueTest(arithmetic) {
    protected abstract fun expectedResult(a: BigDecimal, b: BigDecimal): BigDecimal

    protected abstract fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Decimal<S>): Decimal<S>

    override fun <S : ScaleMetrics> runRandomTest(scaleMetrics: S, index: Int) {
        val dOpA = randomDecimal(scaleMetrics)
        val dOpB = randomDecimal(scaleMetrics)
        runTest(scaleMetrics, "[$index]", dOpA, dOpB)
    }

    override fun <S : ScaleMetrics> runSpecialValueTest(scaleMetrics: S) {
        val specialValues = getSpecialValues(scaleMetrics)
        for (i in specialValues.indices) {
            for (j in specialValues.indices) {
                val dOpA = newDecimal(scaleMetrics, specialValues[i])
                val dOpB = newDecimal(scaleMetrics, specialValues[j])
                runTest(scaleMetrics, "[$i, $j]", dOpA, dOpB)
            }
        }
    }

    protected fun <S : ScaleMetrics> runTest(scaleMetrics: S, name: String, dOpA: Decimal<S>, dOpB: Decimal<S>) {
        val messagePrefix = javaClass.simpleName + name + ": " + dOpA + " " + operation() + " " + dOpB

        val bdOpA = toBigDecimal(dOpA)
        val bdOpB = toBigDecimal(dOpB)

        // expected
        var expected: ArithmeticResult<Long>
        try {
            expected = forResult(arithmetic, expectedResult(bdOpA, bdOpB))
        } catch (e: IllegalArgumentException) {
            expected = forException(e)
        } catch (e: ArithmeticException) {
            expected = forException(e)
        }

        // actual
        var actual: ArithmeticResult<Long>
        try {
            actual = forResult(actualResult(dOpA, dOpB))
        } catch (e: IllegalArgumentException) {
            actual = forException(e)
        } catch (e: ArithmeticException) {
            actual = forException(e)
        }

        // assert
        actual.assertEquivalentTo(expected, messagePrefix)
    }
}
