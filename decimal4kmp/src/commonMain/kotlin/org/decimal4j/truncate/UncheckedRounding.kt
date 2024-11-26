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
 * Provides rounding constants implementing [TruncationPolicy] for [OverflowMode.UNCHECKED]. The constants
 * are equivalent to the constants defined by [RoundingMode]; the policy's [.getOverflowMode] method
 * always returns [UNCHECKED][OverflowMode.UNCHECKED] overflow mode.
 */
enum class UncheckedRounding : TruncationPolicy {
    /**
     * Unchecked truncation policy with rounding mode to round away from zero. Always increments the digit prior to a
     * non-zero discarded fraction. Note that this rounding mode never decreases the magnitude of the calculated value.
     *
     * @see RoundingMode.UP
     */
    UP {
        override fun getRoundingMode(): RoundingMode {
            return RoundingMode.UP
        }

        override fun toCheckedRounding(): CheckedRounding {
            return CheckedRounding.UP
        }
    },

    /**
     * Unchecked truncation policy with rounding mode to round towards zero. Never increments the digit prior to a
     * discarded fraction (i.e., truncates). Note that this rounding mode never increases the magnitude of the
     * calculated value.
     *
     * @see RoundingMode.DOWN
     */
    DOWN {
        override fun getRoundingMode(): RoundingMode {
            return RoundingMode.DOWN
        }

        override fun toCheckedRounding(): CheckedRounding {
            return CheckedRounding.DOWN
        }
    },

    /**
     * Unchecked truncation policy with rounding mode to round towards positive infinity. If the result is positive,
     * behaves as for `RoundingMode.UP`; if negative, behaves as for `RoundingMode.DOWN`. Note that this
     * rounding mode never decreases the calculated value.
     *
     * @see RoundingMode.CEILING
     */
    CEILING {
        override fun getRoundingMode(): RoundingMode {
            return RoundingMode.CEILING
        }

        override fun toCheckedRounding(): CheckedRounding {
            return CheckedRounding.CEILING
        }
    },

    /**
     * Unchecked truncation policy with rounding mode to round towards negative infinity. If the result is positive,
     * behave as for `RoundingMode.DOWN`; if negative, behave as for `RoundingMode.UP`. Note that this
     * rounding mode never increases the calculated value.
     *
     * @see RoundingMode.FLOOR
     */
    FLOOR {
        override fun getRoundingMode(): RoundingMode {
            return RoundingMode.FLOOR
        }

        override fun toCheckedRounding(): CheckedRounding {
            return CheckedRounding.FLOOR
        }
    },

    /**
     * Unchecked truncation policy with rounding mode to round towards &quot;nearest neighbor&quot; unless both
     * neighbors are equidistant, in which case round up. Behaves as for `RoundingMode.UP` if the discarded
     * fraction is  0.5; otherwise, behaves as for `RoundingMode.DOWN`. Note that this is the rounding mode
     * commonly taught at school.
     *
     * @see RoundingMode.HALF_UP
     */
    HALF_UP {
        override fun getRoundingMode(): RoundingMode {
            return RoundingMode.HALF_UP
        }

        override fun toCheckedRounding(): CheckedRounding {
            return CheckedRounding.HALF_UP
        }
    },

    /**
     * Unchecked truncation policy with rounding mode to round towards &quot;nearest neighbor&quot; unless both
     * neighbors are equidistant, in which case round down. Behaves as for `RoundingMode.UP` if the discarded
     * fraction is &gt; 0.5; otherwise, behaves as for `RoundingMode.DOWN`.
     *
     * @see RoundingMode.HALF_DOWN
     */
    HALF_DOWN {
        override fun getRoundingMode(): RoundingMode {
            return RoundingMode.HALF_DOWN
        }

        override fun toCheckedRounding(): CheckedRounding {
            return CheckedRounding.HALF_DOWN
        }
    },

    /**
     * Unchecked truncation policy with rounding mode to round towards the &quot;nearest neighbor&quot; unless both
     * neighbors are equidistant, in which case, round towards the even neighbor. Behaves as for
     * `RoundingMode.HALF_UP` if the digit to the left of the discarded fraction is odd; behaves as for
     * `RoundingMode.HALF_DOWN` if it's even. Note that this is the rounding mode that statistically minimizes
     * cumulative error when applied repeatedly over a sequence of calculations. It is sometimes known as
     * &quot;Banker&#39;s rounding,&quot; and is chiefly used in the USA. This rounding mode is analogous to the rounding
     * policy used for `float` and `double` arithmetic in Java.
     *
     * @see RoundingMode.HALF_EVEN
     */
    HALF_EVEN {
        override fun getRoundingMode(): RoundingMode {
            return RoundingMode.HALF_EVEN
        }

        override fun toCheckedRounding(): CheckedRounding {
            return CheckedRounding.HALF_EVEN
        }
    },

    /**
     * Unchecked truncation policy with rounding mode to assert that the requested operation has an exact result, hence
     * no rounding is necessary. If this rounding mode is specified on an operation that yields an inexact result, an
     * `ArithmeticException` is thrown.
     *
     * @see RoundingMode.UNNECESSARY
     */
    UNNECESSARY {
        override fun getRoundingMode(): RoundingMode {
            return RoundingMode.UNNECESSARY
        }

        override fun toCheckedRounding(): CheckedRounding {
            return CheckedRounding.UNNECESSARY
        }
    };

    /**
     * Returns [OverflowMode.UNCHECKED].
     *
     * @return UNCHECKED overflow mode
     */
    override fun getOverflowMode(): OverflowMode {
        return OverflowMode.UNCHECKED
    }

    /**
     * Returns the policy with the same [rounding mode][.getRoundingMode] as this unchecked rounding policy but
     * for [CHECKED][OverflowMode.CHECKED] [overflow mode][.getOverflowMode].
     *
     * @return the [CheckedRounding] counterpart to this policy.
     */
    abstract fun toCheckedRounding(): CheckedRounding

    /**
     * Returns "UNCHECKED/(name)" where `(name)` stands for the [.name] of this constant.
     *
     * @return a string like "UNCHECKED/HALF_UP"
     */
    override fun toString(): String {
        return "UNCHECKED/$name"
    }

    private object ByRoundingMode {
        val VALUES_BY_ROUNDING_MODE_ORDINAL: Array<UncheckedRounding?> = sortByRoundingModeOrdinal()

        fun sortByRoundingModeOrdinal(): Array<UncheckedRounding?> {
            val sorted = arrayOfNulls<UncheckedRounding>(VALUES.size)
            for (dr in VALUES) {
                sorted[dr.getRoundingMode().ordinal] = dr
            }
            return sorted
        }
    }

    companion object {
        /**
         * Immutable set with all values of this enum. Avoids object creation in contrast to [.values].
         */
		val VALUES: Set<UncheckedRounding> = entries.toSet()

        /**
         * Returns the checked rounding constant for the given rounding mode.
         *
         * @param roundingMode
         * the rounding mode
         * @return the constant corresponding to the given rounding mode
         */
        fun valueOf(roundingMode: RoundingMode): UncheckedRounding {
            return ByRoundingMode.VALUES_BY_ROUNDING_MODE_ORDINAL[roundingMode.ordinal]!!
        }
    }
}
