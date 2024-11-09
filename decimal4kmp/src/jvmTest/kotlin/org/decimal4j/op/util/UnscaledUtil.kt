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
package org.decimal4j.op.util

import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales
import org.decimal4j.test.TestCases
import org.decimal4j.test.TestSettings
import kotlin.math.max
import kotlin.math.min

object UnscaledUtil {
    @JvmStatic
	fun getSpecialUnscaledOperands(scale: Int): LongArray {
        var valueScale = scale
        valueScale = max(Scales.MIN_SCALE.toDouble(), valueScale.toDouble()).toInt()
        valueScale = min(Scales.MAX_SCALE.toDouble(), valueScale.toDouble()).toInt()
        return TestSettings.TEST_CASES.getSpecialValuesFor(Scales.getScaleMetrics(valueScale))
    }

    @JvmStatic
	fun getScales(scaleMetrics: ScaleMetrics): Set<Int> {
        val scale = scaleMetrics.getScale()
        val vals: MutableSet<Int> = sortedSetOf()
        when (TestSettings.TEST_CASES) {
            TestCases.TINY -> vals.addAll(listOf(-1, 0, scale - 1, scale, scale + 1, 18, 19))
            TestCases.SMALL -> vals.addAll(listOf(-1, 0, 1, scale - 1, scale, scale + 1, 17, 18, 19))
            TestCases.STANDARD -> vals.addAll(listOf(-10, -1, 0, 1, scale - 1, scale, scale + 1, 17, 18, 19, 30))
            TestCases.LARGE -> vals.addAll(listOf(-10, -1, 0, 1, scale - 1, scale, scale + 1, 17, 18, 19, 30))
            TestCases.ALL -> vals.addAll(
                listOf(
                    -100, -20, -10, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16,
                    17, 18, 19, 20, 25, 30, 100
                )
            )

            else -> throw RuntimeException("illegal test cases: " + TestSettings.TEST_CASES)
        }
        return vals
    }
}
