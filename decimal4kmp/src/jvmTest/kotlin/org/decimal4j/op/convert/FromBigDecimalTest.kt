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
package org.decimal4j.op.convert

import org.decimal4j.api.*
import org.decimal4j.factory.DecimalFactory
import org.decimal4j.op.AbstractBigDecimalToDecimalTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal

/**
 * Test [DecimalArithmetic.fromBigDecimal] via
 * [DecimalFactory.valueOf], [MutableDecimal.set]
 * and the static `valueOf(BigDecimal)` methods of the Immutable Decimal
 * implementations.
 */
@RunWith(Parameterized::class)
class FromBigDecimalTest(s: ScaleMetrics?, rm: RoundingMode?, arithmetic: DecimalArithmetic) :
    AbstractBigDecimalToDecimalTest(arithmetic) {
    override fun operation(): String {
        return "fromBigDecimal"
    }

    override fun expectedResult(operand: BigDecimal): BigDecimal {
        val result = operand.setScale(getArithmeticScale(), roundingMode.toJavaRoundingMode())
        require(result.unscaledValue().bitLength() <= 63) { "Overflow: $result" }
        return result
    }

    override fun <S : ScaleMetrics> actualResult(scaleMetrics: S, operand: BigDecimal): Decimal<S> {
        when (RND.nextInt(5)) {
            0 ->            //Factory, immutable
                return if (isRoundingDefault && RND.nextBoolean()) {
                    getDecimalFactory(scaleMetrics).valueOf(operand)
                } else {
                    getDecimalFactory(scaleMetrics).valueOf(operand, roundingMode)
                }

            1 ->            //Factory, mutable
                return if (isRoundingDefault && RND.nextBoolean()) {
                    getDecimalFactory(scaleMetrics).newMutable().set(operand)
                } else {
                    getDecimalFactory(scaleMetrics).newMutable().set(operand, roundingMode)
                }

            2 ->            //Immutable, valueOf method
                return valueOf(scaleMetrics, operand)

            3 -> {
                //Mutable, constructor
                if (isRoundingDefault) {
                    return newMutableInstance(scaleMetrics, operand)
                } //else: fallthrough

                return newDecimal(scaleMetrics, arithmetic.fromBigDecimal(operand))
            }

            4 -> return newDecimal(scaleMetrics, arithmetic.fromBigDecimal(operand))
            else -> return newDecimal(scaleMetrics, arithmetic.fromBigDecimal(operand))
        }
    }

    private fun <S : ScaleMetrics> newMutableInstance(scaleMetrics: S, operand: BigDecimal): Decimal<S> {
        try {
            val clazz = Class.forName(mutableClassName) as Class<Decimal<S>>
            return clazz.getConstructor(BigDecimal::class.java).newInstance(operand)
        } catch (e: InvocationTargetException) {
            if (e.targetException is RuntimeException) {
                throw (e.targetException as RuntimeException)
            }
            throw RuntimeException("could not invoke constructor, e=$e", e)
        } catch (e: Exception) {
            throw RuntimeException("could not invoke constructor, e=$e", e)
        }
    }

    private fun <S : ScaleMetrics> valueOf(scaleMetrics: S, operand: BigDecimal): Decimal<S> {
        try {
            val clazz = Class.forName(immutableClassName)
            return if (isRoundingDefault && RND.nextBoolean()) {
                clazz.getMethod("valueOf", BigDecimal::class.java).invoke(null, operand) as Decimal<S>
            } else {
                clazz.getMethod("valueOf", BigDecimal::class.java, RoundingMode::class.java).invoke(
                    null, operand,
                    roundingMode
                ) as Decimal<S>
            }
        } catch (e: InvocationTargetException) {
            if (e.targetException is RuntimeException) {
                throw (e.targetException as RuntimeException)
            }
            throw RuntimeException("could not invoke valueOf method, e=$e", e)
        } catch (e: Exception) {
            throw RuntimeException("could not invoke valueOf method, e=$e", e)
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}, {1}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                for (rm in TestSettings.UNCHECKED_ROUNDING_MODES) {
                    data.add(arrayOf(s, rm, s.getArithmetic(rm)))
                }
            }
            return data
        }
    }
}
