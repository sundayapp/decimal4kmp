package org.decimal4j.api

import org.decimal4j.arithmetic.BigDecimalConversion.bigDecimalToUnscaled
import org.decimal4j.arithmetic.BigDecimalConversion.unscaledToBigDecimal
import org.decimal4j.arithmetic.BigIntegerConversion.bigIntegerToUnscaled
import java.math.BigDecimal
import java.math.BigInteger

object DecimalArithmeticExtensions {

    /**
     * Converts the specified [BigInteger] value to an unscaled decimal. An exception is thrown if the specified
     * value is too large to be represented as a Decimal of this arithmetic's [scale][.getScale].
     *
     * @param value
     * the value to convert
     * @return the unscaled decimal representing the same value as the given big integer value
     * @throws IllegalArgumentException
     * if `value` is too large to be represented as a Decimal with the scale of this arithmetic
     */
    fun DecimalArithmetic.fromBigInteger(value: BigInteger): Long {
        return bigIntegerToUnscaled(scaleMetrics, value)
    }

    /**
     * Converts the specified [BigDecimal] value to an unscaled decimal. An exception is thrown if the specified
     * value is too large to be represented as a Decimal of this arithmetic's [scale][.getScale].
     *
     *
     * Note: this operation is **not** garbage free, meaning that new temporary objects may be allocated during the
     * conversion.
     *
     * @param value
     * the value to convert
     * @return the unscaled decimal representing the same value as the given big decimal value
     * @throws IllegalArgumentException
     * if `value` is too large to be represented as a Decimal with the scale of this arithmetic
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary
     */
    fun DecimalArithmetic.fromBigDecimal(value: BigDecimal): Long {
        return bigDecimalToUnscaled(scaleMetrics, roundingMode, value)
    }

    /**
     * Converts the specified unscaled decimal value into a [BigDecimal] value using this arithmetic's
     * [scale][.getScale] for the result value.
     *
     *
     * Note: this operation is **not** strictly garbage free since the result value is usually allocated; however no
     * temporary objects other than the result are allocated during the conversion.
     *
     * @param uDecimal
     * the unscaled decimal value to convert into a `BigDecimal` value
     * @return the `uDecimal` value converted into a `BigDecimal` value
     */
    fun DecimalArithmetic.toBigDecimal(uDecimal: Long): BigDecimal {
        return unscaledToBigDecimal(scaleMetrics, uDecimal)
    }

    /**
     * Converts the specified unscaled decimal value into a [BigDecimal] value using the specified `scale`
     * for the result value. The arithmetic's [rounding mode][.getRoundingMode] is applied if rounding is
     * necessary.
     *
     *
     * Note: this operation is **not** garbage free since the result value is usually allocated and also temporary
     * objects may be allocated during the conversion. Note however that temporary objects are only allocated if the
     * unscaled value of the result exceeds the range of a long value.
     *
     * @param uDecimal
     * the unscaled decimal value to convert into a `BigDecimal` value
     * @param scale
     * the scale to use for the resulting `BigDecimal` value
     * @return the `uDecimal` value converted into a `BigDecimal` value, possibly rounded or truncated
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary
     */
    fun DecimalArithmetic.toBigDecimal(uDecimal: Long, scale: Int): BigDecimal {
        return unscaledToBigDecimal(scaleMetrics, this.roundingMode, uDecimal, scale)
    }
}