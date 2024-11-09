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
package org.decimal4j.test

import org.decimal4j.api.Decimal
import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.api.toJavaRoundingMode
import org.decimal4j.arithmetic.JDKSupport
import org.junit.Assert
import java.math.BigDecimal

/**
 * Result of an arithmetic operation which can also sometimes lead to an
 * [ArithmeticException]. The assertion is based on a comparable value
 * of type `<T>`.
 *
 * @param <T> the type of the compared value
</T> */
class ArithmeticResult<T> {
    private val resultString: String?
    val compareValue: T?
    private val exception: Exception?
    private val overflow: Boolean?

    private constructor(resultString: String, compareValue: T?, overflow: Boolean?) {
        this.resultString = resultString
        this.compareValue = compareValue
        this.exception = null
        this.overflow = overflow
    }

    private constructor(resultString: String?, compareValue: T?, exception: ArithmeticException) {
        this.resultString = resultString
        this.compareValue = compareValue
        this.exception = exception
        this.overflow = null
    }

    private constructor(resultString: String?, compareValue: T?, exception: IllegalArgumentException) {
        this.resultString = resultString
        this.compareValue = compareValue
        this.exception = exception
        this.overflow = null
    }

    private constructor(resultString: String?, compareValue: T?, exception: NullPointerException) {
        this.resultString = resultString
        this.compareValue = compareValue
        this.exception = exception
        this.overflow = null
    }

    fun assertEquivalentTo(expected: ArithmeticResult<T>, messagePrefix: String) {
        if ((expected.exception == null) != (exception == null)) {
            if (expected.exception != null) {
                // XXX use proper throws declaration
                throw (AssertionError(messagePrefix + " was " + resultString + " but should lead to an exception: " + expected.exception).initCause(
                    expected.exception
                ) as AssertionError)
            } else {
                throw (AssertionError(messagePrefix + " = " + expected.resultString + " but lead to an exception: " + exception).initCause(
                    exception
                ) as AssertionError)
            }
        } else if (expected.exception != null && exception != null) {
            if (expected.exception.javaClass != exception.javaClass) {
                throw (AssertionError(messagePrefix + " exception lead to exception " + exception + " but expected was exception type: " + expected.exception).initCause(
                    exception
                ) as AssertionError)
            }
        } else {
            Assert.assertEquals(messagePrefix + " = " + expected.resultString, expected.compareValue, compareValue)
        }
    }

    fun isException(): Boolean {
        return exception != null
    }

    fun isOverflow(): Boolean {
        return overflow != null && overflow
    }

    override fun toString(): String {
        if (exception == null) {
            return javaClass.simpleName + "[" + resultString + ":" + compareValue + "]"
        }
        return javaClass.simpleName + "[" + exception + "]"
    }

    companion object {
        @JvmStatic
        fun <T> forResult(resultString: String, comparableValue: T): ArithmeticResult<T> {
            return ArithmeticResult(resultString, comparableValue, null as Boolean?)
        }

        @JvmStatic
        fun <T> forResult(resultString: String, comparableValue: T, overflow: Boolean?): ArithmeticResult<T> {
            return ArithmeticResult(resultString, comparableValue, overflow)
        }

        @JvmStatic
		fun forResult(arithmetic: DecimalArithmetic, result: BigDecimal): ArithmeticResult<Long> {
            val rnd = result.setScale(arithmetic.scale, arithmetic.roundingMode.toJavaRoundingMode())
            val resultUnscaled =
                if (arithmetic.overflowMode.isChecked) JDKSupport.bigIntegerToLongValueExact(rnd.unscaledValue()) else rnd.unscaledValue()
                    .toLong()
            return forResult(result.toPlainString(), resultUnscaled, rnd.unscaledValue().bitLength() > 63)
        }

        @JvmStatic
        fun forResult(result: Decimal<*>): ArithmeticResult<Long> {
            return forResult(result.toString(), result.unscaledValue(), null)
        }

        @JvmStatic
		fun <T> forException(e: ArithmeticException): ArithmeticResult<T> {
            return ArithmeticResult(null, null, e)
        }

        @JvmStatic
		fun <T> forException(e: IllegalArgumentException): ArithmeticResult<T> {
            return ArithmeticResult(null, null, e)
        }

        @JvmStatic
		fun <T> forException(e: NullPointerException): ArithmeticResult<T> {
            return ArithmeticResult(null, null, e)
        }
    }
}