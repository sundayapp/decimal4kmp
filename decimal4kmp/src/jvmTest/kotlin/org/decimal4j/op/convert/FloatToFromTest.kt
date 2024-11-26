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
package org.decimal4j.op.convert

import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.op.util.FloatAndDoubleUtil.getOppositeRoundingMode
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.decimal4j.test.TestSettings.getRandomTestCount
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.decimal4j.truncate.RoundingMode
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.random.Random

/**
 * Tests [DecimalArithmetic.toFloat] and [DecimalArithmetic.fromFloat]
 * and checks that the result is the same as the original input (if appropriate rounding modes
 * are used and some tolerance is allowed for 2 possible truncations).
 */
@RunWith(Parameterized::class)
class FloatToFromTest(s: ScaleMetrics?, roundingMode: RoundingMode, private val arithmetic: DecimalArithmetic) {
    private val backRounding = getOppositeRoundingMode(roundingMode)

    @Test
    fun testSpecialFloats() {
        var index = 0
        for (value in TestSettings.TEST_CASES.getSpecialValuesFor(arithmetic.scaleMetrics)) {
            runTest("special[$index]", value)
            index++
        }
    }

    @Test
    fun testRandomFloats() {
        val n = getRandomTestCount()
        for (i in 0 until n) {
            val value = RND.nextLong()
            runTest("random[$i]", value)
        }
    }

    private fun runTest(name: String, value: Long) {
        try {
            val flt = arithmetic.toFloat(value)
            val result = arithmetic.scaleMetrics.getArithmetic(backRounding).fromFloat(flt)
            val tolerance = (ceil((Math.ulp(flt).toDouble()) * arithmetic.scaleMetrics.getScaleFactor())).toLong()
            Assert.assertTrue(
                name + ": result after 2 conversions should be same as input: input=<" + value + ">, output=<" + result + ">, tolerance=<" + tolerance + ">, delta=<" + abs(
                    (value - result).toDouble()
                ) + ">", abs((value - result).toDouble()) <= tolerance
            )
        } catch (e: IllegalArgumentException) {
            //ignore, must be out of range, tested elsewhere
        }
    }

    companion object {
        private val RND = Random.Default

        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}, {1}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                for (mode in TestSettings.UNCHECKED_ROUNDING_MODES) {
                    if (mode != RoundingMode.UNNECESSARY) {
                        val arith = s.getArithmetic(mode)
                        data.add(arrayOf(s, mode, arith))
                    }
                }
            }
            return data
        }
    }
}
