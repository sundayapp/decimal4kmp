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
import org.decimal4j.api.RoundingMode
import org.decimal4j.api.toJavaRoundingMode

/**
 * Unit test for [Decimal.longValue], [Decimal.longValueExact]
 * and [Decimal.longValue].
 */
@RunWith(Parameterized::class)
class LongValueTest(
    scaleMetrics: ScaleMetrics?,
    roundingMode: RoundingMode?,
    private val exact: Boolean,
    arithmetic: DecimalArithmetic
) :
    AbstractDecimalToAnyTest<Long?>(arithmetic) {
    override fun operation(): String {
        return if (exact) "longValueExact" else "longValue"
    }

    override fun expectedResult(operand: BigDecimal): Long {
        if (exact) {
            return operand.longValueExact()
        }
        if (isRoundingDown && RND.nextBoolean()) {
            return operand.toLong()
        }
        return operand.setScale(0, roundingMode.toJavaRoundingMode()).toLong()
    }

    override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Long {
        if (exact) {
            return operand.longValueExact()
        }
        if (isRoundingDown && RND.nextBoolean()) {
            return operand.longValue()
        }
        if (RND.nextBoolean()) {
            return operand.longValue(roundingMode)
        }
        //use arithmetic
        return if (RND.nextBoolean()) {
            arithmetic.toLong(operand.unscaledValue())
        } else {
            arithmetic.deriveArithmetic(OverflowMode.CHECKED).toLong(operand.unscaledValue())
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}, {1}, exact={2}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                data.add(arrayOf(s, RoundingMode.DOWN, true, s.getDefaultArithmetic()))
                for (mode in TestSettings.UNCHECKED_ROUNDING_MODES) {
                    data.add(arrayOf(s, mode, false, s.getArithmetic(mode)))
                }
            }
            return data
        }
    }
}
