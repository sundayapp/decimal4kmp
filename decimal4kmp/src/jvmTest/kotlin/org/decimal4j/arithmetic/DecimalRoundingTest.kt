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

import org.decimal4j.truncate.DecimalRounding
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.math.abs

/**
 * Unit test for [DecimalRounding]
 */
@RunWith(Parameterized::class)
class DecimalRoundingTest {
    @JvmField
    @Rule
    var thrown: ExpectedException = ExpectedException.none()

    @JvmField
    @Parameterized.Parameter(value = 0)
    var name: String? = null

    @Parameterized.Parameter(value = 1)
    lateinit var rounding: DecimalRounding

    @JvmField
    @Parameterized.Parameter(value = 2)
    var input: Double = 0.0

    @JvmField
    @Parameterized.Parameter(value = 3)
    var expected: Double = 0.0

    @Test
    fun shouldCalculateRoundingIncrement() {
        val inputTimes100 = Math.round(input * 100)
        val truncated = inputTimes100 / 100
        val reminder = (inputTimes100 - truncated * 100).toInt()
        val absReminder = abs(reminder.toDouble()).toInt()
        val firstTruncDigit = absReminder / 10
        val anyAfterFirstTruncDigit = 0 == absReminder - 10 * (absReminder / 10)
        if (java.lang.Double.isNaN(expected)) {
            thrown.expect(ArithmeticException::class.java)
            thrown.expectMessage("necessary")
            Rounding.calculateRoundingIncrement(
                rounding,
                java.lang.Long.signum(reminder.toLong()),
                truncated,
                firstTruncDigit,
                anyAfterFirstTruncDigit
            )
        } else {
            val increment = Rounding.calculateRoundingIncrement(
                rounding,
                java.lang.Long.signum(reminder.toLong()),
                truncated,
                firstTruncDigit,
                anyAfterFirstTruncDigit
            )
            val actual = ((input.toLong()) + increment).toDouble()
            Assert.assertEquals("wrong rounding for $input $rounding", expected, actual, 0.0)
        }
    }

    companion object {
        /**
         * InputNumber	UP	DOWN	CEILING	FLOOR	HALF_UP	HALF_DOWN	HALF_EVEN	UNNECESSARY
         */
        private val DATA = arrayOf(
            //first the values from RoundingMode javadoc
            doubleArrayOf(5.5, 6.0, 5.0, 6.0, 5.0, 6.0, 5.0, 6.0, Double.NaN),
            doubleArrayOf(2.5, 3.0, 2.0, 3.0, 2.0, 3.0, 2.0, 2.0, Double.NaN),
            doubleArrayOf(1.6, 2.0, 1.0, 2.0, 1.0, 2.0, 2.0, 2.0, Double.NaN),
            doubleArrayOf(1.1, 2.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, Double.NaN),
            doubleArrayOf(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0),
            doubleArrayOf(-1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0),
            doubleArrayOf(-1.1, -2.0, -1.0, -1.0, -2.0, -1.0, -1.0, -1.0, Double.NaN),
            doubleArrayOf(-1.6, -2.0, -1.0, -1.0, -2.0, -2.0, -2.0, -2.0, Double.NaN),
            doubleArrayOf(-2.5, -3.0, -2.0, -2.0, -3.0, -3.0, -2.0, -2.0, Double.NaN),
            doubleArrayOf(
                -5.5,
                -6.0,
                -5.0,
                -5.0,
                -6.0,
                -6.0,
                -5.0,
                -6.0,
                Double.NaN
            ),  //now some additional values with more after decimal digits
            doubleArrayOf(5.51, 6.0, 5.0, 6.0, 5.0, 6.0, 6.0, 6.0, Double.NaN),
            doubleArrayOf(2.51, 3.0, 2.0, 3.0, 2.0, 3.0, 3.0, 3.0, Double.NaN),
            doubleArrayOf(1.61, 2.0, 1.0, 2.0, 1.0, 2.0, 2.0, 2.0, Double.NaN),
            doubleArrayOf(1.11, 2.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, Double.NaN),
            doubleArrayOf(1.01, 2.0, 1.0, 2.0, 1.0, 1.0, 1.0, 1.0, Double.NaN),
            doubleArrayOf(-1.01, -2.0, -1.0, -1.0, -2.0, -1.0, -1.0, -1.0, Double.NaN),
            doubleArrayOf(-1.11, -2.0, -1.0, -1.0, -2.0, -1.0, -1.0, -1.0, Double.NaN),
            doubleArrayOf(-1.61, -2.0, -1.0, -1.0, -2.0, -2.0, -2.0, -2.0, Double.NaN),
            doubleArrayOf(-2.51, -3.0, -2.0, -2.0, -3.0, -3.0, -3.0, -3.0, Double.NaN),
            doubleArrayOf(
                -5.51,
                -6.0,
                -5.0,
                -5.0,
                -6.0,
                -6.0,
                -6.0,
                -6.0,
                Double.NaN
            ),  //also interesting the special case with a leading zero
            doubleArrayOf(0.61, 1.0, 0.0, 1.0, 0.0, 1.0, 1.0, 1.0, Double.NaN),
            doubleArrayOf(0.6, 1.0, 0.0, 1.0, 0.0, 1.0, 1.0, 1.0, Double.NaN),
            doubleArrayOf(0.51, 1.0, 0.0, 1.0, 0.0, 1.0, 1.0, 1.0, Double.NaN),
            doubleArrayOf(0.5, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, Double.NaN),
            doubleArrayOf(0.1, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.NaN),
            doubleArrayOf(0.01, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, Double.NaN),
            doubleArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            doubleArrayOf(-0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
            doubleArrayOf(-0.01, -1.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, Double.NaN),
            doubleArrayOf(-0.1, -1.0, 0.0, 0.0, -1.0, 0.0, 0.0, 0.0, Double.NaN),
            doubleArrayOf(-0.5, -1.0, 0.0, 0.0, -1.0, -1.0, 0.0, 0.0, Double.NaN),
            doubleArrayOf(-0.51, -1.0, 0.0, 0.0, -1.0, -1.0, -1.0, -1.0, Double.NaN),
            doubleArrayOf(-0.6, -1.0, 0.0, 0.0, -1.0, -1.0, -1.0, -1.0, Double.NaN),
            doubleArrayOf(-0.61, -1.0, 0.0, 0.0, -1.0, -1.0, -1.0, -1.0, Double.NaN),
        )
        private val DATA_COLS = arrayOf(
            null,
            DecimalRounding.UP,
            DecimalRounding.DOWN,
            DecimalRounding.CEILING,
            DecimalRounding.FLOOR,
            DecimalRounding.HALF_UP,
            DecimalRounding.HALF_DOWN,
            DecimalRounding.HALF_EVEN,
            DecimalRounding.UNNECESSARY
        )


        @JvmStatic
        @get:Parameterized.Parameters(name = "{0}")
        val data: List<Array<Any>>
            get() {
                val data: MutableList<Array<Any>> = ArrayList()
                for (rounding in DecimalRounding.entries) {
                    data.addAll(getDataFor(rounding))
                }
                return data
            }

        private fun getDataFor(rounding: DecimalRounding): List<Array<Any>> {
            val expectedCol = getDataColumnFor(rounding)
            val data: MutableList<Array<Any>> = ArrayList(DATA.size)
            for (row in DATA) {
                data.add(
                    arrayOf(
                        row[0].toString() + " " + rounding,
                        rounding,
                        row[0],
                        row[expectedCol]
                    )
                )
            }
            return data
        }

        private fun getDataColumnFor(rounding: DecimalRounding): Int {
            for (i in DATA_COLS.indices) {
                if (DATA_COLS[i] === rounding) return i
            }
            throw IllegalArgumentException("No data column defined for $rounding")
        }
    }
}
