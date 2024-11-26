package org.decimal4j.factory

import org.decimal4j.api.ImmutableDecimal
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.RoundingMode
import java.math.BigDecimal
import java.math.BigInteger

interface DecimalFactoryJvm<S : ScaleMetrics>: DecimalFactory<S> {

    /**
     * Returns a new immutable Decimal whose value is numerically equal to that
     * of the specified [BigInteger] value. An exception is thrown if the
     * specified value is too large to be represented as a Decimal of this
     * factory's [scale][.getScale].
     *
     * @param value
     * `BigInteger` value to convert into an immutable Decimal
     * value
     * @return a Decimal value numerically equal to the specified big integer
     * value
     * @throws IllegalArgumentException
     * if `value` is too large to be represented as a Decimal
     * with the scale of this factory
     */
    fun valueOf(value: BigInteger): ImmutableDecimal<S>

    /**
     * Returns a new immutable Decimal whose value is calculated by rounding the
     * specified [BigDecimal] argument to the [scale][.getScale] of
     * this factory using [HALF_UP][RoundingMode.HALF_UP] rounding. An
     * exception is thrown if the specified value is too large to be represented
     * as a Decimal of this factory's scale.
     *
     * @param value
     * `BigDecimal` value to convert into an immutable Decimal
     * value
     * @return a Decimal calculated as: `round<sub>HALF_UP</sub>(value)`
     * @throws IllegalArgumentException
     * if `value` too large to be represented as a Decimal
     * with the scale of this factory
     */
    fun valueOf(value: BigDecimal): ImmutableDecimal<S>


    /**
     * Returns a new immutable Decimal whose value is calculated by rounding the
     * specified [BigDecimal] argument to the [scale][.getScale] of
     * this factory using the specified `roundingMode`. An exception is
     * thrown if the specified value is too large to be represented as a Decimal
     * of this factory's scale.
     *
     * @param value
     * `BigDecimal` value to convert into an immutable Decimal
     * value
     * @param roundingMode
     * the rounding mode to apply during the conversion if necessary
     * @return a Decimal calculated as: `round(value)`
     * @throws IllegalArgumentException
     * if `value` too large to be represented as a Decimal
     * with the scale of this factory
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is
     * necessary
     */
    fun valueOf(value: BigDecimal, roundingMode: RoundingMode): ImmutableDecimal<S>
}

