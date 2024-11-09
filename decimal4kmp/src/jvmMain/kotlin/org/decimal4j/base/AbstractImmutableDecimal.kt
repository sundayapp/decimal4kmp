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
package org.decimal4j.base

import org.decimal4j.api.Decimal
import org.decimal4j.api.ImmutableDecimal
import org.decimal4j.arithmetic.Exceptions.newArithmeticExceptionWithCause
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales
import org.decimal4j.api.RoundingMode

/**
 * Base class for immutable [Decimal] classes of different scales.
 *
 * @param <S>
 * the scale metrics type associated with this decimal
 * @param <D>
 * the concrete class implementing this `ImmutableDecimal`
</D></S> */
abstract class AbstractImmutableDecimal<S : ScaleMetrics, D : AbstractImmutableDecimal<S, D>>
/**
 * Constructor with unscaled value.
 *
 * @param unscaled
 * the unscaled value
 */(private val unscaled: Long) : AbstractDecimal<S, D>(), ImmutableDecimal<S> {
    /*Used to store the string representation, if computed */
    @Transient
    private var stringCache: String? = null

    override fun unscaledValue(): Long {
        return unscaled
    }

    override fun scale(scale: Int): ImmutableDecimal<*> {
        return scale(scale, RoundingMode.HALF_UP)
    }

    override fun <S : ScaleMetrics> scale(scaleMetrics: S): ImmutableDecimal<S> {
        return scale(scaleMetrics, RoundingMode.HALF_UP)
    }

    override fun scale(scale: Int, roundingMode: RoundingMode): ImmutableDecimal<*> {
        val myScale = this.scale
        if (scale == myScale) {
            return this
        }
        val targetMetrics = Scales.getScaleMetrics(scale)
        try {
            val targetUnscaled = targetMetrics.getArithmetic(roundingMode).fromUnscaled(unscaled, myScale)
            return factory.deriveFactory(targetMetrics).valueOfUnscaled(targetUnscaled)
        } catch (e: IllegalArgumentException) {
            throw newArithmeticExceptionWithCause(
                "Overflow: cannot convert $this to scale $scale",
                e
            )
        }
    }

    override fun <S : ScaleMetrics> scale(scaleMetrics: S, roundingMode: RoundingMode): ImmutableDecimal<S> {
        if (scaleMetrics === this.scaleMetrics) {
            val self = this as ImmutableDecimal<S>
            return self
        }
        try {
            val targetUnscaled = scaleMetrics!!.getArithmetic(roundingMode).fromUnscaled(unscaled, scale)
            return factory.deriveFactory(scaleMetrics).valueOfUnscaled(targetUnscaled)
        } catch (e: IllegalArgumentException) {
            throw newArithmeticExceptionWithCause(
                "Overflow: cannot convert " + this + " to scale " + scaleMetrics!!.getScale(), e
            )
        }
    }

    override fun multiplyExact(multiplicand: Decimal<*>): ImmutableDecimal<*> {
        val targetScale = scale + multiplicand.scale
        require(targetScale <= Scales.MAX_SCALE) {
            ("sum of scales in exact multiplication exceeds max scale "
                    + Scales.MAX_SCALE + ": " + this + " * " + multiplicand)
        }
        try {
            val unscaledProduct = defaultCheckedArithmetic.multiplyByLong(
                unscaled,
                multiplicand.unscaledValue()
            )
            return factory.deriveFactory(targetScale).valueOfUnscaled(unscaledProduct)
        } catch (e: ArithmeticException) {
            throw ArithmeticException("Overflow: $this * $multiplicand")
        }
    }

    override fun min(`val`: ImmutableDecimal<S>): ImmutableDecimal<S> {
        return if (isLessThanOrEqualTo(`val`)) this else `val`
    }

    override fun max(`val`: ImmutableDecimal<S>): ImmutableDecimal<S> {
        return if (isGreaterThanOrEqualTo(`val`)) this else `val`
    }

    override fun toString(): String {
        var s = stringCache
        if (s == null) {
            s = defaultArithmetic.toString(unscaledValue())
            stringCache = s
        }
        return s!!
    }
}
