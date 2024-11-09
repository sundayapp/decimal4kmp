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
import org.decimal4j.api.MutableDecimal
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales
import org.decimal4j.test.AbstractDecimalTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.jvmErasure

/**
 * Unit test for Decimal constants and constant setter methods of the
 * [MutableDecimal].
 */
@RunWith(Parameterized::class)
class ConstantTest(scaleMetrics: ScaleMetrics?, arithmetic: DecimalArithmetic) : AbstractDecimalTest(arithmetic) {
    @Test
    fun testZero() {
        assertUnscaled("should be zero", 0, immutableConstant("ZERO"))
        assertUnscaled("should be zero", 0, immutableValueOf(Long::class, 0L))
        assertUnscaled("should be zero", 0, mutableConstant("zero"))
        assertUnscaled("should be zero", 0, newMutable().setZero())
    }

    @Test
    fun testOne() {
        val one = arithmetic.one()
        assertUnscaled("should be one", one, immutableConstant("ONE"))
        assertUnscaled("should be one", one, immutableValueOf(Long::class, 1L))
        assertUnscaled("should be one", one, mutableConstant("one"))
        assertUnscaled("should be one", one, newMutable().setOne())
    }

    @Test
    fun testTwo() {
        val two = 2 * arithmetic.one()
        assertUnscaled("should be two", two, immutableConstant("TWO"))
        assertUnscaled("should be two", two, immutableValueOf(Long::class, 2L))
        assertUnscaled("should be two", two, mutableConstant("two"))
        assertUnscaled("should be two", two, newMutable().set(2))
    }

    @Test
    fun testThree() {
        val three = 3 * arithmetic.one()
        assertUnscaled("should be three", three, immutableConstant("THREE"))
        assertUnscaled("should be three", three, immutableValueOf(Long::class, 3L))
        assertUnscaled("should be three", three, mutableConstant("three"))
        assertUnscaled("should be three", three, newMutable().set(3))
    }

    @Test
    fun testFour() {
        val four = 4 * arithmetic.one()
        assertUnscaled("should be four", four, immutableConstant("FOUR"))
        assertUnscaled("should be four", four, immutableValueOf(Long::class, 4L))
        assertUnscaled("should be four", four, mutableConstant("four"))
        assertUnscaled("should be four", four, newMutable().set(4))
    }

    @Test
    fun testFive() {
        val five = 5 * arithmetic.one()
        assertUnscaled("should be five", five, immutableConstant("FIVE"))
        assertUnscaled("should be five", five, immutableValueOf(Long::class, 5L))
        assertUnscaled("should be five", five, mutableConstant("five"))
        assertUnscaled("should be five", five, newMutable().set(5))
    }

    @Test
    fun testSix() {
        val six = 6 * arithmetic.one()
        assertUnscaled("should be six", six, immutableConstant("SIX"))
        assertUnscaled("should be six", six, immutableValueOf(Long::class, 6L))
        assertUnscaled("should be six", six, mutableConstant("six"))
        assertUnscaled("should be six", six, newMutable().set(6))
    }

    @Test
    fun testSeven() {
        val seven = 7 * arithmetic.one()
        assertUnscaled("should be seven", seven, immutableConstant("SEVEN"))
        assertUnscaled("should be seven", seven, immutableValueOf(Long::class, 7L))
        assertUnscaled("should be seven", seven, mutableConstant("seven"))
        assertUnscaled("should be seven", seven, newMutable().set(7))
    }

    @Test
    fun testEight() {
        val eight = 8 * arithmetic.one()
        assertUnscaled("should be eight", eight, immutableConstant("EIGHT"))
        assertUnscaled("should be eight", eight, immutableValueOf(Long::class, 8L))
        assertUnscaled("should be eight", eight, mutableConstant("eight"))
        assertUnscaled("should be eight", eight, newMutable().set(8))
    }

    @Test
    fun testNine() {
        val nine = 9 * arithmetic.one()
        assertUnscaled("should be nine", nine, immutableConstant("NINE"))
        assertUnscaled("should be nine", nine, immutableValueOf(Long::class, 9L))
        assertUnscaled("should be nine", nine, mutableConstant("nine"))
        assertUnscaled("should be nine", nine, newMutable().set(9))
    }

    @Test
    fun testUlp() {
        assertUnscaled("should be 1", 1, immutableConstant("ULP"))
        assertUnscaled("should be 1", 1, mutableConstant("ulp"))
        assertUnscaled("should be 1", 1, newMutable().setUlp())
    }

    @Test
    fun testMinusOne() {
        val minusOne = -arithmetic.one()
        assertUnscaled("should be minus one", minusOne, immutableConstant("MINUS_ONE"))
        assertUnscaled("should be minus one", minusOne, mutableConstant("minusOne"))
        assertUnscaled("should be minus one", minusOne, newMutable().setMinusOne())
    }

    @Test
    fun testTen() {
        if (getArithmeticScale() <= 17) {
            val ten = 10 * arithmetic.one()
            assertUnscaled("should be ten", ten, immutableConstant("TEN"))
            assertUnscaled("should be ten", ten, mutableConstant("ten"))
        }
    }

    @Test
    fun testHundred() {
        if (getArithmeticScale() <= 16) {
            val ten = 100 * arithmetic.one()
            assertUnscaled("should be hundred", ten, immutableConstant("HUNDRED"))
            assertUnscaled("should be hundred", ten, mutableConstant("hundred"))
        }
    }

    @Test
    fun testThousand() {
        if (getArithmeticScale() <= 15) {
            val ten = 1000 * arithmetic.one()
            assertUnscaled("should be thousand", ten, immutableConstant("THOUSAND"))
            assertUnscaled("should be thousand", ten, mutableConstant("thousand"))
        }
    }

    @Test
    fun testMillion() {
        if (getArithmeticScale() <= 12) {
            val ten = 1000000 * arithmetic.one()
            assertUnscaled("should be million", ten, immutableConstant("MILLION"))
            assertUnscaled("should be million", ten, mutableConstant("million"))
        }
    }

    @Test
    fun testBillion() {
        if (getArithmeticScale() <= 9) {
            val ten = 1000000000 * arithmetic.one()
            assertUnscaled("should be billion", ten, immutableConstant("BILLION"))
            assertUnscaled("should be billion", ten, mutableConstant("billion"))
        }
    }

    @Test
    fun testTrillion() {
        if (getArithmeticScale() <= 6) {
            val ten = 1000000000000L * arithmetic.one()
            assertUnscaled("should be trillion", ten, immutableConstant("TRILLION"))
            assertUnscaled("should be trillion", ten, mutableConstant("trillion"))
        }
    }

    @Test
    fun testQuadrillion() {
        if (getArithmeticScale() <= 3) {
            val ten = 1000000000000000L * arithmetic.one()
            assertUnscaled("should be quadrillion", ten, immutableConstant("QUADRILLION"))
            assertUnscaled("should be quadrillion", ten, mutableConstant("quadrillion"))
        }
    }

    @Test
    fun testQuintillion() {
        if (getArithmeticScale() <= 0) {
            val ten = 1000000000000000000L * arithmetic.one()
            assertUnscaled("should be quintillion", ten, immutableConstant("QUINTILLION"))
            assertUnscaled("should be quintillion", ten, mutableConstant("quintillion"))
        }
    }

    @Test
    fun testHalf() {
        if (getArithmeticScale() > 0) {
            val half = arithmetic.one() / 2
            assertUnscaled("should be half", half, immutableConstant("HALF"))
            assertUnscaled("should be half", half, mutableConstant("half"))
        }
    }

    @Test
    fun testTenth() {
        if (getArithmeticScale() >= 1) {
            val tenth = arithmetic.one() / 10
            assertUnscaled("should be tenth", tenth, immutableConstant("TENTH"))
            assertUnscaled("should be tenth", tenth, mutableConstant("tenth"))
        }
    }

    @Test
    fun testHundredth() {
        if (getArithmeticScale() >= 2) {
            val hundredth = arithmetic.one() / 100
            assertUnscaled("should be hundredth", hundredth, immutableConstant("HUNDREDTH"))
            assertUnscaled("should be hundredth", hundredth, mutableConstant("hundredth"))
        }
    }

    @Test
    fun testThousandth() {
        if (getArithmeticScale() >= 3) {
            val thousanth = arithmetic.one() / 1000
            assertUnscaled("should be thousanth", thousanth, immutableConstant("THOUSANDTH"))
            assertUnscaled("should be thousanth", thousanth, mutableConstant("thousandth"))
        }
    }

    @Test
    fun testMillionth() {
        if (getArithmeticScale() >= 6) {
            val millionth = arithmetic.one() / 1000000
            assertUnscaled("should be millionth", millionth, immutableConstant("MILLIONTH"))
            assertUnscaled("should be millionth", millionth, mutableConstant("millionth"))
        }
    }

    @Test
    fun testBillionth() {
        if (getArithmeticScale() >= 9) {
            val billionth = arithmetic.one() / 1000000000
            assertUnscaled("should be billionth", billionth, immutableConstant("BILLIONTH"))
            assertUnscaled("should be billionth", billionth, mutableConstant("billionth"))
        }
    }

    @Test
    fun testTrillionth() {
        if (getArithmeticScale() >= 12) {
            val trillionth = arithmetic.one() / 1000000000000L
            assertUnscaled("should be trillionth", trillionth, immutableConstant("TRILLIONTH"))
            assertUnscaled("should be trillionth", trillionth, mutableConstant("trillionth"))
        }
    }

    @Test
    fun testQuadrillionth() {
        if (getArithmeticScale() >= 15) {
            val quadrillionth = arithmetic.one() / 1000000000000000L
            assertUnscaled("should be quadrillionth", quadrillionth, immutableConstant("QUADRILLIONTH"))
            assertUnscaled("should be quadrillionth", quadrillionth, mutableConstant("quadrillionth"))
        }
    }

    @Test
    fun testQuintillionth() {
        if (getArithmeticScale() >= 18) {
            val quintillionth = arithmetic.one() / 1000000000000000000L
            assertUnscaled("should be quintillionth", quintillionth, immutableConstant("QUINTILLIONTH"))
            assertUnscaled("should be quintillionth", quintillionth, mutableConstant("quintillionth"))
        }
    }


    private fun assertUnscaled(msg: String, unscaled: Long, value: Decimal<*>) {
        Assert.assertEquals(msg, unscaled, value.unscaledValue())
    }

    private fun newMutable(): MutableDecimal<*> {
        return getDecimalFactory(scaleMetrics).newMutable().setUnscaled(RND.nextLong())
    }

    private fun immutableConstant(constantName: String): Decimal<*> {
        try {
            val clazz = Class.forName(immutableClassName)
            return clazz.getField(constantName)[null] as Decimal<*>
        } catch (e: Exception) {
            throw RuntimeException("could not access static field '$constantName', e=$e", e)
        }
    }

    private fun mutableConstant(methodName: String): Decimal<*> {
        try {
            val clazz = Class.forName(mutableClassName)
            return clazz.getMethod(methodName).invoke(null) as Decimal<*>
        } catch (e: InvocationTargetException) {
            if (e.targetException is RuntimeException) {
                throw (e.targetException as RuntimeException)
            }
            throw RuntimeException("could not invoke static method '$methodName', e=$e", e)
        } catch (e: Exception) {
            throw RuntimeException("could not invoke static method '$methodName', e=$e", e)
        }
    }

    private fun <V : Any> immutableValueOf(paramType: KClass<V>, paramValue: V): Decimal<*> {
        try {
            val kClass = Class.forName(immutableClassName).kotlin
            val clazz = kClass.companionObject!!
            val companionObjectInstance = kClass.companionObjectInstance
            val method = clazz.functions.find {
                it.name == "valueOf" && it.parameters.size == 2 && it.parameters[1].type.classifier == paramType
            }
            return method?.call(companionObjectInstance, paramValue) as Decimal<*>
        } catch (e: InvocationTargetException) {
            if (e.cause is RuntimeException) {
                throw e.cause as RuntimeException
            }
            throw RuntimeException("could not invoke valueOf method, e=$e", e)
        } catch (e: Exception) {
            throw RuntimeException("could not invoke valueOf method, e=$e", e)
        }
    }
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: scale={0}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in Scales.VALUES) {
                data.add(arrayOf(s, s.getDefaultArithmetic()))
            }
            return data
        }
    }
}
