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

import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.arithmetic.toJavaRoundingMode
import org.decimal4j.op.util.FloatAndDoubleUtil.doubleToBigDecimal
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.decimal4j.truncate.TruncationPolicy
import org.junit.runners.Parameterized
import java.math.BigDecimal
import java.math.MathContext
import org.decimal4j.truncate.RoundingMode

/**
 * Base class for unit tests with a double operand.
 */
abstract class AbstractDecimalDoubleToDecimalTest(
    s: ScaleMetrics?,
    tp: TruncationPolicy?,
    arithmetic: DecimalArithmetic
) :
    AbstractDecimalDoubleValueToDecimalTest(arithmetic) {
    protected val MATH_CONTEXT_DOUBLE_TO_LONG_64: MathContext = MathContext(19, RoundingMode.HALF_EVEN.toJavaRoundingMode())

    protected fun toBigDecimal(operand: Double): BigDecimal {
        return doubleToBigDecimal(operand, getArithmeticScale(), roundingMode).setScale(
            getArithmeticScale(),
            roundingMode.toJavaRoundingMode()
        )
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}, {1}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                for (tp in TestSettings.CHECKED_POLICIES) {
                    val arith = s.getArithmetic(tp)
                    data.add(arrayOf(s, tp, arith))
                }
            }
            return data
        }
    }
}
