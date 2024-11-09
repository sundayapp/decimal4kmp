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
import org.decimal4j.factory.Factories
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.ArithmeticResult
import org.decimal4j.test.ArithmeticResult.Companion.forException
import org.decimal4j.test.ArithmeticResult.Companion.forResult
import java.math.BigDecimal

/**
 * Base class for tests comparing the result of some binary operation of the
 * [Decimal] with another `Decimal<?>` of unknown scale and a result
 * of the type `<R>`.
 *
 * @param <R> the result type of the operation
</R> */
abstract class AbstractDecimalUnknownDecimalToAnyTest<R>
/**
 * Constructor with arithemtics determining scale, rounding mode and
 * overflow policy.
 *
 * @param arithmetic
 * the arithmetic determining scale, rounding mode and overlfow
 * policy
 * @param unknownDecimalScale
 * the scale of the second Decimal argument
 */(arithmetic: DecimalArithmetic, protected val unknownDecimalScale: Int) :
    AbstractRandomAndSpecialValueTest(arithmetic) {
    protected abstract fun expectedResult(a: BigDecimal, b: BigDecimal): R
    protected abstract fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Decimal<*>): R

    override fun <S : ScaleMetrics> runRandomTest(scaleMetrics: S, index: Int) {
        val dOpA = randomDecimal(scaleMetrics)
        val dOpB: Decimal<*> =
            Factories.getDecimalFactory(unknownDecimalScale).valueOfUnscaled(randomSecondUnscaled(dOpA.unscaledValue()))
        runTest(scaleMetrics, "[$index]", dOpA, dOpB)
    }

    protected open fun randomSecondUnscaled(firstUnscaled: Long): Long {
        return nextLongOrInt()
    }

    override fun <S : ScaleMetrics> runSpecialValueTest(scaleMetrics: S) {
        val specialValues = getSpecialValues(scaleMetrics)
        for (i in specialValues.indices) {
            for (j in specialValues.indices) {
                val dOpA = newDecimal(scaleMetrics, specialValues[i])
                val dOpB: Decimal<*> =
                    Factories.getDecimalFactory(unknownDecimalScale).valueOfUnscaled(specialValues[j])
                runTest(scaleMetrics, "[$i, $j]", dOpA, dOpB)
            }
        }
    }

    protected fun <S : ScaleMetrics> runTest(scaleMetrics: S, name: String, a: Decimal<S>, b: Decimal<*>) {
        val messagePrefix = javaClass.simpleName + name + ": " + a + " " + operation() + " " + b

        val bdA = toBigDecimal(a)
        val bdB = toBigDecimal(b)

        //expected
        var expected: ArithmeticResult<R>?
        try {
            val exp = expectedResult(bdA, bdB)
            expected = forResult(exp.toString(), exp)
        } catch (e: ArithmeticException) {
            expected = forException(e)
        }

        //actual
        var actual: ArithmeticResult<R>
        try {
            val act = actualResult(a, b)
            actual = forResult(act.toString(), act)
        } catch (e: ArithmeticException) {
            actual = forException(e)
        }


        //assert
        actual.assertEquivalentTo(expected!!, messagePrefix)
    }
}
