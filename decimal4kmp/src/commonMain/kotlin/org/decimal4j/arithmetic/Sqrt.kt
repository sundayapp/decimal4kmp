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
import org.decimal4j.truncate.DecimalRounding
import org.decimal4j.truncate.TruncatedPart
import kotlin.math.sqrt

/**
 * Provides static methods to calculate square roots of Decimal numbers.
 */
internal object Sqrt {
    /**
     * This mask is used to obtain the value of an int as if it were unsigned.
     */
    private const val LONG_MASK = 0xffffffffL

    /**
     * Calculates the square root of the specified long value truncating the
     * result if necessary.
     *
     * @param lValue
     * the long value
     * @return `round<sub>DOWN</sub>(lValue)`
     * @throws ArithmeticException
     * if `lValue < 0`
     */
    
    fun sqrtLong(lValue: Long): Long {
        if (lValue < 0) {
            throw ArithmeticException("Square root of a negative value: $lValue")
        }
        // http://www.codecodex.com/wiki/Calculate_an_integer_square_root
        if ((lValue and -0x10000000000000L) == 0L) {
            return sqrt(lValue.toDouble()).toLong()
        }
        val result = sqrt(2.0 * (lValue ushr 1)).toLong()
        return if (result * result - lValue > 0L) result - 1 else result
    }

    /**
     * Calculates the square root of the specified long value rounding the
     * result if necessary.
     *
     * @param rounding
     * the rounding to apply if necessary
     * @param lValue
     * the long value
     * @return `round(lValue)`
     * @throws ArithmeticException
     * if `lValue < 0`
     */
    
    fun sqrtLong(rounding: DecimalRounding, lValue: Long): Long {
        if (lValue < 0) {
            throw ArithmeticException("Square root of a negative value: $lValue")
        }
        // square root
        // @see
        // http://www.embedded.com/electronics-blogs/programmer-s-toolbox/4219659/Integer-Square-Roots
        var rem: Long = 0
        var root: Long = 0
        val zerosHalf = lValue.numberOfLeadingZeros() shr 1
        var scaled = lValue shl (zerosHalf shl 1)
        for (i in zerosHalf..31) {
            root = root shl 1
            rem = ((rem shl 2) + (scaled ushr 62))
            scaled = scaled shl 2
            root++
            if (root <= rem) {
                rem -= root
                root++
            } else {
                root--
            }
        }
        val truncated = root ushr 1
        if ((rem == 0L) or (rounding === DecimalRounding.DOWN) or (rounding === DecimalRounding.FLOOR)) {
            return truncated
        }
        return truncated + getRoundingIncrement(rounding, truncated, rem)
    }

    /**
     * Calculates the square root of the specified unscaled decimal value
     * truncating the result if necessary.
     *
     * @param arith
     * the arithmetic associated with the value
     * @param uDecimal
     * the unscaled decimal value
     * @return `round<sub>DOWN</sub>(uDecimal)`
     * @throws ArithmeticException
     * if `uDecimal < 0`
     */
    
    fun sqrt(arith: DecimalArithmetic, uDecimal: Long): Long {
        return sqrt(arith, DecimalRounding.DOWN, uDecimal)
    }

    /**
     * Calculates the square root of the specified unscaled decimal value
     * rounding the result if necessary.
     *
     * @param arith
     * the arithmetic associated with the value
     * @param rounding
     * the rounding to apply if necessary
     * @param uDecimal
     * the unscaled decimal value
     * @return `round(uDecimal)`
     * @throws ArithmeticException
     * if `uDecimal < 0`
     */
    
    fun sqrt(arith: DecimalArithmetic, rounding: DecimalRounding, uDecimal: Long): Long {
        if (uDecimal < 0) {
            throw ArithmeticException("Square root of a negative value: " + arith.toString(uDecimal))
        }
        val scaleMetrics = arith.scaleMetrics

        // multiply by scale factor into a 128bit integer
        val lFactor = (uDecimal and LONG_MASK).toInt()
        val hFactor = (uDecimal ushr 32).toInt()
        var lScaled: Long
        var hScaled: Long

        var product = scaleMetrics.mulloByScaleFactor(lFactor)
        lScaled = product and LONG_MASK
        product = scaleMetrics.mulhiByScaleFactor(lFactor) + (product ushr 32)
        hScaled = product ushr 32
        product = scaleMetrics.mulloByScaleFactor(hFactor) + (product and LONG_MASK)
        lScaled = lScaled or ((product and LONG_MASK) shl 32)
        hScaled = scaleMetrics.mulhiByScaleFactor(hFactor) + hScaled + (product ushr 32)
        var rem: Long = 0
        var root: Long = 0

        // iteration for high 32 bits

        // square root
        // @see
        // http://www.embedded.com/electronics-blogs/programmer-s-toolbox/4219659/Integer-Square-Roots
        var zerosHalf = hScaled.numberOfLeadingZeros() shr 1
        hScaled = hScaled shl (zerosHalf shl 1)
        for (i in zerosHalf..31) {
            root = root shl 1
            rem = ((rem shl 2) + (hScaled ushr 62))
            hScaled = hScaled shl 2
            root++
            if (root <= rem) {
                rem -= root
                root++
            } else {
                root--
            }
        }

        // iteration for low 32 bits (last iteration below)
        zerosHalf = if (zerosHalf == 32) lScaled.numberOfLeadingZeros() shr 1 else 0
        lScaled = lScaled shl (zerosHalf shl 1)
        for (i in zerosHalf..30) {
            root = root shl 1
            rem = ((rem shl 2) + (lScaled ushr 62))
            lScaled = lScaled shl 2
            root++
            if (root <= rem) {
                rem -= root
                root++
            } else {
                root--
            }
        }

        // last iteration needs unsigned compare
        root = root shl 1
        rem = ((rem shl 2) + (lScaled ushr 62))
        lScaled = lScaled shl 2
        root++
        if (Unsigned.isLessOrEqual(root, rem)) {
            rem -= root
            root++
        } else {
            root--
        }

        // round result if necessary
        val truncated = root ushr 1
        if ((rem == 0L) or (rounding === DecimalRounding.DOWN) or (rounding === DecimalRounding.FLOOR)) {
            return truncated
        }
        return truncated + getRoundingIncrement(rounding, truncated, rem)
    }

    // PRECONDITION: rem != 0
    // NOTE: TruncatedPart cannot be 0.5 because this would square to 0.25
    private fun getRoundingIncrement(rounding: DecimalRounding, truncated: Long, rem: Long): Int {
        if (truncated < rem) {
            return rounding.calculateRoundingIncrement(1, truncated, TruncatedPart.GREATER_THAN_HALF)
        }
        return rounding.calculateRoundingIncrement(1, truncated, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO)
    }
}
