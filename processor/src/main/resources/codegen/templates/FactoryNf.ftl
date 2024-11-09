package org.decimal4j.factory;

import org.decimal4j.api.Decimal
import org.decimal4j.factory.Factories.getDecimalFactory
import org.decimal4j.immutable.Decimal${scale}f
import org.decimal4j.mutable.MutableDecimal${scale}f
import org.decimal4j.scale.Scale${scale}f
import org.decimal4j.scale.ScaleMetrics
import java.math.BigDecimal
import java.math.BigInteger
import org.decimal4j.api.RoundingMode
import kotlin.reflect.KClass

/**
 * The factory for decimals with scale ${scale} creating {@link Decimal${scale}f} and
 * {@link MutableDecimal${scale}f} instances.
 */
enum class Factory${scale}f : DecimalFactory<Scale${scale}f> {

	/**
	 * Singleton factory instance for immutable and mutable decimals with scale ${scale}.
	 */
	INSTANCE;

	override val scaleMetrics = Scale${scale}f.INSTANCE

	override val scale = Scale${scale}f.SCALE

	override fun immutableType(): KClass<Decimal${scale}f> {
		return Decimal${scale}f::class
	}

	override fun mutableType(): KClass<MutableDecimal${scale}f> {
		return MutableDecimal${scale}f::class
	}

	override fun deriveFactory(scale: Int): DecimalFactory<*> {
		return getDecimalFactory(scale);
	}

	override fun <S : ScaleMetrics> deriveFactory(scaleMetrics: S): DecimalFactory<S> {
		return getDecimalFactory(scaleMetrics)
	}

	override fun valueOf(value: Long): Decimal${scale}f {
		return Decimal${scale}f.valueOf(value)
	}

	override fun valueOf(value: Float): Decimal${scale}f {
		return Decimal${scale}f.valueOf(value)
	}

	override fun valueOf(value: Float, roundingMode: RoundingMode): Decimal${scale}f {
		return Decimal${scale}f.valueOf(value, roundingMode)
	}

	override fun valueOf(value: Double): Decimal${scale}f {
		return Decimal${scale}f.valueOf(value)
	}

	override fun valueOf(value: Double, roundingMode: RoundingMode): Decimal${scale}f {
		return Decimal${scale}f.valueOf(value, roundingMode)
	}

	override fun valueOf(value: BigInteger): Decimal${scale}f {
		return Decimal${scale}f.valueOf(value)
	}

	override fun valueOf(value: BigDecimal): Decimal${scale}f {
		return Decimal${scale}f.valueOf(value)
	}

	override fun valueOf(value: BigDecimal, roundingMode: RoundingMode): Decimal${scale}f {
		return Decimal${scale}f.valueOf(value, roundingMode)
	}

	override fun valueOf(value: Decimal<*>): Decimal${scale}f {
		return Decimal${scale}f.valueOf(value)
	}

	override fun valueOf(value: Decimal<*>, roundingMode: RoundingMode): Decimal${scale}f {
		return Decimal${scale}f.valueOf(value, roundingMode)
	}

	override fun parse(value: String): Decimal${scale}f {
		return Decimal${scale}f.valueOf(value)
	}

	override fun parse(value: String, roundingMode: RoundingMode): Decimal${scale}f {
		return Decimal${scale}f.valueOf(value, roundingMode)
	}

	override fun valueOfUnscaled(unscaledValue: Long): Decimal${scale}f {
		return Decimal${scale}f.valueOfUnscaled(unscaledValue)
	}

	override fun valueOfUnscaled(unscaledValue: Long, scale: Int): Decimal${scale}f {
		return Decimal${scale}f.valueOfUnscaled(unscaledValue, scale)
	}

	override fun valueOfUnscaled(unscaledValue: Long, scale: Int, roundingMode: RoundingMode): Decimal${scale}f {
		return Decimal${scale}f.valueOfUnscaled(unscaledValue, scale, roundingMode)
	}

	override fun newArray(length: Int): Array<Decimal${scale}f?> {
		return arrayOfNulls(length)
	}

	override fun newMutable(): MutableDecimal${scale}f {
		return MutableDecimal${scale}f()
	}

	override fun newMutableArray(length: Int): Array<MutableDecimal${scale}f?> {
		return arrayOfNulls(length)
	}
}