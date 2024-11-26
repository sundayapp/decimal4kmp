package org.decimal4j.api

import org.decimal4j.api.DecimalArithmeticExtensions.toBigDecimal
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.RoundingMode
import java.math.BigDecimal

object BigDecimalExtensions {
    /**
     * Converts this `Decimal` to a `BigDecimal` using the same [scale][.getScale] as this Decimal
     * value.
     *
     * @return this `Decimal` converted to a `BigDecimal` with the same scale as this Decimal value.
     * @see .toBigDecimal
     */
    fun <S: ScaleMetrics> Decimal<S>.toBigDecimal(): BigDecimal {
        return defaultArithmetic.toBigDecimal(unscaledValue())
    }

    /**
     * Returns a `BigDecimal` value of the given scale using the specified rounding mode if necessary.
     *
     * @param scale
     * the scale used for the returned `BigDecimal`
     * @param roundingMode
     * the rounding mode to apply when rounding is necessary to convert from the this Decimal's
     * [scale][.getScale] to the target scale
     * @return a `BigDecimal` instance of the specified scale
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     * @see .toBigDecimal
     */
    fun <S: ScaleMetrics> Decimal<S>.toBigDecimal(scale: Int, roundingMode: RoundingMode): BigDecimal {
        return getArithmeticFor(roundingMode).toBigDecimal(unscaledValue(), scale)
    }
}