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

/**
 * Defines different inverses of a [DecimalRounding]. Each constant
 * provides its own implementation of an [.invert]
 * method.
 */
internal enum class RoundingInverse {

    /**
     * Constant calculating inverted rounding due to sign reversion.
     *
     *
     * The inverted rounding mode can be used to round a value `a` instead
     * of the value `-a`.
     */
    SIGN_REVERSION {
        override fun invert(rounding: DecimalRounding): DecimalRounding {
            return when (rounding) {
                DecimalRounding.FLOOR -> DecimalRounding.CEILING
                DecimalRounding.CEILING -> DecimalRounding.FLOOR
                DecimalRounding.DOWN -> DecimalRounding.DOWN
                DecimalRounding.HALF_DOWN -> DecimalRounding.HALF_DOWN
                DecimalRounding.UP -> DecimalRounding.UP
                DecimalRounding.HALF_UP -> DecimalRounding.HALF_UP
                DecimalRounding.HALF_EVEN -> DecimalRounding.HALF_EVEN
                DecimalRounding.UNNECESSARY -> DecimalRounding.UNNECESSARY
                else ->                // should not get here
                    throw IllegalArgumentException("Unsupported rounding mode: $rounding")
            }
        }
    },

    /**
     * Constant calculating inverted rounding due to sign reversion occurring
     * after an addition or subtraction.
     *
     *
     * The inverted rounding mode can be used to round a value `x` instead
     * of the sum `(a + x)` when sum and `x` have opposite sign
     * (equivalent for a difference `(a - x)`).
     */
    ADDITIVE_REVERSION {
        override fun invert(rounding: DecimalRounding): DecimalRounding {
            return when (rounding) {
                DecimalRounding.FLOOR -> DecimalRounding.FLOOR
                DecimalRounding.CEILING -> DecimalRounding.CEILING
                DecimalRounding.DOWN -> DecimalRounding.UP
                DecimalRounding.HALF_DOWN -> DecimalRounding.HALF_UP
                DecimalRounding.UP -> DecimalRounding.DOWN
                DecimalRounding.HALF_UP -> DecimalRounding.HALF_DOWN
                DecimalRounding.HALF_EVEN -> DecimalRounding.HALF_EVEN
                DecimalRounding.UNNECESSARY -> DecimalRounding.UNNECESSARY
                else ->                // should not get here
                    throw IllegalArgumentException("Unsupported rounding mode: $rounding")
            }
        }
    },

    /**
     * Constant calculating inverted rounding due to reciprocal of a value.
     *
     *
     * The inverted rounding mode can be used to round a value `x` instead
     * of the reciprocal value `1/x`.
     */
    RECIPROCAL {
        override fun invert(rounding: DecimalRounding): DecimalRounding {
            return when (rounding) {
                DecimalRounding.UP -> DecimalRounding.DOWN
                DecimalRounding.DOWN -> DecimalRounding.UP
                DecimalRounding.CEILING -> DecimalRounding.FLOOR
                DecimalRounding.FLOOR -> DecimalRounding.CEILING
                DecimalRounding.HALF_UP -> DecimalRounding.HALF_DOWN
                DecimalRounding.HALF_DOWN -> DecimalRounding.HALF_UP
                DecimalRounding.HALF_EVEN -> DecimalRounding.HALF_EVEN // HALF_UNEVEN?
                DecimalRounding.UNNECESSARY -> DecimalRounding.UNNECESSARY
                else ->                // should not get here
                    throw IllegalArgumentException("Unsupported rounding mode: $rounding")
            }
        }
    };

    /**
     * Returns the inverted rounding for the inversion case defined by this
     * constant.
     *
     * @param rounding
     * the original rounding
     * @return the inverted rounding
     */
    abstract fun invert(rounding: DecimalRounding): DecimalRounding
}
