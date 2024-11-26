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
 * Helper class for arithmetic operations with overflow checks.
 */
internal object Checked {
    /**
     * Returns true if the addition `long1 + long2 = result` has resulted
     * in an overflow.
     *
     * @param long1
     * the first summand
     * @param long2
     * the second summand
     * @param result
     * the sum
     * @return true if the calculation resulted in an overflow
     */
    fun isAddOverflow(long1: Long, long2: Long, result: Long): Boolean {
        return ((long1 xor long2) >= 0) and ((long1 xor result) < 0)
    }

    /**
     * Returns true if the subtraction `minuend - subtrahend = result` has
     * resulted in an overflow.
     *
     * @param minuend
     * the minuend to subtract from
     * @param subtrahend
     * the subtrahend to subtract
     * @param result
     * the difference
     * @return true if the calculation resulted in an overflow
     */
	fun isSubtractOverflow(minuend: Long, subtrahend: Long, result: Long): Boolean {
        return ((minuend xor subtrahend) < 0) and ((minuend xor result) < 0)
    }

    /**
     * Returns true if the quotient `dividend / divisor` will result in an
     * overflow.
     *
     * @param dividend
     * the dividend
     * @param divisor
     * the divisor
     * @return true if the calculation will result in an overflow
     */
	fun isDivideOverflow(dividend: Long, divisor: Long): Boolean {
        return (dividend == Long.MIN_VALUE) and (divisor == -1L)
    }

    /**
     * Returns the sum `(long1 + long2)` of the two `long` values
     * throwing an exception if an overflow occurs.
     *
     * @param long1
     * the first summand
     * @param long2
     * the second summand
     * @return the sum of the two values
     * @throws ArithmeticException
     * if the calculation results in an overflow
     */
	fun addLong(long1: Long, long2: Long): Long {
        val result = long1 + long2
        if (isAddOverflow(long1, long2, result)) {
            throw ArithmeticException("Overflow: $long1 + $long2 = $result")
        }
        return result
    }

    /**
     * Returns the sum `(uDecimal1 + uDecimal2)` of the two unsigned
     * decimal values throwing an exception if an overflow occurs.
     *
     * @param arith
     * the arithmetic associated with the two unsigned decimals
     * @param uDecimal1
     * the first summand
     * @param uDecimal2
     * the second summand
     * @return the sum of the two values
     * @throws ArithmeticException
     * if the calculation results in an overflow
     */

	fun add(arith: DecimalArithmetic, uDecimal1: Long, uDecimal2: Long): Long {
        val result = uDecimal1 + uDecimal2
        if (((uDecimal1 xor uDecimal2) >= 0) and ((uDecimal1 xor result) < 0)) {
            throw ArithmeticException(
                ("Overflow: " + arith.toString(uDecimal1) + " + " + arith.toString(uDecimal2)
                        + " = " + arith.toString(result))
            )
        }
        return result
    }

    /**
     * Returns the difference `(lMinuend - lSubtrahend)` of the two
     * `long` values throwing an exception if an overflow occurs.
     *
     * @param lMinuend
     * the minuend
     * @param lSubtrahend
     * the subtrahend
     * @return the difference of the two values
     * @throws ArithmeticException
     * if the calculation results in an overflow
     */
    fun subtractLong(lMinuend: Long, lSubtrahend: Long): Long {
        val result = lMinuend - lSubtrahend
        if (isSubtractOverflow(lMinuend, lSubtrahend, result)) {
            throw ArithmeticException("Overflow: $lMinuend - $lSubtrahend = $result")
        }
        return result
    }

    /**
     * Returns the difference `(uDecimalMinuend - uDecimalSubtrahend)` of
     * the two unscaled decimal values throwing an exception if an overflow
     * occurs.
     *
     * @param arith
     * the arithmetic associated with the two unsigned decimals
     * @param uDecimalMinuend
     * the minuend
     * @param uDecimalSubtrahend
     * the subtrahend
     * @return the difference of the two values
     * @throws ArithmeticException
     * if the calculation results in an overflow
     */

	fun subtract(arith: DecimalArithmetic, uDecimalMinuend: Long, uDecimalSubtrahend: Long): Long {
        val result = uDecimalMinuend - uDecimalSubtrahend
        if (isSubtractOverflow(uDecimalMinuend, uDecimalSubtrahend, result)) {
            throw ArithmeticException(
                ("Overflow: " + arith.toString(uDecimalMinuend) + " - "
                        + arith.toString(uDecimalSubtrahend) + " = " + arith.toString(result))
            )
        }
        return result
    }

    /**
     * Returns the product `(lValue1 * lValue2)` of the two `long`
     * values throwing an exception if an overflow occurs.
     *
     * @param lValue1
     * the first factor
     * @param lValue2
     * the second factor
     * @return the product of the two values
     * @throws ArithmeticException
     * if the calculation results in an overflow
     */

	fun multiplyLong(lValue1: Long, lValue2: Long): Long {
        // Hacker's Delight, Section 2-12
        val leadingZeros =
            (lValue1.numberOfLeadingZeros() + (lValue1.inv().numberOfLeadingZeros() )
                    + (lValue2.numberOfLeadingZeros() ) + (lValue2.inv().numberOfLeadingZeros() ))
        /*
		 * If leadingZeros > Long.SIZE + 1 it's definitely fine, if it's <
		 * Long.SIZE it's definitely bad. We do the leadingZeros check to avoid
		 * the division below if at all possible.
		 * 
		 * Otherwise, if b == Long.MIN_VALUE, then the only allowed values of a
		 * are 0 and 1. We take care of all a < 0 with their own check, because
		 * in particular, the case a == -1 will incorrectly pass the division
		 * check below.
		 * 
		 * In all other cases, we check that either a is 0 or the result is
		 * consistent with division.
		 */
        val result = lValue1 * lValue2
        if (leadingZeros > Long.SIZE_BITS + 1) {
            return result
        }
        if (leadingZeros < Long.SIZE_BITS || ((lValue1 < 0) and (lValue2 == Long.MIN_VALUE))
            || (lValue1 != 0L && (result / lValue1) != lValue2)
        ) {
            throw ArithmeticException("Overflow: $lValue1 * $lValue2 = $result")
        }
        return result
    }

    /**
     * Returns the product `(uDecimal * lValue)` of an unsigned decimal
     * value and a `long` value throwing an exception if an overflow
     * occurs.
     *
     * @param arith
     * the arithmetic associated with the first unsigned decimal
     * argument
     * @param uDecimal
     * the first factor
     * @param lValue
     * the second factor
     * @return the product of the two values
     * @throws ArithmeticException
     * if the calculation results in an overflow
     */

	fun multiplyByLong(arith: DecimalArithmetic, uDecimal: Long, lValue: Long): Long {
        // Hacker's Delight, Section 2-12
        val leadingZeros =
            ((uDecimal.numberOfLeadingZeros() ) + (uDecimal.inv().numberOfLeadingZeros() )
                    + (lValue.numberOfLeadingZeros() ) + (lValue.inv().numberOfLeadingZeros() ))
        /*
		 * If leadingZeros > Long.SIZE + 1 it's definitely fine, if it's <
		 * Long.SIZE it's definitely bad. We do the leadingZeros check to avoid
		 * the division below if at all possible.
		 * 
		 * Otherwise, if b == Long.MIN_VALUE, then the only allowed values of a
		 * are 0 and 1. We take care of all a < 0 with their own check, because
		 * in particular, the case a == -1 will incorrectly pass the division
		 * check below.
		 * 
		 * In all other cases, we check that either a is 0 or the result is
		 * consistent with division.
		 */
        val result = uDecimal * lValue
        if (leadingZeros > Long.SIZE_BITS + 1) {
            return result
        }
        if (leadingZeros < Long.SIZE_BITS || ((uDecimal < 0) and (lValue == Long.MIN_VALUE))
            || (uDecimal != 0L && (result / uDecimal) != lValue)
        ) {
            throw ArithmeticException(
                "Overflow: " + arith.toString(uDecimal) + " * " + lValue + " = " + arith.toString(result)
            )
        }
        return result
    }

    /**
     * Returns the quotient `(lDividend / lDivisor)` of the two
     * `long` values throwing an exception if an overflow occurs.
     *
     * @param lDividend
     * the dividend to divide
     * @param lDivisor
     * the divisor to divide by
     * @return the quotient of the two values
     * @throws ArithmeticException
     * if the calculation results in an overflow
     */

	fun divideLong(lDividend: Long, lDivisor: Long): Long {
        if ((lDivisor == -1L) and (lDividend == Long.MIN_VALUE)) {
            throw ArithmeticException("Overflow: " + lDividend + " / " + lDivisor + " = " + Long.MIN_VALUE)
        }
        return lDividend / lDivisor
    }

    /**
     * Returns the quotient `(uDecimalDividend / lDivisor)` of an unscaled
     * decimal value and a `long` value throwing an exception if an
     * overflow occurs.
     *
     * @param arith
     * the arithmetic associated with the first unsigned decimal
     * argument
     * @param uDecimalDividend
     * the dividend to divide
     * @param lDivisor
     * the divisor to divide by
     * @return the quotient of the two values
     * @throws ArithmeticException
     * if the calculation results in an overflow
     */

	fun divideByLong(arith: DecimalArithmetic, uDecimalDividend: Long, lDivisor: Long): Long {
        if (lDivisor == 0L) {
            throw ArithmeticException("Division by zero: " + arith.toString(uDecimalDividend) + " / " + lDivisor)
        }
        if ((lDivisor == -1L) and (uDecimalDividend == Long.MIN_VALUE)) {
            throw ArithmeticException(
                ("Overflow: " + arith.toString(uDecimalDividend) + " / " + lDivisor + " = "
                        + arith.toString(Long.MIN_VALUE))
            )
        }
        return uDecimalDividend / lDivisor
    }

    /**
     * Returns the absolute value `|value|` throwing an exception if an
     * overflow occurs.
     *
     * @param arith
     * the arithmetic associated with the value
     * @param value
     * the number whose absolute value to return
     * @return the absolute of the specified value
     * @throws ArithmeticException
     * if the calculation results in an overflow
     */

	fun abs(arith: DecimalArithmetic, value: Long): Long {
        val abs = kotlin.math.abs(value)
        if (abs < 0) {
            throw ArithmeticException("Overflow: abs(" + arith.toString(value) + ") = " + abs)
        }
        return abs
    }

    /**
     * Returns the negation `(-value)` throwing an exception if an
     * overflow occurs.
     *
     * @param arith
     * the arithmetic associated with the value
     * @param value
     * the number to negate
     * @return the negation of the specified value
     * @throws ArithmeticException
     * if the calculation results in an overflow
     */

	fun negate(arith: DecimalArithmetic, value: Long): Long {
        val neg = -value
        if ((value != 0L) and ((value xor neg) >= 0)) {
            throw ArithmeticException("Overflow: -" + arith.toString(value) + " = " + neg)
        }
        return neg
    }
}

fun Long.numberOfLeadingZeros(): Int {
    if (this == 0L) return 64
    var count = 0
    var value = this
    while (value and (1L shl 63) == 0L) {
        count++
        value = value shl 1
    }
    return count
}
