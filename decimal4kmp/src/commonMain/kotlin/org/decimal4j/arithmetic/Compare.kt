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

import org.decimal4j.scale.Scales
import kotlin.math.sign

/**
 * Contains static methods to compare unscaled decimals of different scales.
 */
internal object Compare {
    /**
     * Compares the two unscaled values with possibly different scales.
     *
     * @param unscaled
     * the first unscaled value to compare
     * @param scale
     * the scale of the first value
     * @param otherUnscaled
     * the second unscaled value to compare
     * @param otherScale
     * the scale of the second value
     * @return the value `0` if `unscaled1 == unscaled2`; a value
     * less than `0` if `unscaled1 < unscaled2`; and a value
     * greater than `0` if `unscaled1 > unscaled2`
     */
	
	fun compareUnscaled(unscaled: Long, scale: Int, otherUnscaled: Long, otherScale: Int): Int {
        if (scale == otherScale) {
            return unscaled.compareTo(otherUnscaled)
        }
        if (scale < otherScale) {
            val diffMetrics = Scales.getScaleMetrics(otherScale - scale)
            val otherRescaled = diffMetrics.divideByScaleFactor(otherUnscaled)
            val cmp = unscaled.compareTo(otherRescaled)
            if (cmp != 0) {
                return cmp
            }
            // remainder must be zero for equality
            val otherRemainder = otherUnscaled - diffMetrics.multiplyByScaleFactor(otherRescaled)
            return -otherRemainder.sign
        } else {
            val diffMetrics = Scales.getScaleMetrics(scale - otherScale)
            val rescaled = diffMetrics.divideByScaleFactor(unscaled)
            val cmp = rescaled.compareTo(otherUnscaled)
            if (cmp != 0) {
                return cmp
            }
            // remainder must be zero for equality
            val remainder = unscaled - diffMetrics.multiplyByScaleFactor(rescaled)
            return remainder.sign
        }
    }
}
