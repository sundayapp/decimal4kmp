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

/**
 * `DecimalArithmetic` defines the basic primitive operations for [Decimal] numbers for one particular
 * combination of [scale][.getScale], [rounding mode][.getRoundingMode] and [ overflow mode][.getOverflowMode]. Primitive here means that `Decimal` values are simply represented by their underlying unscaled
 * `long` value. All operations therefore use unscaled longs for `Decimal` arguments and return longs for
 * `Decimal` number results.
 *
 *
 * Application code does not usually need to use `DecimalArithmetic` directly. It may be appropriate however for
 * very specialized applications with low latency, high frequency or zero garbage requirements. All operations of
 * `DecimalArithmetic` do not allocate any objects (zero garbage) unless otherwise indicated.
 */
interface DecimalArithmetic {
    /**
     * Returns the scale `f` applied to all unscaled decimal values passed to and returned by this
     * `DecimalArithmetic`. Corresponds to the number of digits to the right of the decimal point (cannot be
     * negative).
     *
     *
     * A given [Decimal] value multiplied with `10<sup>f</sup>` results in an unscaled long value.
     * Conversely, a `Decimal` value `d` can be computed from the unscaled value `u` as
     * `d = u*10<sup>-f</sup>`.
     *
     * @return the non-negative scale `f` applied to unscaled decimal values within this `DecimalArithmetic`
     * object
     * @see ScaleMetrics.getScale
     */
    val scale: Int

    /**
     * Returns the scale metrics associated with this decimal arithmetic object.
     *
     * @return the scale metrics
     */
    val scaleMetrics: ScaleMetrics

    /**
     * Returns the *rounding mode* applied to operations of this `DecimalArithmetic` object if rounding is
     * necessary.
     *
     * @return the rounding mode applied to operations of this `DecimalArithmetic` object if rounding is necessary
     */
    val roundingMode: RoundingMode

    /**
     * Returns the *overflow mode* applied to operations of this `DecimalArithmetic` object if an overflow
     * occurs. The overflow mode defines whether an operation should throw an exception if an overflow occurs.
     *
     * @return the overflow mode applied to operations of this `DecimalArithmetic` object if an overflow occurs
     */
    val overflowMode: OverflowMode

    /**
     * Returns the *truncation policy* defining how to handle truncation due to overflow or rounding. The
     * `TruncationPolicy` is defined by the [overflow mode][.getOverflowMode] and the
     * [rounding mode][.getRoundingMode].
     *
     * @return the truncation policy defining how this `DecimalArithmetic` handles truncation
     */
    val truncationPolicy: TruncationPolicy

    /**
     * Derives an arithmetic instance for the specified `scale` using this arithmetic's [ rounding mode][.getRoundingMode] and [overflow mode][.getOverflowMode].
     *
     * @param scale
     * the scale for the new arithmetic; must be in `[0,18]` both ends inclusive
     * @return an arithmetic instance with the given scale and this arithmetic's rounding and overflow mode
     * @throws IllegalArgumentException
     * if scale is not in `[0, 18]`
     * @see Scales.getScaleMetrics
     * @see ScaleMetrics.getArithmetic
     * @see ScaleMetrics.getArithmetic
     */
    fun deriveArithmetic(scale: Int): DecimalArithmetic

    /**
     * Derives an arithmetic instance for the same [scale][.getScale] as this arithmetic but for the specified
     * `roundingMode`. The returned arithmetic uses the same [overflow mode][.getOverflowMode] as this
     * arithmetic.
     *
     * @param roundingMode
     * the rounding mode for the new arithmetic
     * @return an arithmetic instance with the given rounding mode and this arithmetic's scale and overflow mode
     * @throws NullPointerException
     * if rounding mode is null
     */
    fun deriveArithmetic(roundingMode: RoundingMode): DecimalArithmetic

    /**
     * Derives an arithmetic instance for the same [scale][.getScale] as this arithmetic but for the specified
     * `roundingMode` and `overflowMode`.
     *
     * @param roundingMode
     * the rounding mode for the new arithmetic
     * @param overflowMode
     * the overflow mode for the new arithmetic
     * @return an arithmetic instance with the given rounding and overflow mode and this arithmetic's scale
     * @throws NullPointerException
     * if any of the arguments is null
     */
    fun deriveArithmetic(roundingMode: RoundingMode, overflowMode: OverflowMode): DecimalArithmetic

    /**
     * Derives an arithmetic instance for the same [scale][.getScale] as this arithmetic but for the specified
     * `overflowMode`. The returned arithmetic uses the same [rounding mode][.getRoundingMode] as this
     * arithmetic.
     *
     * @param overflowMode
     * the overflow mode for the new arithmetic
     * @return an arithmetic instance with the given overflow mode and this arithmetic's scale and rounding mode
     * @throws NullPointerException
     * if overflow mode is null
     */
    fun deriveArithmetic(overflowMode: OverflowMode): DecimalArithmetic

    /**
     * Derives an arithmetic instance for the same [scale][.getScale] as this arithmetic but with rounding and
     * overflow mode specified by the given `truncationPolicy`.
     *
     * @param truncationPolicy
     * the truncation policy specifying rounding and overflow mode for the new arithmetic
     * @return an arithmetic instance with rounding and overflow mode specified by the truncation policy using this
     * arithmetic's scale
     * @throws NullPointerException
     * if truncation policy is null
     */
    fun deriveArithmetic(truncationPolicy: TruncationPolicy): DecimalArithmetic

    /**
     * Returns the unscaled decimal for the decimal value `1`. One is the value `10<sup>scale</sup>` which
     * is also the multiplier used to get the unscaled decimal from the true decimal value.
     *
     * @return the unscaled decimal representing the decimal value 1
     */
    fun one(): Long

    /**
     * Returns the signum function of the specified unscaled decimal.
     *
     * @param uDecimal
     * the unscaled decimal
     * @return -1, 0, or 1 as the value of the specified unscaled decimal is negative, zero, or positive.
     */
    fun signum(uDecimal: Long): Int

    /**
     * Compares two unscaled decimal values numerically.
     *
     * @param uDecimal1
     * the first unscaled decimal to compare
     * @param uDecimal2
     * the second unscaled decimal to compare
     * @return the value `0` if `unscaled1 == unscaled2`; a value less than `0` if
     * `unscaled1 < unscaled2`; and a value greater than `0` if `unscaled1 > unscaled2`
     */
    fun compare(uDecimal1: Long, uDecimal2: Long): Int

    /**
     * Compares two unscaled decimal values numerically. Note that scale of the first operand is determined by this
     * arithmetic's [scale][.getScale] whereas the scale of the second `unscaled` value is explicitly
     * specified by the `scale` argument.
     *
     * @param uDecimal
     * the first unscaled decimal to compare
     * @param unscaled
     * the second unscaled decimal to compare
     * @param scale
     * the scale of `unscaled`
     * @return the value `0` if `unscaled1 == unscaled2`; a value less than `0` if
     * `unscaled1 < unscaled2`; and a value greater than `0` if `unscaled1 > unscaled2`
     */
    fun compareToUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Int

    /**
     * Returns an unscaled decimal whose value is `(uDecimal1 + uDecimal2)`.
     *
     * @param uDecimal1
     * first unscaled decimal value to be added
     * @param uDecimal2
     * second unscaled decimal value to be added
     * @return `uDecimal1 + uDecimal2`
     * @throws ArithmeticException
     * if an overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     */
    fun add(uDecimal1: Long, uDecimal2: Long): Long

    /**
     * Returns an unscaled decimal whose value is the sum of the specified arguments: `(uDecimal + lValue)`.
     *
     *
     * Mathematically the method calculates `(uDecimal + lValue * 10<sup>scale</sup>)` avoiding information loss
     * due to overflow of intermediary results.
     *
     * @param uDecimal
     * unscaled decimal value to be added
     * @param lValue
     * long value to be added
     * @return `(uDecimal + lValue * 10<sup>scale</sup>)`
     * @throws ArithmeticException
     * if an overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     */
    fun addLong(uDecimal: Long, lValue: Long): Long

    /**
     * Returns an unscaled decimal whose value is `(uDecimal + unscaled * 10<sup>-scale</sup>)`. If rounding must
     * be performed, this arithmetic's [rounding mode][.getRoundingMode] is applied. Note that scale of the first
     * operand is determined by this arithmetic's [scale][.getScale] whereas the scale of the second
     * `unscaled` value is explicitly specified by the `scale` argument.
     *
     *
     * Mathematically the method calculates `round(uDecimal + lValue * 10<sup>-scale + s</sup>)` where `s`
     * refers to this arithetic's scale. The method avoids information loss due to overflow of intermediary results.
     *
     * @param uDecimal
     * unscaled decimal value to be added to
     * @param unscaled
     * unscaled value to add
     * @param scale
     * scale associated with the `unscaled` value
     * @return `round(uDecimal + unscaled * 10<sup>-scale</sup>)`
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary or if an
     * overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     */
    fun addUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long

    /**
     * Returns an unscaled decimal whose value is `(uDecimalMinuend -
     * uDecimalSubtrahend)`.
     *
     * @param uDecimalMinuend
     * unscaled decimal value to subtract from
     * @param uDecimalSubtrahend
     * unscaled decimal value to subtract from the minuend
     * @return `uDecimalMinuend - uDecimalSubtrahend`
     * @throws ArithmeticException
     * if an overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     */
    fun subtract(uDecimalMinuend: Long, uDecimalSubtrahend: Long): Long

    /**
     * Returns an unscaled decimal whose value is the difference of the specified arguments: `(uDecimal - lValue)`
     * .
     *
     *
     * Mathematically the method calculates `(uDecimal - lValue * 10<sup>scale</sup>)` avoiding information loss
     * due to overflow of intermediary results.
     *
     * @param uDecimal
     * unscaled decimal value to subtract from
     * @param lValue
     * long value to subtract
     * @return `(uDecimal - lValue * 10<sup>scale</sup>)`
     * @throws ArithmeticException
     * if an overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     */
    fun subtractLong(uDecimal: Long, lValue: Long): Long

    /**
     * Returns an unscaled decimal whose value is `(uDecimal - unscaled * 10<sup>-scale</sup>)`. If rounding must
     * be performed, this arithmetic's [rounding mode][.getRoundingMode] is applied. Note that scale of the first
     * operand is determined by this arithmetic's [scale][.getScale] whereas the scale of the second
     * `unscaled` value is explicitly specified by the `scale` argument.
     *
     *
     * Mathematically the method calculates `round(uDecimal - lValue * 10<sup>-scale + s</sup>)` where `s`
     * refers to this arithetic's scale. The method avoids information loss due to overflow of intermediary results.
     *
     * @param uDecimal
     * unscaled decimal value to subtract from
     * @param unscaled
     * unscaled value to subtract
     * @param scale
     * scale associated with the `unscaled` value
     * @return `round(uDecimal - unscaled * 10<sup>-scale</sup>)` as unscaled decimal
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary or if an
     * overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     */
    fun subtractUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long

    /**
     * Returns an unscaled decimal whose value is the product of the specified arguments:
     * `(uDecimal1 * uDecimal2)`. If rounding must be performed, this arithmetic's [ rounding mode][.getRoundingMode] is applied.
     *
     *
     * Mathematically the method calculates `round((uDecimal1 * uDecimal2) * 10<sup>-scale</sup>)` avoiding
     * information loss due to overflow of intermediary results.
     *
     * @param uDecimal1
     * first unscaled decimal value to be multiplied
     * @param uDecimal2
     * second unscaled decimal value to be multiplied
     * @return `round(uDecimal1 * uDecimal2)`
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary or if an
     * overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     */
    fun multiply(uDecimal1: Long, uDecimal2: Long): Long

    /**
     * Returns an unscaled decimal whose value is `(uDecimal * lValue)` where the second argument is a true long
     * value instead of an unscaled decimal.
     *
     * @param uDecimal
     * unscaled decimal value to be multiplied
     * @param lValue
     * long value to be multiplied
     * @return `uDecimal * lValue`
     * @throws ArithmeticException
     * if an overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     */
    fun multiplyByLong(uDecimal: Long, lValue: Long): Long

    /**
     * Returns an unscaled decimal whose value is `(uDecimal * unscaled * 10<sup>-scale</sup>)`. If rounding must
     * be performed, this arithmetic's [rounding mode][.getRoundingMode] is applied. Note that scale of the first
     * operand is determined by this arithmetic's [scale][.getScale] whereas the scale of the second
     * `unscaled` value is explicitly specified by the `scale` argument.
     *
     *
     * Mathematically the method calculates `round((uDecimal * unscaled) * 10<sup>-scale</sup>)` avoiding
     * information loss due to overflow of intermediary results.
     *
     * @param uDecimal
     * unscaled decimal value to be multiplied
     * @param unscaled
     * unscaled value to be multiplied
     * @param scale
     * scale associated with the `unscaled` value
     * @return `round(uDecimal * (unscaled * 10<sup>-scale</sup>))`
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary or if an
     * overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     */
    fun multiplyByUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long

    /**
     * Returns an unscaled decimal whose value is `(uDecimal * 10<sup>n</sup>)`.
     *
     *
     * The power, `n`, may be negative, in which case this method performs a division by a power of ten. If
     * rounding must be performed (for negative n), this arithmetic's [rounding mode][.getRoundingMode] is
     * applied.
     *
     * @param uDecimal
     * value to be multiplied
     * @param n
     * the power of ten
     * @return `round(uDecimal  10<sup>n</sup>)`
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary or if an
     * overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     */
    fun multiplyByPowerOf10(uDecimal: Long, n: Int): Long

    /**
     * Returns an unscaled decimal whose value is the quotient of the specified arguments:
     * `(uDecimalDividend / uDecimalDivisor)`. If rounding must be performed, this arithmetic's
     * [rounding mode][.getRoundingMode] is applied.
     *
     *
     * Mathematically the method calculates `round((uDecimalDividend * 10<sup>scale</sup>) / uDecimalDivisor)`
     * avoiding information loss due to overflow of intermediary results.
     *
     * @param uDecimalDividend
     * value to be divided.
     * @param uDecimalDivisor
     * value by which the dividend is to be divided.
     * @return `round(uDecimalDividend / uDecimalDivisor)`
     * @throws ArithmeticException
     * if `uDecimalDividend` is zero, if [rounding mode][.getRoundingMode] is UNNECESSARY and
     * rounding is necessary or if an overflow occurs and the [overflow mode][.getOverflowMode] is
     * set to throw an exception
     */
    fun divide(uDecimalDividend: Long, uDecimalDivisor: Long): Long

    /**
     * Returns an unscaled decimal whose value is `(uDecimalDividend / lDivisor)` where the second argument is a
     * true long value instead of an unscaled decimal. If rounding must be performed, this arithmetic's
     * [rounding mode][.getRoundingMode] is applied.
     *
     * @param uDecimalDividend
     * value to be divided.
     * @param lDivisor
     * long value by which the dividend is to be divided.
     * @return `round(uDecimalDividend / lDivisor)`
     * @throws ArithmeticException
     * if `uDecimalDividend` is zero, if [rounding mode][.getRoundingMode] is UNNECESSARY and
     * rounding is necessary or if an overflow occurs and the [overflow mode][.getOverflowMode] is
     * set to throw an exception
     */
    fun divideByLong(uDecimalDividend: Long, lDivisor: Long): Long

    /**
     * Returns an unscaled decimal whose value is `(uDecimal / (unscaled * 10<sup>-scale</sup>))`. If rounding
     * must be performed, this arithmetic's [rounding mode][.getRoundingMode] is applied. Note that scale of the
     * first operand is determined by this arithmetic's [scale][.getScale] whereas the scale of the second
     * `unscaled` value is explicitly specified by the `scale` argument.
     *
     *
     * Mathematically the method calculates `round((uDecimal * 10<sup>scale</sup>) / unscaled)` avoiding
     * information loss due to overflow of intermediary results.
     *
     * @param uDecimal
     * value to be divided.
     * @param unscaled
     * unscaled value by which the dividend is to be divided.
     * @param scale
     * scale associated with the `unscaled` value
     * @return `round(uDecimal / (unscaled * 10<sup>-scale</sup>))`
     * @throws ArithmeticException
     * if `uDecimal` is zero, if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding
     * is necessary or if an overflow occurs and the [overflow mode][.getOverflowMode] is set to
     * throw an exception
     */
    fun divideByUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Long

    /**
     * Returns an unscaled decimal whose value is `(uDecimal / 10<sup>n</sup>)`. If rounding must be performed,
     * this arithmetic's [rounding mode][.getRoundingMode] is applied.
     *
     *
     * The power, `n`, may be negative, in which case this method performs a multiplication by a power of ten.
     *
     * @param uDecimal
     * value to be divided.
     * @param n
     * the power of ten
     * @return `round(uDecimal / 10<sup>n</sup>)`
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary or if an
     * overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     */
    fun divideByPowerOf10(uDecimal: Long, n: Int): Long

    /**
     * Returns an unscaled decimal whose value is the average of `uDecimal1` and `uDecimal2`. The method is
     * much more efficient than an addition and subsequent long division and is guaranteed not to overflow. If rounding
     * must be performed, this arithmetic's [rounding mode][.getRoundingMode] is applied.
     *
     * @param uDecimal1
     * the first unscaled decimal value to average
     * @param uDecimal2
     * the second unscaled decimal value to average
     * @return `round((uDecimal1 + uDecimal2) / 2)`
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary
     */
    fun avg(uDecimal1: Long, uDecimal2: Long): Long

    /**
     * Returns an unscaled decimal whose value is `abs(uDecimal)`, which is the value itself if
     * `uDecimal>=0` and `-uDecimal` if the given value is negative.
     *
     * @param uDecimal
     * the unscaled decimal value
     * @return `abs(uDecimal)`
     * @throws ArithmeticException
     * if an overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     */
    fun abs(uDecimal: Long): Long

    /**
     * Returns an unscaled decimal whose value is `-uDecimal`.
     *
     * @param uDecimal
     * the unscaled decimal value to negate
     * @return `-uDecimal`
     * @throws ArithmeticException
     * if an overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     */
    fun negate(uDecimal: Long): Long

    /**
     * Returns an unscaled decimal whose value is the inverse of the argument: `1/uDecimal`. If rounding must be
     * performed, this arithmetic's [rounding mode][.getRoundingMode] is applied.
     *
     *
     * Mathematically the method calculates `round((10<sup>scale</sup> * 10<sup>scale</sup>) / uDecimalDivisor)`
     * avoiding information loss due to overflow of intermediary results.
     *
     * @param uDecimal
     * the unscaled decimal value to invert
     * @return `round(1/uDecimal)`
     * @throws ArithmeticException
     * if `uDecimal` is zero, if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding
     * is necessary or if an overflow occurs and the [overflow mode][.getOverflowMode] is set to
     * throw an exception
     */
    fun invert(uDecimal: Long): Long

    /**
     * Returns an unscaled decimal whose value is the square of the specified argument: `uDecimal<sup>2</sup>`.
     * If rounding must be performed, this arithmetic's [rounding mode][.getRoundingMode] is applied.
     *
     *
     * Mathematically the method calculates `round((uDecimal * uDecimal) * 10<sup>-scale</sup>)` avoiding
     * information loss due to overflow of intermediary results.
     *
     * @param uDecimal
     * the unscaled decimal value to be squared
     * @return `round(uDecimal * uDecimal)`
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary or if an
     * overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     */
    fun square(uDecimal: Long): Long

    /**
     * Returns an unscaled decimal whose value is the square root of the specified argument: `sqrt(uDecimal)`. If
     * rounding must be performed, this arithmetic's [rounding mode][.getRoundingMode] is applied.
     *
     *
     * Mathematically the method calculates `round(sqrt(uDecimal * 10<sup>scale</sup>))` avoiding information
     * loss due to overflow of intermediary results.
     *
     * @param uDecimal
     * the unscaled decimal value
     * @return `round(sqrt(uDecimal))`
     * @throws ArithmeticException
     * if `uDecimal` is negative or if [rounding mode][.getRoundingMode] is UNNECESSARY and
     * rounding is necessary
     */
    fun sqrt(uDecimal: Long): Long

    /**
     * Returns an unscaled decimal whose value is `(uDecimalBase<sup>exponent</sup>)`. Note that `exponent`
     * is an integer rather than a decimal. If rounding must be performed, this arithmetic's [ rounding mode][.getRoundingMode] is applied.
     *
     *
     * The current implementation uses the core algorithm defined in ANSI standard X3.274-1996. For `exponent >= 0`, the
     * returned numerical value is within 1 ULP of the exact numerical value; the result is actually exact for all
     * rounding modes other than HALF_UP, HALF_EVEN and HALF_DOWN. No precision is guaranteed for `exponent < 0` but the
     * result is usually exact up to 10-20 ULP.
     *
     *
     * Properties of the X3.274-1996 algorithm are:
     *
     *  * An `IllegalArgumentException` is thrown if `abs(n) > 999999999`
     *  * if `exponent` is zero, one is returned even if `uDecimalBase` is zero, otherwise
     *
     *  * if `exponent` is positive, the result is calculated via the repeated squaring technique into a single
     * accumulator
     *  * if `exponent` is negative, the result is calculated as if `exponent` were positive; this value is then divided
     * into one
     *  * The final value from either the positive or negative case is then rounded using this arithmetic's
     * [rounding mode][.getRoundingMode]
     *
     *
     *
     *
     *
     * Note: this operation is **not** strictly garbage free since internally, two [ThreadLocal] objects are
     * used to calculate the result. The `ThreadLocal` values may become garbage if the thread becomes garbage.
     *
     * @param uDecimalBase
     * the unscaled decimal base value
     * @param exponent
     * exponent to which `uDecimalBase` is to be raised.
     * @return `uDecimalBase<sup>exponent</sup>`
     * @throws IllegalArgumentException
     * if `abs(exponent) > 999999999`
     * @throws ArithmeticException
     * if `uDecimalBase==0` and `exponent` is negative. (This would cause a division by zero.),
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary or if an
     * overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     */
    fun pow(uDecimalBase: Long, exponent: Int): Long

    /**
     * Returns an unscaled decimal whose value is `(uDecimal << n)`. The shift distance, `n`, may be
     * negative, in which case this method performs a right shift. The result is equal to
     * `round(uDecimal * 2<sup>n</sup>)` using this arithmetic's [rounding mode][.getRoundingMode] if
     * rounding is necessary.
     *
     * @param uDecimal
     * the unscaled decimal value to shift
     * @param n
     * shift distance, in bits.
     * @return `round(uDecimal << n)`
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary or if an
     * overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     * @see .shiftRight
     */
    fun shiftLeft(uDecimal: Long, n: Int): Long

    /**
     * Returns an unscaled decimal whose value is `(uDecimal >> n)`. The shift distance, `n`, may be
     * negative, in which case this method performs a left shift. The result is equal to
     * `round(uDecimal / 2<sup>n</sup>)` using this arithmetic's [rounding mode][.getRoundingMode] if
     * rounding is necessary.
     *
     * @param uDecimal
     * the unscaled decimal value to shift
     * @param n
     * shift distance, in bits.
     * @return `round(uDecimal >> n)`
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary or if an
     * overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     * @see .shiftLeft
     */
    fun shiftRight(uDecimal: Long, n: Int): Long

    /**
     * Returns an unscaled decimal whose value is rounded to the specified `precision` using the
     * [rounding mode][.getRoundingMode] of this arithmetic.
     *
     *
     * Note that this method does not change the scale of the value --- extra digits are simply zeroised.
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
     * @param uDecimal
     * the unscaled decimal value to round
     * @param precision
     * the precision to use for the rounding, for instance 2 to round to the second digit after the decimal
     * point; must be at least `(scale - 18)`
     * @return an unsigned decimal rounded to the given precision
     * @throws IllegalArgumentException
     * if `precision < scale - 18`
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary or if an
     * overflow occurs and the [overflow mode][.getOverflowMode] is set to throw an exception
     */
    fun round(uDecimal: Long, precision: Int): Long

    /**
     * Converts the specified long value to an unscaled decimal. An exception is thrown if the specified value is too
     * large to be represented as a Decimal of this arithmetic's [scale][.getScale].
     *
     * @param value
     * the value to convert
     * @return the unscaled decimal representing the same value as the given long value
     * @throws IllegalArgumentException
     * if `value` is too large to be represented as a Decimal with the scale of this arithmetic
     */
    fun fromLong(value: Long): Long

    /**
     * Converts the specified float value to an unscaled decimal. An exception is thrown if the specified value is too
     * large to be represented as a Decimal of this arithmetic's [scale][.getScale].
     *
     * @param value
     * the value to convert
     * @return the unscaled decimal representing the same value as the given float value
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is too large for the float to be represented
     * as a `Decimal` with the scale of this arithmetic
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary
     */
    fun fromFloat(value: Float): Long

    /**
     * Converts the specified double value to an unscaled decimal. An exception is thrown if the specified value is too
     * large to be represented as a Decimal of this arithmetic's [scale][.getScale].
     *
     * @param value
     * the value to convert
     * @return the unscaled decimal representing the same value as the given double value
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is too large for the double to be represented
     * as a `Decimal` with the scale of this arithmetic
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary
     */
    fun fromDouble(value: Double): Long

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
    fun fromBigInteger(value: BigInteger): Long

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
    fun fromBigDecimal(value: BigDecimal): Long

    /**
     * Converts the specified unscaled decimal with the given scale to another unscaled decimal of the scale of this
     * arithmetic.
     *
     * @param unscaledValue
     * the unscaled decimal value to convert
     * @param scale
     * the scale associated with `unscaledValue`
     * @return the unscaled decimal representing the same value as the given unscaled decimal with the new scale defined
     * by this arithmetic
     * @throws IllegalArgumentException
     * if the unscaled value with the specified scale is too large to be represented as a Decimal with the
     * scale of this arithmetic
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary
     */
    fun fromUnscaled(unscaledValue: Long, scale: Int): Long

    /**
     * Translates the string representation of a `Decimal` into an unscaled Decimal. The string representation
     * consists of an optional sign, `'+'` or `'-'` , followed by a sequence of zero or more decimal digits
     * ("the integer"), optionally followed by a fraction.
     *
     *
     * The fraction consists of a decimal point followed by zero or more decimal digits. The string must contain at
     * least one digit in either the integer or the fraction. If the fraction contains more digits than this
     * arithmetic's [scale][.getScale], the value is rounded using the arithmetic's [ rounding mode][.getRoundingMode]. An exception is thrown if the value is too large to be represented as a Decimal of this
     * arithmetic's scale.
     *
     * @param value
     * a `String` containing the decimal value representation to be parsed
     * @return the decimal as unscaled `long` value
     * @throws NumberFormatException
     * if `value` does not represent a valid `Decimal` or if the value is too large to be
     * represented as a Decimal with the scale of this arithmetic
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary
     */
    fun parse(value: String): Long

    /**
     * Translates the string representation of a `Decimal` into an unscaled Decimal. The string representation
     * consists of an optional sign, `'+'` or `'-'` , followed by a sequence of zero or more decimal digits
     * ("the integer"), optionally followed by a fraction.
     *
     *
     * The fraction consists of a decimal point followed by zero or more decimal digits. The string must contain at
     * least one digit in either the integer or the fraction. If the fraction contains more digits than this
     * arithmetic's [scale][.getScale], the value is rounded using the arithmetic's [ rounding mode][.getRoundingMode]. An exception is thrown if the value is too large to be represented as a Decimal of this
     * arithmetic's scale.
     *
     * @param value
     * a character sequence such as a `String` containing the decimal value representation to be parsed
     * @param start
     * the start index to read characters in `value`, inclusive
     * @param end
     * the end index where to stop reading in characters in `value`, exclusive
     * @return the decimal as unscaled `long` value
     * @throws IndexOutOfBoundsException
     * if `start < 0` or `end > value.length()`
     * @throws NumberFormatException
     * if `value` does not represent a valid `Decimal` or if the value is too large to be
     * represented as a Decimal with the scale of this arithmetic
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary
     */
    fun parse(value: CharSequence, start: Int, end: Int): Long

    /**
     * Converts the specified unscaled decimal value into a long value and returns it. The arithmetic's
     * [rounding mode][.getRoundingMode] is applied if rounding is necessary.
     *
     * @param uDecimal
     * the unscaled decimal value to convert into a long value
     * @return the `uDecimal` value converted into a long value, possibly rounded or truncated
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary
     */
    fun toLong(uDecimal: Long): Long

    /**
     * Converts the specified unscaled decimal value into an unscaled value of the given scale. The arithmetic's
     * [rounding mode][.getRoundingMode] is applied if rounding is necessary.
     *
     * @param uDecimal
     * the unscaled decimal value to convert into an unscaled value of the specified scale
     * @param scale
     * the target scale for the result value
     * @return the `uDecimal` value converted into an unscaled value of the specified scale, possibly rounded or
     * truncated
     * @throws IllegalArgumentException
     * if the unscaled value with this arithmetic's scale is too large to be represented as an unscaled
     * decimal with the specified scale
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary
     */
    fun toUnscaled(uDecimal: Long, scale: Int): Long

    /**
     * Converts the specified unscaled decimal value into a float value and returns it. The arithmetic's
     * [rounding mode][.getRoundingMode] is applied if rounding is necessary.
     *
     * @param uDecimal
     * the unscaled decimal value to convert into a float value
     * @return the `uDecimal` value converted into a float value, possibly rounded or truncated
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary
     */
    fun toFloat(uDecimal: Long): Float

    /**
     * Converts the specified unscaled decimal value into a double value and returns it. The arithmetic's
     * [rounding mode][.getRoundingMode] is applied if rounding is necessary.
     *
     * @param uDecimal
     * the unscaled decimal value to convert into a double value
     * @return the `uDecimal` value converted into a double value, possibly rounded or truncated
     * @throws ArithmeticException
     * if [rounding mode][.getRoundingMode] is UNNECESSARY and rounding is necessary
     */
    fun toDouble(uDecimal: Long): Double

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
    fun toBigDecimal(uDecimal: Long): BigDecimal

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
    fun toBigDecimal(uDecimal: Long, scale: Int): BigDecimal

    /**
     * Converts the specified unscaled decimal value into a [String] and returns it. If the [ scale][.getScale] is zero, the conversion is identical to [Long.toString]. For all other scales a value with
     * exactly `scale` fraction digits is returned even if some trailing fraction digits are zero.
     *
     *
     * Note: this operation is **not** strictly garbage free since the result value is allocated; however no
     * temporary objects other than the result are allocated during the conversion (internally a [ThreadLocal]
     * [StringBuilder] object is used to construct the string value, which may become garbage if the thread
     * becomes garbage).
     *
     * @param uDecimal
     * the unscaled decimal value to convert into a `String`
     * @return the `uDecimal` value as into a `String`
     */
    fun toString(uDecimal: Long): String

    /**
     * Converts the specified unscaled decimal value into a [String] and appends the string to the
     * `appendable`.
     *
     *
     * If the [scale][.getScale] is zero, the conversion into a string is identical to
     * [Long.toString]. For all other scales a string value with exactly `scale` fraction digits is
     * created even if some trailing fraction digits are zero.
     *
     *
     * Note: this operation is **not** strictly garbage free since internally, a [ThreadLocal] string builder
     * is used to construct the string. The `ThreadLocal` value may become garbage if the thread becomes garbage.
     *
     * @param uDecimal
     * the unscaled decimal value to convert into a `String`
     * @param appendable
     * the appendable to which the string representation of the unscaled decimal value is to be appended
     * @throws IOException
     * If an I/O error occurs when appending to `appendable`
     */
    fun toString(uDecimal: Long, appendable: Appendable)
}
