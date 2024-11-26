package org.decimal4j.api

import org.decimal4j.api.DecimalArithmeticExtensions.fromBigDecimal
import org.decimal4j.api.DecimalArithmeticExtensions.fromBigInteger
import org.decimal4j.base.AbstractMutableDecimal
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.RoundingMode
import java.math.BigDecimal
import java.math.BigInteger

object MutableDecimalJvm {

    /**
     * Sets `this` Decimal to the specified `value` and returns
     * `this` now representing `value`. The specified
     * [BigDecimal] argument is rounded to the [scale][.getScale]
     * of this mutable Decimal using [RoundingMode.HALF_UP] rounding. An
     * exception is thrown if the specified value is too large to be represented
     * as a Decimal of this mutable Decimal's scale.
     *
     * @param value
     * value to be set
     * @return `this` Decimal after assigning:
     * `round<sub>HALF_UP</sub>(value)`
     * @throws IllegalArgumentException
     * if `value` is too large to be represented as a Decimal
     * with the scale of this mutable Decimal
     */
    fun <S: ScaleMetrics> MutableDecimal<S>.set(value: BigDecimal): MutableDecimal<S> {
        if (this is AbstractMutableDecimal<S, *>) {
            this.unscaled = this.defaultCheckedArithmetic.fromBigDecimal(value)
            return this.self() as MutableDecimal<S>
        }
        throw UnsupportedOperationException("Unsupported mutable decimal type: ${this::class.simpleName}")

    }

    /**
     * Sets `this` Decimal to the specified `value` and returns
     * `this` now representing `value`. The specified
     * [BigDecimal] argument is rounded to the [scale][.getScale]
     * of this mutable Decimal using the specified `roundingMode`. An
     * exception is thrown if the specified value is too large to be represented
     * as a Decimal of this mutable Decimal's scale.
     *
     * @param value
     * value to be set
     * @param roundingMode
     * the rounding mode to apply if rounding is necessary
     * @return `this` Decimal after assigning: `round(value)`
     * @throws IllegalArgumentException
     * if `value` is too large to be represented as a Decimal
     * with the scale of this mutable Decimal
     * @throws ArithmeticException
     * if `roundingMode` is [             UNNESSESSARY][RoundingMode.UNNECESSARY] and rounding is necessary
     */
    fun <S: ScaleMetrics> MutableDecimal<S>.set(value: BigDecimal, roundingMode: RoundingMode): MutableDecimal<S> {
        if (this is AbstractMutableDecimal<S, *>) {
            unscaled = getCheckedArithmeticFor(roundingMode).fromBigDecimal(value)
            return self() as MutableDecimal<S>
        }
        throw UnsupportedOperationException("Unsupported mutable decimal type: ${this::class.simpleName}")
    }

    /**
     * Sets `this` Decimal to the specified `value` and returns
     * `this` now representing `value`. An exception is thrown if
     * the specified value is too large to be represented as a Decimal of this
     * mutable Decimal's scale.
     *
     * @param value
     * value to be set
     * @return `this` Decimal after assigning the given `value`
     * @throws IllegalArgumentException
     * if `value` is too large to be represented as a Decimal
     * with the scale of this mutable Decimal
     */
    fun <S: ScaleMetrics> MutableDecimal<S>.set(value: BigInteger): MutableDecimal<S> {
        if (this is AbstractMutableDecimal<S, *>) {
            unscaled = defaultCheckedArithmetic.fromBigInteger(value)
            return self() as MutableDecimal<S>
        }
        throw UnsupportedOperationException("Unsupported mutable decimal type: ${this::class.simpleName}")
    }
}