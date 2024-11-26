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
import org.decimal4j.scale.ScaleMetricsExtensions.getScaleFactorAsBigDecimal
import org.decimal4j.scale.Scales
import java.math.BigDecimal
import org.decimal4j.truncate.RoundingMode

/**
 * Contains methods to convert from and to [BigDecimal].
 */
internal object BigDecimalConversion {
    /**
     * Converts the specified big decimal value to a long value applying the
     * given rounding mode. An exception is thrown if the value exceeds the
     * valid long range.
     *
     * @param roundingMode
     * the rounding mode to apply if necessary
     * @param value
     * the big decimal value to convert
     * @return `round(value)`
     * @throws IllegalArgumentException
     * if the value is outside of the valid long range
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is
     * necessary
     */
	
	fun bigDecimalToLong(roundingMode: RoundingMode?, value: BigDecimal): Long {
        // TODO any chance to make this garbage free?
        // Difficult as we cannot look inside the BigDecimal value
        val scaled = value //
            .setScale(0, roundingMode?.toJavaRoundingMode()) //
            .toBigInteger()
        if (scaled.bitLength() <= 63) {
            return scaled.toLong()
        }
        throw IllegalArgumentException("Overflow: cannot convert $value to long")
    }

    /**
     * Converts the specified big decimal value to an unscaled decimal applying
     * the given rounding mode if necessary. An exception is thrown if the value
     * exceeds the valid Decimal range.
     *
     * @param scaleMetrics
     * the scale metrics of the result value
     * @param roundingMode
     * the rounding mode to apply if necessary
     * @param value
     * the big decimal value to convert
     * @return `round(value)`
     * @throws IllegalArgumentException
     * if the value is outside of the valid Decimal range
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is
     * necessary
     */
	
	fun bigDecimalToUnscaled(scaleMetrics: ScaleMetrics, roundingMode: RoundingMode, value: BigDecimal): Long {
        // TODO any chance to make this garbage free?
        // Difficult as we cannot look inside the BigDecimal value
        val scaled = value //
            .multiply(scaleMetrics.getScaleFactorAsBigDecimal()) //
            .setScale(0, roundingMode.toJavaRoundingMode()) //
            .toBigInteger()
        if (scaled.bitLength() <= 63) {
            return scaled.toLong()
        }
        throw IllegalArgumentException(
            "Overflow: cannot convert " + value + " to Decimal with scale " + scaleMetrics.getScale()
        )
    }

    /**
     * Converts the given unscaled decimal value to a [BigDecimal] of the
     * same scale as the given decimal value.
     *
     * @param scaleMetrics
     * the scale metrics associated with the unscaled value
     * @param uDecimal
     * the unscaled decimal value to convert
     * @return a big decimal with the scale from scale metrics
     */
	
	fun unscaledToBigDecimal(scaleMetrics: ScaleMetrics, uDecimal: Long): BigDecimal {
        return BigDecimal.valueOf(uDecimal, scaleMetrics.getScale())
    }

    /**
     * Converts the given unscaled decimal value to a [BigDecimal] of the
     * specified `targetScale` rounding the value if necessary.
     *
     * @param scaleMetrics
     * the scale metrics associated with the unscaled value
     * @param roundingMode
     * the rounding mode to use if rounding is necessary
     * @param uDecimal
     * the unscaled decimal value to convert
     * @param targetScale
     * the scale of the result value
     * @return a big decimal with the specified `targetScale`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is
     * necessary
     */
	
	fun unscaledToBigDecimal(
        scaleMetrics: ScaleMetrics,
        roundingMode: RoundingMode,
        uDecimal: Long,
        targetScale: Int
    ): BigDecimal {
        val sourceScale = scaleMetrics.getScale()
        if (targetScale == sourceScale) {
            return unscaledToBigDecimal(scaleMetrics, uDecimal)
        }
        if (targetScale < sourceScale) {
            val diff = sourceScale - targetScale
            if (diff <= 18) {
                val diffMetrics = Scales.getScaleMetrics(diff)
                val rescaled = diffMetrics.getArithmetic(roundingMode).divideByPowerOf10(uDecimal, diff)
                return BigDecimal.valueOf(rescaled, targetScale)
            }
        } else {
            // does it fit in a long?
            val diff = targetScale - sourceScale
            if (diff <= 18) {
                val diffMetrics = Scales.getScaleMetrics(diff)
                if (diffMetrics.isValidIntegerValue(uDecimal)) {
                    val rescaled = diffMetrics.multiplyByScaleFactor(uDecimal)
                    return BigDecimal.valueOf(rescaled, targetScale)
                }
            }
        }
        // let the big decimal deal with such large numbers then
        return BigDecimal.valueOf(uDecimal, sourceScale).setScale(targetScale, roundingMode.toJavaRoundingMode())
    }
}

fun RoundingMode.toJavaRoundingMode(): java.math.RoundingMode {
    return when (this) {
        RoundingMode.UP -> java.math.RoundingMode.UP
        RoundingMode.DOWN -> java.math.RoundingMode.DOWN
        RoundingMode.CEILING -> java.math.RoundingMode.CEILING
        RoundingMode.FLOOR -> java.math.RoundingMode.FLOOR
        RoundingMode.HALF_UP -> java.math.RoundingMode.HALF_UP
        RoundingMode.HALF_DOWN -> java.math.RoundingMode.HALF_DOWN
        RoundingMode.HALF_EVEN -> java.math.RoundingMode.HALF_EVEN
        RoundingMode.UNNECESSARY -> java.math.RoundingMode.UNNECESSARY
    }
}