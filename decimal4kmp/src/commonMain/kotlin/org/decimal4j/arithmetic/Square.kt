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
import org.decimal4j.arithmetic.Checked.addLong
import org.decimal4j.arithmetic.Checked.multiplyLong
import org.decimal4j.arithmetic.Exceptions.newArithmeticExceptionWithCause
import org.decimal4j.arithmetic.Exceptions.rethrowIfRoundingNecessary
import org.decimal4j.arithmetic.Rounding.calculateRoundingIncrement
import org.decimal4j.scale.Scale9f
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales
import org.decimal4j.truncate.DecimalRounding

/**
 * Provides methods to calculate squares.
 */
internal object Square {
    private val SCALE9F = Scale9f.INSTANCE

    /**
     * Value representing: `floor(sqrt(Long.MAX_VALUE))`
     */
    const val SQRT_MAX_VALUE: Long = 3037000499L

    // necessary and sufficient condition that square fits in long
    private fun doesSquareFitInLong(uDecimal: Long): Boolean {
        return (-SQRT_MAX_VALUE <= uDecimal) and (uDecimal <= SQRT_MAX_VALUE)
    }

    /**
     * Calculates the square `uDecimal^2 / scaleFactor` without rounding.
     * Overflows are silently truncated.
     *
     * @param scaleMetrics
     * the scale metrics defining the scale
     * @param uDecimal
     * the unscaled decimal value to square
     * @return the square result without rounding
     */
    
    fun square(scaleMetrics: ScaleMetrics, uDecimal: Long): Long {
        if (doesSquareFitInLong(uDecimal)) {
            // square fits in long, just do it
            return scaleMetrics.divideByScaleFactor(uDecimal * uDecimal)
        }
        val scale = scaleMetrics.getScale()
        if (scale <= 9) {
            // use scale to split into 2 parts: i (integral) and f (fractional)
            // with this scale, the low order product f*f fits in a long
            val i = scaleMetrics.divideByScaleFactor(uDecimal)
            val f = uDecimal - scaleMetrics.multiplyByScaleFactor(i)
            return scaleMetrics.multiplyByScaleFactor(i * i) + ((i * f) shl 1) + scaleMetrics.divideByScaleFactor(f * f)
        } else {
            // use scale9 to split into 2 parts: h (high) and l (low)
            val scaleDiff09 = Scales.getScaleMetrics(scale - 9)
            val scaleDiff18 = Scales.getScaleMetrics(18 - scale)
            val h = SCALE9F.divideByScaleFactor(uDecimal)
            val l = uDecimal - SCALE9F.multiplyByScaleFactor(h)
            val hxl = h * l
            val lxld = SCALE9F.divideByScaleFactor(l * l)
            val hxld = scaleDiff09.divideByScaleFactor(hxl)
            val hxlr = hxl - scaleDiff09.multiplyByScaleFactor(hxld)
            return (scaleDiff18.multiplyByScaleFactor(h * h) + (hxld shl 1)
                    + scaleDiff09.divideByScaleFactor((hxlr shl 1) + lxld))
        }
    }

    /**
     * Calculates the square `uDecimal^2 / scaleFactor` applying the
     * specified rounding for truncated decimals. Overflows are silently
     * truncated.
     *
     * @param scaleMetrics
     * the scale metrics defining the scale
     * @param rounding
     * the rounding to apply for truncated decimals
     * @param uDecimal
     * the unscaled decimal value to square
     * @return the square result with rounding
     */
    
    fun square(scaleMetrics: ScaleMetrics, rounding: DecimalRounding, uDecimal: Long): Long {
        if (doesSquareFitInLong(uDecimal)) {
            // square fits in long, just do it
            return square32(scaleMetrics, rounding, uDecimal)
        }
        val scale = scaleMetrics.getScale()
        if (scale <= 9) {
            // use scale to split into 2 parts: i (integral) and f (fractional)
            // with this scale, the low order product f*f fits in a long
            val i = scaleMetrics.divideByScaleFactor(uDecimal)
            val f = uDecimal - scaleMetrics.multiplyByScaleFactor(i)
            val fxf = f * f
            val fxfd = scaleMetrics.divideByScaleFactor(fxf)
            val fxfr = fxf - scaleMetrics.multiplyByScaleFactor(fxfd)
            val unrounded = scaleMetrics.multiplyByScaleFactor(i * i) + ((i * f) shl 1) + fxfd
            return (unrounded
                    + calculateRoundingIncrement(rounding, unrounded, fxfr, scaleMetrics.getScaleFactor()))
        } else {
            // use scale9 to split into 2 parts: h (high) and l (low)
            val scaleDiff09 = Scales.getScaleMetrics(scale - 9)
            val scaleDiff18 = Scales.getScaleMetrics(18 - scale)
            val h = SCALE9F.divideByScaleFactor(uDecimal)
            val l = uDecimal - SCALE9F.multiplyByScaleFactor(h)
            val hxl = h * l
            val lxl = l * l
            val lxld = SCALE9F.divideByScaleFactor(lxl)
            val hxld = scaleDiff09.divideByScaleFactor(hxl)
            val hxlr = hxl - scaleDiff09.multiplyByScaleFactor(hxld)
            val lxlr = lxl - SCALE9F.multiplyByScaleFactor(lxld)
            val hxlx2_lxl = (hxlr shl 1) + lxld
            val hxlx2_lxld = scaleDiff09.divideByScaleFactor(hxlx2_lxl)
            val hxlx2_lxlr = hxlx2_lxl - scaleDiff09.multiplyByScaleFactor(hxlx2_lxld)
            val unrounded = scaleDiff18.multiplyByScaleFactor(h * h) + (hxld shl 1) + hxlx2_lxld
            val remainder = SCALE9F.multiplyByScaleFactor(hxlx2_lxlr) + lxlr
            return unrounded + calculateRoundingIncrement(
                rounding, unrounded, remainder,
                scaleMetrics.getScaleFactor()
            )
        }
    }

    // PRECONDITION: uDecimal <= SQRT_MAX_VALUE
    private fun square32(scaleMetrics: ScaleMetrics, rounding: DecimalRounding, uDecimal: Long): Long {
        val u2 = uDecimal * uDecimal
        val u2d = scaleMetrics.divideByScaleFactor(u2)
        val u2r = u2 - scaleMetrics.multiplyByScaleFactor(u2d)
        return u2d + calculateRoundingIncrement(rounding, u2d, u2r, scaleMetrics.getScaleFactor())
    }

    /**
     * Calculates the square `uDecimal^2 / scaleFactor` truncating the
     * result if necessary. Throws an exception if an overflow occurs.
     *
     * @param arith
     * the arithmetic associated with the value
     * @param uDecimal
     * the unscaled decimal value to square
     * @return the square result without rounding
     */
    fun squareChecked(arith: DecimalArithmetic, uDecimal: Long): Long {
        val scaleMetrics = arith.scaleMetrics
        if (doesSquareFitInLong(uDecimal)) {
            // square fits in long, just do it
            return scaleMetrics.divideByScaleFactor(uDecimal * uDecimal)
        }
        val scale = scaleMetrics.getScale()
        try {
            if (scale <= 9) {
                // use scale to split into 2 parts: i (integral) and f
                // (fractional)
                // with this scale, the low order product f*f fits in a long
                val i = scaleMetrics.divideByScaleFactor(uDecimal)
                val f = uDecimal - scaleMetrics.multiplyByScaleFactor(i)
                val ixi = multiplyLong(i, i) // checked
                val ixf = i * f // cannot overflow
                val fxf = scaleMetrics.divideByScaleFactor(f * f) // unchecked:ok
                // check whether we can multiply ixf by 2
                if (ixf < 0) throw ArithmeticException("Overflow: $ixf<<1")
                val ixfx2 = ixf shl 1
                // add it all up now, every operation checked
                var result = scaleMetrics.multiplyByScaleFactorExact(ixi)
                result = addLong(result, ixfx2)
                result = addLong(result, fxf)
                return result
            } else {
                // use scale9 to split into 2 parts: h (high) and l (low)
                val scaleDiff09 = Scales.getScaleMetrics(scale - 9)
                val scaleDiff18 = Scales.getScaleMetrics(18 - scale)
                val h = SCALE9F.divideByScaleFactor(uDecimal)
                val l = uDecimal - SCALE9F.multiplyByScaleFactor(h)

                val hxh = multiplyLong(h, h) // checked
                val hxl = h * l // cannot overflow
                val lxld = SCALE9F.divideByScaleFactor(l * l) // unchecked:ok
                val hxld = scaleDiff09.divideByScaleFactor(hxl)
                val hxlr = hxl - scaleDiff09.multiplyByScaleFactor(hxld)
                // check whether we can multiply hxld by 2
                if (hxld < 0) throw ArithmeticException("Overflow: $hxld<<1")
                val hxldx2 = hxld shl 1
                // add it all up now, every operation checked
                var result = scaleDiff18.multiplyByScaleFactorExact(hxh)
                result = addLong(result, hxldx2)
                result = addLong(result, scaleDiff09.divideByScaleFactor((hxlr shl 1) + lxld))
                return result
            }
        } catch (e: ArithmeticException) {
            throw newArithmeticExceptionWithCause("Overflow: " + arith.toString(uDecimal) + "^2", e)
        }
    }

    /**
     * Calculates the square `uDecimal^2 / scaleFactor` applying the
     * specified rounding for truncated decimals. Throws an exception if an
     * overflow occurs.
     *
     * @param arith
     * the arithmetic associated with the value
     * @param rounding
     * the rounding to apply for truncated decimals
     * @param uDecimal
     * the unscaled decimal value to square
     * @return the square result with rounding
     */
    fun squareChecked(arith: DecimalArithmetic, rounding: DecimalRounding, uDecimal: Long): Long {
        val scaleMetrics = arith.scaleMetrics
        if (doesSquareFitInLong(uDecimal)) {
            // square fits in long, just do it
            return square32(scaleMetrics, rounding, uDecimal)
        }
        try {
            val scale = scaleMetrics.getScale()
            if (scale <= 9) {
                // use scale to split into 2 parts: i (integral) and f
                // (fractional)
                val i = scaleMetrics.divideByScaleFactor(uDecimal)
                val f = uDecimal - scaleMetrics.multiplyByScaleFactor(i)

                val ixi = multiplyLong(i, i)
                val fxf = f * f // low order product f*f fits in a long
                val ixf = i * f // cannot overflow
                // check whether we can multiply ixf by 2
                if (ixf < 0) throw ArithmeticException("Overflow: $ixf<<1")
                val ixfx2 = ixf shl 1

                val fxfd = scaleMetrics.divideByScaleFactor(fxf)
                val fxfr = fxf - scaleMetrics.multiplyByScaleFactor(fxfd)

                // add it all up now, every operation checked
                var unrounded = scaleMetrics.multiplyByScaleFactorExact(ixi)
                unrounded = addLong(unrounded, ixfx2)
                unrounded = addLong(unrounded, fxfd)
                return addLong(
                    unrounded,
                    calculateRoundingIncrement(rounding, unrounded, fxfr, scaleMetrics.getScaleFactor()).toLong()
                )
            } else {
                // use scale9 to split into 2 parts: h (high) and l (low)
                val scaleDiff09 = Scales.getScaleMetrics(scale - 9)
                val scaleDiff18 = Scales.getScaleMetrics(18 - scale)
                val h = SCALE9F.divideByScaleFactor(uDecimal)
                val l = uDecimal - SCALE9F.multiplyByScaleFactor(h)

                val hxh = multiplyLong(h, h)
                val hxl = h * l // cannot overflow

                val hxld = scaleDiff09.divideByScaleFactor(hxl)
                val hxlr = hxl - scaleDiff09.multiplyByScaleFactor(hxld)
                val hxldx2 = hxld shl 1 // cannot overflow

                val lxl = l * l // cannot overflow
                val lxld = SCALE9F.divideByScaleFactor(lxl)
                val lxlr = lxl - SCALE9F.multiplyByScaleFactor(lxld)

                val hxlx2_lxl = (hxlr shl 1) + lxld // cannot overflow
                val hxlx2_lxld = scaleDiff09.divideByScaleFactor(hxlx2_lxl)
                val hxlx2_lxlr = hxlx2_lxl - scaleDiff09.multiplyByScaleFactor(hxlx2_lxld)

                // add it all up now, every operation checked
                var unrounded = scaleDiff18.multiplyByScaleFactorExact(hxh)
                unrounded = addLong(unrounded, hxldx2)
                unrounded = addLong(unrounded, hxlx2_lxld)
                val remainder = SCALE9F.multiplyByScaleFactor(hxlx2_lxlr) + lxlr // cannot
                // overflow
                return addLong(
                    unrounded, calculateRoundingIncrement(
                        rounding, unrounded, remainder,
                        scaleMetrics.getScaleFactor()
                    ).toLong()
                )
            }
        } catch (e: ArithmeticException) {
            rethrowIfRoundingNecessary(e)
            throw newArithmeticExceptionWithCause("Overflow: " + arith.toString(uDecimal) + "^2", e)
        }
    }
}
