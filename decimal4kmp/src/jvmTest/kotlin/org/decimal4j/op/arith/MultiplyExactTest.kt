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
package org.decimal4j.op.arith

import org.decimal4j.api.Decimal
import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.exact.Multiplier
import org.decimal4j.immutable.Decimal0f
import org.decimal4j.mutable.MutableDecimal0f
import org.decimal4j.op.AbstractRandomAndSpecialValueTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales
import org.decimal4j.test.ArithmeticResult
import org.decimal4j.test.TestSettings
import org.junit.Assert
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal

/**
 * Unit test for [Decimal.multiplyExact]
 */
@RunWith(Parameterized::class)
class MultiplyExactTest(private val scaleMetrics1: ScaleMetrics?, private val scaleMetrics2: ScaleMetrics, arithmetic: DecimalArithmetic) :
    AbstractRandomAndSpecialValueTest(arithmetic) {


    override fun operation(): String {
        return "*"
    }

    override fun <S : ScaleMetrics> runRandomTest(scaleMetrics: S, index: Int) {
        val dOpA = randomDecimal(scaleMetrics)
        val dOpB: Decimal<*> = randomDecimal(scaleMetrics2)
        runTest(scaleMetrics, "[$index]", dOpA, dOpB)
    }

    override fun <S : ScaleMetrics> runSpecialValueTest(scaleMetrics: S) {
        val specialValues = getSpecialValues(scaleMetrics)
        for (i in specialValues.indices) {
            for (j in specialValues.indices) {
                val dOpA = newDecimal(scaleMetrics, specialValues[i])
                val dOpB: Decimal<*> = newDecimal(scaleMetrics2, specialValues[j])
                runTest(scaleMetrics, "[$i, $j]", dOpA, dOpB)
            }
        }
    }

    protected fun <S : ScaleMetrics> runTest(scaleMetrics: S, name: String, dOpA: Decimal<S>, dOpB: Decimal<*>) {
        val messagePrefix = javaClass.simpleName + name + ": " + dOpA + " " + operation() + " " + dOpB

        val sumOfScales = dOpA.scale + dOpB.scale
        val bdOpA = toBigDecimal(dOpA)
        val bdOpB = toBigDecimal(dOpB)

        // expected
        var expected: ArithmeticResult<Long>
        try {
            require(sumOfScales <= Scales.MAX_SCALE) { "sum of scales exceeds max scale" }
            val exp = expectedResult(bdOpA, bdOpB)
            expected = ArithmeticResult.forResult(arithmetic.deriveArithmetic(sumOfScales), exp)
        } catch (e: IllegalArgumentException) {
            expected = ArithmeticResult.forException(e)
        } catch (e: ArithmeticException) {
            expected = ArithmeticResult.forException(e)
        }

        // actual
        var act: Decimal<*>? = null
        var actual: ArithmeticResult<Long>
        try {
            act = actualResult(dOpA, dOpB)
            actual = ArithmeticResult.forResult(act)
        } catch (e: IllegalArgumentException) {
            actual = ArithmeticResult.forException(e)
        } catch (e: ArithmeticException) {
            actual = ArithmeticResult.forException(e)
        } catch (e: Error) {
            throw e
        } catch (e: RuntimeException) {
            throw e
        } catch (e: Throwable) {
            throw RuntimeException(e)
        }

        // assert
        if (!actual.isException()) {
            Assert.assertEquals("result scale should be sum of scales", sumOfScales.toLong(), act!!.scale.toLong())
        }
        actual.assertEquivalentTo(expected, messagePrefix)
    }

    private fun expectedResult(a: BigDecimal, b: BigDecimal): BigDecimal {
        return a.multiply(b)
    }

    @Throws(Throwable::class)
    private fun actualResult(a: Decimal<*>, b: Decimal<*>): Decimal<*> {
        if (a.scale + b.scale > Scales.MAX_SCALE || RND.nextBoolean()) {
            return a.multiplyExact(b)
        }
        return multiplyExactTyped(a, b)
    }

    companion object {
		@JvmStatic @Parameterized.Parameters(name = "{index}: {0}, {1}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s1 in TestSettings.SCALES) {
                for (s2 in TestSettings.SCALES) {
                    data.add(arrayOf(s1, s2, s1.getDefaultCheckedArithmetic()))
                }
            }
            return data
        }

        @Throws(Throwable::class)
        private fun multiplyExactTyped(a: Decimal<*>, b: Decimal<*>): Decimal<*> {
            //convert to DecimalXf or MutableDecimalXf
            val factorA = if (RND.nextBoolean()) immutable(a) else mutable(a)
            val factorB = if (RND.nextBoolean()) immutable(b) else mutable(b)


            //MultibliableXf multipliable = ...
            val multipliable = multiplyExact(factorA)
            assertMultipliableObjectMethods(multipliable, factorA)

            try {
                if ((b.scale == 0) and b.isOne()) {
                    //return multipliable.getValue()
                    return getMultiplierValue(multipliable)
                }
                if (a.scale == b.scale) {
                    if (a.scale + b.scale <= Scales.MAX_SCALE && a == b) {
                        //return multipliable.square();
                        return exactSquare(multipliable)
                    }
                }
                //return multipliable.by(factorB)
                return exactMultiplyBy(factorA, factorB, multipliable)
            } catch (e: InvocationTargetException) {
                throw e.targetException
            }
        }

        @Throws(IllegalAccessException::class, InvocationTargetException::class, NoSuchMethodException::class)
        private fun exactMultiplyBy(factorA: Decimal<*>, factorB: Decimal<*>, multipliable: Any): Decimal<*> {
            if (factorA.scale == factorB.scale) {
                val result = multipliable.javaClass.getMethod("by", Decimal::class.java).invoke(multipliable, factorB)
                return Decimal::class.java.cast(result)
            }
            val result = multipliable.javaClass.getMethod("by", factorB.javaClass).invoke(multipliable, factorB)
            return Decimal::class.java.cast(result)
        }

        @Throws(
            IllegalAccessException::class,
            IllegalArgumentException::class,
            InvocationTargetException::class,
            NoSuchMethodException::class,
            SecurityException::class
        )
        private fun exactSquare(multipliable: Any): Decimal<*> {
            val result = multipliable.javaClass.getMethod("square").invoke(multipliable)
            return Decimal::class.java.cast(result)
        }

        @Throws(IllegalAccessException::class, InvocationTargetException::class, NoSuchMethodException::class)
        private fun getMultiplierValue(multipliable: Any): Decimal<*> {
            val result = multipliable.javaClass.getMethod("getValue").invoke(multipliable)
            return Decimal::class.java.cast(result)
        }

        @Throws(
            IllegalAccessException::class,
            InvocationTargetException::class,
            NoSuchMethodException::class,
            Exception::class
        )
        private fun assertMultipliableObjectMethods(multipliable: Any, value: Decimal<*>) {
            //perform some extra asserts here
            Assert.assertEquals(
                "multipliable.toString() should equal value.toString()",
                value.toString(),
                multipliable.toString()
            )
            Assert.assertEquals(
                "multipliable.hashCode() should equal value.hashCode()",
                value.hashCode().toLong(),
                multipliable.hashCode().toLong()
            )
            Assert.assertEquals("multipliable should be equal to itself", multipliable, multipliable)
            Assert.assertFalse("multipliable should not be equal to null", multipliable == null)
            Assert.assertNotEquals(
                "multipliable should not be equal to some other type of object",
                multipliable,
                "blabla"
            )
            Assert.assertEquals(
                "multipliable should be equal to another instance with same value", multipliable, multiplyExact(
                    mutable(value)
                )
            )
        }

        @Throws(IllegalAccessException::class, InvocationTargetException::class, NoSuchMethodException::class)
        private fun multiplyExact(factorA: Decimal<*>): Any {
            return if (RND.nextBoolean()) {
                //return factorA.multiplyExact()
                factorA.javaClass.getMethod("multiplyExact").invoke(factorA)
            } else {
                //return Multiplier.multiplyExact(factorA)
                Multiplier::class.java.getMethod("multiplyExact", factorA.javaClass).invoke(null, factorA)
            }
        }

        @Throws(Exception::class)
        private fun immutable(value: Decimal<*>): Decimal<*> {
            val className = Decimal0f::class.java.name.replace("0", value.scale.toString())
            val clazz = Class.forName(className)
            val instance = clazz.getMethod("valueOf", Decimal::class.java).invoke(null, value)
            return Decimal::class.java.cast(instance)
        }

        @Throws(Exception::class)
        private fun mutable(value: Decimal<*>): Decimal<*> {
            val className = MutableDecimal0f::class.java.name.replace("0", value.scale.toString())
            val clazz = Class.forName(className)
            val instance = clazz.getConstructor(Decimal::class.java).newInstance(value)
            return Decimal::class.java.cast(instance)
        }
    }
}
