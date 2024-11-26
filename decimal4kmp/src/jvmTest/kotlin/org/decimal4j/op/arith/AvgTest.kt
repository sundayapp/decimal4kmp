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
import org.decimal4j.op.AbstractDecimalDecimalToDecimalTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.decimal4j.truncate.CheckedRounding
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import org.decimal4j.truncate.RoundingMode
import org.decimal4j.arithmetic.toJavaRoundingMode

/**
 * Unit test for [Decimal.avg] and [Decimal.avg]
 */
@RunWith(Parameterized::class)
class AvgTest(scaleMetrics: ScaleMetrics?, roundingMode: RoundingMode?, arithmetic: DecimalArithmetic) :
    AbstractDecimalDecimalToDecimalTest(arithmetic) {
    override fun operation(): String {
        return "avg"
    }

    override fun expectedResult(a: BigDecimal, b: BigDecimal): BigDecimal {
        return a.add(b).divide(TWO, roundingMode.toJavaRoundingMode())
    }

    override fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Decimal<S>): Decimal<S> {
        if (isStandardTruncationPolicy && RND.nextBoolean()) {
            return a.avg(b)
        }
        if (RND.nextBoolean()) {
            return a.avg(b, roundingMode)
        }
        //also test checked arithmetic otherwise this is not covered
        val checkedAith = a.scaleMetrics.getArithmetic(CheckedRounding.valueOf(roundingMode))
        return newDecimal(a.scaleMetrics, checkedAith.avg(a.unscaledValue(), b.unscaledValue()))
    }

    companion object {
        private val TWO: BigDecimal = BigDecimal.valueOf(2)

        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}, {1}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                for (rm in TestSettings.UNCHECKED_ROUNDING_MODES) {
                    data.add(arrayOf(s, rm, s.getArithmetic(rm)))
                }
            }
            return data
        }
    }
}
