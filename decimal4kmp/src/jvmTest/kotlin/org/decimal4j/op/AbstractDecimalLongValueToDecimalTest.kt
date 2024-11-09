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
 * [Decimal] with a Decimal argument and a long argument. The expected
 * result is produced by the equivalent operation of the [BigDecimal]. The
 * test operand values are created based on random long values.
 */
abstract class AbstractDecimalLongValueToDecimalTest
/**
 * Constructor with arithemtics determining scale, rounding mode and
 * overflow policy.
 *
 * @param arithmetic
 * the arithmetic determining scale, rounding mode and overlfow
 * policy
 */
    (arithmetic: DecimalArithmetic) : AbstractRandomAndSpecialValueTest(arithmetic) {
    protected abstract fun expectedResult(a: BigDecimal, b: Long): BigDecimal

    protected abstract fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Long): Decimal<S>

    protected fun nextLongOperand(): Long {
        return nextLongOrInt()
    }

    protected abstract fun getSpecialLongOperands(): LongArray

    override fun <S : ScaleMetrics> runRandomTest(scaleMetrics: S, index: Int) {
        runTest(scaleMetrics, "[$index]", randomDecimal(scaleMetrics), nextLongOperand())
    }

    override fun <S : ScaleMetrics> runSpecialValueTest(scaleMetrics: S) {
        val specialValues = getSpecialValues(scaleMetrics)
        val specialLongOperands = getSpecialLongOperands()
        for (i in specialValues.indices) {
            for (j in specialLongOperands.indices) {
                runTest(
                    scaleMetrics,
                    "[$i, $j]", newDecimal(scaleMetrics, specialValues[i]), specialLongOperands[j]
                )
            }
        }
    }

    protected fun <S : ScaleMetrics> runTest(scaleMetrics: S, name: String, dOperandA: Decimal<S>, b: Long) {
        val messagePrefix = javaClass.simpleName + name + ": " + dOperandA + " " + operation() + " " + b

        val bdOperandA = toBigDecimal(dOperandA)

        //expected
        var expected: ArithmeticResult<Long>
        try {
            expected = forResult(arithmetic, expectedResult(bdOperandA, b)!!)
        } catch (e: ArithmeticException) {
            expected = forException(e)
        } catch (e: IllegalArgumentException) {
            expected = forException(e)
        }

        //actual
        var actual: ArithmeticResult<Long>
        try {
            actual = forResult(actualResult(dOperandA, b)!!)
        } catch (e: ArithmeticException) {
            actual = forException(e)
        } catch (e: IllegalArgumentException) {
            actual = forException(e)
        }


        //assert
        actual.assertEquivalentTo(expected, messagePrefix)
    }
}
