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
import org.decimal4j.scale.Scales
import org.decimal4j.test.ArithmeticResult
import org.decimal4j.test.ArithmeticResult.Companion.forException
import org.decimal4j.test.ArithmeticResult.Companion.forResult
import java.math.BigDecimal

/**
 * Base class for tests asserting the result of some unary operation of the
 * [Decimal] with a wildcard `Decimal<?>` argument. The expected
 * result is produced by the equivalent operation of the [BigDecimal].
 */
abstract class AbstractUnknownDecimalToDecimalTest
/**
 * Constructor with arithemtics determining scale, rounding mode and
 * overflow policy.
 *
 * @param arithmetic
 * the arithmetic determining scale, rounding mode and overlfow
 * policy
 */
    (arithmetic: DecimalArithmetic) : AbstractRandomAndSpecialValueTest(arithmetic) {
    protected abstract fun expectedResult(operand: Decimal<*>): BigDecimal

    protected abstract fun <S : ScaleMetrics> actualResult(scaleMetrics: S, operand: Decimal<*>): Decimal<S>

    protected fun randomDecimalOperand(): Decimal<*> {
        val scaleMetrics = Scales.getScaleMetrics(RND.nextInt(1 + Scales.MAX_SCALE))
        return newDecimal(scaleMetrics, nextLongOrInt())
    }

    protected val specialDecimalOperands: Array<Decimal<*>>
        get() {
            val specials = getSpecialValues(scaleMetrics)
            val set: MutableSet<Decimal<*>> = HashSet()
            for (i in specials.indices) {
                for (scaleMetrics in Scales.VALUES) {
                    set.add(newDecimal(scaleMetrics, specials[i]))
                }
            }
            return set.toTypedArray<Decimal<*>>()
        }

    override fun <S : ScaleMetrics> runRandomTest(scaleMetrics: S, index: Int) {
        runTest(scaleMetrics, "[$index]", randomDecimalOperand())
    }

    override fun <S : ScaleMetrics> runSpecialValueTest(scaleMetrics: S) {
        val specialOperands = specialDecimalOperands
        for (i in specialOperands.indices) {
            runTest(scaleMetrics, "[$i]", specialOperands[i])
        }
    }

    protected fun <S : ScaleMetrics> runTest(scaleMetrics: S, name: String, operand: Decimal<*>) {
        val messagePrefix = javaClass.simpleName + name + ": " + operation() + " " + operand

        // expected
        var expected: ArithmeticResult<Long>
        try {
            expected = forResult(arithmetic, expectedResult(operand))
        } catch (e: ArithmeticException) {
            expected = forException(e)
        } catch (e: IllegalArgumentException) {
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
        }

        // assert
        actual.assertEquivalentTo(expected, messagePrefix)
    }
}
