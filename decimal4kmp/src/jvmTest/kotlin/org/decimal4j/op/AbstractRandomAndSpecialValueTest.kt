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

import org.decimal4j.api.BigDecimalExtensions.toBigDecimal
import org.decimal4j.api.Decimal
import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.AbstractDecimalTest
import org.decimal4j.test.TestSettings
import org.decimal4j.test.TestSettings.getRandomTestCount
import org.junit.Test
import java.math.BigDecimal

/**
 * Base class for [Decimal] unit tests performing two types of tests, one with
 * random values and another one with special values.
 *
 * @see .runRandomTest
 * @see .runSpecialValueTest
 */
abstract class AbstractRandomAndSpecialValueTest
/**
 * Constructor with arithmetic determining scale, rounding mode and
 * overflow mode.
 *
 * @param arithmetic
 * the arithmetic determining scale, rounding mode and overflow
 * mode
 */
    (arithmetic: DecimalArithmetic) : AbstractDecimalTest(arithmetic) {
    protected open fun getRandomTestCount() = TestSettings.getRandomTestCount()

    @Test
    open fun runRandomTest() {
        val n = getRandomTestCount()
        val scaleMetrics = arithmetic.scaleMetrics
        for (i in 0 until n) {
            runRandomTest(scaleMetrics, i)
        }
    }

    @Test
    open fun runSpecialValueTest() {
        val scaleMetrics = arithmetic.scaleMetrics
        runSpecialValueTest(scaleMetrics)
    }

    /**
     * Returns the operation string, such as "+", "-", "*", "/", "abs" etc.
     *
     * @return the operation string used in exceptions and log statements
     */
    protected abstract fun operation(): String?

    protected abstract fun <S : ScaleMetrics> runRandomTest(scaleMetrics: S, index: Int)

    protected abstract fun <S : ScaleMetrics> runSpecialValueTest(scaleMetrics: S)

    companion object {
        @JvmStatic
        protected fun toBigDecimal(decimal: Decimal<*>): BigDecimal {
            return decimal.toBigDecimal()
        }
    }
}
