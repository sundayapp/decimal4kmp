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
package org.decimal4j.generic

import org.decimal4j.api.Decimal
import org.decimal4j.base.AbstractImmutableDecimal
import org.decimal4j.factory.Factories
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales

/**
 * `GenericImmutableDecimal` is an [ImmutableDecimal] implemented
 * in a generic way, that is, different instances can have different scales. In
 * contrast the classes defined in the `immutable` package have have no
 * generic parameter as they have a fixed scale per class.
 *
 * @param <S>
 * the scale metrics type associated with this decimal
</S> */
class GenericImmutableDecimal<S : ScaleMetrics>(override val scaleMetrics: S, unscaledValue: Long) :
    AbstractImmutableDecimal<S, GenericImmutableDecimal<S>>(unscaledValue) {

    /**
     * Creates a new `GenericImmutableDecimal` with the same value and scale
     * as the given `decimal` argument.
     *
     * @param decimal
     * the numeric value to assign to the created immutable Decimal
     */
    constructor(decimal: Decimal<S>) : this(decimal.scaleMetrics, decimal.unscaledValue())

    override val scale: Int
        get() = scaleMetrics.getScale()

    override val factory: GenericDecimalFactory<S>
        get() = Factories.getGenericDecimalFactory(scaleMetrics)

    override fun self(): GenericImmutableDecimal<S> {
        return this
    }

    override fun createOrAssign(unscaled: Long): GenericImmutableDecimal<S> {
        return if (unscaledValue() == unscaled) this else create(unscaled)
    }

    override fun create(unscaled: Long): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(scaleMetrics, unscaled)
    }

    override fun createArray(length: Int): Array<GenericImmutableDecimal<S>?> {
        return arrayOfNulls(length)
    }

    override fun toMutableDecimal(): GenericMutableDecimal<S> {
        return GenericMutableDecimal<S>(this)
    }

    override fun toImmutableDecimal(): GenericImmutableDecimal<S> {
        return this
    }

    companion object {
        private const val serialVersionUID = 1L

        /**
         * Creates and returns a new `GenericImmutableDecimal` with the same
         * value and scale as the given `decimal` argument.
         *
         * @param decimal
         * the numeric value to assign to the created immutable Decimal
         * @param <S> the scale metrics type
         * @return a new generic immutable Decimal value with scale and value copied
         * from the `decimal` argument
        </S> */
        fun <S : ScaleMetrics> valueOf(decimal: Decimal<S>): GenericImmutableDecimal<S> {
            return GenericImmutableDecimal(decimal)
        }

        /**
         * Creates and returns a new `GenericImmutableDecimal` with the scale
         * specified by the given `scaleMetrics` argument. The numeric value
         * of new the Decimal is
         * `unscaledValue  10<sup>-scale</sup>`
         *
         * @param scaleMetrics
         * the metrics object defining the scale for the new value
         * @param unscaled
         * the unscaled long value representing the new Decimal's
         * numerical value before applying the scale factor
         * @param <S> the scale metrics type
         * @return a new Decimal value representing
         * `unscaledValue  10<sup>-scale</sup>`
        </S> */
        fun <S : ScaleMetrics> valueOfUnscaled(scaleMetrics: S, unscaled: Long): GenericImmutableDecimal<S> {
            return GenericImmutableDecimal(scaleMetrics, unscaled)
        }

        /**
         * Creates and returns a new `GenericImmutableDecimal` with the
         * specified `scale` and value. The numeric value of new the Decimal
         * is `unscaledValue  10<sup>-scale</sup>`
         *
         * @param scale
         * the scale for the new value
         * @param unscaled
         * the unscaled long value representing the new Decimal's
         * numerical value before applying the scale factor
         * @return a new Decimal value representing
         * `unscaledValue  10<sup>-scale</sup>`
         */
        fun valueOfUnscaled(scale: Int, unscaled: Long): GenericImmutableDecimal<*> {
            return valueOfUnscaled(Scales.getScaleMetrics(scale), unscaled)
        }
    }
}
