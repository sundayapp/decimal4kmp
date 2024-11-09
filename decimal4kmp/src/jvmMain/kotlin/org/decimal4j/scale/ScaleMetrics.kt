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
package org.decimal4j.scale

import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.truncate.TruncationPolicy
import java.math.BigDecimal
import java.math.BigInteger
import org.decimal4j.api.RoundingMode

/**
 * `ScaleMetrics` defines various metrics associated with the scale of a [Decimal] number. It is mainly
 * used internally from code implementing the arithmetic operations of a `Decimal`.
 *
 *
 * The [scale][.getScale] determines the number of fraction digits of the `Decimal`. The
 * [scale factor][.getScaleFactor] is the multiplier/divisor for conversions between the `Decimal` value
 * and the unscaled `long` value underlying every `Decimal`.
 *
 *
 * Operations such as [multiplyByScaleFactor(..)][.multiplyByScaleFactor] are defined here as separate
 * methods to allow for compiler optimizations. Multiplications and divisions are for instance translated into shifts
 * and adds by the compiler instead of the more expensive multiplication and division operations with non-constant long
 * values.
 *
 *
 * `ScaleMetrics` also provides access to [DecimalArithmetic] instances for different rounding modes and
 * overflow policies. `DecimalArithmetic` objects can be used to deal with `Decimal` numbers in their
 * *primitive* form, meaning that `Decimal` numbers are passed to the arithmetic class as unscaled
 * `long` values.
 */
interface ScaleMetrics {
    /**
     * Returns the scale, the number of fraction digits to the right of the decimal point of a [Decimal] value.
     *
     * @return the scale also known as number of fraction digits
     */
    fun getScale(): Int

    /**
     * Returns the scale factor, which is 10<sup>f</sup> where `f` stands for the [scale][.getScale].
     *
     * @return the scale factor
     */
    fun getScaleFactor(): Long

    /**
     * Returns the [scale factor][.getScaleFactor] as a [BigInteger] value.
     *
     * @return the scale factor as big integer
     */
    fun getScaleFactorAsBigInteger(): BigInteger?

    /**
     * Returns the [scale factor][.getScaleFactor] as a [BigDecimal] value with scale zero.
     *
     * @return the scale factor as big decimal with scale zero.
     */
    fun getScaleFactorAsBigDecimal(): BigDecimal?


    /**
     * Returns the number of leading zeros of the scale factor
     *
     * @return [Long.numberOfLeadingZeros] applied to the scale factor
     */
    fun getScaleFactorNumberOfLeadingZeros(): Int

    /**
     * Returns the largest integer value that can be represented using this scale.
     *
     * @return `Long.MAX_VALUE / scaleFactor`
     */
    fun getMaxIntegerValue(): Long

    /**
     * Returns the smallest integer value that can be represented using this scale.
     *
     * @return `Long.MIN_VALUE / scaleFactor`
     */
    fun getMinIntegerValue(): Long

    /**
     * Returns true if the specified integer `value` can be represented using this scale.
     *
     * @param value
     * the value to test
     * @return true if `(Long.MIN_VALUE / scaleFactor) <= value <= (Long.MAX_VALUE / scaleFactor)`
     */
    fun isValidIntegerValue(value: Long): Boolean

    /**
     * Returns `factor*scaleFactor`.
     *
     * @param factor
     * the factor
     * @return `factor*scaleFactor`
     */
    fun multiplyByScaleFactor(factor: Long): Long

    /**
     * Returns `factor*scaleFactor`, checking for lost information. If the result is out of the range of the
     * `long` type, then an `ArithmeticException` is thrown.
     *
     * @param factor
     * the factor
     * @return `factor*scaleFactor`
     * @throws ArithmeticException
     * if an overflow occurs
     */
    fun multiplyByScaleFactorExact(factor: Long): Long

    /**
     * Returns `factor*low32(scaleFactor)` where low32 refers to the low 32 bits of the factor.
     *
     * @param factor
     * the factor
     * @return `factor*low32(scaleFactor)`
     */
    fun mulloByScaleFactor(factor: Int): Long

    /**
     * Returns `factor*high32(scaleFactor)` where high32 refers to the high 32 bits of the factor.
     *
     * @param factor
     * the factor
     * @return `factor*high32(scaleFactor)`
     */
    fun mulhiByScaleFactor(factor: Int): Long

    /**
     * Returns `dividend/scaleFactor`.
     *
     * @param dividend
     * the dividend
     * @return `dividend/scaleFactor`
     */
    fun divideByScaleFactor(dividend: Long): Long

    /**
     * Returns `unsignedDividend/scaleFactor` using unsigned division.
     *
     * @param unsignedDividend
     * the unsigned dividend
     * @return `unsignedDividend/scaleFactor`
     */
    fun divideUnsignedByScaleFactor(unsignedDividend: Long): Long

    /**
     * Returns `dividend % scaleFactor` also known as reminder.
     *
     * @param dividend
     * the dividend
     * @return `dividend % scaleFactor`
     */
    fun moduloByScaleFactor(dividend: Long): Long

    /**
     * Returns the string representation of the specified `value` applying this metric's scale.
     *
     * @param value
     * the unscaled decimal to convert to a string
     * @return a fixed-point string representation of the specified value
     * @see DecimalArithmetic.toString
     */
    fun toString(value: Long): String

    /**
     * Returns the default arithmetic for this scale performing unchecked operations with rounding mode
     * [HALF_UP][RoundingMode.HALF_UP].
     *
     * @return default arithmetic for this scale rounding HALF_UP without overflow checks
     */
    fun getDefaultArithmetic(): DecimalArithmetic

    /**
     * Returns the default arithmetic for this scale performing checked operations with rounding mode
     * [HALF_UP][RoundingMode.HALF_UP].
     *
     * @return default arithmetic for this scale rounding HALF_UP with overflow checks
     */
    fun getDefaultCheckedArithmetic(): DecimalArithmetic

    /**
     * Returns the arithmetic for this scale performing unchecked operations with rounding mode [ DOWN][RoundingMode.DOWN].
     *
     * @return arithmetic for this scale rounding DOWN without overflow checks
     */
    fun getRoundingDownArithmetic(): DecimalArithmetic

    /**
     * Returns the arithmetic for this scale performing unchecked operations with rounding mode
     * [FLOOR][RoundingMode.FLOOR].
     *
     * @return arithmetic for this scale rounding FLOOR without overflow checks
     */
    fun getRoundingFloorArithmetic(): DecimalArithmetic

    /**
     * Returns the arithmetic for this scale performing unchecked operations with rounding mode
     * [HALF_EVEN][RoundingMode.HALF_EVEN].
     *
     * @return arithmetic for this scale rounding HALF_EVEN without overflow checks
     */
    fun getRoundingHalfEvenArithmetic(): DecimalArithmetic

    /**
     * Returns the arithmetic for this scale performing unchecked operations with rounding mode
     * [UNNECESSARY][RoundingMode.UNNECESSARY].
     *
     * @return default arithmetic for this scale for rounding UNNECESSARY mode without overflow checks
     */
    fun getRoundingUnnecessaryArithmetic(): DecimalArithmetic

    /**
     * Returns the arithmetic for this scale that performs all operations with the specified `roundingMode` and
     * without overflow checks.
     *
     * @param roundingMode
     * the rounding mode used by the returned arithmetic
     * @return arithmetic for this scale with specified rounding mode and without overflow checks
     */
    fun getArithmetic(roundingMode: RoundingMode): DecimalArithmetic

    /**
     * Returns the arithmetic for this scale that performs all operations with the specified `roundingMode` and
     * with overflow checks.
     *
     * @param roundingMode
     * the rounding mode used by the returned arithmetic
     * @return arithmetic for this scale with specified rounding mode and with overflow checks
     */
    fun getCheckedArithmetic(roundingMode: RoundingMode): DecimalArithmetic

    /**
     * Returns the arithmetic for this scale that performs all operations with the specified `truncationPolicy`.
     *
     * @param truncationPolicy
     * the truncation policy used by the returned arithmetic
     * @return arithmetic for this scale with specified truncation policy
     */
    fun getArithmetic(truncationPolicy: TruncationPolicy): DecimalArithmetic
}
