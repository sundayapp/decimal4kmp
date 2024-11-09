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
import org.decimal4j.op.AbstractUnscaledToDecimalTest
import org.decimal4j.op.util.UnscaledUtil
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal
import org.decimal4j.api.RoundingMode

/**
 * Test [DecimalArithmetic.fromUnscaled] via
 * [DecimalFactory.valueOfUnscaled] etc.,
 * [MutableDecimal.setUnscaled] etc. and the static
 * `valueOfUnscaled(...)` methods of the Immutable Decimal
 * implementations.
 */
@RunWith(Parameterized::class)
class FromUnscaledTest(sm: ScaleMetrics?, rm: RoundingMode?, scale: Int, arithmetic: DecimalArithmetic) :
    AbstractUnscaledToDecimalTest(scale, arithmetic) {
    override fun operation(): String {
        return "fromUnscaled"
    }

    override fun expectedResult(operand: Long): BigDecimal {
        return toBigDecimal(operand)
    }

    override fun <S : ScaleMetrics> actualResult(scaleMetrics: S, operand: Long): Decimal<S> {
        val noScale = scale == scaleMetrics!!.getScale() && RND.nextBoolean()
        val factory = getDecimalFactory(scaleMetrics)
        when (RND.nextInt(5)) {
            0 ->            // Factory, immutable
                return if (isRoundingDefault && RND.nextBoolean()) {
                    if (noScale) factory.valueOfUnscaled(operand) else factory.valueOfUnscaled(operand, scale)
                } else {
                    factory.valueOfUnscaled(operand, scale, roundingMode)
                }

            1 -> {
                // Factory, mutable (via set)
                val mutable = factory.newMutable()
                return if (isRoundingDefault && RND.nextBoolean()) {
                    if (noScale) mutable.setUnscaled(operand) else mutable.setUnscaled(operand, scale)
                } else {
                    mutable.setUnscaled(operand, scale, roundingMode)
                }
            }

            2 -> {
                if (scale == scaleMetrics.getScale() && isRoundingDefault) {
                    return newMutableInstance(scaleMetrics, operand)
                } //else: fallthrough

                return newDecimal(scaleMetrics, arithmetic.fromUnscaled(operand, scale))
            }

            3 -> return newDecimal(scaleMetrics, arithmetic.fromUnscaled(operand, scale))
            4 ->            // Immutable, valueOfUnscaled method
                return valueOfUnscaled(scaleMetrics, operand)

            else ->
                return valueOfUnscaled(scaleMetrics, operand)
        }
    }

    private fun <S : ScaleMetrics> newMutableInstance(scaleMetrics: S, operand: Long): Decimal<S> {
        try {
            val clazz = Class.forName(mutableClassName) as Class<Decimal<S>>
            val result = clazz.getMethod(
                "unscaled",
                Long::class.javaPrimitiveType
            ).invoke(null, operand) as Decimal<S>
            return result
        } catch (e: InvocationTargetException) {
            if (e.targetException is RuntimeException) {
                throw (e.targetException as RuntimeException)
            }
            throw RuntimeException("could not invoke constructor, e=$e", e)
        } catch (e: Exception) {
            throw RuntimeException("could not invoke constructor, e=$e", e)
        }
    }

    private fun <S : ScaleMetrics> valueOfUnscaled(scaleMetrics: S, operand: Long): Decimal<S> {
        try {
            val clazz = Class.forName(immutableClassName)
            return if (isRoundingDefault && RND.nextBoolean()) {
                if (scale == scaleMetrics!!.getScale() && RND.nextBoolean()) {
                    clazz.getMethod("valueOfUnscaled", Long::class.javaPrimitiveType) //
                        .invoke(null, operand) as Decimal<S>
                } else {
                    clazz.getMethod("valueOfUnscaled", Long::class.javaPrimitiveType, Int::class.javaPrimitiveType) //
                        .invoke(null, operand, scale) as Decimal<S>
                }
            } else {
                clazz.getMethod(
                    "valueOfUnscaled",
                    Long::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType,
                    RoundingMode::class.java
                ) //
                    .invoke(null, operand, scale, roundingMode) as Decimal<S>
            }
        } catch (e: InvocationTargetException) {
            if (e.targetException is RuntimeException) {
                throw (e.targetException as RuntimeException)
            }
            throw RuntimeException("could not invoke valueOfUnscaled method, e=$e", e)
        } catch (e: Exception) {
            throw RuntimeException("could not invoke valueOfUnscaled method, e=$e", e)
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}, {1}, scale={2}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                for (rm in TestSettings.UNCHECKED_ROUNDING_MODES) {
                    val arith = s.getArithmetic(rm)
                    for (scale in UnscaledUtil.getScales(s)) {
                        data.add(arrayOf(s, rm, scale, arith))
                    }
                }
            }
            return data
        }
    }
}
