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
 * Represents the truncated part for instance after division. It is passed to
 * the rounding methods in [DecimalRounding]
 */
enum class TruncatedPart {
    /**
     * Truncated part `t == 0`.
     */
    ZERO {
        override fun isGreaterThanZero(): Boolean {
            return false
        }

        override fun isEqualToHalf(): Boolean {
            return false
        }

        override fun isGreaterEqualHalf(): Boolean {
            return false
        }

        override fun isGreaterThanHalf(): Boolean {
            return false
        }
    },

    /**
     * Truncated part `0 < t < 0.5`.
     */
    LESS_THAN_HALF_BUT_NOT_ZERO {
        override fun isGreaterThanZero(): Boolean {
            return true
        }

        override fun isEqualToHalf(): Boolean {
            return false
        }

        override fun isGreaterEqualHalf(): Boolean {
            return false
        }

        override fun isGreaterThanHalf(): Boolean {
            return false
        }
    },

    /**
     * Truncated part `t == 0.5`.
     */
    EQUAL_TO_HALF {
        override fun isGreaterThanZero(): Boolean {
            return true
        }

        override fun isEqualToHalf(): Boolean {
            return true
        }

        override fun isGreaterEqualHalf(): Boolean {
            return true
        }

        override fun isGreaterThanHalf(): Boolean {
            return false
        }
    },

    /**
     * Truncated part `t > 0.5`.
     */
    GREATER_THAN_HALF {
        override fun isGreaterThanZero(): Boolean {
            return true
        }

        override fun isEqualToHalf(): Boolean {
            return false
        }

        override fun isGreaterEqualHalf(): Boolean {
            return true
        }

        override fun isGreaterThanHalf(): Boolean {
            return true
        }
    };

    /**
     * Returns true if the truncated part is greater than zero.
     *
     * @return true if `this > 0`
     */
    abstract fun isGreaterThanZero(): Boolean

    /**
     * Returns true if the truncated part is equal to one half.
     *
     * @return true if `this == 0.5`
     */
    abstract fun isEqualToHalf(): Boolean

    /**
     * Returns true if the truncated part is greater than or equal to one half.
     *
     * @return true if `this >= 0.5`
     */
    abstract fun isGreaterEqualHalf(): Boolean

    /**
     * Returns true if the truncated part is greater than one half.
     *
     * @return true if `this > 0.5`
     */
    abstract fun isGreaterThanHalf(): Boolean

    companion object {
        /**
         * Returns a truncated part constant given the first truncated digit and a
         * boolean indicating whether there is non-zero digits after that.
         *
         * @param firstTruncatedDigit
         * the first truncated digit, must be in `[0, 1, ..., 9]`
         * @param zeroAfterFirstTruncatedDigit
         * true if all truncated digits after the first truncated digit
         * are zero, and false otherwise
         * @return the truncated part constant equivalent to the given arguments
         */
        fun valueOf(firstTruncatedDigit: Int, zeroAfterFirstTruncatedDigit: Boolean): TruncatedPart {
            if (firstTruncatedDigit > 5) {
                return GREATER_THAN_HALF
            }
            if (zeroAfterFirstTruncatedDigit) {
                if (firstTruncatedDigit == 5) {
                    return EQUAL_TO_HALF
                }
                if (firstTruncatedDigit > 0) {
                    return LESS_THAN_HALF_BUT_NOT_ZERO
                }
                return ZERO
            }
            if (firstTruncatedDigit < 5) {
                return LESS_THAN_HALF_BUT_NOT_ZERO
            }
            return GREATER_THAN_HALF
        }
    }
}