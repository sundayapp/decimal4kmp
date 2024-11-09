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
import org.decimal4j.op.AbstractDecimalUnscaledToDecimalTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.TruncationPolicy
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal

/**
 * Unit test for [Decimal.addUnscaled]
 */
@RunWith(Parameterized::class)
class SubtractUnscaledTest(sm: ScaleMetrics?, tp: TruncationPolicy?, scale: Int, arithmetic: DecimalArithmetic) :
    AbstractDecimalUnscaledToDecimalTest(sm, tp, scale, arithmetic) {
    override fun operation(): String {
        return "- 10^" + (-scale) + " *"
    }

    override fun expectedResult(a: BigDecimal, b: Long): BigDecimal {
        return a.subtract(toBigDecimal(b))
    }

    override fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Long): Decimal<S> {
        if (scale == getArithmeticScale() && RND.nextBoolean()) {
            if (isUnchecked && RND.nextBoolean()) {
                return a.subtractUnscaled(b)
            }
            return a.subtractUnscaled(b, overflowMode)
        }
        if (isStandardTruncationPolicy && RND.nextBoolean()) {
            return a.subtractUnscaled(b, scale)
        }
        if (isUnchecked && RND.nextBoolean()) {
            return a.subtractUnscaled(b, scale, roundingMode)
        }
        return a.subtractUnscaled(b, scale, truncationPolicy)
    }
}
