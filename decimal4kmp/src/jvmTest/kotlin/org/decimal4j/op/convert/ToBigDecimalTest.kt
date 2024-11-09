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
import org.decimal4j.op.AbstractDecimalToAnyTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import org.decimal4j.api.RoundingMode
import org.decimal4j.api.toJavaRoundingMode

/**
 * Unit test for [Decimal.toBigDecimal] and [Decimal.toBigDecimal]
 */
@RunWith(Parameterized::class)
class ToBigDecimalTest(
    scaleMetrics: ScaleMetrics?,
    private val newScale: Int?,
    rounding: RoundingMode?,
    arithmetic: DecimalArithmetic
) :
    AbstractDecimalToAnyTest<BigDecimal?>(arithmetic) {
    override fun operation(): String {
        return "toBigDecimal"
    }

    override fun getRandomTestCount(): Int {
        return 100
    }

    override fun expectedResult(operand: BigDecimal): BigDecimal {
        if (newScale == null) {
            return operand
        }
        return operand.setScale(newScale, roundingMode.toJavaRoundingMode())
    }

    override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): BigDecimal {
        if (newScale == null) {
            return operand.toBigDecimal()
        }
        return operand.toBigDecimal(newScale, roundingMode)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: scale={0}, newScale={1}, rounding={2}")
        fun data(): Iterable<Array<Any?>> {
            val data: MutableList<Array<Any?>> = ArrayList()
            for (s in TestSettings.SCALES) {
                data.add(arrayOf(s, null, RoundingMode.DOWN, s.getDefaultArithmetic()))
                for (scale in -100..99) {
                    for (rounding in TestSettings.UNCHECKED_ROUNDING_MODES) {
                        data.add(arrayOf(s, scale, rounding, s.getArithmetic(rounding)))
                    }
                }
            }
            return data
        }
    }
}
