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
package org.decimal4j.scale

import org.junit.Assert
import org.junit.Test

/**
 * Unit test for [Scales].
 */
class ScalesTest {
    @Test
    fun shouldGetScaleMetricsByScale() {
        for (scale in Scales.MIN_SCALE..Scales.MAX_SCALE) {
            val scaleMetrics = Scales.getScaleMetrics(scale)
            Assert.assertNotNull("scaleMetrics should not be null", scaleMetrics)
            Assert.assertEquals("scaleMetrics should have scale $scale", scale.toLong(), scaleMetrics.getScale().toLong())
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowExceptionForNegativeScale() {
        Scales.getScaleMetrics(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowExceptionForScaleExceedingMax() {
        Scales.getScaleMetrics(Scales.MAX_SCALE + 1)
    }

    @Test
    fun valuesListShouldBeSortedByScale() {
        Assert.assertEquals(
            "Scales.VALUES size does not equal all scales",
            (Scales.MAX_SCALE - Scales.MIN_SCALE + 1).toLong(),
            Scales.VALUES.size.toLong()
        )
        var scale = 0
        for (scaleMetrics in Scales.VALUES) {
            Assert.assertEquals("should have scale $scale", scale.toLong(), scaleMetrics.getScale().toLong())
            scale++
        }
    }

    @Test
    @Throws(Exception::class)
    fun testScaleMetricsSingleton() {
        for (scaleMetrics in Scales.VALUES) {
            //when
            val instance = scaleMetrics.javaClass.getMethod("valueOf", String::class.java).invoke(null, "INSTANCE")
            //then
            Assert.assertSame("should be same metric instance", scaleMetrics, instance)
            //when
            val instances = scaleMetrics.javaClass.getMethod("values").invoke(null)
            //then
            Assert.assertTrue("should be an array", instances is Array<*> && instances.isArrayOf<Any>())
            Assert.assertEquals("should be array length 1", 1, (instances as Array<Any?>).size.toLong())
            Assert.assertSame(
                "should be same metric instance", scaleMetrics,
                instances[0]
            )
        }
    }
}
