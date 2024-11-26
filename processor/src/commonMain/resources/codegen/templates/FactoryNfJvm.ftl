package org.decimal4j.factory

import org.decimal4j.api.Decimal
import org.decimal4j.api.ImmutableDecimal
import org.decimal4j.api.MutableDecimal
import org.decimal4j.immutable.Decimal${scale}f
import org.decimal4j.immutable.Decimal${scale}fExtensions.valueOf
import org.decimal4j.scale.Scale${scale}f
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.RoundingMode
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KClass

class Factory${scale}fJvm (private val factory: Factory${scale}f): DecimalFactoryJvm<Scale${scale}f> {

    override val scaleMetrics: Scale${scale}f = factory.scaleMetrics
    override val scale: Int = factory.scale

    override fun valueOf(value: BigInteger): ImmutableDecimal<Scale${scale}f> {
        return Decimal${scale}f.valueOf(value)
    }

    override fun valueOf(value: BigDecimal): ImmutableDecimal<Scale${scale}f> {
        return Decimal${scale}f.valueOf(value)
    }

    override fun valueOf(value: BigDecimal, roundingMode: RoundingMode): ImmutableDecimal<Scale${scale}f> {
        return Decimal${scale}f.valueOf(value, roundingMode)
    }

    override fun valueOf(value: Long): ImmutableDecimal<Scale${scale}f> {
        return factory.valueOf(value)
    }

    override fun valueOf(value: Double): ImmutableDecimal<Scale${scale}f> {
        return factory.valueOf(value)
    }

    override fun valueOf(value: Double, roundingMode: RoundingMode): ImmutableDecimal<Scale${scale}f> {
        return factory.valueOf(value, roundingMode)
    }

    override fun valueOfUnscaled(unscaled: Long): ImmutableDecimal<Scale${scale}f> {
        return factory.valueOfUnscaled(unscaled)
    }

    override fun valueOfUnscaled(unscaledValue: Long, scale: Int): ImmutableDecimal<Scale${scale}f> {
        return factory.valueOfUnscaled(unscaledValue, scale)
    }

    override fun parse(value: String): ImmutableDecimal<Scale${scale}f> {
        return factory.parse(value)
    }

    override fun parse(value: String, roundingMode: RoundingMode): ImmutableDecimal<Scale${scale}f> {
        return factory.parse(value, roundingMode)
    }

    override fun immutableType(): KClass<out ImmutableDecimal<Scale${scale}f>> = factory.immutableType()

    override fun valueOf(value: Float): ImmutableDecimal<Scale${scale}f> {
        return factory.valueOf(value)
    }

    override fun valueOf(value: Float, roundingMode: RoundingMode): ImmutableDecimal<Scale${scale}f> {
        return factory.valueOf(value, roundingMode)
    }

    override fun valueOf(value: Decimal<*>): ImmutableDecimal<Scale${scale}f> {
        return factory.valueOf(value)
    }

    override fun valueOf(value: Decimal<*>, roundingMode: RoundingMode): ImmutableDecimal<Scale${scale}f> {
        return factory.valueOf(value, roundingMode)
    }

    override fun valueOfUnscaled(
        unscaled: Long,
        scale: Int,
        roundingMode: RoundingMode
    ): ImmutableDecimal<Scale${scale}f> {
        return factory.valueOfUnscaled(unscaled, scale, roundingMode)
    }

    override fun mutableType(): KClass<out MutableDecimal<Scale${scale}f>> {
        return factory.mutableType()
    }

    override fun deriveFactory(scale: Int): DecimalFactory<*> {
        return factory.deriveFactory(scale)
    }

    override fun <S : ScaleMetrics> deriveFactory(scaleMetrics: S): DecimalFactory<S> {
        return factory.deriveFactory(scaleMetrics)
    }

    override fun newArray(length: Int): Array<out ImmutableDecimal<Scale${scale}f>?> {
        return factory.newArray(length)
    }

    override fun newMutable(): MutableDecimal<Scale${scale}f> {
        return factory.newMutable()
    }

    override fun newMutableArray(length: Int): Array<out MutableDecimal<Scale${scale}f>?> {
        return factory.newMutableArray(length)
    }
}