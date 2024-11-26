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
import org.decimal4j.api.MutableDecimal
import org.decimal4j.factory.DecimalFactory
import org.decimal4j.factory.Factories
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.RoundingMode
import kotlin.reflect.KClass

/**
 * `GenericDecimalFactory` is a [DecimalFactory] for generic
 * decimal values. Different generic decimal instances of the same class can
 * have different scales as opposed to the decimal values defined in the
 * `immutable` and `mutable` packages.
 *
 *
 * The recommended way to create a generic factory is via
 * [Factories.getGenericDecimalFactory].
 *
 * @param <S>
 * the scale metrics type associated with this decimal
 * @see GenericImmutableDecimal
 *
 * @see GenericMutableDecimal
</S> */
open class GenericDecimalFactory<S : ScaleMetrics>(override val scaleMetrics: S) :
    DecimalFactory<S> {

    override val  scale = scaleMetrics.getScale()

    override fun immutableType(): KClass<out GenericImmutableDecimal<S>> {
        return GenericImmutableDecimal::class as KClass<out GenericImmutableDecimal<S>>
    }

    override fun mutableType(): KClass<out MutableDecimal<S>> {
        return GenericMutableDecimal::class as KClass<out GenericMutableDecimal<S>>
    }

    override fun deriveFactory(scale: Int): GenericDecimalFactory<*> {
        return Factories.getGenericDecimalFactory(scale)
    }

    override fun <T : ScaleMetrics> deriveFactory(scaleMetrics: T): GenericDecimalFactory<T> {
        return Factories.getGenericDecimalFactory(scaleMetrics)
    }

    override fun valueOf(value: Long): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(scaleMetrics, scaleMetrics.getDefaultCheckedArithmetic().fromLong(value))
    }

    override fun valueOf(value: Float): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(scaleMetrics, scaleMetrics.getDefaultCheckedArithmetic().fromFloat(value))
    }

    override fun valueOf(value: Float, roundingMode: RoundingMode): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(scaleMetrics, scaleMetrics.getCheckedArithmetic(roundingMode).fromFloat(value))
    }

    override fun valueOf(value: Double): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(scaleMetrics, scaleMetrics.getDefaultCheckedArithmetic().fromDouble(value))
    }

    override fun valueOf(value: Double, roundingMode: RoundingMode): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(
            scaleMetrics,
            scaleMetrics.getCheckedArithmetic(roundingMode).fromDouble(value)
        )
    }

    override fun valueOf(value: Decimal<*>): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(
            scaleMetrics, scaleMetrics.getDefaultCheckedArithmetic().fromUnscaled(
                value.unscaledValue(), value.scale
            )
        )
    }

    override fun valueOf(value: Decimal<*>, roundingMode: RoundingMode): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(
            scaleMetrics, scaleMetrics.getCheckedArithmetic(roundingMode).fromUnscaled(
                value.unscaledValue(), value.scale
            )
        )
    }

    override fun parse(value: String): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(scaleMetrics, scaleMetrics.getDefaultCheckedArithmetic().parse(value))
    }

    override fun parse(value: String, roundingMode: RoundingMode): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(scaleMetrics, scaleMetrics.getCheckedArithmetic(roundingMode).parse(value))
    }

    override fun valueOfUnscaled(unscaled: Long): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(scaleMetrics, unscaled)
    }

    override fun valueOfUnscaled(unscaledValue: Long, scale: Int): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(
            scaleMetrics, scaleMetrics.getDefaultCheckedArithmetic().fromUnscaled(
                unscaledValue, scale
            )
        )
    }

    override fun valueOfUnscaled(
        unscaledValue: Long,
        scale: Int,
        roundingMode: RoundingMode
    ): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(
            scaleMetrics, scaleMetrics.getCheckedArithmetic(roundingMode).fromUnscaled(
                unscaledValue, scale
            )
        )
    }

    override fun newArray(length: Int): Array<GenericImmutableDecimal<S>?> {
        return arrayOfNulls(length)
    }

    override fun newMutable(): GenericMutableDecimal<S> {
        return GenericMutableDecimal(scaleMetrics)
    }

    override fun newMutableArray(length: Int): Array<GenericMutableDecimal<S>?> {
        return arrayOfNulls(length)
    }
}