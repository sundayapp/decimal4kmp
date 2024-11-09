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

import org.decimal4j.truncate.TruncatedPart
import org.junit.Assert
import org.junit.Test
import kotlin.math.abs
import org.junit.Assert.assertEquals

/**
 * Unit test for [Rounding]
 */
class RoundingUtilTest {
    @Test
    fun testRemainingZeroOfOne() {
        assertEquals(TruncatedPart.ZERO, Rounding.truncatedPartFor(0, 1));
    }
    @Test
    fun testRemainingOneOfTwo() {
        assertEquals(TruncatedPart.EQUAL_TO_HALF, Rounding.truncatedPartFor(1, 2));
    }
    @Test
    fun testRemainingOneOfThree() {
        assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, Rounding.truncatedPartFor(1, 3));
    }
    @Test
    fun testRemainingTwoOfThree() {
        assertEquals(TruncatedPart.GREATER_THAN_HALF, Rounding.truncatedPartFor(2, 3));
    }
    @Test
    fun testRemainingLongMaxHalfOfLongMax() {
        assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, Rounding.truncatedPartFor(Long.MAX_VALUE/2, Long.MAX_VALUE));
    }
    @Test
    fun testRemainingLongMaxHalfMinusOneOfLongMax() {
        assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, Rounding.truncatedPartFor(Long.MAX_VALUE/2-1, Long.MAX_VALUE));
    }
    @Test
    fun testRemainingLongMaxHalfPlusOneOfLongMax() {
        assertEquals(TruncatedPart.GREATER_THAN_HALF, Rounding.truncatedPartFor(Long.MAX_VALUE/2+1, Long.MAX_VALUE));
    }
    @Test
    fun testRemainingLongMaxHalfOfLongMaxMinusOne() {
        assertEquals(TruncatedPart.EQUAL_TO_HALF, Rounding.truncatedPartFor(Long.MAX_VALUE/2, Long.MAX_VALUE - 1));
    }
    @Test
    fun testRemainingLongMaxHalfMinusOneOfLongMaxMinusOne() {
        assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, Rounding.truncatedPartFor(Long.MAX_VALUE/2-1, Long.MAX_VALUE - 1));
    }
    @Test
    fun testRemainingLongMaxHalfPlusOneOfLongMaxMinusOne() {
        assertEquals(TruncatedPart.GREATER_THAN_HALF, Rounding.truncatedPartFor(Long.MAX_VALUE/2+1, Long.MAX_VALUE - 1));
    }
    @Test
    fun testRemainingLongMinHalfOfLongMin() {
        assertEquals(TruncatedPart.EQUAL_TO_HALF, Rounding.truncatedPartFor(Math.abs(Long.MIN_VALUE/2), Long.MIN_VALUE));
    }
    @Test
    fun testRemainingLongMinHalfMinusOneOfLongMin() {
        assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, Rounding.truncatedPartFor(Math.abs(Long.MIN_VALUE/2)-1, Long.MIN_VALUE));
    }
    @Test
    fun testRemainingLongMinHalfPlusOneOfLongMin() {
        assertEquals(TruncatedPart.GREATER_THAN_HALF, Rounding.truncatedPartFor(Math.abs(Long.MIN_VALUE/2)+1, Long.MIN_VALUE));
    }
}
