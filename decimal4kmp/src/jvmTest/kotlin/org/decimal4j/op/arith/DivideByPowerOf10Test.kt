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
import org.decimal4j.op.AbstractDecimalIntToDecimalTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.decimal4j.truncate.TruncationPolicy
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import kotlin.math.abs

/**
 * Unit test for [Decimal.divideByPowerOfTen]
 */
@RunWith(Parameterized::class)
class DivideByPowerOf10Test(
    scaleMetrics: ScaleMetrics?,
    truncationPolicy: TruncationPolicy?,
    arithmetic: DecimalArithmetic
) :
    AbstractDecimalIntToDecimalTest(arithmetic) {
    //	private static final int MAX_EXPONENT = 999999999;
    override fun <S : ScaleMetrics> randomIntOperand(decimalOperand: Decimal<S>): Int {
        return RND.nextInt(200) - 100
    }

    override fun getRandomTestCount(): Int {
        return 1000
    }

    override fun getSpecialIntOperands(): IntArray {
        val exp: MutableSet<Int> = sortedSetOf()
        //1..9 and negatives
        for (i in 1..9) {
            exp.add(i)
            exp.add(-i)
        }
        //10..50 in steps of 10 and negatives
        var i = 10
        while (i <= 50) {
            exp.add(i)
            exp.add(-i)
            i += 10
        }
        //100 and -100
        exp.add(100)
        exp.add(-100)
        //zero
        exp.add(0)

        //convert to array
        val result = IntArray(exp.size)
        var index = 0
        for (`val` in exp) {
            result[index] = `val`
            index++
        }
        return result
    }

    override fun operation(): String {
        return "/10^"
    }

    override fun expectedResult(a: BigDecimal, b: Int): BigDecimal {
        val multiplier = BigDecimal.TEN.pow(abs(b.toDouble()).toInt())
        return if (b >= 0) a.divide(multiplier, mathContextLong128) else a.multiply(multiplier)
    }

    override fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Int): Decimal<S> {
        return if (isStandardTruncationPolicy && RND.nextBoolean()) {
            a.divideByPowerOfTen(b)
        } else {
            if (isUnchecked && RND.nextBoolean()) {
                a.divideByPowerOfTen(b, roundingMode)
            } else {
                a.divideByPowerOfTen(b, truncationPolicy)
            }
        }
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
