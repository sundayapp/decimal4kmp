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
import org.decimal4j.arithmetic.Exceptions
import org.decimal4j.factory.Factories
import org.decimal4j.op.AbstractRandomAndSpecialValueTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales
import org.decimal4j.test.ArithmeticResult
import org.decimal4j.test.TestCases
import org.decimal4j.test.TestSettings
import org.decimal4j.truncate.OverflowMode
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import org.decimal4j.truncate.RoundingMode
import org.decimal4j.arithmetic.toJavaRoundingMode

/**
 * Unit test for the different scale methods of a Decimal such as
 * [Decimal.scale] etc.
 */
@RunWith(Parameterized::class)
class ScaleTest
/**
 * Constructor for parameterized test.
 *
 * @param sourceScale
 * the source scale metrics
 * @param rm
 * the rounding mode to apply
 * @param targetScale
 * the target scale
 * @param arithmetic
 * the arithmetic object passed to the constructor
 */(sourceScale: ScaleMetrics?, rm: RoundingMode?, private val targetScale: Int, arithmetic: DecimalArithmetic) :
    AbstractRandomAndSpecialValueTest(arithmetic) {
    override fun getRandomTestCount(): Int {
        return when (TestSettings.TEST_CASES) {
            TestCases.ALL -> 2000
            TestCases.LARGE -> 2000
            TestCases.STANDARD -> 1000
            TestCases.SMALL -> 1000
            TestCases.TINY -> 100
            else -> throw RuntimeException("unsupported: " + TestSettings.TEST_CASES)
        }
    }

    override fun operation(): String {
        return "scale"
    }

    override fun <S : ScaleMetrics> runRandomTest(scaleMetrics: S, index: Int) {
        val decimalOperand = randomDecimal(scaleMetrics)
        runTest(scaleMetrics, "[$index]", decimalOperand, targetScale)
    }

    override fun <S : ScaleMetrics> runSpecialValueTest(scaleMetrics: S) {
        val specialValues = getSpecialValues(scaleMetrics)
        for (i in specialValues.indices) {
            runTest(scaleMetrics, "[$i]", newDecimal(scaleMetrics, specialValues[i]), targetScale)
        }
    }

    protected fun <S : ScaleMetrics> runTest(scaleMetrics: S, name: String, dOperandA: Decimal<S>, targetScale: Int) {
        val messagePrefix = (javaClass.simpleName + name + ": " + dOperandA + " " + operation() + " "
                + targetScale)

        val bdOperandA = toBigDecimal(dOperandA)
        val resultArithmetic = arithmetic.deriveArithmetic(targetScale)

        // expected
        var expected = try {
            ArithmeticResult.forResult(resultArithmetic, expectedResult(bdOperandA, targetScale))
        } catch (e: IllegalArgumentException) {
            ArithmeticResult.forException(e)
        } catch (e: ArithmeticException) {
            ArithmeticResult.forException(e)
        }

        // actual
        var actual = try {
            ArithmeticResult.forResult(actualResult(dOperandA, targetScale))
        } catch (e: IllegalArgumentException) {
            ArithmeticResult.forException(e)
        } catch (e: ArithmeticException) {
            ArithmeticResult.forException(e)
        }

        // assert
        actual.assertEquivalentTo(expected, messagePrefix)
    }

    private fun expectedResult(a: BigDecimal, targetScale: Int): BigDecimal {
        if ((Scales.MIN_SCALE <= targetScale) and (targetScale <= Scales.MAX_SCALE)) {
            val result = a.setScale(targetScale, roundingMode.toJavaRoundingMode())
            if (result.unscaledValue().bitLength() > 63) {
                throw ArithmeticException("Overflow: $result")
            }
            return result
        }
        throw IllegalArgumentException("Illegal target scale: $targetScale")
    }

    private fun <S : ScaleMetrics> actualResult(a: Decimal<S>, targetScale: Int): Decimal<*> {
        val metrics = Scales.getScaleMetrics(targetScale)
        val mode = roundingMode
        if (isStandardTruncationPolicy && RND.nextBoolean()) {
            return if (RND.nextBoolean()) a.scale(targetScale) else a.scale(metrics)
        }
        if (RND.nextBoolean()) {
            return if (RND.nextBoolean()) a.scale(targetScale, mode) else a.scale(metrics, mode)
        }
        //use arithmetic.toUnscaled(..)
        val checkedArith = arithmetic.deriveArithmetic(OverflowMode.CHECKED)
        val factory = Factories.getDecimalFactory(targetScale)
        try {
            val unscaledResult = if (RND.nextBoolean()) {
                arithmetic.toUnscaled(a.unscaledValue(), targetScale)
            } else {
                checkedArith.toUnscaled(a.unscaledValue(), targetScale)
            }
            return factory.valueOfUnscaled(unscaledResult)
        } catch (e: IllegalArgumentException) {
            throw Exceptions.newArithmeticExceptionWithCause(e.message, e)
        }
    }

    companion object {
		@JvmStatic @Parameterized.Parameters(name = "{index}: {0}, {1}, targetScale={2}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                for (rm in TestSettings.UNCHECKED_ROUNDING_MODES) {
                    for (targetScale in 0 until Scales.MAX_SCALE) {
                        val arith = s.getArithmetic(rm)
                        data.add(arrayOf(s, rm, targetScale, arith))
                    }
                }
            }
            return data
        }
    }
}
