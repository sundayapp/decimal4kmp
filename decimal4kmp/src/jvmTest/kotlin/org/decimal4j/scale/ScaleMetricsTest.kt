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

import org.decimal4j.arithmetic.Unsigned
import org.decimal4j.scale.ScaleMetricsExtensions.getScaleFactorAsBigDecimal
import org.decimal4j.scale.ScaleMetricsExtensions.getScaleFactorAsBigInteger
import org.decimal4j.test.TestSettings.getRandomTestCount
import org.junit.Assert
import org.junit.Test
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.random.Random

/**
 * Unit test for [ScaleMetrics] implementations.
 */
class ScaleMetricsTest {
    @Test
    fun shouldCalcByScaleFactor() {
        for (scaleMetrics in Scales.VALUES) {
            for (i in 0 until getRandomTestCount()) {
                val value = RND.nextInt()
                val scaleFactor = scaleMetrics.getScaleFactor()

                // expected
                val expMul = value * scaleFactor
                val expDiv = value / scaleFactor
                val expDivU = Unsigned.divide(value.toLong(), scaleFactor)
                val expMod = value % scaleFactor
                val expLo = (0xffffffffL and value.toLong()) * (0xffffffffL and scaleFactor)
                val expHi = (0xffffffffL and value.toLong()) * (scaleFactor ushr 32)

                // actual + assert
                Assert.assertEquals(
                    "unexpected result $value * $scaleFactor", expMul,
                    scaleMetrics.multiplyByScaleFactor(value.toLong())
                )
                Assert.assertEquals(
                    "unexpected result $value / $scaleFactor", expDiv,
                    scaleMetrics.divideByScaleFactor(value.toLong())
                )
                Assert.assertEquals(
                    "unexpected result unsigned($value / $scaleFactor)", expDivU,
                    scaleMetrics.divideUnsignedByScaleFactor(value.toLong())
                )
                Assert.assertEquals(
                    "unexpected result $value % $scaleFactor", expMod,
                    scaleMetrics.moduloByScaleFactor(value.toLong())
                )
                Assert.assertEquals(
                    "unexpected result for mullo($value * $scaleFactor)", expLo,
                    scaleMetrics.mulloByScaleFactor(value)
                )
                Assert.assertEquals(
                    "unexpected result for mulhi($value * $scaleFactor)", expHi,
                    scaleMetrics.mulhiByScaleFactor(value)
                )
            }
        }
    }

    @Test
    fun assertScaleMetricsConstants() {
        var scale = 0
        var scaleFactor: Long = 1
        for (scaleMetrics in Scales.VALUES) {
            Assert.assertEquals("unexpected scale", scale.toLong(), scaleMetrics.getScale().toLong())
            Assert.assertEquals("unexpected scale factor", scaleFactor, scaleMetrics.getScaleFactor())
            Assert.assertEquals(
                "unexpected BigInteger scale factor", BigInteger.valueOf(scaleFactor),
                scaleMetrics.getScaleFactorAsBigInteger()
            )
            Assert.assertEquals(
                "unexpected BigDecimal scale factor", BigDecimal.valueOf(scaleFactor),
                scaleMetrics.getScaleFactorAsBigDecimal()
            )
            Assert.assertEquals(
                "unexpected min integer value", Long.MIN_VALUE / scaleFactor,
                scaleMetrics.getMinIntegerValue()
            )
            Assert.assertEquals(
                "unexpected max integer value", Long.MAX_VALUE / scaleFactor,
                scaleMetrics.getMaxIntegerValue()
            )
            Assert.assertEquals(
                "unexpected NLZ(scale factor)", java.lang.Long.numberOfLeadingZeros(scaleFactor).toLong(),
                scaleMetrics.getScaleFactorNumberOfLeadingZeros().toLong()
            )

            scale++
            scaleFactor *= 10
        }
    }

    companion object {
        private val RND = Random.Default
    }
}
