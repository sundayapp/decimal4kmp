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

import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.factory.DecimalFactory
import org.decimal4j.factory.Factories
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.AbstractDecimalTest
import org.decimal4j.test.ArithmeticResult
import org.decimal4j.test.ArithmeticResult.Companion.forException
import org.decimal4j.test.ArithmeticResult.Companion.forResult
import org.decimal4j.test.TestSettings.getRandomTestCount
import org.junit.Test

/**
 * Base class for unit converting a value to a Decimal and back to the original
 * value. This type of factory test is only possible for exact conversions, i.e.
 * if no information is lost.
 *
 * @param <V>
 * the generic type of the source (and target) value
</V> */
abstract class AbstractFromToTest<V>(arithmetic: DecimalArithmetic) : AbstractDecimalTest(arithmetic) {
    protected abstract fun randomValue(scaleMetrics: ScaleMetrics): V

    protected abstract fun specialValues(scaleMetrics: ScaleMetrics): Array<V>

    // override if should throw an exception
    protected open fun <S : ScaleMetrics> expectedResult(scaleMetrics: S, value: V): V {
        return value
    }

    protected abstract fun <S : ScaleMetrics> actualResult(factory: DecimalFactory<S>, value: V): V

    protected val randomTestCount: Int
        get() = getRandomTestCount()

    private val decimalFactory: DecimalFactory<*>
        get() = if (RND.nextBoolean()) Factories.getDecimalFactory(
            scaleMetrics
        ) else Factories
            .getGenericDecimalFactory(scaleMetrics)

    @Test
    fun runRandomTest() {
        val n = randomTestCount
        val scaleMetrics = arithmetic.scaleMetrics
        for (i in 0 until n) {
            runTest(".random[$i]", randomValue(scaleMetrics))
        }
    }

    @Test
    fun runSpecialValueTest() {
        var index = 0
        for (value in specialValues(scaleMetrics)) {
            runTest(".special[$index]", value)
            index++
        }
    }

    protected fun runTest(name: String, value: V) {
        runTest(decimalFactory, name, value)
    }

    private fun <S : ScaleMetrics> runTest(decimalFactory: DecimalFactory<S>, name: String, value: V) {
        val messagePrefix = javaClass.simpleName + name + ": " + value

        // expected
        var expected: ArithmeticResult<V>?
        try {
            expectedResult(decimalFactory.scaleMetrics, value)
            expected = forResult(value.toString(), value)
        } catch (e: IllegalArgumentException) {
            expected = forException(e)
        } catch (e: ArithmeticException) {
            expected = forException(e)
        }

        // actual
        var actual: ArithmeticResult<V>
        try {
            val act = actualResult(decimalFactory, value)
            actual = forResult(act.toString(), act)
        } catch (e: IllegalArgumentException) {
            actual = forException(e)
        } catch (e: ArithmeticException) {
            actual = forException(e)
        }

        // assert
        actual.assertEquivalentTo(expected!!, messagePrefix)
    }
}
