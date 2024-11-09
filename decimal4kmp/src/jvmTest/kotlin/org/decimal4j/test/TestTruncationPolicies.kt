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
package org.decimal4j.test

import org.decimal4j.truncate.CheckedRounding
import org.decimal4j.truncate.TruncationPolicy
import org.decimal4j.truncate.UncheckedRounding
import org.decimal4j.api.RoundingMode

enum class TestTruncationPolicies(val policies: Collection<TruncationPolicy>) {
    TINY(
        listOf(
            UncheckedRounding.DOWN, UncheckedRounding.HALF_UP,
            CheckedRounding.DOWN, CheckedRounding.UNNECESSARY
        )
    ),

    SMALL(
        listOf(
            UncheckedRounding.DOWN, UncheckedRounding.HALF_UP,
            CheckedRounding.DOWN, CheckedRounding.HALF_UP, CheckedRounding.UNNECESSARY
        )
    ),

    STANDARD(
        listOf(
            UncheckedRounding.DOWN, UncheckedRounding.HALF_UP, UncheckedRounding.HALF_EVEN,
            CheckedRounding.DOWN, CheckedRounding.HALF_UP, CheckedRounding.UNNECESSARY
        )
    ),

    LARGE(
        listOf(
            UncheckedRounding.UP,
            UncheckedRounding.DOWN,
            UncheckedRounding.HALF_UP,
            UncheckedRounding.HALF_EVEN,
            UncheckedRounding.UNNECESSARY,
            CheckedRounding.UP,
            CheckedRounding.DOWN,
            CheckedRounding.HALF_UP,
            CheckedRounding.HALF_EVEN,
            CheckedRounding.UNNECESSARY
        )
    ),

    ALL(TruncationPolicy.VALUES);


    val checkedPolicies: Collection<TruncationPolicy>
        get() {
            val policies: MutableList<TruncationPolicy> = ArrayList()
            for (policy in this.policies) {
                if (policy.getOverflowMode().isChecked) {
                    policies.add(policy)
                }
            }
            return policies
        }
    val uncheckedRoundingModes: Set<RoundingMode>
        get() {
            val rounding: MutableSet<RoundingMode> = mutableSetOf()
            for (policy in policies) {
                if (!policy.getOverflowMode().isChecked) {
                    rounding.add(policy.getRoundingMode())
                }
            }
            return rounding
        }
}
