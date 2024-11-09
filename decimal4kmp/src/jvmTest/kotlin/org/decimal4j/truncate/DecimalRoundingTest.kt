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
import org.decimal4j.api.RoundingMode

/**
 * Unit test for [DecimalRounding].
 */
class DecimalRoundingTest {
    @Test
    fun shouldNotRoundWithZeroPart() {
        for (rounding in DecimalRounding.VALUES) {
            for (sgn in intArrayOf(-1, 0, 1)) {
                for (i in -10..9) {
                    Assert.assertEquals(
                        "rounding increment should be zero",
                        0,
                        rounding.calculateRoundingIncrement(sgn, i.toLong(), TruncatedPart.ZERO).toLong()
                    )
                }
                Assert.assertEquals(
                    "rounding increment should be zero",
                    0,
                    rounding.calculateRoundingIncrement(sgn, Long.MIN_VALUE, TruncatedPart.ZERO).toLong()
                )
                Assert.assertEquals(
                    "rounding increment should be zero",
                    0,
                    rounding.calculateRoundingIncrement(sgn, Long.MAX_VALUE, TruncatedPart.ZERO).toLong()
                )
            }
        }
    }

    @Test
    fun shouldNotRoundWithUndefinedSign() {
        for (rounding in DecimalRounding.VALUES) {
            for (i in -10..9) {
                Assert.assertEquals(
                    "rounding increment should be zero",
                    0,
                    rounding.calculateRoundingIncrement(0, i.toLong(), TruncatedPart.ZERO).toLong()
                )
            }
            Assert.assertEquals(
                "rounding increment should be zero",
                0,
                rounding.calculateRoundingIncrement(0, Long.MIN_VALUE, TruncatedPart.ZERO).toLong()
            )
            Assert.assertEquals(
                "rounding increment should be zero",
                0,
                rounding.calculateRoundingIncrement(0, Long.MAX_VALUE, TruncatedPart.ZERO).toLong()
            )
        }
    }

    @Test
    fun shouldRoundUp() {
        //pos
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.UP.calculateRoundingIncrement(1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.UP.calculateRoundingIncrement(1, 0, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.UP.calculateRoundingIncrement(1, 0, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.UP.calculateRoundingIncrement(1, 1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.UP.calculateRoundingIncrement(1, 1, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.UP.calculateRoundingIncrement(1, 1, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        //neg
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.UP.calculateRoundingIncrement(-1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.UP.calculateRoundingIncrement(-1, 0, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.UP.calculateRoundingIncrement(-1, 0, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.UP.calculateRoundingIncrement(-1, -1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.UP.calculateRoundingIncrement(-1, -1, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.UP.calculateRoundingIncrement(-1, -1, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
    }

    @Test
    fun shouldRoundDown() {
        //pos
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.DOWN.calculateRoundingIncrement(1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.DOWN.calculateRoundingIncrement(1, 0, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.DOWN.calculateRoundingIncrement(1, 0, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.DOWN.calculateRoundingIncrement(1, 1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.DOWN.calculateRoundingIncrement(1, 1, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.DOWN.calculateRoundingIncrement(1, 1, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        //neg
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.DOWN.calculateRoundingIncrement(-1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.DOWN.calculateRoundingIncrement(-1, 0, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.DOWN.calculateRoundingIncrement(-1, 0, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.DOWN.calculateRoundingIncrement(-1, -1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.DOWN.calculateRoundingIncrement(-1, -1, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.DOWN.calculateRoundingIncrement(-1, -1, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
    }

    @Test
    fun shouldRoundCeiling() {
        //pos
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.CEILING.calculateRoundingIncrement(1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.CEILING.calculateRoundingIncrement(1, 0, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.CEILING.calculateRoundingIncrement(1, 0, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.CEILING.calculateRoundingIncrement(1, 1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.CEILING.calculateRoundingIncrement(1, 1, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.CEILING.calculateRoundingIncrement(1, 1, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        //neg
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.CEILING.calculateRoundingIncrement(-1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO)
                .toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.CEILING.calculateRoundingIncrement(-1, 0, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.CEILING.calculateRoundingIncrement(-1, 0, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.CEILING.calculateRoundingIncrement(-1, -1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO)
                .toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.CEILING.calculateRoundingIncrement(-1, -1, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.CEILING.calculateRoundingIncrement(-1, -1, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
    }

    @Test
    fun shouldRoundFloor() {
        //pos
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.FLOOR.calculateRoundingIncrement(1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.FLOOR.calculateRoundingIncrement(1, 0, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.FLOOR.calculateRoundingIncrement(1, 0, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.FLOOR.calculateRoundingIncrement(1, 1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.FLOOR.calculateRoundingIncrement(1, 1, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.FLOOR.calculateRoundingIncrement(1, 1, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        //neg
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.FLOOR.calculateRoundingIncrement(-1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.FLOOR.calculateRoundingIncrement(-1, 0, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.FLOOR.calculateRoundingIncrement(-1, 0, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.FLOOR.calculateRoundingIncrement(-1, -1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.FLOOR.calculateRoundingIncrement(-1, -1, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.FLOOR.calculateRoundingIncrement(-1, -1, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
    }

    @Test
    fun shouldRoundHalfUp() {
        //pos
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_UP.calculateRoundingIncrement(1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.HALF_UP.calculateRoundingIncrement(1, 0, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.HALF_UP.calculateRoundingIncrement(1, 0, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_UP.calculateRoundingIncrement(1, 1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.HALF_UP.calculateRoundingIncrement(1, 1, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.HALF_UP.calculateRoundingIncrement(1, 1, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        //neg
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_UP.calculateRoundingIncrement(-1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO)
                .toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.HALF_UP.calculateRoundingIncrement(-1, 0, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.HALF_UP.calculateRoundingIncrement(-1, 0, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_UP.calculateRoundingIncrement(-1, -1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO)
                .toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.HALF_UP.calculateRoundingIncrement(-1, -1, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.HALF_UP.calculateRoundingIncrement(-1, -1, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
    }

    @Test
    fun shouldRoundHalfDown() {
        //pos
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_DOWN.calculateRoundingIncrement(1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO)
                .toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_DOWN.calculateRoundingIncrement(1, 0, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.HALF_DOWN.calculateRoundingIncrement(1, 0, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_DOWN.calculateRoundingIncrement(1, 1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO)
                .toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_DOWN.calculateRoundingIncrement(1, 1, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.HALF_DOWN.calculateRoundingIncrement(1, 1, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        //neg
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_DOWN.calculateRoundingIncrement(-1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO)
                .toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_DOWN.calculateRoundingIncrement(-1, 0, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.HALF_DOWN.calculateRoundingIncrement(-1, 0, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_DOWN.calculateRoundingIncrement(-1, -1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO)
                .toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_DOWN.calculateRoundingIncrement(-1, -1, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.HALF_DOWN.calculateRoundingIncrement(-1, -1, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
    }

    @Test
    fun shouldRoundHalfEven() {
        //pos
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_EVEN.calculateRoundingIncrement(1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO)
                .toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_EVEN.calculateRoundingIncrement(1, 0, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.HALF_EVEN.calculateRoundingIncrement(1, 0, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_EVEN.calculateRoundingIncrement(1, 1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO)
                .toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.HALF_EVEN.calculateRoundingIncrement(1, 1, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 1",
            1,
            DecimalRounding.HALF_EVEN.calculateRoundingIncrement(1, 1, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        //neg
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_EVEN.calculateRoundingIncrement(-1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO)
                .toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_EVEN.calculateRoundingIncrement(-1, 0, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.HALF_EVEN.calculateRoundingIncrement(-1, 0, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be 0",
            0,
            DecimalRounding.HALF_EVEN.calculateRoundingIncrement(-1, -1, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO)
                .toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.HALF_EVEN.calculateRoundingIncrement(-1, -1, TruncatedPart.EQUAL_TO_HALF).toLong()
        )
        Assert.assertEquals(
            "increment should be -1",
            -1,
            DecimalRounding.HALF_EVEN.calculateRoundingIncrement(-1, -1, TruncatedPart.GREATER_THAN_HALF).toLong()
        )
    }

    @Test(expected = ArithmeticException::class)
    fun shouldRoundUnneccessaryWithHalfButNotZero() {
        DecimalRounding.UNNECESSARY.calculateRoundingIncrement(1, 0, TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO)
    }

    @Test(expected = ArithmeticException::class)
    fun shouldRoundUnneccessaryWithEqualToHalf() {
        DecimalRounding.UNNECESSARY.calculateRoundingIncrement(-1, -1, TruncatedPart.EQUAL_TO_HALF)
    }

    @Test(expected = ArithmeticException::class)
    fun shouldRoundUnneccessaryWithGreaterThanHalf() {
        DecimalRounding.UNNECESSARY.calculateRoundingIncrement(-1, 0, TruncatedPart.GREATER_THAN_HALF)
    }

    @Test
    fun valuesListShouldBeSortedByOrdinal() {
        Assert.assertEquals(
            "VALUES size not as expected",
            DecimalRounding.entries.size.toLong(),
            DecimalRounding.VALUES.size.toLong()
        )
        var ordinal = 0
        for (decimalRounding in DecimalRounding.VALUES) {
            Assert.assertEquals("should have ordinal $ordinal", ordinal.toLong(), decimalRounding.ordinal.toLong())
            ordinal++
        }
    }

    @Test
    fun compareWithRoundingMode() {
        Assert.assertEquals(
            "RoundingMode and DecimalRounding should have same number of constants",
            RoundingMode.entries.size.toLong(),
            DecimalRounding.entries.size.toLong()
        )
        for (roundingMode in RoundingMode.entries) {
            //when
            val decimalRounding = DecimalRounding.valueOf(roundingMode)
            //then
            Assert.assertSame("should be same rounding mode", roundingMode, decimalRounding.getRoundingMode())
            Assert.assertSame("should have same name", roundingMode.name, decimalRounding.name)
        }
    }

    @Test
    fun testValueOf() {
        //test to achieve 100% coverage 
        for (rounding in DecimalRounding.entries) {
            Assert.assertSame("should be same instance", rounding, DecimalRounding.valueOf(rounding.name))
        }
    }
}
