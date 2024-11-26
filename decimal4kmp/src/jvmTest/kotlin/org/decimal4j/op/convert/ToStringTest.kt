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
import org.decimal4j.truncate.OverflowMode
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import org.decimal4j.truncate.RoundingMode

/**
 * Unit test for [Decimal.toString]
 */
@RunWith(Parameterized::class)
class ToStringTest(scaleMetrics: ScaleMetrics?, arithmetic: DecimalArithmetic) :
    AbstractDecimalToAnyTest<String?>(arithmetic) {
    override fun operation(): String {
        return "toString"
    }

    override fun expectedResult(operand: BigDecimal): String {
        return operand.toPlainString()
    }

    override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): String {
        when (RND.nextInt(5)) {
            0 -> return operand.toString()
            1 ->                //Scale.toString(..)
                return scaleMetrics.toString(operand.unscaledValue())

            2 -> {
                //use appendable version
                val sb = StringBuilder()
                arithmetic.toString(operand.unscaledValue(), sb)
                return sb.toString()
            }

            3 -> {
                //use appendable version with some existing string
                val sb = StringBuilder()
                val prefix = STRING.substring(0, RND.nextInt(STRING.length))
                sb.append(prefix)
                arithmetic.toString(operand.unscaledValue(), sb)
                return sb.substring(prefix.length)
            }

            4 -> {
                //use appendable version for checked arithmetic
                val sb = StringBuilder()
                arithmetic.deriveArithmetic(OverflowMode.CHECKED).toString(operand.unscaledValue(), sb)
                return sb.toString()
            }

            else -> {
                val sb = StringBuilder()
                arithmetic.deriveArithmetic(OverflowMode.CHECKED).toString(operand.unscaledValue(), sb)
                return sb.toString()
            }
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: scale={0}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                data.add(arrayOf(s, s.getArithmetic(RoundingMode.DOWN)))
            }
            return data
        }

        private const val STRING = "BLABLABLKJSLDFJLKJOI_)$(@U)DKSLDFLKJSLKXCMFREWOKLRJT"
    }
}
