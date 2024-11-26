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
import org.decimal4j.truncate.RoundingMode

/**
 * Interface implemented by immutable [Decimal] classes of different
 * scales. Immutable Decimals allocate a new Decimals instance for results of
 * arithmetic operations.
 *
 *
 * Consider also [MutableDecimal] descendants especially for chained
 * operations.
 *
 *
 * Immutable Decimals are thread safe.
 *
 * @param <S>
 * the scale metrics type associated with this Decimal
</S> */
interface ImmutableDecimal<S : ScaleMetrics> : Decimal<S> {
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
    fun min(`val`: ImmutableDecimal<S>): ImmutableDecimal<S>

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
    fun max(`val`: ImmutableDecimal<S>): ImmutableDecimal<S>

    //override some methods with specialized return type
    override fun integralPart(): ImmutableDecimal<S>

    override fun fractionalPart(): ImmutableDecimal<S>

    override fun round(precision: Int): ImmutableDecimal<S>

    override fun round(precision: Int, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun round(precision: Int, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun scale(scale: Int): ImmutableDecimal<*>

    override fun <S : ScaleMetrics> scale(scaleMetrics: S): ImmutableDecimal<S>

    override fun scale(scale: Int, roundingMode: RoundingMode): ImmutableDecimal<*>

    override fun <S : ScaleMetrics> scale(scaleMetrics: S, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun add(augend: Decimal<S>): ImmutableDecimal<S>

    override fun add(augend: Decimal<S>, overflowMode: OverflowMode): ImmutableDecimal<S>

    override fun add(augend: Decimal<*>, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun add(augend: Decimal<*>, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun add(augend: Long): ImmutableDecimal<S>

    override fun add(augend: Long, overflowMode: OverflowMode): ImmutableDecimal<S>

    override fun add(augend: Double): ImmutableDecimal<S>

    override fun add(augend: Double, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun addUnscaled(unscaledAugend: Long): ImmutableDecimal<S>

    override fun addUnscaled(unscaledAugend: Long, overflowMode: OverflowMode): ImmutableDecimal<S>

    override fun addUnscaled(unscaledAugend: Long, scale: Int): ImmutableDecimal<S>

    override fun addUnscaled(unscaledAugend: Long, scale: Int, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun addUnscaled(unscaledAugend: Long, scale: Int, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun addSquared(value: Decimal<S>): ImmutableDecimal<S>

    override fun addSquared(value: Decimal<S>, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun addSquared(value: Decimal<S>, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun subtract(subtrahend: Decimal<S>): ImmutableDecimal<S>

    override fun subtract(subtrahend: Decimal<S>, overflowMode: OverflowMode): ImmutableDecimal<S>

    override fun subtract(subtrahend: Decimal<*>, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun subtract(subtrahend: Decimal<*>, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun subtract(subtrahend: Long): ImmutableDecimal<S>

    override fun subtract(subtrahend: Long, overflowMode: OverflowMode): ImmutableDecimal<S>

    override fun subtract(subtrahend: Double): ImmutableDecimal<S>

    override fun subtract(subtrahend: Double, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun subtractUnscaled(unscaledSubtrahend: Long): ImmutableDecimal<S>

    override fun subtractUnscaled(unscaledSubtrahend: Long, overflowMode: OverflowMode): ImmutableDecimal<S>

    override fun subtractUnscaled(unscaledSubtrahend: Long, scale: Int): ImmutableDecimal<S>

    override fun subtractUnscaled(unscaledSubtrahend: Long, scale: Int, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun subtractUnscaled(
        unscaledSubtrahend: Long,
        scale: Int,
        truncationPolicy: TruncationPolicy
    ): ImmutableDecimal<S>

    override fun subtractSquared(value: Decimal<S>): ImmutableDecimal<S>

    override fun subtractSquared(value: Decimal<S>, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun subtractSquared(value: Decimal<S>, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun multiply(multiplicand: Decimal<S>): ImmutableDecimal<S>

    override fun multiply(multiplicand: Decimal<S>, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun multiply(multiplicand: Decimal<S>, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun multiplyBy(multiplicand: Decimal<*>): ImmutableDecimal<S>

    override fun multiplyBy(multiplicand: Decimal<*>, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun multiplyBy(multiplicand: Decimal<*>, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun multiplyExact(multiplicand: Decimal<*>): ImmutableDecimal<*>

    override fun multiply(multiplicand: Long): ImmutableDecimal<S>

    override fun multiply(multiplicand: Long, overflowMode: OverflowMode): ImmutableDecimal<S>

    override fun multiply(multiplicand: Double): ImmutableDecimal<S>

    override fun multiply(multiplicand: Double, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun multiplyUnscaled(unscaledMultiplicand: Long): ImmutableDecimal<S>

    override fun multiplyUnscaled(unscaledMultiplicand: Long, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun multiplyUnscaled(unscaledMultiplicand: Long, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun multiplyUnscaled(unscaledMultiplicand: Long, scale: Int): ImmutableDecimal<S>

    override fun multiplyUnscaled(
        unscaledMultiplicand: Long,
        scale: Int,
        roundingMode: RoundingMode
    ): ImmutableDecimal<S>

    override fun multiplyUnscaled(
        unscaledMultiplicand: Long,
        scale: Int,
        truncationPolicy: TruncationPolicy
    ): ImmutableDecimal<S>

    override fun multiplyByPowerOfTen(n: Int): ImmutableDecimal<S>

    override fun multiplyByPowerOfTen(n: Int, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun multiplyByPowerOfTen(n: Int, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun divide(divisor: Decimal<S>): ImmutableDecimal<S>

    override fun divide(divisor: Decimal<S>, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun divide(divisor: Decimal<S>, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun divideBy(divisor: Decimal<*>): ImmutableDecimal<S>

    override fun divideBy(divisor: Decimal<*>, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun divideBy(divisor: Decimal<*>, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun divideTruncate(divisor: Decimal<S>): ImmutableDecimal<S>

    override fun divideExact(divisor: Decimal<S>): ImmutableDecimal<S>

    override fun divide(divisor: Long): ImmutableDecimal<S>

    override fun divide(divisor: Long, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun divide(divisor: Long, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun divide(divisor: Double): ImmutableDecimal<S>

    override fun divide(divisor: Double, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun divideUnscaled(unscaledDivisor: Long): ImmutableDecimal<S>

    override fun divideUnscaled(unscaledDivisor: Long, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun divideUnscaled(unscaledDivisor: Long, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun divideUnscaled(unscaledDivisor: Long, scale: Int): ImmutableDecimal<S>

    override fun divideUnscaled(unscaledDivisor: Long, scale: Int, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun divideUnscaled(
        unscaledDivisor: Long,
        scale: Int,
        truncationPolicy: TruncationPolicy
    ): ImmutableDecimal<S>

    override fun divideByPowerOfTen(n: Int): ImmutableDecimal<S>

    override fun divideByPowerOfTen(n: Int, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun divideByPowerOfTen(n: Int, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun divideToIntegralValue(divisor: Decimal<S>): ImmutableDecimal<S>

    override fun divideToIntegralValue(divisor: Decimal<S>, overflowMode: OverflowMode): ImmutableDecimal<S>

    override fun divideAndRemainder(divisor: Decimal<S>): Array<out ImmutableDecimal<S>?>

    override fun divideAndRemainder(divisor: Decimal<S>, overflowMode: OverflowMode): Array<out ImmutableDecimal<S>?>

    override fun remainder(divisor: Decimal<S>): ImmutableDecimal<S>

    override fun negate(): ImmutableDecimal<S>

    override fun negate(overflowMode: OverflowMode): ImmutableDecimal<S>

    override fun abs(): ImmutableDecimal<S>

    override fun abs(overflowMode: OverflowMode): ImmutableDecimal<S>

    override fun invert(): ImmutableDecimal<S>

    override fun invert(roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun invert(truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun square(): ImmutableDecimal<S>

    override fun square(roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun square(truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun sqrt(): ImmutableDecimal<S>

    override fun sqrt(roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun shiftLeft(n: Int): ImmutableDecimal<S>

    override fun shiftLeft(n: Int, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun shiftLeft(n: Int, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun shiftRight(n: Int): ImmutableDecimal<S>

    override fun shiftRight(n: Int, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun shiftRight(n: Int, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun pow(n: Int): ImmutableDecimal<S>

    override fun pow(n: Int, roundingMode: RoundingMode): ImmutableDecimal<S>

    override fun pow(n: Int, truncationPolicy: TruncationPolicy): ImmutableDecimal<S>

    override fun avg(`val`: Decimal<S>): ImmutableDecimal<S>

    override fun avg(`val`: Decimal<S>, roundingMode: RoundingMode): ImmutableDecimal<S>
}
