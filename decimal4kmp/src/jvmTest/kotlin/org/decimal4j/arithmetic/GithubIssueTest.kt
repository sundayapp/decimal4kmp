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

import org.decimal4j.scale.Scales
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal
import org.decimal4j.truncate.RoundingMode


/**
 * Unit test addressing issues raised on github.
 */
class GithubIssueTest {
    /**
     * Issue #17: Difference in RoundingMode.HALF_UP rounded value for 0.663125 at scale 5
     *
     * Part I -- reproduction of provided example
     *
     * See [Github Issue 17](https://github.com/tools4j/decimal4j/issues/17)
     */
    @Test
    fun roundDoubleHalfUp_exact() {
        //given
        val input = 0.663125
        val exactString = BigDecimal(input).toPlainString()
        val arith = Scales.getScaleMetrics(5).getArithmetic(RoundingMode.HALF_UP)
        val expected = BigDecimal(input).setScale(5, RoundingMode.HALF_UP?.toJavaRoundingMode()).toDouble()
        val unexpected = BigDecimal.valueOf(input).setScale(5, RoundingMode.HALF_UP?.toJavaRoundingMode()).toDouble()

        //when
        val actual = arith.toDouble(arith.fromDouble(input))

        //then
        Assert.assertEquals("round($exactString, 5)", expected, actual, ZERO_TOLERANCE)
        Assert.assertNotEquals("round($exactString, 5)", unexpected, actual, ZERO_TOLERANCE)
    }

    /**
     * Issue #17: Difference in RoundingMode.HALF_UP rounded value for 0.663125 at scale 5
     *
     * Part II -- proposed alternative ways to handle rounding of a double
     *
     * See [Github Issue 17](https://github.com/tools4j/decimal4j/issues/17)
     */
    @Test
    fun roundDoubleHalfUp_string() {
        //given
        val input = 0.663125
        val expected = 0.66313
        val str = StringBuilder().append(input)
        val arith8 = Scales.getScaleMetrics(8).getRoundingHalfEvenArithmetic()
        val arith5 = Scales.getScaleMetrics(5).getArithmetic(RoundingMode.HALF_UP)

        //when
        val actual0 = BigDecimal.valueOf(input).setScale(5, RoundingMode.HALF_UP?.toJavaRoundingMode()).toDouble()
        val actual1 = arith5.toDouble(arith5.fromUnscaled(arith8.fromDouble(input), arith8.scale))
        val actual2 = arith5.toDouble(arith5.parse(input.toString()))
        val actual3 = arith5.toDouble(arith5.parse(str, 0, str.length))

        //then
        Assert.assertEquals("round($input, 5) via BigDecimal.valueOf", expected, actual0, ZERO_TOLERANCE)
        Assert.assertEquals("round($input, 5) via 2 step rounding", expected, actual1, ZERO_TOLERANCE)
        Assert.assertEquals("round($input, 5) via String", expected, actual2, ZERO_TOLERANCE)
        Assert.assertEquals("round($input, 5) via StringBuilder", expected, actual3, ZERO_TOLERANCE)
    }

    companion object {
        private const val ZERO_TOLERANCE = 0.0
    }
}
