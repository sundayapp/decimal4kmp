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
package org.decimal4j.truncate

/**
 * Policy defining how to handle truncation due to overflow or rounding. A
 * `TruncationPolicy` is uniquely defined by the two elements
 * [overflow mode][.getOverflowMode] and [rounding][.getRoundingMode].
 *
 *
 * Truncation policies are are defined by [UncheckedRounding] and [CheckedRounding].
 * Some special truncation policies are also defined by
 *
 *  * [.DEFAULT]
 *  * [.VALUES]
 *  * [UncheckedRounding.VALUES]
 *  * [CheckedRounding.VALUES]
 *
 */
interface TruncationPolicy {
    /**
     * Returns the overflow mode which defines how to deal the situation when an
     * operation that causes an overflow.
     *
     * @return the mode to apply if an arithmetic operation causes an overflow
     */

    fun getOverflowMode(): OverflowMode

    /**
     * Returns the rounding mode which defines how to deal the situation when an
     * operation leads to truncation or rounding.
     *
     * @return the rounding mode indicating how the least significant returned
     * digit of a rounded result is to be calculated
     */

	fun getRoundingMode(): RoundingMode

    companion object {
        /**
         * Default truncation policy: [UncheckedRounding.HALF_UP].
         */
        val DEFAULT: TruncationPolicy =
            UncheckedRounding.HALF_UP

        /**
         * Unmodifiable set with all possible truncation policies.
         */
        val VALUES: Set<TruncationPolicy> = UncheckedRounding.VALUES + CheckedRounding.VALUES
    }
}
