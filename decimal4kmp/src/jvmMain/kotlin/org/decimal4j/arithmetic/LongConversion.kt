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

import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.DecimalRounding

/**
 * Contains methods to convert from and to long.
 */
internal object LongConversion {
    /**
     * Converts the specified long value to an unscaled value of the scale
     * defined by the given `scaleMetrics`. Performs no overflow checks.
     *
     * @param scaleMetrics
     * the scale metrics defining the result scale
     * @param value
     * the long value to convert
     * @return the value converted to the scale defined by `scaleMetrics`
     */
	@JvmStatic
	fun longToUnscaledUnchecked(scaleMetrics: ScaleMetrics, value: Long): Long {
        return scaleMetrics.multiplyByScaleFactor(value)
    }

    /**
     * Converts the specified long value to an unscaled value of the scale
     * defined by the given `scaleMetrics`. An exception is thrown if an
     * overflow occurs.
     *
     * @param scaleMetrics
     * the scale metrics defining the result scale
     * @param value
     * the long value to convert
     * @return the value converted to the scale defined by `scaleMetrics`
     * @throws IllegalArgumentException
     * if `value` is too large to be represented as a Decimal
     * with the scale of this factory
     */
	@JvmStatic
	fun longToUnscaled(scaleMetrics: ScaleMetrics, value: Long): Long {
        if (scaleMetrics.isValidIntegerValue(value)) {
            return scaleMetrics.multiplyByScaleFactor(value)
        }
        throw IllegalArgumentException(
            "Overflow: cannot convert " + value + " to Decimal with scale " + scaleMetrics.getScale()
        )
    }

    /**
     * Converts the specified unscaled value to a long truncating the result if
     * necessary.
     *
     * @param scaleMetrics
     * the scale metrics associated with `uDecimal`
     * @param uDecimal
     * the unscaled decimal value to convert
     * @return `round<sub>DOWN</sub>(uDecimal)`
     */
    fun unscaledToLong(scaleMetrics: ScaleMetrics, uDecimal: Long): Long {
        return scaleMetrics.divideByScaleFactor(uDecimal)
    }

    /**
     * Converts the specified unscaled value to a long rounding the result if
     * necessary.
     *
     * @param scaleMetrics
     * the scale metrics associated with `uDecimal`
     * @param rounding
     * the rounding to apply during the conversion if necessary
     * @param uDecimal
     * the unscaled decimal value to convert
     * @return `round(uDecimal)`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is
     * necessary
     */
	@JvmStatic
	fun unscaledToLong(scaleMetrics: ScaleMetrics, rounding: DecimalRounding, uDecimal: Long): Long {
        val truncated = scaleMetrics.divideByScaleFactor(uDecimal)
        val remainder = uDecimal - scaleMetrics.multiplyByScaleFactor(truncated)
        return (truncated
                + Rounding.calculateRoundingIncrement(rounding, truncated, remainder, scaleMetrics.getScaleFactor()))
    }
}
