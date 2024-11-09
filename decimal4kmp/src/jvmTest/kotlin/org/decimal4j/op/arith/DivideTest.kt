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
import org.decimal4j.truncate.TruncationPolicy
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import org.decimal4j.api.RoundingMode

/**
 * Unit test for [Decimal.divide]
 */
@RunWith(Parameterized::class)
open class DivideTest(
    scaleMetrics: ScaleMetrics?,
    truncationPolicy: TruncationPolicy?,
    arithmetic: DecimalArithmetic
) :
    AbstractDecimalDecimalToDecimalTest(arithmetic) {
    override fun runSpecialValueTest() {
        super.runSpecialValueTest()
    }

    override fun operation(): String {
        return "/"
    }

    override fun expectedResult(a: BigDecimal, b: BigDecimal): BigDecimal {
        return a.divide(b, mathContextLong128)
    }

    override fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Decimal<S>): Decimal<S> {
        if (isStandardTruncationPolicy && RND.nextBoolean()) {
            return a.divide(b)
        }
        if (isUnchecked && RND.nextBoolean()) {
            return a.divide(b, roundingMode)
        }
        if (isUnchecked && roundingMode == RoundingMode.DOWN && RND.nextBoolean()) {
            return a.divideTruncate(b)
        }
        if (!isUnchecked && roundingMode == RoundingMode.UNNECESSARY && RND.nextBoolean()) {
            return a.divideExact(b)
        }
        return a.divide(b, truncationPolicy)
    }

    companion object {
        @JvmStatic @Parameterized.Parameters(name = "{index}: {0}, {1}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                for (tp in TestSettings.POLICIES) {
                    val arith = s.getArithmetic(tp)
                    data.add(arrayOf(s, tp, arith))
                }
            }
            return data
        }
    }
}
