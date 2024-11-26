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

import org.decimal4j.api.BigDecimalExtensions.toBigDecimal
import org.decimal4j.api.Decimal
import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.op.AbstractRandomAndSpecialValueTest
import org.decimal4j.scale.Scale18f
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.ArithmeticResult
import org.decimal4j.test.TestSettings
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import java.math.BigInteger
import org.decimal4j.truncate.RoundingMode
import org.decimal4j.arithmetic.toJavaRoundingMode

/**
 * Unit test for [Decimal.invert]
 */
@RunWith(Parameterized::class)
class SqrtTest(scaleMetrics: ScaleMetrics?, roundingMode: RoundingMode?, arithmetic: DecimalArithmetic) :
    AbstractRandomAndSpecialValueTest(arithmetic) {

    @Test
    fun runProblemTest0_offByOne() {
        if (getArithmeticScale() == 18) {
            val `val` = newDecimal(Scale18f.INSTANCE, 6900127896905764146L)
            runTest(`val`, -1)
        }
    }

    @Test
    fun runProblemTest2_offByTwo() {
        if (getArithmeticScale() == 18) {
            val `val` = newDecimal(Scale18f.INSTANCE, 8669660385983162309L)
            runTest(`val`, -1)
        }
    }

    override fun operation(): String {
        return "sqrt"
    }

    override fun <S : ScaleMetrics> runRandomTest(scaleMetrics: S, index: Int) {
        runTest(randomDecimal(scaleMetrics), index)
    }

    override fun <S : ScaleMetrics> runSpecialValueTest(scaleMetrics: S) {
        var index = 0
        for (unscaledSpecial in getSpecialValues(scaleMetrics)) {
            runTest(newDecimal(scaleMetrics, unscaledSpecial), index++)
        }
    }

    private fun <S : ScaleMetrics> runTest(operand: Decimal<S>, index: Int) {
        if (operand.isNegative()) {
            runNegativeTest(operand)
            return
        }

        //given: positive
        if ((roundingMode == RoundingMode.DOWN || roundingMode == RoundingMode.FLOOR) && RND.nextBoolean()) {
            //given
            val x = operand.toBigDecimal()

            //when
            val actual = actualResult(operand)


            //then: compare operand with actual^2 and (actual+ULP)^2
            val xSquared = actual.toBigDecimal().pow(2)
            val xPlusUlpSquared = actual.addUnscaled(1).toBigDecimal().pow(2)

            val msg =
                "{x=" + operand + ", y=sqrt(x)=" + actual + ", sqrt(x)+ULP=" + actual.addUnscaled(1) + ", sqrt(x)^2=" + xSquared.toPlainString() + ", (sqrt(x)+ULP)^2=" + xPlusUlpSquared.toPlainString() + "}"
            Assert.assertTrue("[$index] sqrt(x)^2 must be <= x. $msg", xSquared.compareTo(x) <= 0)
            Assert.assertTrue("[$index] sqrt(x+ULP)^2 must be > x. $msg", xPlusUlpSquared.compareTo(x) > 0)
        } else {
            val name = "[$index]"
            val messagePrefix = javaClass.simpleName + name + ": " + operand + " " + operation()

            //expected
            var expected = try {
                ArithmeticResult.forResult(
                    arithmetic,
                    expectedResult(toBigDecimal(operand))
                )
            } catch (e: ArithmeticException) {
                ArithmeticResult.forException(e)
            }

            //actual
            var actual = try {
                ArithmeticResult.forResult(actualResult(operand))
            } catch (e: ArithmeticException) {
                ArithmeticResult.forException(e)
            }

            //assert
            actual.assertEquivalentTo(expected, messagePrefix)
        }
    }

    private fun <S : ScaleMetrics> runNegativeTest(operand: Decimal<S>) {
        try {
            //when
            val actual = actualResult(operand)


            //then: expect exception
            Assert.fail("expected arithmetic exception for sqrt($operand) but result was: $actual")
        } catch (e: ArithmeticException) {
            //as expected
            return
        }
    }

    private fun expectedResult(bigDecimal: BigDecimal): BigDecimal {
        //we calculate 20 extra decimal places, should be enough, chance that we have 20 zero's or a 5 and 19 zeros is relatively low
        return sqrt(bigDecimal.multiply(TEN_POW_2xPRECISION)).divide(
            TEN_POW_PRECISION,
            getArithmeticScale(), roundingMode.toJavaRoundingMode()
        )
    }

    private fun <S : ScaleMetrics> actualResult(operand: Decimal<S>): Decimal<S> {
        if (isStandardTruncationPolicy && RND.nextBoolean()) {
            return operand.sqrt()
        }
        if (RND.nextBoolean()) {
            return operand.sqrt(roundingMode)
        }
        //also test checked arithmetic otherwise this is not covered
        val checkedAith = operand.scaleMetrics.getCheckedArithmetic(roundingMode)
        return newDecimal(operand.scaleMetrics, checkedAith.sqrt(operand.unscaledValue()))
    }

    companion object {
        private const val precision = 20
        private val TEN_POW_PRECISION: BigDecimal = BigDecimal.TEN.pow(precision)
        private val TEN_POW_2xPRECISION: BigDecimal = BigDecimal.TEN.pow(precision shl 1)

        @JvmStatic
        @Parameterized.Parameters(name = "{index}: scale={0}, rounding={1}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                for (rm in TestSettings.UNCHECKED_ROUNDING_MODES) {
                    data.add(arrayOf(s, rm, s.getArithmetic(rm)))
                }
            }
            return data
        }

        fun sqrt(bigDecimal: BigDecimal): BigDecimal {
            if (bigDecimal.signum() < 0) {
                throw ArithmeticException("Square root of a negative value: $bigDecimal")
            }
            val scale = bigDecimal.scale()
            val bigInt = bigDecimal.unscaledValue().multiply(BigInteger.TEN.pow(scale))
            var len = bigInt.bitLength()
            len += len and 0x1 //round up if odd
            var rem = BigInteger.ZERO
            var root = BigInteger.ZERO
            var i = len - 1
            while (i >= 0) {
                root = root.shiftLeft(1)
                rem = rem.shiftLeft(2)
                val add = (if (bigInt.testBit(i)) 2 else 0) + (if (bigInt.testBit(i - 1)) 1 else 0)
                rem = rem.add(BigInteger.valueOf(add.toLong()))
                val rootPlusOne = root.add(BigInteger.ONE)
                if (rootPlusOne.compareTo(rem) <= 0) {
                    rem = rem.subtract(rootPlusOne)
                    root = rootPlusOne.add(BigInteger.ONE)
                }
                i -= 2
            }
            return BigDecimal(root.shiftRight(1), scale)
        }
    }
}
