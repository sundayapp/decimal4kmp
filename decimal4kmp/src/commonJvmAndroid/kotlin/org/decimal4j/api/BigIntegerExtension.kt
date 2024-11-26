package org.decimal4j.api

import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.RoundingMode
import java.math.BigInteger

object BigIntegerExtension {

    /**
     * Converts this `Decimal` to a `BigInteger`. This conversion is analogous to the *narrowing primitive
     * conversion* from `double` to `long` as defined in section 5.1.3 of <cite>The Java Language
     * Specification</cite>: any fractional part of this `Decimal` will be discarded. Note that this conversion
     * can lose information about the precision of the `Decimal` value.
     *
     *
     * To have an exception thrown if the conversion is inexact (in other words if a nonzero fractional part is
     * discarded), use the [.toBigIntegerExact] method.
     *
     *
     * Note that this method uses [RoundingMode.DOWN] to be consistent with other integer conversion methods as
     * defined by <cite>The JavaLanguage Specification</cite>. Other rounding modes are supported via
     * [.toBigInteger].
     *
     * @return this `Decimal` converted to a `BigInteger`.
     * @see .toBigIntegerExact
     * @see .toBigInteger
     * @see .longValue
     */
    fun <S: ScaleMetrics> Decimal<S>.toBigInteger(): BigInteger {
        return BigInteger.valueOf(toLong())
    }

    /**
     * Converts this `Decimal` to a [BigInteger] value using the specified rounding mode if necessary.
     * Rounding is applied if the Decimal value can not be represented as a `BigInteger`, that is, if it has a
     * nonzero fractional part. Note that this conversion can lose information about the precision of the
     * `Decimal` value.
     *
     * @param roundingMode
     * the rounding mode to apply when rounding is necessary to convert this Decimal into a
     * `BigInteger`
     * @return this `Decimal` converted to a `BigInteger`.
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     * @see .toBigInteger
     * @see .toBigIntegerExact
     */
    fun <S: ScaleMetrics> Decimal<S>.toBigInteger(roundingMode: RoundingMode): BigInteger {
        return BigInteger.valueOf(longValue(roundingMode))
    }

    /**
     * Converts this `Decimal` to a `BigInteger`, checking for lost information. An exception is thrown if
     * this `Decimal` has a nonzero fractional part.
     *
     * @return this `Decimal` converted to a `BigInteger`.
     * @throws ArithmeticException
     * if `this` has a nonzero fractional part.
     * @see .toBigInteger
     * @see .toBigInteger
     * @see .longValueExact
     */
    fun <S: ScaleMetrics> Decimal<S>.toBigIntegerExact(): BigInteger {
        return BigInteger.valueOf(longValueExact())
    }


}