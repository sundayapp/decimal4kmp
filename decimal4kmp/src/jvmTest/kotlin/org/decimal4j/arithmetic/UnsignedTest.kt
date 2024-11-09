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

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit test for [Unsigned].
 */
@RunWith(JUnitParamsRunner::class)
class UnsignedTest {
    // Division
    @Test
    @Parameters(source = DivisionArgumentProvider::class)
    @TestCaseName("{0} / {1} = {2}")
    fun unsignedDivision(
        dividend: Long,
        divisor: Long,
        expectedQuotient: Long
    ) {
        // when
        val result = Unsigned.divide(dividend, divisor)

        // then
        Assert.assertEquals(expectedQuotient, result)
    }

    // Special cases for division
    @Test(expected = ArithmeticException::class)
    fun divideByZero() {
        // given
        val dividend: Long = 1
        val divisor: Long = 0

        // when
        Unsigned.divide(dividend, divisor)
    }

    // Comparison tests
    @Test
    @Parameters(source = LowerThanArgumentProvider::class)
    @TestCaseName("{0} < {1}")
    fun unsignedLowerThan(first: Long, second: Long) {
        assertLess(first, second)
    }

    @Test
    @Parameters(source = LowerThanArgumentProvider::class)
    @TestCaseName("{1} > {0}")
    fun unsignedGreaterThan(first: Long, second: Long) {
        assertGreater(second, first)
    }

    /**
     * Provides arguments for division tests where dividend and divisor are
     * unsigned long values.
     *
     * <pre>
     * Arguments: { dividend (a), divisor (b), expected quotient(c) }
     *
     * Notes:
     * - the expression 'large value' is used for long values greater than
     * [Long.MAX_VALUE] (i.e. >= 2^63)
    </pre> */
    object DivisionArgumentProvider {
        @JvmStatic
        fun provideInput(): Array<Any> {
            return arrayOf( //
                //
                /**
                 * Cases for large divisor values (i.e. b >= 2^63)
                 */
                /**
                 * Cases for large divisor values (i.e. b >= 2^63)
                 */
                // b > 2^63, a < b
                arrayOf(LARGE_VALUE - 1, LARGE_VALUE, 0),  //
                arrayOf(0, LARGE_VALUE, 0),  //
                arrayOf(1, LARGE_VALUE, 0),  //
                arrayOf(100, LARGE_VALUE, 0),  //
                // b > 2^63, a = b

                arrayOf(FIRST_LARGE_VALUE, FIRST_LARGE_VALUE, 1),
                arrayOf(LARGE_VALUE, LARGE_VALUE, 1),  //
                // b = 2^63, a < b

                arrayOf(FIRST_LARGE_VALUE - 1, FIRST_LARGE_VALUE, 0),  //
                arrayOf(0, FIRST_LARGE_VALUE, 0),  //
                arrayOf(1, FIRST_LARGE_VALUE, 0),  //
                arrayOf(100, FIRST_LARGE_VALUE, 0),  //
                // b = 2^63, a = b

                arrayOf(FIRST_LARGE_VALUE, FIRST_LARGE_VALUE, 1),  //
                // b > 2^63, a > b

                arrayOf(LAST_LARGE_VALUE, LARGE_VALUE, 1),
                arrayOf(LARGE_VALUE + 1, LARGE_VALUE, 1),
                /**
                 * Cases for 'normal' values (i.e. a, b < 2^63)
                 */
                /**
                 * Cases for 'normal' values (i.e. a, b < 2^63)
                 */

                arrayOf(0, 1, 0),  //
                arrayOf(1, 1, 1),  //
                arrayOf(100, 2, 50),  //
                arrayOf(Long.MAX_VALUE, Long.MAX_VALUE, 1),  //
                arrayOf(Long.MAX_VALUE, 2, Long.MAX_VALUE / 2),  //
                arrayOf(Long.MAX_VALUE, Long.MAX_VALUE - 1, 1),  //
                arrayOf(Long.MAX_VALUE - 1, Long.MAX_VALUE, 0),  //
                /**
                 * Cases for 'normal' divisor & 'large' dividend (i.e. b <
                 * 2^63, a >= 2^63)
                 */
                /**
                 * Cases for 'normal' divisor & 'large' dividend (i.e. b <
                 * 2^63, a >= 2^63)
                 */
                // a = 2^63, b < a

                arrayOf(FIRST_LARGE_VALUE, 1, FIRST_LARGE_VALUE),  //
                arrayOf(FIRST_LARGE_VALUE, 100, 92233720368547758L),  //
                arrayOf(FIRST_LARGE_VALUE, FIRST_LARGE_VALUE - 1, 1),  //
                // a > 2^63, b < a

                arrayOf(LARGE_VALUE, 1, -9223372036854775799L),  //
                arrayOf(LARGE_VALUE, 100, 92233720368547758L),  //
                arrayOf(LARGE_VALUE, Long.MAX_VALUE, 1) //
            )
        }
    }

    /**
     * Provides arguments for testing [Unsigned.isLess].
     *
     * <pre>
     * Arguments: { first (a), second (b) }
    </pre> */

    object LowerThanArgumentProvider {
        @JvmStatic
        fun provideInput(): Array<Any> {
            return arrayOf( //
                // a, b < 2^63
                arrayOf(0, 1),  //
                arrayOf(0, 100),  //
                arrayOf(1, 100),  //

                arrayOf(Long.MAX_VALUE / 2, Long.MAX_VALUE),  //
                arrayOf(100, Long.MAX_VALUE),  //
                // a < 2^63, b = 2^63

                arrayOf(0, FIRST_LARGE_VALUE),  //
                arrayOf(1, FIRST_LARGE_VALUE),  //
                arrayOf(100, FIRST_LARGE_VALUE),  //
                arrayOf(Long.MAX_VALUE / 2, FIRST_LARGE_VALUE),  //
                arrayOf(Long.MAX_VALUE, FIRST_LARGE_VALUE),  //
                // a < 2^63, b > 2^63

                arrayOf(0, LARGE_VALUE),  //
                arrayOf(1, LARGE_VALUE),  //
                arrayOf(100, LARGE_VALUE),  //
                arrayOf(Long.MAX_VALUE / 2, LARGE_VALUE),  //
                arrayOf(Long.MAX_VALUE, LARGE_VALUE),  //

                arrayOf(0, LAST_LARGE_VALUE),  //
                arrayOf(Long.MAX_VALUE / 2, LAST_LARGE_VALUE),  //
                arrayOf(Long.MAX_VALUE, LAST_LARGE_VALUE) //
            )
        }
    }

    companion object {
        private const val FIRST_LARGE_VALUE = Long.MAX_VALUE + 1
        private const val LARGE_VALUE = Long.MAX_VALUE + 10

        private const val LAST_LARGE_VALUE = 2 * FIRST_LARGE_VALUE - 1

        private fun assertLess(first: Long, second: Long) {
            Assert.assertTrue(Unsigned.compare(first, second) < 0)
            Assert.assertTrue(Unsigned.isLess(first, second))
            Assert.assertTrue(Unsigned.isLessOrEqual(first, second))
            Assert.assertFalse(Unsigned.isGreater(first, second))
        }

        private fun assertGreater(first: Long, second: Long) {
            Assert.assertTrue(Unsigned.compare(first, second) > 0)
            Assert.assertTrue(Unsigned.isGreater(first, second))
            Assert.assertFalse(Unsigned.isLess(first, second))
            Assert.assertFalse(Unsigned.isLessOrEqual(first, second))
        }
    }
}
