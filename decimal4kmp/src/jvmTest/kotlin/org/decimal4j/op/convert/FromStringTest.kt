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
import org.decimal4j.op.AbstractRandomAndSpecialValueTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.ArithmeticResult
import org.decimal4j.test.ArithmeticResult.Companion.forException
import org.decimal4j.test.ArithmeticResult.Companion.forResult
import org.decimal4j.test.TestSettings
import org.decimal4j.truncate.OverflowMode
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.lang.reflect.InvocationTargetException
import java.math.BigDecimal
import org.decimal4j.api.RoundingMode
import org.decimal4j.api.toJavaRoundingMode

/**
 * Test [DecimalArithmetic.parse] via
 * [DecimalFactory.parse], [MutableDecimal.set] and
 * the static `valueOf(String)` methods of the Immutable Decimal
 * implementations.
 */
@RunWith(Parameterized::class)
class FromStringTest(s: ScaleMetrics?, mode: RoundingMode?, arithmetic: DecimalArithmetic) :
    AbstractRandomAndSpecialValueTest(arithmetic) {
    override fun operation(): String {
        return "fromString"
    }

    protected fun randomStringOperand(): String {
        val s = RND.nextLong().toString()
        return toDecimalString(s, RND.nextInt(s.length + 1))
    }

    protected val specialStringOperands: Array<String>
        get() {
            val values: MutableSet<String> = LinkedHashSet()
            for (value in TestSettings.TEST_CASES.getSpecialValuesFor(scaleMetrics)) {
                val s = value.toString()
                for (i in 0..s.length) {
                    val decimalString = toDecimalString(s, i)
                    values.add(decimalString)
                    if (value > 0) {
                        values.add("+$decimalString")
                    }
                    if (decimalString.indexOf('.') >= 0) {
                        values.add(decimalString + "0")
                        values.add(decimalString + "5")
                        values.add(decimalString + "9")
                        values.add(decimalString + "0000000000000000000000000000000")
                        values.add(decimalString + "0000000000000000000000000000001")
                        values.add(decimalString + "4999999999999999999999999999999")
                        values.add(decimalString + "5000000000000000000000000000000")
                        values.add(decimalString + "5000000000000000000000000000001")
                        values.add(decimalString + "9999999999999999999999999999999")
                    }
                    // some invalid
                    values.add(decimalString + "A")
                    values.add(decimalString + "000000000000000000000000000000Z")
                }
            }
            // some potential overflow values
            values.add("9223372036854775808") // Long.MAX_VALUE + 1
            values.add("-9223372036854775809") // Long.MIN_VALUE - 1
            // Long.MAX_VALUE + 1, with decimal point
            values.add(
                ("9223372036854775808".substring(0, 19 - getArithmeticScale()) + "."
                        + "9223372036854775808".substring(19 - getArithmeticScale()))
            )
            // Long.MIN_VALUE - 1, with decimal point
            values.add(
                ("-9223372036854775809".substring(0, 20 - getArithmeticScale()) + "."
                        + "-9223372036854775809".substring(20 - getArithmeticScale()))
            )
            values.add(Long.MAX_VALUE.toString() + "0")
            values.add(Long.MAX_VALUE.toString() + "0.0")
            values.add(Long.MAX_VALUE.toString() + ".1")
            values.add(Long.MAX_VALUE.toString() + ".5")
            values.add(Long.MAX_VALUE.toString() + ".9")
            values.add(Long.MIN_VALUE.toString() + "0")
            values.add(Long.MIN_VALUE.toString() + "0.0")
            values.add(Long.MIN_VALUE.toString() + ".1")
            values.add(Long.MIN_VALUE.toString() + ".5")
            values.add(Long.MIN_VALUE.toString() + ".9")
            // some invalid values
            values.add("")
            values.add(" 1")
            values.add("1 ")
            values.add(" 1.0")
            values.add("1.0 ")
            values.add("A")
            values.add("+-1.0")
            values.add("--1.0")
            values.add("++1.0")
            values.add("1.")
            values.add("+1.")
            values.add("-1.")
            values.add("+1.A")
            values.add("-1.A")
            return values.toTypedArray<String>()
        }

    override fun <S : ScaleMetrics> runRandomTest(scaleMetrics: S, index: Int) {
        runTest(scaleMetrics, "[$index]", randomStringOperand())
    }

    override fun <S : ScaleMetrics> runSpecialValueTest(scaleMetrics: S) {
        val specialOperands = specialStringOperands
        for (i in specialOperands.indices) {
            runTest(scaleMetrics, "[$i]", specialOperands[i])
        }
    }

    protected fun <S : ScaleMetrics> runTest(scaleMetrics: S, name: String, operand: String) {
        val messagePrefix = javaClass.simpleName + name + ": " + operation() + " " + operand

        // expected
        var expected: ArithmeticResult<Long>
        try {
            expected = forResult(arithmetic, expectedResult(operand))
        } catch (e: ArithmeticException) {
            expected = forException(e)
        } catch (e: IllegalArgumentException) {
            expected = forException(e)
        } catch (e: NullPointerException) {
            expected = forException(e)
        }

        // actual
        var actual: ArithmeticResult<Long>
        try {
            actual = forResult(actualResult(scaleMetrics, operand))
        } catch (e: ArithmeticException) {
            actual = forException(e)
        } catch (e: IllegalArgumentException) {
            actual = forException(e)
        } catch (e: NullPointerException) {
            actual = forException(e)
        }

        // assert
        actual.assertEquivalentTo(expected, messagePrefix)
    }

    protected fun expectedResult(operand: String?): BigDecimal {
        val result = BigDecimal(operand).setScale(getArithmeticScale(), roundingMode.toJavaRoundingMode())
        if (result.unscaledValue().bitLength() > 63) {
            throw NumberFormatException("Overflow: $result")
        }
        return result
    }

    protected fun <S : ScaleMetrics> actualResult(scaleMetrics: S, operand: String): Decimal<S> {
        when (RND.nextInt(6)) {
            0 ->            // Factory, immutable
                return if (isRoundingDefault && RND.nextBoolean()) {
                    getDecimalFactory(scaleMetrics).parse(operand)
                } else {
                    getDecimalFactory(scaleMetrics).parse(operand, roundingMode)
                }

            1 ->            // Factory, mutable
                return if (isRoundingDefault && RND.nextBoolean()) {
                    getDecimalFactory(scaleMetrics).newMutable().set(operand)
                } else {
                    getDecimalFactory(scaleMetrics).newMutable().set(operand, roundingMode)
                }

            2 ->            // DecimalArithmetic API with String
                return if (RND.nextBoolean()) {
                    getDecimalFactory(scaleMetrics).valueOfUnscaled(arithmetic.parse(operand))
                } else {
                    getDecimalFactory(scaleMetrics).valueOfUnscaled(
                        arithmetic.deriveArithmetic(OverflowMode.CHECKED).parse(operand)
                    )
                }

            3 ->            // DecimalArithmetic API with CharSequence
                return if (RND.nextBoolean()) {
                    parseCharSequence(arithmetic, scaleMetrics, operand)
                } else {
                    parseCharSequence(arithmetic.deriveArithmetic(OverflowMode.CHECKED), scaleMetrics, operand)
                }

            4 -> {
                // String constructor
                // NOTE: immutable has no constructor with rounding mode param
                if (isRoundingDefault) {
                    if (RND.nextBoolean()) {
                        return newImmutableInstance(scaleMetrics, operand)
                    }
                    return newMutableInstance(scaleMetrics, operand)
                }
                // Immutable, valueOf method
                return valueOf(scaleMetrics, operand)
            }

            5 ->
                return valueOf(scaleMetrics, operand)

            else ->
                return valueOf(scaleMetrics, operand)
        }
    }

    private fun <S : ScaleMetrics> newImmutableInstance(scaleMetrics: S, operand: String?): Decimal<S> {
        return newInstance(scaleMetrics, immutableClassName, operand)
    }

    private fun <S : ScaleMetrics> newMutableInstance(scaleMetrics: S, operand: String?): Decimal<S> {
        return newInstance(scaleMetrics, mutableClassName, operand)
    }

    private fun <S : ScaleMetrics> newInstance(scaleMetrics: S, className: String, operand: String?): Decimal<S> {
        try {
            val clazz = Class.forName(className) as Class<Decimal<S>>
            return clazz.getConstructor(String::class.java).newInstance(operand)
        } catch (e: InvocationTargetException) {
            if (e.targetException is RuntimeException) {
                throw (e.targetException as RuntimeException)
            }
            throw RuntimeException("could not invoke constructor, e=$e", e)
        } catch (e: Exception) {
            throw RuntimeException("could not invoke constructor, e=$e", e)
        }
    }

    private fun <S : ScaleMetrics> parseCharSequence(
        arith: DecimalArithmetic,
        scaleMetrics: S,
        operand: String?
    ): Decimal<S> {
        val charSeq = StringBuilder(operand)
        //prepend and append some crap chars
        val blabla = "BLABLA"
        val prefix = blabla.substring(0, RND.nextInt(blabla.length))
        val postfix = blabla.substring(0, RND.nextInt(blabla.length))
        charSeq.insert(0, prefix).append(postfix)
        val start = prefix.length
        val end = charSeq.length - postfix.length
        return getDecimalFactory(scaleMetrics).valueOfUnscaled(arith.parse(charSeq, start, end))
    }

    private fun <S : ScaleMetrics> valueOf(scaleMetrics: S, operand: String?): Decimal<S> {
        try {
            val clazz = Class.forName(immutableClassName)
            return if (isRoundingDefault && RND.nextBoolean()) {
                clazz.getMethod("valueOf", String::class.java).invoke(null, operand) as Decimal<S>
            } else {
                clazz.getMethod("valueOf", String::class.java, RoundingMode::class.java).invoke(
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

        private fun toDecimalString(s: String, decimalIndex: Int): String {
            var decimalIndex = decimalIndex
            if (decimalIndex < 0 || decimalIndex >= s.length) {
                return s
            }
            if (decimalIndex == 0 && s.startsWith("-")) {
                decimalIndex++
            }
            return s.substring(0, decimalIndex) + "." + s.substring(decimalIndex)
        }
    }
}
