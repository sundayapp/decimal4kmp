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
import org.decimal4j.base.AbstractMutableDecimal
import org.decimal4j.factory.Factories
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales

/**
 * `GenericMutableDecimal` is an [MutableDecimal] implemented in a
 * generic way, that is, different instances can have different scales. In
 * contrast the classes defined in the `mutable` package have have no
 * generic parameter as they have a fixed scale per class.
 *
 * @param <S>
 * the scale metrics type associated with this Decimal
</S> */
class GenericMutableDecimal<S : ScaleMetrics> constructor(override val scaleMetrics: S, unscaledValue: Long = 0) :
    AbstractMutableDecimal<S, GenericMutableDecimal<S>>(unscaledValue) {


    /**
     * Creates a new `GenericMutableDecimal` with the same value and scale
     * as the given `decimal` argument.
     *
     * @param decimal
     * the numeric value to assign to the created mutable Decimal
     */
    constructor(decimal: Decimal<S>) : this(decimal.scaleMetrics, decimal.unscaledValue())

    /**
     * Creates a new `GenericMutableDecimal` with the scale specified by
     * the given `scaleMetrics` argument. The numeric value of new the
     * Decimal is `unscaledValue  10<sup>-scale</sup>`
     *
     * @param scaleMetrics
     * the metrics object defining the scale for the new value
     * @param unscaledValue
     * the unscaled long value representing the new Decimal's
     * numerical value before applying the scale factor
     */

    override fun create(unscaled: Long): GenericMutableDecimal<S> {
        return GenericMutableDecimal(scaleMetrics, unscaled)
    }

    override fun createArray(length: Int): Array<GenericMutableDecimal<S>?> {
        return arrayOfNulls(length)
    }

    override fun self(): GenericMutableDecimal<S> {
        return this
    }

    override val scale: Int = scaleMetrics.getScale()

    override val factory: GenericDecimalFactory<S>
        get() = Factories.getGenericDecimalFactory(scaleMetrics)

    override fun clone(): GenericMutableDecimal<S> {
        return GenericMutableDecimal(scaleMetrics, unscaledValue())
    }

    override fun toImmutableDecimal(): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(this)
    }

    override fun toMutableDecimal(): GenericMutableDecimal<S> {
        return this
    }

    companion object {
        private const val serialVersionUID = 1L

        /**
         * Creates and returns a new `GenericMutableDecimal` with the same
         * value and scale as the given `decimal` argument.
         *
         * @param decimal
         * the numeric value to assign to the created mutable Decimal
         * @param <S>
         * the scale metrics type
         * @return a new generic mutable Decimal value with scale and value copied
         * from the `decimal` argument
        </S> */
        fun <S : ScaleMetrics> valueOf(decimal: Decimal<S>): GenericMutableDecimal<S> {
            return GenericMutableDecimal(decimal)
        }

        /**
         * Creates and returns a new `GenericMutableDecimal` with the scale
         * specified by the given `scaleMetrics` argument. The numeric value
         * of new the Decimal is
         * `unscaledValue  10<sup>-scale</sup>`
         *
         * @param scaleMetrics
         * the metrics object defining the scale for the new value
         * @param unscaled
         * the unscaled long value representing the new Decimal's
         * numerical value before applying the scale factor
         * @param <S>
         * the scale metrics type
         * @return a new Decimal value representing
         * `unscaledValue  10<sup>-scale</sup>`
        </S> */
        fun <S : ScaleMetrics> valueOfUnscaled(scaleMetrics: S, unscaled: Long): GenericMutableDecimal<S> {
            return GenericMutableDecimal(scaleMetrics, unscaled)
        }

        /**
         * Creates and returns a new `GenericMutableDecimal` with the
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
        fun valueOfUnscaled(scale: Int, unscaled: Long): GenericMutableDecimal<*> {
            return valueOfUnscaled(Scales.getScaleMetrics(scale), unscaled)
        }
    }
}
