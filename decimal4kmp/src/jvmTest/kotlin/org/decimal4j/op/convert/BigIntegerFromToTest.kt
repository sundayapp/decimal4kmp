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

import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.api.MutableDecimal
import org.decimal4j.arithmetic.JDKSupport
import org.decimal4j.factory.DecimalFactory
import org.decimal4j.op.AbstractFromToTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigInteger

/**
 * Unit test for [DecimalFactory.valueOf], [MutableDecimal.set]
 * and indirectly also the static `valueOf(..)` method of the immutable Decimal.
 */
@RunWith(Parameterized::class)
class BigIntegerFromToTest(s: ScaleMetrics?, arithmetic: DecimalArithmetic) :
    AbstractFromToTest<BigInteger>(arithmetic) {
    override fun randomValue(scaleMetrics: ScaleMetrics): BigInteger {
        if (RND.nextInt(10) != 0) {
            return BigInteger.valueOf(nextLongOrInt())
        }
        //every tenth potentially an overflow
        val bytes = ByteArray(1 + RND.nextInt(100))
        RND.nextBytes(bytes)
        return BigInteger(bytes)
    }

    override fun specialValues(scaleMetrics: ScaleMetrics): Array<BigInteger> {
        val specials = TestSettings.TEST_CASES.getSpecialValuesFor(scaleMetrics)
        val set: MutableSet<BigInteger> = sortedSetOf()
        for (i in specials.indices) {
            set.add(BigInteger.valueOf(specials[i]))
        }
        //add two non-long values
        set.add(BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE))
        set.add(BigInteger.valueOf(Long.MIN_VALUE).subtract(BigInteger.ONE))
        return set.toTypedArray<BigInteger>()
    }

    override fun <S : ScaleMetrics> expectedResult(scaleMetrics: S, value: BigInteger): BigInteger {
        require(value.bitLength() <= 63) { "Overflow: $value" }
        val lvalue = JDKSupport.bigIntegerToLongValueExact(value)
        if (scaleMetrics.isValidIntegerValue(lvalue)) {
            return value
        }
        throw IllegalArgumentException("Overflow for $scaleMetrics with value $value")
    }

    override fun <S : ScaleMetrics> actualResult(factory: DecimalFactory<S>, value: BigInteger): BigInteger {
        val decimal = if (RND.nextBoolean()) factory.valueOf(value) else factory.newMutable().set(value)
        return if (RND.nextBoolean()) decimal.toBigInteger() else decimal.toBigIntegerExact()
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                data.add(arrayOf(s, s.getRoundingDownArithmetic()))
            }
            return data
        }
    }
}
