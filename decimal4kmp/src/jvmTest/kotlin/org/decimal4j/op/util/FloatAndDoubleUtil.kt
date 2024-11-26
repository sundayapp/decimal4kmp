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
package org.decimal4j.op.util

import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import java.math.BigDecimal
import java.math.BigInteger
import org.decimal4j.truncate.RoundingMode
import org.decimal4j.arithmetic.toJavaRoundingMode
import kotlin.math.ln
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Utility class with static helper methods for floats and doubles used in tests.
 */
object FloatAndDoubleUtil {
    // The mask for the significand, according to the {@link
    // Double#doubleToRawLongBits(double)} spec.
    private const val SIGNIFICAND_MASK_DOUBLE = 0x000fffffffffffffL

    // The mask for the significand, according to the {@link
    // Float#floatToRawIntBits(float)} spec.
    private const val SIGNIFICAND_MASK_FLOAT = 0x007fffff

    private const val SIGNIFICAND_BITS_DOUBLE = 52

    private const val SIGNIFICAND_BITS_FLOAT = 23

    /**
     * The implicit 1 bit that is omitted in significands of normal doubles.
     */
    private const val IMPLICIT_BIT_DOUBLE = SIGNIFICAND_MASK_DOUBLE + 1

    /**
     * The implicit 1 bit that is omitted in significands of normal floats.
     */
    private const val IMPLICIT_BIT_FLOAT = SIGNIFICAND_MASK_FLOAT + 1

    private val SPECIALS_DOUBLE = doubleArrayOf(
        Double.NaN,
        Double.POSITIVE_INFINITY,
        Double.NEGATIVE_INFINITY,
        Double.MIN_VALUE,
        -Double.MIN_VALUE,
        Double.MAX_VALUE,
        -Double.MAX_VALUE,
        java.lang.Double.MIN_NORMAL,
        -java.lang.Double.MIN_NORMAL
    )
    private val SPECIALS_FLOAT = floatArrayOf(
        Float.NaN,
        Float.POSITIVE_INFINITY,
        Float.NEGATIVE_INFINITY,
        Float.MIN_VALUE,
        -Float.MIN_VALUE,
        Float.MAX_VALUE,
        -Float.MAX_VALUE,
        java.lang.Float.MIN_NORMAL,
        -java.lang.Float.MIN_NORMAL
    )

    @JvmStatic
	fun randomDoubleOperand(rnd: Random): Double {
        return when (rnd.nextInt(3)) {
            0 -> rnd.nextDouble()
            1 -> rnd.nextGaussian()
            else -> java.lang.Double.longBitsToDouble(rnd.nextLong())
        }
    }

    @JvmStatic
	fun randomFloatOperand(rnd: Random): Float {
        return when (rnd.nextInt(3)) {
            0 -> rnd.nextFloat()
            1 -> rnd.nextGaussian().toFloat()
            else -> java.lang.Float.intBitsToFloat(rnd.nextInt())
        }
    }

    fun Random.nextGaussian(): Double {
        var u: Double
        var v: Double
        var s: Double
        do {
            u = nextDouble() * 2 - 1
            v = nextDouble() * 2 - 1
            s = u * u + v * v
        } while (s >= 1 || s == 0.0)
        val multiplier = sqrt(-2.0 * ln(s) / s)
        return u * multiplier
    }

    @JvmStatic
	fun specialDoubleOperands(scaleMetrics: ScaleMetrics): DoubleArray {
        val set = sortedSetOf<Double>()
        for (dbl in SPECIALS_DOUBLE) {
            set.add(dbl)
            set.add(dbl + Math.ulp(dbl))
            set.add(dbl - Math.ulp(dbl))
        }
        for (l in TestSettings.TEST_CASES.getSpecialValuesFor(scaleMetrics)) {
            val dbl = l.toDouble()
            set.add(dbl)
            set.add(dbl + Math.ulp(dbl))
            set.add(dbl - Math.ulp(dbl))
        }
        val vals = DoubleArray(set.size)
        val it: Iterator<Double> = set.iterator()
        for (i in vals.indices) {
            vals[i] = it.next()
        }
        return vals
    }

    @JvmStatic
	fun specialFloatOperands(scaleMetrics: ScaleMetrics): FloatArray {
        val set = sortedSetOf<Float>()
        for (flt in SPECIALS_FLOAT) {
            set.add(flt)
            set.add(flt + Math.ulp(flt))
            set.add(flt - Math.ulp(flt))
        }
        for (l in TestSettings.TEST_CASES.getSpecialValuesFor(scaleMetrics)) {
            val flt = l.toFloat()
            set.add(flt)
            set.add(flt + Math.ulp(flt))
            set.add(flt - Math.ulp(flt))
        }
        val vals = FloatArray(set.size)
        val it: Iterator<Float> = set.iterator()
        for (i in vals.indices) {
            vals[i] = it.next()
        }
        return vals
    }

    //PRECONDITION: isFinite(d)
    fun getSignificand(d: Double): Long {
        val exponent = Math.getExponent(d)
        var bits = java.lang.Double.doubleToRawLongBits(d)
        bits = bits and SIGNIFICAND_MASK_DOUBLE
        return if ((exponent == java.lang.Double.MIN_EXPONENT - 1)) bits shl 1 else bits or IMPLICIT_BIT_DOUBLE
    }

    //PRECONDITION: isFinite(d)
    fun getSignificand(f: Float): Int {
        val exponent = Math.getExponent(f)
        var bits = java.lang.Float.floatToRawIntBits(f)
        bits = bits and SIGNIFICAND_MASK_FLOAT
        return if ((exponent == java.lang.Float.MIN_EXPONENT - 1)) bits shl 1 else bits or IMPLICIT_BIT_FLOAT
    }

    /**
     * Similar to [BigDecimal.valueOf] but exact e.g. for -1pe63
     * which is rounded by the BigDecimal standard conversion. Scale and rounding
     * mode are only used to check that the double fits in a 64 bit decimal.
     *
     * @param d the double to convert
     * @param scale the scale for the result (which is returned with higher scale for debugging in case of error --- tests convert to correct scale later)
     * @param roundingMode  the rounding mode
     * @return a big decimal representing exactly d
     */
	@JvmStatic
	fun doubleToBigDecimal(d: Double, scale: Int, roundingMode: RoundingMode): BigDecimal {
        val exp = Math.getExponent(d)
        require(exp < java.lang.Long.SIZE) { "Overflow for conversion from double to Decimal: $d" }
        if (exp < java.lang.Double.MIN_EXPONENT) {
            return BigDecimal.valueOf(d)
        }
        val significand = getSignificand(d)
        val scaledBigDecimal = BigDecimal.valueOf(if (d < 0) -significand else significand)
        val shift = exp - SIGNIFICAND_BITS_DOUBLE
        val converted = if (shift >= 0) {
            scaledBigDecimal.multiply(BigDecimal(BigInteger.valueOf(2).pow(shift)))
        } else {
            scaledBigDecimal.divide(BigDecimal(BigInteger.valueOf(2).pow(-shift)), -shift, RoundingMode.UNNECESSARY.toJavaRoundingMode())
        }
        //if rounding=UNNECESSARY, use round down first to trigger overflow exception before rounding unnecessary
        val isRoundingUnnecessary = roundingMode == RoundingMode.UNNECESSARY
        val rounded = converted.setScale(scale, if (isRoundingUnnecessary) RoundingMode.DOWN.toJavaRoundingMode() else roundingMode.toJavaRoundingMode())
        //now check that the conversion does not overflow
        require(rounded.unscaledValue().bitLength() <= 63) { "Overflow: $rounded: $converted" }
        return if (isRoundingUnnecessary) converted.setScale(scale, roundingMode.toJavaRoundingMode()) else rounded
    }

    /**
     * Similar to [BigDecimal.valueOf] but exact e.g. for -1pe63
     * which is rounded by the BigDecimal standard conversion. Scale and rounding
     * mode are only used to check that the float fits in a 64 bit decimal.
     *
     * @param f the float to convert
     * @param scale the scale for the result (which is returned with higher scale for debugging in case of error --- tests convert to correct scale later)
     * @param roundingMode  the rounding mode
     * @return a big decimal representing exactly f
     */
    fun floatToBigDecimal(f: Float, scale: Int, roundingMode: RoundingMode): BigDecimal {
        val exp = Math.getExponent(f)
        require(exp < java.lang.Long.SIZE) { "Overflow for conversion from float to long: $f" }
        if (exp < java.lang.Float.MIN_EXPONENT) {
            return BigDecimal.valueOf(f.toDouble())
        }
        val significand = getSignificand(f)
        val scaledBigDecimal = BigDecimal.valueOf((if (f < 0) -significand else significand).toLong())
        val shift = exp - SIGNIFICAND_BITS_FLOAT
        val converted = if (shift >= 0) {
            scaledBigDecimal.multiply(BigDecimal(BigInteger.valueOf(2).pow(shift)))
        } else {
            scaledBigDecimal.divide(BigDecimal(BigInteger.valueOf(2).pow(-shift)), -shift, RoundingMode.UNNECESSARY.toJavaRoundingMode())
        }
        //if rounding=UNNECESSARY, use round down first to trigger overflow exception before rounding unnecessary
        val isRoundingUnnecessary = roundingMode == RoundingMode.UNNECESSARY
        val rounded = converted.setScale(scale, if (isRoundingUnnecessary) RoundingMode.DOWN.toJavaRoundingMode() else roundingMode.toJavaRoundingMode())
        //now check that the conversion does not overflow
        require(rounded.unscaledValue().bitLength() <= 63) { "Overflow: $rounded: $converted" }
        return if (isRoundingUnnecessary) converted.setScale(scale, roundingMode.toJavaRoundingMode()) else rounded
    }

    @JvmStatic
	fun getOppositeRoundingMode(roundingMode: RoundingMode): RoundingMode {
        return when (roundingMode) {
            RoundingMode.UP -> RoundingMode.DOWN
            RoundingMode.DOWN -> RoundingMode.UP
            RoundingMode.CEILING -> RoundingMode.FLOOR
            RoundingMode.FLOOR -> RoundingMode.CEILING
            RoundingMode.HALF_UP -> RoundingMode.HALF_DOWN
            RoundingMode.HALF_DOWN -> RoundingMode.HALF_UP
            RoundingMode.HALF_EVEN -> RoundingMode.HALF_EVEN //HALF_UNEVEN?
            RoundingMode.UNNECESSARY -> RoundingMode.UNNECESSARY
            else -> throw IllegalArgumentException("unsupported rounding mode: $roundingMode")
        }
    }
}
