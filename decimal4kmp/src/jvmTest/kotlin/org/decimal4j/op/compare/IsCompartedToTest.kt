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
package org.decimal4j.op.compare

import org.decimal4j.api.Decimal
import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.op.AbstractDecimalDecimalToAnyTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal

/**
 * Unit test for the diverse comparison methods returning a boolean value, such
 * as [Decimal.isEqualTo], [Decimal.isGreaterThan] etc.
 */
@RunWith(Parameterized::class)
class IsCompartedToTest(scaleMetrics: ScaleMetrics?, private val operation: Operation, arithmetic: DecimalArithmetic) :
    AbstractDecimalDecimalToAnyTest<Boolean?>(arithmetic) {
    enum class Operation {
        isEqualto {
            override fun expectedResult(compareToResult: Int): Boolean {
                return compareToResult == 0
            }

            override fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Decimal<S>): Boolean {
                return a.isEqualTo(b)
            }
        },
        isGreaterThan {
            override fun expectedResult(compareToResult: Int): Boolean {
                return compareToResult > 0
            }

            override fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Decimal<S>): Boolean {
                return a.isGreaterThan(b)
            }
        },
        isGreaterThanOrEqualTo {
            override fun expectedResult(compareToResult: Int): Boolean {
                return compareToResult >= 0
            }

            override fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Decimal<S>): Boolean {
                return a.isGreaterThanOrEqualTo(b)
            }
        },
        isLessThan {
            override fun expectedResult(compareToResult: Int): Boolean {
                return compareToResult < 0
            }

            override fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Decimal<S>): Boolean {
                return a.isLessThan(b)
            }
        },
        isLessThanOrEqualTo {
            override fun expectedResult(compareToResult: Int): Boolean {
                return compareToResult <= 0
            }

            override fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Decimal<S>): Boolean {
                return a.isLessThanOrEqualTo(b)
            }
        };

        abstract fun expectedResult(compareToResult: Int): Boolean
        abstract fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Decimal<S>): Boolean
    }

    override fun operation(): String {
        return operation.name
    }

    override fun expectedResult(a: BigDecimal, b: BigDecimal): Boolean {
        return operation.expectedResult(a.compareTo(b))
    }

    override fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Decimal<S>): Boolean {
        return operation.actualResult(a, b)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: scale={0}, operation={1}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                for (op in Operation.entries) {
                    data.add(arrayOf(s, op, s.getDefaultArithmetic()))
                }
            }
            return data
        }
    }
}
