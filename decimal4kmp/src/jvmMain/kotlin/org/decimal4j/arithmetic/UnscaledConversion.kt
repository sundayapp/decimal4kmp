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
import org.decimal4j.arithmetic.Checked.isSubtractOverflow
import org.decimal4j.arithmetic.Exceptions.rethrowIfRoundingNecessary
import org.decimal4j.arithmetic.Pow10.divideByPowerOf10Checked
import org.decimal4j.arithmetic.Pow10.multiplyByPowerOf10Checked
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.DecimalRounding

/**
 * Contains static methods to convert between different scales.
 */
internal object UnscaledConversion {
    private fun getScaleDiff(scaleMetrics: ScaleMetrics, scale: Int): Int {
        return getScaleDiff(scaleMetrics.getScale(), scale)
    }

    private fun getScaleDiff(targetScale: Int, sourceScale: Int): Int {
        val diffScale = targetScale - sourceScale
        if (!isSubtractOverflow(targetScale.toLong(), sourceScale.toLong(), diffScale.toLong())) {
            return diffScale
        }
        throw IllegalArgumentException(
            ("Cannot convert from scale " + sourceScale + " to " + targetScale
                    + " (scale difference is out of integer range)")
        )
    }

    /**
     * Converts the given `unscaledValue` with the specified `scale`
     * to a long value. The value is rounded DOWN if necessary. An exception is
     * thrown if the conversion is not possible.
     *
     * @param arith
     * arithmetic of the target value
     * @param unscaledValue
     * the unscaled value to convert
     * @param scale
     * the scale of `unscaledValue`
     * @return a long value rounded down if necessary
     * @throws IllegalArgumentException
     * if the conversion cannot be performed due to overflow
     */
    fun unscaledToLong(arith: DecimalArithmetic, unscaledValue: Long, scale: Int): Long {
        try {
            return divideByPowerOf10Checked(arith, unscaledValue, scale)
        } catch (e: ArithmeticException) {
            throw toIllegalArgumentExceptionOrRethrow(e, unscaledValue, scale, arith.scale)
        }
    }

    /**
     * Converts the given `unscaledValue` with the specified `scale`
     * to a long value. The value is rounded using the specified
     * `rounding` if necessary. An exception is thrown if the conversion
     * is not possible.
     *
     * @param arith
     * arithmetic of the target value
     * @param rounding
     * the rounding to apply if rounding is necessary
     * @param unscaledValue
     * the unscaled value to convert
     * @param scale
     * the scale of `unscaledValue`
     * @return long value rounded with given rounding if necessary
     * @throws IllegalArgumentException
     * if the conversion cannot be performed due to overflow
     * @throws ArithmeticException
     * if rounding is necessary and `rounding==UNNECESSARY`
     */
    fun unscaledToLong(arith: DecimalArithmetic, rounding: DecimalRounding, unscaledValue: Long, scale: Int): Long {
        try {
            return divideByPowerOf10Checked(arith, rounding, unscaledValue, scale)
        } catch (e: ArithmeticException) {
            throw toIllegalArgumentExceptionOrRethrow(e, unscaledValue, scale, arith.scale)
        }
    }

    /**
     * Returns an unscaled value of the scale defined by `arith` given an
     * `unscaledValue` with its `scale`. The value is rounded DOWN
     * if necessary. An exception is thrown if the conversion is not possible.
     *
     * @param arith
     * arithmetic defining the target scale
     * @param unscaledValue
     * the unscaled value to convert
     * @param scale
     * the scale of `unscaledValue`
     * @return the unscaled value in the arithmetic's scale
     * @throws IllegalArgumentException
     * if the conversion cannot be performed due to overflow
     */
    fun unscaledToUnscaled(arith: DecimalArithmetic, unscaledValue: Long, scale: Int): Long {
        val scaleDiff = getScaleDiff(arith.scaleMetrics, scale)
        try {
            return multiplyByPowerOf10Checked(arith, unscaledValue, scaleDiff)
        } catch (e: ArithmeticException) {
            throw toIllegalArgumentExceptionOrRethrow(e, unscaledValue, scale, arith.scale)
        }
    }

    /**
     * Returns an unscaled value of the scale defined by `arith` given an
     * `unscaledValue` with its `scale`. The value is rounded using
     * the specified `rounding` if necessary. An exception is thrown if
     * the conversion is not possible.
     *
     * @param arith
     * arithmetic defining the target scale
     * @param rounding
     * the rounding to apply if rounding is necessary
     * @param unscaledValue
     * the unscaled value to convert
     * @param scale
     * the scale of `unscaledValue`
     * @return the unscaled value in the arithmetic's scale
     * @throws IllegalArgumentException
     * if the conversion cannot be performed due to overflow
     * @throws ArithmeticException
     * if rounding is necessary and `rounding==UNNECESSARY`
     */
    fun unscaledToUnscaled(arith: DecimalArithmetic, rounding: DecimalRounding, unscaledValue: Long, scale: Int): Long {
        val scaleDiff = getScaleDiff(arith.scaleMetrics, scale)
        try {
            return multiplyByPowerOf10Checked(arith, rounding, unscaledValue, scaleDiff)
        } catch (e: ArithmeticException) {
            throw toIllegalArgumentExceptionOrRethrow(e, unscaledValue, scale, arith.scale)
        }
    }

    /**
     * Converts an unscaled value `uDecimal` having the scale specified by
     * `arith` into another unscaled value of the provided
     * `targetScale`. The value is rounded DOWN if necessary. An exception
     * is thrown if the conversion is not possible.
     *
     * @param targetScale
     * the scale of the result value
     * @param arith
     * arithmetic defining the source scale
     * @param uDecimal
     * the unscaled value to convert
     * @return the unscaled value with `targetScale`
     * @throws IllegalArgumentException
     * if the conversion cannot be performed due to overflow
     */
    fun unscaledToUnscaled(targetScale: Int, arith: DecimalArithmetic, uDecimal: Long): Long {
        val scaleDiff = getScaleDiff(targetScale, arith.scale)
        try {
            return multiplyByPowerOf10Checked(arith, uDecimal, scaleDiff)
        } catch (e: ArithmeticException) {
            throw toIllegalArgumentExceptionOrRethrow(e, uDecimal, arith.scale, targetScale)
        }
    }

    /**
     * Converts an unscaled value `uDecimal` having the scale specified by
     * `arith` into another unscaled value of the provided
     * `targetScale`. The value is rounded using the specified
     * `rounding` if necessary. An exception is thrown if the conversion
     * is not possible.
     *
     *
     * @param rounding
     * the rounding to apply if rounding is necessary
     * @param targetScale
     * the scale of the result value
     * @param arith
     * arithmetic defining the source scale
     * @param uDecimal
     * the unscaled value to convert
     * @return the unscaled value with `targetScale`
     * @throws IllegalArgumentException
     * if the conversion cannot be performed due to overflow
     */
    fun unscaledToUnscaled(
        rounding: DecimalRounding,
        targetScale: Int,
        arith: DecimalArithmetic,
        uDecimal: Long
    ): Long {
        val scaleDiff = getScaleDiff(targetScale, arith.scale)
        try {
            return multiplyByPowerOf10Checked(arith, rounding, uDecimal, scaleDiff)
        } catch (e: ArithmeticException) {
            throw toIllegalArgumentExceptionOrRethrow(e, uDecimal, arith.scale, targetScale)
        }
    }

    private fun toIllegalArgumentExceptionOrRethrow(
        e: ArithmeticException,
        unscaledValue: Long,
        sourceScale: Int,
        targetScale: Int
    ): IllegalArgumentException {
        rethrowIfRoundingNecessary(e)
        return if (targetScale > 0) {
            IllegalArgumentException(
                ("Overflow: Cannot convert unscaled value " + unscaledValue
                        + " from scale " + sourceScale + " to scale " + targetScale), e
            )
        } else {
            IllegalArgumentException(
                ("Overflow: Cannot convert unscaled value " + unscaledValue
                        + " from scale " + sourceScale + " to long"), e
            )
        }
    }
}
