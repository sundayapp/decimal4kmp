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
import org.decimal4j.truncate.DecimalRounding

/**
 * Provides static methods to invert a Decimal number, that is, to calculate
 * `1/x`.
 */
internal object Invert {
    /**
     * Inverts the specified long value truncating the result if necessary.
     *
     * @param lValue
     * the long value to invert
     * @return `round<sub>DOWN</sub>(1/lValue)`
     * @throws ArithmeticException
     * if `lValue == 0`
     */
	
	fun invertLong(lValue: Long): Long {
        if (lValue == 0L) {
            throw ArithmeticException("Division by zero: $lValue^-1")
        }
        if (lValue == 1L) {
            return 1
        }
        if (lValue == -1L) {
            return -1
        }
        return 0
    }

    /**
     * Inverts the specified long value rounding the result if necessary.
     *
     * @param rounding
     * the rounding to apply if necessary
     * @param lValue
     * the long value to invert
     * @return `round(1/lValue)`
     * @throws ArithmeticException
     * if `lValue == 0` or if
     * `roundingMode==UNNECESSARY` and rounding is necessary
     */
	
	fun invertLong(rounding: DecimalRounding, lValue: Long): Long {
        // special cases first
        if (lValue == 0L) {
            throw ArithmeticException("Division by zero: $lValue^-1")
        }
        if (lValue == 1L) {
            return 1
        }
        if (lValue == -1L) {
            return -1
        }
        return Rounding.calculateRoundingIncrementForDivision(rounding, 0, 1, lValue).toLong()
    }

    /**
     * Inverts the specified unscaled decimal value truncating the result if
     * necessary.
     *
     * @param arith
     * the arithmetic associated with the given value
     * @param uDecimal
     * the unscaled decimal value to invert
     * @return `round<sub>DOWN</sub>(1/uDecimal)`
     * @throws ArithmeticException
     * if `uDecimalDividend` is zero or if an overflow occurs
     * and the arithmetics [OverflowMode] is set to throw an
     * exception
     * @see DecimalArithmetic.divide
     */
	
	fun invert(arith: DecimalArithmetic, uDecimal: Long): Long {
        // special cases are handled by divide
        return arith.divide(arith.one(), uDecimal)
    }

    /**
     * Inverts the specified unscaled decimal value rounding the result if
     * necessary.
     *
     * @param arith
     * the arithmetic associated with the given value
     * @param rounding
     * the rounding to apply if necessary
     * @param uDecimal
     * the unscaled decimal value to invert
     * @return `round(1/uDecimal)`
     * @throws ArithmeticException
     * if `uDecimalDividend` is zero, if `roundingMode`
     * is UNNECESSARY and rounding is necessary or if an overflow
     * occurs and the arithmetics [OverflowMode] is set to
     * throw an exception
     * @see DecimalArithmetic.divide
     */
	
	fun invert(arith: DecimalArithmetic, rounding: DecimalRounding?, uDecimal: Long): Long {
        // special cases are handled by divide
        return arith.divide(arith.one(), uDecimal)
    }
}
