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
package org.decimal4j.api

import org.decimal4j.factory.DecimalFactory
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.OverflowMode
import org.decimal4j.truncate.TruncationPolicy
import java.math.BigDecimal
import java.math.BigInteger
import org.decimal4j.api.RoundingMode

/**
 * Signed fixed-point decimal number similar to [BigDecimal]. A Decimal number can be immutable or mutable and it
 * is based on an underlying *unscaled* long value and a fixed [scale][.getScale]. The scale defines the
 * number of digits to the right of the decimal point. If the scale is `f` then the value represented by a
 * `Decimal` instance is `(unscaledValue  10<sup>-f</sup>)`.
 *
 *
 * *Scale of Result and Operands* <br></br>
 * The result of an arithmetic operation is generally of the same scale as this Decimal unless otherwise indicated.
 * Decimal operands of arithmetic operations are typically also of the same scale as this Decimal. Scale compatibility
 * of Decimal operands is enforced through the generic [ScaleMetrics] parameter `<S>`.
 *
 *
 * *Operands involving Type Conversion* <br></br>
 * For convenience, arithmetic operations with other data types are sometimes also provided. Such operations usually
 * perform a value conversion into a Decimal of the current scale before performing the actual operation.
 *
 *
 * *Rounding* <br></br>
 * If the result of an arithmetic operation cannot be represented exactly in the scale of this Decimal, it is rounded to
 * the least significant digit. [HALF_UP][RoundingMode.HALF_UP] rounding is used by default if no other rounding
 * mode is explicitly specified. Note that in a few exceptional cases [HALF_EVEN][RoundingMode.HALF_EVEN] rounding
 * is used by default to comply with inherited specification constraints (e.g. see [.doubleValue],
 * [.floatValue] etc.). The documentation of operations which involve rounding indicate the rounding mode that
 * is applied.
 *
 *
 * *Overflows* <br></br>
 * Operations with Decimal values can lead to overflows in marked contrast to the [BigDecimal]. This is a direct
 * consequence of the construction of a Decimal value on the basis of a long value. Unless otherwise indicated
 * operations silently truncate overflows by default. This choice has been made for performance reasons and because Java
 * programmers are already familiar with this behavior from operations with primitive integer types. If this behavior is
 * inappropriate for an application, exceptions in overflow situations can be enforced through an optional
 * [OverflowMode] or [TruncationPolicy] argument. Some operations like conversion operations or arithmetic
 * operations involving conversion *always* throw an exception if an overflow occurs. The documentation of
 * operations which can cause an overflow always indicates the exact overflow behavior.
 *
 *
 * All methods for this interface throw `NullPointerException` when passed a `null` object reference for any
 * input parameter.
 *
 * @param <S>
 * the scale metrics type associated with this Decimal
</S> */
interface Decimal<S : ScaleMetrics> : Comparable<Decimal<S>> {
    /**
     * Returns the metrics associated with the scale of this Decimal. Scale defines the number of fraction digits and
     * the scale factor applied to the `long` value underlying this `Decimal`.
     *
     * @return the scale metrics object
     * @see ScaleMetrics.getScale
     * @see ScaleMetrics.getScaleFactor
     */
    val scaleMetrics: S

    /**
     * Returns the scale associated with this Decimal. The scale defines the number of fraction digits and the scale
     * factor applied to the `long` value underlying this `Decimal`.
     *
     *
     * If the scale is `f` then the value represented by a `Decimal` instance is
     * `(unscaledValue  10<sup>-f</sup>)`.
     *
     *
     * This method is a shortcut for `getScaleMetrics().getScale()`.
     *
     * @return the scale
     * @see .getScaleMetrics
     * @see ScaleMetrics.getScale
     * @see .unscaledValue
     */
    val scale: Int

    /**
     * Returns the unscaled value underlying this `Decimal`. This `Decimal` is
     * `(unscaledValue  10<sup>-f</sup>)` with `f` representing the [scale][.getScale], hence
     * the returned value equals `(10<sup>f</sup>  this)`.
     *
     * @return the unscaled numeric value, the same as this Decimal but without applying the scale factor
     * @see .getScale
     * @see ScaleMetrics.getScaleFactor
     */
    fun unscaledValue(): Long

    /**
     * Returns the factory that can be used to create other Decimal values of the same scale as `this` Decimal.
     *
     * @return the factory to create other Decimal values of the same scale as this Decimal
     */
    val factory: DecimalFactory<S>

    /**
     * Returns a `Decimal` whose value represents the integral part of `this` Decimal. The integral part
     * corresponds to digits at the left of the decimal point. The result is `this` Decimal rounded to precision
     * zero with [RoundingMode.DOWN].
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the operation.
     *
     * @return `this` for non-negative and `this` for negative
     * values
     * @see .fractionalPart
     * @see .isIntegral
     * @see .isIntegralPartZero
     * @see .round
     */
    fun integralPart(): Decimal<S>

    /**
     * Returns a `Decimal` whose value represents the fractional part of `(this)` value. The fractional part
     * corresponds to digits at the right of the decimal point. The result is `this` minus the integral part of
     * this Decimal.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the operation.
     *
     * @return `this-integralPart()` which is always less than one and greater than minus one
     * @see .integralPart
     * @see .isIntegral
     * @see .isIntegralPartZero
     */
    fun fractionalPart(): Decimal<S>

    // some methods "inherited" from Number and BigDecimal
    /**
     * Returns the value of this `Decimal` as a `byte` after a narrowing primitive conversion.
     *
     * @return this `Decimal` converted to an `byte`.
     * @see Number.byteValue
     * @see .byteValueExact
     */
    fun byteValue(): Byte

    /**
     * Converts this `Decimal` to a `byte`, checking for lost information. If this `Decimal` has a
     * nonzero fractional part or is out of the possible range for a `byte` result then an
     * `ArithmeticException` is thrown.
     *
     * @return this `Decimal` converted to a `byte`.
     * @throws ArithmeticException
     * if `this` has a nonzero fractional part, or will not fit in a `byte`.
     * @see .byteValue
     */
    fun byteValueExact(): Byte

    /**
     * Returns the value of this `Decimal` as a `short` after a narrowing primitive conversion.
     *
     * @return this `Decimal` converted to an `short`.
     * @see Number.shortValue
     * @see .shortValueExact
     */
    fun shortValue(): Short

    /**
     * Converts this `Decimal` to a `short`, checking for lost information. If this `Decimal` has a
     * nonzero fractional part or is out of the possible range for a `short` result then an
     * `ArithmeticException` is thrown.
     *
     * @return this `Decimal` converted to a `short`.
     * @throws ArithmeticException
     * if `this` has a nonzero fractional part, or will not fit in a `short`.
     * @see .shortValue
     */
    fun shortValueExact(): Short

    /**
     * Converts this `Decimal` to an `int`. This conversion is analogous to the *narrowing primitive
     * conversion* from `double` to `short` as defined in section 5.1.3 of <cite>The Java Language
     * Specification</cite>: any fractional part of this `Decimal` will be discarded, and if the resulting
     * "`long`" is too big to fit in an `int`, only the low-order 32 bits are returned. Note that this
     * conversion can lose information about the overall magnitude and precision of this `Decimal` value as well
     * as return a result with the opposite sign.
     *
     * @return this `Decimal` converted to an `int`.
     * @see Number.intValue
     * @see .intValueExact
     */
    fun intValue(): Int

    /**
     * Converts this `Decimal` to an `int`, checking for lost information. If this `Decimal` has a
     * nonzero fractional part or is out of the possible range for an `int` result then an
     * `ArithmeticException` is thrown.
     *
     * @return this `Decimal` converted to an `int`.
     * @throws ArithmeticException
     * if `this` has a nonzero fractional part, or will not fit in an `int`.
     * @see .intValue
     */
    fun intValueExact(): Int

    /**
     * Converts this `Decimal` to a `long`. This conversion is analogous to the *narrowing primitive
     * conversion* from `double` to `short` as defined in section 5.1.3 of <cite>The Java Language
     * Specification</cite>: any fractional part of this `Decimal` will be discarded. Note that this conversion
     * can lose information about the precision of the `Decimal` value.
     *
     *
     * Note that this method uses [RoundingMode.DOWN] as defined by <cite>The JavaLanguage
     * Specification</cite>. Other rounding modes are supported via [.longValue].
     *
     * @return this `Decimal` converted to a `long`.
     * @see Number.longValue
     * @see .longValue
     * @see .longValueExact
     */
    fun longValue(): Long

    /**
     * Converts this `Decimal` to a `long`, checking for lost information. If this `Decimal` has a
     * nonzero fractional part or is out of the possible range for a `long` result then an
     * `ArithmeticException` is thrown.
     *
     * @return this `Decimal` converted to a `long`.
     * @throws ArithmeticException
     * if `this` has a nonzero fractional part
     * @see .longValue
     * @see .longValue
     */
    fun longValueExact(): Long

    /**
     * Converts this `Decimal` to a `float`. This conversion is similar to the *narrowing primitive
     * conversion* from `double` to `float` as defined in section 5.1.3 of <cite>The Java Language
     * Specification</cite>. Note that this conversion can lose information about the precision of the `Decimal`
     * value.
     *
     *
     * Note that this method uses [RoundingMode.HALF_EVEN] as defined by <cite>The JavaLanguage
     * Specification</cite>. Other rounding modes are supported via [.floatValue].
     *
     * @return this `Decimal` converted to a `float`.
     * @see Number.floatValue
     * @see .floatValue
     */
    fun floatValue(): Float

    /**
     * Converts this `Decimal` to a `double`. This conversion is similar to the *narrowing primitive
     * conversion* from `double` to `float` as defined in section 5.1.3 of <cite>The Java Language
     * Specification</cite>. Note that this conversion can lose information about the precision of the `Decimal`
     * value.
     *
     *
     * Note that this method uses [RoundingMode.HALF_EVEN] as defined by <cite>The JavaLanguage
     * Specification</cite>. Other rounding modes are supported via [.doubleValue].
     *
     * @return this `Decimal` converted to a `double`.
     * @see Number.doubleValue
     * @see .doubleValue
     */
    fun doubleValue(): Double

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
    fun toBigInteger(): BigInteger

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
    fun toBigIntegerExact(): BigInteger

    /**
     * Converts this `Decimal` to a `BigDecimal` using the same [scale][.getScale] as this Decimal
     * value.
     *
     * @return this `Decimal` converted to a `BigDecimal` with the same scale as this Decimal value.
     * @see .toBigDecimal
     */
    fun toBigDecimal(): BigDecimal

    // mutable/immutable conversion methods
    /**
     * If this `Decimal` value is already an [ImmutableDecimal] it is simply returned. Otherwise a new
     * immutable value with the same scale and numerical value as `this` Decimal is created and returned.
     *
     * @return `this` if immutable and a new [ImmutableDecimal] with the same scale and value as
     * `this` Decimal otherwise
     */
    fun toImmutableDecimal(): ImmutableDecimal<S>

    /**
     * If this `Decimal` value is already a [MutableDecimal] it is simply returned. Otherwise a new mutable
     * value with the same scale and numerical value as `this` Decimal is created and returned.
     *
     * @return `this` if mutable and a new [MutableDecimal] with the same scale and value as `this`
     * Decimal otherwise
     */
    fun toMutableDecimal(): MutableDecimal<S>

    // some conversion methods with rounding mode
    /**
     * Converts this `Decimal` to a `long` using the specified rounding mode if necessary. Rounding is
     * applied if the Decimal value can not be represented as a long value, that is, if it has a nonzero fractional
     * part. Note that this conversion can lose information about the precision of the `Decimal` value.
     *
     * @param roundingMode
     * the rounding mode to apply when rounding is necessary to convert this Decimal into a long
     * @return this `Decimal` converted to a `long`.
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     * @see .longValue
     * @see .longValueExact
     */
    fun longValue(roundingMode: RoundingMode): Long

    /**
     * Converts this `Decimal` to a `float` using the specified rounding mode if the Decimal value can not
     * be exactly represented as a float value. Note that this conversion can lose information about the precision of
     * the `Decimal` value.
     *
     * @param roundingMode
     * the rounding mode to apply when rounding is necessary to convert this Decimal into a float value
     * @return this `Decimal` converted to a `float`.
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     * @see .floatValue
     */
    fun floatValue(roundingMode: RoundingMode): Float

    /**
     * Converts this `Decimal` to a `double` using the specified rounding mode if the Decimal value can not
     * be exactly represented as a double value. Note that this conversion can lose information about the precision of
     * the `Decimal` value.
     *
     * @param roundingMode
     * the rounding mode to apply when rounding is necessary to convert this Decimal into a double value
     * @return this `Decimal` converted to a `double`.
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     * @see .doubleValue
     */
    fun doubleValue(roundingMode: RoundingMode): Double

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
    fun toBigInteger(roundingMode: RoundingMode): BigInteger

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
    fun toBigDecimal(scale: Int, roundingMode: RoundingMode): BigDecimal

    // methods to round and change the scale
    /**
     * Returns a `Decimal` value rounded to the specified `precision` using [ HALF_UP][RoundingMode.HALF_UP] rounding. If an overflow occurs due to the rounding operation, the result is silently truncated.
     *
     *
     * Note that contrary to the `scale(..)` operations this method does not change the scale of the value ---
     * extra digits are simply zeroised.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the rounded value.
     *
     *
     * *Examples and special cases:*
     *
     *  * **precision = 0**<br></br>
     * value is rounded to an integer value
     *  * **precision = 2**<br></br>
     * value is rounded to the second digit after the decimal point
     *  * **precision = -3**<br></br>
     * value is rounded to the thousands
     *  * **precision  scale**<br></br>
     * values is returned unchanged
     *  * **precision &lt; scale - 18**<br></br>
     * `IllegalArgumentException` is thrown
     *
     *
     * @param precision
     * the precision to use for the rounding, for instance 2 to round to the second digit after the decimal
     * point; must be at least `(scale - 18)`
     * @return a Decimal instance rounded to the given precision
     * @throws IllegalArgumentException
     * if `precision < scale - 18`
     * @see .round
     * @see .round
     * @see .scale
     */
    fun round(precision: Int): Decimal<S>

    /**
     * Returns a `Decimal` value rounded to the specified `precision` using the given rounding mode. If an
     * overflow occurs due to the rounding operation, the result is silently truncated.
     *
     *
     * Note that contrary to the `scale(..)` operations this method does not change the scale of the value ---
     * extra digits are simply zeroised.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the rounded value.
     *
     *
     * *Examples and special cases:*
     *
     *  * **precision = 0**<br></br>
     * value is rounded to an integer value
     *  * **precision = 2**<br></br>
     * value is rounded to the second digit after the decimal point
     *  * **precision = -3**<br></br>
     * value is rounded to the thousands
     *  * **precision  scale**<br></br>
     * values is returned unchanged
     *  * **precision &lt; scale - 18**<br></br>
     * `IllegalArgumentException` is thrown
     *
     *
     * @param precision
     * the precision to use for the rounding, for instance 2 to round to the second digit after the decimal
     * point; must be at least `(scale - 18)`
     * @param roundingMode
     * the rounding mode to apply when rounding to the desired precision
     * @return a Decimal instance rounded to the given precision
     * @throws IllegalArgumentException
     * if `precision < scale - 18`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     * @see .round
     * @see .round
     * @see .scale
     */
    fun round(precision: Int, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` value rounded to the specified `precision` using the given truncation policy.
     *
     *
     * Note that contrary to the `scale(..)` operations this method does not change the scale of the value ---
     * extra digits are simply zeroised.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the rounded value.
     *
     *
     * *Examples and special cases:*
     *
     *  * **precision = 0**<br></br>
     * value is rounded to an integer value
     *  * **precision = 2**<br></br>
     * value is rounded to the second digit after the decimal point
     *  * **precision = -3**<br></br>
     * value is rounded to the thousands
     *  * **precision  scale**<br></br>
     * values is returned unchanged
     *  * **precision &lt; scale - 18**<br></br>
     * `IllegalArgumentException` is thrown
     *
     *
     * @param precision
     * the precision to use for the rounding, for instance 2 to round to the second digit after the decimal
     * point; must be at least `(scale - 18)`
     * @param truncationPolicy
     * the truncation policy defining [RoundingMode] and [OverflowMode] for the rounding
     * operation
     * @return a Decimal instance rounded to the given precision
     * @throws IllegalArgumentException
     * if `precision < scale - 18`
     * @throws ArithmeticException
     * if `truncationPolicy` specifies [RoundingMode.UNNECESSARY] and rounding is necessary or
     * if an overflow occurs and the policy declares [OverflowMode.CHECKED]
     * @see .round
     * @see .round
     * @see .scale
     */
    fun round(precision: Int, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` value whose [scale][.getScale] is changed to the given `scale` value.
     * [HALF_UP][RoundingMode.HALF_UP] rounding is used if the scale change involves rounding.
     *
     *
     * An exception is thrown if the scale conversion leads to an overflow.
     *
     * @param scale
     * the scale to use for the result, must be in `[0,18]`
     * @return a Decimal instance with the given new scale
     * @throws IllegalArgumentException
     * if `scale < 0` or `scale > 18`
     * @throws ArithmeticException
     * if an overflow occurs during the scale conversion
     * @see .scale
     * @see .scale
     * @see .round
     */
    fun scale(scale: Int): Decimal<*>

    /**
     * Returns a `Decimal` value whose [scale][.getScale] is changed to the scale given by the
     * `scaleMetrics` argument. [HALF_UP][RoundingMode.HALF_UP] rounding is used if the scale change involves
     * rounding.
     *
     *
     * An exception is thrown if the scale conversion leads to an overflow.
     *
     * @param <S>
     * the scale metrics type of the result
     * @param scaleMetrics
     * the scale metrics to use for the result
     * @return a Decimal instance with the given new scale metrics
     * @throws ArithmeticException
     * if an overflow occurs during the scale conversion
     * @see .scale
     * @see .scale
     * @see .round
    </S> */
    fun <S : ScaleMetrics> scale(scaleMetrics: S): Decimal<S>

    /**
     * Returns a `Decimal` value whose [scale][.getScale] is changed to the given `scale` value. The
     * specified `roundingMode` is used if the scale change involves rounding.
     *
     *
     * An exception is thrown if the scale conversion leads to an overflow.
     *
     * @param scale
     * the scale to use for the result, must be in `[0,18]`
     * @param roundingMode
     * the rounding mode to apply if the scale change involves rounding
     * @return a Decimal instance with the given new scale
     * @throws IllegalArgumentException
     * if `scale < 0` or `scale > 18`
     * @throws ArithmeticException
     * if `roundingMode` is [UNNESSESSARY][RoundingMode.UNNECESSARY] and rounding is necessary,
     * or if an overflow occurs during the scale conversion
     * @see .scale
     * @see .scale
     * @see .round
     */
    fun scale(scale: Int, roundingMode: RoundingMode): Decimal<*>

    /**
     * Returns a `Decimal` value whose [scale][.getScale] is changed to the scale given by the
     * `scaleMetrics` argument. The specified `roundingMode` is used if the scale change involves rounding.
     *
     *
     * An exception is thrown if the scale conversion leads to an overflow.
     *
     * @param <S>
     * the scale metrics type of the result
     * @param scaleMetrics
     * the scale metrics to use for the result
     * @param roundingMode
     * the rounding mode to apply if the scale change involves rounding
     * @return a Decimal instance with the given new scale metrics
     * @throws ArithmeticException
     * if `roundingMode` is [UNNESSESSARY][RoundingMode.UNNECESSARY] and rounding is necessary,
     * or if an overflow occurs during the scale conversion
     * @see .scale
     * @see .scale
     * @see .round
    </S> */
    fun <S : ScaleMetrics> scale(scaleMetrics: S, roundingMode: RoundingMode): Decimal<S>

    // add
    /**
     * Returns a `Decimal` whose value is `(this + augend)`. If the addition causes an overflow, the result
     * is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the addition.
     *
     * @param augend
     * value to be added to this `Decimal`
     * @return `this + augend`
     */
    fun add(augend: Decimal<S>): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this + augend)`. The specified `overflowMode` determines
     * whether to truncate the result silently or to throw an exception if an overflow occurs.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the addition.
     *
     * @param augend
     * value to be added to this `Decimal`
     * @param overflowMode
     * the overflow mode to apply if the addition leads to an overflow
     * @return `this + augend`
     * @throws ArithmeticException
     * if `overflowMode==CHECKED` and an overflow occurs
     */
    fun add(augend: Decimal<S>, overflowMode: OverflowMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this + augend)`. The result of the addition is rounded if
     * necessary using the specified `roundingMode`. Overflows during scale conversion or subtraction are silently
     * truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the addition.
     *
     * @param augend
     * value to be added to this `Decimal`
     * @param roundingMode
     * the rounding mode to apply if rounding is necessary
     * @return `round<sub>HALF_UP</sub>(this + augend)`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun add(augend: Decimal<*>, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this + augend)`. The result of the addition is rounded if
     * necessary using the [RoundingMode] defined by the `truncationPolicy` argument. The
     * `truncationPolicy` also defines the [OverflowMode] to apply if the operation causes an overflow.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the addition.
     *
     * @param augend
     * value to be added to this `Decimal`
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs during the addition
     * @return `round(this + augend)`
     * @throws ArithmeticException
     * if `truncationPolicy` specifies [RoundingMode.UNNECESSARY] and rounding is necessary or
     * if an overflow occurs and the policy declares [OverflowMode.CHECKED]
     */
    fun add(augend: Decimal<*>, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this + augend)` after converting the given `long` value to
     * the scale of `this` Decimal. If the operation causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the addition.
     *
     * @param augend
     * long value to be added to this `Decimal`
     * @return `this + augend`
     */
    fun add(augend: Long): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this + augend)` after converting the given `long` value to
     * the scale of `this` Decimal. The specified `overflowMode` determines whether to truncate the result
     * silently or to throw an exception if an overflow occurs.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the addition.
     *
     * @param augend
     * long value to be added to this `Decimal`
     * @param overflowMode
     * the overflow mode to apply if the addition leads to an overflow
     * @return `this + augend`
     * @throws ArithmeticException
     * if `overflowMode==CHECKED` and an overflow occurs
     */
    fun add(augend: Long, overflowMode: OverflowMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this + augend)` after converting the given `double`
     * argument into a Decimal value of the same scale as `this` Decimal. If rounding is necessary,
     * [HALF_UP][RoundingMode.HALF_UP] rounding mode is used and applied during the conversion step *before*
     * the addition operation. Overflows due to conversion or addition result in an exception.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the addition.
     *
     * @param augend
     * value to be added to this `Decimal`
     * @return `this + round<sub>HALF_UP</sub>(augend)`
     * @throws IllegalArgumentException
     * if `augend` is NaN or infinite or if the magnitude is too large for the double to be
     * represented as a `Decimal`
     * @throws ArithmeticException
     * if an overflow occurs during the addition operation
     */
    fun add(augend: Double): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this + augend)` after converting the given `double`
     * argument into a Decimal value of the same scale as `this` Decimal. If rounding is necessary, the specified
     * `roundingMode` is used and applied during the conversion step *before* the addition operation.
     * Overflows due to conversion or addition result in an exception.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the addition.
     *
     * @param augend
     * value to be added to this `Decimal`
     * @param roundingMode
     * the rounding mode to apply if the augend argument needs to be rounded when converted into a Decimal
     * number of the same scale as `this` Decimal
     * @return `this + round(augend)`
     * @throws IllegalArgumentException
     * if `augend` is NaN or infinite or if the magnitude is too large for the double to be
     * represented as a `Decimal`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary or if an overflow occurs during the
     * addition operation
     */
    fun add(augend: Double, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this + unscaledAugend  10<sup>-scale</sup>)` with the
     * [scale][.getScale] of this Decimal. If the addition causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the addition.
     *
     * @param unscaledAugend
     * value to be added to this `Decimal`
     * @return `round<sub>HALF_UP</sub>(this + unscaledAugend  10<sup>-scale</sup>)`
     */
    fun addUnscaled(unscaledAugend: Long): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this + unscaledAugend  10<sup>-scale</sup>)` with the
     * [scale][.getScale] of this Decimal. The specified `overflowMode` determines whether to truncate the
     * result silently or to throw an exception if an overflow occurs.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the addition.
     *
     * @param unscaledAugend
     * value to be added to this `Decimal`
     * @param overflowMode
     * the overflow mode to apply if the addition leads to an overflow
     * @return `round(this + unscaledAugend  10<sup>-scale</sup>)`
     * @throws ArithmeticException
     * if `overflowMode==CHECKED` and an overflow occurs
     */
    fun addUnscaled(unscaledAugend: Long, overflowMode: OverflowMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this + unscaledAugend  10<sup>-scale</sup>)`. The result
     * of the addition is rounded if necessary using [HALF_UP][RoundingMode.HALF_UP] rounding. If the operation
     * causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the addition.
     *
     * @param unscaledAugend
     * value to be added to this `Decimal`
     * @param scale
     * the scale to apply to `unscaledAugend`, positive to indicate the number of fraction digits to
     * the right of the Decimal point and negative to indicate up-scaling with a power of ten
     * @return `round<sub>HALF_UP</sub>(this + unscaledAugend  10<sup>-scale</sup>)`
     */
    fun addUnscaled(unscaledAugend: Long, scale: Int): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this + unscaledAugend  10<sup>-scale</sup>)`. The result
     * of the addition is rounded if necessary using the specified `roundingMode`. If the operation causes an
     * overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the addition.
     *
     * @param unscaledAugend
     * value to be added to this `Decimal`
     * @param scale
     * the scale to apply to `unscaledAugend`, positive to indicate the number of fraction digits to
     * the right of the Decimal point and negative to indicate up-scaling with a power of ten
     * @param roundingMode
     * the rounding mode to apply if rounding is necessary
     * @return `round(this + unscaledAugend  10<sup>-scale</sup>)`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun addUnscaled(unscaledAugend: Long, scale: Int, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this + unscaledAugend  10<sup>-scale</sup>)`. The result
     * of the addition is rounded if necessary using the [RoundingMode] defined by the `truncationPolicy`
     * argument. The `truncationPolicy` also defines the [OverflowMode] to apply if the operation causes an
     * overflow.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the addition.
     *
     * @param unscaledAugend
     * value to be added to this `Decimal`
     * @param scale
     * the scale to apply to `unscaledAugend`, positive to indicate the number of fraction digits to
     * the right of the Decimal point and negative to indicate up-scaling with a power of ten
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs during the addition
     * @return `round(this + unscaledAugend  10<sup>-scale</sup>)`
     * @throws ArithmeticException
     * if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and rounding is necessary or if
     * an overflow occurs and the policy declares [OverflowMode.CHECKED]
     */
    fun addUnscaled(unscaledAugend: Long, scale: Int, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this + value<sup>2</sup>)`. The squared value is rounded
     * *before* the addition if necessary using default [HALF_UP][RoundingMode.HALF_UP] rounding. Overflows
     * during squaring or addition are silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the addition.
     *
     * @param value
     * value to be squared and added to this `Decimal`
     * @return `this + round<sub>HALF_UP</sub>(value*value)`
     */
    fun addSquared(value: Decimal<S>): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this + value<sup>2</sup>)`. The squared value is rounded
     * *before* the addition if necessary using the specified `roundingMode`. Overflows during squaring or
     * addition are silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the addition.
     *
     * @param value
     * value to be squared and added to this `Decimal`
     * @param roundingMode
     * the rounding mode to apply if necessary when squaring the value
     * @return `this + round(value*value)`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun addSquared(value: Decimal<S>, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this + value<sup>2</sup>)`. The squared value is rounded
     * *before* the addition if necessary using the [RoundingMode] specified by the `truncationPolicy`
     * argument. The `truncationPolicy` also defines the [OverflowMode] to apply if an overflow occurs
     * during square or add operation.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the addition.
     *
     * @param value
     * value to be squared and added to this `Decimal`
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary when squaring the value or if an overflow occurs during the square or add operation
     * @return `this + round(value*value)`
     * @throws ArithmeticException
     * if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and rounding is necessary or if
     * an overflow occurs and the policy declares [OverflowMode.CHECKED]
     */
    fun addSquared(value: Decimal<S>, truncationPolicy: TruncationPolicy): Decimal<S>

    // subtract
    /**
     * Returns a `Decimal` whose value is `(this - subtrahend)`. If the subtraction causes an overflow, the
     * result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the subtraction.
     *
     * @param subtrahend
     * value to be subtracted from this `Decimal`
     * @return `this - subtrahend`
     */
    fun subtract(subtrahend: Decimal<S>): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this - subtrahend)`. The specified `overflowMode`
     * determines whether to truncate the result silently or to throw an exception if an overflow occurs.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the subtraction.
     *
     * @param subtrahend
     * value to be subtracted from this `Decimal`
     * @param overflowMode
     * the overflow mode to apply if the subtraction leads to an overflow
     * @return `this - subtrahend`
     * @throws ArithmeticException
     * if `overflowMode==CHECKED` and an overflow occurs
     */
    fun subtract(subtrahend: Decimal<S>, overflowMode: OverflowMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this - subtrahend)` after converting the given
     * `subtrahend` argument to the scale of `this` Decimal. The result of the subtraction is rounded if
     * necessary using the specified `roundingMode`. Overflows during scale conversion or subtraction are silently
     * truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the subtraction.
     *
     * @param subtrahend
     * value to be subtracted from this `Decimal`
     * @param roundingMode
     * the rounding mode to apply if rounding is necessary
     * @return `round<sub>HALF_UP</sub>(this - subtrahend)`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun subtract(subtrahend: Decimal<*>, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this - subtrahend)` after converting the given
     * `subtrahend` argument to the scale of `this` Decimal. The result of the subtraction is rounded if
     * necessary using the [RoundingMode] defined by the `truncationPolicy` argument. The
     * `truncationPolicy` also defines the [OverflowMode] to apply if the operation causes an overflow.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the subtraction.
     *
     * @param subtrahend
     * value to be subtracted from this `Decimal`
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs during the subtraction
     * @return `round(this - subtrahend)`
     * @throws ArithmeticException
     * if `truncationPolicy` specifies [RoundingMode.UNNECESSARY] and rounding is necessary or
     * if an overflow occurs and the policy declares [OverflowMode.CHECKED]
     */
    fun subtract(subtrahend: Decimal<*>, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this - subtrahend)`. If the subtraction causes an overflow, the
     * result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the subtraction.
     *
     * @param subtrahend
     * long value to be subtracted from this `Decimal`
     * @return `this - subtrahend`
     */
    fun subtract(subtrahend: Long): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this - subtrahend)`. The specified `overflowMode`
     * determines whether to truncate the result silently or to throw an exception if an overflow occurs.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the subtraction.
     *
     * @param subtrahend
     * long value to be subtracted from this `Decimal`
     * @param overflowMode
     * the overflow mode to apply if the subtraction leads to an overflow
     * @return `this - subtrahend`
     * @throws ArithmeticException
     * if `overflowMode==CHECKED` and an overflow occurs
     */
    fun subtract(subtrahend: Long, overflowMode: OverflowMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this - subtrahend)` after converting the given `double`
     * argument into a Decimal value of the same scale as `this` Decimal. If rounding is necessary,
     * [HALF_UP][RoundingMode.HALF_UP] rounding mode is used and applied during the conversion step *before*
     * the subtraction operation. Overflows due to conversion or subtraction result in an exception.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the subtraction.
     *
     * @param subtrahend
     * value to be subtracted from this `Decimal`
     * @return `this - round<sub>HALF_UP</sub>(subtrahend)`
     * @throws IllegalArgumentException
     * if `subtrahend` is NaN or infinite or if the magnitude is too large for the double to be
     * represented as a `Decimal`
     * @throws ArithmeticException
     * if an overflow occurs during the subtraction operation
     */
    fun subtract(subtrahend: Double): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this - subtrahend)` after converting the given `double`
     * argument into a Decimal value of the same scale as `this` Decimal. If rounding is necessary, the specified
     * `roundingMode` is used and applied during the conversion step *before* the subtraction operation.
     * Overflows due to conversion or subtraction result in an exception.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the subtraction.
     *
     * @param subtrahend
     * value to be subtracted from this `Decimal`
     * @param roundingMode
     * the rounding mode to apply if the subtrahend argument needs to be rounded when converted into a
     * Decimal number of the same scale as `this` Decimal
     * @return `this - round(subtrahend)`
     * @throws IllegalArgumentException
     * if `subtrahend` is NaN or infinite or if the magnitude is too large for the double to be
     * represented as a `Decimal`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary or if an overflow occurs during the
     * subtraction operation
     */
    fun subtract(subtrahend: Double, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this - unscaledSubtrahend  10<sup>-scale</sup>)` with
     * the [scale][.getScale] of this Decimal. If the subtraction causes an overflow, the result is silently
     * truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the subtraction.
     *
     * @param unscaledSubtrahend
     * value to be subtracted from this `Decimal`
     * @return `this - unscaledSubtrahend  10<sup>-scale</sup>`
     */
    fun subtractUnscaled(unscaledSubtrahend: Long): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this - unscaledSubtrahend  10<sup>-scale</sup>)` with
     * the [scale][.getScale] of this Decimal. The specified `overflowMode` determines whether to truncate
     * the result silently or to throw an exception if an overflow occurs.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the subtraction.
     *
     * @param unscaledSubtrahend
     * value to be subtracted from this `Decimal`
     * @param overflowMode
     * the overflow mode to apply if the subtraction leads to an overflow
     * @return `this - unscaledSubtrahend  10<sup>-scale</sup>`
     * @throws ArithmeticException
     * if `overflowMode==CHECKED` and an overflow occurs
     */
    fun subtractUnscaled(unscaledSubtrahend: Long, overflowMode: OverflowMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this - unscaledSubtrahend  10<sup>-scale</sup>)`. The
     * result of the subtraction is rounded if necessary using [HALF_UP][RoundingMode.HALF_UP] rounding. If the
     * operation causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the subtraction.
     *
     * @param unscaledSubtrahend
     * value to be subtracted from this `Decimal`
     * @param scale
     * the scale to apply to `unscaledSubtrahend`, positive to indicate the number of fraction digits
     * to the right of the Decimal point and negative to indicate up-scaling with a power of ten
     * @return `round<sub>HALF_UP</sub>(this - unscaledSubtrahend  10<sup>-scale</sup>)`
     */
    fun subtractUnscaled(unscaledSubtrahend: Long, scale: Int): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this - unscaledSubtrahend  10<sup>-scale</sup>)`. The
     * result of the subtraction is rounded if necessary using the specified `roundingMode`. If the operation
     * causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the subtraction.
     *
     * @param unscaledSubtrahend
     * value to be subtracted from this `Decimal`
     * @param scale
     * the scale to apply to `unscaledSubtrahend`, positive to indicate the number of fraction digits
     * to the right of the Decimal point and negative to indicate up-scaling with a power of ten
     * @param roundingMode
     * the rounding mode to apply if rounding is necessary
     * @return `round(this - unscaledSubtrahend  10<sup>-scale</sup>)`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun subtractUnscaled(unscaledSubtrahend: Long, scale: Int, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this - unscaledSubtrahend  10<sup>-scale</sup>)`. The
     * result of the subtraction is rounded if necessary using the [RoundingMode] defined by the
     * `truncationPolicy` argument. The `truncationPolicy` also defines the [OverflowMode] to apply if
     * the operation causes an overflow.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the subtraction.
     *
     * @param unscaledSubtrahend
     * value to be subtracted from this `Decimal`
     * @param scale
     * the scale to apply to `unscaledSubtrahend`, positive to indicate the number of fraction digits
     * to the right of the Decimal point and negative to indicate up-scaling with a power of ten
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs during the subtraction
     * @return `round(this - unscaledSubtrahend  10<sup>-scale</sup>)`
     * @throws ArithmeticException
     * if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and rounding is necessary or if
     * an overflow occurs and the policy declares [OverflowMode.CHECKED]
     */
    fun subtractUnscaled(unscaledSubtrahend: Long, scale: Int, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this - value<sup>2</sup>)`. The squared value is rounded
     * *before* the subtraction if necessary using default [HALF_UP][RoundingMode.HALF_UP] rounding. Overflows
     * during squaring or subtraction are silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the subtraction.
     *
     * @param value
     * value to be squared and subtracted from this `Decimal`
     * @return `this - round<sub>HALF_UP</sub>(value*value)`
     */
    fun subtractSquared(value: Decimal<S>): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this - value<sup>2</sup>)`. The squared value is rounded
     * *before* the subtraction if necessary using the specified `roundingMode`. Overflows during squaring or
     * subtraction are silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the subtraction.
     *
     * @param value
     * value to be squared and subtracted from this `Decimal`
     * @param roundingMode
     * the rounding mode to apply if necessary when squaring the value
     * @return `this - round(value*value)`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun subtractSquared(value: Decimal<S>, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this - value<sup>2</sup>)`. The squared value is rounded
     * *before* the subtraction if necessary using the [RoundingMode] specified by the
     * `truncationPolicy` argument. The `truncationPolicy` also defines the [OverflowMode] to apply if
     * an overflow occurs during square or subtract operation.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the subtraction.
     *
     * @param value
     * value to be squared and subtracted from this `Decimal`
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary when squaring the value or if an overflow occurs during the square or subtract operation
     * @return `this - round(value*value)`
     * @throws ArithmeticException
     * if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and rounding is necessary or if
     * an overflow occurs and the policy declares [OverflowMode.CHECKED]
     */
    fun subtractSquared(value: Decimal<S>, truncationPolicy: TruncationPolicy): Decimal<S>

    // multiply
    /**
     * Returns a `Decimal` whose value is `(this * multiplicand)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using default [HALF_UP][RoundingMode.HALF_UP] rounding. If the
     * multiplication causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param multiplicand
     * factor to multiply with this `Decimal`
     * @return `round<sub>HALF_UP</sub>(this * multiplicand)`
     */
    fun multiply(multiplicand: Decimal<S>): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * multiplicand)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using the specified `roundingMode`. If the multiplication causes
     * an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param multiplicand
     * factor to multiply with this `Decimal`
     * @param roundingMode
     * the rounding mode to apply if the result needs to be rounded
     * @return `round(this * multiplicand)`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun multiply(multiplicand: Decimal<S>, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * multiplicand)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using the [RoundingMode] specified by the
     * `truncationPolicy` argument. The `truncationPolicy` also defines the [OverflowMode] to apply if
     * an overflow occurs during the multiply operation.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param multiplicand
     * factor to multiply with this `Decimal`
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs
     * @return `round(this * multiplicand)`
     * @throws ArithmeticException
     * if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and rounding is necessary or if
     * an overflow occurs and the policy declares [OverflowMode.CHECKED]
     */
    fun multiply(multiplicand: Decimal<S>, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * multiplicand)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using [HALF_UP][RoundingMode.HALF_UP] rounding. If the
     * multiplication causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param multiplicand
     * factor to multiply with this `Decimal`
     * @return `round<sub>HALF_UP</sub>(this * multiplicand)`
     */
    fun multiplyBy(multiplicand: Decimal<*>): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * multiplicand)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using the specified `roundingMode`. If the multiplication causes
     * an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param multiplicand
     * factor to multiply with this `Decimal`
     * @param roundingMode
     * the rounding mode to apply if rounding is necessary
     * @return `round(this * multiplicand)`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun multiplyBy(multiplicand: Decimal<*>, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * multiplicand)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using the [RoundingMode] defined by the `truncationPolicy`
     * argument. The `truncationPolicy` also defines the [OverflowMode] to apply if the operation causes an
     * overflow.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param multiplicand
     * factor to multiply with this `Decimal`
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs
     * @return `round(this * multiplicand)`
     * @throws ArithmeticException
     * if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and rounding is necessary or if
     * an overflow occurs and the policy declares [OverflowMode.CHECKED]
     */
    fun multiplyBy(multiplicand: Decimal<*>, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * multiplicand)`. The scale of the returned value is the
     * sum of the scales of `this` Decimal and the `multiplicand` argument. If the result scale exceeds 18,
     * an [IllegalArgumentException] is thrown. An [ArithmeticException] is thrown if the product is out of
     * the possible range for a Decimal with the result scale.
     *
     *
     * Note that the result is *always* a new instance --- immutable if this Decimal is an [ImmutableDecimal]
     * and mutable if it is a [MutableDecimal].
     *
     * @param multiplicand
     * factor to multiply with this `Decimal`
     * @return `(this * multiplicand)` with scale equal to the sum of scales of `this` and
     * `multiplicand`
     * @throws IllegalArgumentException
     * if the sum of the scales of `this` Decimal and the `multiplicand` argument exceeds 18
     * @throws ArithmeticException
     * if an overflow occurs and product is out of the possible range for a Decimal with the result scale
     */
    fun multiplyExact(multiplicand: Decimal<*>): Decimal<*>

    /**
     * Returns a `Decimal` whose value is `(this * multiplicand)`. If the multiplication causes an overflow,
     * the result is silently truncated.
     *
     *
     * This method performs multiplication of this `Decimal` with a `long` value which is usually more
     * efficient than multiplication of two Decimal values.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param multiplicand
     * factor to multiply with this `Decimal`
     * @return `(this * multiplicand)`
     */
    fun multiply(multiplicand: Long): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * multiplicand)`. The specified `overflowMode`
     * determines whether to truncate the result silently or to throw an exception if an overflow occurs.
     *
     *
     * This method performs multiplication of this `Decimal` with a `long` value which is usually more
     * efficient than multiplication of two Decimal values.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param multiplicand
     * factor to multiply with this `Decimal`
     * @param overflowMode
     * the overflow mode to apply if the multiplication leads to an overflow
     * @return `(this * multiplicand)`
     * @throws ArithmeticException
     * if `overflowMode==CHECKED` and an overflow occurs
     */
    fun multiply(multiplicand: Long, overflowMode: OverflowMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * multiplicand)` after converting the given `double`
     * argument into a Decimal value of the same scale as `this` Decimal. [HALF_UP][RoundingMode.HALF_UP]
     * rounding mode is used if necessary and applied twice during the conversion step *before* the multiplication
     * and again when rounding the product to the [scale][.getScale] of this Decimal. Overflows due to conversion
     * or multiplication result in an exception.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param multiplicand
     * factor to multiply with this `Decimal`
     * @return `round<sub>HALF_UP</sub>(this * round<sub>HALF_UP</sub>(multiplicand))`
     * @throws IllegalArgumentException
     * if `multiplicand` is NaN or infinite or if the magnitude is too large for the double to be
     * represented as a `Decimal`
     * @throws ArithmeticException
     * if an overflow occurs during the multiply operation
     */
    fun multiply(multiplicand: Double): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * multiplicand)` after converting the given `double`
     * argument into a Decimal value of the same scale as `this` Decimal. Rounding, if necessary, uses the
     * specified `roundingMode` and is applied during the conversion step *before* the multiplication and
     * again when rounding the product to the [scale][.getScale] of this Decimal. Overflows due to conversion or
     * multiplication result in an exception.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param multiplicand
     * factor to multiply with this `Decimal`
     * @param roundingMode
     * the rounding mode to apply if the converted multiplicand or the resulting product needs to be rounded
     * @return `round(this * round(multiplicand))`
     * @throws IllegalArgumentException
     * if `multiplicand` is NaN or infinite or if the magnitude is too large for the double to be
     * represented as a `Decimal`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary or if an overflow occurs during the
     * multiply operation
     */
    fun multiply(multiplicand: Double, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * unscaledMultiplicand  10<sup>-scale</sup>)` with
     * the [scale][.getScale] of this Decimal. The result is rounded to the scale of this Decimal using default
     * [HALF_UP][RoundingMode.HALF_UP] rounding. If the multiplication causes an overflow, the result is silently
     * truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param unscaledMultiplicand
     * factor to multiply with this `Decimal`
     * @return `round<sub>HALF_UP</sub>(this * unscaledMultiplicand  10<sup>-scale</sup>)`
     */
    fun multiplyUnscaled(unscaledMultiplicand: Long): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * unscaledMultiplicand  10<sup>-scale</sup>)` with
     * the [scale][.getScale] of this Decimal. The result is rounded to the scale of this Decimal using the
     * specified `roundingMode`. If the multiplication causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param unscaledMultiplicand
     * factor to multiply with this `Decimal`
     * @param roundingMode
     * the rounding mode to apply if the result needs to be rounded
     * @return `round(this * unscaledMultiplicand  10<sup>-scale</sup>)`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun multiplyUnscaled(unscaledMultiplicand: Long, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * unscaledMultiplicand  10<sup>-scale</sup>)` with
     * the [scale][.getScale] of this Decimal. The result is rounded to the scale of this Decimal using the using
     * the [RoundingMode] specified by the `truncationPolicy` argument. The `truncationPolicy` also
     * defines the [OverflowMode] to apply if an overflow occurs during the multiplication.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param unscaledMultiplicand
     * factor to multiply with this `Decimal`
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs
     * @return `round(this * unscaledMultiplicand  10<sup>-scale</sup>)`
     * @throws ArithmeticException
     * if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and rounding is necessary or if
     * an overflow occurs and the policy declares [OverflowMode.CHECKED]
     */
    fun multiplyUnscaled(unscaledMultiplicand: Long, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * unscaledMultiplicand  10<sup>-scale</sup>)`. The
     * result of the multiplication is rounded to the [scale][.getScale] of this Decimal using
     * [HALF_UP][RoundingMode.HALF_UP] rounding. If the operation causes an overflow, the result is silently
     * truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param unscaledMultiplicand
     * factor to multiply with this `Decimal`
     * @param scale
     * the scale to apply to `unscaledMultiplicand`, positive to indicate the number of fraction digits
     * to the right of the Decimal point and negative to indicate up-scaling with a power of ten
     * @return `round<sub>HALF_UP</sub>(this * unscaledMultiplicand  10<sup>-scale</sup>)`
     */
    fun multiplyUnscaled(unscaledMultiplicand: Long, scale: Int): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * unscaledMultiplicand  10<sup>-scale</sup>)`. The
     * result of the multiplication is rounded to the [scale][.getScale] of this Decimal using the specified
     * `roundingMode`. If the operation causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param unscaledMultiplicand
     * factor to multiply with this `Decimal`
     * @param scale
     * the scale to apply to `unscaledMultiplicand`, positive to indicate the number of fraction digits
     * to the right of the Decimal point and negative to indicate up-scaling with a power of ten
     * @param roundingMode
     * the rounding mode to apply if rounding is necessary
     * @return `round(this * unscaledMultiplicand  10<sup>-scale</sup>)`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun multiplyUnscaled(unscaledMultiplicand: Long, scale: Int, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * unscaledMultiplicand  10<sup>-scale</sup>)`. The
     * result of the multiplication is rounded to the [scale][.getScale] of this Decimal using the
     * [RoundingMode] defined by the `truncationPolicy` argument. The `truncationPolicy` also defines
     * the [OverflowMode] to apply if the operation causes an overflow.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param unscaledMultiplicand
     * factor to multiply with this `Decimal`
     * @param scale
     * the scale to apply to `unscaledMultiplicand`, positive to indicate the number of fraction digits
     * to the right of the Decimal point and negative to indicate up-scaling with a power of ten
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs
     * @return `round(this * unscaledMultiplicand  10<sup>-scale</sup>)`
     * @throws ArithmeticException
     * if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and rounding is necessary or if
     * an overflow occurs and the policy declares [OverflowMode.CHECKED]
     */
    fun multiplyUnscaled(unscaledMultiplicand: Long, scale: Int, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * 10<sup>n</sup>)` . For negative `n` the
     * multiplication turns into a de-facto division and the result is rounded to the [scale][.getScale] of this
     * Decimal using default [HALF_UP][RoundingMode.HALF_UP] rounding. If the multiplication causes an overflow,
     * the result is silently truncated.
     *
     *
     * The result of this operation is the same as for [divideByPowerOfTen(-n)][.divideByPowerOfTen] given
     * `n > `[Integer.MIN_VALUE].
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param n
     * the exponent of the power-of-ten factor to multiply with this `Decimal`
     * @return `round<sub>HALF_UP</sub>(this * 10<sup>n</sup>)`
     */
    fun multiplyByPowerOfTen(n: Int): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * 10<sup>n</sup>)` . For negative `n` the
     * multiplication turns into a de-facto division and the result is rounded to the [scale][.getScale] of this
     * Decimal using the specified `roundingMode`. If the multiplication causes an overflow, the result is
     * silently truncated.
     *
     *
     * The result of this operation is the same as for [ divideByPowerOfTen(-n, roundingMode)][.divideByPowerOfTen] given `n > `[Integer.MIN_VALUE].
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param n
     * the exponent of the power-of-ten factor to multiply with this `Decimal`
     * @param roundingMode
     * the rounding mode to apply if the result needs to be rounded for the case `n < 0`
     * @return `round(this * 10<sup>n</sup>)`
     * @throws ArithmeticException
     * if `n < 0` and `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun multiplyByPowerOfTen(n: Int, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this * 10<sup>n</sup>)` . For negative `n` the
     * multiplication turns into a de-facto division and the result is rounded to the [scale][.getScale] of this
     * Decimal using the [RoundingMode] specified by the `truncationPolicy` argument. The
     * `truncationPolicy` also defines the [OverflowMode] to apply if an overflow occurs during the
     * multiplication.
     *
     *
     * The result of this operation is the same as for [ divideByPowerOfTen(-n, truncationPolicy)][.divideByPowerOfTen] given `n > `[Integer.MIN_VALUE].
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the multiplication.
     *
     * @param n
     * the exponent of the power-of-ten factor to multiply with this `Decimal`
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] to apply if rounding is necessary when
     * `n < 0` as well [OverflowMode] to use if `n > 0` and an overflow occurs during the
     * multiplication
     * @return `round(this * 10<sup>n</sup>)`
     * @throws ArithmeticException
     * if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and rounding is necessary when
     * `n < 0`, or if an overflow occurs and the policy declares [OverflowMode.CHECKED] for the
     * case `n > 0`
     */
    fun multiplyByPowerOfTen(n: Int, truncationPolicy: TruncationPolicy): Decimal<S>

    // divide
    /**
     * Returns a `Decimal` whose value is `(this / divisor)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using [HALF_UP][RoundingMode.HALF_UP] rounding. If the division
     * causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param divisor
     * divisor value by which this `Decimal` is to be divided
     * @return `round<sub>HALF_UP</sub>(this / divisor)`
     * @throws ArithmeticException
     * if `divisor==0`
     */
    fun divide(divisor: Decimal<S>): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / divisor)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using the specified `roundingMode`. If the division causes an
     * overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param divisor
     * divisor value by which this `Decimal` is to be divided
     * @param roundingMode
     * the rounding mode to apply if the result needs to be rounded
     * @return `round(this / divisor)`
     * @throws ArithmeticException
     * if `divisor==0` or if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun divide(divisor: Decimal<S>, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / divisor)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using the [RoundingMode] specified by the
     * `truncationPolicy` argument. The `truncationPolicy` also defines the [OverflowMode] to apply if
     * an overflow occurs during the divide operation.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param divisor
     * divisor value by which this `Decimal` is to be divided
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs
     * @return `round(this / divisor)`
     * @throws ArithmeticException
     * if `divisor==0`, or if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and
     * rounding is necessary or if an overflow occurs and the policy declares [OverflowMode.CHECKED]
     */
    fun divide(divisor: Decimal<S>, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / divisor)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using [HALF_UP][RoundingMode.HALF_UP] rounding. If the division
     * causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param divisor
     * divisor value by which this `Decimal` is to be divided
     * @return `round<sub>HALF_UP</sub>(this / divisor)`
     * @throws ArithmeticException
     * if `divisor==0`
     */
    fun divideBy(divisor: Decimal<*>): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / divisor)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using the specified `roundingMode`. If the division causes an
     * overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param divisor
     * divisor value by which this `Decimal` is to be divided
     * @param roundingMode
     * the rounding mode to apply if rounding is necessary
     * @return `round(this / divisor)`
     * @throws ArithmeticException
     * if `divisor==0` or if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun divideBy(divisor: Decimal<*>, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / divisor)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using the [RoundingMode] defined by the `truncationPolicy`
     * argument. The `truncationPolicy` also defines the [OverflowMode] to apply if the operation causes an
     * overflow.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param divisor
     * divisor value by which this `Decimal` is to be divided
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs
     * @return `round(this / divisor)`
     * @throws ArithmeticException
     * if `divisor==0`, or if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and
     * rounding is necessary, or if an overflow occurs and the policy declares [OverflowMode.CHECKED]
     */
    fun divideBy(divisor: Decimal<*>, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / divisor)` rounded down. This method is a shortcut for
     * calling [divide(divisor, RoundingMode.DOWN)][.divide]. If the division causes an
     * overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param divisor
     * divisor value by which this `Decimal` is to be divided
     * @return `round<sub>DOWN</sub>(this / divisor)`
     * @throws ArithmeticException
     * if `divisor==0`
     */
    fun divideTruncate(divisor: Decimal<S>): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / divisor)`, checking for lost information. If the quotient
     * cannot be represented exactly with the [scale][.getScale] of this Decimal then an
     * `ArithmeticException` is thrown.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param divisor
     * divisor value by which this `Decimal` is to be divided
     * @return `this / divisor`
     * @throws ArithmeticException
     * if `divisor==0`, or if the result does not fit in a Decimal with the scale of this Decimal
     * without rounding and without exceeding the the possible range of such a Decimal
     */
    fun divideExact(divisor: Decimal<S>): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / divisor)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using [HALF_UP][RoundingMode.HALF_UP] rounding. If the division
     * causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param divisor
     * long value by which this `Decimal` is to be divided
     * @return `round<sub>HALF_UP</sub>(this / divisor)`
     * @throws ArithmeticException
     * if `divisor==0`
     */
    fun divide(divisor: Long): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / divisor)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using the specified `roundingMode`. If the division causes an
     * overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param divisor
     * long value by which this `Decimal` is to be divided
     * @param roundingMode
     * the rounding mode to apply if the result needs to be rounded
     * @return `round(this / divisor)`
     * @throws ArithmeticException
     * if `divisor==0` or if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun divide(divisor: Long, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / divisor)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using the [RoundingMode] specified by the
     * `truncationPolicy` argument. The `truncationPolicy` also defines the [OverflowMode] to apply if
     * an overflow occurs during the divide operation.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param divisor
     * long value by which this `Decimal` is to be divided
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs
     * @return `round(this / divisor)`
     * @throws ArithmeticException
     * if `divisor==0`, or if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and
     * rounding is necessary, or if the policy declares [OverflowMode.CHECKED] an overflow occurs
     */
    fun divide(divisor: Long, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / divisor)` after converting the given `double`
     * argument into a Decimal value of the same scale as `this` Decimal. [HALF_UP][RoundingMode.HALF_UP]
     * rounding mode is used if necessary and applied twice during the conversion step *before* the division and
     * again when rounding the quotient to the [scale][.getScale] of this Decimal. Overflows due to conversion or
     * division result in an exception.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param divisor
     * divisor value by which this `Decimal` is to be divided
     * @return `round<sub>HALF_UP</sub>(this / round<sub>HALF_UP</sub>(divisor))`
     * @throws IllegalArgumentException
     * if `divisor` is NaN or infinite or if the magnitude is too large for the double to be
     * represented as a `Decimal`
     * @throws ArithmeticException
     * if `divisor==0` or if an overflow occurs during the divide operation
     */
    fun divide(divisor: Double): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / divisor)` after converting the given `double`
     * argument into a Decimal value of the same scale as `this` Decimal. Rounding, if necessary, uses the
     * specified `roundingMode` and is applied during the conversion step *before* the division and again
     * when rounding the quotient to the [scale][.getScale] of this Decimal. Overflows due to conversion or
     * division result in an exception.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param divisor
     * divisor value by which this `Decimal` is to be divided
     * @param roundingMode
     * the rounding mode to apply if the converted divisor or the resulting quotient needs to be rounded
     * @return `round(this / round(divisor))`
     * @throws IllegalArgumentException
     * if `divisor` is NaN or infinite or if the magnitude is too large for the double to be
     * represented as a `Decimal`
     * @throws ArithmeticException
     * if `divisor==0`, if `roundingMode==UNNECESSARY` and rounding is necessary or if an
     * overflow occurs during the divide operation
     */
    fun divide(divisor: Double, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / (unscaledDivisor  10<sup>-scale</sup>))` with the
     * [scale][.getScale] of this Decimal. The result is rounded to the scale of this Decimal using default
     * [HALF_UP][RoundingMode.HALF_UP] rounding. If the division causes an overflow, the result is silently
     * truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param unscaledDivisor
     * divisor value by which this `Decimal` is to be divided
     * @return `round<sub>HALF_UP</sub>(this / (unscaledDivisor  10<sup>-scale</sup>))`
     * @throws ArithmeticException
     * if `unscaledDivisor==0`
     */
    fun divideUnscaled(unscaledDivisor: Long): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / (unscaledDivisor  10<sup>-scale</sup>))` with the
     * [scale][.getScale] of this Decimal. The result is rounded to the scale of this Decimal using the specified
     * `roundingMode`. If the division causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param unscaledDivisor
     * divisor value by which this `Decimal` is to be divided
     * @param roundingMode
     * the rounding mode to apply if the result needs to be rounded
     * @return `round(this / (unscaledDivisor  10<sup>-scale</sup>))`
     * @throws ArithmeticException
     * if `unscaledDivisor==0` or if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun divideUnscaled(unscaledDivisor: Long, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / (unscaledDivisor  10<sup>-scale</sup>))` with the
     * [scale][.getScale] of this Decimal. The result is rounded to the scale of this Decimal using the using the
     * [RoundingMode] specified by the `truncationPolicy` argument. The `truncationPolicy` also
     * defines the [OverflowMode] to apply if an overflow occurs during the division.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param unscaledDivisor
     * divisor value by which this `Decimal` is to be divided
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs
     * @return `round(this / (unscaledDivisor  10<sup>-scale</sup>))`
     * @throws ArithmeticException
     * if `unscaledDivisor==0`, if `truncationPolicy` defines [RoundingMode.UNNECESSARY]
     * and rounding is necessary or if an overflow occurs and the policy declares
     * [OverflowMode.CHECKED]
     */
    fun divideUnscaled(unscaledDivisor: Long, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / (unscaledDivisor  10<sup>-scale</sup>))`. The
     * result is rounded to the scale of this Decimal using [HALF_UP][RoundingMode.HALF_UP] rounding. If the
     * operation causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param unscaledDivisor
     * divisor value by which this `Decimal` is to be divided
     * @param scale
     * the scale to apply to `unscaledDivisor`, positive to indicate the number of fraction digits to
     * the right of the Decimal point and negative to indicate up-scaling with a power of ten
     * @return `round<sub>HALF_UP</sub>(this / (unscaledDivisor  10<sup>-scale</sup>))`
     * @throws ArithmeticException
     * if `unscaledDivisor==0`
     */
    fun divideUnscaled(unscaledDivisor: Long, scale: Int): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / (unscaledDivisor  10<sup>-scale</sup>))`. The
     * result is rounded to the scale of this Decimal using the specified `roundingMode`. If the operation causes
     * an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param unscaledDivisor
     * divisor value by which this `Decimal` is to be divided
     * @param scale
     * the scale to apply to `unscaledDivisor`, positive to indicate the number of fraction digits to
     * the right of the Decimal point and negative to indicate up-scaling with a power of ten
     * @param roundingMode
     * the rounding mode to apply if the result needs to be rounded
     * @return `round(this / (unscaledDivisor  10<sup>-scale</sup>))`
     * @throws ArithmeticException
     * if `unscaledDivisor==0` or if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun divideUnscaled(unscaledDivisor: Long, scale: Int, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / (unscaledDivisor  10<sup>-scale</sup>))`. The
     * result is rounded to the scale of this Decimal using the [RoundingMode] defined by the
     * `truncationPolicy` argument. The `truncationPolicy` also defines the [OverflowMode] to apply if
     * the operation causes an overflow.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param unscaledDivisor
     * divisor value by which this `Decimal` is to be divided
     * @param scale
     * the scale to apply to `unscaledDivisor`, positive to indicate the number of fraction digits to
     * the right of the Decimal point and negative to indicate up-scaling with a power of ten
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs
     * @return `round(this / (unscaledDivisor  10<sup>-scale</sup>))`
     * @throws ArithmeticException
     * if `unscaledDivisor==0`, if `truncationPolicy` defines [RoundingMode.UNNECESSARY]
     * and rounding is necessary or if an overflow occurs and the policy declares
     * [OverflowMode.CHECKED]
     */
    fun divideUnscaled(unscaledDivisor: Long, scale: Int, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / 10<sup>n</sup>)` . The result is rounded to the
     * [scale][.getScale] of this Decimal using [HALF_UP][RoundingMode.HALF_UP] rounding.
     *
     *
     * For negative `n` the division turns into a de-facto multiplication. If the multiplication causes an
     * overflow, the result is silently truncated.
     *
     *
     * The result of this operation is the same as for [multiplyByPowerOfTen(-n)][.multiplyByPowerOfTen]
     * (unless `n == ` [Integer.MIN_VALUE]).
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param n
     * the exponent of the power-of-ten divisor by which this `Decimal` is to be divided
     * @return `round<sub>HALF_UP</sub>(this / 10<sup>n</sup>)`
     */
    fun divideByPowerOfTen(n: Int): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / 10<sup>n</sup>)` . The result is rounded to the
     * [scale][.getScale] of this Decimal using the specified `roudningMode`.
     *
     *
     * For negative `n` the division turns into a de-facto multiplication. If the multiplication causes an
     * overflow, the result is silently truncated.
     *
     *
     * The result of this operation is the same as for [ multiplyByPowerOfTen(-n, roundingMode)][.multiplyByPowerOfTen] (unless `n == ` [Integer.MIN_VALUE]).
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param n
     * the exponent of the power-of-ten divisor by which this `Decimal` is to be divided
     * @param roundingMode
     * the rounding mode to apply if the result needs to be rounded for the case `n > 0`
     * @return `round(this / 10<sup>n</sup>)`
     * @throws ArithmeticException
     * if `n > 0` and `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun divideByPowerOfTen(n: Int, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / 10<sup>n</sup>)` . The result is rounded to the
     * [scale][.getScale] of this Decimal using the [RoundingMode] specified by the
     * `truncationPolicy` argument.
     *
     *
     * For negative `n` the division turns into a de-facto multiplication and `truncationPolicy` defines
     * the [OverflowMode] to apply if an overflow occurs during the multiplication.
     *
     *
     * The result of this operation is the same as for [ multiplyByPowerOfTen(-n, truncationPolicy)][.multiplyByPowerOfTen] (unless `n == ` [Integer.MIN_VALUE]).
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param n
     * the exponent of the power-of-ten divisor by which this `Decimal` is to be divided
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] to apply if rounding is necessary when
     * `n > 0` as well [OverflowMode] to use if `n < 0` and an overflow occurs during the
     * de-facto multiplication
     * @return `round(this / 10<sup>n</sup>)`
     * @throws ArithmeticException
     * if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and rounding is necessary when
     * `n > 0`, or if an overflow occurs and the policy declares [OverflowMode.CHECKED] for the
     * case `n < 0`
     */
    fun divideByPowerOfTen(n: Int, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / divisor)` rounded down to the next integer. If the
     * division causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param divisor
     * value by which this `Decimal` is to be divided.
     * @return The integer part of `(this / divisor)`.
     * @throws ArithmeticException
     * if `divisor==0`
     * @see .divideToIntegralValue
     * @see .divideToLongValue
     * @see .remainder
     */
    fun divideToIntegralValue(divisor: Decimal<S>): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this / divisor)` rounded down to the next integer. The specified
     * `overflowMode` determines whether to truncate the result silently or to throw an exception if an overflow
     * occurs.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the division.
     *
     * @param divisor
     * value by which this `Decimal` is to be divided.
     * @param overflowMode
     * the overflow mode to apply if the division leads to an overflow
     * @return The integer part of `(this / divisor)`.
     * @throws ArithmeticException
     * if `divisor==0` or if `overflowMode==CHECKED` and an overflow occurs
     * @see .divideToIntegralValue
     * @see .divideToLongValue
     * @see .remainder
     */
    fun divideToIntegralValue(divisor: Decimal<S>, overflowMode: OverflowMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value `(this / divisor)` rounded down to the next `long` value. If
     * the division causes an overflow, the result is silently truncated.
     *
     * @param divisor
     * value by which this `Decimal` is to be divided.
     * @return The integer part of `(this / divisor)` returned as `long`
     * @throws ArithmeticException
     * if `divisor==0`
     * @see .divideToLongValue
     * @see .divideToIntegralValue
     * @see .remainder
     */
    fun divideToLongValue(divisor: Decimal<S>): Long

    /**
     * Returns a `Decimal` whose value `(this / divisor)` rounded down to the next `long` value. The
     * specified `overflowMode` determines whether to truncate the result silently or to throw an exception if an
     * overflow occurs.
     *
     * @param divisor
     * value by which this `Decimal` is to be divided.
     * @param overflowMode
     * the overflow mode to apply if the division leads to an overflow
     * @return The integer part of `(this / divisor)` returned as `long`
     * @throws ArithmeticException
     * if `divisor==0` or if `overflowMode==CHECKED` and an overflow occurs
     * @see .divideToLongValue
     * @see .divideToIntegralValue
     * @see .remainder
     */
    fun divideToLongValue(divisor: Decimal<S>, overflowMode: OverflowMode): Long

    /**
     * Returns a two-element `Decimal` array containing the result of `divideToIntegralValue` followed by
     * the result of `remainder` on the two operands. If the division causes an overflow, the result is silently
     * truncated.
     *
     *
     * Note that if both the integer quotient and remainder are needed, this method is faster than using the
     * `divideToIntegralValue` and `remainder` methods separately because the division need only be carried
     * out once.
     *
     * @param divisor
     * value by which this `Decimal` is to be divided, and the remainder computed.
     * @return a two element `Decimal` array: the quotient (the result of `divideToIntegralValue`) is the
     * initial element and the remainder is the final element.
     * @throws ArithmeticException
     * if `divisor==0`
     * @see .divideAndRemainder
     * @see .divideToIntegralValue
     * @see .remainder
     */
    fun divideAndRemainder(divisor: Decimal<S>): Array<out Decimal<S>?>

    /**
     * Returns a two-element `Decimal` array containing the result of `divideToIntegralValue` followed by
     * the result of `remainder` on the two operands. The specified `overflowMode` determines whether to
     * truncate the result silently or to throw an exception if an overflow occurs.
     *
     *
     * Note that if both the integer quotient and remainder are needed, this method is faster than using the
     * `divideToIntegralValue` and `remainder` methods separately because the division need only be carried
     * out once.
     *
     * @param divisor
     * value by which this `Decimal` is to be divided, and the remainder computed.
     * @param overflowMode
     * the overflow mode to apply if the division leads to an overflow
     * @return a two element `Decimal` array: the quotient (the result of `divideToIntegralValue`) is the
     * initial element and the remainder is the final element.
     * @throws ArithmeticException
     * if `divisor==0` or if `overflowMode==CHECKED` and an overflow occurs
     * @see .divideAndRemainder
     * @see .divideToIntegralValue
     * @see .remainder
     */
    fun divideAndRemainder(divisor: Decimal<S>, overflowMode: OverflowMode): Array<out Decimal<S>?>

    /**
     * Returns a `Decimal` whose value is `(this % divisor)`.
     *
     *
     * The remainder is given by `this.subtract(this.divideToIntegralValue(divisor).multiply(divisor))` . Note
     * that this is not the modulo operation (the result can be negative).
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the operation.
     *
     * @param divisor
     * value by which this `Decimal` is to be divided.
     * @return `this % divisor`.
     * @throws ArithmeticException
     * if `divisor==0`
     * @see .divideToIntegralValue
     * @see .divideAndRemainder
     */
    fun remainder(divisor: Decimal<S>): Decimal<S>

    // other arithmetic operations
    /**
     * Returns a `Decimal` whose value is `(-this)`.
     *
     *
     * If an overflow occurs (which is true iff `this.unscaledValue()==Long.MIN_VALUE`) then the result is still
     * negative and numerically equal to `this` value.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the negation.
     *
     * @return `-this`
     */
    fun negate(): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(-this)`.
     *
     *
     * The specified `overflowMode` determines whether to truncate the result silently or to throw an exception if
     * an overflow occurs (which is true iff `this.unscaledValue()==Long.MIN_VALUE`).
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the negation.
     *
     * @param overflowMode
     * the overflow mode to apply
     * @return `-this`
     * @throws ArithmeticException
     * if `overflowMode==CHECKED` and an overflow occurs (which is true iff
     * `this.unscaledValue()==Long.MIN_VALUE`)
     */
    fun negate(overflowMode: OverflowMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is the absolute value of this `Decimal`.
     *
     *
     * If an overflow occurs (which is true iff `this.unscaledValue()==Long.MIN_VALUE`) then the result is still
     * negative and numerically equal to `this` value.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the operation.
     *
     * @return `abs(this)`
     */
    fun abs(): Decimal<S>

    /**
     * Returns a `Decimal` whose value is the absolute value of this `Decimal`.
     *
     *
     * The specified `overflowMode` determines whether to truncate the result silently or to throw an exception if
     * an overflow occurs (which is true iff `this.unscaledValue()==Long.MIN_VALUE`).
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the operation.
     *
     * @param overflowMode
     * the overflow mode to apply
     * @return `abs(this)`
     * @throws ArithmeticException
     * if `overflowMode==CHECKED` and an overflow occurs (which is true iff
     * `this.unscaledValue()==Long.MIN_VALUE`)
     */
    fun abs(overflowMode: OverflowMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(1 / this)`. The result is rounded to the [ scale][.getScale] of this Decimal using default [HALF_UP][RoundingMode.HALF_UP] rounding. If the inversion causes an
     * overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the inversion.
     *
     * @return `round<sub>HALF_UP</sub>(1 / this)`
     * @throws ArithmeticException
     * if `this==0`
     */
    fun invert(): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(1 / this)`. The result is rounded to the [ scale][.getScale] of this Decimal using the specified `roundingMode`. If the inversion causes an overflow, the result
     * is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the inversion.
     *
     * @param roundingMode
     * the rounding mode to apply if the result needs to be rounded
     * @return `round(1 / this)`
     * @throws ArithmeticException
     * if `this==0` or if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun invert(roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(1 / this)`. The result is rounded to the [ scale][.getScale] of this Decimal using the [RoundingMode] specified by the `truncationPolicy` argument. The
     * `truncationPolicy` also defines the [OverflowMode] to apply if an overflow occurs during the invert
     * operation.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the inversion.
     *
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs
     * @return `round(1 / this)`
     * @throws ArithmeticException
     * if `this==0`, if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and rounding
     * is necessary or if an overflow occurs and the policy declares [OverflowMode.CHECKED]
     */
    fun invert(truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this<sup>2</sup>)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using default [HALF_UP][RoundingMode.HALF_UP] rounding. If the
     * square operation causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the square operation.
     *
     * @return `round<sub>HALF_UP</sub>(this * this)`
     */
    fun square(): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this<sup>2</sup>)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using the specified `roundingMode`. If the square operation
     * causes an overflow, the result is silently truncated.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the square operation.
     *
     * @param roundingMode
     * the rounding mode to apply if the result needs to be rounded
     * @return `round(this * this)`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun square(roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this<sup>2</sup>)`. The result is rounded to the
     * [scale][.getScale] of this Decimal using the [RoundingMode] specified by the
     * `truncationPolicy` argument. The `truncationPolicy` also defines the [OverflowMode] to apply if
     * an overflow occurs during the square operation.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the square operation.
     *
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs
     * @return `round(this * this)`
     * @throws ArithmeticException
     * if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and rounding is necessary or if
     * an overflow occurs and the policy declares [OverflowMode.CHECKED]
     */
    fun square(truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is the square root of `this` Decimal value. The result is rounded to
     * the [scale][.getScale] of this Decimal using default [HALF_UP][RoundingMode.HALF_UP] rounding.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the square root operation.
     *
     * @return `sqrt(this)`
     * @throws ArithmeticException
     * if `this < 0`
     */
    fun sqrt(): Decimal<S>

    /**
     * Returns a `Decimal` whose value is the square root of `this` Decimal value. The result is rounded to
     * the [scale][.getScale] of this Decimal using the specified `roundingMode`.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the square root operation.
     *
     * @param roundingMode
     * the rounding mode to apply if the result needs to be rounded
     * @return `sqrt(this)`
     * @throws ArithmeticException
     * if `this < 0` or if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun sqrt(roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns the signum function of this `Decimal`.
     *
     * @return -1, 0, or 1 as the value of this `Decimal` is negative, zero, or positive.
     */
    fun signum(): Int

    /**
     * Returns a `Decimal` whose value is `(this << n)`. The shift distance, `n`, may be negative, in
     * which case this method performs a right shift.
     *
     *
     * Computes `floor(this * 2<sup>n</sup>)`.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the shift operation.
     *
     * @param n
     * shift distance, in bits.
     * @return `this << n`
     * @see .shiftRight
     */
    fun shiftLeft(n: Int): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this << n)`. The shift distance, `n`, may be negative, in
     * which case this method performs a right shift.
     *
     *
     * Computes `round(this * 2<sup>n</sup>)` using the specified `roundingMode`.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the shift operation.
     *
     * @param n
     * shift distance, in bits.
     * @param roundingMode
     * the rounding mode to use if truncation is involved for negative `n`, that is, for right shifts
     * @return `this << n`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     * @see .shiftRight
     */
    fun shiftLeft(n: Int, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this << n)`. The shift distance, `n`, may be negative, in
     * which case this method performs a right shift.
     *
     *
     * Computes `round(this * 2<sup>n</sup>)` using the [RoundingMode] specified by the
     * `truncationPolicy` argument.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the shift operation.
     *
     * @param n
     * shift distance, in bits.
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs
     * @return `this << n`
     * @throws ArithmeticException
     * if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and rounding is necessary or if
     * an overflow occurs and the policy declares [OverflowMode.CHECKED]
     * @see .shiftRight
     */
    fun shiftLeft(n: Int, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a BigInteger whose value is `(this >> n)`. Sign extension is performed. The shift distance,
     * `n`, may be negative, in which case this method performs a left shift.
     *
     *
     * Computes `floor(this / 2<sup>n</sup>)`.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the shift operation.
     *
     * @param n
     * shift distance, in bits.
     * @return `this >> n`
     * @see .shiftLeft
     */
    fun shiftRight(n: Int): Decimal<S>

    /**
     * Returns a BigInteger whose value is `(this >> n)`. Sign extension is performed. The shift distance,
     * `n`, may be negative, in which case this method performs a left shift.
     *
     *
     * Computes `round(this / 2<sup>n</sup>)` using the specified `roundingMode`.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the shift operation.
     *
     * @param n
     * shift distance, in bits.
     * @param roundingMode
     * the rounding mode to use if truncation is involved
     * @return `this >> n`
     * @see .shiftLeft
     *
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun shiftRight(n: Int, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a BigInteger whose value is `(this >> n)`. Sign extension is performed. The shift distance,
     * `n`, may be negative, in which case this method performs a left shift.
     *
     *
     * Computes `round(this / 2<sup>n</sup>)` using the [RoundingMode] specified by the
     * `truncationPolicy` argument.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the shift operation.
     *
     * @param n
     * shift distance, in bits.
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs
     * @return `this >> n`
     * @throws ArithmeticException
     * if `truncationPolicy` defines [RoundingMode.UNNECESSARY] and rounding is necessary or if
     * an overflow occurs and the policy declares [OverflowMode.CHECKED]
     * @see .shiftLeft
     */
    fun shiftRight(n: Int, truncationPolicy: TruncationPolicy): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this<sup>n</sup>)` using default [ HALF_UP][RoundingMode.HALF_UP] rounding.
     *
     *
     * The current implementation uses the core algorithm defined in ANSI standard X3.274-1996. For `n >= 0`, the
     * returned numerical value is within 1 ULP of the exact numerical value. No precision is guaranteed for
     * `n < 0` but the result is usually exact up to 10-20 ULP.
     *
     *
     * Properties of the X3.274-1996 algorithm are:
     *
     *  * An `IllegalArgumentException` is thrown if `abs(n) > 999999999`
     *  * if `n` is zero, one is returned even if `this` is zero, otherwise
     *
     *  * if `n` is positive, the result is calculated via the repeated squaring technique into a single
     * accumulator
     *  * if `n` is negative, the result is calculated as if `n` were positive; this value is then divided
     * into one
     *  * The final value from either the positive or negative case is then rounded using [ HALF_UP][RoundingMode.HALF_UP] rounding
     *
     *
     *
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the operation.
     *
     * @param n
     * power to raise this `Decimal` to
     * @return `this<sup>n</sup>` using the ANSI standard X3.274-1996 algorithm
     * @throws IllegalArgumentException
     * if `abs(n) > 999999999`
     * @throws ArithmeticException
     * if `n` is negative and `this` equals zero
     */
    fun pow(n: Int): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this<sup>n</sup>)` applying the specified `roundingMode`.
     *
     *
     * The current implementation uses the core algorithm defined in ANSI standard X3.274-1996. For `n >= 0`, the
     * returned numerical value is within 1 ULP of the exact numerical value; the result is actually exact for all
     * rounding modes other than HALF_UP, HALF_EVEN and HALF_DOWN. No precision is guaranteed for `n < 0` but the
     * result is usually exact up to 10-20 ULP.
     *
     *
     * Properties of the X3.274-1996 algorithm are:
     *
     *  * An `IllegalArgumentException` is thrown if `abs(n) > 999999999`
     *  * if `n` is zero, one is returned even if `this` is zero, otherwise
     *
     *  * if `n` is positive, the result is calculated via the repeated squaring technique into a single
     * accumulator
     *  * if `n` is negative, the result is calculated as if `n` were positive; this value is then divided
     * into one
     *  * The final value from either the positive or negative case is then rounded using the specified
     * `roundingMode`
     *
     *
     *
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the operation.
     *
     * @param n
     * power to raise this `Decimal` to
     * @param roundingMode
     * the rounding mode to apply if rounding is necessary
     * @return `this<sup>n</sup>` using the ANSI standard X3.274-1996 algorithm
     * @throws IllegalArgumentException
     * if `abs(n) > 999999999`
     * @throws ArithmeticException
     * if `n` is negative and `this` equals zero or if `roundingMode` equals
     * [RoundingMode.UNNECESSARY] and rounding is necessary
     */
    fun pow(n: Int, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns a `Decimal` whose value is `(this<sup>n</sup>)` applying the [RoundingMode] specified
     * by `truncationPolicy`. The `truncationPolicy` argument also defines the [OverflowMode] to apply
     * if an overflow occurs during the power operation.
     *
     *
     * The current implementation uses the core algorithm defined in ANSI standard X3.274-1996. For `n >= 0`, the
     * returned numerical value is within 1 ULP of the exact numerical value; the result is actually exact for all
     * rounding modes other than HALF_UP, HALF_EVEN and HALF_DOWN. No precision is guaranteed for `n < 0` but the
     * result is usually exact up to 10-20 ULP.
     *
     *
     * Properties of the X3.274-1996 algorithm are:
     *
     *  * An `IllegalArgumentException` is thrown if `abs(n) > 999999999`
     *  * if `n` is zero, one is returned even if `this` is zero, otherwise
     *
     *  * if `n` is positive, the result is calculated via the repeated squaring technique into a single
     * accumulator
     *  * if `n` is negative, the result is calculated as if `n` were positive; this value is then divided
     * into one
     *  * The final value from either the positive or negative case is then rounded using the [RoundingMode]
     * specified by `truncationPolicy`
     *
     *
     *
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the operation.
     *
     * @param n
     * power to raise this `Decimal` to
     * @param truncationPolicy
     * the truncation policy specifying [RoundingMode] and [OverflowMode] to apply if rounding is
     * necessary or if an overflow occurs
     * @return `this<sup>n</sup>` using the ANSI standard X3.274-1996 algorithm
     * @throws IllegalArgumentException
     * if `abs(n) > 999999999`
     * @throws ArithmeticException
     * if `n` is negative and `this` equals zero; if `truncationPolicy` defines
     * [RoundingMode.UNNECESSARY] and rounding is necessary or if an overflow occurs and the policy
     * declares [OverflowMode.CHECKED]
     */
    fun pow(n: Int, truncationPolicy: TruncationPolicy): Decimal<S>

    // compare and related methods
    /**
     * Compares two `Decimal` objects numerically.
     *
     * @param other
     * `Decimal` to which this `Decimal` is to be compared
     * @return the value `0` if this `Decimal` is equal to the argument `Decimal`; a value less than
     * `0` if this `Decimal` is numerically less than the argument `Decimal`; and a value
     * greater than `0` if this `Decimal` is numerically greater than the argument `Decimal`
     */
    override fun compareTo(other: Decimal<S>): Int

    /**
     * Compares this `Decimal` with the specified `Decimal` and returns true if the two are numerically
     * equal.
     *
     *
     * Returns true iff [.compareTo] returns 0.
     *
     * @param other
     * `Decimal` to which this `Decimal` is to be compared
     * @return true this `Decimal` is numerically equal to `other` and false otherwise
     */
    fun isEqualTo(other: Decimal<S>): Boolean

    /**
     * Compares this `Decimal` with the specified `Decimal` and returns true if this Decimal is numerically
     * greater than `other`.
     *
     *
     * Returns true iff [.compareTo] returns a value greater than 0.
     *
     * @param other
     * `Decimal` to which this `Decimal` is to be compared
     * @return true if `this > other`
     */
    fun isGreaterThan(other: Decimal<S>): Boolean

    /**
     * Compares this `Decimal` with the specified `Decimal` and returns true if this Decimal is numerically
     * greater than or equal to `other`.
     *
     *
     * Returns true iff [.compareTo] returns a non-negative value.
     *
     * @param other
     * `Decimal` to which this `Decimal` is to be compared
     * @return true if `this >= other`
     */
    fun isGreaterThanOrEqualTo(other: Decimal<S>): Boolean

    /**
     * Compares this `Decimal` with the specified `Decimal` and returns true if this Decimal is numerically
     * less than `other`.
     *
     *
     * Returns true iff [.compareTo] returns a negative value.
     *
     * @param other
     * `Decimal` to which this `Decimal` is to be compared.
     * @return true if `this < other`
     */
    fun isLessThan(other: Decimal<S>): Boolean

    /**
     * Compares this `Decimal` with the specified `Decimal` and returns true if this Decimal is numerically
     * less than or equal to `other`.
     *
     *
     * Returns true iff [.compareTo] returns a non-positive value.
     *
     * @param other
     * `Decimal` to which this `Decimal` is to be compared
     * @return true if `this <= other`
     */
    fun isLessThanOrEqualTo(other: Decimal<S>): Boolean

    /**
     * Returns the minimum of this `Decimal` and `val`.
     *
     * @param val
     * value with which the minimum is to be computed.
     * @return the `Decimal` whose value is the lesser of this `Decimal` and `val`. If they are equal,
     * as defined by the [compareTo][.compareTo] method, `this` is returned.
     * @see .compareTo
     */
    fun min(`val`: Decimal<S>): Decimal<S>

    /**
     * Returns the maximum of this `Decimal` and `val`.
     *
     * @param val
     * value with which the maximum is to be computed.
     * @return the `Decimal` whose value is the greater of this `Decimal` and `val`. If they are
     * equal, as defined by the [compareTo][.compareTo] method, `this` is returned.
     * @see .compareTo
     */
    fun max(`val`: Decimal<S>): Decimal<S>

    /**
     * Returns the average of this `Decimal` and `val`. The result is rounded to the [ scale][.getScale] of this Decimal using default [HALF_UP][RoundingMode.HALF_UP] rounding. The method is much more
     * efficient than an addition and subsequent long division and is guaranteed not to overflow.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the average operation.
     *
     * @param val
     * value with which the average is to be computed.
     * @return `round<sub>HALF_UP</sub>((this + val) / 2)`
     */
    fun avg(`val`: Decimal<S>): Decimal<S>

    /**
     * Returns the average of this `Decimal` and `val`. The result is rounded to the [ scale][.getScale] of this Decimal using the specified `roundingMode`. The method is much more efficient than an
     * addition and subsequent long division and is guaranteed not to overflow.
     *
     *
     * The returned value is a new instance if this Decimal is an [ImmutableDecimal]. If it is a
     * [MutableDecimal] then its internal state is altered and `this` is returned as result now representing
     * the outcome of the average operation.
     *
     * @param val
     * value with which the average is to be computed.
     * @param roundingMode
     * the rounding mode to use if rounding is necessary
     * @return `round((this + val) / 2)`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun avg(`val`: Decimal<S>, roundingMode: RoundingMode): Decimal<S>

    /**
     * Returns true if this `Decimal` is zero.
     *
     * @return true if `this == 0`
     */
    fun isZero(): Boolean

    /**
     * Returns true if this `Decimal` is one.
     *
     * @return true if `this == 1`
     */
    fun isOne(): Boolean

    /**
     * Returns true if this `Decimal` is minus one.
     *
     * @return true if `this == -1`
     */
    fun isMinusOne(): Boolean

    /**
     * Returns true if this `Decimal` is equal to the smallest positive number representable by a Decimal with the
     * current [scale][.getScale].
     *
     * @return true if `unscaledValue() == 1`
     */
    fun isUlp(): Boolean

    /**
     * Returns true if this `Decimal` is strictly positive.
     *
     * @return true if `this > 0`
     */
    fun isPositive(): Boolean

    /**
     * Returns true if this `Decimal` is not negative.
     *
     * @return true if `this >= 0`
     */
    fun isNonNegative(): Boolean

    /**
     * Returns true if this `Decimal` is negative.
     *
     * @return true if `this < 0`
     */
    fun isNegative(): Boolean

    /**
     * Returns true if this `Decimal` is not positive.
     *
     * @return true if `this <= 0`
     */
    fun isNonPositive(): Boolean

    /**
     * Returns true if this `Decimal` number is integral, or equivalently if its [ fractional part][.fractionalPart] is zero.
     *
     * @return true if `this` is an integer number
     */
    fun isIntegral(): Boolean

    /**
     * Returns true if the [integral part][.integralPart] of this `Decimal` number is zero.
     *
     * @return true if `-1 < this < 1`
     */
    fun isIntegralPartZero(): Boolean

    /**
     * Returns true if this `Decimal` is between zero (inclusive) and one (exclusive). The result value is true if
     * and only if this `Decimal` is not negative and its [integral part][.integralPart] is zero.
     *
     * @return true if `0 <= this < 1`
     */
    fun isBetweenZeroAndOne(): Boolean

    /**
     * Returns true if this `Decimal` is between zero (inclusive) and minus one (exclusive). The result value is
     * true if and only if this `Decimal` is not positive and its [integral part][.integralPart] is zero.
     *
     * @return true if `-1 < this <= 0`
     */
    fun isBetweenZeroAndMinusOne(): Boolean

    /**
     * Compares this `Decimal` with the specified `Decimal`. Two `Decimal` objects that are equal in
     * value but have a different scale (like 2.0 and 2.00) are considered equal by this method.
     *
     * @param other
     * `Decimal` to which this `Decimal` is to be compared.
     * @return the value `0` if this `Decimal` is equal to the argument `Decimal`; a value less than
     * `0` if this `Decimal` is numerically less than the argument `Decimal`; and a value
     * greater than `0` if this `Decimal` is numerically greater than the argument `Decimal`
     * @see .isEqualToNumerically
     * @see .compareTo
     */
    fun compareToNumerically(other: Decimal<*>): Int

    /**
     * Compares this `Decimal` with the specified `Decimal` and returns true if the two are numerically
     * equal. Two `Decimal` objects that are equal in value but have a different scale (like 2.0 and 2.00) are
     * considered equal by this method as opposed to the [equals][.equals] method which requires identical
     * scales of the compared values.
     *
     *
     * Returns true iff [.compareToNumerically] returns 0.
     *
     * @param other
     * `Decimal` to which this `Decimal` is to be compared.
     * @return true if this `Decimal` is numerically equal to `other` and false otherwise.
     * @see .compareToNumerically
     * @see .compareTo
     */
    fun isEqualToNumerically(other: Decimal<*>): Boolean

    // finally some basic object methods plus equals
    /**
     * Returns a hash code for this `Decimal`. The hash code is calculated from [scale][.getScale] and
     * [unscaled value][.unscaledValue].
     *
     * @return a hash code value for this object
     */
    override fun hashCode(): Int

    /**
     * Compares this Decimal to the specified object. The result is `true` if and only if the argument is a
     * `Decimal` value with the same [scale][.getScale] and [unscaled value][.unscaledValue] as this
     * Decimal.
     *
     * @param obj
     * the object to compare with
     * @return `true` if the argument is a `Decimal` object that contains the same value and scale as this
     * object; `false` otherwise
     *
     * @see .isEqualTo
     * @see .isEqualToNumerically
     * @see .hashCode
     */
    override fun equals(obj: Any?): Boolean

    /**
     * Returns a string representation of this `Decimal` object as fixed-point Decimal always showing all Decimal
     * places (also trailing zeros) and a leading sign character if negative.
     *
     * @return a `String` Decimal representation of this `Decimal` object with all the fraction digits
     * (including trailing zeros)
     */
    override fun toString(): String
}
