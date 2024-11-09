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

import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.OverflowMode
import org.decimal4j.truncate.TruncationPolicy
import java.math.BigDecimal
import java.math.BigInteger
import org.decimal4j.api.RoundingMode

/**
 * Interface implemented by mutable [Decimal] classes of different scales.
 * Mutable Decimals modify their state when performing arithmetic operations;
 * they represent the result after the operation. Arithmetic operations
 * therefore return `this` as return value. Note however that the
 * [scale][.getScale] of a Mutable Decimal does not change and remains
 * constant throughout the lifetime of a `MutableDecimal` instance.
 *
 *
 * Mutable Decimals may be preferred over [ImmutableDecimal] descendants
 * e.g. if the allocation of new objects is undesired or if a chain of
 * operations is performed.
 *
 *
 * Mutable Decimals are *NOT* thread safe.
 *
 * @param <S>
 * the scale metrics type associated with this Decimal
</S> */
interface MutableDecimal<S : ScaleMetrics> : Decimal<S> {
    /**
     * Sets `this` Decimal to 0 and returns `this` now representing
     * zero.
     *
     * @return `this` Decimal after assigning the value `0`
     */
    fun setZero(): MutableDecimal<S>

    /**
     * Sets `this` Decimal to 1 and returns `this` now representing
     * one.
     *
     * @return `this` Decimal after assigning the value `1`
     */
    fun setOne(): MutableDecimal<S>

    /**
     * Sets `this` Decimal to -1 and returns `this` now representing
     * minus one.
     *
     * @return `this` Decimal after assigning the value `-1`
     */
    fun setMinusOne(): MutableDecimal<S>

    /**
     * Sets `this` Decimal to the smallest positive value representable by
     * this Mutable Decimal and returns `this` now representing one ULP.
     *
     * @return `this` Decimal after assigning the value
     * `ULP=10<sup>-scale</sup>`
     */
    fun setUlp(): MutableDecimal<S>

    /**
     * Sets `this` Decimal to the specified `value` and returns
     * `this` now representing `value`.
     *
     * @param value
     * value to be set
     * @return `this` Decimal after assigning the given `value`
     */
    fun set(value: Decimal<S>): MutableDecimal<S>

    /**
     * Sets `this` Decimal to the specified `value` and returns
     * `this` now representing `value`. The specified
     * [Decimal] argument is rounded to the [scale][.getScale] of
     * this mutable Decimal using [RoundingMode.HALF_UP] rounding. An
     * exception is thrown if the specified value is too large to be represented
     * as a Decimal of this mutable Decimal's scale.
     *
     * @param value
     * value to be set
     * @param roundingMode
     * the rounding mode to apply during the conversion if necessary
     * @return `this` Decimal after assigning:
     * `round<sub>HALF_UP</sub>(value)`
     * @throws IllegalArgumentException
     * if `value` is too large to be represented as a Decimal
     * with the scale of this mutable Decimal
     * @throws ArithmeticException
     * if `roundingMode` is [             UNNESSESSARY][RoundingMode.UNNECESSARY] and rounding is necessary
     */
    fun set(value: Decimal<*>, roundingMode: RoundingMode): MutableDecimal<S>

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
    fun set(value: Long): MutableDecimal<S>

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
    fun set(value: BigInteger): MutableDecimal<S>

    /**
     * Sets `this` Decimal to the specified `value` and returns
     * `this` now representing `value`. The specified `float`
     * argument is rounded to the [scale][.getScale] of this mutable
     * Decimal using [RoundingMode.HALF_UP] rounding. An exception is
     * thrown if the specified value is too large to be represented as a Decimal
     * of this mutable Decimal's scale.
     *
     * @param value
     * value to be set
     * @return `this` Decimal after assigning:
     * `round<sub>HALF_UP</sub>(value)`
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is
     * too large for the float to be represented as a Decimal with
     * the scale of this mutable Decimal
     */
    fun set(value: Float): MutableDecimal<S>

    /**
     * Sets `this` Decimal to the specified `value` and returns
     * `this` now representing `value`. The specified `float`
     * argument is rounded to the [scale][.getScale] of this mutable
     * Decimal using the specified `roundingMode`. An exception is thrown
     * if the specified value is too large to be represented as a Decimal of
     * this mutable Decimal's scale.
     *
     * @param value
     * value to be set
     * @param roundingMode
     * the rounding mode to apply during the conversion if necessary
     * @return `this` Decimal after assigning: `round(value)`
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is
     * too large for the float to be represented as a Decimal with
     * the scale of this mutable Decimal
     * @throws ArithmeticException
     * if `roundingMode` is [             UNNESSESSARY][RoundingMode.UNNECESSARY] and rounding is necessary
     */
    fun set(value: Float, roundingMode: RoundingMode): MutableDecimal<S>

    /**
     * Sets `this` Decimal to the specified `value` and returns
     * `this` now representing `value`. The specified `double`
     * argument is rounded to the [scale][.getScale] of this mutable
     * Decimal using [RoundingMode.HALF_UP] rounding. An exception is
     * thrown if the specified value is too large to be represented as a Decimal
     * of this mutable Decimal's scale.
     *
     * @param value
     * value to be set
     * @return `this` Decimal after assigning:
     * `round<sub>HALF_UP</sub>(value)`
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is
     * too large for the double to be represented as a Decimal with
     * the scale of this mutable Decimal
     */
    fun set(value: Double): MutableDecimal<S>

    /**
     * Sets `this` Decimal to the specified `value` and returns
     * `this` now representing `value`. The specified `double`
     * argument is rounded to the [scale][.getScale] of this mutable
     * Decimal using the specified `roundingMode`. An exception is thrown
     * if the specified value is too large to be represented as a Decimal of
     * this mutable Decimal's scale.
     *
     * @param value
     * value to be set
     * @param roundingMode
     * the rounding mode to apply during the conversion if necessary
     * @return `this` Decimal after assigning: `round(value)`
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is
     * too large for the double to be represented as a Decimal with
     * the scale of this mutable Decimal
     * @throws ArithmeticException
     * if `roundingMode` is [             UNNESSESSARY][RoundingMode.UNNECESSARY] and rounding is necessary
     */
    fun set(value: Double, roundingMode: RoundingMode): MutableDecimal<S>

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
    fun set(value: BigDecimal): MutableDecimal<S>

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
    fun set(value: BigDecimal, roundingMode: RoundingMode): MutableDecimal<S>

    /**
     * Sets `this` Decimal to the specified `unscaledValue` and
     * returns `this` now representing `(unscaledValue * 10
     * <sup>-scale</sup>)` where scale refers to the [ scale][.getScale] of this mutable Decimal.
     *
     * @param unscaledValue
     * value to be set
     * @return `this` Decimal after assigning:
     * `unscaledValue * 10<sup>-scale</sup>`.
     */
    fun setUnscaled(unscaledValue: Long): MutableDecimal<S>

    /**
     * Sets `this` Decimal to the specified `unscaledValue` with the
     * given `scale` and returns `this` now representing
     * `(unscaledValue * 10<sup>-scale</sup>)`. The value is rounded to
     * the [scale][.getScale] of this mutable Decimal using
     * [HALF_UP][RoundingMode.HALF_UP] rounding. An exception is thrown if
     * the specified value is too large to be represented as a Decimal of this
     * of this mutable Decimal's scale.
     *
     * @param unscaledValue
     * value to be set
     * @param scale
     * the scale used for `unscaledValue`
     * @return `this` Decimal after assigning:
     * `round<sub>HALF_UP</sub>(unscaledValue * 10<sup>-scale</sup>)`
     * @throws IllegalArgumentException
     * if the value is too large to be represented as a Decimal with
     * the scale of this mutable Decimal
     */
    fun setUnscaled(unscaledValue: Long, scale: Int): MutableDecimal<S>

    /**
     * Sets `this` Decimal to the specified `unscaledValue` with the
     * given `scale` and returns `this` now representing
     * `(unscaledValue * 10<sup>-scale</sup>)`. The value is rounded to
     * the [scale][.getScale] of this mutable Decimal using the specified
     * `roundingMode`. An exception is thrown if the specified value is
     * too large to be represented as a Decimal of this of this mutable
     * Decimal's scale.
     *
     * @param unscaledValue
     * value to be set
     * @param scale
     * the scale used for `unscaledValue`
     * @param roundingMode
     * the rounding mode to apply during the conversion if necessary
     * @return `this` Decimal after assigning:
     * `round(unscaledValue * 10<sup>-scale</sup>)`
     * @throws IllegalArgumentException
     * if the value is too large to be represented as a Decimal with
     * the scale of this mutable Decimal
     * @throws ArithmeticException
     * if `roundingMode` is [             UNNESSESSARY][RoundingMode.UNNECESSARY] and rounding is necessary
     */
    fun setUnscaled(unscaledValue: Long, scale: Int, roundingMode: RoundingMode): MutableDecimal<S>

    /**
     * Parses the given string value and sets `this` Decimal to the parsed
     * `value`.
     *
     *
     * The string representation of a `Decimal` consists of an optional
     * sign, `'+'` or `'-'` , followed by a sequence of zero or more
     * decimal digits ("the integer"), optionally followed by a fraction.
     *
     *
     * The fraction consists of a decimal point followed by zero or more decimal
     * digits. The string must contain at least one digit in either the integer
     * or the fraction. If the fraction contains more digits than this mutable
     * Decimal's [scale][.getScale], the value is rounded using
     * [HALF_UP][RoundingMode.HALF_UP] rounding. An exception is thrown if
     * the value is too large to be represented as a Decimal of this mutable
     * Decimals's scale.
     *
     * @param value
     * the string value to parse and assign
     * @return `this` Decimal after assigning the parsed value
     * @throws NumberFormatException
     * if `value` does not represent a valid `Decimal`
     * or if the value is too large to be represented as a Decimal
     * with the scale of this mutable Decimal's scale
     */
    fun set(value: String): MutableDecimal<S>

    /**
     * Parses the given string value and sets `this` Decimal to the parsed
     * `value`.
     *
     *
     * The string representation of a `Decimal` consists of an optional
     * sign, `'+'` or `'-'` , followed by a sequence of zero or more
     * decimal digits ("the integer"), optionally followed by a fraction.
     *
     *
     * The fraction consists of a decimal point followed by zero or more decimal
     * digits. The string must contain at least one digit in either the integer
     * or the fraction. If the fraction contains more digits than this mutable
     * Decimal's [scale][.getScale], the value is rounded using the
     * specified `roundingMode`. An exception is thrown if the value is
     * too large to be represented as a Decimal of this mutable Decimals's
     * scale.
     *
     * @param value
     * the string value to parse and assign
     * @param roundingMode
     * the rounding mode to apply if the fraction contains more
     * digits than the scale of this mutable Decimal
     * @return `this` Decimal after assigning the parsed value
     * @throws NumberFormatException
     * if `value` does not represent a valid `Decimal`
     * or if the value is too large to be represented as a Decimal
     * with the scale of this mutable Decimal's scale
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is
     * necessary
     */
    fun set(value: String, roundingMode: RoundingMode): MutableDecimal<S>

    /**
     * Returns the minimum of this `Decimal` and `val`.
     *
     * @param val
     * value with which the minimum is to be computed.
     * @return the `Decimal` whose value is the lesser of this
     * `Decimal` and `val`. If they are equal, as defined by
     * the [compareTo][.compareTo] method, `this` is
     * returned.
     * @see .compareTo
     */
    fun min(`val`: MutableDecimal<S>): MutableDecimal<S>

    /**
     * Returns the maximum of this `Decimal` and `val`.
     *
     * @param val
     * value with which the maximum is to be computed.
     * @return the `Decimal` whose value is the greater of this
     * `Decimal` and `val`. If they are equal, as defined by
     * the [compareTo][.compareTo] method, `this` is
     * returned.
     * @see .compareTo
     */
    fun max(`val`: MutableDecimal<S>): MutableDecimal<S>

    /**
     * Returns a clone of this mutable Decimal numerically identical to this
     * value.
     *
     * @return a numerically identical clone of this value
     */
    fun clone(): MutableDecimal<S>

    // override some methods with specialized return type
    override fun integralPart(): MutableDecimal<S>

    override fun fractionalPart(): MutableDecimal<S>

    override fun round(precision: Int): MutableDecimal<S>

    override fun round(precision: Int, roundingMode: RoundingMode): MutableDecimal<S>

    override fun round(precision: Int, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun scale(scale: Int): MutableDecimal<*>

    override fun <S : ScaleMetrics> scale(scaleMetrics: S): MutableDecimal<S>

    override fun scale(scale: Int, roundingMode: RoundingMode): MutableDecimal<*>

    override fun <S : ScaleMetrics> scale(scaleMetrics: S, roundingMode: RoundingMode): MutableDecimal<S>

    override fun add(augend: Decimal<S>): MutableDecimal<S>

    override fun add(augend: Decimal<S>, overflowMode: OverflowMode): MutableDecimal<S>

    override fun add(augend: Decimal<*>, roundingMode: RoundingMode): MutableDecimal<S>

    override fun add(augend: Decimal<*>, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun add(augend: Long): MutableDecimal<S>

    override fun add(augend: Long, overflowMode: OverflowMode): MutableDecimal<S>

    override fun add(augend: Double): MutableDecimal<S>

    override fun add(augend: Double, roundingMode: RoundingMode): MutableDecimal<S>

    override fun addUnscaled(unscaledAugend: Long): MutableDecimal<S>

    override fun addUnscaled(unscaledAugend: Long, overflowMode: OverflowMode): MutableDecimal<S>

    override fun addUnscaled(unscaledAugend: Long, scale: Int): MutableDecimal<S>

    override fun addUnscaled(unscaledAugend: Long, scale: Int, roundingMode: RoundingMode): MutableDecimal<S>

    override fun addUnscaled(unscaledAugend: Long, scale: Int, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun addSquared(value: Decimal<S>): MutableDecimal<S>

    override fun addSquared(value: Decimal<S>, roundingMode: RoundingMode): MutableDecimal<S>

    override fun addSquared(value: Decimal<S>, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun subtract(subtrahend: Decimal<S>): MutableDecimal<S>

    override fun subtract(subtrahend: Decimal<S>, overflowMode: OverflowMode): MutableDecimal<S>

    override fun subtract(subtrahend: Decimal<*>, roundingMode: RoundingMode): MutableDecimal<S>

    override fun subtract(subtrahend: Decimal<*>, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun subtract(subtrahend: Long): MutableDecimal<S>

    override fun subtract(subtrahend: Long, overflowMode: OverflowMode): MutableDecimal<S>

    override fun subtract(subtrahend: Double): MutableDecimal<S>

    override fun subtract(subtrahend: Double, roundingMode: RoundingMode): MutableDecimal<S>

    override fun subtractUnscaled(unscaledSubtrahend: Long): MutableDecimal<S>

    override fun subtractUnscaled(unscaledSubtrahend: Long, overflowMode: OverflowMode): MutableDecimal<S>

    override fun subtractUnscaled(unscaledSubtrahend: Long, scale: Int): MutableDecimal<S>

    override fun subtractUnscaled(unscaledSubtrahend: Long, scale: Int, roundingMode: RoundingMode): MutableDecimal<S>

    override fun subtractUnscaled(
        unscaledSubtrahend: Long,
        scale: Int,
        truncationPolicy: TruncationPolicy
    ): MutableDecimal<S>

    override fun subtractSquared(value: Decimal<S>): MutableDecimal<S>

    override fun subtractSquared(value: Decimal<S>, roundingMode: RoundingMode): MutableDecimal<S>

    override fun subtractSquared(value: Decimal<S>, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun multiply(multiplicand: Decimal<S>): MutableDecimal<S>

    override fun multiply(multiplicand: Decimal<S>, roundingMode: RoundingMode): MutableDecimal<S>

    override fun multiply(multiplicand: Decimal<S>, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun multiplyBy(multiplicand: Decimal<*>): MutableDecimal<S>

    override fun multiplyBy(multiplicand: Decimal<*>, roundingMode: RoundingMode): MutableDecimal<S>

    override fun multiplyBy(multiplicand: Decimal<*>, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun multiplyExact(multiplicand: Decimal<*>): MutableDecimal<*>

    override fun multiply(multiplicand: Long): MutableDecimal<S>

    override fun multiply(multiplicand: Long, overflowMode: OverflowMode): MutableDecimal<S>

    override fun multiply(multiplicand: Double): MutableDecimal<S>

    override fun multiply(multiplicand: Double, roundingMode: RoundingMode): MutableDecimal<S>

    override fun multiplyUnscaled(unscaledMultiplicand: Long): MutableDecimal<S>

    override fun multiplyUnscaled(unscaledMultiplicand: Long, roundingMode: RoundingMode): MutableDecimal<S>

    override fun multiplyUnscaled(unscaledMultiplicand: Long, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun multiplyUnscaled(unscaledMultiplicand: Long, scale: Int): MutableDecimal<S>

    override fun multiplyUnscaled(unscaledMultiplicand: Long, scale: Int, roundingMode: RoundingMode): MutableDecimal<S>

    override fun multiplyUnscaled(
        unscaledMultiplicand: Long,
        scale: Int,
        truncationPolicy: TruncationPolicy
    ): MutableDecimal<S>

    override fun multiplyByPowerOfTen(n: Int): MutableDecimal<S>

    override fun multiplyByPowerOfTen(n: Int, roundingMode: RoundingMode): MutableDecimal<S>

    override fun multiplyByPowerOfTen(n: Int, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun divide(divisor: Decimal<S>): MutableDecimal<S>

    override fun divide(divisor: Decimal<S>, roundingMode: RoundingMode): MutableDecimal<S>

    override fun divide(divisor: Decimal<S>, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun divideBy(divisor: Decimal<*>): MutableDecimal<S>

    override fun divideBy(divisor: Decimal<*>, roundingMode: RoundingMode): MutableDecimal<S>

    override fun divideBy(divisor: Decimal<*>, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun divideTruncate(divisor: Decimal<S>): MutableDecimal<S>

    override fun divideExact(divisor: Decimal<S>): MutableDecimal<S>

    override fun divide(divisor: Long): MutableDecimal<S>

    override fun divide(divisor: Long, roundingMode: RoundingMode): MutableDecimal<S>

    override fun divide(divisor: Long, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun divide(divisor: Double): MutableDecimal<S>

    override fun divide(divisor: Double, roundingMode: RoundingMode): MutableDecimal<S>

    override fun divideUnscaled(unscaledDivisor: Long): MutableDecimal<S>

    override fun divideUnscaled(unscaledDivisor: Long, roundingMode: RoundingMode): MutableDecimal<S>

    override fun divideUnscaled(unscaledDivisor: Long, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun divideUnscaled(unscaledDivisor: Long, scale: Int): MutableDecimal<S>

    override fun divideUnscaled(unscaledDivisor: Long, scale: Int, roundingMode: RoundingMode): MutableDecimal<S>

    override fun divideUnscaled(
        unscaledDivisor: Long,
        scale: Int,
        truncationPolicy: TruncationPolicy
    ): MutableDecimal<S>

    override fun divideByPowerOfTen(n: Int): MutableDecimal<S>

    override fun divideByPowerOfTen(n: Int, roundingMode: RoundingMode): MutableDecimal<S>

    override fun divideByPowerOfTen(n: Int, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun divideToIntegralValue(divisor: Decimal<S>): MutableDecimal<S>

    override fun divideToIntegralValue(divisor: Decimal<S>, overflowMode: OverflowMode): MutableDecimal<S>

    override fun divideAndRemainder(divisor: Decimal<S>): Array<out MutableDecimal<S>?>

    override fun divideAndRemainder(divisor: Decimal<S>, overflowMode: OverflowMode): Array<out MutableDecimal<S>?>

    override fun remainder(divisor: Decimal<S>): MutableDecimal<S>

    override fun negate(): MutableDecimal<S>

    override fun negate(overflowMode: OverflowMode): MutableDecimal<S>

    override fun abs(): MutableDecimal<S>

    override fun abs(overflowMode: OverflowMode): MutableDecimal<S>

    override fun invert(): MutableDecimal<S>

    override fun invert(roundingMode: RoundingMode): MutableDecimal<S>

    override fun invert(truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun square(): MutableDecimal<S>

    override fun square(roundingMode: RoundingMode): MutableDecimal<S>

    override fun square(truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun sqrt(): MutableDecimal<S>

    override fun sqrt(roundingMode: RoundingMode): MutableDecimal<S>

    override fun shiftLeft(n: Int): MutableDecimal<S>

    override fun shiftLeft(n: Int, roundingMode: RoundingMode): MutableDecimal<S>

    override fun shiftLeft(n: Int, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun shiftRight(n: Int): MutableDecimal<S>

    override fun shiftRight(n: Int, roundingMode: RoundingMode): MutableDecimal<S>

    override fun shiftRight(n: Int, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun pow(n: Int): MutableDecimal<S>

    override fun pow(n: Int, roundingMode: RoundingMode): MutableDecimal<S>

    override fun pow(n: Int, truncationPolicy: TruncationPolicy): MutableDecimal<S>

    override fun avg(`val`: Decimal<S>): MutableDecimal<S>

    override fun avg(`val`: Decimal<S>, roundingMode: RoundingMode): MutableDecimal<S>
}
