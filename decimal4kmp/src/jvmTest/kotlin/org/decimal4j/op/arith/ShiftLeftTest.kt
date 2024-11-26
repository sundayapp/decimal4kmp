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
import java.math.BigInteger
import org.decimal4j.truncate.RoundingMode
import org.decimal4j.arithmetic.toJavaRoundingMode
import kotlin.math.max
import kotlin.math.min

/**
 * Unit test for [Decimal.shiftLeft]
 */
@RunWith(Parameterized::class)
class ShiftLeftTest(scaleMetrics: ScaleMetrics?, truncationPolicy: TruncationPolicy?, arithmetic: DecimalArithmetic) :
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
        //1..100 and negatives
        for (i in 1..99) {
            exp.add(i)
            exp.add(-i)
        }
        //zero
        exp.add(0)
        exp.add(java.lang.Long.SIZE - 1)
        exp.add(java.lang.Long.SIZE)
        exp.add(java.lang.Long.SIZE + 1)
        exp.add(-(java.lang.Long.SIZE - 1))
        exp.add(-java.lang.Long.SIZE)
        exp.add(-(java.lang.Long.SIZE + 1))
        exp.add(Int.MAX_VALUE)
        exp.add(Int.MIN_VALUE)

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
        return "<<"
    }

    override fun expectedResult(a: BigDecimal, b: Int): BigDecimal {
        val exp = max(
            min(b.toDouble(), (java.lang.Long.SIZE + 1).toDouble()),
            (-java.lang.Long.SIZE - 1).toDouble()
        ).toInt()
        if (b >= 0 || roundingMode == RoundingMode.FLOOR) {
            return BigDecimal(a.unscaledValue().shiftLeft(exp), a.scale())
        }
        //		System.out.println(a + "<<" + b + ": " + exp);
        return a.divide(BigDecimal(BigInteger.ONE.shiftLeft(-exp)), getArithmeticScale(), roundingMode.toJavaRoundingMode())
        //		return new BigDecimal(a.unscaledValue().shiftRight(exp), a.scale());
    }

    override fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Int): Decimal<S> {
        return if (roundingMode == RoundingMode.FLOOR && isUnchecked && RND.nextBoolean()) {
            a.shiftLeft(b)
        } else {
            if (isUnchecked && RND.nextBoolean()) {
                a.shiftLeft(b, roundingMode)
            } else {
                a.shiftLeft(b, truncationPolicy)
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
