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
 * Unit test for [TruncationPolicy].
 */
class TruncationPolicyTest {
    @Test
    fun testUncheckedPolicies() {
        for (policy in UncheckedRounding.VALUES) {
            //then
            Assert.assertSame("overflow mode should be UNCHECKED", OverflowMode.UNCHECKED, policy.getOverflowMode())


            //when
            val other = CheckedRounding.valueOf(policy.name).toUncheckedRounding()
            //then
            Assert.assertSame("should be same policy", policy, other)
        }
    }

    @Test
    fun testCheckedPolicies() {
        for (policy in CheckedRounding.VALUES) {
            //then
            Assert.assertSame("overflow mode should be CHECKED", OverflowMode.CHECKED, policy.getOverflowMode())

            //when
            val other = UncheckedRounding.valueOf(policy.name).toCheckedRounding()
            //then
            Assert.assertSame("should be same policy", policy, other)
        }
    }

    @Test
    fun testPoliciesByRoundingMode() {
        for (rounding in DecimalRounding.VALUES) {
            val roundingMode = rounding.getRoundingMode()
            //when
            val policy1: TruncationPolicy = UncheckedRounding.valueOf(roundingMode)
            //then
            Assert.assertSame("overflow mode should be UNCHECKED", OverflowMode.UNCHECKED, policy1.getOverflowMode())
            Assert.assertSame("rounding mode should be $roundingMode", roundingMode, policy1.getRoundingMode())
            //when
            val policy2: TruncationPolicy = CheckedRounding.valueOf(roundingMode)
            //then
            Assert.assertSame("overflow mode should be CHECKED", OverflowMode.CHECKED, policy2.getOverflowMode())
            Assert.assertSame("rounding mode should be $roundingMode", roundingMode, policy2.getRoundingMode())
        }
    }

    @Test
    fun defaultShouldBeUncheckedRoundingHalfUp() {
        Assert.assertSame(
            "overflow mode should be UNCHECKED",
            OverflowMode.UNCHECKED,
            TruncationPolicy.DEFAULT.getOverflowMode()
        )
        Assert.assertSame(
            "rounding mode should be HALF_UP",
            RoundingMode.HALF_UP,
            TruncationPolicy.DEFAULT.getRoundingMode()
        )
    }
}
