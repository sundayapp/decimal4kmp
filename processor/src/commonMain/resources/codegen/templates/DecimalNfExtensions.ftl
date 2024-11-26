package org.decimal4j.immutable

import org.decimal4j.api.DecimalArithmeticExtensions.fromBigDecimal
import org.decimal4j.api.DecimalArithmeticExtensions.fromBigInteger
import org.decimal4j.immutable.Decimal${scale}f.Companion.valueOfUnscaled
import org.decimal4j.immutable.Decimal${scale}f.Companion.DEFAULT_CHECKED_ARITHMETIC
import org.decimal4j.immutable.Decimal${scale}f.Companion.METRICS
import org.decimal4j.truncate.RoundingMode
import java.math.BigInteger
import java.math.BigDecimal

object Decimal${scale}fExtensions {
    /**
     * Returns a {@code Decimal${scale}f} whose value is numerically equal to that of
     * the specified {@link BigInteger} value. An exception is thrown if the
     * specified value is too large to be represented as a {@code Decimal${scale}f}.
     *
     * @param value
     *            {@code BigInteger} value to convert into a {@code Decimal${scale}f}
     * @return a {@code Decimal${scale}f} value numerically equal to the specified big
     *         integer value
     * @throws IllegalArgumentException
     *             if {@code value} is too large to be represented as a {@code Decimal${scale}f}
     */
    fun Decimal${scale}f.Companion.valueOf(value: BigInteger): Decimal${scale}f {
        return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromBigInteger(value));
    }

    /**
    * Returns a {@code Decimal${scale}f} whose value is calculated by rounding
    * the specified {@link BigDecimal} argument to scale ${scale} using
    * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the
    * specified value is too large to be represented as a {@code Decimal${scale}f}.
    *
    * @param value
    *            {@code BigDecimal} value to convert into a {@code Decimal${scale}f}
    * @return a {@code Decimal${scale}f} calculated as: <code>round<sub>HALF_UP</sub>(value)</code>
    * @throws IllegalArgumentException
    *             if {@code value} is too large to be represented as a {@code Decimal${scale}f}
    */
    fun Decimal${scale}f.Companion.valueOf(value: BigDecimal): Decimal${scale}f {
        return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromBigDecimal(value));
    }

    /**
    * Returns a {@code Decimal${scale}f} whose value is calculated by rounding
    * the specified {@link BigDecimal} argument to scale ${scale} using
    * the specified {@code roundingMode}. An exception is thrown if the
    * specified value is too large to be represented as a {@code Decimal${scale}f}.
    *
    * @param value
    *            {@code BigDecimal} value to convert into a {@code Decimal${scale}f}
    * @param roundingMode
    *            the rounding mode to apply during the conversion if necessary
    * @return a {@code Decimal${scale}f} calculated as: <code>round(value)</code>
    * @throws IllegalArgumentException
    *             if {@code value} is too large to be represented as a {@code Decimal${scale}f}
    * @throws ArithmeticException
    *             if {@code roundingMode==UNNECESSARY} and rounding is
    *             necessary
    */
    fun Decimal${scale}f.Companion.valueOf(value: BigDecimal, roundingMode: RoundingMode): Decimal${scale}f {
        return valueOfUnscaled(METRICS.getCheckedArithmetic(roundingMode).fromBigDecimal(value));
    }

}