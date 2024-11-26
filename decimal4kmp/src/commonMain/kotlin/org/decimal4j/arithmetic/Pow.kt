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
import org.decimal4j.arithmetic.Checked.multiplyLong
import org.decimal4j.arithmetic.LongConversion.longToUnscaled
import org.decimal4j.arithmetic.LongConversion.longToUnscaledUnchecked
import org.decimal4j.truncate.DecimalRounding
import org.decimal4j.truncate.OverflowMode
import org.decimal4j.truncate.TruncatedPart
import kotlin.math.abs
import kotlin.math.sign

/**
 * Contains static methods to calculate powers of a Decimal number.
 */
internal object Pow {
    /**
     * Constant for `floor(sqrt(Long.MAX_VALUE))`
     */
    private const val FLOOR_SQRT_MAX_LONG = 3037000499L

    private fun checkExponent(exponent: Int) {
        require(!(exponent < -999999999 || exponent > 999999999)) { "Exponent must be in [-999999999,999999999] but was: $exponent" }
    }

    /**
     * Calculates the power `(lBase<sup>exponent</sup>)`. Overflows are
     * silently ignored.
     *
     * @param arith
     * the arithmetic associated with `lBase`
     * @param rounding
     * the rounding to apply if rounding is necessary for negative
     * exponents
     * @param lBase
     * the unscaled decimal base value
     * @param exponent
     * the exponent
     * @return `round(lBase<sup>exponent</sup>)`
     * @throws ArithmeticException
     * if `lBase==0` and the exponent is negative or if
     * `roundingMode==UNNECESSARY` and rounding is necessary
     */
	
	fun powLong(arith: DecimalArithmetic, rounding: DecimalRounding, lBase: Long, exponent: Int): Long {
        checkExponent(exponent)
        val special = SpecialPowResult.getFor(arith, lBase, exponent.toLong())
        if (special != null) {
            return special.pow(arith, lBase, exponent)
        }
        return powLong(rounding, lBase, exponent)
    }

    private fun powLong(rounding: DecimalRounding, lBase: Long, exponent: Int): Long {
        if (exponent >= 0) {
            return powLongWithPositiveExponent(lBase, exponent)
        } else {
            // result is 1/powered
            // we have dealt with special cases above hence powered is neither
            // of 0, 1, -1
            // and everything else can't be 0.5 because sqrt_i(0.5) is not real
            val sgn = if ((lBase > 0) or ((exponent and 0x1) == 0)) 1 else -1 // lBase
            // cannot
            // be 0
            return rounding.calculateRoundingIncrement(sgn, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        }
    }

    /**
     * Calculates the power `(lBase<sup>exponent</sup>)`. An exception is
     * thrown if an overflow occurs.
     *
     * @param arith
     * the arithmetic associated with `lBase`
     * @param rounding
     * the rounding to apply if rounding is necessary for negative
     * exponents
     * @param lBase
     * the unscaled decimal base value
     * @param exponent
     * the exponent
     * @return `round(lBase<sup>exponent</sup>)`
     * @throws ArithmeticException
     * if `lBase==0` and the exponent is negative, if
     * `roundingMode==UNNECESSARY` and rounding is necessary
     * or if an overflow occurs and the arithmetic's
     * [OverflowMode] is set to throw an exception
     */
    fun powLongChecked(arith: DecimalArithmetic, rounding: DecimalRounding, lBase: Long, exponent: Int): Long {
        checkExponent(exponent)
        val special = SpecialPowResult.getFor(arith, lBase, exponent.toLong())
        if (special != null) {
            return special.pow(arith, lBase, exponent)
        }
        return powLongChecked(rounding, lBase, exponent)
    }

    private fun powLongChecked(rounding: DecimalRounding, lBase: Long, exponent: Int): Long {
        if (exponent >= 0) {
            return powLongCheckedWithPositiveExponent(lBase, exponent)
        } else {
            // result is 1/powered
            // we have dealt with special cases above hence powered is neither
            // of 0, 1, -1
            // and everything else can't be 0.5 because sqrt_i(0.5) is not real
            val sgn = if ((lBase > 0) or ((exponent and 0x1) == 0)) 1 else -1 // lBase
            // cannot
            // be 0
            return rounding.calculateRoundingIncrement(sgn, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        }
    }

    private fun powLongCheckedOrUnchecked(
        overflowMode: OverflowMode,
        rounding: DecimalRounding,
        longBase: Long,
        exponent: Int
    ): Long {
        return if (overflowMode == OverflowMode.UNCHECKED)
            powLong(rounding, longBase, exponent)
        else
            powLongChecked(rounding, longBase, exponent)
    }

    /**
     * Power function for checked or unchecked arithmetic. The result is within
     * 1 ULP for positive exponents.
     *
     * @param arith
     * the arithmetic
     * @param rounding
     * the rounding to apply
     * @param uDecimalBase
     * the unscaled base
     * @param exponent
     * the exponent
     * @return `uDecimalbase ^ exponent`
     */
	
	fun pow(arith: DecimalArithmetic, rounding: DecimalRounding, uDecimalBase: Long, exponent: Int): Long {
        checkExponent(exponent)
        val special = SpecialPowResult.getFor(arith, uDecimalBase, exponent.toLong())
        if (special != null) {
            return special.pow(arith, uDecimalBase, exponent)
        }

        // some other special cases
        val scaleMetrics = arith.scaleMetrics

        val intVal = scaleMetrics.divideByScaleFactor(uDecimalBase)
        val fraVal = uDecimalBase - scaleMetrics.multiplyByScaleFactor(intVal)
        if ((exponent >= 0) and (fraVal == 0L)) {
            // integer
            val result = powLongCheckedOrUnchecked(arith.overflowMode, rounding, intVal, exponent)
            return longToUnscaledCheckedOrUnchecekd(arith, uDecimalBase, exponent, result)
        }
        if ((exponent < 0) and (intVal == 0L)) {
            val one = scaleMetrics.getScaleFactor()
            if ((one % fraVal) == 0L) {
                // inverted value is an integer
                val result = powLongCheckedOrUnchecked(
                    arith.overflowMode, rounding, one / fraVal,
                    -exponent
                )
                return longToUnscaledCheckedOrUnchecekd(arith, uDecimalBase, exponent, result)
            }
        }
        try {
            return powWithPrecision18(arith, rounding, intVal, fraVal, exponent)
        } catch (e: IllegalArgumentException) {
            throw ArithmeticException("Overflow: " + arith.toString(uDecimalBase) + "^" + exponent)
        }
    }

    // PRECONDITION: n != 0 and n in [-999999999,999999999]
    private fun powWithPrecision18(
        arith: DecimalArithmetic,
        rounding: DecimalRounding,
        ival: Long,
        fval: Long,
        n: Int
    ): Long {
        // eliminate sign
        val sgn = if ((n and 0x1) != 0) (ival or fval).sign else 1
        val absInt = abs(ival)
        val absFra = abs(fval)
        val powRounding = if (n >= 0) rounding else RoundingInverse.RECIPROCAL.invert(rounding)

        // 36 digit left hand side, initialized with base value
        val lhs = UnsignedDecimal9i36f().init(
            absInt, absFra,
            arith.scaleMetrics
        )

        // 36 digit accumulator, initialized with one
        val acc = UnsignedDecimal9i36f().initOne()

        // ready to carry out power calculation...
        var mag = abs(n)
        var seenbit = false // avoid squaring ONE
        var i = 1
        while (true) {
            // for each bit [top bit ignored]
            mag += mag // shift left 1 bit
            if (mag < 0) { // top bit is set
                if (seenbit) {
                    acc.multiply(sgn, lhs, powRounding) // acc=acc*x
                } else {
                    seenbit = true
                    acc.init(lhs) // acc=x
                }
            }
            if (i == 31) {
                break // that was the last bit
            }
            if (seenbit) {
                acc.multiply(sgn, acc, powRounding) // acc=acc*acc [square]
            } // else (!seenbit) no point in squaring ONE

            i++
        }

        if (n < 0) {
            return acc.getInverted(sgn, arith, rounding, powRounding)
        }
        return acc.getDecimal(sgn, arith, rounding)
    }

    private fun powLongWithPositiveExponent(lBase: Long, exponent: Int): Long {
        var lBase = lBase
        var exponent = exponent
        require(exponent > 0)

        var accum: Long = 1
        while (true) {
            when (exponent) {
                0 -> return accum
                1 -> return accum * lBase
                else -> {
                    if ((exponent and 1) != 0) {
                        accum *= lBase
                    }
                    exponent = exponent shr 1
                    if (exponent > 0) {
                        lBase *= lBase
                    }
                }
            }
        }
    }

    private fun powLongCheckedWithPositiveExponent(lBase: Long, exponent: Int): Long {
        var lBase = lBase
        var exponent = exponent
        require(exponent > 0)
        if ((lBase >= -2) and (lBase <= 2)) {
            when (lBase.toInt()) {
                0 -> return (if (exponent == 0) 1 else 0).toLong()
                1 -> return 1
                (-1) -> return (if ((exponent and 1) == 0) 1 else -1).toLong()
                2 -> {
                    if (exponent >= Long.SIZE_BITS - 1) {
                        throw ArithmeticException("Overflow: $lBase^$exponent")
                    }
                    return 1L shl exponent
                }

                (-2) -> {
                    if (exponent >= Long.SIZE_BITS) {
                        throw ArithmeticException("Overflow: $lBase^$exponent")
                    }
                    return if ((exponent and 1) == 0) (1L shl exponent) else (-1L shl exponent)
                }

                else -> throw AssertionError()
            }
        }
        var accum: Long = 1
        while (true) {
            when (exponent) {
                0 -> return accum
                1 -> return multiplyLong(accum, lBase)
                else -> {
                    if ((exponent and 1) != 0) {
                        accum = multiplyLong(accum, lBase)
                    }
                    exponent = exponent shr 1
                    if (exponent > 0) {
                        if ((lBase > FLOOR_SQRT_MAX_LONG) or (lBase < -FLOOR_SQRT_MAX_LONG)) {
                            throw ArithmeticException("Overflow: $lBase^$exponent")
                        }
                        lBase *= lBase
                    }
                }
            }
        }
    }

    private fun longToUnscaledCheckedOrUnchecekd(
        arith: DecimalArithmetic,
        uBase: Long,
        exponent: Int,
        longResult: Long
    ): Long {
        if (!arith.overflowMode.isChecked) {
            return longToUnscaledUnchecked(arith.scaleMetrics, longResult)
        }
        try {
            return longToUnscaled(arith.scaleMetrics, longResult)
        } catch (e: IllegalArgumentException) {
            throw ArithmeticException("Overflow: " + arith.toString(uBase) + "^" + exponent + "=" + longResult)
        }
    }
}
