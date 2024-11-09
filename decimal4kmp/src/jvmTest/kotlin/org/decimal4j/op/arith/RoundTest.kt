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
import org.decimal4j.api.toJavaRoundingMode
import org.decimal4j.op.AbstractDecimalToDecimalTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.decimal4j.truncate.TruncationPolicy
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal

/**
 * Unit test for [Decimal.pow]
 */
@RunWith(Parameterized::class)
class RoundTest(
    scaleMetrics: ScaleMetrics?,
    private val precision: Int,
    truncationPolicy: TruncationPolicy?,
    arithmetic: DecimalArithmetic
) :
    AbstractDecimalToDecimalTest(arithmetic) {
    override fun getRandomTestCount(): Int {
        return 1000
    }

    override fun operation(): String {
        return "round"
    }

    override fun expectedResult(operand: BigDecimal): BigDecimal {
        if (getArithmeticScale() - precision <= 18) {
            return operand.setScale(precision, roundingMode.toJavaRoundingMode()).setScale(getArithmeticScale(), roundingMode.toJavaRoundingMode())
        }
        throw IllegalArgumentException("scale - precision must be <= 18 but was " + (getArithmeticScale() - precision) + " for scale=" + getArithmeticScale() + " and precision=" + precision)
    }

    override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Decimal<S> {
        return if (isStandardTruncationPolicy && RND.nextBoolean()) {
            operand.round(precision)
        } else {
            if (isUnchecked && RND.nextBoolean()) {
                operand.round(precision, roundingMode)
            } else {
                operand.round(precision, truncationPolicy)
            }
        }
    }

    companion object {
		@JvmStatic @Parameterized.Parameters(name = "{index}: {0}, precision={1}, {2}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                val scale = s.getScale()
                for (precision in (scale - 18) - 1..(scale + 1)) {
                    for (tp in TestSettings.POLICIES) {
                        val arith = s.getArithmetic(tp)
                        data.add(arrayOf(s, precision, tp, arith))
                    }
                }
            }
            return data
        }
    }
}
