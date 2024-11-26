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
package org.decimal4j.arithmetic

import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.arithmetic.Div.div128by64
import org.decimal4j.arithmetic.Rounding.calculateRoundingIncrementForDivision
import org.decimal4j.arithmetic.Rounding.truncatedPartFor2powN
import org.decimal4j.arithmetic.Unsigned.isLess
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.DecimalRounding
import org.decimal4j.truncate.TruncatedPart
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.withSign

/**
 * Contains methods to convert from and to double.
 */
internal object DoubleConversion {
    private const val LONG_MASK = 0xffffffffL

    // The mask for the significand, according to the {@link Double#doubleToRawLongBits(double)} spec.
    private const val SIGNIFICAND_MASK = 0x000fffffffffffffL

    // The mask for the exponent, according to the {@link Double#doubleToRawLongBits(double)} spec.
    @Suppress("unused")
    private const val EXPONENT_MASK = 0x7ff0000000000000L

    // The mask for the sign, according to the {@link Double#doubleToRawLongBits(double)} spec.
    private const val SIGN_MASK = 0x8000000000000000UL

    private const val SIGNIFICAND_BITS = 52

    private const val EXPONENT_BIAS = 1023

    /**
     * The implicit 1 bit that is omitted in significands of normal doubles.
     */
    private const val IMPLICIT_BIT = SIGNIFICAND_MASK + 1

    private const val MIN_LONG_AS_DOUBLE = -9.223372036854776E18

    /*
	 * We cannot store Long.MAX_VALUE as a double without losing precision. Instead, we store Long.MAX_VALUE + 1 ==
	 * -Long.MIN_VALUE, and then offset all comparisons by 1.
	 */
    private const val MAX_LONG_AS_DOUBLE_PLUS_ONE = 9.223372036854776E18

    /**
     * Converts the specified double value to a long truncating the fractional part if any is present. If the value is
     * NaN, infinite or outside of the valid long range, an exception is thrown.
     *
     * @param value
     * the value to convert
     * @return `round<sub>DOWN</sub>(value)`
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is too large for the double to be represented
     * as a `long`
     */
    fun doubleToLong(value: Double): Long {
        require(!value.isNaN()) { "Cannot convert double to long: $value" }
        if (isInLongRange(value)) {
            return value.toLong()
        }
        throw IllegalArgumentException("Overflow for conversion from double to long: $value")
    }

    /**
     * Converts the specified double value to a long rounding the fractional part if necessary using the given
     * `rounding` mode. If the value is NaN, infinite or outside of the valid long range, an exception is thrown.
     *
     * @param rounding
     * the rounding to apply if necessary
     * @param value
     * the value to convert
     * @return `round(value)`
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is too large for the double to be represented
     * as a `long`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun doubleToLong(rounding: DecimalRounding, value: Double): Long {
        require(!value.isNaN()) { "Cannot convert double to long: $value" }
        if (isInLongRange(value)) {
            return roundIntermediate(value, rounding).toLong()
        }
        throw IllegalArgumentException("Overflow for conversion from double to long: $value")
    }

    /*
	 * Copied from guava. This method returns a value y such that rounding y DOWN (towards zero) gives the same result
	 * as rounding x according to the specified mode. PRECONDITION: isFinite(x)
	 */
    private fun roundIntermediate(x: Double, mode: DecimalRounding): Double {
        when (mode) {
            DecimalRounding.UNNECESSARY -> {
                if (!isMathematicalInteger(x)) {
                    throw ArithmeticException("Rounding necessary to convert to an integer value: $x")
                }
                return x
            }

            DecimalRounding.FLOOR -> return if (x >= 0.0 || isMathematicalInteger(x)) {
                x
            } else {
                (x.toLong() - 1L).toDouble()
            }

            DecimalRounding.CEILING -> return if (x <= 0.0 || isMathematicalInteger(x)) {
                x
            } else {
                (x.toLong() + 1L).toDouble()
            }

            DecimalRounding.DOWN -> return x
            DecimalRounding.UP -> return if (isMathematicalInteger(x)) {
                x
            } else {
                (x.toLong() + (if (x > 0) 1L else -1L)).toDouble()
            }

            DecimalRounding.HALF_EVEN -> return round(x)
            DecimalRounding.HALF_UP -> {
                val z = round(x)
                return if (abs(x - z) == 0.5) {
                    x + 0.5.withSign(x)
                } else {
                    z
                }
            }

            DecimalRounding.HALF_DOWN -> {
                val z = round(x)
                return if (abs(x - z) == 0.5) {
                    x
                } else {
                    z
                }
            }

            else -> throw IllegalArgumentException("Unsupported rounding mode: $mode")
        }
    }

    /**
     * Converts the specified double value to an unscaled decimal truncating extra fractional digits if necessary. If
     * the value is NaN, infinite or outside of the valid Decimal range, an exception is thrown.
     *
     * @param arith
     * the arithmetic associated with the result value
     * @param value
     * the value to convert
     * @return `round(value)`
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is too large for the double to be represented
     * as a Decimal of the arithmetic's scale
     */
    fun doubleToUnscaled(arith: DecimalArithmetic, value: Double): Long {
        return doubleToUnscaled(arith, DecimalRounding.DOWN, value)
    }

    /**
     * Converts the specified double value to an unscaled decimal. The specified `rounding` mode is used if
     * rounding is necessary. If the value is NaN, infinite or outside of the valid Decimal range, an exception is
     * thrown.
     *
     * @param arith
     * the arithmetic associated with the result value
     * @param rounding
     * the rounding to apply if necessary
     * @param value
     * the value to convert
     * @return `round(value)`
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is too large for the double to be represented
     * as a Decimal of the arithmetic's scale
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun doubleToUnscaled(arith: DecimalArithmetic, rounding: DecimalRounding, value: Double): Long {
        if (value == 0.0) {
            return 0
        }
        val exp = value.getExponent()
        if (exp >= Long.SIZE_BITS) {
            throw newOverflowException(arith, value)
        }

        // multiply significand by scale factor into a 128bit integer
        val scaleMetrics = arith.scaleMetrics
        val significand = getSignificand(value)

        // HD + Knuth's Algorithm M from [Knu2] section 4.3.1.
        val lFactor = (significand and LONG_MASK).toInt()
        val hFactor = (significand ushr 32).toInt()
        val w1: Long
        val w2: Long
        val w3: Long
        var k: Long

        var t = scaleMetrics.mulloByScaleFactor(lFactor)
        w3 = t and LONG_MASK
        k = t ushr 32

        t = scaleMetrics.mulloByScaleFactor(hFactor) + k
        w2 = t and LONG_MASK
        w1 = t ushr 32

        t = scaleMetrics.mulhiByScaleFactor(lFactor) + w2
        k = t ushr 32

        val hScaled = scaleMetrics.mulhiByScaleFactor(hFactor) + w1 + k
        val lScaled = (t shl 32) + w3

        // now multiply or divide by powers of two as instructed by the double
        // exponent
        val shift = exp - SIGNIFICAND_BITS
        return doubleToUnscaledShift(arith, rounding, value, hScaled, lScaled, shift)
    }

    private fun doubleToUnscaledShift(
        arith: DecimalArithmetic,
        rounding: DecimalRounding,
        value: Double,
        hScaled: Long,
        lScaled: Long,
        shift: Int
    ): Long {
        if (shift > 0) {
            // multiply: shift left
            if (hScaled != 0L) {
                throw newOverflowException(arith, value)
            }
            val zeros = lScaled.numberOfLeadingZeros()
            if (shift >= zeros) {
                throw newOverflowException(arith, value)
            }
            val absResult = lScaled shl shift
            return if (value >= 0) absResult else -absResult
        } else if (shift == 0) {
            if ((hScaled != 0L) or (lScaled < 0)) {
                throw newOverflowException(arith, value)
            }
            return if (value >= 0) lScaled else -lScaled
        } else { // shift < 0
            // divide: shift right
            if (rounding === DecimalRounding.DOWN) {
                return doubleToUnscaledShiftRight(arith, value, hScaled, lScaled, -shift)
            }
            return doubleToUnscaledShiftRight(arith, rounding, value, hScaled, lScaled, -shift)
        }
    }

    private fun doubleToUnscaledShiftRight(
        arith: DecimalArithmetic,
        value: Double,
        hScaled: Long,
        lScaled: Long,
        shift: Int
    ): Long {
        val absResult: Long
        if (shift < Long.SIZE_BITS) {
            if ((hScaled ushr shift) != 0L) {
                throw newOverflowException(arith, value)
            }
            absResult = (hScaled shl (Long.SIZE_BITS - shift)) or (lScaled ushr shift)
        } else if (shift < 2 * Long.SIZE_BITS) {
            absResult = (hScaled ushr (shift - Long.SIZE_BITS))
        } else {
            return 0 // rounded down
        }
        if (absResult < 0) {
            throw newOverflowException(arith, value)
        }
        return if (value >= 0) absResult else -absResult
    }

    private fun doubleToUnscaledShiftRight(
        arith: DecimalArithmetic,
        rounding: DecimalRounding,
        value: Double,
        hScaled: Long,
        lScaled: Long,
        shift: Int
    ): Long {
        val absResult: Long
        val truncatedPart: TruncatedPart
        if (shift < Long.SIZE_BITS) {
            if ((hScaled ushr shift) != 0L) {
                throw newOverflowException(arith, value)
            }
            absResult = (hScaled shl (Long.SIZE_BITS - shift)) or (lScaled ushr shift)
            val rem = modPow2(lScaled, shift)
            truncatedPart = truncatedPartFor2powN(rem, shift)
        } else if (shift < 2 * Long.SIZE_BITS) {
            absResult = (hScaled ushr (shift - Long.SIZE_BITS))
            val rem = modPow2(hScaled, shift - Long.SIZE_BITS)
            truncatedPart = truncatedPartFor2powN(rem, lScaled, shift)
        } else {
            absResult = 0 // rounded down
            truncatedPart = truncatedPartFor2powN(hScaled, lScaled, shift)
        }
        val inc = if (absResult < 0)
            0
        else
            rounding.calculateRoundingIncrement(if (value >= 0) 1 else -1, absResult, truncatedPart)
        if ((absResult < 0) or ((value >= 0) and (absResult == Long.MAX_VALUE) and (inc == 1))) {
            throw newOverflowException(arith, value)
        }
        return (if (value >= 0) absResult else -absResult) + inc
    }

    /**
     * Converts the specified long value to a double truncating extra mantissa digits if necessary.
     *
     * @param arith
     * the arithmetic associated with the value
     * @param value
     * the long value
     * @return `round<sub>DOWN</sub>(value)`
     */
    fun longToDouble(arith: DecimalArithmetic, value: Long): Double {
        return unscaledToDouble(arith, DecimalRounding.DOWN, value)
    }

    /**
     * Converts the specified long value to a double rounding extra mantissa digits if necessary.
     *
     * @param arith
     * the arithmetic associated with the value
     * @param rounding
     * the rounding to apply if necessary
     * @param value
     * the long value
     * @return `round(value)`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun longToDouble(arith: DecimalArithmetic, rounding: DecimalRounding, value: Long): Double {
        if (rounding === DecimalRounding.HALF_EVEN) {
            return value.toDouble()
        }
        return unscaledToDouble(arith, rounding, value)
    }

    /**
     * Converts the specified unscaled decimal value to a double truncating extra precision digits if necessary.
     *
     * @param arith
     * the arithmetic associated with the value
     * @param unscaled
     * the unscaled decimal value
     * @return `round<sub>DOWN</sub>(value)`
     */
    fun unscaledToDouble(arith: DecimalArithmetic, unscaled: Long): Double {
        return unscaledToDouble(arith, DecimalRounding.DOWN, unscaled)
    }

    /**
     * Converts the specified unscaled decimal value to a double rounding extra precision digits if necessary.
     *
     * @param arith
     * the arithmetic associated with the value
     * @param rounding
     * the rounding to apply if necessary
     * @param unscaled
     * the unscaled decimal value
     * @return `round(value)`
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    fun unscaledToDouble(arith: DecimalArithmetic, rounding: DecimalRounding, unscaled: Long): Double {
        if (unscaled == 0L) {
            return 0.0
        }
        val scaleMetrics = arith.scaleMetrics

        val absUnscaled = abs(unscaled)
        if ((absUnscaled < (1L shl SIGNIFICAND_BITS)) and (rounding === DecimalRounding.HALF_EVEN)) {
            // Don't have too guard against Math.abs(MIN_VALUE)
            // because MIN_VALUE also works without loss of precision
            return unscaled.toDouble() / scaleMetrics.getScaleFactor()
        }


        // eliminate sign and trailing power-of-2 zero bits
        val pow2 = absUnscaled.countTrailingZeroBits()
        val absVal = absUnscaled ushr pow2
        val nlzAbsVal = absVal.numberOfLeadingZeros()

        /*
		 * NOTE: a) If absVal has no more than 53 bits it can be represented as a double value without loss of precision
		 * (52 mantissa bits plus the implicit leading 1 bit) b) The scale factor has never more than 53 bits if shifted
		 * right by the trailing power-of-2 zero bits ==> For HALF_EVEN rounding mode we can therefore apply the scale
		 * factor via double division without losing information
		 */
        if ((Long.SIZE_BITS - nlzAbsVal <= SIGNIFICAND_BITS + 1) and (rounding === DecimalRounding.HALF_EVEN)) {
            return unscaledToDoubleWithDoubleDivisionRoundHalfEven(scaleMetrics, unscaled, pow2, absVal)
        }

        /*
		 * 1) we align absVal and factor such that: 2*factor > absVal >= factor 
		 *    then the division absVal/factor == 1.xxxxx, i.e. it is normalized 
		 * 2) because we omit the 1 in the mantissa, we calculate 
		 *    valModFactor = absVal - floor(absVal/factor)*factor = absVal - 1*factor 
		 * 3) we shift valModFactor such that the 1 from the division would be on bit 53 
		 * 4) we perform the division
		 */

        // (1) + (2)
        val exp: Int
        val mantissaShift: Int
        val valModFactor: Long
        val alignShift = nlzAbsVal - scaleMetrics.getScaleFactorNumberOfLeadingZeros()
        if (alignShift >= 0) {
            val scaledAbsVal = absVal shl alignShift
            val diff = scaledAbsVal - scaleMetrics.getScaleFactor()
            exp = -alignShift + (diff shr 63).toInt()
            // if scaledAbsVal < factor we shift left by 1, i.e. we add the absVal
            valModFactor = diff + ((diff shr 63) and scaledAbsVal)
            mantissaShift = SIGNIFICAND_BITS
        } else {
            val scaledFactor = scaleMetrics.getScaleFactor() shl -alignShift
            if (isLess(absVal, scaledFactor)) {
                exp = -alignShift - 1
                // if absVal < scaledFactor we shift left by 1 (right shift of scaledFactor to avoid overflow)
                valModFactor = absVal - (scaledFactor ushr 1)
                mantissaShift = SIGNIFICAND_BITS + alignShift + 1
            } else {
                exp = -alignShift
                valModFactor = absVal - scaledFactor
                mantissaShift = SIGNIFICAND_BITS + alignShift
            }
        }
        if (rounding === DecimalRounding.DOWN) {
            return unscaledToDoubleShiftAndDivideByScaleFactor(
                scaleMetrics, unscaled, exp + pow2, mantissaShift,
                valModFactor
            )
        }
        // (3) + (4)
        return unscaledToDoubleShiftAndDivideByScaleFactor(
            scaleMetrics, rounding, unscaled, exp + pow2, mantissaShift,
            valModFactor
        )
    }

    private fun unscaledToDoubleWithDoubleDivisionRoundHalfEven(
        scaleMetrics: ScaleMetrics,
        unscaled: Long,
        pow2: Int,
        absVal: Long
    ): Double {
        val scale = scaleMetrics.getScale()
        val dividend = absVal.toDouble()
        val divisor = (scaleMetrics.getScaleFactor() shr scale).toDouble()
        val quotient = dividend / divisor
        val exponent = quotient.getExponent() + pow2 - scale
        val significand = quotient.toRawBits() and SIGNIFICAND_MASK
        val raw = ((unscaled.toULong() and SIGN_MASK).toLong() or (((exponent + EXPONENT_BIAS).toLong()) shl SIGNIFICAND_BITS)
                or significand)
        return Double.fromBits(raw)
    }

    private fun unscaledToDoubleShiftAndDivideByScaleFactor(
        scaleMetrics: ScaleMetrics,
        unscaled: Long,
        exp: Int,
        mantissaShift: Int,
        valModFactor: Long
    ): Double {
        val quot: Long
        if (mantissaShift >= 0) {
            val hValModFactor =
                (valModFactor ushr (Long.SIZE_BITS - mantissaShift)) and (-mantissaShift shr 63).toLong()
            val lValModFactor = valModFactor shl mantissaShift
            quot = if (hValModFactor == 0L) {
                scaleMetrics.divideUnsignedByScaleFactor(lValModFactor)
            } else {
                abs(
                    div128by64(
                        DecimalRounding.DOWN, unscaled < 0, hValModFactor, lValModFactor,
                        scaleMetrics.getScaleFactor()
                    )
                )
            }
        } else {
            quot = scaleMetrics.divideByScaleFactor(valModFactor ushr -mantissaShift)
        }
        val raw = ((unscaled.toULong() and SIGN_MASK).toLong() or (((exp + EXPONENT_BIAS).toLong()) shl SIGNIFICAND_BITS)
                or (quot and SIGNIFICAND_MASK))
        return Double.fromBits(raw)
    }

    private fun unscaledToDoubleShiftAndDivideByScaleFactor(
        scaleMetrics: ScaleMetrics,
        rounding: DecimalRounding,
        unscaled: Long,
        exp: Int,
        mantissaShift: Int,
        valModFactor: Long
    ): Double {
        val quotient: Long
        val scaleFactor = scaleMetrics.getScaleFactor()
        if (mantissaShift >= 0) {
            val hValModFactor =
                (valModFactor ushr (Long.SIZE_BITS - mantissaShift)) and (-mantissaShift shr 63).toLong()
            val lValModFactor = valModFactor shl mantissaShift
            if (hValModFactor == 0L) {
                val truncated = scaleMetrics.divideUnsignedByScaleFactor(lValModFactor)
                val remainder = applySign(unscaled, lValModFactor - scaleMetrics.multiplyByScaleFactor(truncated))
                quotient = (truncated + abs(
                    calculateRoundingIncrementForDivision(
                        rounding,
                        truncated,
                        remainder,
                        scaleFactor
                    )
                ))
            } else {
                quotient = abs(
                    div128by64(
                        rounding,
                        unscaled < 0,
                        hValModFactor,
                        lValModFactor,
                        scaleFactor
                    )
                )
                // rounding already done by div128by64
            }
        } else {
            val scaledVal = valModFactor ushr -mantissaShift
            val truncated = scaleMetrics.divideByScaleFactor(scaledVal)
            val remainder = applySign(
                unscaled, ((scaledVal - scaleMetrics.multiplyByScaleFactor(truncated)) shl -mantissaShift)
                        or (valModFactor and (-1L ushr (Long.SIZE_BITS + mantissaShift)))
            )
            // this cannot overflow as min(mantissaShift)=-10 for scale=1, -9 for scale=10, ..., -1 for scale=10^9
            val shiftedScaleFactor = scaleFactor shl -mantissaShift
            quotient = (truncated + abs(
                calculateRoundingIncrementForDivision(
                    rounding, truncated, remainder,
                    shiftedScaleFactor
                )
            ))
        }
        val raw = if (quotient <= SIGNIFICAND_MASK) {
            ((unscaled.toULong() and SIGN_MASK).toLong() or (((exp + EXPONENT_BIAS).toLong()) shl SIGNIFICAND_BITS)
                    or (quotient and SIGNIFICAND_MASK))
        } else {
            // rounding made our value to be 1 instead of smaller than one. 1 +
            // 1 == 2 i.e. our mantissa is zero due to the implicit 1 and our
            // exponent increments by 1
            (unscaled.toULong() and SIGN_MASK).toLong() or ((((exp + 1) + EXPONENT_BIAS).toLong()) shl SIGNIFICAND_BITS)
        }
        return Double.fromBits(raw)
    }

    // @return value % (2^n)
    private fun modPow2(value: Long, n: Int): Long {
        // return value & ((1L << n) - 1);
        return value and (-1L ushr (Long.SIZE_BITS - n)) and (-n shr 31).toLong() // last bracket is for case n=0
    }

    private fun applySign(signed: Long, value: Long): Long {
        return if (signed >= 0) value else -value
    }

    private fun isInLongRange(value: Double): Boolean {
        return (MIN_LONG_AS_DOUBLE - value < 1.0) and (value < MAX_LONG_AS_DOUBLE_PLUS_ONE)
    }

    private fun isMathematicalInteger(x: Double): Boolean {
        return isFinite(x) && (x == 0.0
                || SIGNIFICAND_BITS - (getSignificand(x).countTrailingZeroBits()) <= x.getExponent())
    }

    private fun isFinite(d: Double): Boolean {
        return abs(d) <= Double.MAX_VALUE
    }

    // PRECONDITION: isFinite(d)
    private fun getSignificand(d: Double): Long {
        val exponent = d.getExponent()
        var bits = d.toRawBits()
        bits = bits and SIGNIFICAND_MASK
        return if (exponent == MIN_EXPONENT - 1) bits shl 1 else bits or IMPLICIT_BIT
    }

    private fun newOverflowException(arith: DecimalArithmetic, value: Double): IllegalArgumentException {
        return IllegalArgumentException(
            "Overflow for conversion from double to decimal with scale " + arith.scale + ": " + value
        )
    }
}

const val MIN_EXPONENT = -1022

fun Double.getExponent(): Int {
    if (this == 0.0 || this.isNaN() || this.isInfinite()) {
        return Int.MIN_VALUE // Mimic the behavior of Math.getExponent
    }
    val bits = this.toRawBits() // Get the raw bit representation
    val exponentBits = ((bits ushr 52) and 0x7FF) // Extract exponent bits (52–62)
    return (exponentBits - 1023).toInt() // Adjust for the bias (1023 for Double)
}