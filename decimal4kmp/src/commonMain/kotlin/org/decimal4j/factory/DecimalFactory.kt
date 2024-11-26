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
package org.decimal4j.factory

import org.decimal4j.api.Decimal
import org.decimal4j.api.ImmutableDecimal
import org.decimal4j.api.MutableDecimal
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.RoundingMode
import kotlin.reflect.KClass

/**
 * Factory for [Decimal] values and Decimal arrays of the
 * [scale][.getScale] defined by `<S>`.
 *
 * @param <S>
 * the [scale metrics][.getScaleMetrics] type associated with
 * decimals created by this factory
</S> */
interface DecimalFactory<S : ScaleMetrics> {
    /**
     * Returns the scale metrics type associated with Decimal values created by
     * this factory.
     *
     * @return the scale metrics defining the scale for Decimal values created
     * by this factory
     */
    val scaleMetrics: S

    /**
     * Returns the scale of values created by this factory.
     *
     * @return the scale for Decimal values created by this factory
     */
    val scale: Int

    /**
     * Returns the implementing class for immutable values.
     *
     * @return the implementation type for immutable Decimal values
     */
    fun immutableType(): KClass<out ImmutableDecimal<S>>

    /**
     * Returns the implementing class for mutable values.
     *
     * @return the implementation type for mutable Decimal values
     */
    fun mutableType(): KClass<out MutableDecimal<S>>

    /**
     * Returns a factory for the given `scale`.
     *
     * @param scale
     * the scale of Decimal numbers created by the returned factory
     * @return a decimal factory for numbers with the given scale
     */
    fun deriveFactory(scale: Int): DecimalFactory<*>

    /**
     * Returns a factory for the given `scaleMetrics`.
     *
     * @param scaleMetrics
     * the metrics defining the scale of the Decimal numbers created
     * by the returned factory
     * @param <S>
     * the generic type for `scaleMetrics`
     * @return a decimal factory for numbers with the scale specified by
     * `scaleMetrics`
    </S> */
    fun <S : ScaleMetrics> deriveFactory(scaleMetrics: S): DecimalFactory<S>

    /**
     * Returns a new immutable Decimal whose value is numerically equal to that
     * of the specified `long` value. An exception is thrown if the
     * specified value is too large to be represented as a Decimal of this
     * factory's [scale][.getScale].
     *
     * @param value
     * long value to convert into an immutable Decimal value
     * @return a Decimal value numerically equal to the specified `long`
     * value
     * @throws IllegalArgumentException
     * if `value` is too large to be represented as a Decimal
     * with the scale of this factory
     */
    fun valueOf(value: Long): ImmutableDecimal<S>

    /**
     * Returns a new immutable Decimal whose value is calculated by rounding the
     * specified `float` argument to the [scale][.getScale] of this
     * factory using [HALF_UP][RoundingMode.HALF_UP] rounding. An exception
     * is thrown if the specified value is too large to be represented as a
     * Decimal of this factory's scale.
     *
     * @param value
     * float value to convert into an immutable Decimal value
     * @return a Decimal calculated as: `round<sub>HALF_UP</sub>(value)`
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is
     * too large for the float to be represented as a
     * `Decimal` with the scale of this factory
     */
    fun valueOf(value: Float): ImmutableDecimal<S>

    /**
     * Returns a new immutable Decimal whose value is calculated by rounding the
     * specified `float` argument to the [scale][.getScale] of this
     * factory using the specified `roundingMode`. An exception is thrown
     * if the specified value is too large to be represented as a Decimal of
     * this factory's scale.
     *
     * @param value
     * float value to convert into an immutable Decimal value
     * @param roundingMode
     * the rounding mode to apply during the conversion if necessary
     * @return a Decimal calculated as: `round(value)`
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is
     * too large for the float to be represented as a
     * `Decimal` with the scale of this factory
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is
     * necessary
     */
    fun valueOf(value: Float, roundingMode: RoundingMode): ImmutableDecimal<S>

    /**
     * Returns a new immutable Decimal whose value is calculated by rounding the
     * specified `double` argument to the [scale][.getScale] of
     * this factory using [HALF_UP][RoundingMode.HALF_UP] rounding. An
     * exception is thrown if the specified value is too large to be represented
     * as a Decimal of this factory's scale.
     *
     * @param value
     * double value to convert into an immutable Decimal value
     * @return a Decimal calculated as: `round<sub>HALF_UP</sub>(value)`
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is
     * too large for the double to be represented as a
     * `Decimal` with the scale of this factory
     */
    fun valueOf(value: Double): ImmutableDecimal<S>

    /**
     * Returns a new immutable Decimal whose value is calculated by rounding the
     * specified `double` argument to the [scale][.getScale] of
     * this factory using the specified `roundingMode`. An exception is
     * thrown if the specified value is too large to be represented as a Decimal
     * of this factory's scale.
     *
     * @param value
     * double value to convert into an immutable Decimal value
     * @param roundingMode
     * the rounding mode to apply during the conversion if necessary
     * @return a Decimal calculated as: `round(value)`
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is
     * too large for the double to be represented as a
     * `Decimal` with the scale of this factory
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is
     * necessary
     */
    fun valueOf(value: Double, roundingMode: RoundingMode): ImmutableDecimal<S>





    /**
     * Returns a new immutable Decimal whose value is calculated by rounding the
     * specified [Decimal] argument to the [scale][.getScale] of
     * this factory using [HALF_UP][RoundingMode.HALF_UP] rounding. An
     * exception is thrown if the specified value is too large to be represented
     * as a Decimal of this factory's scale.
     *
     * @param value
     * Decimal value to convert into an immutable Decimal value of
     * this factory's scale
     * @return a Decimal calculated as: `round<sub>HALF_UP</sub>(value)`
     * @throws IllegalArgumentException
     * if `value` too large to be represented as a Decimal
     * with the scale of this factory
     */
    fun valueOf(value: Decimal<*>): ImmutableDecimal<S>

    /**
     * Returns a new immutable Decimal whose value is calculated by rounding the
     * specified [Decimal] argument to the [scale][.getScale] of
     * this factory using the specified `roundingMode`. An exception is
     * thrown if the specified value is too large to be represented as a Decimal
     * of this factory's scale.
     *
     * @param value
     * Decimal value to convert into an immutable Decimal value of
     * this factory's scale
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
    fun valueOf(value: Decimal<*>, roundingMode: RoundingMode): ImmutableDecimal<S>

    /**
     * Translates the string representation of a `Decimal` into an
     * immutable `Decimal`. The string representation consists of an
     * optional sign, `'+'` or `'-'` , followed by a sequence of
     * zero or more decimal digits ("the integer"), optionally followed by a
     * fraction.
     *
     *
     * The fraction consists of a decimal point followed by zero or more decimal
     * digits. The string must contain at least one digit in either the integer
     * or the fraction. If the fraction contains more digits than this factory's
     * [scale][.getScale], the value is rounded using
     * [HALF_UP][RoundingMode.HALF_UP] rounding. An exception is thrown if
     * the value is too large to be represented as a Decimal of this factory's
     * scale.
     *
     * @param value
     * String value to convert into an immutable Decimal value of
     * this factory's scale
     * @return a Decimal calculated as: `round<sub>HALF_UP</sub>(value)`
     * @throws NumberFormatException
     * if `value` does not represent a valid `Decimal`
     * or if the value is too large to be represented as a Decimal
     * with the scale of this factory
     */
    fun parse(value: String): ImmutableDecimal<S>

    /**
     * Translates the string representation of a `Decimal` into an
     * immutable `Decimal`. The string representation consists of an
     * optional sign, `'+'` or `'-'` , followed by a sequence of
     * zero or more decimal digits ("the integer"), optionally followed by a
     * fraction.
     *
     *
     * The fraction consists of a decimal point followed by zero or more decimal
     * digits. The string must contain at least one digit in either the integer
     * or the fraction. If the fraction contains more digits than this factory's
     * [scale][.getScale], the value is rounded using the specified
     * `roundingMode`. An exception is thrown if the value is too large to
     * be represented as a Decimal of this factory's scale.
     *
     * @param value
     * String value to convert into an immutable Decimal value of
     * this factory's scale
     * @param roundingMode
     * the rounding mode to apply if the fraction contains more
     * digits than the scale of this factory
     * @return a Decimal calculated as: `round(value)`
     * @throws NumberFormatException
     * if `value` does not represent a valid `Decimal`
     * or if the value is too large to be represented as a Decimal
     * with the scale of this factory
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is
     * necessary
     */
    fun parse(value: String, roundingMode: RoundingMode): ImmutableDecimal<S>

    /**
     * Returns a new immutable Decimal whose value is numerically equal to
     * `(unscaled  10<sup>-scale</sup>)` where `scale`
     * refers to this factory's [scale][.getScale].
     *
     * @param unscaled
     * unscaled value to convert into an immutable Decimal value
     * @return a Decimal calculated as:
     * `unscaled  10<sup>-scale</sup>`
     */
    fun valueOfUnscaled(unscaled: Long): ImmutableDecimal<S>

    /**
     * Returns a new immutable Decimal whose value is numerically equal to
     * `(unscaled  10<sup>-scale</sup>)`. The result is rounded to
     * the [scale][.getScale] of this factory using
     * [HALF_UP][RoundingMode.HALF_UP] rounding. An exception is thrown if
     * the specified value is too large to be represented as a Decimal of this
     * factory's scale.
     *
     * @param unscaled
     * unscaled value to convert into an immutable Decimal value
     * @param scale
     * the scale to apply to the `unscaled` value
     * @return a Decimal calculated as:
     * `round<sub>HALF_UP</sub>(unscaled  10<sup>-scale</sup>)`
     * @throws IllegalArgumentException
     * if `value` too large to be represented as a Decimal of
     * this factory's scale
     */
    fun valueOfUnscaled(unscaled: Long, scale: Int): ImmutableDecimal<S>

    /**
     * Returns a new immutable Decimal whose value is numerically equal to
     * `(unscaled  10<sup>-scale</sup>)`. The result is rounded to
     * the [scale][.getScale] of this factory using the specified
     * `roundingMode`. An exception is thrown if the specified value is
     * too large to be represented as a Decimal of this factory's scale.
     *
     * @param unscaled
     * unscaled value to convert into an immutable Decimal value
     * @param scale
     * the scale to apply to the `unscaled` value
     * @param roundingMode
     * the rounding mode to apply during the conversion if necessary
     * @return a Decimal calculated as:
     * `round(unscaled  10<sup>-scale</sup>)`
     * @throws IllegalArgumentException
     * if `value` too large to be represented as a Decimal of
     * this factory's scale
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is
     * necessary
     */
    fun valueOfUnscaled(unscaled: Long, scale: Int, roundingMode: RoundingMode): ImmutableDecimal<S>

    /**
     * Creates a one dimensional array of the specified `length` for
     * immutable Decimal values.
     *
     * @param length
     * the length of the returned array
     * @return a new array of the specified length
     */
    fun newArray(length: Int): Array<out ImmutableDecimal<S>?>

    /**
     * Creates a new mutable value initialized with zero.
     *
     * @return a new mutable Decimal value representing zero.
     */
    fun newMutable(): MutableDecimal<S>

    /**
     * Creates a one dimensional array of the specified `length` for
     * mutable Decimal values.
     *
     * @param length
     * the length of the returned array
     * @return a new array of the specified length
     */
    fun newMutableArray(length: Int): Array<out MutableDecimal<S>?>
}
