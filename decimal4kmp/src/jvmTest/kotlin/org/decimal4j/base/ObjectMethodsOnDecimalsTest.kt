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
package org.decimal4j.base

import junitparams.JUnitParamsRunner
import junitparams.Parameters
import junitparams.naming.TestCaseName
import org.decimal4j.api.Decimal
import org.decimal4j.base.DecimalArgumentProviders.BinaryDecimalArgumentProvider
import org.decimal4j.base.DecimalArgumentProviders.TernaryDecimalArgumentProvider
import org.decimal4j.base.DecimalArgumentProviders.UnaryDecimalArgumentProvider
import org.decimal4j.base.DecimalArgumentProviders.newDecimal
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Unit test for proving that [Decimal.equals] obeys the contract
 * of {code}equals{code}.
 *
 *
 * Note: equals is implemented in [AbstractDecimal] class which has
 * mutable and immutable extensions.
 *
 */
@RunWith(JUnitParamsRunner::class)
class ObjectMethodsOnDecimalsTest {
    // Test cases for equals
    /**
     * Checks the reflexivity requirement of [Object.equals] in
     * case of different [Decimal] implementations.
     *
     * @param first the first argument of the equality comparison with itself
     */
    @Test
    @Parameters(source = UnaryDecimalArgumentProvider::class)
    @TestCaseName("reflexivity check: {0}")
    fun equalsIsReflexive(first: Decimal<ScaleMetrics>) {
        Assert.assertEquals(first, first)
    }

    /**
     * Checks the symmetry requirement of [Object.equals] in case
     * of different [Decimal] implementations.
     *
     * @param first the first argument of the equality comparison
     * @param second the second argument of the equality comparison
     */
    @Test
    @Parameters(source = BinaryDecimalArgumentProvider::class)
    @TestCaseName("symmetry check: [{0}, {1}]")
    fun equalsIsSymmetric(
        first: Decimal<ScaleMetrics>,
        second: Decimal<ScaleMetrics>
    ) {
        // given
        Assert.assertEquals(first, second)

        // then
        Assert.assertEquals(second, first)
    }

    /**
     * Checks the transitivity requirement of [Object.equals] in
     * case of different [Decimal] implementations.
     *
     * @param first the first argument of the equality comparison
     * @param second the second argument of the equality comparison
     * @param third the third argument of the equality comparison
     */
    @Test
    @Parameters(source = TernaryDecimalArgumentProvider::class)
    @TestCaseName("transitivity check: [{0}, {1}, {2}]")
    fun equalsIsTransitive(
        first: Decimal<ScaleMetrics>,
        second: Decimal<ScaleMetrics>,
        third: Decimal<ScaleMetrics>
    ) {
        // given
        Assert.assertEquals(first, second)
        Assert.assertEquals(second, third)

        // then
        Assert.assertEquals(first, third)
    }

    @Test
    @Parameters(source = UnaryDecimalArgumentProvider::class)
    @TestCaseName("equals is null-safe: {0}")
    fun equalsIsNullSafe(first: Decimal<ScaleMetrics>) {
        // given
        Assert.assertNotNull(first)

        // then
        Assert.assertNotEquals(first, null)
    }

    @Test
    @Parameters(source = UnaryDecimalArgumentProvider::class)
    @TestCaseName("non-decimal test: {0}")
    fun isNotEqualToNonDecimalObjects(first: Decimal<ScaleMetrics>) {
        // given
        val nonDecimalObj = Any()

        // then
        Assert.assertNotEquals(first, nonDecimalObj)
    }

    @Test
    fun isNotEqualToDecimalHavingDifferentScale () {
            // given
            val unscaled: Long = 123

            val first = newDecimal(
                Scales.getScaleMetrics(0), unscaled
            )
            val second = newDecimal(
                Scales.getScaleMetrics(5), unscaled
            )

            // then
            Assert.assertNotEquals(first, second)
        }

    @Test
    fun isNotEqualToDecimalHavingDifferentValue() {
            // given
            val unscaled: Long = 123
            val scale = 2

            val first = newDecimal(
                Scales.getScaleMetrics(scale), unscaled
            )
            val second = newDecimal(
                Scales.getScaleMetrics(scale), 2 * unscaled
            )

            // then
            Assert.assertNotEquals(first, second)
        }

    // Test cases for hashCode
    @Test
    @Parameters(source = BinaryDecimalArgumentProvider::class)
    @TestCaseName("hashCode check: [{0}, {1}]")
    fun equalDecimalsHaveEqualHashCodes(
        first: Decimal<ScaleMetrics>,
        second: Decimal<ScaleMetrics>
    ) {
        // given
        Assert.assertEquals(first, second)

        // when
        val hashCodeFirst = first.hashCode()
        val hashCodeSecond = second.hashCode()

        // then
        Assert.assertEquals(hashCodeFirst.toLong(), hashCodeSecond.toLong())
    }

    // Test cases for toString
    // NOTE: Decimal#toString does not have a fixed format to parse/check, so
    // this case just checks, that it is overridden, thus equal decimals must
    // have the same string representation
    @Test
    @Parameters(source = BinaryDecimalArgumentProvider::class)
    @TestCaseName("toString() check: [{0}, {1}]")
    fun equalDecimalsHaveSameStringRepresentation(
        first: Decimal<ScaleMetrics>,
        second: Decimal<ScaleMetrics>
    ) {
        // given
        Assert.assertEquals(first, second)

        // when
        val stringFirst = first.toString()
        val stringSecond = second.toString()

        // then
        Assert.assertEquals(stringFirst, stringSecond)
    }
}
