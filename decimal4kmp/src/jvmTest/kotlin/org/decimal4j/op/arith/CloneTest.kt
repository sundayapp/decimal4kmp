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

import org.decimal4j.api.Decimal
import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.api.MutableDecimal
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.AbstractDecimalTest
import org.decimal4j.test.TestSettings
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

/**
 * Unit test for [Decimal.abs]
 */
@RunWith(Parameterized::class)
class CloneTest(scaleMetrics: ScaleMetrics?, arithmetic: DecimalArithmetic) : AbstractDecimalTest(arithmetic) {
    @Test
    fun testRandom() {
        val count = TestSettings.getRandomTestCount()
        for (i in 0 until count) {
            val value = newDecimal(scaleMetrics, nextLongOrInt())
            runTest("random[$i]: + value", value)
        }
    }

    @Test
    fun testSpecial() {
        var i = 0
        for (special in TestSettings.TEST_CASES.getSpecialValuesFor(scaleMetrics)) {
            val value = newDecimal(scaleMetrics, special)
            runTest("special[$i]: + value", value)
            i++
        }
    }

    private fun <S : ScaleMetrics> runTest(testName: String, value: Decimal<S>) {
        val mutable: MutableDecimal<*> = value.toMutableDecimal()
        val clone = mutable.clone()

        Assert.assertEquals("unscaled value should be equal", value.unscaledValue(), clone.unscaledValue())
        Assert.assertEquals("unscaled value should be equal", mutable.unscaledValue(), clone.unscaledValue())

        Assert.assertEquals("value should be equal", value, clone)
        Assert.assertEquals("value should be equal", mutable, clone)

        Assert.assertEquals("should be same scale", value.scale.toLong(), clone.scale.toLong())
        Assert.assertEquals("should be same scale", mutable.scale.toLong(), clone.scale.toLong())

        Assert.assertNotSame("value should be different instances", mutable, clone)

        Assert.assertEquals("should be same class", mutable.javaClass, clone.javaClass)
    }

    companion object {
        @JvmStatic @Parameterized.Parameters(name = "{index}: scale={0}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                data.add(arrayOf(s, s.getDefaultArithmetic()))
            }
            return data
        }
    }
}
