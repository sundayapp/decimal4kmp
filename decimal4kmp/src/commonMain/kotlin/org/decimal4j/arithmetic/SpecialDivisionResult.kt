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
 * Helper class used by division and inversion methods to handle special cases.
 */
internal enum class SpecialDivisionResult {
    /**
     * `a/b` with `a==0, b!=0` leading to `0/b=0`
     */
    DIVIDEND_IS_ZERO {
        override fun divide(arithmetic: DecimalArithmetic, uDecimalDividend: Long, uDecimalDivisor: Long): Long {
            return 0
        }
    },

    /**
     * `a/b` with `b==0` leading to an arithmetic exception
     */
    DIVISOR_IS_ZERO {
        override fun divide(arithmetic: DecimalArithmetic, uDecimalDividend: Long, uDecimalDivisor: Long): Long {
            throw ArithmeticException(
                ("Division by zero: " + arithmetic.toString(uDecimalDividend) + " / "
                        + arithmetic.toString(uDecimalDivisor))
            )
        }
    },

    /**
     * `a/b` with `b==1` leading to `a/1=a`
     */
    DIVISOR_IS_ONE {
        override fun divide(arithmetic: DecimalArithmetic, uDecimalDividend: Long, uDecimalDivisor: Long): Long {
            return uDecimalDividend
        }
    },

    /**
     * `a/b` with `b==-1` resulting in `a/-1=-a`
     */
    DIVISOR_IS_MINUS_ONE {
        override fun divide(arithmetic: DecimalArithmetic, uDecimalDividend: Long, uDecimalDivisor: Long): Long {
            return arithmetic.negate(uDecimalDividend) // we must go through
            // arithmetic because
            // overflow is possible
        }
    },

    /**
     * `a/b` with `a==b` resulting in `a/a=b/b=1`
     */
    DIVISOR_EQUALS_DIVIDEND {
        override fun divide(arithmetic: DecimalArithmetic, uDecimalDividend: Long, uDecimalDivisor: Long): Long {
            return arithmetic.one()
        }
    },

    /**
     * `a/b` with `a==-b` resulting in `a/-a=-b/b=-1`
     */
    DIVISOR_EQUALS_MINUS_DIVIDEND {
        override fun divide(arithmetic: DecimalArithmetic, uDecimalDividend: Long, uDecimalDivisor: Long): Long {
            return -arithmetic.one()
        }
    };

    /**
     * Performs the division for this special division result. The arithmetics
     * overflow mode is considered.
     *
     * @param arithmetic
     * the arithmetic associated with the values
     * @param uDecimalDividend
     * the dividend
     * @param uDecimalDivisor
     * the divisor
     * @return `uDecimalDividend / uDecimalDivisor`
     * @throws ArithmeticException
     * if `this==DIVISOR_IS_ZERO` or if an overflow occurs and
     * the arithmetic's [OverflowMode] is set to throw an
     * exception
     */
    abstract fun divide(arithmetic: DecimalArithmetic, uDecimalDividend: Long, uDecimalDivisor: Long): Long

    companion object {
        /**
         * Returns the special division case if it is one and null otherwise.
         *
         * @param arithmetic
         * the arithmetic object
         * @param uDecimalDividend
         * the dividend
         * @param uDecimalDivisor
         * the divisor
         * @return the special case if it is one and null otherwise
         */
        fun getFor(
            arithmetic: DecimalArithmetic,
            uDecimalDividend: Long,
            uDecimalDivisor: Long
        ): SpecialDivisionResult? {
            // NOTE: this must be the first case because 0/0 must also throw an
            // exception!
            if (uDecimalDivisor == 0L) {
                return DIVISOR_IS_ZERO
            }
            if (uDecimalDividend == 0L) {
                return DIVIDEND_IS_ZERO
            }
            val one = arithmetic.one()
            if (uDecimalDivisor == one) {
                return DIVISOR_IS_ONE
            }
            if (uDecimalDivisor == -one) {
                return DIVISOR_IS_MINUS_ONE
            }
            if (uDecimalDividend == uDecimalDivisor) {
                return DIVISOR_EQUALS_DIVIDEND
            }
            if (uDecimalDividend == -uDecimalDivisor) {
                return DIVISOR_EQUALS_MINUS_DIVIDEND
            }
            return null
        }
    }
}