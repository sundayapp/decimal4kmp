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
package org.decimal4j.util

import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales
import org.decimal4j.api.RoundingMode
import kotlin.math.abs

/**
 * DoubleRounder Utility **(Deprecated)**.
 *
 *
 * DoubleRounder sometimes returns counter-intuitive results. The reason is that it performs mathematically
 * correct rounding. For instance `DoubleRounder.round(256.025d, 2)` will be rounded down to
 * `256.02` because the double value represented as `256.025d` is somewhat smaller than the rational
 * value `256.025` and hence will be rounded down.
 *
 *
 * Notes:
 *
 *  * This behaviour is very similar to that of the [BigDecimal(double)][java.math.BigDecimal.BigDecimal]
 * constructor (but not to [valueOf(double)][java.math.BigDecimal.valueOf] which uses the string
 * constructor).
 *  * The problem can be circumvented with a double rounding step to a higher precision first, but it is complicated
 * and we are not going into the details here
 *
 * For those reasons we **cannot recommend to use DoubleRounder**.
 */
@Deprecated("")
class DoubleRounder(private val scaleMetrics: ScaleMetrics) {

    private val ulp = scaleMetrics.getRoundingHalfEvenArithmetic().toDouble(1)

    /**
     * Creates a rounder for the given decimal precision.
     *
     * @param precision
     * the decimal rounding precision, must be in `[0,18]`
     * @throws IllegalArgumentException
     * if precision is negative or larger than 18
     */
    constructor(precision: Int) : this(toScaleMetrics(precision))

    val precision: Int
        /**
         * Returns the precision of this rounder, a value between zero and 18.
         *
         * @return this rounder's decimal precision
         */
        get() = scaleMetrics.getScale()

    /**
     * Rounds the given double value to the decimal precision of this rounder using [HALF_UP][RoundingMode.HALF_UP]
     * rounding.
     *
     * @param value
     * the value to round
     * @return the rounded value
     * @see .getPrecision
     */
    fun round(value: Double): Double {
        return round(value, scaleMetrics.getDefaultArithmetic(), scaleMetrics.getRoundingHalfEvenArithmetic(), ulp)
    }

    /**
     * Rounds the given double value to the decimal precision of this rounder using the specified rounding mode.
     *
     * @param value
     * the value to round
     * @param roundingMode
     * the rounding mode indicating how the least significant returned decimal digit of the result is to be
     * calculated
     * @return the rounded value
     * @see .getPrecision
     */
    fun round(value: Double, roundingMode: RoundingMode): Double {
        return round(value, roundingMode, scaleMetrics.getRoundingHalfEvenArithmetic(), ulp)
    }

    /**
     * Returns a hash code for this `DoubleRounder` instance.
     *
     * @return a hash code value for this object.
     */
    override fun hashCode(): Int {
        return scaleMetrics.hashCode()
    }

    /**
     * Returns true if `obj` is a `DoubleRounder` with the same precision as `this` rounder instance.
     *
     * @param obj
     * the reference object with which to compare
     * @return true for a double rounder with the same precision as this instance
     */
    override fun equals(obj: Any?): Boolean {
        if (obj === this) return true
        if (obj == null) return false
        if (obj is DoubleRounder) {
            return scaleMetrics == obj.scaleMetrics
        }
        return false
    }

    /**
     * Returns a string consisting of the simple class name and the precision.
     *
     * @return a string like "DoubleRounder[precision=7]"
     */
    override fun toString(): String {
        return "DoubleRounder[precision=" + precision + "]"
    }

    companion object {
        /**
         * Rounds the given double value to the specified decimal `precision` using [ HALF_UP][RoundingMode.HALF_UP] rounding.
         *
         * @param value
         * the value to round
         * @param precision
         * the decimal precision to round to (aka decimal places)
         * @return the rounded value
         */
        fun round(value: Double, precision: Int): Double {
            val sm = toScaleMetrics(precision)
            val halfEvenArith = sm.getRoundingHalfEvenArithmetic()
            return round(value, sm.getDefaultArithmetic(), halfEvenArith, halfEvenArith.toDouble(1))
        }

        /**
         * Rounds the given double value to the specified decimal `precision` using the specified rounding mode.
         *
         * @param value
         * the value to round
         * @param precision
         * the decimal precision to round to (aka decimal places)
         * @param roundingMode
         * the rounding mode indicating how the least significant returned decimal digit of the result is to be
         * calculated
         * @return the rounded value
         */
        fun round(value: Double, precision: Int, roundingMode: RoundingMode): Double {
            val sm = toScaleMetrics(precision)
            val halfEvenArith = sm.getRoundingHalfEvenArithmetic()
            return round(value, roundingMode, halfEvenArith, halfEvenArith.toDouble(1))
        }

        private fun round(
            value: Double,
            roundingMode: RoundingMode,
            halfEvenArith: DecimalArithmetic,
            ulp: Double
        ): Double {
            if (roundingMode == RoundingMode.UNNECESSARY) {
                return checkRoundingUnnecessary(value, halfEvenArith, ulp)
            }
            return round(value, halfEvenArith.deriveArithmetic(roundingMode), halfEvenArith, ulp)
        }

        private fun round(
            value: Double,
            roundingArith: DecimalArithmetic,
            halfEvenArith: DecimalArithmetic,
            ulp: Double
        ): Double {
            //return the value unchanged if
            // (a) the value is infinite or NaN
            // (b) the next double is 2 decimal UPLs away (or more):
            //     in this case no other double value represents the decimal value more accurately
            if (!isFinite(value) || ulp * 2 <= Math.ulp(value)) {
                return value
            }
            // NOTE: condition (b) above prevents overflows as such cases do not get to here
            val uDecimal = roundingArith.fromDouble(value)
            return halfEvenArith.toDouble(uDecimal)
        }

        private fun checkRoundingUnnecessary(value: Double, halfEvenArith: DecimalArithmetic, ulp: Double): Double {
            //same condition as in round(..) method above
            if (isFinite(value) && 2 * ulp > Math.ulp(value)) {
                //By definition, rounding is necessary if there is another double value that represents our decimal more
                //accurately. This is the case when we get a different double value after two conversions.
                val uDecimal = halfEvenArith.fromDouble(value)
                if (halfEvenArith.toDouble(uDecimal) != value) {
                    throw ArithmeticException(
                        "Rounding necessary for precision " + halfEvenArith.scale + ": " + value
                    )
                }
            }
            return value
        }

        private fun toScaleMetrics(precision: Int): ScaleMetrics {
            require(!((precision < Scales.MIN_SCALE) or (precision > Scales.MAX_SCALE))) { "Precision must be in [" + Scales.MIN_SCALE + "," + Scales.MAX_SCALE + "] but was " + precision }
            return Scales.getScaleMetrics(precision)
        }

        /**
         * Java-7 port of `Double#isFinite(double)`.
         *
         *
         * Returns `true` if the argument is a finite floating-point value; returns `false` otherwise (for NaN
         * and infinity arguments).
         *
         * @param d
         * the `double` value to be tested
         * @return `true` if the argument is a finite floating-point value, `false` otherwise.
         */
        private fun isFinite(d: Double): Boolean {
            return abs(d) <= Double.MAX_VALUE
        }
    }
}
