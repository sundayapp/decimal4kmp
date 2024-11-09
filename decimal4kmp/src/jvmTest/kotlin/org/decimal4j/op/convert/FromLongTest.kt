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
import org.decimal4j.op.AbstractLongValueToDecimalTest
import org.decimal4j.scale.Scale0f
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal

/**
 * Test [DecimalArithmetic.fromLong],
 * [DecimalFactory.valueOf] etc., [MutableDecimal.set]
 * etc. and the static `valueOf(long)` method of the Immutable Decimal
 * implementations.
 */
@RunWith(Parameterized::class)
class FromLongTest(sm: ScaleMetrics?, arithmetic: DecimalArithmetic) : AbstractLongValueToDecimalTest(arithmetic) {
    override fun operation(): String {
        return "fromLong"
    }

    override fun getSpecialLongOperands(): LongArray {
        return TestSettings.TEST_CASES.getSpecialValuesFor(Scale0f.INSTANCE)
    }

    override fun expectedResult(operand: Long): BigDecimal {
        val result = BigDecimal.valueOf(operand).setScale(getArithmeticScale())
        require(result.unscaledValue().bitLength() <= 63) { "Overflow: $result" }
        return result
    }

    override fun <S : ScaleMetrics> actualResult(scaleMetrics: S, operand: Long): Decimal<S> {
        if (isUnchecked) {
            return newDecimal(scaleMetrics, arithmetic.fromLong(operand))
        }
        return when (RND.nextInt(4)) {
            0 ->            // Factory, immutable
                getDecimalFactory(scaleMetrics).valueOf(operand)

            1 ->            // Factory, mutable
                getDecimalFactory(scaleMetrics).newMutable().set(operand)

            2 -> newMutableInstance(scaleMetrics, operand)
            3 ->            // Immutable, valueOf method
                valueOf(scaleMetrics, operand)

            else ->
                valueOf(scaleMetrics, operand)
        }
    }

    private fun <S : ScaleMetrics> newMutableInstance(scaleMetrics: S, operand: Long): Decimal<S> {
        try {
            val clazz = Class.forName(mutableClassName) as Class<Decimal<S>>
            return clazz.getConstructor(Long::class.javaPrimitiveType).newInstance(operand)
        } catch (e: InvocationTargetException) {
            if (e.targetException is RuntimeException) {
                throw (e.targetException as RuntimeException)
            }
            throw RuntimeException("could not invoke constructor, e=$e", e)
        } catch (e: Exception) {
            throw RuntimeException("could not invoke constructor, e=$e", e)
        }
    }

    private fun <S : ScaleMetrics> valueOf(scaleMetrics: S, operand: Long): Decimal<S> {
        try {
            val clazz = Class.forName(immutableClassName)
            return clazz.getMethod("valueOf", Long::class.javaPrimitiveType).invoke(null, operand) as Decimal<S>
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
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                data.add(arrayOf(s, s.getDefaultArithmetic()))
                data.add(arrayOf(s, s.getDefaultCheckedArithmetic()))
            }
            return data
        }
    }
}
