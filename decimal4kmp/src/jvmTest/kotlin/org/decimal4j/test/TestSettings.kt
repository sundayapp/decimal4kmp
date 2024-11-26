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

import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.TruncationPolicy
import org.decimal4j.truncate.RoundingMode

/**
 * Test settings for unit tests, defines the scales to test and the size of the
 * test case sets. Can be controlled via system properties during the build.
 */
object TestSettings {
    const val SYSTEM_PROPERTY_TEST_VARIANT: String = "testVariant"
    const val SYSTEM_PROPERTY_TEST_SCALES: String = "testScales"
    const val SYSTEM_PROPERTY_TEST_CASES: String = "testCases"

    val TEST_SCALES: TestScales = testScales
    val TEST_POLICIES: TestTruncationPolicies = testTruncationPolicies
    @JvmField
	val TEST_CASES: TestCases = testCases

    @JvmField
	val SCALES: List<ScaleMetrics> = TEST_SCALES.scales
    @JvmField
	val POLICIES: Collection<TruncationPolicy> = TEST_POLICIES.policies
    @JvmField
	val CHECKED_POLICIES: Collection<TruncationPolicy> = TEST_POLICIES.checkedPolicies
    @JvmField
	val UNCHECKED_ROUNDING_MODES: Set<RoundingMode> = TEST_POLICIES.uncheckedRoundingModes

    @JvmStatic
	fun getRandomTestCount(): Int = when (TEST_CASES) {
            TestCases.ALL -> 10000
            TestCases.LARGE -> 10000
            TestCases.STANDARD -> 1000
            TestCases.SMALL -> 1000
            TestCases.TINY -> 100
            else -> throw RuntimeException("unsupported: " + TEST_CASES)
        }

    private val testScales: TestScales
        get() {
            val testVariant =
                System.getProperty(SYSTEM_PROPERTY_TEST_VARIANT, TestScales.STANDARD.name)
            val testScales =
                System.getProperty(SYSTEM_PROPERTY_TEST_SCALES, testVariant)
            try {
                return TestScales.valueOf(testScales)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException(
                    "illegal system property for test scales, must be one of " + TestScales.entries.toTypedArray()
                        .contentToString() + " but was <" + testScales + ">"
                )
            }
        }

    private val testCases: TestCases
        get() {
            val testVariant =
                System.getProperty(SYSTEM_PROPERTY_TEST_VARIANT, TestCases.STANDARD.name)
            val testCases =
                System.getProperty(SYSTEM_PROPERTY_TEST_CASES, testVariant)
            try {
                return TestCases.valueOf(testCases)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException(
                    "illegal system property for test cases, must be one of " + TestCases.entries.toTypedArray()
                        .contentToString() + " but was <" + testCases + ">"
                )
            }
        }

    private val testTruncationPolicies: TestTruncationPolicies
        get() {
            val testVariant =
                System.getProperty(SYSTEM_PROPERTY_TEST_VARIANT, TestCases.STANDARD.name)
            try {
                return TestTruncationPolicies.valueOf(testVariant)
            } catch (e: IllegalArgumentException) {
                throw IllegalArgumentException(
                    "illegal system property for test truncation policies, must be one of " + TestTruncationPolicies.entries.toTypedArray()
                        .contentToString() + " but was <" + testVariant + ">"
                )
            }
        }
}
