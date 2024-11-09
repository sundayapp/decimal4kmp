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

import org.decimal4j.api.Decimal
import org.decimal4j.factory.Factories
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import kotlin.random.Random

/**
 * Defines several providers those supply [Decimal] arguments used in parameterized unit
 * tests.
 */
internal object DecimalArgumentProviders {
    private val RND = Random.Default

    @JvmStatic
    fun newDecimal(scaleMetrics: ScaleMetrics, unscaled: Long): Decimal<ScaleMetrics> {
        return Factories.getDecimalFactory(scaleMetrics).valueOfUnscaled(unscaled)
    }

    object UnaryDecimalArgumentProvider {
        @JvmStatic
        fun provideInput(): Array<Any> {
            val data: MutableList<Array<Any>> = ArrayList()

            var decimal: Decimal<ScaleMetrics>
            for (s in TestSettings.SCALES) {
                decimal = newDecimal(s, RND.nextLong())

                data.add(arrayOf(decimal))
                data.add(arrayOf(decimal.toMutableDecimal()))
            }
            return data.toTypedArray()
        }
    }

    object BinaryDecimalArgumentProvider {
        @JvmStatic
        fun provideInput(): Array<Any> {
            val data: MutableList<Array<Any>> = ArrayList()

            var random: Long
            var firstDecimal: Decimal<ScaleMetrics>
            var secondDecimal: Decimal<ScaleMetrics>

            for (s in TestSettings.SCALES) {
                random = RND.nextLong()

                firstDecimal = newDecimal(s, random)
                secondDecimal = newDecimal(s, random)

                data.add(arrayOf(firstDecimal, secondDecimal))
                data.add(arrayOf(firstDecimal, firstDecimal.toMutableDecimal()))
                data.add(arrayOf(firstDecimal, secondDecimal.toMutableDecimal()))
                data.add(arrayOf(firstDecimal.toMutableDecimal(), secondDecimal.toMutableDecimal()))
                data.add(arrayOf(secondDecimal, secondDecimal.toMutableDecimal()))
                data.add(arrayOf(secondDecimal, firstDecimal.toMutableDecimal()))
            }

            return data.toTypedArray()
        }
    }

    object TernaryDecimalArgumentProvider {
        @JvmStatic
        fun provideInput(): Array<Any> {
            val data: MutableList<Array<Any>> = ArrayList()

            var random: Long
            var firstDecimal: Decimal<ScaleMetrics>
            var secondDecimal: Decimal<ScaleMetrics>
            var thirdDecimal: Decimal<ScaleMetrics>

            for (s in TestSettings.SCALES) {
                random = RND.nextLong()

                firstDecimal = newDecimal(s, random)
                secondDecimal = newDecimal(s, random)
                thirdDecimal = newDecimal(s, random)

                data.add(arrayOf(firstDecimal, secondDecimal, thirdDecimal))
                data.add(arrayOf(firstDecimal, secondDecimal, thirdDecimal.toMutableDecimal()))
            }

            return data.toTypedArray()
        }
    }
}
