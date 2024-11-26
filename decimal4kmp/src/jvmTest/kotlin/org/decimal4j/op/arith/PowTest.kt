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
import org.decimal4j.test.ArithmeticResult
import org.decimal4j.test.TestCases
import org.decimal4j.test.TestSettings
import org.decimal4j.truncate.TruncationPolicy
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import org.decimal4j.truncate.RoundingMode
import org.decimal4j.arithmetic.toJavaRoundingMode
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min

/**
 * Unit test for [Decimal.pow]
 */
@RunWith(Parameterized::class)
class PowTest(scaleMetrics: ScaleMetrics?, truncationPolicy: TruncationPolicy?, arithmetic: DecimalArithmetic) :
    AbstractDecimalIntToDecimalTest(arithmetic) {
    override fun <S : ScaleMetrics> randomDecimal(scaleMetrics: S): Decimal<S> {
        val one = scaleMetrics.getScaleFactor()
        //		final long unscaled = one * (4 - RND.nextInt(9)) + one - RND.nextLong(2*one + 1);
        val unscaled = one * (8 - RND.nextInt(17)) + one - RND.nextLong(2 * one + 1)
        return newDecimal(scaleMetrics, unscaled)
    }

    override fun <S : ScaleMetrics> randomIntOperand(decimalOperand: Decimal<S>): Int {
        if (decimalOperand.isZero() || decimalOperand.isOne() || decimalOperand.isMinusOne()) {
            return MAX_POW_EXPONENT - RND.nextInt(2 * MAX_POW_EXPONENT + 1)
        }
        val posExp = RND.nextBoolean()
        val absBase = if (posExp) {
            abs(decimalOperand.doubleValue(RoundingMode.UP))
        } else {
            abs(1.0 / decimalOperand.doubleValue(RoundingMode.DOWN))
        }
        val maxPow = if (absBase >= 1) {
            (ln(decimalOperand.scaleMetrics.getMaxIntegerValue().toDouble()) / max(
                1e-10,
                ln(absBase)
            )).toInt()
        } else {
            -(64 / (ln(absBase) / ln(2.0))).toInt()
        }
        val pow =
            max(1.0, min(MAX_POW_EXPONENT.toDouble(), maxPow.toDouble())).toInt()
        return if (posExp) RND.nextInt(pow) else -RND.nextInt(pow)
    }

    @Test
    fun test3pow27() {
        val m = scaleMetrics
        runTest(m, "3^27", newDecimal(m, m.multiplyByScaleFactor(3)), 27)
    }

    @Test
    fun test3pow28() {
        val m = scaleMetrics
        runTest(m, "3^28", newDecimal(m, m.multiplyByScaleFactor(3)), 28)
    }

    @Test
    fun test3pow29() {
        val m = scaleMetrics
        runTest(m, "3^29", newDecimal(m, m.multiplyByScaleFactor(3)), 29)
    }

    @Test
    fun test10pow3() {
        val m = scaleMetrics
        runTest(m, "10^3", newDecimal(m, m.multiplyByScaleFactor(10)), 3)
    }

    @Test
    fun test100pow3() {
        val m = scaleMetrics
        runTest(m, "100^3", newDecimal(m, m.multiplyByScaleFactor(100)), 3)
    }

    @Test
    fun test2powNeg16() {
        val m = scaleMetrics
        runTest(m, "2^-16", newDecimal(m, m.multiplyByScaleFactor(3)), -16)
    }

    @Test
    fun test3powNeg10() {
        val m = scaleMetrics
        runTest(m, "3^-10", newDecimal(m, m.multiplyByScaleFactor(3)), -10)
    }

    @Test
    fun test3_1powNeg2() {
        val m = scaleMetrics
        runTest(m, "3.1^-2", newDecimal(m, m.multiplyByScaleFactor(3) + m.getScaleFactor() / 10), -2)
    }

    @Test
    fun test3_2powNeg2() {
        val m = scaleMetrics
        runTest(m, "3.2^-2", newDecimal(m, m.multiplyByScaleFactor(3) + m.getScaleFactor() / 5), -2)
    }

    @Test
    fun test0_84pow254() {
        if (getArithmeticScale() == 18) {
            val m = scaleMetrics
            runTest(m, "0.849628138173771215^254", newDecimal(m, 849628138173771215L), 254)
        }
    }

    @Test
    fun test0_9979046pow914() {
        if (getArithmeticScale() == 7) {
            val m = scaleMetrics
            runTest(m, "0.9979046^914", newDecimal(m, 9979046), 914)
        }
    }

    @Test
    fun testMinus0_943powMinus625() {
        if (getArithmeticScale() == 3) {
            val m = scaleMetrics
            runTest(m, "-0.943^-625", newDecimal(m, -943), -625)
        }
    }

    @Test
    fun test0_935341829powMinus342() {
        if (getArithmeticScale() == 9) {
            //fails with tolerance < 17 ULP
            val m = scaleMetrics
            runTest(m, "0.935341829^-342", newDecimal(m, 935341829), -342)
        }
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
        //extremes
//		exp.add(-999999999);
//		exp.add(999999999);
        //illegal exponents
        exp.add(-999999999 - 1)
        exp.add(999999999 + 1)
        exp.add(Int.MIN_VALUE)
        exp.add(Int.MAX_VALUE)

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
        return "^"
    }

    override fun <S : ScaleMetrics> runTest(scaleMetrics: S, name: String, dOperandA: Decimal<S>, b: Int) {
        val messagePrefix = javaClass.simpleName + name + ": " + dOperandA + " " + operation() + " " + b

        val bdOperandA = toBigDecimal(dOperandA)

        //expected
        val expected = try {
            ArithmeticResult.forResult(arithmetic, expectedResult(bdOperandA, b))
        } catch (e: IllegalArgumentException) {
            ArithmeticResult.forException(e)
        } catch (e: ArithmeticException) {
            ArithmeticResult.forException(e)
        }

        //actual
        val actual = try {
            ArithmeticResult.forResult(actualResult(dOperandA, b))
        } catch (e: IllegalArgumentException) {
            ArithmeticResult.forException(e)
        } catch (e: ArithmeticException) {
            ArithmeticResult.forException(e)
        }


        //assert
        try {
            actual.assertEquivalentTo(expected, messagePrefix)
        } catch (e: AssertionError) {
            if (isUnchecked && expected.isOverflow()) {
                //overflown results without CHECKED mode don't match
                return
            }
            if (!isWithinAllowedTolerance(expected, actual, b)) {
                throw e
            }
        }
    }

    //By definition pow precision is
    //n >= 0: rounding = HALF_UP, HALF_DOWN, HALF_EVEN: 1 ULP
    //n >= 0: other rounding modes: 0
    //n < 0: 16 ULP ??? 
    private fun isWithinAllowedTolerance(
        expected: ArithmeticResult<Long>,
        actual: ArithmeticResult<Long>,
        exponent: Int
    ): Boolean {
        val maxTolerance = if (exponent >= 0) 0 else 17
        val maxRoundingHalfTolerance = if (exponent >= 0) 1 else 17
        val exp = expected.compareValue
        val act = actual.compareValue
        if (exp == null || act == null) {
            return false
        }
        val neg = ((exp < 0) and (act < 0)) or (((exp == 0L) or (act == 0L)) and ((exp < 0) or (act < 0)))
        val diff = act - exp
        return when (roundingMode) {
            RoundingMode.UP -> if (neg) (diff <= 0) and (diff >= -maxTolerance) else (diff >= 0) and (diff <= maxTolerance)
            RoundingMode.DOWN -> if (neg) (diff >= 0) and (diff <= maxTolerance) else (diff <= 0) and (diff >= -maxTolerance)
            RoundingMode.CEILING -> (diff >= 0) and (diff <= maxTolerance)
            RoundingMode.FLOOR -> (diff <= 0) and (diff >= -maxTolerance)
            RoundingMode.HALF_UP, RoundingMode.HALF_DOWN, RoundingMode.HALF_EVEN -> (diff <= maxRoundingHalfTolerance) and (diff >= -maxRoundingHalfTolerance)
            RoundingMode.UNNECESSARY -> false
            else -> false
        }
    }

    override fun expectedResult(a: BigDecimal, b: Int): BigDecimal {
        require(!((b < -999999999) or (b > 999999999))) { "exponent out of range: $b" }
        val result = a.pow(abs(b.toDouble()).toInt())
        return if (b >= 0) result.setScale(getArithmeticScale(), roundingMode.toJavaRoundingMode()) else BigDecimal.ONE.divide(
            result,
            getArithmeticScale(), roundingMode.toJavaRoundingMode()
        )
    }

    override fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Int): Decimal<S> {
        return if (isStandardTruncationPolicy && RND.nextBoolean()) {
            a.pow(b)
        } else {
            if (isUnchecked && RND.nextBoolean()) {
                a.pow(b, roundingMode)
            } else {
                a.pow(b, truncationPolicy)
            }
        }
    }

    companion object {
        private val MAX_POW_EXPONENT = maxPowExponent

        private val maxPowExponent: Int
            get() = when (TestSettings.TEST_CASES) {
                TestCases.ALL -> 2000
                TestCases.LARGE -> 1000
                TestCases.STANDARD -> 500
                TestCases.SMALL -> 200
                TestCases.TINY -> 100
                else -> throw RuntimeException("unsupported: " + TestSettings.TEST_CASES)
            }

        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}, {1}")
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
