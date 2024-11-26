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
package org.decimal4j.op

import org.decimal4j.api.Decimal
import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.ArithmeticResult
import org.decimal4j.test.ArithmeticResult.Companion.forException
import org.decimal4j.test.ArithmeticResult.Companion.forResult
import java.math.BigDecimal

/**
 * Base class for tests comparing the result of some binary operation of the
 * [Decimal] with a Decimal argument and an integer argument. The expected
 * result is produced by the equivalent operation of the [BigDecimal]. The
 * test operand values are created based on random long values.
 */
abstract class AbstractDecimalIntToDecimalTest
/**
 * Constructor with arithemtics determining scale, rounding mode and
 * overflow policy.
 *
 * @param arithmetic
 * the arithmetic determining scale, rounding mode and overlfow
 * policy
 */
    (arithmetic: DecimalArithmetic) : AbstractRandomAndSpecialValueTest(arithmetic) {
    protected abstract fun expectedResult(a: BigDecimal, b: Int): BigDecimal

    protected abstract fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Int): Decimal<S>

    protected abstract fun <S : ScaleMetrics> randomIntOperand(decimalOperand: Decimal<S>): Int

    protected abstract fun getSpecialIntOperands(): IntArray

    override fun <S : ScaleMetrics> runRandomTest(scaleMetrics: S, index: Int) {
        val decimalOperand = randomDecimal(scaleMetrics)
        runTest(scaleMetrics, "[$index]", decimalOperand, randomIntOperand(decimalOperand))
    }

    override fun <S : ScaleMetrics> runSpecialValueTest(scaleMetrics: S) {
        val specialValues = getSpecialValues(scaleMetrics)
        val specialIntOperands = getSpecialIntOperands()
        for (i in specialValues.indices) {
            for (j in specialIntOperands.indices) {
                runTest(
                    scaleMetrics,
                    "[$i, $j]", newDecimal(scaleMetrics, specialValues[i]), specialIntOperands[j]
                )
            }
        }
    }

    protected open fun <S : ScaleMetrics> runTest(scaleMetrics: S, name: String, dOperandA: Decimal<S>, b: Int) {
        val messagePrefix = javaClass.simpleName + name + ": " + dOperandA + " " + operation() + " " + b

        val bdOperandA = toBigDecimal(dOperandA)

        //expected
        var expected: ArithmeticResult<Long>
        try {
            expected = forResult(arithmetic, expectedResult(bdOperandA, b))
        } catch (e: IllegalArgumentException) {
            expected = forException(e)
        } catch (e: ArithmeticException) {
            expected = forException(e)
        }

        //actual
        var actual: ArithmeticResult<Long>
        try {
            actual = forResult(actualResult(dOperandA, b))
        } catch (e: IllegalArgumentException) {
            actual = forException(e)
        } catch (e: ArithmeticException) {
            actual = forException(e)
        }


        //assert
        actual.assertEquivalentTo(expected, messagePrefix)
    }
}
