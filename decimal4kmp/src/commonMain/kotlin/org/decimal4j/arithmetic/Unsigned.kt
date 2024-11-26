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

/**
 * Helper class to emulate unsigned 64bit operations.
 */
object Unsigned {
    /**
     * A (self-inverse) bijection which converts the ordering on unsigned longs
     * to the ordering on longs, that is, `a <= b` as unsigned longs if
     * and only if `flip(a) <= flip(b)` as signed longs.
     *
     *
     * From Guava's [UnsignedLongs](http://docs.guava-libraries.googlecode.com/git/javadoc/src-html/com/google/common/primitives/UnsignedLongs.html).
     *
     * @param a the unsigned long value to flip
     * @return the flipped value
     */
    private fun flip(a: Long): Long {
        return a xor Long.MIN_VALUE
    }

    /**
     * Compares the two specified `long` values, treating them as unsigned
     * values between `0` and `2^64 - 1` inclusive.
     *
     *
     * From Guava's [UnsignedLongs](http://docs.guava-libraries.googlecode.com/git/javadoc/src-html/com/google/common/primitives/UnsignedLongs.html).
     *
     * @param a
     * the first unsigned `long` to compare
     * @param b
     * the second unsigned `long` to compare
     * @return a negative value if `a` is less than `b`; a positive
     * value if `a` is greater than `b`; or zero if they are
     * equal
     */
    fun compare(a: Long, b: Long): Int {
        return flip(a).compareTo(flip(b))
    }

    /**
     * Compare two longs as if they were unsigned. Returns true iff one is
     * bigger than two.
     *
     * @param one
     * the first unsigned `long` to compare
     * @param two
     * the second unsigned `long` to compare
     * @return true if `one > two`
     */
    fun isGreater(one: Long, two: Long): Boolean {
        return flip(one) > flip(two)
    }

    /**
     * Compare two longs as if they were unsigned. Returns true iff one is
     * smaller than two.
     *
     * @param one
     * the first unsigned `long` to compare
     * @param two
     * the second unsigned `long` to compare
     * @return true if `one < two`
     */
	fun isLess(one: Long, two: Long): Boolean {
        return flip(one) < flip(two)
    }

    /**
     * Compare two longs as if they were unsigned. Returns true iff one is less
     * than or equal to two.
     *
     * @param one
     * the first unsigned `long` to compare
     * @param two
     * the second unsigned `long` to compare
     * @return true if `one <= two`
     */
    fun isLessOrEqual(one: Long, two: Long): Boolean {
        return flip(one) <= flip(two)
    }

    /**
     * Returns dividend / divisor, where the dividend and divisor are treated as
     * unsigned 64-bit quantities.
     *
     * @param dividend
     * the dividend (numerator)
     * @param divisor
     * the divisor (denominator)
     * @return `dividend / divisor`
     * @throws ArithmeticException
     * if divisor is 0
     */
    fun divide(dividend: Long, divisor: Long): Long {
        if (divisor < 0) { // i.e., divisor >= 2^63:
            return (if (compare(dividend, divisor) < 0) 0 else 1).toLong()
        }

        // Optimization - use signed division if dividend < 2^63
        if (dividend >= 0) {
            return dividend / divisor
        }
        // If divisor is even, we can divide both by 2
        if (0L == (divisor and 0x1L)) {
            return (dividend ushr 1) / (divisor ushr 1)
        }

        /*
		 * Otherwise, approximate the quotient, check, and correct if necessary.
		 * Our approximation is guaranteed to be either exact or one less than
		 * the correct value. This follows from fact that floor(floor(x)/i) ==
		 * floor(x/i) for any real x and integer i != 0. The proof is not quite
		 * trivial.
		 */
        val quotient = ((dividend ushr 1) / divisor) shl 1
        val rem = dividend - quotient * divisor
        return quotient + (if (isLess(rem, divisor)) 0 else 1)
    }
}
