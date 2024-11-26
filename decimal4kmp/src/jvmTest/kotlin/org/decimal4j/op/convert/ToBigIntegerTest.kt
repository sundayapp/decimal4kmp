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

import org.decimal4j.api.BigIntegerExtension.toBigInteger
import org.decimal4j.api.BigIntegerExtension.toBigIntegerExact
import org.decimal4j.api.Decimal
import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.op.AbstractDecimalToAnyTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import java.math.BigInteger
import org.decimal4j.truncate.RoundingMode
import org.decimal4j.arithmetic.toJavaRoundingMode

/**
 * Unit test for [Decimal.toBigInteger], [Decimal.toBigIntegerExact]
 * and [Decimal.toBigInteger]
 */
@RunWith(Parameterized::class)
class ToBigIntegerTest(
    scaleMetrics: ScaleMetrics?,
    rounding: RoundingMode?,
    private val exact: Boolean,
    arithmetic: DecimalArithmetic
) :
    AbstractDecimalToAnyTest<BigInteger?>(arithmetic) {
    override fun operation(): String {
        return if (exact) "toBigIntegerExact" else "toBigInteger"
    }

    override fun expectedResult(operand: BigDecimal): BigInteger {
        if (exact) {
            return operand.toBigIntegerExact()
        }
        if (isRoundingDown && RND.nextBoolean()) {
            return operand.toBigInteger()
        }
        return operand.setScale(0, roundingMode.toJavaRoundingMode()).toBigInteger()
    }

    override fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): BigInteger {
        if (exact) {
            return operand.toBigIntegerExact()
        }
        if (isRoundingDown && RND.nextBoolean()) {
            return operand.toBigInteger()
        }
        return operand.toBigInteger(roundingMode)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: scale={0}, rounding={1}, exact={2}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                data.add(arrayOf(s, RoundingMode.DOWN, true, s.getDefaultArithmetic()))
                for (rounding in TestSettings.UNCHECKED_ROUNDING_MODES) {
                    data.add(arrayOf(s, rounding, false, s.getArithmetic(rounding)))
                }
            }
            return data
        }
    }
}
