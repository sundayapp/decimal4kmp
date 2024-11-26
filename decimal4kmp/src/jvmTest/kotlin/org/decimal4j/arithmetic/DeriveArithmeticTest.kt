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

import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales
import org.decimal4j.test.TestSettings
import org.decimal4j.truncate.CheckedRounding
import org.decimal4j.truncate.OverflowMode
import org.decimal4j.truncate.TruncationPolicy
import org.decimal4j.truncate.UncheckedRounding
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.decimal4j.truncate.RoundingMode

/**
 * Unit test for [DecimalArithmetic.deriveArithmetic] and all the other
 * derive methods of arithmetic.
 */
@RunWith(Parameterized::class)
class DeriveArithmeticTest(
    scaleMetrics: ScaleMetrics?,
    truncationPolicy: TruncationPolicy?,
    private val arithmetic: DecimalArithmetic
) {

    @Test
    fun shouldDeriveOtherScale() {
        for (scale in Scales.MIN_SCALE..Scales.MAX_SCALE) {
            val expected = Scales.getScaleMetrics(scale).getArithmetic(arithmetic.truncationPolicy)
            Assert.assertSame(
                "unexpected arithmetic instance for scale $scale",
                expected,
                arithmetic.deriveArithmetic(scale)
            )
        }
    }

    @Test
    fun shouldDeriveOtherRoundingMode() {
        for (roundingMode in RoundingMode.entries) {
            val tp: TruncationPolicy =
                if (arithmetic.overflowMode.isChecked) CheckedRounding.valueOf(roundingMode) else UncheckedRounding.valueOf(
                    roundingMode
                )
            val expected = arithmetic.scaleMetrics.getArithmetic(tp)
            Assert.assertSame(
                "unexpected arithmetic instance for rounding mode $roundingMode",
                expected,
                arithmetic.deriveArithmetic(roundingMode)
            )
        }
    }

    @Test
    fun shouldDeriveOtherOverflowMode() {
        for (overflowMode in OverflowMode.entries) {
            val tp: TruncationPolicy =
                if (overflowMode.isChecked) CheckedRounding.valueOf(arithmetic.roundingMode) else UncheckedRounding.valueOf(
                    arithmetic.roundingMode
                )
            val expected = arithmetic.scaleMetrics.getArithmetic(tp)
            Assert.assertSame(
                "unexpected arithmetic instance for overflow mode $overflowMode",
                expected,
                arithmetic.deriveArithmetic(overflowMode)
            )
        }
    }

    @Test
    fun shouldDeriveOtherRoundingModeAndOverflowMode() {
        for (roundingMode in RoundingMode.entries) {
            for (overflowMode in OverflowMode.entries) {
                val tp: TruncationPolicy =
                    if (overflowMode.isChecked) CheckedRounding.valueOf(roundingMode) else UncheckedRounding.valueOf(
                        roundingMode
                    )
                val expected = arithmetic.scaleMetrics.getArithmetic(tp)
                Assert.assertSame(
                    "unexpected arithmetic instance for rounding/overflow mode $roundingMode/$overflowMode",
                    expected,
                    arithmetic.deriveArithmetic(roundingMode, overflowMode)
                )
            }
        }
    }

    @Test
    fun shouldDeriveOtherTruncationPolicy() {
        for (truncationPolicy in TruncationPolicy.VALUES) {
            val expected = arithmetic.scaleMetrics.getArithmetic(truncationPolicy)
            Assert.assertSame(
                "unexpected arithmetic instance for truncation policy $truncationPolicy",
                expected,
                arithmetic.deriveArithmetic(truncationPolicy)
            )
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}, {1}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                for (tp in TestSettings.POLICIES) {
                    val arith = s.getArithmetic(tp)
                    data.add(arrayOf(s, tp, arith))
                }
            }
            return data
        }
    }
}
