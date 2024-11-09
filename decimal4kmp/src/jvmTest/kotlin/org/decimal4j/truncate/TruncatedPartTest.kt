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
package org.decimal4j.truncate

import org.junit.Assert
import org.junit.Test

/**
 * Unit test for [TruncatedPart].
 */
class TruncatedPartTest {
    @Test
    fun testFirstZeroRestZero() {
        Assert.assertEquals(TruncatedPart.ZERO, TruncatedPart.valueOf(0, true))
    }

    @Test
    fun testFirstOneRestZero() {
        Assert.assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, TruncatedPart.valueOf(1, true))
    }

    @Test
    fun testFirstFiveRestZero() {
        Assert.assertEquals(TruncatedPart.EQUAL_TO_HALF, TruncatedPart.valueOf(5, true))
    }

    @Test
    fun testFirstSixRestZero() {
        Assert.assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(6, true))
    }

    @Test
    fun testFirstNineRestZero() {
        Assert.assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(9, false))
    }

    @Test
    fun testFirstZeroRestNonZero() {
        Assert.assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, TruncatedPart.valueOf(0, false))
    }

    @Test
    fun testFirstOneRestNonZero() {
        Assert.assertEquals(TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO, TruncatedPart.valueOf(1, false))
    }

    @Test
    fun testFirstFiveRestNonZero() {
        Assert.assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(5, false))
    }

    @Test
    fun testFirstSixRestNonZero() {
        Assert.assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(6, false))
    }

    @Test
    fun testFirstNineRestNonZero() {
        Assert.assertEquals(TruncatedPart.GREATER_THAN_HALF, TruncatedPart.valueOf(9, false))
    }

    @Test
    fun testGreaterThanZero() {
        val expectTrue = TruncatedPart.entries.filter { it != TruncatedPart.ZERO }.toSet()
        for (part in TruncatedPart.entries) {
            Assert.assertEquals(expectTrue.contains(part), part.isGreaterThanZero())
        }
    }

    @Test
    fun testEqualToHalf() {
        val expectTrue = setOf(TruncatedPart.EQUAL_TO_HALF)
        for (part in TruncatedPart.entries) {
            Assert.assertEquals(expectTrue.contains(part), part.isEqualToHalf())
        }
    }

    @Test
    fun testGreaterEqualHalf() {
        val expectTrue = setOf(TruncatedPart.EQUAL_TO_HALF, TruncatedPart.GREATER_THAN_HALF)
        for (part in TruncatedPart.entries) {
            Assert.assertEquals(expectTrue.contains(part), part.isGreaterEqualHalf())
        }
    }

    @Test
    fun testGreaterThanHalf() {
        val expectTrue = setOf(TruncatedPart.GREATER_THAN_HALF)
        for (part in TruncatedPart.entries) {
            Assert.assertEquals(expectTrue.contains(part), part.isGreaterThanHalf())
        }
    }

    @Test
    fun testValueOf() {
        //a bit a thumb test but we do a lot to get 100% coverage... 
        for (part in TruncatedPart.entries) {
            Assert.assertSame("should be same instance", part, TruncatedPart.valueOf(part.name))
        }
    }
}