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
import org.decimal4j.scale.Scale9f
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales
import org.decimal4j.truncate.DecimalRounding

/**
 * Provides methods to calculate multiplication results.
 */
internal object Mul {
    private val SCALE9F: ScaleMetrics = Scale9f.INSTANCE

    //sufficient (but not necessary) condition that product fits in long
    private fun doesProductFitInLong(uDecimal1: Long, uDecimal2: Long): Boolean {
        if ((-Square.SQRT_MAX_VALUE <= uDecimal1) and (uDecimal1 <= Square.SQRT_MAX_VALUE) and (-Square.SQRT_MAX_VALUE <= uDecimal2) and (uDecimal2 <= Square.SQRT_MAX_VALUE)) {
            return true
        }
        return false
        //NOTE: not worth checking (too much overhead for too few special cases):
//		final int leadingZeros = Long.numberOfLeadingZeros(uDecimal1) + Long.numberOfLeadingZeros(~uDecimal1) + Long.numberOfLeadingZeros(uDecimal2) + Long.numberOfLeadingZeros(~uDecimal2);
//		return leadingZeros > Long.SIZE + 1;
    }

    /**
     * Calculates the multiple `uDecimal1 * uDecimal2 / scaleFactor`
     * without rounding.
     *
     * @param arith
     * the arithmetic with access to scale metrics etc.
     * @param uDecimal1
     * the first unscaled decimal factor
     * @param uDecimal2
     * the second unscaled decimal factor
     * @return the multiplication result without rounding
     */
	@JvmStatic
	fun multiply(arith: DecimalArithmetic, uDecimal1: Long, uDecimal2: Long): Long {
        val special = SpecialMultiplicationResult.getFor(arith, uDecimal1, uDecimal2)
        if (special != null) {
            return special.multiply(arith, uDecimal1, uDecimal2)
        }
        return multiply(uDecimal1, arith.scaleMetrics, uDecimal2)
    }

    /**
     * Calculates unchecked multiplication by an unscaled value with the given scale
     * without rounding.
     *
     * @param uDecimal
     * the unscaled decimal factor
     * @param unscaled
     * the second unscaled factor
     * @param scale
     * the scale of the second factor
     * @return the multiplication result without rounding and without overflow checks
     */
	@JvmStatic
	fun multiplyByUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long {
        require(scale <= Scales.MAX_SCALE) { "Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale }
        if ((uDecimal == 0L) or (unscaled == 0L)) {
            return 0
        } else if (scale == 0) {
            return uDecimal * unscaled
        } else if (scale < 0) {
            return Pow10.divideByPowerOf10(uDecimal * unscaled, scale)
        }
        val scaleMetrics = Scales.getScaleMetrics(scale)
        return multiply(uDecimal, scaleMetrics, unscaled)
    }

    /**
     * Calculates the multiple `uDecimal1 * uDecimal2 / scaleFactor`
     * without rounding.
     *
     * @param uDecimal1
     * the first unscaled decimal factor
     * @param scaleMetrics2
     * the scale metrics associated with the second factor
     * @param uDecimal2
     * the second unscaled decimal factor
     * @return the multiplication result without rounding
     */
    private fun multiply(uDecimal1: Long, scaleMetrics2: ScaleMetrics, uDecimal2: Long): Long {
        if (doesProductFitInLong(uDecimal1, uDecimal2)) {
            //product fits in long, just do it
            return scaleMetrics2.divideByScaleFactor(uDecimal1 * uDecimal2)
        }
        val scale = scaleMetrics2.getScale()
        if (scale <= 9) {
            //use scale to split into 2 parts: i (integral) and f (fractional)
            //with this scale, the low order product f1*f2 fits in a long
            val i1 = scaleMetrics2.divideByScaleFactor(uDecimal1)
            val i2 = scaleMetrics2.divideByScaleFactor(uDecimal2)
            val f1 = uDecimal1 - scaleMetrics2.multiplyByScaleFactor(i1)
            val f2 = uDecimal2 - scaleMetrics2.multiplyByScaleFactor(i2)
            return uDecimal1 * i2 + i1 * f2 + scaleMetrics2.divideByScaleFactor(f1 * f2)
        } else {
            //use scale9 to split into 2 parts: h (high) and l (low)
            val scaleDiff09 = Scales.getScaleMetrics(scale - 9)
            val scaleDiff18 = Scales.getScaleMetrics(18 - scale)
            val h1 = SCALE9F.divideByScaleFactor(uDecimal1)
            val h2 = SCALE9F.divideByScaleFactor(uDecimal2)
            val l1 = uDecimal1 - SCALE9F.multiplyByScaleFactor(h1)
            val l2 = uDecimal2 - SCALE9F.multiplyByScaleFactor(h2)
            val h1xl2 = h1 * l2
            val h2xl1 = h2 * l1
            val l1xl2d = SCALE9F.divideByScaleFactor(l1 * l2)
            val h1xl2d = scaleDiff09.divideByScaleFactor(h1xl2)
            val h2xl1d = scaleDiff09.divideByScaleFactor(h2xl1)
            val h1xl2r = h1xl2 - scaleDiff09.multiplyByScaleFactor(h1xl2d)
            val h2xl1r = h2xl1 - scaleDiff09.multiplyByScaleFactor(h2xl1d)
            return scaleDiff18.multiplyByScaleFactor(h1 * h2) + h1xl2d + h2xl1d + scaleDiff09.divideByScaleFactor(h1xl2r + h2xl1r + l1xl2d)
        }
    }

    /**
     * Calculates the multiple `uDecimal1 * uDecimal2 / scaleFactor`
     * applying the specified rounding if necessary.
     *
     * @param arith
     * the arithmetic with access to scale metrics etc.
     * @param rounding
     * the rounding to apply if necessary
     * @param uDecimal1
     * the first unscaled decimal factor
     * @param uDecimal2
     * the second unscaled decimal factor
     * @return the multiplication result with rounding
     */
	@JvmStatic
	fun multiply(arith: DecimalArithmetic, rounding: DecimalRounding, uDecimal1: Long, uDecimal2: Long): Long {
        val special = SpecialMultiplicationResult.getFor(arith, uDecimal1, uDecimal2)
        if (special != null) {
            return special.multiply(arith, uDecimal1, uDecimal2)
        }
        return multiply(rounding, uDecimal1, arith.scaleMetrics, uDecimal2)
    }

    /**
     * Calculates unchecked multiplication by an unscaled value with the given
     * scale with rounding.
     *
     * @param rounding
     * the rounding to apply
     * @param uDecimal
     * the unscaled decimal factor
     * @param unscaled
     * the second unscaled factor
     * @param scale
     * the scale of the second factor
     * @return the multiplication result with rounding and without overflow checks
     */
	@JvmStatic
	fun multiplyByUnscaled(rounding: DecimalRounding, uDecimal: Long, unscaled: Long, scale: Int): Long {
        require(scale <= Scales.MAX_SCALE) { "Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale }
        if ((uDecimal == 0L) or (unscaled == 0L)) {
            return 0
        } else if (scale == 0) {
            return uDecimal * unscaled
        } else if (scale < 0) {
            return Pow10.divideByPowerOf10(rounding, uDecimal * unscaled, scale)
        }
        val scaleMetrics = Scales.getScaleMetrics(scale)
        return multiply(rounding, uDecimal, scaleMetrics, unscaled)
    }

    /**
     * Calculates unchecked multiplication by an unscaled value with the given
     * scale with rounding.
     *
     * @param rounding
     * the rounding to apply
     * @param uDecimal1
     * the first unscaled decimal factor
     * @param scaleMetrics2
     * the scale metrics associated with the second factor
     * @param uDecimal2
     * the second unscaled decimal factor
     * @return the multiplication result with rounding and without overflow checks
     */
    private fun multiply(
        rounding: DecimalRounding,
        uDecimal1: Long,
        scaleMetrics2: ScaleMetrics,
        uDecimal2: Long
    ): Long {
        if (doesProductFitInLong(uDecimal1, uDecimal2)) {
            //product fits in long, just do it
            return multiply32(rounding, uDecimal1, scaleMetrics2, uDecimal2)
        }

        val scale = scaleMetrics2.getScale()
        if (scale <= 9) {
            //use scale to split into 2 parts: i (integral) and f (fractional)
            //with this scale, the low order product f1*f2 fits in a long
            val i1 = scaleMetrics2.divideByScaleFactor(uDecimal1)
            val i2 = scaleMetrics2.divideByScaleFactor(uDecimal2)
            val f1 = uDecimal1 - scaleMetrics2.multiplyByScaleFactor(i1)
            val f2 = uDecimal2 - scaleMetrics2.multiplyByScaleFactor(i2)
            val f1xf2 = f1 * f2
            val f1xf2d = scaleMetrics2.divideByScaleFactor(f1xf2)
            val f1xf2r = f1xf2 - scaleMetrics2.multiplyByScaleFactor(f1xf2d)
            val unrounded = uDecimal1 * i2 + i1 * f2 + f1xf2d
            return unrounded + Rounding.calculateRoundingIncrement(
                rounding,
                unrounded,
                f1xf2r,
                scaleMetrics2.getScaleFactor()
            )
        } else {
            //use scale9 to split into 2 parts: h (high) and l (low)
            val scaleDiff09 = Scales.getScaleMetrics(scale - 9)
            val scaleDiff18 = Scales.getScaleMetrics(18 - scale)
            val h1 = SCALE9F.divideByScaleFactor(uDecimal1)
            val h2 = SCALE9F.divideByScaleFactor(uDecimal2)
            val l1 = uDecimal1 - SCALE9F.multiplyByScaleFactor(h1)
            val l2 = uDecimal2 - SCALE9F.multiplyByScaleFactor(h2)
            val h1xl2 = h1 * l2
            val h2xl1 = h2 * l1
            val l1xl2 = l1 * l2
            val l1xl2d = SCALE9F.divideByScaleFactor(l1xl2)
            val h1xl2d = scaleDiff09.divideByScaleFactor(h1xl2)
            val h2xl1d = scaleDiff09.divideByScaleFactor(h2xl1)
            val h1xl2r = h1xl2 - scaleDiff09.multiplyByScaleFactor(h1xl2d)
            val h2xl1r = h2xl1 - scaleDiff09.multiplyByScaleFactor(h2xl1d)
            val l1xl2r = l1xl2 - SCALE9F.multiplyByScaleFactor(l1xl2d)
            val h1xl2_h2xl1_l1xl1 = h1xl2r + h2xl1r + l1xl2d
            val h1xl2_h2xl1_l1xl1d = scaleDiff09.divideByScaleFactor(h1xl2_h2xl1_l1xl1)
            val h1xl2_h2xl1_l1xl1r = h1xl2_h2xl1_l1xl1 - scaleDiff09.multiplyByScaleFactor(h1xl2_h2xl1_l1xl1d)
            val unrounded = scaleDiff18.multiplyByScaleFactor(h1 * h2) + h1xl2d + h2xl1d + h1xl2_h2xl1_l1xl1d
            val remainder = SCALE9F.multiplyByScaleFactor(h1xl2_h2xl1_l1xl1r) + l1xl2r
            return unrounded + Rounding.calculateRoundingIncrement(
                rounding,
                unrounded,
                remainder,
                scaleMetrics2.getScaleFactor()
            )
        }
    }

    /**
     * Calculates `round((uDecimal1 * uDecimal2) / scaleFactor2)` treating
     * the factors as 32 bit values whose product must fit in a long result.
     *
     * @param rounding
     * the rounding to use
     * @param uDecimal1
     * the first factor
     * @param scaleMetrics2
     * the scale metrics to apply to the product
     * @param uDecimal2
     * the second factor
     * @return the product rounded if necessary
     */
    private fun multiply32(
        rounding: DecimalRounding,
        uDecimal1: Long,
        scaleMetrics2: ScaleMetrics,
        uDecimal2: Long
    ): Long {
        val u1xu2 = uDecimal1 * uDecimal2
        val u1xu2d = scaleMetrics2.divideByScaleFactor(u1xu2)
        val u1xu2r = u1xu2 - scaleMetrics2.multiplyByScaleFactor(u1xu2d)
        return u1xu2d + Rounding.calculateRoundingIncrement(rounding, u1xu2d, u1xu2r, scaleMetrics2.getScaleFactor())
    }

    /**
     * Calculates the multiple `uDecimal1 * uDecimal2 / scaleFactor`
     * without rounding checking for overflows.
     *
     * @param arith
     * the arithmetic with access to scale metrics etc.
     * @param uDecimal1
     * the first unscaled decimal factor
     * @param uDecimal2
     * the second unscaled decimal factor
     * @return the multiplication result without rounding and with overflow checks
     */
    fun multiplyChecked(arith: DecimalArithmetic, uDecimal1: Long, uDecimal2: Long): Long {
        val special = SpecialMultiplicationResult.getFor(arith, uDecimal1, uDecimal2)
        if (special != null) {
            return special.multiply(arith, uDecimal1, uDecimal2)
        }
        val scaleMetrics = arith.scaleMetrics
        return multiplyChecked(scaleMetrics, uDecimal1, scaleMetrics, uDecimal2)
    }

    /**
     * Calculates checked multiplication by an unscaled value with the given scale
     * without rounding.
     *
     * @param arith
     * the decimal arithmetics associated with the first factor
     * @param uDecimal
     * the unscaled decimal factor
     * @param unscaled
     * the second unscaled factor
     * @param scale
     * the scale of the second factor
     * @return the multiplication result without rounding and with overflow checks
     */
    fun multiplyByUnscaledChecked(arith: DecimalArithmetic, uDecimal: Long, unscaled: Long, scale: Int): Long {
        require(scale <= Scales.MAX_SCALE) { "Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale }
        if ((uDecimal == 0L) or (unscaled == 0L)) {
            return 0
        } else if (scale == 0) {
            return arith.multiplyByLong(uDecimal, unscaled)
        } else if (scale < 0) {
            val unscaledResult = multiplyLong(uDecimal, unscaled)
            return Pow10.divideByPowerOf10Checked(arith, unscaledResult, scale)
        }
        val scaleMetrics = Scales.getScaleMetrics(scale)
        return multiplyChecked(arith.scaleMetrics, uDecimal, scaleMetrics, unscaled)
    }

    /**
     * Calculates checked multiplication by an unscaled value with the given scale
     * without rounding.
     *
     * @param scaleMetrics1
     * the scale matrics associated with the first factor
     * @param uDecimal1
     * the first unscaled decimal factor
     * @param scaleMetrics2
     * the scale matrics associated with the second factor
     * @param uDecimal2
     * the second unscaled decimal factor
     * @return the multiplication result without rounding and with overflow checks
     */
    private fun multiplyChecked(
        scaleMetrics1: ScaleMetrics,
        uDecimal1: Long,
        scaleMetrics2: ScaleMetrics,
        uDecimal2: Long
    ): Long {
        try {
            if (doesProductFitInLong(uDecimal1, uDecimal2)) {
                return scaleMetrics2.divideByScaleFactor(uDecimal1 * uDecimal2)
            }

            val scale = scaleMetrics2.getScale()
            if (scale <= 9) {
                //use scale to split into 2 parts: i (integral) and f (fractional)
                //with this scale, the low order product f1*f2 fits in a long
                val i1 = scaleMetrics2.divideByScaleFactor(uDecimal1)
                val i2 = scaleMetrics2.divideByScaleFactor(uDecimal2)
                val f1 = uDecimal1 - scaleMetrics2.multiplyByScaleFactor(i1)
                val f2 = uDecimal2 - scaleMetrics2.multiplyByScaleFactor(i2)
                val i1xf2 = i1 * f2 //cannot overflow
                val f1xf2 = scaleMetrics2.divideByScaleFactor(f1 * f2) //product fits for this scale, hence unchecked
                //add it all up now, every operation checked
                var result = multiplyLong(uDecimal1, i2)
                result = addLong(result, i1xf2)
                result = addLong(result, f1xf2)
                return result
            } else {
                //use scale9 to split into 2 parts: h (high) and l (low)
                val scaleDiff09 = Scales.getScaleMetrics(scale - 9)
                val scaleDiff18 = Scales.getScaleMetrics(18 - scale)
                val h1 = SCALE9F.divideByScaleFactor(uDecimal1)
                val h2 = SCALE9F.divideByScaleFactor(uDecimal2)
                val l1 = uDecimal1 - SCALE9F.multiplyByScaleFactor(h1)
                val l2 = uDecimal2 - SCALE9F.multiplyByScaleFactor(h2)
                val h1xh2 = multiplyLong(h1, h2) //checked
                val h1xl2 = h1 * l2 //cannot overflow
                val h2xl1 = h2 * l1 //cannot overflow
                val l1xl2d = SCALE9F.divideByScaleFactor(l1 * l2) //product fits for scale 9, hence unchecked
                val h1xl2d = scaleDiff09.divideByScaleFactor(h1xl2)
                val h2xl1d = scaleDiff09.divideByScaleFactor(h2xl1)
                val h1xl2r = h1xl2 - scaleDiff09.multiplyByScaleFactor(h1xl2d)
                val h2xl1r = h2xl1 - scaleDiff09.multiplyByScaleFactor(h2xl1d)
                //add it all up now, every operation checked
                var result = scaleDiff18.multiplyByScaleFactorExact(h1xh2)
                result = addLong(result, h1xl2d)
                result = addLong(result, h2xl1d)
                result = addLong(result, scaleDiff09.divideByScaleFactor(h1xl2r + h2xl1r + l1xl2d))
                return result
            }
        } catch (e: ArithmeticException) {
            throw newArithmeticExceptionWithCause(
                "Overflow: " + scaleMetrics1.toString(uDecimal1) + " * " + scaleMetrics2.toString(
                    uDecimal2
                ), e
            )
        }
    }

    /**
     * Calculates the multiple `uDecimal1 * uDecimal2 / scaleFactor`
     * with rounding.
     *
     * @param arith
     * the arithmetic with access to scale metrics etc.
     * @param rounding
     * the rounding to apply for truncated decimals
     * @param uDecimal1
     * the first unscaled decimal factor
     * @param uDecimal2
     * the second unscaled decimal factor
     *
     * @return the multiplication result with rounding and overflow checking
     */
    fun multiplyChecked(arith: DecimalArithmetic, rounding: DecimalRounding, uDecimal1: Long, uDecimal2: Long): Long {
        val special = SpecialMultiplicationResult.getFor(arith, uDecimal1, uDecimal2)
        if (special != null) {
            return special.multiply(arith, uDecimal1, uDecimal2)
        }
        val scaleMetrics = arith.scaleMetrics
        return multiplyChecked(rounding, scaleMetrics, uDecimal1, scaleMetrics, uDecimal2)
    }

    /**
     * Calculates checked multiplication by an unscaled value with the given
     * scale with rounding.
     *
     * @param arith
     * the arithmetics associated with `uDecimal`
     * @param rounding
     * the rounding to apply
     * @param uDecimal
     * the unscaled decimal factor
     * @param unscaled
     * the second unscaled factor
     * @param scale
     * the scale of the second factor
     * @return the multiplication result with rounding and overflow checks
     */
    fun multiplyByUnscaledChecked(
        arith: DecimalArithmetic,
        rounding: DecimalRounding,
        uDecimal: Long,
        unscaled: Long,
        scale: Int
    ): Long {
        require(scale <= Scales.MAX_SCALE) { "Illegal scale, must be <=" + Scales.MAX_SCALE + " but was " + scale }
        if ((uDecimal == 0L) or (unscaled == 0L)) {
            return 0
        } else if (scale == 0) {
            return arith.multiplyByLong(uDecimal, unscaled)
        } else if (scale < 0) {
            val unscaledResult = multiplyLong(uDecimal, unscaled)
            return Pow10.divideByPowerOf10Checked(arith, rounding, unscaledResult, scale)
        }
        val scaleMetrics2 = Scales.getScaleMetrics(scale)
        return multiplyChecked(rounding, arith.scaleMetrics, uDecimal, scaleMetrics2, unscaled)
    }

    /**
     * Calculates the checked multiple
     * `uDecimal1 * uDecimal2 / scaleFactor2` with rounding.
     *
     * @param rounding
     * the rounding to apply for truncated decimals
     * @param scaleMetrics1
     * the scale metrics of the first factor
     * @param uDecimal1
     * the first unscaled decimal factor
     * @param scaleMetrics2
     * the scale metrics of the second factor
     * @param uDecimal2
     * the second unscaled decimal factor
     * @return the multiplication result with rounding and overflow checking
     */
    private fun multiplyChecked(
        rounding: DecimalRounding,
        scaleMetrics1: ScaleMetrics,
        uDecimal1: Long,
        scaleMetrics2: ScaleMetrics,
        uDecimal2: Long
    ): Long {
        try {
            if (doesProductFitInLong(uDecimal1, uDecimal2)) {
                //product fits in long, just do it
                return multiply32(rounding, uDecimal1, scaleMetrics2, uDecimal2)
            }

            val scale = scaleMetrics2.getScale()
            if (scale <= 9) {
                //use scale to split into 2 parts: i (integral) and f (fractional)
                //with this scale, the low order product f1*f2 fits in a long
                val i1 = scaleMetrics2.divideByScaleFactor(uDecimal1)
                val i2 = scaleMetrics2.divideByScaleFactor(uDecimal2)
                val f1 = uDecimal1 - scaleMetrics2.multiplyByScaleFactor(i1)
                val f2 = uDecimal2 - scaleMetrics2.multiplyByScaleFactor(i2)
                val i1xf2 = i1 * f2 //cannot overflow
                val f1xf2 = f1 * f2 //cannot overflow for this scale
                val f1xf2d = scaleMetrics2.divideByScaleFactor(f1xf2)
                val f1xf2r = f1xf2 - scaleMetrics2.multiplyByScaleFactor(f1xf2d)
                //add it all up now, every operation checked
                var result = multiplyLong(uDecimal1, i2)
                result = addLong(result, i1xf2)
                result = addLong(result, f1xf2d)

                return result + Rounding.calculateRoundingIncrement(rounding, result, f1xf2r, scaleMetrics2.getScaleFactor())
            } else {
                //use scale9 to split into 2 parts: h (high) and l (low)
                val scaleDiff09 = Scales.getScaleMetrics(scale - 9)
                val scaleDiff18 = Scales.getScaleMetrics(18 - scale)
                val h1 = SCALE9F.divideByScaleFactor(uDecimal1)
                val h2 = SCALE9F.divideByScaleFactor(uDecimal2)
                val l1 = uDecimal1 - SCALE9F.multiplyByScaleFactor(h1)
                val l2 = uDecimal2 - SCALE9F.multiplyByScaleFactor(h2)
                val h1xl2 = h1 * l2
                val h2xl1 = h2 * l1
                val l1xl2 = l1 * l2
                val l1xl2d = SCALE9F.divideByScaleFactor(l1xl2)
                val h1xl2d = scaleDiff09.divideByScaleFactor(h1xl2)
                val h2xl1d = scaleDiff09.divideByScaleFactor(h2xl1)
                val h1xl2r = h1xl2 - scaleDiff09.multiplyByScaleFactor(h1xl2d)
                val h2xl1r = h2xl1 - scaleDiff09.multiplyByScaleFactor(h2xl1d)
                val l1xl2r = l1xl2 - SCALE9F.multiplyByScaleFactor(l1xl2d)
                val h1xl2_h2xl1_l1xl1 = h1xl2r + h2xl1r + l1xl2d
                val h1xl2_h2xl1_l1xl1d = scaleDiff09.divideByScaleFactor(h1xl2_h2xl1_l1xl1)
                val h1xl2_h2xl1_l1xl1r = h1xl2_h2xl1_l1xl1 - scaleDiff09.multiplyByScaleFactorExact(h1xl2_h2xl1_l1xl1d)

                val h1xh2 = multiplyLong(h1, h2) //checked
                //add it all up now, every operation checked
                var result = scaleDiff18.multiplyByScaleFactorExact(h1xh2)
                result = addLong(result, h1xl2d)
                result = addLong(result, h2xl1d)
                result = addLong(
                    result,
                    scaleDiff09.divideByScaleFactor(h1xl2r + h2xl1r + l1xl2d)
                ) //inner sum cannot overflow

                val remainder = SCALE9F.multiplyByScaleFactor(h1xl2_h2xl1_l1xl1r) + l1xl2r //cannot overflow
                return addLong(
                    result,
                    Rounding.calculateRoundingIncrement(rounding, result, remainder, scaleMetrics2.getScaleFactor()).toLong()
                )
            }
        } catch (e: ArithmeticException) {
            rethrowIfRoundingNecessary(e)
            throw newArithmeticExceptionWithCause(
                "Overflow: " + scaleMetrics1.toString(uDecimal1) + " * " + scaleMetrics2.toString(
                    uDecimal2
                ), e
            )
        }
    }
}
