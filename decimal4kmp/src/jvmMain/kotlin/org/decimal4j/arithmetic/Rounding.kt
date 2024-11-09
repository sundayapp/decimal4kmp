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

import org.decimal4j.truncate.DecimalRounding
import org.decimal4j.truncate.TruncatedPart
import kotlin.math.abs

/**
 * Utility class to calculate rounding increments in different situations;
 * utilizes functionality provided by [DecimalRounding] and
 * [TruncatedPart].
 */
internal object Rounding {
    /**
     * Returns the rounding increment appropriate for the specified
     * `rounding`. The returned value is one of -1, 0 or 1.
     *
     * @param rounding
     * the rounding mode to apply
     * @param sign
     * the sign of the total value, either +1 or -1; determines the
     * result value if rounded
     * @param truncatedValue
     * the truncated result before rounding is applied (only used for
     * HALF_EVEN rounding)
     * @param firstTruncatedDigit
     * the first truncated digit, must be in `[0, 1, ..., 9]`
     * @param zeroAfterFirstTruncatedDigit
     * true if all truncated digits after the first truncated digit
     * are zero, and false otherwise
     * @return the value to add to `truncatedValue` to get the rounded
     * result, one of -1, 0 or 1
     */
    fun calculateRoundingIncrement(
        rounding: DecimalRounding,
        sign: Int,
        truncatedValue: Long,
        firstTruncatedDigit: Int,
        zeroAfterFirstTruncatedDigit: Boolean
    ): Int {
        return rounding.calculateRoundingIncrement(
            sign,
            truncatedValue,
            TruncatedPart.valueOf(firstTruncatedDigit, zeroAfterFirstTruncatedDigit)
        )
    }

    /**
     * Returns the rounding increment appropriate for the specified
     * `rounding` given the remaining truncated digits truncated by a
     * given divisor. The returned value is one of -1, 0 or 1.
     *
     * @param rounding
     * the rounding mode to apply
     * @param truncatedValue
     * the truncated result before rounding is applied (only used for
     * HALF_EVEN rounding)
     * @param truncatedDigits
     * the truncated part, it most hold that
     * `abs(truncatedDigits) < abs(divisor)`
     * @param divisor
     * the divisor that led to the truncated digits
     * @return the value to add to `truncatedValue` to get the rounded
     * result, one of -1, 0 or 1
     */
	@JvmStatic
	fun calculateRoundingIncrementForDivision(
        rounding: DecimalRounding,
        truncatedValue: Long,
        truncatedDigits: Long,
        divisor: Long
    ): Int {
        if (truncatedDigits == 0L) {
            return 0
        }
        val truncatedPart = truncatedPartFor(abs(truncatedDigits), abs(divisor))
        return rounding.calculateRoundingIncrement(
            java.lang.Long.signum(truncatedDigits xor divisor),
            truncatedValue,
            truncatedPart
        )
    }

    /**
     * Returns the rounding increment appropriate for the specified
     * `rounding` given the remaining truncated digits truncated by modulo
     * one. The returned value is one of -1, 0 or 1.
     *
     * @param rounding
     * the rounding mode to apply
     * @param truncatedValue
     * the truncated result before rounding is applied (only used for
     * HALF_EVEN rounding)
     * @param truncatedDigits
     * the truncated part of a double, must be `>-one` and
     * `<one`
     * @param one
     * the value representing 1 which is `10^scale`, must be
     * `>= 10`
     * @return the value to add to `truncatedValue` to get the rounded
     * result, one of -1, 0 or 1
     */
	@JvmStatic
	fun calculateRoundingIncrement(
        rounding: DecimalRounding,
        truncatedValue: Long,
        truncatedDigits: Long,
        one: Long
    ): Int {
        if (truncatedDigits == 0L) {
            return 0
        }
        val truncatedPart = truncatedPartFor(abs(truncatedDigits), one)
        return rounding.calculateRoundingIncrement(
            java.lang.Long.signum(truncatedDigits),
            truncatedValue,
            truncatedPart
        )
    }

    /**
     * Returns a truncated part constant given a non-negative remainder
     * resulting from a division by the given non-negative divisor.
     *
     * @param nonNegativeRemainder
     * the remainder part, not negative and
     * `nonNegativeRemainder < nonNegativeDivisor`
     * @param nonNegativeDivisor
     * the divisor, not negative or LONG.MIN_VALUE --- the latter
     * equal to `abs(Long.MIN_VALUE)`
     * @return the truncated part constant equivalent to the given arguments
     */
	@JvmStatic
	fun truncatedPartFor(nonNegativeRemainder: Long, nonNegativeDivisor: Long): TruncatedPart {
        if (nonNegativeRemainder == 0L) {
            return TruncatedPart.ZERO
        }
        val halfNonNegativeDivisor = nonNegativeDivisor ushr 1

        //NOTE: halfNonNegativeDivisor cannot be zero, because if it was 1 then nonNegativeRemainder was 0
        if (halfNonNegativeDivisor < nonNegativeRemainder) {
            return TruncatedPart.GREATER_THAN_HALF
        }
        if (((nonNegativeDivisor and 0x1L) == 0L) and (halfNonNegativeDivisor == nonNegativeRemainder)) {
            return TruncatedPart.EQUAL_TO_HALF
        }
        return TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO
    }

    /**
     * Returns a truncated part constant given a non-negative remainder
     * resulting from a division by 10^19.
     *
     * @param remainder
     * the remainder part
     * @return the truncated part constant equivalent to the given arguments
     */
    fun truncatedPartForScale19(remainder: Long): TruncatedPart {
        if (remainder == 0L) {
            return TruncatedPart.ZERO
        }
        if ((5000000000000000000L > remainder) and (remainder > -5000000000000000000L)) {
            return TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO
        }
        if ((remainder == 5000000000000000000L) or (remainder == -5000000000000000000L)) {
            return TruncatedPart.EQUAL_TO_HALF
        }
        return TruncatedPart.GREATER_THAN_HALF
    }

    /**
     * Returns a truncated part constant given a non-negative remainder
     * resulting from a division by 2^n
     *
     * @param remainder
     * the remainder part
     * @param n
     * the power of 2 of the divisor, `n > 0`
     * @return the truncated part constant equivalent to the given arguments
     */
	@JvmStatic
	fun truncatedPartFor2powN(remainder: Long, n: Int): TruncatedPart {
        return if (n < 63) {
            truncatedPartFor(remainder, 1L shl n)
        } else if (n == 63) {
            truncatedPartFor2pow63(remainder)
        } else if (n == 64) {
            truncatedPartFor2pow64(remainder)
        } else {
            if (remainder == 0L) TruncatedPart.ZERO else TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO
        }
    }

    /**
     * Returns a truncated part constant given a non-negative 128 bit remainder
     * resulting from a division by 2^n
     *
     * @param hRemainder
     * the high bits of the remainder part
     * @param lRemainder
     * the low bits of the remainder part
     * @param n
     * the power of 2 of the divisor, `n > 0`
     * @return the truncated part constant equivalent to the given arguments
     */
	@JvmStatic
	fun truncatedPartFor2powN(hRemainder: Long, lRemainder: Long, n: Int): TruncatedPart {
        if (hRemainder == 0L) {
            return truncatedPartFor2powN(lRemainder, n)
        }
        val hPart = truncatedPartFor2powN(hRemainder, n - java.lang.Long.SIZE)
        return when (hPart) {
            TruncatedPart.ZERO -> if (lRemainder == 0L) TruncatedPart.ZERO else TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO
            TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO -> hPart
            TruncatedPart.EQUAL_TO_HALF -> if (lRemainder == 0L) TruncatedPart.EQUAL_TO_HALF else TruncatedPart.GREATER_THAN_HALF
            TruncatedPart.GREATER_THAN_HALF -> hPart
            else -> throw RuntimeException("internal error: unsupported truncated part: $hPart")
        }
    }

    /**
     * Returns a truncated part constant given a non-negative remainder
     * resulting from a division by 2^63
     *
     * @param remainder
     * the remainder part
     * @return the truncated part constant equivalent to the given arguments
     */
	@JvmStatic
	fun truncatedPartFor2pow63(remainder: Long): TruncatedPart {
        if (remainder == 0L) {
            return TruncatedPart.ZERO
        }
        if (((1L shl 62) > remainder) and (remainder > -(1L shl 62))) {
            return TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO
        }
        if ((remainder == (1L shl 62)) or (remainder == -(1L shl 62))) {
            return TruncatedPart.EQUAL_TO_HALF
        }
        return TruncatedPart.GREATER_THAN_HALF
    }

    /**
     * Returns a truncated part constant given a non-negative remainder
     * resulting from a division by 2^64
     *
     * @param remainder
     * the remainder part
     * @return the truncated part constant equivalent to the given arguments
     */
	@JvmStatic
	fun truncatedPartFor2pow64(remainder: Long): TruncatedPart {
        if (remainder == 0L) {
            return TruncatedPart.ZERO
        }
        if (remainder.toULong() == 0x8000000000000000UL) {
            return TruncatedPart.EQUAL_TO_HALF
        }
        if (remainder < 0) {
            return TruncatedPart.GREATER_THAN_HALF
        }
        return TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO
    }
}
