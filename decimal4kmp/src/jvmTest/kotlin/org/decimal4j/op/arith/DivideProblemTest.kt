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
package org.decimal4j.op.arith

import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.scale.*
import org.decimal4j.truncate.CheckedRounding
import org.decimal4j.truncate.TruncationPolicy
import org.decimal4j.truncate.UncheckedRounding
import org.junit.Test
import org.junit.runners.Parameterized
import org.decimal4j.api.RoundingMode

class DivideProblemTest(
    scaleMetrics: ScaleMetrics?,
    truncationPolicy: TruncationPolicy?,
    arithmetic: DecimalArithmetic
) :
    DivideTest(scaleMetrics, truncationPolicy, arithmetic) {
    @Test
    fun runProblemTest0() {
        if (getArithmeticScale() == 6 && !isUnchecked) {
            val dOpA = newDecimal(Scale6f.INSTANCE, 345)
            val dOpB = newDecimal(Scale6f.INSTANCE, 0)
            runTest(Scale6f.INSTANCE, "problem", dOpA, dOpB)
        }
    }

    @Test
    fun runProblemTest1() {
        if (getArithmeticScale() == 0 && isUnchecked && roundingMode == RoundingMode.HALF_EVEN) {
            val dOpA = newDecimal(Scale0f.INSTANCE, Long.MIN_VALUE + 1)
            val dOpB = newDecimal(Scale0f.INSTANCE, Long.MIN_VALUE)
            runTest(Scale0f.INSTANCE, "problem", dOpA, dOpB)
        }
    }

    @Test
    fun runProblemTest2() {
        if (getArithmeticScale() == 0 && isUnchecked && roundingMode == RoundingMode.HALF_EVEN) {
            val dOpA = newDecimal(Scale0f.INSTANCE, Long.MIN_VALUE)
            val dOpB = newDecimal(Scale0f.INSTANCE, -Scale18f.INSTANCE.getScaleFactor())
            runTest(Scale0f.INSTANCE, "problem", dOpA, dOpB)
        }
    }

    @Test
    fun runProblemTest3() {
        if (getArithmeticScale() == 6 && isUnchecked && roundingMode == RoundingMode.HALF_EVEN) {
            val dOpA = newDecimal(Scale6f.INSTANCE, Long.MIN_VALUE)
            val dOpB = newDecimal(Scale6f.INSTANCE, -10000000000000000L)
            runTest(Scale6f.INSTANCE, "problem", dOpA, dOpB)
        }
    }

    @Test
    fun runProblemTest4() {
        if (getArithmeticScale() == 6 && isUnchecked && roundingMode == RoundingMode.HALF_EVEN) {
            val dOpA = newDecimal(Scale6f.INSTANCE, Long.MIN_VALUE)
            val dOpB = newDecimal(Scale6f.INSTANCE, -4611686018427387905L)
            runTest(Scale6f.INSTANCE, "problem", dOpA, dOpB)
        }
    }

    @Test
    fun runProblemTest5() {
        if (getArithmeticScale() == 17 && isUnchecked) {
            val dOpA = newDecimal(Scale17f.INSTANCE, Scale17f.INSTANCE.getScaleFactor())
            val dOpB = newDecimal(Scale17f.INSTANCE, -92233720368547L)
            runTest(Scale17f.INSTANCE, "problem", dOpA, dOpB)
        }
    }

    @Test
    fun runProblemTest6() {
        if (getArithmeticScale() == 17 && isUnchecked) {
            val dOpA = newDecimal(Scale17f.INSTANCE, Scale17f.INSTANCE.getScaleFactor())
            val dOpB = newDecimal(Scale17f.INSTANCE, Int.MIN_VALUE * 1000L)
            runTest(Scale17f.INSTANCE, "problem", dOpA, dOpB)
        }
    }

    @Test
    fun runProblemTest7() {
        if (getArithmeticScale() == 6 && isUnchecked && roundingMode == RoundingMode.UNNECESSARY) {
            val dOpA = newDecimal(Scale6f.INSTANCE, 99999999000000L)
            val dOpB = newDecimal(Scale6f.INSTANCE, 5)
            runTest(Scale6f.INSTANCE, "problem", dOpA, dOpB)
        }
    }

    override fun runRandomTest() {
        // no op
    }

    override fun runSpecialValueTest() {
        // no op
    }

    companion object {
		@JvmStatic @Parameterized.Parameters(name = "{index}: scale={0}, rounding={1}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()

            var s: ScaleMetrics = Scale6f.INSTANCE
            var tp: TruncationPolicy = CheckedRounding.DOWN
            data.add(arrayOf(s, tp, s.getArithmetic(tp)))

            s = Scale0f.INSTANCE
            tp = UncheckedRounding.HALF_EVEN
            data.add(arrayOf(s, tp, s.getArithmetic(tp)))

            s = Scale6f.INSTANCE
            tp = UncheckedRounding.HALF_EVEN
            data.add(arrayOf(s, tp, s.getArithmetic(tp)))

            s = Scale6f.INSTANCE
            tp = UncheckedRounding.UNNECESSARY
            data.add(arrayOf(s, tp, s.getArithmetic(tp)))

            s = Scale17f.INSTANCE
            tp = UncheckedRounding.DOWN
            data.add(arrayOf(s, tp, s.getArithmetic(tp)))

            return data
        }
    }
}
