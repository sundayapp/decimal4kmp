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

import org.decimal4j.api.Decimal
import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.api.MutableDecimal
import org.decimal4j.factory.DecimalFactory
import org.decimal4j.op.AbstractDoubleToDecimalTest
import org.decimal4j.op.util.FloatAndDoubleUtil
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal
import org.decimal4j.api.RoundingMode

/**
 * Test [DecimalArithmetic.fromDouble] via
 * [DecimalFactory.valueOf], [MutableDecimal.set]
 * and the static `valueOf(double)` methods of the Immutable Decimal
 * implementations. The same conversion method is also used in other operations
 * that are involving doubles.
 */
@RunWith(Parameterized::class)
class FromDoubleTest(s: ScaleMetrics?, mode: RoundingMode?, arithmetic: DecimalArithmetic) :
    AbstractDoubleToDecimalTest(arithmetic) {
    @Test
    fun run0p99999999999999994_failureBuild_513() {
        //failed for 0.99999999999999984 to 0.99999999999999994, scale 0, rounding UP / CEILING
        val input = 0.99999999999999994
        runTest(scaleMetrics, "0p99999999999999994_failureBuild_513", input)
    }

    @Test
    fun runNeg0p99999999999999994_failureBuild_513() {
        //failed for -0.99999999999999984 to -0.99999999999999994, scale 0, rounding UP, FLOOR
        val input = -0.99999999999999994
        runTest(scaleMetrics, "Neg0p99999999999999994_failureBuild_513", input)
    }

    override fun operation(): String {
        return "fromDouble"
    }

    @Test
    fun testProblem1() {
        if (getArithmeticScale() == 4 && roundingMode == RoundingMode.HALF_DOWN) {
            runTest(scaleMetrics, "testProblem1", 3.354719257560035e-4)
        }
    }

    @Test
    fun testProblem2() {
        if (getArithmeticScale() == 4 && roundingMode == RoundingMode.HALF_DOWN) {
            runTest(scaleMetrics, "testProblem2", 3.9541250940045014e-4)
        }
    }

    override fun expectedResult(operand: Double): BigDecimal {
        return FloatAndDoubleUtil.doubleToBigDecimal(operand, getArithmeticScale(), roundingMode)
    }

    override fun <S : ScaleMetrics> actualResult(scaleMetrics: S, operand: Double): Decimal<S> {
        when (RND.nextInt(4)) {
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

            2 -> {
                //mutable constructor
                if (isRoundingDefault) {
                    return newMutableInstance(scaleMetrics, operand)
                } //else: fallthrough

                //Immutable, valueOf method
                return valueOf(scaleMetrics, operand)
            }

            3 ->
                return valueOf(scaleMetrics, operand)

            else ->
                return valueOf(scaleMetrics, operand)
        }
    }

    private fun <S : ScaleMetrics> newMutableInstance(scaleMetrics: S, operand: Double): Decimal<S> {
        try {
            val clazz = Class.forName(mutableClassName) as Class<Decimal<S>>
            return clazz.getConstructor(Double::class.javaPrimitiveType).newInstance(operand)
        } catch (e: InvocationTargetException) {
            if (e.targetException is RuntimeException) {
                throw (e.targetException as RuntimeException)
            }
            throw RuntimeException("could not invoke constructor, e=$e", e)
        } catch (e: Exception) {
            throw RuntimeException("could not invoke constructor, e=$e", e)
        }
    }

    private fun <S : ScaleMetrics> valueOf(scaleMetrics: S, operand: Double): Decimal<S> {
        try {
            val clazz = Class.forName(immutableClassName)
            return if (isRoundingDefault && RND.nextBoolean()) {
                clazz.getMethod(
                    "valueOf",
                    Double::class.javaPrimitiveType
                ).invoke(null, operand) as Decimal<S>
            } else {
                clazz.getMethod("valueOf", Double::class.javaPrimitiveType, RoundingMode::class.java).invoke(
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
                for (mode in TestSettings.UNCHECKED_ROUNDING_MODES) {
                    val arith = s.getArithmetic(mode)
                    data.add(arrayOf(s, mode, arith))
                }
            }
            return data
        }
    }
}
