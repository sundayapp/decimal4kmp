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
package org.decimal4j.base

import org.decimal4j.api.Decimal
import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.CheckedRounding
import org.decimal4j.truncate.OverflowMode
import org.decimal4j.truncate.TruncationPolicy
import org.decimal4j.truncate.UncheckedRounding
import org.decimal4j.truncate.RoundingMode
import kotlin.math.sign

/**
 * Common base class for [immutable][AbstractImmutableDecimal] and
 * [mutable][AbstractMutableDecimal] [Decimal] numbers of different
 * scales.
 *
 * @param <S>
 * the scale metrics type associated with this decimal
 * @param <D>
 * the concrete class implementing this decimal
</D></S> */
abstract class AbstractDecimal<S : ScaleMetrics, D : AbstractDecimal<S, D>>
    : Number(), Decimal<S> {
    /**
     * Returns this or a new `Decimal` whose value is
     * `(unscaled  10<sup>-scale</sup>)`.
     *
     *
     * The returned value is a new instance if this decimal is an
     * [ImmutableDecimal]. If it is a [MutableDecimal] then its
     * internal state is altered and `this` is returned as result now
     * representing `(unscaled  10<sup>-scale</sup>)`.
     *
     * @param unscaled
     * unscaled value to be returned as a `Decimal`
     * @return `unscaled  10<sup>-scale</sup>`
     */
    protected abstract fun createOrAssign(unscaled: Long): D

    /**
     * Returns a new `Decimal` whose value is
     * `(unscaled  10<sup>-scale</sup>)`.
     *
     * @param unscaled
     * unscaled value to be returned as a `Decimal`
     * @return `unscaled  10<sup>-scale</sup>`
     */
    protected abstract fun create(unscaled: Long): D

    /**
     * Returns a new `Decimal` array of the specified `length`.
     *
     * @param length
     * the length of the array to return
     * @return `new D[length]`
     */
    protected abstract fun createArray(length: Int): Array<D?>

    /**
     * Returns `this` decimal value as concrete implementation subtype.
     *
     * @return `this`
     */
    abstract fun self(): D

    override val defaultArithmetic: DecimalArithmetic
        /**
         * Returns the default arithmetic performing unchecked operations with
         * rounding mode [HALF_UP][RoundingMode.HALF_UP].
         *
         * @return default arithmetic with [HALF_UP][RoundingMode.HALF_UP]
         * rounding and [UNCHECKED][OverflowMode.UNCHECKED] overflow
         * mode
         */
        get() = scaleMetrics.getDefaultArithmetic()

    open val defaultCheckedArithmetic: DecimalArithmetic
        /**
         * Returns the default arithmetic performing checked operations with
         * rounding mode [HALF_UP][RoundingMode.HALF_UP].
         *
         * @return default arithmetic with [HALF_UP][RoundingMode.HALF_UP]
         * rounding and [CHECKED][OverflowMode.CHECKED] overflow mode
         */
        get() = scaleMetrics.getDefaultCheckedArithmetic()

    /**
     * Returns the default arithmetic performing checked operations with
     * rounding mode [HALF_UP][RoundingMode.HALF_UP] and the specified
     * `overflowMode`.
     *
     * @param overflowMode
     * the overflow for the returned arithmetic
     *
     * @return default arithmetic with [HALF_UP][RoundingMode.HALF_UP]
     * rounding and the given `overflowMode`
     */
    protected fun getArithmeticFor(overflowMode: OverflowMode): DecimalArithmetic {
        return scaleMetrics.getArithmetic(if (overflowMode == OverflowMode.CHECKED) CheckedRounding.HALF_UP else UncheckedRounding.HALF_UP)
    }

    protected open val roundingDownArithmetic: DecimalArithmetic
        /**
         * Returns the arithmetic performing unchecked operations with rounding mode
         * [DOWN][RoundingMode.DOWN].
         *
         * @return arithmetic with [DOWN][RoundingMode.DOWN] rounding and
         * [UNCHECKED][OverflowMode.UNCHECKED] overflow mode
         */
        get() = scaleMetrics.getRoundingDownArithmetic()

    protected open val roundingFloorArithmetic: DecimalArithmetic
        /**
         * Returns the arithmetic performing unchecked operations with rounding mode
         * [FLOOR][RoundingMode.FLOOR].
         *
         * @return arithmetic with [FLOOR][RoundingMode.FLOOR] rounding and
         * [UNCHECKED][OverflowMode.UNCHECKED] overflow mode
         */
        get() = scaleMetrics.getRoundingFloorArithmetic()

    protected open val roundingHalfEvenArithmetic: DecimalArithmetic
        /**
         * Returns the arithmetic performing unchecked operations with rounding mode
         * [HALF_EVEN][RoundingMode.HALF_EVEN].
         *
         * @return arithmetic with [HALF_UP][RoundingMode.HALF_UP] rounding and
         * [UNCHECKED][OverflowMode.UNCHECKED] overflow mode
         */
        get() = scaleMetrics.getRoundingHalfEvenArithmetic()

    protected open val roundingUnnecessaryArithmetic: DecimalArithmetic
        /**
         * Returns the arithmetic performing unchecked operations with rounding mode
         * [UNNECESSARY][RoundingMode.UNNECESSARY].
         *
         * @return arithmetic with [UNNECESSARY][RoundingMode.UNNECESSARY]
         * rounding and [UNCHECKED][OverflowMode.UNCHECKED] overflow
         * mode
         */
        get() = scaleMetrics.getRoundingUnnecessaryArithmetic()

    /**
     * Returns the arithmetic performing unchecked operations with the specified
     * [RoundingMode].
     *
     * @param roundingMode
     * the rounding for the returned arithmetic
     * @return arithmetic with specified `roundingMode` and
     * [UNCHECKED][OverflowMode.UNCHECKED] overflow mode
     */
    override fun getArithmeticFor(roundingMode: RoundingMode): DecimalArithmetic {
        return scaleMetrics.getArithmetic(roundingMode)
    }

    /**
     * Returns the arithmetic performing checked operations with the specified
     * [RoundingMode].
     *
     * @param roundingMode
     * the rounding for the returned arithmetic
     * @return arithmetic with specified `roundingMode` and
     * [CHECKED][OverflowMode.CHECKED] overflow mode
     */
    fun getCheckedArithmeticFor(roundingMode: RoundingMode): DecimalArithmetic {
        return scaleMetrics.getCheckedArithmetic(roundingMode)
    }

    /**
     * Returns the arithmetic for the specified `truncationPolicy`.
     *
     * @param truncationPolicy
     * the truncation policy for the returned arithmetic
     * @return arithmetic performing operations according to the given
     * `truncationPolicy`
     */
    protected fun getArithmeticFor(truncationPolicy: TruncationPolicy): DecimalArithmetic {
        return scaleMetrics.getArithmetic(truncationPolicy)
    }

    override fun toByte(): Byte {
        return toLong().toByte()
    }

    override fun toShort(): Short {
        return toLong().toShort()
    }

    /* -------------------- Number and simular conversion ------------------- */
    override fun byteValueExact(): Byte {
        val num = longValueExact() // will check decimal part
        if (num.toByte().toLong() != num) {
            throw ArithmeticException("Overflow: $num is out of the possible range for a byte")
        }
        return num.toByte()
    }

    override fun shortValueExact(): Short {
        val num = longValueExact() // will check decimal part
        if (num.toShort().toLong() != num) {
            throw ArithmeticException("Overflow: $num is out of the possible range for a short")
        }
        return num.toShort()
    }

    override fun toInt(): Int {
        return toLong().toInt()
    }

    override fun intValueExact(): Int {
        val num = longValueExact() // will check decimal part
        if (num.toInt().toLong() != num) {
            throw ArithmeticException("Overflow: $num is out of the possible range for an int")
        }
        return num.toInt()
    }

    override fun toLong(): Long {
        return roundingDownArithmetic.toLong(unscaledValue())
    }

    override fun longValueExact(): Long {
        return roundingUnnecessaryArithmetic.toLong(unscaledValue())
    }

    override fun longValue(roundingMode: RoundingMode): Long {
        return getArithmeticFor(roundingMode).toLong(unscaledValue())
    }

    override fun toFloat(): Float {
        // NOTE: Must be HALF_EVEN rounding mode according to The Java Language
        // Specification
        // @see section 5.1.3 narrowing primitive conversion
        // @see section 4.2.3. Floating-Point Types, Formats, and Values
        // @see IEEE 754-1985 Standard for Binary Floating-Point Arithmetic
        return roundingHalfEvenArithmetic.toFloat(unscaledValue())
    }

    override fun floatValue(roundingMode: RoundingMode): Float {
        return getArithmeticFor(roundingMode).toFloat(unscaledValue())
    }

    override fun toDouble(): Double {
        // NOTE: Must be HALF_EVEN rounding mode according to The Java Language
        // Specification
        // @see section 5.1.3 narrowing primitive conversion
        // @see section 4.2.3. Floating-Point Types, Formats, and Values
        // @see IEEE 754-1985 Standard for Binary Floating-Point Arithmetic
        return roundingHalfEvenArithmetic.toDouble(unscaledValue())
    }

    override fun doubleValue(roundingMode: RoundingMode): Double {
        return getArithmeticFor(roundingMode).toDouble(unscaledValue())
    }

    override fun integralPart(): D {
        val unscaled = unscaledValue()
        val integral = unscaled - scaleMetrics.moduloByScaleFactor(unscaled)
        return createOrAssign(integral)
    }

    override fun fractionalPart(): D {
        return createOrAssign(scaleMetrics.moduloByScaleFactor(unscaledValue()))
    }

    /* ----------------------------- rounding ------------------------------ */
    override fun round(precision: Int): D {
        if (precision < scale) {
            return createOrAssign(defaultArithmetic.round(unscaledValue(), precision))
        }
        return self()
    }

    override fun round(precision: Int, roundingMode: RoundingMode): D {
        if (precision < scale) {
            return createOrAssign(getArithmeticFor(roundingMode).round(unscaledValue(), precision))
        }
        return self()
    }

    override fun round(precision: Int, truncationPolicy: TruncationPolicy): D {
        if (precision < scale) {
            return createOrAssign(getArithmeticFor(truncationPolicy).round(unscaledValue(), precision))
        }
        return self()
    }

    /* -------------------------------- add -------------------------------- */
    override fun add(augend: Decimal<S>): D {
        return addUnscaled(augend.unscaledValue())
    }

    override fun add(augend: Decimal<S>, overflowMode: OverflowMode): D {
        return addUnscaled(augend.unscaledValue(), overflowMode)
    }

    override fun add(augend: Decimal<*>, roundingMode: RoundingMode): D {
        return addUnscaled(augend.unscaledValue(), augend.scale, roundingMode)
    }

    override fun add(augend: Decimal<*>, truncationPolicy: TruncationPolicy): D {
        return addUnscaled(augend.unscaledValue(), augend.scale, truncationPolicy)
    }

    override fun add(augend: Long): D {
        return createOrAssign(defaultArithmetic.addLong(unscaledValue(), augend))
    }

    override fun add(augend: Long, overflowMode: OverflowMode): D {
        return createOrAssign(getArithmeticFor(overflowMode).addLong(unscaledValue(), augend))
    }

    override fun add(augend: Double): D {
        val arith = defaultCheckedArithmetic
        return createOrAssign(arith.add(unscaledValue(), arith.fromDouble(augend)))
    }

    override fun add(augend: Double, roundingMode: RoundingMode): D {
        val arith = getCheckedArithmeticFor(roundingMode)
        return createOrAssign(arith.add(unscaledValue(), arith.fromDouble(augend)))
    }

    override fun addUnscaled(unscaledAugend: Long): D {
        val arith = defaultArithmetic
        return createOrAssign(arith.add(unscaledValue(), unscaledAugend))
    }

    override fun addUnscaled(unscaledAugend: Long, overflowMode: OverflowMode): D {
        val arith = getArithmeticFor(overflowMode)
        return createOrAssign(arith.add(unscaledValue(), unscaledAugend))
    }

    override fun addUnscaled(unscaledAugend: Long, scale: Int): D {
        val arith = defaultArithmetic
        return createOrAssign(arith.addUnscaled(unscaledValue(), unscaledAugend, scale))
    }

    override fun addUnscaled(unscaledAugend: Long, scale: Int, roundingMode: RoundingMode): D {
        val arith = getArithmeticFor(roundingMode)
        return createOrAssign(arith.addUnscaled(unscaledValue(), unscaledAugend, scale))
    }

    override fun addUnscaled(unscaledAugend: Long, scale: Int, truncationPolicy: TruncationPolicy): D {
        val arith = getArithmeticFor(truncationPolicy)
        return createOrAssign(arith.addUnscaled(unscaledValue(), unscaledAugend, scale))
    }

    override fun addSquared(value: Decimal<S>): D {
        val arith = defaultArithmetic
        return createOrAssign(arith.add(unscaledValue(), arith.square(value.unscaledValue())))
    }

    override fun addSquared(value: Decimal<S>, roundingMode: RoundingMode): D {
        val arith = getArithmeticFor(roundingMode)
        return createOrAssign(arith.add(unscaledValue(), arith.square(value.unscaledValue())))
    }

    override fun addSquared(value: Decimal<S>, truncationPolicy: TruncationPolicy): D {
        val arith = getArithmeticFor(truncationPolicy)
        return createOrAssign(arith.add(unscaledValue(), arith.square(value.unscaledValue())))
    }

    /* ------------------------------ subtract ------------------------------ */
    override fun subtract(subtrahend: Decimal<S>): D {
        return subtractUnscaled(subtrahend.unscaledValue())
    }

    override fun subtract(subtrahend: Decimal<S>, overflowMode: OverflowMode): D {
        return subtractUnscaled(subtrahend.unscaledValue(), overflowMode)
    }

    override fun subtract(subtrahend: Decimal<*>, roundingMode: RoundingMode): D {
        return subtractUnscaled(subtrahend.unscaledValue(), subtrahend.scale, roundingMode)
    }

    override fun subtract(subtrahend: Decimal<*>, truncationPolicy: TruncationPolicy): D {
        return subtractUnscaled(subtrahend.unscaledValue(), subtrahend.scale, truncationPolicy)
    }

    override fun subtract(subtrahend: Long): D {
        return createOrAssign(defaultArithmetic.subtractLong(unscaledValue(), subtrahend))
    }

    override fun subtract(subtrahend: Long, overflowMode: OverflowMode): D {
        return createOrAssign(getArithmeticFor(overflowMode).subtractLong(unscaledValue(), subtrahend))
    }

    override fun subtract(subtrahend: Double): D {
        val arith = defaultCheckedArithmetic
        return createOrAssign(arith.subtract(unscaledValue(), arith.fromDouble(subtrahend)))
    }

    override fun subtract(subtrahend: Double, roundingMode: RoundingMode): D {
        val arith = getCheckedArithmeticFor(roundingMode)
        return createOrAssign(arith.subtract(unscaledValue(), arith.fromDouble(subtrahend)))
    }

    override fun subtractUnscaled(unscaledSubtrahend: Long): D {
        val arith = defaultArithmetic
        return createOrAssign(arith.subtract(unscaledValue(), unscaledSubtrahend))
    }

    override fun subtractUnscaled(unscaledSubtrahend: Long, overflowMode: OverflowMode): D {
        val arith = getArithmeticFor(overflowMode)
        return createOrAssign(arith.subtract(unscaledValue(), unscaledSubtrahend))
    }

    override fun subtractUnscaled(unscaledSubtrahend: Long, scale: Int): D {
        val arith = defaultArithmetic
        return createOrAssign(arith.subtractUnscaled(unscaledValue(), unscaledSubtrahend, scale))
    }

    override fun subtractUnscaled(unscaledSubtrahend: Long, scale: Int, roundingMode: RoundingMode): D {
        val arith = getArithmeticFor(roundingMode)
        return createOrAssign(arith.subtractUnscaled(unscaledValue(), unscaledSubtrahend, scale))
    }

    override fun subtractUnscaled(unscaledSubtrahend: Long, scale: Int, truncationPolicy: TruncationPolicy): D {
        val arith = getArithmeticFor(truncationPolicy)
        return createOrAssign(arith.subtractUnscaled(unscaledValue(), unscaledSubtrahend, scale))
    }

    override fun subtractSquared(value: Decimal<S>): D {
        val arith = defaultArithmetic
        return createOrAssign(arith.subtract(unscaledValue(), arith.square(value.unscaledValue())))
    }

    override fun subtractSquared(value: Decimal<S>, roundingMode: RoundingMode): D {
        val arith = getArithmeticFor(roundingMode)
        return createOrAssign(arith.subtract(unscaledValue(), arith.square(value.unscaledValue())))
    }

    override fun subtractSquared(value: Decimal<S>, truncationPolicy: TruncationPolicy): D {
        val arith = getArithmeticFor(truncationPolicy)
        return createOrAssign(arith.subtract(unscaledValue(), arith.square(value.unscaledValue())))
    }

    /* ------------------------------ multiply ------------------------------ */
    override fun multiply(multiplicand: Decimal<S>): D {
        return multiplyUnscaled(multiplicand.unscaledValue())
    }

    override fun multiply(multiplicand: Decimal<S>, roundingMode: RoundingMode): D {
        return multiplyUnscaled(multiplicand.unscaledValue(), roundingMode)
    }

    override fun multiply(multiplicand: Decimal<S>, truncationPolicy: TruncationPolicy): D {
        return multiplyUnscaled(multiplicand.unscaledValue(), truncationPolicy)
    }

    override fun multiplyBy(multiplicand: Decimal<*>): D {
        return multiplyUnscaled(multiplicand.unscaledValue(), multiplicand.scale)
    }

    override fun multiplyBy(multiplicand: Decimal<*>, roundingMode: RoundingMode): D {
        return multiplyUnscaled(multiplicand.unscaledValue(), multiplicand.scale, roundingMode)
    }

    override fun multiplyBy(multiplicand: Decimal<*>, truncationPolicy: TruncationPolicy): D {
        return multiplyUnscaled(multiplicand.unscaledValue(), multiplicand.scale, truncationPolicy)
    }

    override fun multiply(multiplicand: Long): D {
        val arith = defaultArithmetic
        return createOrAssign(arith.multiplyByLong(unscaledValue(), multiplicand))
    }

    override fun multiply(multiplicand: Long, overflowMode: OverflowMode): D {
        val arith = getArithmeticFor(overflowMode)
        return createOrAssign(arith.multiplyByLong(unscaledValue(), multiplicand))
    }

    override fun multiply(multiplicand: Double): D {
        val arith = defaultCheckedArithmetic
        return createOrAssign(arith.multiply(unscaledValue(), arith.fromDouble(multiplicand)))
    }

    override fun multiply(multiplicand: Double, roundingMode: RoundingMode): D {
        val arith = getCheckedArithmeticFor(roundingMode)
        return createOrAssign(arith.multiply(unscaledValue(), arith.fromDouble(multiplicand)))
    }

    override fun multiplyUnscaled(unscaledMultiplicand: Long): D {
        val arith = defaultArithmetic
        return createOrAssign(arith.multiply(unscaledValue(), unscaledMultiplicand))
    }

    override fun multiplyUnscaled(unscaledMultiplicand: Long, roundingMode: RoundingMode): D {
        val arith = getArithmeticFor(roundingMode)
        return createOrAssign(arith.multiply(unscaledValue(), unscaledMultiplicand))
    }

    override fun multiplyUnscaled(unscaledMultiplicand: Long, truncationPolicy: TruncationPolicy): D {
        val arith = getArithmeticFor(truncationPolicy)
        return createOrAssign(arith.multiply(unscaledValue(), unscaledMultiplicand))
    }

    override fun multiplyUnscaled(unscaledMultiplicand: Long, scale: Int): D {
        val arith = defaultArithmetic
        return createOrAssign(arith.multiplyByUnscaled(unscaledValue(), unscaledMultiplicand, scale))
    }

    override fun multiplyUnscaled(unscaledMultiplicand: Long, scale: Int, roundingMode: RoundingMode): D {
        val arith = getArithmeticFor(roundingMode)
        return createOrAssign(arith.multiplyByUnscaled(unscaledValue(), unscaledMultiplicand, scale))
    }

    override fun multiplyUnscaled(unscaledMultiplicand: Long, scale: Int, truncationPolicy: TruncationPolicy): D {
        val arith = getArithmeticFor(truncationPolicy)
        return createOrAssign(arith.multiplyByUnscaled(unscaledValue(), unscaledMultiplicand, scale))
    }

    override fun multiplyByPowerOfTen(n: Int): D {
        return createOrAssign(defaultArithmetic.multiplyByPowerOf10(unscaledValue(), n))
    }

    override fun multiplyByPowerOfTen(n: Int, roundingMode: RoundingMode): D {
        return createOrAssign(getArithmeticFor(roundingMode).multiplyByPowerOf10(unscaledValue(), n))
    }

    override fun multiplyByPowerOfTen(n: Int, truncationPolicy: TruncationPolicy): D {
        return createOrAssign(getArithmeticFor(truncationPolicy).multiplyByPowerOf10(unscaledValue(), n))
    }

    /* ------------------------------ divide ------------------------------ */
    override fun divide(divisor: Decimal<S>): D {
        return divideUnscaled(divisor.unscaledValue())
    }

    override fun divide(divisor: Decimal<S>, roundingMode: RoundingMode): D {
        return divideUnscaled(divisor.unscaledValue(), roundingMode)
    }

    override fun divide(divisor: Decimal<S>, truncationPolicy: TruncationPolicy): D {
        return divideUnscaled(divisor.unscaledValue(), truncationPolicy)
    }

    override fun divideBy(divisor: Decimal<*>): D {
        return divideUnscaled(divisor.unscaledValue(), divisor.scale)
    }

    override fun divideBy(divisor: Decimal<*>, roundingMode: RoundingMode): D {
        return divideUnscaled(divisor.unscaledValue(), divisor.scale, roundingMode)
    }

    override fun divideBy(divisor: Decimal<*>, truncationPolicy: TruncationPolicy): D {
        return divideUnscaled(divisor.unscaledValue(), divisor.scale, truncationPolicy)
    }

    override fun divide(divisor: Long): D {
        val arith = defaultArithmetic
        return createOrAssign(arith.divideByLong(unscaledValue(), divisor))
    }

    override fun divide(divisor: Long, roundingMode: RoundingMode): D {
        val arith = getArithmeticFor(roundingMode)
        return createOrAssign(arith.divideByLong(unscaledValue(), divisor))
    }

    override fun divide(divisor: Long, truncationPolicy: TruncationPolicy): D {
        val arith = getArithmeticFor(truncationPolicy)
        return createOrAssign(arith.divideByLong(unscaledValue(), divisor))
    }

    override fun divide(divisor: Double): D {
        val arith = defaultCheckedArithmetic
        return createOrAssign(arith.divide(unscaledValue(), arith.fromDouble(divisor)))
    }

    override fun divide(divisor: Double, roundingMode: RoundingMode): D {
        val arith = getCheckedArithmeticFor(roundingMode)
        return createOrAssign(arith.divide(unscaledValue(), arith.fromDouble(divisor)))
    }

    override fun divideUnscaled(unscaledDivisor: Long): D {
        val arith = defaultArithmetic
        return createOrAssign(arith.divide(unscaledValue(), unscaledDivisor))
    }

    override fun divideUnscaled(unscaledDivisor: Long, roundingMode: RoundingMode): D {
        val arith = getArithmeticFor(roundingMode)
        return createOrAssign(arith.divide(unscaledValue(), unscaledDivisor))
    }

    override fun divideUnscaled(unscaledDivisor: Long, truncationPolicy: TruncationPolicy): D {
        val arith = getArithmeticFor(truncationPolicy)
        return createOrAssign(arith.divide(unscaledValue(), unscaledDivisor))
    }

    override fun divideUnscaled(unscaledDivisor: Long, scale: Int): D {
        val arith = defaultArithmetic
        return createOrAssign(arith.divideByUnscaled(unscaledValue(), unscaledDivisor, scale))
    }

    override fun divideUnscaled(unscaledDivisor: Long, scale: Int, roundingMode: RoundingMode): D {
        val arith = getArithmeticFor(roundingMode)
        return createOrAssign(arith.divideByUnscaled(unscaledValue(), unscaledDivisor, scale))
    }

    override fun divideUnscaled(unscaledDivisor: Long, scale: Int, truncationPolicy: TruncationPolicy): D {
        val arith = getArithmeticFor(truncationPolicy)
        return createOrAssign(arith.divideByUnscaled(unscaledValue(), unscaledDivisor, scale))
    }

    override fun divideExact(divisor: Decimal<S>): D {
        return divide(divisor, CheckedRounding.UNNECESSARY)
    }

    override fun divideTruncate(divisor: Decimal<S>): D {
        return createOrAssign(roundingDownArithmetic.divide(unscaledValue(), divisor.unscaledValue()))
    }

    override fun divideByPowerOfTen(n: Int): D {
        return createOrAssign(defaultArithmetic.divideByPowerOf10(unscaledValue(), n))
    }

    override fun divideByPowerOfTen(n: Int, roundingMode: RoundingMode): D {
        return createOrAssign(getArithmeticFor(roundingMode).divideByPowerOf10(unscaledValue(), n))
    }

    override fun divideByPowerOfTen(n: Int, truncationPolicy: TruncationPolicy): D {
        return createOrAssign(getArithmeticFor(truncationPolicy).divideByPowerOf10(unscaledValue(), n))
    }

    override fun divideToIntegralValue(divisor: Decimal<S>): D {
        val longValue = unscaledValue() / divisor.unscaledValue()
        return createOrAssign(scaleMetrics.multiplyByScaleFactor(longValue))
    }

    override fun divideToIntegralValue(divisor: Decimal<S>, overflowMode: OverflowMode): D {
        if (!overflowMode.isChecked) {
            return divideToIntegralValue(divisor)
        }
        val longValue = divideToLongValue(divisor, overflowMode)
        return createOrAssign(scaleMetrics.multiplyByScaleFactorExact(longValue))
    }

    override fun divideToLongValue(divisor: Decimal<S>): Long {
        return unscaledValue() / divisor.unscaledValue()
    }

    override fun divideToLongValue(divisor: Decimal<S>, overflowMode: OverflowMode): Long {
        val arith =
            getArithmeticFor(if (overflowMode == OverflowMode.CHECKED) CheckedRounding.DOWN else UncheckedRounding.DOWN)
        try {
            return arith.divideByLong(unscaledValue(), divisor.unscaledValue())
        } catch (e: ArithmeticException) {
            if (divisor.isZero()) {
                throw ArithmeticException("Division by zero: integral($this / $divisor)")
            }
            throw ArithmeticException("Overflow: integral($this / $divisor)")
        }
    }

    override fun divideAndRemainder(divisor: Decimal<S>): Array<D?> {
        val uDividend = unscaledValue()
        val uDivisor = divisor.unscaledValue()
        val lIntegral = uDividend / uDivisor
        val uIntegral = scaleMetrics.multiplyByScaleFactor(lIntegral)
        val uReminder = uDividend - uDivisor * lIntegral
        val result = createArray(2)
        result[0] = create(uIntegral)
        result[1] = create(uReminder)
        return result
    }

    override fun divideAndRemainder(divisor: Decimal<S>, overflowMode: OverflowMode): Array<D?> {
        if (!overflowMode.isChecked) {
            return divideAndRemainder(divisor)
        }
        try {
            val arith = getArithmeticFor(CheckedRounding.DOWN)
            val uDividend = unscaledValue()
            val uDivisor = divisor.unscaledValue()
            val lIntegral = arith.divideByLong(uDividend, uDivisor)
            val uIntegral = scaleMetrics.multiplyByScaleFactorExact(lIntegral)
            val uReminder = uDividend - uDivisor * lIntegral
            val result = createArray(2)
            result[0] = create(uIntegral)
            result[1] = create(uReminder)
            return result
        } catch (e: ArithmeticException) {
            if (divisor.isZero()) {
                throw ArithmeticException("Division by zero: integral($this / $divisor)")
            }
            throw ArithmeticException("Overflow: integral($this / $divisor)")
        }
    }

    override fun remainder(divisor: Decimal<S>): D {
        return createOrAssign(unscaledValue() % divisor.unscaledValue())
    }

    /* ------------------------- other arithmetic ------------------------- */
    override fun signum(): Int {
        return unscaledValue().sign
    }

    override fun negate(): D {
        return createOrAssign(defaultArithmetic.negate(unscaledValue()))
    }

    override fun negate(overflowMode: OverflowMode): D {
        return createOrAssign(getArithmeticFor(overflowMode).negate(unscaledValue()))
    }

    override fun abs(): D {
        val unscaled = unscaledValue()
        return if (unscaled >= 0) self() else createOrAssign(defaultArithmetic.negate(unscaled))
    }

    override fun abs(overflowMode: OverflowMode): D {
        val unscaled = unscaledValue()
        return if (unscaled >= 0) self() else createOrAssign(getArithmeticFor(overflowMode).negate(unscaled))
    }

    override fun invert(): D {
        return createOrAssign(defaultArithmetic.invert(unscaledValue()))
    }

    override fun invert(roundingMode: RoundingMode): D {
        return createOrAssign(getArithmeticFor(roundingMode).invert(unscaledValue()))
    }

    override fun invert(truncationPolicy: TruncationPolicy): D {
        return createOrAssign(getArithmeticFor(truncationPolicy).invert(unscaledValue()))
    }

    override fun square(): D {
        return createOrAssign(defaultArithmetic.square(unscaledValue()))
    }

    override fun square(roundingMode: RoundingMode): D {
        return createOrAssign(getArithmeticFor(roundingMode).square(unscaledValue()))
    }

    override fun square(truncationPolicy: TruncationPolicy): D {
        return createOrAssign(getArithmeticFor(truncationPolicy).square(unscaledValue()))
    }

    override fun sqrt(): D {
        return createOrAssign(defaultArithmetic.sqrt(unscaledValue()))
    }

    override fun sqrt(roundingMode: RoundingMode): D {
        return createOrAssign(getArithmeticFor(roundingMode).sqrt(unscaledValue()))
    }

    override fun shiftLeft(n: Int): D {
        // NOTE: FLOOR is default for shift!
        return createOrAssign(roundingFloorArithmetic.shiftLeft(unscaledValue(), n))
    }

    override fun shiftLeft(n: Int, roundingMode: RoundingMode): D {
        return createOrAssign(getArithmeticFor(roundingMode).shiftLeft(unscaledValue(), n))
    }

    override fun shiftLeft(n: Int, truncationPolicy: TruncationPolicy): D {
        return createOrAssign(getArithmeticFor(truncationPolicy).shiftLeft(unscaledValue(), n))
    }

    override fun shiftRight(n: Int): D {
        // NOTE: FLOOR is default for shift!
        return createOrAssign(roundingFloorArithmetic.shiftRight(unscaledValue(), n))
    }

    override fun shiftRight(n: Int, roundingMode: RoundingMode): D {
        return createOrAssign(getArithmeticFor(roundingMode).shiftRight(unscaledValue(), n))
    }

    override fun shiftRight(n: Int, truncationPolicy: TruncationPolicy): D {
        return createOrAssign(getArithmeticFor(truncationPolicy).shiftRight(unscaledValue(), n))
    }

    override fun pow(n: Int): D {
        return createOrAssign(defaultArithmetic.pow(unscaledValue(), n))
    }

    override fun pow(n: Int, roundingMode: RoundingMode): D {
        return createOrAssign(getArithmeticFor(roundingMode).pow(unscaledValue(), n))
    }

    override fun pow(n: Int, truncationPolicy: TruncationPolicy): D {
        return createOrAssign(getArithmeticFor(truncationPolicy).pow(unscaledValue(), n))
    }

    /* --------------------------- compare etc. ---------------------------- */
    override fun compareTo(other: Decimal<S>): Int {
        return defaultArithmetic.compare(unscaledValue(), other.unscaledValue())
    }

    override fun isEqualTo(other: Decimal<S>): Boolean {
        return compareTo(other) == 0
    }

    override fun isGreaterThan(other: Decimal<S>): Boolean {
        return compareTo(other) > 0
    }

    override fun isGreaterThanOrEqualTo(other: Decimal<S>): Boolean {
        return compareTo(other) >= 0
    }

    override fun isLessThan(other: Decimal<S>): Boolean {
        return compareTo(other) < 0
    }

    override fun isLessThanOrEqualTo(other: Decimal<S>): Boolean {
        return compareTo(other) <= 0
    }

    override fun isZero(): Boolean {
        return unscaledValue() == 0L
    }

    override fun isOne(): Boolean {
        return unscaledValue() == scaleMetrics.getScaleFactor()
    }

    override fun isUlp(): Boolean {
        return unscaledValue() == 1L
    }

    override fun isMinusOne(): Boolean {
        return unscaledValue() == -scaleMetrics.getScaleFactor()
    }

    override fun isPositive(): Boolean {
        return unscaledValue() > 0
    }

    override fun isNonNegative(): Boolean {
        return unscaledValue() >= 0
    }

    override fun isNegative(): Boolean {
        return unscaledValue() < 0
    }

    override fun isNonPositive(): Boolean {
        return unscaledValue() <= 0
    }

    override fun isIntegral(): Boolean {
        return scaleMetrics.moduloByScaleFactor(unscaledValue()) == 0L
    }

    override fun isIntegralPartZero(): Boolean {
        val unscaled = unscaledValue()
        val one = scaleMetrics.getScaleFactor()
        return (one > unscaled) and (unscaled > -one)
    }

    override fun isBetweenZeroAndOne(): Boolean {
        val unscaled = unscaledValue()
        return 0 <= unscaled && unscaled < scaleMetrics.getScaleFactor()
    }

    override fun isBetweenZeroAndMinusOne(): Boolean {
        val unscaled = unscaledValue()
        return 0 >= unscaled && unscaled > -(scaleMetrics.getScaleFactor())
    }

    override fun compareToNumerically(other: Decimal<*>): Int {
        return defaultArithmetic.compareToUnscaled(unscaledValue(), other.unscaledValue(), other.scale)
    }

    override fun isEqualToNumerically(other: Decimal<*>): Boolean {
        return compareToNumerically(other) == 0
    }

    override fun min(`val`: Decimal<S>): Decimal<S> {
        return if (isLessThanOrEqualTo(`val`)) this else `val`
    }

    override fun max(`val`: Decimal<S>): Decimal<S> {
        return if (isGreaterThanOrEqualTo(`val`)) this else `val`
    }

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
    fun min(`val`: D): D {
        return if (isLessThanOrEqualTo(`val`)) self() else `val`
    }

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
    fun max(`val`: D): D {
        return if (isGreaterThanOrEqualTo(`val`)) self() else `val`
    }

    override fun avg(`val`: Decimal<S>): D {
        return createOrAssign(defaultArithmetic.avg(unscaledValue(), `val`.unscaledValue()))
    }

    override fun avg(`val`: Decimal<S>, roundingMode: RoundingMode): D {
        return createOrAssign(getArithmeticFor(roundingMode).avg(unscaledValue(), `val`.unscaledValue()))
    }

    /* ---------------------------- equals etc. ---------------------------- */
    override fun hashCode(): Int {
        val unscaled = unscaledValue()
        var hash = scale.toLong()
        hash = 31 * hash + (unscaled ushr 32)
        hash = 31 * hash + unscaled
        return hash.toInt()
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is Decimal<*>) {
            val other = obj
            return unscaledValue() == other.unscaledValue() && scale == other.scale
        }
        return false
    }

    override fun toString(): String {
        return defaultArithmetic.toString(unscaledValue())
    }
}
