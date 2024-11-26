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
import org.decimal4j.arithmetic.Rounding.truncatedPartFor
import org.decimal4j.arithmetic.Rounding.truncatedPartFor2pow63
import org.decimal4j.arithmetic.Rounding.truncatedPartFor2pow64
import org.decimal4j.truncate.DecimalRounding
import org.decimal4j.truncate.TruncatedPart
import kotlin.math.abs
import kotlin.math.sign

/**
 * Provides methods for left and right shifts.
 */
internal object Shift {
    /**
     * Performs a shift left operation applying the given rounding mode if
     * rounding is necessary. Overflows are siltently truncated.
     *
     * @param rounding
     * the rounding to apply for negative position (i.e. a right
     * shift)
     * @param uDecimal
     * the value to shift
     * @param positions
     * the positions to shift
     * @return `round(uDecimal << positions)`
     */
	
	fun shiftLeft(rounding: DecimalRounding, uDecimal: Long, positions: Int): Long {
        if (positions >= 0) {
            return if (positions < Long.SIZE_BITS) uDecimal shl positions else 0
        }
        // one shift missing for (-Integer.MIN_VALUE) but does not matter as
        // result is always between 0 (incl) and 0.5 (excl)
        return shiftRight(rounding, uDecimal, if (-positions > 0) -positions else Int.MAX_VALUE)
    }

    /**
     * Performs a shift right operation applying the given rounding mode if
     * rounding is necessary. Overflows are siltently truncated.
     *
     * @param rounding
     * the rounding to apply if necessary
     * @param uDecimal
     * the value to shift
     * @param positions
     * the positions to shift
     * @return `round(uDecimal >> positions)`
     */
	
	fun shiftRight(rounding: DecimalRounding, uDecimal: Long, positions: Int): Long {
        if ((uDecimal == 0L) or (positions == 0)) {
            return uDecimal
        }
        if (positions >= 0) {
            if (rounding === DecimalRounding.FLOOR) {
                return if (positions < Long.SIZE_BITS) uDecimal shr positions else (if (uDecimal >= 0) 0 else -1).toLong()
            }
            if (positions < Long.SIZE_BITS) {
                val truncated = if (uDecimal >= 0) (uDecimal ushr positions) else -(-uDecimal ushr positions)
                val remainder = uDecimal - (truncated shl positions)
                val truncatedPart = if (positions == 63)
                    truncatedPartFor2pow63(remainder)
                else
                    truncatedPartFor(abs(remainder), 1L shl positions)
                return truncated + rounding.calculateRoundingIncrement(
                    uDecimal.sign,
                    truncated,
                    truncatedPart
                )
            }
            if (positions == Long.SIZE_BITS) {
                return rounding.calculateRoundingIncrement(
                    uDecimal.sign, 0,
                    truncatedPartFor2pow64(abs(uDecimal))
                ).toLong()
            }
            return rounding.calculateRoundingIncrement(
                uDecimal.sign, 0,
                TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO
            ).toLong()
        }
        return if (positions > -Long.SIZE_BITS) uDecimal shl -positions else 0
    }

    /**
     * Performs a shift left operation applying the given rounding mode if
     * rounding is necessary. Throws an exception if an overflow occurs.
     *
     * @param arith
     * the arithmetic associated with the shifted value
     * @param rounding
     * the rounding to apply for negative position (i.e. a right
     * shift)
     * @param uDecimal
     * the value to shift
     * @param positions
     * the positions to shift
     * @return `round(uDecimal << positions)`
     * @throws ArithmeticException
     * if an overflow occurs and the arithmetic's
     * [OverflowMode] is set to throw an exception
     */
    fun shiftLeftChecked(arith: DecimalArithmetic, rounding: DecimalRounding, uDecimal: Long, positions: Int): Long {
        if (positions >= 0) {
            if ((uDecimal == 0L) or (positions == 0)) {
                return uDecimal
            }
            if (positions < Long.SIZE_BITS) {
                if (uDecimal > 0) {
                    if (positions < Long.SIZE_BITS - 1) {
                        val leadingZeros = uDecimal.numberOfLeadingZeros()
                        if (leadingZeros > positions) {
                            return uDecimal shl positions
                        }
                    }
                } else if (uDecimal > Long.MIN_VALUE) {
                    val leadingZeros = uDecimal.inv().numberOfLeadingZeros()
                    if (leadingZeros > positions) {
                        return uDecimal shl positions
                    }
                }
            }
            throw ArithmeticException(
                ("Overflow: " + arith.toString(uDecimal) + " << " + positions + " = "
                        + arith.toString(uDecimal shl positions))
            )
        }
        // one shift missing for (-Integer.MIN_VALUE) but does not matter as
        // result is always between 0 (incl) and 0.5 (excl)
        return shiftRight(rounding, uDecimal, if (-positions > 0) -positions else Int.MAX_VALUE)
    }

    /**
     * Performs a shift right operation applying the given rounding mode if
     * rounding is necessary. Throws an exception if an overflow occurs.
     *
     * @param arith
     * the arithmetic associated with the shifted value
     * @param rounding
     * the rounding to apply if necessary
     * @param uDecimal
     * the value to shift
     * @param positions
     * the positions to shift
     * @return `round(uDecimal >> positions)`
     * @throws ArithmeticException
     * if an overflow occurs and the arithmetic's
     * [OverflowMode] is set to throw an exception
     */
    fun shiftRightChecked(arith: DecimalArithmetic, rounding: DecimalRounding, uDecimal: Long, positions: Int): Long {
        if (uDecimal == 0L) {
            return 0
        }
        if (positions >= 0) {
            return shiftRight(rounding, uDecimal, positions)
        }
        if (positions > -Long.SIZE_BITS) {
            try {
                return shiftLeftChecked(arith, rounding, uDecimal, -positions)
            } catch (e: ArithmeticException) {
                // ignore, throw again below with correct shift direction
            }
        }
        throw ArithmeticException(
            ("Overflow: " + arith.toString(uDecimal) + " >> " + positions + " = "
                    + arith.toString(uDecimal shr positions))
        )
    }
}
