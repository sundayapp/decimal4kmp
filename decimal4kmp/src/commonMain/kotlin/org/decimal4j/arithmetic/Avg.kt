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
import org.decimal4j.truncate.DecimalRounding

/**
 * Provides static methods to calculate average of two numbers, that is,
 * `(a+b)/2`.
 */
internal object Avg {
    /**
     * Calculates and returns the average of the two values rounded DOWN.
     *
     * @param a
     * the first value
     * @param b
     * the second value
     * @return `round<sub>DOWN</sub>((a + b) / 2)`
     */
	fun avg(a: Long, b: Long): Long {
        val xor = a xor b
        val floor = (a and b) + (xor shr 1)
        return floor + ((floor ushr 63) and xor)
    }

    /**
     * Calculates and returns the average of the two values applying the given
     * rounding if necessary.
     *
     * @param arith
     * the arithmetic associated with the two values
     * @param rounding
     * the rounding to apply if necessary
     * @param a
     * the first value
     * @param b
     * the second value
     * @return `round((a + b) / 2)`
     */
	fun avg(arith: DecimalArithmetic, rounding: DecimalRounding, a: Long, b: Long): Long {
        val xor = a xor b
        when (rounding) {
            DecimalRounding.FLOOR -> {
                return (a and b) + (xor shr 1)
            }

            DecimalRounding.CEILING -> {
                return (a or b) - (xor shr 1)
            }

            DecimalRounding.DOWN, DecimalRounding.HALF_DOWN -> {
                val floor = (a and b) + (xor shr 1)
                return floor + ((floor ushr 63) and xor)
            }

            DecimalRounding.UP, DecimalRounding.HALF_UP -> {
                val floor = (a and b) + (xor shr 1)
                return floor + ((floor.inv() ushr 63) and xor)
            }

            DecimalRounding.HALF_EVEN -> {
                val xorShifted = xor shr 1
                val floor = (a and b) + xorShifted
                // use ceiling if floor is odd
                return if (((floor and 0x1L) == 0L)) floor else (a or b) - xorShifted
            }

            DecimalRounding.UNNECESSARY -> {
                val floor = (a and b) + (xor shr 1)
                if ((xor and 0x1L) != 0L) {
                    throw ArithmeticException(
                        ("Rounding necessary: " + arith.toString(a) + " avg " + arith.toString(b)
                                + " = " + arith.toString(floor))
                    )
                }
                return floor
            }

            else -> {
                // should not get here
                throw IllegalArgumentException("Unsupported rounding mode: $rounding")
            }
        }
    }
}
