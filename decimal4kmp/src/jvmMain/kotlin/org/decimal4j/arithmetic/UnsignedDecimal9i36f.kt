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
import org.decimal4j.arithmetic.Checked.addLong
import org.decimal4j.arithmetic.Rounding.truncatedPartFor
import org.decimal4j.scale.*
import org.decimal4j.truncate.DecimalRounding
import org.decimal4j.truncate.OverflowMode
import org.decimal4j.truncate.TruncatedPart
import org.decimal4j.api.RoundingMode

/**
 * Helper class for an unsigned decimal value with 9 integral digits and 38 decimal
 * fraction digits used internally by [Pow] to calculate decimal powers.
 */
internal class UnsignedDecimal9i36f
/** Constructor  */
private constructor() {
    /**
     * Normalization mode.
     */
    private enum class Norm {
        /** Not normalized: ival and valX can be any positive longs  */
        UNNORMALIZED,

        /** 18 digit normalization (standard): ival is 9 digits; val3/val2 are 18 digits, val1/val0 are zero */
        NORMALIZED_18,

        /** 9 digit normalization (for multiplication): ival and valX are 9 digits values */
        NORMALIZED_09
    }

    private var norm: Norm? = null

    /**
     * Returns the current power-ten exponent.
     *
     * @return the base-10 exponent of this value
     */
    var pow10: Int = 0
        private set
    private var ival: Long = 0
    private var val3: Long = 0
    private var val2: Long = 0
    private var val1: Long = 0
    private var val0: Long = 0

    /**
     * Assigns the value one to this unsigned 9x36 decimal and returns it.
     *
     * @return this
     */
    fun initOne(): UnsignedDecimal9i36f {
        this.norm = Norm.NORMALIZED_18
        this.pow10 = 0
        this.ival = 1
        this.val3 = 0
        this.val2 = 0
        this.val1 = 0
        this.val0 = 0
        return this
    }

    /**
     * Assigns the value one to this unsigned 9x36 decimal and returns it.
     *
     * @param copy
     * the value to copy
     * @return this
     */
    fun init(copy: UnsignedDecimal9i36f): UnsignedDecimal9i36f {
        this.norm = copy.norm
        this.pow10 = copy.pow10
        this.ival = copy.ival
        this.val3 = copy.val3
        this.val2 = copy.val2
        this.val1 = copy.val1
        this.val0 = copy.val0
        return this
    }

    /**
     * Assigns the given integer and fraction component to this unsigned 9x36
     * decimal and returns it.
     *
     * @param ival
     * the integer part of the value to assign
     * @param fval
     * the fractional part of the value to assign
     * @param scaleMetrics
     * the scale metrics associated with the value
     * @return this
     */
    fun init(ival: Long, fval: Long, scaleMetrics: ScaleMetrics): UnsignedDecimal9i36f {
        val diffMetrics = Scales.getScaleMetrics(18 - scaleMetrics.getScale())
        normalizeAndRound(1, 0, ival, diffMetrics.multiplyByScaleFactor(fval), 0, 0, 0, DecimalRounding.UNNECESSARY)
        return this
    }

    private fun normalizeAndRound(
        sgn: Int,
        pow10: Int,
        ival: Long,
        val3: Long,
        val2: Long,
        val1: Long,
        val0: Long,
        rounding: DecimalRounding
    ) {
        var pow10 = pow10
        var ival = ival
        var val3 = val3
        var val2 = val2
        var val1 = val1
        var val0 = val0
        while (ival == 0L) {
            ival = val3
            val3 = val2
            val2 = val1
            val1 = val0
            val0 = 0
            pow10 -= 18
        }
        if (ival >= Scale9f.SCALE_FACTOR) {
            var carry: Long

            val log10 = log10(ival)
            val div10 = log10 - 9
            val divScale = Scales.getScaleMetrics(div10)
            val mulScale = Scales.getScaleMetrics(18 - div10)

            val ivHi = divScale.divideByScaleFactor(ival)
            val ivLo = ival - divScale.multiplyByScaleFactor(ivHi)
            ival = ivHi
            carry = mulScale.multiplyByScaleFactor(ivLo)

            if (val3 != 0L) {
                val v3Hi = divScale.divideByScaleFactor(val3)
                val v3Lo = val3 - divScale.multiplyByScaleFactor(v3Hi)
                val3 = v3Hi + carry
                carry = mulScale.multiplyByScaleFactor(v3Lo)
            } else {
                val3 = carry
                carry = 0
            }

            if (val2 != 0L) {
                val v2Hi = divScale.divideByScaleFactor(val2)
                val v2Lo = val2 - divScale.multiplyByScaleFactor(v2Hi)
                val2 = v2Hi + carry
                carry = mulScale.multiplyByScaleFactor(v2Lo)
            } else {
                val2 = carry
                carry = 0
            }

            if (val1 != 0L) {
                val v1Hi = divScale.divideByScaleFactor(val1)
                val v1Lo = val1 - divScale.multiplyByScaleFactor(v1Hi)
                val1 = v1Hi + carry
                carry = mulScale.multiplyByScaleFactor(v1Lo)
            } else {
                val1 = carry
                carry = 0
            }
            roundToVal2(sgn, pow10 + div10, ival, val3, val2, val1, (val0 != 0L) or (carry != 0L), rounding)
        } else {
            roundToVal2(sgn, pow10, ival, val3, val2, val1, val0 != 0L, rounding)
        }
    }

    private fun roundToVal2(
        sgn: Int,
        pow10: Int,
        ival: Long,
        val3: Long,
        val2: Long,
        val1: Long,
        nonZeroAfterVal1: Boolean,
        rounding: DecimalRounding
    ) {
        //(ival|val3|val2) += round(val1|val0|carry) 
        var pow10 = pow10
        var ival = ival
        var val3 = val3
        var val2 = val2
        val inc = getRoundingIncrement(sgn, val2, Scale18f.INSTANCE, val1, nonZeroAfterVal1, rounding)
        if (inc > 0) {
            val2++
            if (val2 >= Scale18f.SCALE_FACTOR) {
                val2 = 0 //val2 -= Scale18f.SCALE_FACTOR;
                val3++
                if (val3 >= Scale18f.SCALE_FACTOR) {
                    val3 = 0 //val3 -= Scale18f.SCALE_FACTOR;
                    ival++
                    if (ival >= Scale9f.SCALE_FACTOR) {
                        ival = Scale8f.SCALE_FACTOR //ival /= 10
                        pow10++
                    }
                }
            }
        }
        this.norm = Norm.NORMALIZED_18
        this.pow10 = pow10
        this.ival = ival
        this.val3 = val3
        this.val2 = val2
        this.val1 = 0
        this.val0 = 0
    }

    private fun normalize09() {
        val val3 = this.val3
        val val2 = this.val2
        val v3 = val3 / Scale9f.SCALE_FACTOR
        val v2 = val3 - v3 * Scale9f.SCALE_FACTOR
        val v1 = val2 / Scale9f.SCALE_FACTOR
        val v0 = val2 - v1 * Scale9f.SCALE_FACTOR
        this.norm = Norm.NORMALIZED_09
        this.val3 = v3
        this.val2 = v2
        this.val1 = v1
        this.val0 = v0
    }

    /**
     * Multiplies this unsigned 9x36 decimal value with another one.
     *
     * @param sgn
     * the sign of the final result
     * @param factor
     * the factor to be multiplied with
     * @param rounding
     * the rounding to apply
     */
    fun multiply(sgn: Int, factor: UnsignedDecimal9i36f, rounding: DecimalRounding) {
        if (norm != Norm.NORMALIZED_18) {
            normalizeAndRound(sgn, pow10, ival, val3, val2, val1, val0, rounding)
        }
        multiply(sgn, val3, val2, factor, rounding)
    }

    //PRECONDITION: this and factor normalized, i.e. ival < Scale9f.SCALE_FACTOR
    private fun multiply(sgn: Int, val3: Long, val2: Long, factor: UnsignedDecimal9i36f, rounding: DecimalRounding) {
        //split each factor into 9 digit parts
        if (this.norm != Norm.NORMALIZED_09) {
            this.normalize09()
        }
        val lhs4 = this.ival
        val lhs3 = this.val3
        val lhs2 = this.val2
        val lhs1 = this.val1
        val lhs0 = this.val0
        if (factor.norm != Norm.NORMALIZED_09) {
            factor.normalize09()
        }
        val rhs4 = factor.ival
        val rhs3 = factor.val3
        val rhs2 = factor.val2
        val rhs1 = factor.val1
        val rhs0 = factor.val0


        //multiply now
        val scale72 = lhs0 * rhs0
        var scale63 = lhs1 * rhs0 + rhs1 * lhs0
        var scale54 = lhs2 * rhs0 + rhs2 * lhs0 + lhs1 * rhs1
        var scale45 = lhs3 * rhs0 + rhs3 * lhs0 + lhs2 * rhs1 + rhs2 * lhs1
        var scale36 = lhs3 * rhs1 + rhs3 * lhs1 + lhs2 * rhs2 + lhs0 * rhs4 + rhs0 * lhs4
        var scale27 = lhs3 * rhs2 + rhs3 * lhs2 + lhs1 * rhs4 + rhs1 * lhs4
        var scale18 = lhs3 * rhs3 + lhs2 * rhs4 + rhs2 * lhs4
        var scale09 = lhs3 * rhs4 + rhs3 * lhs4
        var scale00 = lhs4 * rhs4

        //NOTE: largest value is val36: sum of 5 products + sum of 4 products 
        //      -- each product consists of 2 factors < Scale9f.SCALE_FACTOR
        //		-- hence each product < Scale18f.SCALE_FACTOR
        //		-- sum of 9 products each < Scale18f.SCALE_FACTOR 
        //		=> sum < 9 * Scale18f.SCALE_FACTOR < Long.MAX_VALUE
        //		=> no overflows


        //reduce 8 to 4 parts and propagate carries
        var carry = scale63 / Scale9f.SCALE_FACTOR
        scale63 -= carry * Scale9f.SCALE_FACTOR
        var val72 = scale63 * Scale9f.SCALE_FACTOR + scale72
        while (val72 >= Scale18f.SCALE_FACTOR) {
            val72 -= Scale18f.SCALE_FACTOR
            carry++
        }
        scale54 += carry

        carry = scale45 / Scale9f.SCALE_FACTOR
        scale45 -= carry * Scale9f.SCALE_FACTOR
        var val54 = scale45 * Scale9f.SCALE_FACTOR + scale54
        while (val54 >= Scale18f.SCALE_FACTOR) {
            val54 -= Scale18f.SCALE_FACTOR
            carry++
        }
        scale36 += carry

        carry = scale27 / Scale9f.SCALE_FACTOR
        scale27 -= carry * Scale9f.SCALE_FACTOR
        var val36 = scale27 * Scale9f.SCALE_FACTOR + scale36
        while (val36 >= Scale18f.SCALE_FACTOR) {
            val36 -= Scale18f.SCALE_FACTOR
            carry++
        }
        scale18 += carry

        carry = scale09 / Scale9f.SCALE_FACTOR
        scale09 -= carry * Scale9f.SCALE_FACTOR
        var val18 = scale09 * Scale9f.SCALE_FACTOR + scale18
        while (val18 >= Scale18f.SCALE_FACTOR) {
            val18 -= Scale18f.SCALE_FACTOR
            carry++
        }
        scale00 += carry

        //assign values
        this.norm = Norm.UNNORMALIZED
        this.pow10 += factor.pow10
        this.ival = scale00
        this.val3 = val18
        this.val2 = val36
        this.val1 = val54
        this.val0 = val72
    }

    private val invNormPow10: Int
        get() {
            val log10 = log10(ival)
            return if (ival >= Scales.getScaleMetrics(log10 - 1)
                    .getScaleFactor() * 3
            ) log10 else log10 - 1 //we want to normalize the ival part to be between 1 and 5
        }

    private fun getInvNorm(sgn: Int, arith: DecimalArithmetic, rounding: DecimalRounding): Long {
        val pow10 = -invNormPow10
        if (pow10 >= 0) {
            return getDecimal(sgn, pow10, ival, val3, val2, val1, val0, 0, 0, 0, 0, arith, rounding)
        }
        return getDecimal(sgn, pow10 + 18, 0, ival, val3, val2, val1, val0, 0, 0, 0, arith, rounding)
    }

    /**
     * Returns the inverted result resulting from exponentiation with a negative
     * exponent. The result is best-effort accurate.
     *
     * @param sgn
     * the sign of the final result
     * @param arith
     * the arithmetic of the base value
     * @param rounding
     * the rounding to apply
     * @param powRounding
     * reciprocal rounding if exponent is negative and rounding
     * otherwise
     * @return `round(1 / this)`
     */
    fun getInverted(sgn: Int, arith: DecimalArithmetic, rounding: DecimalRounding, powRounding: DecimalRounding): Long {
        //1) get scale18 value normalized to 0.3 <= x < 3 (i.e. make it invertible without overflow for uninverted and inverted value)
        val arith18 = Scale18f.INSTANCE.getArithmetic(rounding.getRoundingMode()) //unchecked is fine, see comments below
        val divisor = this.getInvNorm(sgn, arith18, powRounding)
        //2) invert normalized scale18 value 
        val inverted = arith18.invert(divisor) //can't overflow as for x=abs(divisor): 0.9 <= x < 9 
        //3) apply inverted powers of 10, including powers from normalization and rescaling 
        val pow10 = this.pow10 + this.invNormPow10 + (18 - arith.scale)
        return arith.multiplyByPowerOf10(inverted, -pow10) //overflow possible
    }

    /**
     * Returns the unscaled Decimal result resulting from exponentiation with a non-negative
     * exponent. The result is accurate up to 1 ULP of the Decimal.
     *
     * @param sgn
     * the sign of the final result
     * @param arith
     * the arithmetic of the base value
     * @param rounding
     * the rounding to apply
     * @return `round(this)`
     */
    fun getDecimal(sgn: Int, arith: DecimalArithmetic, rounding: DecimalRounding): Long {
        if (pow10 >= 0) {
            if (pow10 <= 18) {
                return getDecimal(sgn, pow10, ival, val3, val2, val1, val0, 0, 0, 0, 0, arith, rounding)
            }
            if (arith.overflowMode.isChecked) {
                return checkedMultiplyByPowerOf10AndRound(sgn, arith, rounding)
            }
            return multiplyByPowerOf10AndRound(sgn, arith, rounding)
        } else {
            return divideByPowerOf10AndRound(sgn, arith, rounding)
        }
    }

    private fun multiplyByPowerOf10AndRound(sgn: Int, arith: DecimalArithmetic, rounding: DecimalRounding): Long {
        var iv = ival * Scale18f.SCALE_FACTOR + val3
        if (pow10 <= 36) {
            return getDecimal(sgn, pow10 - 18, iv, val2, val1, val0, 0, 0, 0, 0, 0, arith, rounding)
        }
        iv *= Scale18f.SCALE_FACTOR + val2
        if (pow10 <= 54) {
            return getDecimal(sgn, pow10 - 36, iv, val1, val0, 0, 0, 0, 0, 0, 0, arith, rounding)
        }
        iv *= Scale18f.SCALE_FACTOR + val1
        if (pow10 <= 72) {
            return getDecimal(sgn, pow10 - 54, iv, val0, 0, 0, 0, 0, 0, 0, 0, arith, rounding)
        }
        iv *= Scale18f.SCALE_FACTOR + val0
        var pow = pow10 - 72
        while ((pow > 18) and (iv != 0L)) {
            iv *= Scale18f.SCALE_FACTOR
            pow -= 18
        }
        if (iv != 0L) {
            val absVal = arith.fromLong(iv)
            return if (sgn >= 0) absVal else -absVal
        }
        return 0 //overflow, everything was shifted out to the left
    }

    private fun checkedMultiplyByPowerOf10AndRound(
        sgn: Int,
        arith: DecimalArithmetic,
        rounding: DecimalRounding
    ): Long {
        val arith18 = Scale18f.INSTANCE.getCheckedArithmetic(RoundingMode.DOWN)
        var iv = arith18.add(arith18.fromLong(ival), val3) //ival * 10^18 + val3
        if (pow10 <= 36) {
            return getDecimal(sgn, pow10 - 18, iv, val2, val1, val0, 0, 0, 0, 0, 0, arith, rounding)
        }
        iv = arith18.add(arith18.fromLong(iv), val2) //iv * 10^18 + val2
        if (pow10 <= 54) {
            return getDecimal(sgn, pow10 - 36, iv, val1, val0, 0, 0, 0, 0, 0, 0, arith, rounding)
        }
        iv = arith18.add(arith18.fromLong(iv), val1) //iv * 10^18 + val1
        if (pow10 <= 72) {
            return getDecimal(sgn, pow10 - 54, iv, val0, 0, 0, 0, 0, 0, 0, 0, arith, rounding)
        }
        iv = arith18.add(arith18.fromLong(iv), val0) //iv * 10^18 + val0
        var pow = pow10 - 72
        while ((pow > 18) and (iv != 0L)) {
            iv = arith18.fromLong(iv) //iv * 10^18
            pow -= 18
        }
        if (iv != 0L) {
            val absVal = arith.fromLong(iv)
            return if (sgn >= 0) absVal else arith.negate(absVal)
        }
        //should not get here, an overflow exception should have been thrown
        return 0 //overflow, everything was shifted out to the left
    }

    private fun divideByPowerOf10AndRound(sgn: Int, arith: DecimalArithmetic, rounding: DecimalRounding): Long {
        if (pow10 >= -18) {
            return getDecimal(sgn, pow10 + 18, 0, ival, val3, val2, val1, val0, 0, 0, 0, arith, rounding)
        } else if (pow10 >= -36) {
            return getDecimal(sgn, pow10 + 36, 0, 0, ival, val3, val2, val1, val0, 0, 0, arith, rounding)
        } else {
            //only rounding left
            if ((rounding !== DecimalRounding.DOWN) and ((ival != 0L) or (val3 != 0L) or (val2 != 0L) or (val1 != 0L) or (val0 != 0L))) {
                return rounding.calculateRoundingIncrement(sgn, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
            }
            return 0
        }
    }

    override fun toString(): String {
        var len: Int
        val sb =
            StringBuilder(64) //9-18 integral digits + 1 decimal point + 2*18 fractional digits + some extra for pow10 etc
        sb.append(ival)
        sb.append('.')
        len = sb.length
        sb.append(val3)
        sb.insert(len, "000000000000000000", 0, len + 18 - sb.length)
        len = sb.length
        sb.append(val2)
        sb.insert(len, "000000000000000000", 0, len + 18 - sb.length)
        if ((val1 != 0L) or (val0 != 0L)) {
            sb.append("..")
        }
        sb.append("*10^").append(pow10)
        return sb.toString()
    }

    companion object {
        /** Thread local for factor 1 */
        val THREAD_LOCAL_1: ThreadLocal<UnsignedDecimal9i36f> = object : ThreadLocal<UnsignedDecimal9i36f>() {
            override fun initialValue(): UnsignedDecimal9i36f {
                return UnsignedDecimal9i36f()
            }
        }

        /** Thread local for accumulator */
        val THREAD_LOCAL_2: ThreadLocal<UnsignedDecimal9i36f> = object : ThreadLocal<UnsignedDecimal9i36f>() {
            override fun initialValue(): UnsignedDecimal9i36f {
                return UnsignedDecimal9i36f()
            }
        }

        private fun getRoundingIncrement(
            sgn: Int,
            truncated: Long,
            scaleMetrics: ScaleMetrics,
            remainder: Long,
            nonZeroAfterRemainder: Boolean,
            rounding: DecimalRounding
        ): Int {
            if ((rounding !== DecimalRounding.DOWN) and ((remainder != 0L) or nonZeroAfterRemainder)) {
                var truncatedPart = truncatedPartFor(remainder, scaleMetrics.getScaleFactor())
                if (nonZeroAfterRemainder) {
                    if (truncatedPart === TruncatedPart.ZERO) truncatedPart = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO
                    else if (truncatedPart === TruncatedPart.EQUAL_TO_HALF) truncatedPart =
                        TruncatedPart.GREATER_THAN_HALF
                }
                return getRoundingIncrement(sgn, truncated, rounding, truncatedPart)
            }
            return 0
        }

        private fun getRoundingIncrement(
            sgn: Int,
            absValue: Long,
            rounding: DecimalRounding,
            truncatedPart: TruncatedPart
        ): Int {
            return if (sgn < 0) {
                -rounding.calculateRoundingIncrement(-1, -absValue, truncatedPart)
            } else {
                rounding.calculateRoundingIncrement(1, absValue, truncatedPart)
            }
        }

        //PRECONDITION: 0 <= pow10 <= 18
        private fun getDecimal(
            sgn: Int,
            pow10: Int,
            ival: Long,
            val3: Long,
            val2: Long,
            val1: Long,
            val0: Long,
            rem1: Long,
            rem2: Long,
            rem3: Long,
            rem4: Long,
            arith: DecimalArithmetic,
            rounding: DecimalRounding
        ): Long {
            val overflowMode = arith.overflowMode


            //apply pow10 first and convert to intVal and fra18 (with scale 18, w/o rounding)
            val int18: Long
            val fra18: Long
            val rem18: Long
            if (pow10 > 0) {
                val mul10Scale = Scales.getScaleMetrics(pow10)
                val div10Scale = Scales.getScaleMetrics(18 - pow10)
                val hiVal3 = div10Scale.divideByScaleFactor(val3)
                val loVal3 = val3 - div10Scale.multiplyByScaleFactor(hiVal3)
                val hiVal2 = div10Scale.divideByScaleFactor(val2)
                val loVal2 = val2 - div10Scale.multiplyByScaleFactor(hiVal2)
                int18 =
                    add(mulByScaleFactor(mul10Scale, ival, overflowMode), hiVal3, overflowMode) //overflow possible (2x)
                fra18 = mul10Scale.multiplyByScaleFactor(loVal3) + hiVal2 //cannot overflow because it is < 1
                rem18 = loVal2
            } else {
                int18 = ival
                fra18 = val3
                rem18 = val2
            }

            //apply scale now this time with rounding
            val diffMetrics = Scales.getScaleMetrics(18 - arith.scale)
            val fraVal = diffMetrics.divideByScaleFactor(fra18)
            val fraRem = fra18 - diffMetrics.multiplyByScaleFactor(fraVal)
            val inc = getRoundingIncrement(
                sgn,
                fraVal,
                diffMetrics,
                fraRem,
                (rem18 != 0L) or (val1 != 0L) or (val0 != 0L) or (rem1 != 0L) or (rem2 != 0L) or (rem3 != 0L) or (rem4 != 0L),
                rounding
            )
            val fraRnd = fraVal + inc //cannot overflow because it is <= 1
            val absVal = add(arith.fromLong(int18), fraRnd, overflowMode) //overflow possible (2x)
            return if (sgn >= 0) absVal else arith.negate(absVal)
        }

        private fun add(l1: Long, l2: Long, overflowMode: OverflowMode): Long {
            return if (overflowMode == OverflowMode.UNCHECKED) l1 + l2 else addLong(l1, l2)
        }

        private fun mulByScaleFactor(scaleMetrics: ScaleMetrics, `val`: Long, overflowMode: OverflowMode): Long {
            return if (`val` == 0L) 0 else if (overflowMode == OverflowMode.UNCHECKED) scaleMetrics.multiplyByScaleFactor(
                `val`
            ) else scaleMetrics.multiplyByScaleFactorExact(`val`)
        }

        private val LONG_TEN_POWERS_TABLE = longArrayOf(
            1,  // 0 / 10^0
            10,  // 1 / 10^1
            100,  // 2 / 10^2
            1000,  // 3 / 10^3
            10000,  // 4 / 10^4
            100000,  // 5 / 10^5
            1000000,  // 6 / 10^6
            10000000,  // 7 / 10^7
            100000000,  // 8 / 10^8
            1000000000,  // 9 / 10^9
            10000000000L,  // 10 / 10^10
            100000000000L,  // 11 / 10^11
            1000000000000L,  // 12 / 10^12
            10000000000000L,  // 13 / 10^13
            100000000000000L,  // 14 / 10^14
            1000000000000000L,  // 15 / 10^15
            10000000000000000L,  // 16 / 10^16
            100000000000000000L,  // 17 / 10^17
            1000000000000000000L // 18 / 10^18
        )

        /**
         * Returns the length of the absolute value of a `long`, in decimal
         * digits.
         *
         * @param absVal the `long`
         * @return the length of the unscaled value, in deciaml digits.
         */
        private fun log10(absVal: Long): Int {
            /*
         * As described in "Bit Twiddling Hacks" by Sean Anderson,
         * (http://graphics.stanford.edu/~seander/bithacks.html)
         * integer log 10 of x is within 1 of (1233/4096)* (1 +
         * integer log 2 of x). The fraction 1233/4096 approximates
         * log10(2). So we first do a version of log2 (a variant of
         * Long class with pre-checks and opposite directionality) and
         * then scale and check against powers table. This is a little
         * simpler in present context than the version in Hacker's
         * Delight sec 11-4. Adding one to bit length allows comparing
         * downward from the LONG_TEN_POWERS_TABLE that we need
         * anyway.
         */
            if (absVal < 10)  // must screen for 0, might as well 10
                return 1
            val r = ((64 - java.lang.Long.numberOfLeadingZeros(absVal) + 1) * 1233) ushr 12
            val tab = LONG_TEN_POWERS_TABLE
            // if r >= length, must have max possible digits for long
            return if (r >= tab.size || absVal < tab[r]) r else r + 1
        }
    }
}