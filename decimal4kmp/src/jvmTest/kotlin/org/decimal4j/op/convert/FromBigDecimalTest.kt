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
import org.decimal4j.api.DecimalArithmeticExtensions.fromBigDecimal
import org.decimal4j.api.MutableDecimalJvm.set
import org.decimal4j.factory.DecimalFactory
import org.decimal4j.op.AbstractBigDecimalToDecimalTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.decimal4j.truncate.RoundingMode
import org.decimal4j.arithmetic.toJavaRoundingMode
import org.decimal4j.factory.DecimalFactoryJvm
import org.decimal4j.factory.Factory0f
import org.decimal4j.factory.Factory0fJvm
import org.decimal4j.factory.Factory10f
import org.decimal4j.factory.Factory10fJvm
import org.decimal4j.factory.Factory11f
import org.decimal4j.factory.Factory11fJvm
import org.decimal4j.factory.Factory12f
import org.decimal4j.factory.Factory12fJvm
import org.decimal4j.factory.Factory13f
import org.decimal4j.factory.Factory13fJvm
import org.decimal4j.factory.Factory14f
import org.decimal4j.factory.Factory14fJvm
import org.decimal4j.factory.Factory15f
import org.decimal4j.factory.Factory15fJvm
import org.decimal4j.factory.Factory16f
import org.decimal4j.factory.Factory16fJvm
import org.decimal4j.factory.Factory17f
import org.decimal4j.factory.Factory17fJvm
import org.decimal4j.factory.Factory18f
import org.decimal4j.factory.Factory18fJvm
import org.decimal4j.factory.Factory1f
import org.decimal4j.factory.Factory1fJvm
import org.decimal4j.factory.Factory2f
import org.decimal4j.factory.Factory2fJvm
import org.decimal4j.factory.Factory3f
import org.decimal4j.factory.Factory3fJvm
import org.decimal4j.factory.Factory4f
import org.decimal4j.factory.Factory4fJvm
import org.decimal4j.factory.Factory5f
import org.decimal4j.factory.Factory5fJvm
import org.decimal4j.factory.Factory6f
import org.decimal4j.factory.Factory6fJvm
import org.decimal4j.factory.Factory7f
import org.decimal4j.factory.Factory7fJvm
import org.decimal4j.factory.Factory8f
import org.decimal4j.factory.Factory8fJvm
import org.decimal4j.factory.Factory9f
import org.decimal4j.factory.Factory9fJvm
import org.decimal4j.generic.GenericDecimalFactory
import org.decimal4j.generic.GenericDecimalFactoryExt
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.functions

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
                    getDecimalFactory(scaleMetrics).toJvm().valueOf(operand)
                } else {
                    getDecimalFactory(scaleMetrics).toJvm().valueOf(operand, roundingMode)
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
            val clazz = Class.forName(mutableClassName) as Class<MutableDecimal<S>>
            val mutableDecimal = clazz.getConstructor().newInstance() as MutableDecimal<S>
            return mutableDecimal.set(operand)
        } catch (e: RuntimeException) {
            throw e
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
            val clazz = Class.forName(immutableClassName).kotlin
            val extensionKClass = Class.forName(immutableExtensionClassName).kotlin

            return if (isRoundingDefault && RND.nextBoolean()) {
                val kFunction = extensionKClass.functions.find {
                    it.name == "valueOf" &&
                            it.parameters.size == 3 &&
                            it.parameters[2].type.classifier == BigDecimal::class
                }!!
                kFunction.call(extensionKClass.objectInstance, clazz.companionObjectInstance, operand) as Decimal<S>
            } else {
                val kFunction = extensionKClass.functions.find {
                    it.name == "valueOf" &&
                            it.parameters.size == 4 &&
                            it.parameters[2].type.classifier == BigDecimal::class &&
                            it.parameters[3].type.classifier == RoundingMode::class
                }!!
                kFunction.call(extensionKClass.objectInstance, clazz.companionObjectInstance, operand, roundingMode) as Decimal<S>
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

fun <S: ScaleMetrics> DecimalFactory<S>.toJvm(): DecimalFactoryJvm<S> {
    when (this) {
        is DecimalFactoryJvm -> return this
        is Factory0f -> return Factory0fJvm(this) as DecimalFactoryJvm<S>
        is Factory1f -> return Factory1fJvm(this) as DecimalFactoryJvm<S>
        is Factory2f -> return Factory2fJvm(this) as DecimalFactoryJvm<S>
        is Factory3f -> return Factory3fJvm(this) as DecimalFactoryJvm<S>
        is Factory4f -> return Factory4fJvm(this) as DecimalFactoryJvm<S>
        is Factory5f -> return Factory5fJvm(this) as DecimalFactoryJvm<S>
        is Factory6f -> return Factory6fJvm(this) as DecimalFactoryJvm<S>
        is Factory7f -> return Factory7fJvm(this) as DecimalFactoryJvm<S>
        is Factory8f -> return Factory8fJvm(this) as DecimalFactoryJvm<S>
        is Factory9f -> return Factory9fJvm(this) as DecimalFactoryJvm<S>
        is Factory10f -> return Factory10fJvm(this) as DecimalFactoryJvm<S>
        is Factory11f -> return Factory11fJvm(this) as DecimalFactoryJvm<S>
        is Factory12f -> return Factory12fJvm(this) as DecimalFactoryJvm<S>
        is Factory13f -> return Factory13fJvm(this) as DecimalFactoryJvm<S>
        is Factory14f -> return Factory14fJvm(this) as DecimalFactoryJvm<S>
        is Factory15f -> return Factory15fJvm(this) as DecimalFactoryJvm<S>
        is Factory16f -> return Factory16fJvm(this) as DecimalFactoryJvm<S>
        is Factory17f -> return Factory17fJvm(this) as DecimalFactoryJvm<S>
        is Factory18f -> return Factory18fJvm(this) as DecimalFactoryJvm<S>
        is GenericDecimalFactory -> return GenericDecimalFactoryExt(this.scaleMetrics)


        else -> throw IllegalArgumentException("Unsupported factory: $this")
    }
}
