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
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.DecimalRounding
import org.decimal4j.truncate.TruncatedPart
import kotlin.math.abs
import kotlin.math.withSign

/**
 * Contains methods to convert from and to float.
 */
internal object FloatConversion {
    private const val LONG_MASK = 0xffffffffL

    // The mask for the significand, according to the {@link
    // Float#floatToRawIntBits(float)} spec.
    private const val SIGNIFICAND_MASK = 0x007fffff

    // The mask for the exponent, according to the {@link Float#floatToRawIntBits(float)} spec.
    @Suppress("unused")
    private const val EXPONENT_MASK = 0x7f800000

    // The mask for the sign, according to the {@link Float#floatToRawIntBits(float)} spec.
    private const val SIGN_MASK = -0x80000000

    private const val SIGNIFICAND_BITS = 23

    private const val EXPONENT_BIAS = 127

    /**
     * The implicit 1 bit that is omitted in significands of normal floats.
     */
    private const val IMPLICIT_BIT = SIGNIFICAND_MASK + 1

    private const val MIN_LONG_AS_FLOAT = -9.223372E18f

    /*
	 * We cannot store Long.MAX_VALUE as a float without losing precision. Instead, we store Long.MAX_VALUE + 1 ==
	 * -Long.MIN_VALUE, and then offset all comparisons by 1.
	 */
    private const val MAX_LONG_AS_FLOAT_PLUS_ONE = 9.223372E18f

    /**
     * Converts the specified float value to a long truncating the fractional part if any is present. If the value is
     * NaN, infinite or outside of the valid long range, an exception is thrown.
     *
     * @param value
     * the value to convert
     * @return `round<sub>DOWN</sub>(value)`
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is too large for the float to be represented
     * as a `long`
     */
    
    fun floatToLong(value: Float): Long {
        require(!value.isNaN()) { "Cannot convert float to long: $value" }
        if (isInLongRange(value)) {
            return value.toLong()
        }
        throw IllegalArgumentException("Overflow for conversion from float to long: $value")
    }

    /**
     * Converts the specified float value to a long rounding the fractional part if necessary using the given
     * `rounding` mode. If the value is NaN, infinite or outside of the valid long range, an exception is thrown.
     *
     * @param rounding
     * the rounding to apply if necessary
     * @param value
     * the value to convert
     * @return `round(value)`
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is too large for the float to be represented
     * as a `long`
     */
    
    fun floatToLong(rounding: DecimalRounding, value: Float): Long {
        require(!value.isNaN()) { "Cannot convert float to long: $value" }
        if (isInLongRange(value)) {
            return roundIntermediate(value, rounding).toLong()
        }
        throw IllegalArgumentException("Overflow for conversion from float to long: $value")
    }

    /*
	 * Copied from guava. This method returns a value y such that rounding y DOWN (towards zero) gives the same result
	 * as rounding x according to the specified mode. PRECONDITION: isFinite(x)
	 */
    private fun roundIntermediate(x: Float, mode: DecimalRounding): Float {
        when (mode) {
            DecimalRounding.UNNECESSARY -> {
                if (!isMathematicalInteger(x)) {
                    throw ArithmeticException("Rounding necessary to convert to an integer value: $x")
                }
                return x
            }

            DecimalRounding.FLOOR -> return if (x >= 0.0f || isMathematicalInteger(x)) {
                x
            } else {
                (x.toLong() - 1L).toFloat()
            }

            DecimalRounding.CEILING -> return if (x <= 0.0f || isMathematicalInteger(x)) {
                x
            } else {
                (x.toLong() + 1L).toFloat()
            }

            DecimalRounding.DOWN -> return x
            DecimalRounding.UP -> return if (isMathematicalInteger(x)) {
                x
            } else {
                (x.toLong() + (if (x > 0) 1L else -1L)).toFloat()
            }

            DecimalRounding.HALF_EVEN -> return rint(x)
            DecimalRounding.HALF_UP -> {
                val z = rint(x)
                return if (abs((x - z)) == 0.5f) {
                    x + 0.5f.withSign(x)
                } else {
                    z
                }
            }

            DecimalRounding.HALF_DOWN -> {
                val z = rint(x)
                return if (abs((x - z)) == 0.5f) {
                    x
                } else {
                    z
                }
            }

            else -> throw IllegalArgumentException("Unsupported rounding mode: $mode")
        }
    }

    /**
     * Converts the specified float value to an unscaled decimal truncating extra fractional digits if necessary. If the
     * value is NaN, infinite or outside of the valid Decimal range, an exception is thrown.
     *
     * @param arith
     * the arithmetic associated with the result value
     * @param value
     * the value to convert
     * @return `round(value)`
     * @throws IllegalArgumentException
     * if `value` is NaN or infinite or if the magnitude is too large for the float to be represented
     * as a Decimal of the arithmetic's scale
     */
    
    fun floatToUnscaled(arith: DecimalArithmetic, value: Float): Long {
        return floatToUnscaled(arith, DecimalRounding.DOWN, value)
    }

    /**
     * Converts the specified float value to an unscaled decimal. The specified `rounding` mode is used if
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
     * if `value` is NaN or infinite or if the magnitude is too large for the float to be represented
     * as a Decimal of the arithmetic's scale
     * @throws ArithmeticException
     * if `roundingMode==UNNECESSARY` and rounding is necessary
     */
    
    fun floatToUnscaled(arith: DecimalArithmetic, rounding: DecimalRounding, value: Float): Long {
        if (value == 0f) {
            return 0
        }
        val exp = getExponent(value)
        if (exp >= Long.SIZE_BITS) {
            throw newOverflowException(arith, value.toDouble())
        }

        // multiply significand by scale factor into a 128bit integer
        val scaleMetrics = arith.scaleMetrics
        val significand = getSignificand(value).toLong()

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

        // now multiply or divide by powers of two as instructed by the float exponent
        val shift = exp - SIGNIFICAND_BITS
        return floatToUnscaledShift(arith, rounding, value, hScaled, lScaled, shift)
    }

    private fun floatToUnscaledShift(
        arith: DecimalArithmetic,
        rounding: DecimalRounding,
        value: Float,
        hScaled: Long,
        lScaled: Long,
        shift: Int
    ): Long {
        if (shift > 0) {
            // multiply: shift left
            if (hScaled != 0L) {
                throw newOverflowException(arith, value.toDouble())
            }
            val zeros = lScaled.numberOfLeadingZeros()
            if (shift >= zeros) {
                throw newOverflowException(arith, value.toDouble())
            }
            val absResult = lScaled shl shift
            return if (value >= 0) absResult else -absResult
        } else if (shift == 0) {
            if ((hScaled != 0L) or (lScaled < 0)) {
                throw newOverflowException(arith, value.toDouble())
            }
            return if (value >= 0) lScaled else -lScaled
        } else { // shift < 0
            // divide: shift right
            if (rounding === DecimalRounding.DOWN) {
                return floatToUnscaledShiftRight(arith, value, hScaled, lScaled, -shift)
            }
            return floatToUnscaledShiftRight(arith, rounding, value, hScaled, lScaled, -shift)
        }
    }

    private fun floatToUnscaledShiftRight(
        arith: DecimalArithmetic,
        value: Float,
        hScaled: Long,
        lScaled: Long,
        shift: Int
    ): Long {
        val absResult: Long
        if (shift < Long.SIZE_BITS) {
            if ((hScaled ushr shift) != 0L) {
                throw newOverflowException(arith, value.toDouble())
            }
            absResult = (hScaled shl (Long.SIZE_BITS - shift)) or (lScaled ushr shift)
        } else if (shift < 2 * Long.SIZE_BITS) {
            absResult = (hScaled ushr (shift - Long.SIZE_BITS))
        } else {
            return 0 // rounded down
        }
        if (absResult < 0) {
            throw newOverflowException(arith, value.toDouble())
        }
        return if (value >= 0) absResult else -absResult
    }

    private fun floatToUnscaledShiftRight(
        arith: DecimalArithmetic,
        rounding: DecimalRounding,
        value: Float,
        hScaled: Long,
        lScaled: Long,
        shift: Int
    ): Long {
        val absResult: Long
        val truncatedPart: TruncatedPart
        if (shift < Long.SIZE_BITS) {
            if ((hScaled ushr shift) != 0L) {
                throw newOverflowException(arith, value.toDouble())
            }
            absResult = (hScaled shl (Long.SIZE_BITS - shift)) or (lScaled ushr shift)
            val rem = modPow2(lScaled, shift)
            truncatedPart = Rounding.truncatedPartFor2powN(rem, shift)
        } else if (shift < 2 * Long.SIZE_BITS) {
            absResult = (hScaled ushr (shift - Long.SIZE_BITS))
            val rem = modPow2(hScaled, shift - Long.SIZE_BITS)
            truncatedPart = Rounding.truncatedPartFor2powN(rem, lScaled, shift)
        } else {
            absResult = 0 // rounded down
            truncatedPart = Rounding.truncatedPartFor2powN(hScaled, lScaled, shift)
        }
        val inc = if (absResult < 0)
            0
        else
            rounding.calculateRoundingIncrement(if (value >= 0) 1 else -1, absResult, truncatedPart)
        if ((absResult < 0) or ((value >= 0) and (absResult == Long.MAX_VALUE) and (inc == 1))) {
            throw newOverflowException(arith, value.toDouble())
        }
        return (if (value >= 0) absResult else -absResult) + inc
    }

    /**
     * Converts the specified long value to a float truncating extra mantissa digits if necessary.
     *
     * @param arith
     * the arithmetic associated with the value
     * @param value
     * the long value
     * @return `round<sub>DOWN</sub>(value)`
     */
    
    fun longToFloat(arith: DecimalArithmetic, value: Long): Float {
        return unscaledToFloat(arith, DecimalRounding.DOWN, value)
    }

    /**
     * Converts the specified long value to a float rounding extra mantissa digits if necessary.
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
    
    fun longToFloat(arith: DecimalArithmetic, rounding: DecimalRounding, value: Long): Float {
        if (rounding === DecimalRounding.HALF_EVEN) {
            return value.toFloat()
        }
        return unscaledToFloat(arith, rounding, value)
    }

    /**
     * Converts the specified unscaled decimal value to a float truncating extra precision digits if necessary.
     *
     * @param arith
     * the arithmetic associated with the value
     * @param unscaled
     * the unscaled decimal value
     * @return `round<sub>DOWN</sub>(value)`
     */
    
    fun unscaledToFloat(arith: DecimalArithmetic, unscaled: Long): Float {
        return unscaledToFloat(arith, DecimalRounding.DOWN, unscaled)
    }

    /**
     * Converts the specified unscaled decimal value to a float rounding extra precision digits if necessary.
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
    
    fun unscaledToFloat(arith: DecimalArithmetic, rounding: DecimalRounding, unscaled: Long): Float {
        if (unscaled == 0L) {
            return 0f
        }
        if (rounding === DecimalRounding.HALF_EVEN) {
            return DoubleConversion.unscaledToDouble(arith, rounding, unscaled).toFloat()
        }

        val scaleMetrics = arith.scaleMetrics
        val absUnscaled = abs(unscaled)


        // eliminate sign and trailing power-of-2 zero bits
        val pow2 = absUnscaled.countTrailingZeroBits()
        val absVal = absUnscaled ushr pow2
        val nlzAbsVal = absVal.numberOfLeadingZeros()

        /*
		 * 1) we align absVal and factor such that: 2*factor > absVal >= factor then the division 
		 *    absVal/factor == 1.xxxxx, i.e. it is normalized 
		 * 2) because we omit the 1 in the mantissa, we calculate 
		 *    valModFactor = absVal - floor(absVal/factor)*factor = absVal - 1*factor 
		 * 3) we shift valModFactor such that the 1 from the division would be on bit 24 
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
            if (Unsigned.isLess(absVal, scaledFactor)) {
                exp = -alignShift - 1
                // if absVal < scaledFactor we shift by 1 (right shift of scaledFactor to avoid overflow)
                valModFactor = absVal - (scaledFactor ushr 1)
                mantissaShift = SIGNIFICAND_BITS + alignShift + 1
            } else {
                exp = -alignShift
                valModFactor = absVal - scaledFactor
                mantissaShift = SIGNIFICAND_BITS + alignShift
            }
        }
        if (rounding === DecimalRounding.DOWN) {
            return unscaledToFloatShiftAndDivideByScaleFactor(
                scaleMetrics, unscaled, exp + pow2, mantissaShift,
                valModFactor
            )
        }
        // (3) + (4)
        return unscaledToFloatShiftAndDivideByScaleFactor(
            scaleMetrics, rounding, unscaled, exp + pow2, mantissaShift,
            valModFactor
        )
    }

    private fun unscaledToFloatShiftAndDivideByScaleFactor(
        scaleMetrics: ScaleMetrics,
        unscaled: Long,
        exp: Int,
        mantissaShift: Int,
        valModFactor: Long
    ): Float {
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
        val signBit = ((unscaled ushr 32) and SIGN_MASK.toLong()).toInt()
        val raw =
            signBit or ((exp + EXPONENT_BIAS) shl SIGNIFICAND_BITS) or (quot and SIGNIFICAND_MASK.toLong()).toInt()
        return Float.fromBits(raw)
    }

    private fun unscaledToFloatShiftAndDivideByScaleFactor(
        scaleMetrics: ScaleMetrics,
        rounding: DecimalRounding,
        unscaled: Long,
        exp: Int,
        mantissaShift: Int,
        valModFactor: Long
    ): Float {
        val quotient: Long
        val scaleFactor = scaleMetrics.getScaleFactor()
        if (mantissaShift >= 0) {
            val hValModFactor =
                (valModFactor ushr (Long.SIZE_BITS - mantissaShift)) and (-mantissaShift shr 63).toLong()
            val lValModFactor = valModFactor shl mantissaShift
            if (hValModFactor == 0L) {
                val truncated = scaleMetrics.divideUnsignedByScaleFactor(lValModFactor)
                val remainder = applySign(unscaled, lValModFactor - scaleMetrics.multiplyByScaleFactor(truncated))
                quotient = ((truncated
                        + abs(
                    Rounding.calculateRoundingIncrementForDivision(
                        rounding,
                        truncated,
                        remainder,
                        scaleFactor
                    )
                )))
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
            // NOTE: below shift can overflow as min(mantissaShift)=-39 for scale=1, -38 for scale=10, ..., -21 for scale=10^18
            //		 hence we use MAX_VALUE in this case, should always be more than 2x remainder (which is good enough for HALF_UP etc)
            val shiftedScaleFactor =
                if (-mantissaShift >= scaleMetrics.getScaleFactorNumberOfLeadingZeros()) Long.MAX_VALUE else scaleFactor shl -mantissaShift
            quotient = (truncated + abs(
                Rounding.calculateRoundingIncrementForDivision(
                    rounding, truncated, remainder,
                    shiftedScaleFactor
                )
            ))
        }
        val raw: Int
        val signBit = ((unscaled ushr 32) and SIGN_MASK.toLong()).toInt()
        raw = if (quotient <= SIGNIFICAND_MASK) {
            signBit or ((exp + EXPONENT_BIAS) shl SIGNIFICAND_BITS) or (quotient and SIGNIFICAND_MASK.toLong()).toInt()
        } else {
            // rounding made our value to be 1 instead of smaller than one. 1 + 1 == 2 i.e. our mantissa is zero due to
            // the implicit 1 and our exponent increments by 1
            signBit or ((exp + 1 + EXPONENT_BIAS) shl SIGNIFICAND_BITS)
        }
        return Float.fromBits(raw)
    }

    // @return value % (2^n)
    private fun modPow2(value: Long, n: Int): Long {
        // return value & ((1L << n) - 1);
        return value and (-1L ushr (Long.SIZE_BITS - n)) and (-n shr 31).toLong() // last bracket is for case n=0
    }

    private fun applySign(signed: Long, value: Long): Long {
        return if (signed >= 0) value else -value
    }

    private fun isInLongRange(value: Float): Boolean {
        return (MIN_LONG_AS_FLOAT - value < 1.0f) and (value < MAX_LONG_AS_FLOAT_PLUS_ONE)
    }

    private fun isMathematicalInteger(x: Float): Boolean {
        return isFinite(x) && (x == 0.0f
                || SIGNIFICAND_BITS - getSignificand(x).toLong().countTrailingZeroBits() <= getExponent(
            x
        ))
    }

    private fun isFinite(f: Float): Boolean {
        return abs(f) <= Float.MAX_VALUE
    }

    // PRECONDITION: isFinite(d)
    private fun getSignificand(f: Float): Int {
        val exponent = getExponent(f)
        var bits = f.toRawBits()
        bits = bits and SIGNIFICAND_MASK
        return if (exponent == FLOAT_MIN_EXPONENT - 1) bits shl 1 else bits or IMPLICIT_BIT
    }

    /**
     * Returns the `float` value that is closest in value to the argument and is equal to a mathematical integer.
     * If two `float` values that are mathematical integers are equally close to the value of the argument, the
     * result is the integer value that is even. Special cases:
     *
     *  * If the argument value is already equal to a mathematical integer, then the result is the same as the
     * argument.
     *  * If the argument is NaN or an infinity or positive zero or negative zero, then the result is the same as the
     * argument.
     *
     *
     * @param a
     * a value.
     * @return the closest floating-point value to `a` that is equal to a mathematical integer.
     * @author Joseph D. Darcy
     */
    private fun rint(a: Float): Float {
        /*
		 * If the absolute value of a is not less than 2^23, it is either a finite integer (the float format does not
		 * have enough significand bits for a number that large to have any fractional portion), an infinity, or a NaN.
		 * In any of these cases, rint of the argument is the argument.
		 *
		 * Otherwise, the sum (twoToThe23 + a ) will properly round away any fractional portion of a since
		 * ulp(twoToThe23) == 1.0; subtracting out twoToThe23 from this sum will then be exact and leave the rounded
		 * integer portion of a.
		 *
		 * This method does *not* need to be declared strictfp to get fully reproducible results. Whether or not a
		 * method is declared strictfp can only make a difference in the returned result if some operation would
		 * overflow or underflow with strictfp semantics. The operation (twoToThe23 + a ) cannot overflow since large
		 * values of a are screened out; the add cannot underflow since twoToThe23 is too large. The subtraction
		 * ((twoToThe23 + a ) - twoToThe23) will be exact as discussed above and thus cannot overflow or meaningfully
		 * underflow. Finally, the last multiply in the return statement is by plus or minus 1.0, which is exact too.
		 */
        var a = a
        val twoToThe23 = (1L shl 23).toFloat() // 2^23
        val sign: Float = 1.0f.withSign(a) // preserve sign info
        a = abs(a)

        if (a < twoToThe23) { // E_min <= ilogb(a) <= 51
            a = ((twoToThe23 + a) - twoToThe23)
        }

        return sign * a // restore original sign
    }

    private fun newOverflowException(arith: DecimalArithmetic, value: Double): IllegalArgumentException {
        return IllegalArgumentException(
            "Overflow for conversion from float to decimal with scale " + arith.scale + ": " + value
        )
    }
}

const val FLOAT_MIN_EXPONENT: Int = -126

fun getExponent(value: Float): Int {
    return if (value.isFinite() && value != 0.0f) {
        ((value.toBits() ushr 23) and 0xFF) - 127
    } else {
        throw IllegalArgumentException("Value must be a finite non-zero number")
    }
}
