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
package org.decimal4j.util

import org.decimal4j.op.util.FloatAndDoubleUtil.randomDoubleOperand
import org.decimal4j.op.util.FloatAndDoubleUtil.specialDoubleOperands
import org.decimal4j.scale.Scales
import org.decimal4j.test.ArithmeticResult
import org.decimal4j.test.TestSettings
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import org.decimal4j.api.RoundingMode
import org.decimal4j.api.toJavaRoundingMode
import kotlin.math.abs
import kotlin.random.Random

/**
 * Unit test for [DoubleRounder]
 */
@RunWith(Parameterized::class)
class DoubleRounderTest(private val precision: Int, private val roundingMode: RoundingMode) {
    @Test
    fun testSpecialDoubles() {
        var index = 0
        for (d in specialDoubleOperands(Scales.getScaleMetrics(precision))) {
            runTest("special[$index]", d)
            index++
        }
    }

    @Test
    fun testRandomDoubles() {
        val n = TestSettings.getRandomTestCount()
        for (i in 0 until n) {
            runTest("random[$i]", randomDoubleOperand(RND))
        }
    }

    private fun expectedResult(d: Double): Double {
        if (!isFinite(d)) {
            return d
        }
        //we need exact representation of the double, except when we check UNNECESSARY rounding mode
        val bd = if (roundingMode == RoundingMode.UNNECESSARY) BigDecimal.valueOf(d) else BigDecimal(d)
        return bd.setScale(precision, roundingMode.toJavaRoundingMode()).toDouble()
    }

    private fun actualResult(d: Double): Double {
        if (RND.nextBoolean()) {
            //static methods
            if ((roundingMode == RoundingMode.HALF_UP) and RND.nextBoolean()) {
                return DoubleRounder.round(d, precision)
            }
            return DoubleRounder.round(d, precision, roundingMode)
        }
        //create rounder instance
        val rounder = DoubleRounder(precision)
        if ((roundingMode == RoundingMode.HALF_UP) and RND.nextBoolean()) {
            return rounder.round(d)
        }
        return rounder.round(d, roundingMode)
    }

    private fun runTest(name: String, d: Double) {
        val messagePrefix = javaClass.simpleName + name + ": round(" + d + ")"


        // expected
        var expected: ArithmeticResult<Double>
        try {
            val exp = expectedResult(d)
            expected = ArithmeticResult.forResult(exp.toString(), exp)
        } catch (e: ArithmeticException) {
            expected = ArithmeticResult.forException(e)
        } catch (e: NumberFormatException) {
            expected = ArithmeticResult.forException(IllegalArgumentException(e))
        } catch (e: IllegalArgumentException) {
            expected = ArithmeticResult.forException(e)
        }

        // actual
        var actual: ArithmeticResult<Double>
        try {
            val act = actualResult(d)
            actual = ArithmeticResult.forResult(act.toString(), act)
        } catch (e: ArithmeticException) {
            actual = ArithmeticResult.forException(e)
        } catch (e: IllegalArgumentException) {
            actual = ArithmeticResult.forException(e)
        }

        // assert
        actual.assertEquivalentTo(expected, messagePrefix)
    }

    companion object {
        private val RND = Random.Default

        @JvmStatic
        @Parameterized.Parameters(name = "{index}: precision={0}, {1}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (precision in TestSettings.SCALES) {
                for (mode in TestSettings.UNCHECKED_ROUNDING_MODES) {
                    data.add(arrayOf(precision.getScale(), mode))
                }
            }
            return data
        }

        private fun isFinite(d: Double): Boolean {
            return abs(d) <= Double.MAX_VALUE
        }
    }
}
