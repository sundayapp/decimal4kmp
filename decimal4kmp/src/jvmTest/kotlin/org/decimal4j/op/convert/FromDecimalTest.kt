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
import org.decimal4j.api.BigDecimalExtensions.toBigDecimal
import org.decimal4j.factory.DecimalFactory
import org.decimal4j.op.AbstractUnknownDecimalToDecimalTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.decimal4j.truncate.RoundingMode
import org.decimal4j.arithmetic.toJavaRoundingMode
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.memberFunctions

/**
 * Tests from-Decimal conversion via
 * [DecimalFactory.valueOf], [MutableDecimal.set]
 * and the static `valueOf(Decimal)` methods of the Immutable Decimal
 * implementations.
 */
@RunWith(Parameterized::class)
class FromDecimalTest(s: ScaleMetrics?, mode: RoundingMode?, arithmetic: DecimalArithmetic) :
    AbstractUnknownDecimalToDecimalTest(arithmetic) {
    override fun operation(): String {
        return "fromDecimal"
    }

    override fun expectedResult(operand: Decimal<*>): BigDecimal {
        val result = operand.toBigDecimal().setScale(getArithmeticScale(), roundingMode.toJavaRoundingMode())
        require(result.unscaledValue().bitLength() <= 63) { "Overflow: $result" }
        return result
    }

    override fun <S : ScaleMetrics> actualResult(scaleMetrics: S, operand: Decimal<*>): Decimal<S> {
        when (RND.nextInt(4)) {
            0 ->            //Factory, immutable
                return if (isRoundingDefault && RND.nextBoolean()) {
                    getDecimalFactory(scaleMetrics).valueOf(operand)
                } else {
                    getDecimalFactory(scaleMetrics).valueOf(operand, roundingMode)
                }

            1 ->            //Factory, mutable
                if (operand.scale == scaleMetrics.getScale() && RND.nextBoolean()) {
                    val `val` = operand.scale(scaleMetrics) //won't change, acts like a cast
                    return getDecimalFactory(scaleMetrics).newMutable().set(`val`)
                } else {
                    return getDecimalFactory(scaleMetrics).newMutable().set(operand, roundingMode)
                }

            2 -> {
                //Mutable, constructor
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

    private fun <S : ScaleMetrics> newMutableInstance(scaleMetrics: S, operand: Decimal<*>): Decimal<S> {
        try {
            val mutableClass = Class.forName(mutableClassName) as Class<Decimal<S>>
            val immutableClass = Class.forName(immutableClassName) as Class<Decimal<S>>
            if (immutableClass.isInstance(operand) && RND.nextBoolean()) {
                return mutableClass.getConstructor(immutableClass).newInstance(operand)
            }
            return mutableClass.getConstructor(Decimal::class.java).newInstance(operand)
        } catch (e: InvocationTargetException) {
            if (e.targetException is RuntimeException) {
                throw (e.targetException as RuntimeException)
            }
            throw RuntimeException("could not invoke constructor, e=$e", e)
        } catch (e: Exception) {
            throw RuntimeException("could not invoke constructor, e=$e", e)
        }
    }

    private fun <S : ScaleMetrics> valueOf(scaleMetrics: S, operand: Decimal<*>): Decimal<S> {
        try {
            val kClass = Class.forName(immutableClassName).kotlin
            return if (operand.scale == scaleMetrics.getScale() && RND.nextBoolean()) {
                kClass.companionObject!!.memberFunctions.first {
                    it.name == "valueOf" &&
                        it.parameters.size == 2 &&
                        it.parameters[1].type.classifier == Decimal::class
                }
                    .call(kClass.companionObjectInstance, operand) as Decimal<S>

            } else {
                kClass.companionObject!!.memberFunctions.first {
                    it.name == "valueOf" &&
                            it.parameters.size == 3 &&
                            it.parameters[1].type.classifier == Decimal::class &&
                            it.parameters[2].type.classifier == RoundingMode::class
                }
                    .call(kClass.companionObjectInstance, operand, roundingMode) as Decimal<S>
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
