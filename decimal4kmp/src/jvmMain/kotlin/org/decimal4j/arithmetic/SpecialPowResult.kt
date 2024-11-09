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

import org.decimal4j.api.DecimalArithmetic

/**
 * Helper class used by pow methods to handle special cases.
 */
internal enum class SpecialPowResult {
    /**
     * `a^n` with `n==0` leading to `1`
     */
    EXPONENT_IS_ZERO {
        override fun pow(arithmetic: DecimalArithmetic, uDecimal: Long, exponent: Int): Long {
            return arithmetic.one() // yes 0^0 is also 1
        }
    },

    /**
     * `a^n` with `n==1` leading to `a`
     */
    EXPONENT_IS_ONE {
        override fun pow(arithmetic: DecimalArithmetic, uDecimal: Long, exponent: Int): Long {
            return uDecimal
        }
    },

    /**
     * `a^n` with `a==0` leading to `0` if `n>=0` and to
     * an arithmetic exception if `n<0`
     */
    BASE_IS_ZERO {
        override fun pow(arithmetic: DecimalArithmetic, uDecimal: Long, exponent: Int): Long {
            if (exponent >= 0) {
                // uDecimal == 0 should never happen (0^0 is usually defined as
                // 1)
                return 0
            }
            throw ArithmeticException("Division by zero: " + arithmetic.toString(uDecimal) + "^" + exponent)
        }
    },

    /**
     * `a^n` with `a==1` leading to `1`
     */
    BASE_IS_ONE {
        override fun pow(arithmetic: DecimalArithmetic, uDecimal: Long, exponent: Int): Long {
            return uDecimal // uDecimal is 1
        }
    },

    /**
     * `a^n` with `a==-1` leading to `1` if `n` is even
     * and to `-1` if `n` is odd.
     */
    BASE_IS_MINUS_ONE {
        override fun pow(arithmetic: DecimalArithmetic, uDecimal: Long, exponent: Int): Long {
            return if ((exponent and 0x1) == 0) -uDecimal else uDecimal // uDecimal is
            // one and
            // it's
            // negation
            // cannot
            // overflow
        }
    },

    /**
     * `a^n` with `n==-1` leading to `1/a`
     */
    EXPONENT_IS_MINUS_ONE {
        override fun pow(arithmetic: DecimalArithmetic, uDecimal: Long, exponent: Int): Long {
            return arithmetic.invert(uDecimal)
        }
    },

    /**
     * `a^n` with `n==2` leading to `square(a)`
     */
    EXPONENT_IS_TWO {
        override fun pow(arithmetic: DecimalArithmetic, uDecimal: Long, exponent: Int): Long {
            return arithmetic.square(uDecimal)
        }
    };

    /**
     * Performs the exponentiation for this special pow result. The arithmetics
     * overflow mode is considered.
     *
     * @param arithmetic
     * the arithmetic associated with the values
     * @param uDecimal
     * the base value
     * @param exponent
     * the exponent
     * @return `uDecimal<sup>exponent</sup>`
     * @throws ArithmeticException
     * if `uDecimal==0` and exponent is negative or if an
     * overflow occurs and the arithmetic's [OverflowMode] is
     * set to throw an exception
     */
    abstract fun pow(arithmetic: DecimalArithmetic, uDecimal: Long, exponent: Int): Long

    companion object {
        /**
         * Returns the special power case if it is one and null otherwise.
         *
         * @param arithmetic
         * the arithmetic object
         * @param uDecimal
         * the base
         * @param n
         * the exponent
         * @return the special case if it is one and null otherwise
         */
        fun getFor(arithmetic: DecimalArithmetic, uDecimal: Long, n: Long): SpecialPowResult? {
            if (n == 0L) {
                return EXPONENT_IS_ZERO
            }
            if (n == 1L) {
                return EXPONENT_IS_ONE
            }
            if (uDecimal == 0L) {
                return BASE_IS_ZERO
            }
            val one = arithmetic.one()
            if (uDecimal == one) {
                return BASE_IS_ONE
            }
            if (uDecimal == -one) {
                return BASE_IS_MINUS_ONE
            }
            if (n == -1L) {
                return EXPONENT_IS_MINUS_ONE
            }
            if (n == 2L) {
                return EXPONENT_IS_TWO
            }
            return null
        }
    }
}