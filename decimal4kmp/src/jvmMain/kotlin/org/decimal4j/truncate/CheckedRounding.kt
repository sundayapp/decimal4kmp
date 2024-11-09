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

import org.decimal4j.api.RoundingMode

/**
 * Provides rounding constants implementing [TruncationPolicy] for
 * [OverflowMode.CHECKED]. The constants are equivalent to the constants
 * defined by [RoundingMode]; the policy's [.getOverflowMode]
 * method always returns [CHECKED][OverflowMode.CHECKED] overflow mode.
 */
enum class CheckedRounding : org.decimal4j.truncate.TruncationPolicy {
    /**
     * Checked truncation policy with rounding mode to round away from zero.
     * Always increments the digit prior to a non-zero discarded fraction. Note
     * that this rounding mode never decreases the magnitude of the calculated
     * value.
     *
     * @see RoundingMode.UP
     */
    UP {
        override fun getRoundingMode(): RoundingMode {
            return RoundingMode.UP
        }

        override fun toUncheckedRounding(): UncheckedRounding {
            return UncheckedRounding.UP
        }
    },

    /**
     * Checked truncation policy with rounding mode to round towards zero. Never
     * increments the digit prior to a discarded fraction (i.e., truncates).
     * Note that this rounding mode never increases the magnitude of the
     * calculated value.
     *
     * @see RoundingMode.DOWN
     */
    DOWN {
        override fun getRoundingMode(): RoundingMode {
            return RoundingMode.DOWN
        }

        override fun toUncheckedRounding(): UncheckedRounding {
            return UncheckedRounding.DOWN
        }
    },

    /**
     * Checked truncation policy with rounding mode to round towards positive
     * infinity. If the result is positive, behaves as for
     * `RoundingMode.UP`; if negative, behaves as for
     * `RoundingMode.DOWN`. Note that this rounding mode never decreases
     * the calculated value.
     *
     * @see RoundingMode.CEILING
     */
    CEILING {
        override fun getRoundingMode(): RoundingMode {
            return RoundingMode.CEILING
        }

        override fun toUncheckedRounding(): UncheckedRounding {
            return UncheckedRounding.CEILING
        }
    },

    /**
     * Checked truncation policy with rounding mode to round towards negative
     * infinity. If the result is positive, behave as for
     * `RoundingMode.DOWN`; if negative, behave as for
     * `RoundingMode.UP`. Note that this rounding mode never increases the
     * calculated value.
     *
     * @see RoundingMode.FLOOR
     */
    FLOOR {
        override fun getRoundingMode(): RoundingMode {
            return RoundingMode.FLOOR
        }

        override fun toUncheckedRounding(): UncheckedRounding {
            return UncheckedRounding.FLOOR
        }
    },

    /**
     * Checked truncation policy with rounding mode to round towards
     * &quot;nearest neighbor&quot; unless both neighbors are equidistant, in
     * which case round up. Behaves as for `RoundingMode.UP` if the
     * discarded fraction is  0.5; otherwise, behaves as for
     * `RoundingMode.DOWN`. Note that this is the rounding mode commonly
     * taught at school.
     *
     * @see RoundingMode.HALF_UP
     */
    HALF_UP {
        override fun getRoundingMode(): RoundingMode {
            return RoundingMode.HALF_UP
        }

        override fun toUncheckedRounding(): UncheckedRounding {
            return UncheckedRounding.HALF_UP
        }
    },

    /**
     * Checked truncation policy with rounding mode to round towards
     * &quot;nearest neighbor&quot; unless both neighbors are equidistant, in
     * which case round down. Behaves as for `RoundingMode.UP` if the
     * discarded fraction is &gt; 0.5; otherwise, behaves as for
     * `RoundingMode.DOWN`.
     *
     * @see RoundingMode.HALF_DOWN
     */
    HALF_DOWN {
        override fun getRoundingMode(): RoundingMode {
            return RoundingMode.HALF_DOWN
        }

        override fun toUncheckedRounding(): UncheckedRounding {
            return UncheckedRounding.HALF_DOWN
        }
    },

    /**
     * Checked truncation policy with rounding mode to round towards the
     * &quot;nearest neighbor&quot; unless both neighbors are equidistant, in
     * which case, round towards the even neighbor. Behaves as for
     * `RoundingMode.HALF_UP` if the digit to the left of the discarded
     * fraction is odd; behaves as for `RoundingMode.HALF_DOWN` if it's
     * even. Note that this is the rounding mode that statistically minimizes
     * cumulative error when applied repeatedly over a sequence of calculations.
     * It is sometimes known as &quot;Banker&#39;s rounding,&quot; and is chiefly
     * used in the USA. This rounding mode is analogous to the rounding policy
     * used for `float` and `double` arithmetic in Java.
     *
     * @see RoundingMode.HALF_EVEN
     */
    HALF_EVEN {
        override fun getRoundingMode(): RoundingMode {
            return RoundingMode.HALF_EVEN
        }

        override fun toUncheckedRounding(): UncheckedRounding {
            return UncheckedRounding.HALF_EVEN
        }
    },

    /**
     * Checked truncation policy with rounding mode to assert that the requested
     * operation has an exact result, hence no rounding is necessary. If this
     * rounding mode is specified on an operation that yields an inexact result,
     * an `ArithmeticException` is thrown.
     *
     * @see RoundingMode.UNNECESSARY
     */
    UNNECESSARY {
        override fun getRoundingMode(): RoundingMode {
            return RoundingMode.UNNECESSARY
        }

        override fun toUncheckedRounding(): UncheckedRounding {
            return UncheckedRounding.UNNECESSARY
        }
    };

    /**
     * Returns [OverflowMode.CHECKED].
     *
     * @return CHECKED overflow mode
     */
    override fun getOverflowMode(): OverflowMode {
        return OverflowMode.CHECKED
    }

    /**
     * Returns the policy with the same [rounding mode][.getRoundingMode]
     * as this checked rounding policy but for [ UNCHECKED][OverflowMode.UNCHECKED] [overflow mode][.getOverflowMode].
     *
     * @return the [UncheckedRounding] counterpart to this policy.
     */
    abstract fun toUncheckedRounding(): UncheckedRounding

    /**
     * Returns "CHECKED/(name)" where `(name)` stands for the [.name] of this constant.
     *
     * @return a string like "CHECKED/HALF_UP"
     */
    override fun toString(): String {
        return "CHECKED/$name"
    }

    private object ByRoundingMode {
        val VALUES_BY_ROUNDING_MODE_ORDINAL: Array<CheckedRounding?> = sortByRoundingModeOrdinal()

        fun sortByRoundingModeOrdinal(): Array<CheckedRounding?> {
            val sorted = arrayOfNulls<CheckedRounding>(VALUES.size)
            for (dr in VALUES) {
                sorted[dr.getRoundingMode().ordinal] = dr
            }
            return sorted
        }
    }

    companion object {
        /**
         * Immutable set with all values of this enum. Avoids object creation in
         * contrast to [.values].
         */
        @JvmField
        val VALUES: Set<CheckedRounding> = entries.toSet()

        /**
         * Returns the checked rounding constant for the given rounding mode.
         *
         * @param roundingMode
         * the rounding mode
         * @return the constant corresponding to the given rounding mode
         */
        fun valueOf(roundingMode: RoundingMode): CheckedRounding {
            return ByRoundingMode.VALUES_BY_ROUNDING_MODE_ORDINAL[roundingMode.ordinal]!!
        }
    }
}
