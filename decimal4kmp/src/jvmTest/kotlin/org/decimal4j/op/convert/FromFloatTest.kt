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
import org.decimal4j.op.AbstractFloatToDecimalTest
import org.decimal4j.op.util.FloatAndDoubleUtil
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal
import org.decimal4j.truncate.RoundingMode
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberFunctions

/**
 * Test [DecimalArithmetic.fromFloat] via
 * [DecimalFactory.valueOf], [MutableDecimal.set] and
 * the static `valueOf(float)` methods of the Immutable Decimal
 * implementations.
 */
@RunWith(Parameterized::class)
class FromFloatTest(s: ScaleMetrics?, mode: RoundingMode?, arithmetic: DecimalArithmetic) :
    AbstractFloatToDecimalTest(arithmetic) {
    @Test
    fun run0p99999994_failureBuild_513() {
        //failed for 0.99999992f to 0.99999997f, scale 0, rounding UP, CEILING
        val input = 0.99999994f
        runTest(scaleMetrics, "0p99999994_failureBuild_513", input)
    }

    @Test
    fun runNeg0p99999994_failureBuild_513() {
        //failed for -0.99999994f to -0.99999997f, scale 0, rounding UP, FLOOR
        val input = -0.99999994f
        runTest(scaleMetrics, "Neg0p99999994_failureBuild_513", input)
    }

    override fun operation(): String {
        return "fromFloat"
    }

    override fun expectedResult(operand: Float): BigDecimal {
        return FloatAndDoubleUtil.floatToBigDecimal(operand, getArithmeticScale(), roundingMode)
    }

    override fun <S : ScaleMetrics> actualResult(scaleMetrics: S, operand: Float): Decimal<S> {
        return when (RND.nextInt(3)) {
            0 ->            //Factory, immutable
                if (isRoundingDefault && RND.nextBoolean()) {
                    getDecimalFactory(scaleMetrics).valueOf(operand)
                } else {
                    getDecimalFactory(scaleMetrics).valueOf(operand, roundingMode)
                }

            1 ->            //Factory, mutable
                if (isRoundingDefault && RND.nextBoolean()) {
                    getDecimalFactory(scaleMetrics).newMutable().set(operand)
                } else {
                    getDecimalFactory(scaleMetrics).newMutable().set(operand, roundingMode)
                }

            2 ->            //Immutable, valueOf method
                valueOf(scaleMetrics, operand)

            else ->
                valueOf(scaleMetrics, operand)
        }
    }

    private fun <S : ScaleMetrics> valueOf(scaleMetrics: S, operand: Float): Decimal<S> {
        try {
            val kClass = Class.forName(immutableClassName).kotlin
            return if (isRoundingDefault && RND.nextBoolean()) {
                kClass.companionObject!!.memberFunctions.first {
                    it.name == "valueOf" &&
                            it.parameters.size == 2 &&
                            it.parameters[1].type.classifier == Float::class
                }.call(kClass.companionObjectInstance, operand) as Decimal<S>
            } else {
                kClass.companionObject!!.memberFunctions.first {
                    it.name == "valueOf" &&
                            it.parameters.size == 3 &&
                            it.parameters[1].type.classifier == Float::class &&
                            it.parameters[2].type.classifier == RoundingMode::class
                }.call(kClass.companionObjectInstance, operand, roundingMode) as Decimal<S>
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
