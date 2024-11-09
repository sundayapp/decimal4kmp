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
import org.decimal4j.op.util.FloatAndDoubleUtil
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.decimal4j.api.RoundingMode
import kotlin.math.abs
import kotlin.random.Random

/**
 * Tests [DecimalArithmetic.fromFloat] and [DecimalArithmetic.toFloat]
 * and checks that the result is the same as the original input (if appropriate rounding modes
 * are used and some tolerance is allowed for 2 possible truncations).
 */
@RunWith(Parameterized::class)
class FloatFromToTest(s: ScaleMetrics?, roundingMode: RoundingMode, private val arithmetic: DecimalArithmetic) {
    private val backRounding: RoundingMode = FloatAndDoubleUtil.getOppositeRoundingMode(roundingMode)

    @Test
    fun testSpecialFloats() {
        var index = 0
        for (f in FloatAndDoubleUtil.specialFloatOperands(arithmetic.scaleMetrics)) {
            runTest("special[$index]", f)
            index++
        }
    }

    @Test
    fun testRandomFloats() {
        val n = TestSettings.getRandomTestCount()
        for (i in 0 until n) {
            runTest("random[$i]", FloatAndDoubleUtil.randomFloatOperand(RND))
        }
    }

    private fun runTest(name: String, f: Float) {
        try {
            val uDecimal = arithmetic.fromFloat(f)
            val result = arithmetic.scaleMetrics.getArithmetic(backRounding).toFloat(uDecimal)
            val tolerance = 2.0f * max(Math.ulp(result), Math.ulp(f), 1.0f / arithmetic.scaleMetrics.getScaleFactor())
            Assert.assertEquals(
                name + ": result after 2 conversions should be same as input with tolerance=<" + tolerance + ">, delta=<" + abs(
                    (f - result).toDouble()
                ) + ">", f, result, tolerance
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

        private fun max(val1: Float, val2: Float, val3: Float): Float {
            return kotlin.math.max(kotlin.math.max(val1.toDouble(), val2.toDouble()), val3.toDouble()).toFloat()
        }
    }
}
