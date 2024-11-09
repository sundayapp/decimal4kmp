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
import org.decimal4j.arithmetic.CheckedScale0fRoundingArithmetic
import org.decimal4j.arithmetic.CheckedScale0fTruncatingArithmetic
import org.decimal4j.arithmetic.UncheckedScale0fRoundingArithmetic
import org.decimal4j.arithmetic.UncheckedScale0fTruncatingArithmetic
import org.decimal4j.truncate.DecimalRounding
import org.decimal4j.truncate.OverflowMode
import org.decimal4j.truncate.TruncationPolicy
import java.math.BigDecimal
import java.math.BigInteger
import org.decimal4j.api.RoundingMode

/**
 * Scale class for decimals with [scale][.getScale] 0 and
 * [scale factor][.getScaleFactor] 1. Decimals with scale zero are
 * essentially longs.
 */
enum class Scale0f : ScaleMetrics {
    INSTANCE;

    override fun getScale():Int {
        return SCALE;
    }


    override fun getScaleFactor(): Long {
        return SCALE_FACTOR;
    }


    override fun getScaleFactorNumberOfLeadingZeros():Int {
        return NLZ_SCALE_FACTOR;
    }


    override fun getScaleFactorAsBigInteger(): BigInteger {
        return BigInteger.ONE;
    }


    override fun getScaleFactorAsBigDecimal():BigDecimal {
        return BigDecimal.ONE;
    }


    override fun getMaxIntegerValue(): Long {
        return Long.MAX_VALUE;
    }


    override fun getMinIntegerValue(): Long {
        return Long.MIN_VALUE;
    }


    override fun isValidIntegerValue(value: Long): Boolean {
        return true;
    }


    override fun multiplyByScaleFactor(factor: Long): Long {
        return factor;
    }


    override fun multiplyByScaleFactorExact(factor: Long) : Long{
        return factor;
    }


    override fun mulloByScaleFactor(factor: Int): Long {
        return factor.toLong() and LONG_MASK
    }


    override fun mulhiByScaleFactor(factor: Int) : Long{
        return 0;
    }


    override fun divideByScaleFactor( dividend: Long) : Long{
        return dividend;
    }


    override fun divideUnsignedByScaleFactor(unsignedDividend: Long): Long {
        return unsignedDividend;
    }


    override fun moduloByScaleFactor(dividend: Long): Long {
        return 0;
    }


    override fun toString(value: Long): String {
        return value.toString();
    }


    override fun getDefaultArithmetic(): DecimalArithmetic {
        return DEFAULT_ARITHMETIC;
    }


    override fun getDefaultCheckedArithmetic(): DecimalArithmetic {
        return DEFAULT_CHECKED_ARITHMETIC;
    }


    override fun getRoundingDownArithmetic(): DecimalArithmetic {
        return ROUNDING_DOWN_ARITHMETIC;
    }


    override fun getRoundingFloorArithmetic(): DecimalArithmetic {
        return ROUNDING_FLOOR_ARITHMETIC;
    }


    override fun getRoundingHalfEvenArithmetic(): DecimalArithmetic {
        return ROUNDING_HALF_EVEN_ARITHMETIC;
    }


    override fun getRoundingUnnecessaryArithmetic(): DecimalArithmetic {
        return ROUNDING_UNNECESSARY_ARITHMETIC;
    }


    override fun getArithmetic(roundingMode: RoundingMode): DecimalArithmetic {
        return UNCHECKED_ARITHMETIC[roundingMode.ordinal]!!;
    }


    override fun getCheckedArithmetic(roundingMode: RoundingMode): DecimalArithmetic {
        return CHECKED_ARITHMETIC[roundingMode.ordinal]!!;
    }


    override fun getArithmetic(truncationPolicy: TruncationPolicy): DecimalArithmetic {
        val overflow = truncationPolicy.getOverflowMode()
        val rounding = truncationPolicy.getRoundingMode()
        return (if (overflow == OverflowMode.UNCHECKED) UNCHECKED_ARITHMETIC else CHECKED_ARITHMETIC)[rounding.ordinal]!!
    }



    override fun toString(): String {
        return "Scale0f";
    }
    
    companion object {
        /**
         * The scale value `0`.
         */
        const val SCALE: Int = 0

        /**
         * The scale factor `10<sup>0</sup>`.
         */
        const val SCALE_FACTOR: Long = 1L

        /** Long.numberOfLeadingZeros(SCALE_FACTOR) */
        const val NLZ_SCALE_FACTOR: Int = 63

        const val LONG_MASK: Long = 0xffffffffL

        val UNCHECKED_ARITHMETIC: Array<DecimalArithmetic?> = initArithmetic(OverflowMode.UNCHECKED)
        val CHECKED_ARITHMETIC: Array<DecimalArithmetic?> = initArithmetic(OverflowMode.CHECKED)

        val DEFAULT_ARITHMETIC: DecimalArithmetic = UNCHECKED_ARITHMETIC[RoundingMode.HALF_UP.ordinal]!!
        val DEFAULT_CHECKED_ARITHMETIC: DecimalArithmetic = CHECKED_ARITHMETIC[RoundingMode.HALF_UP.ordinal]!!
        val ROUNDING_DOWN_ARITHMETIC: DecimalArithmetic = UNCHECKED_ARITHMETIC[RoundingMode.DOWN.ordinal]!!
        val ROUNDING_FLOOR_ARITHMETIC: DecimalArithmetic = UNCHECKED_ARITHMETIC[RoundingMode.FLOOR.ordinal]!!
        val ROUNDING_HALF_EVEN_ARITHMETIC: DecimalArithmetic = UNCHECKED_ARITHMETIC[RoundingMode.HALF_EVEN.ordinal]!!
        val ROUNDING_UNNECESSARY_ARITHMETIC: DecimalArithmetic = UNCHECKED_ARITHMETIC[RoundingMode.UNNECESSARY.ordinal]!!

        private fun initArithmetic(overflowMode: OverflowMode): Array<DecimalArithmetic?> {
            val checked = overflowMode == OverflowMode.CHECKED
            val arith = arrayOfNulls<DecimalArithmetic>(DecimalRounding.VALUES.size)
            for (dr in DecimalRounding.VALUES) {
                val index = dr.getRoundingMode().ordinal
                if (dr === DecimalRounding.DOWN) {
                    arith[index] = if (checked)
                        CheckedScale0fTruncatingArithmetic.INSTANCE
                    else
                        UncheckedScale0fTruncatingArithmetic.INSTANCE
                } else {
                    arith[index] = if (checked)
                        CheckedScale0fRoundingArithmetic(dr)
                    else
                        UncheckedScale0fRoundingArithmetic(dr)
                }
            }
            return arith
        }
    }
}