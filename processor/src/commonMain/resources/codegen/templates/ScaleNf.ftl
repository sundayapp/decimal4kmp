package org.decimal4j.scale

import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.arithmetic.CheckedScaleNfRoundingArithmetic
import org.decimal4j.arithmetic.CheckedScaleNfTruncatingArithmetic
import org.decimal4j.arithmetic.UncheckedScaleNfRoundingArithmetic
import org.decimal4j.arithmetic.UncheckedScaleNfTruncatingArithmetic
import org.decimal4j.truncate.DecimalRounding
import org.decimal4j.truncate.OverflowMode
import org.decimal4j.truncate.TruncationPolicy
import org.decimal4j.truncate.RoundingMode

/**
 * Scale class for decimals with [scale][.getScale] ${scale} and
 * [scale factor][.getScaleFactor] ${"1"?right_pad(scale+1, "0")}.
 */
enum class Scale${scale}f : ScaleMetrics {
    /**
     * The singleton instance for scale ${scale}.
     */
    INSTANCE;

    override fun getScale(): Int {
        return SCALE
    }

    override fun getScaleFactor(): Long {
        return SCALE_FACTOR
    }

    override fun getScaleFactorNumberOfLeadingZeros(): Int {
        return NLZ_SCALE_FACTOR
    }

    override fun multiplyByScaleFactor(factor: Long): Long {
        return factor * SCALE_FACTOR
    }

    override fun getMaxIntegerValue(): Long {
        return MAX_INTEGER_VALUE
    }

    override fun getMinIntegerValue(): Long {
        return MIN_INTEGER_VALUE
    }

    override fun isValidIntegerValue(value: Long): Boolean {
        return (MIN_INTEGER_VALUE <= value) and (value <= MAX_INTEGER_VALUE)
    }

    override fun multiplyByScaleFactorExact(factor: Long): Long {
        val result = factor * SCALE_FACTOR
        if ((MIN_INTEGER_VALUE <= factor) and (factor <= MAX_INTEGER_VALUE)) {
            return result
        }
        throw ArithmeticException("Overflow: " + factor + " * " + SCALE_FACTOR + " = " + result)
    }

    override fun mulloByScaleFactor(factor: Int): Long {
<#if (scale > 9)>
        return (factor.toLong() and LONG_MASK) * SCALE_FACTOR_LOW_BITS
<#else>
        return (factor.toLong() and LONG_MASK) * SCALE_FACTOR
</#if>
    }

    override fun mulhiByScaleFactor(factor: Int): Long {
<#if (scale > 9)>
        return (factor.toLong() and LONG_MASK) * SCALE_FACTOR_HIGH_BITS
<#else>
        return 0
</#if>
    }

    override fun divideByScaleFactor(dividend: Long): Long {
        return dividend / SCALE_FACTOR
    }

    override fun divideUnsignedByScaleFactor(unsignedDividend: Long): Long {
        //we can do this since SCALE_FACTOR > 1 and even
        return (unsignedDividend ushr 1) / (SCALE_FACTOR ushr 1)
    }

    override fun moduloByScaleFactor(dividend: Long): Long {
        return dividend % SCALE_FACTOR
    }

    override fun toString(value: Long): String {
        return DEFAULT_ARITHMETIC.toString(value)
    }

    override fun getDefaultArithmetic(): DecimalArithmetic {
        return DEFAULT_ARITHMETIC
    }

    override fun getDefaultCheckedArithmetic(): DecimalArithmetic {
        return DEFAULT_CHECKED_ARITHMETIC
    }

    override fun getRoundingDownArithmetic(): DecimalArithmetic {
        return ROUNDING_DOWN_ARITHMETIC
    }

    override fun getRoundingFloorArithmetic(): DecimalArithmetic {
        return ROUNDING_FLOOR_ARITHMETIC
    }

    override fun getRoundingHalfEvenArithmetic(): DecimalArithmetic {
        return ROUNDING_HALF_EVEN_ARITHMETIC
    }

    override fun getRoundingUnnecessaryArithmetic(): DecimalArithmetic {
        return ROUNDING_UNNECESSARY_ARITHMETIC
    }

    override fun getArithmetic(roundingMode: RoundingMode): DecimalArithmetic {
        return UNCHECKED_ARITHMETIC[roundingMode.ordinal]!!
    }

    override fun getCheckedArithmetic(roundingMode: RoundingMode): DecimalArithmetic {
        return CHECKED_ARITHMETIC[roundingMode.ordinal]!!
    }

    override fun getArithmetic(truncationPolicy: TruncationPolicy): DecimalArithmetic {
        val overflow = truncationPolicy.getOverflowMode()
        val rounding = truncationPolicy.getRoundingMode()
        return (if (overflow == OverflowMode.UNCHECKED) UNCHECKED_ARITHMETIC else CHECKED_ARITHMETIC)[rounding.ordinal]!!
    }

    override fun toString(): String {
        return "Scale${scale}f"
    }

    companion object {
        private const val LONG_MASK = 0xffffffffL

        /**
         * The scale value `${scale}`.
         */
        const val SCALE: Int = ${scale}

        /**
         * The scale factor `10<sup>${scale}</sup>`.
         */
        const val SCALE_FACTOR: Long = ${"1"?right_pad(scale+1, "0")}L

        /** Long.numberOfLeadingZeros(SCALE_FACTOR) */
        private const val NLZ_SCALE_FACTOR = ${nlzScaleFactor[scale]}

<#if (scale > 9)>
        private const val SCALE_FACTOR_HIGH_BITS = SCALE_FACTOR ushr 32
        private const val SCALE_FACTOR_LOW_BITS = SCALE_FACTOR and LONG_MASK

</#if>
        private const val MAX_INTEGER_VALUE = Long.MAX_VALUE / SCALE_FACTOR
        private const val MIN_INTEGER_VALUE = Long.MIN_VALUE / SCALE_FACTOR

        private val UNCHECKED_ARITHMETIC = initArithmetic(OverflowMode.UNCHECKED)
        private val CHECKED_ARITHMETIC = initArithmetic(OverflowMode.CHECKED)

        private val DEFAULT_ARITHMETIC = UNCHECKED_ARITHMETIC[RoundingMode.HALF_UP.ordinal]!!
        private val DEFAULT_CHECKED_ARITHMETIC = CHECKED_ARITHMETIC[RoundingMode.HALF_UP.ordinal]!!
        private val ROUNDING_DOWN_ARITHMETIC = UNCHECKED_ARITHMETIC[RoundingMode.DOWN.ordinal]!!
        private val ROUNDING_FLOOR_ARITHMETIC = UNCHECKED_ARITHMETIC[RoundingMode.FLOOR.ordinal]!!
        private val ROUNDING_HALF_EVEN_ARITHMETIC = UNCHECKED_ARITHMETIC[RoundingMode.HALF_EVEN.ordinal]!!
        private val ROUNDING_UNNECESSARY_ARITHMETIC = UNCHECKED_ARITHMETIC[RoundingMode.UNNECESSARY.ordinal]!!

        private fun initArithmetic(overflowMode: OverflowMode): Array<DecimalArithmetic?> {
            val checked = overflowMode == OverflowMode.CHECKED
            val arith = arrayOfNulls<DecimalArithmetic>(DecimalRounding.VALUES.size)
            for (dr in DecimalRounding.VALUES) {
                val index = dr.getRoundingMode().ordinal
                if (dr === DecimalRounding.DOWN) {
                    arith[index] = if (checked)
                        CheckedScaleNfTruncatingArithmetic(INSTANCE)
                    else
                        UncheckedScaleNfTruncatingArithmetic(INSTANCE)
                } else {
                    arith[index] = if (checked)
                        CheckedScaleNfRoundingArithmetic(INSTANCE, dr)
                    else
                        UncheckedScaleNfRoundingArithmetic(INSTANCE, dr)
                }
            }
            return arith
        }
    }
}