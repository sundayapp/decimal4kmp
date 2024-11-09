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
import org.decimal4j.op.AbstractDecimalToAnyTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Unit test for the diverse methods returning a boolean value, such
 * as [Decimal.isZero], [Decimal.isOne], [Decimal.isNegative] etc.
 */
@RunWith(Parameterized::class)
class IsSomethingTest(scaleMetrics: ScaleMetrics?, private val operation: Operation, arithmetic: DecimalArithmetic) :
    AbstractDecimalToAnyTest<Boolean?>(arithmetic) {
    enum class Operation {
        isZero {
            override fun expectedResult(operand: BigDecimal): Boolean {
                return operand.compareTo(BigDecimal.ZERO) == 0
            }

            override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Boolean {
                return operand.isZero()
            }
        },
        isOne {
            override fun expectedResult(operand: BigDecimal): Boolean {
                return operand.compareTo(BigDecimal.ONE) == 0
            }

            override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Boolean {
                return operand.isOne()
            }
        },
        isMinusOne {
            override fun expectedResult(operand: BigDecimal): Boolean {
                return operand.compareTo(BigDecimal.ONE.negate()) == 0
            }

            override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Boolean {
                return operand.isMinusOne()
            }
        },
        isUlp {
            override fun expectedResult(operand: BigDecimal): Boolean {
                return operand.unscaledValue() == BigInteger.ONE
            }

            override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Boolean {
                return operand.isUlp()
            }
        },
        isPositive {
            override fun expectedResult(operand: BigDecimal): Boolean {
                return operand.signum() > 0
            }

            override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Boolean {
                return operand.isPositive()
            }
        },
        isNonNegative {
            override fun expectedResult(operand: BigDecimal): Boolean {
                return operand.signum() >= 0
            }

            override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Boolean {
                return operand.isNonNegative()
            }
        },
        isNegative {
            override fun expectedResult(operand: BigDecimal): Boolean {
                return operand.signum() < 0
            }

            override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Boolean {
                return operand.isNegative()
            }
        },
        isNonPositive {
            override fun expectedResult(operand: BigDecimal): Boolean {
                return operand.signum() <= 0
            }

            override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Boolean {
                return operand.isNonPositive()
            }
        },
        isIntegral {
            override fun expectedResult(operand: BigDecimal): Boolean {
                return operand.compareTo(operand.divideToIntegralValue(BigDecimal.ONE)) == 0
            }

            override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Boolean {
                return operand.isIntegral()
            }
        },
        isIntegralPartZero {
            override fun expectedResult(operand: BigDecimal): Boolean {
                return operand.compareTo(BigDecimal.ONE) < 0 && operand.compareTo(BigDecimal.ONE.negate()) > 0
            }

            override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Boolean {
                return operand.isIntegralPartZero()
            }
        },
        isBetweenZeroAndOne {
            override fun expectedResult(operand: BigDecimal): Boolean {
                return operand.compareTo(BigDecimal.ONE) < 0 && operand.compareTo(BigDecimal.ZERO) >= 0
            }

            override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Boolean {
                return operand.isBetweenZeroAndOne()
            }
        },
        isBetweenZeroAndMinusOne {
            override fun expectedResult(operand: BigDecimal): Boolean {
                return operand.compareTo(BigDecimal.ONE.negate()) > 0 && operand.compareTo(BigDecimal.ZERO) <= 0
            }

            override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Boolean {
                return operand.isBetweenZeroAndMinusOne()
            }
        };

        abstract fun expectedResult(operand: BigDecimal): Boolean
        abstract fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Boolean
    }

    override fun operation(): String {
        return operation.name
    }

    override fun expectedResult(operand: BigDecimal): Boolean {
        return operation.expectedResult(operand)
    }

    override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Boolean {
        return operation.actualResult(operand)
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
