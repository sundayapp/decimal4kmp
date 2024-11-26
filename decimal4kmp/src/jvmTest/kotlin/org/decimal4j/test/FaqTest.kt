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
package org.decimal4j.test

import junit.framework.TestCase
import org.decimal4j.immutable.Decimal2f
import org.decimal4j.immutable.Decimal4f
import org.decimal4j.mutable.MutableDecimal2f
import org.decimal4j.scale.Scale2f
import org.decimal4j.scale.Scale4f
import org.junit.Test
import org.decimal4j.truncate.RoundingMode


class FaqTest {
    /**
     * Examples given in [Issue 23](https://github.com/tools4j/decimal4j/issues/23)
     */
    @Test
    fun issue23() {
        //1) With immutable decimals
        val valueA = Decimal2f.valueOf("1.23")
        val valueB = Decimal2f.valueOf("4.56")
        val halfUp = Decimal2f.valueOf(5.61)
        val down = Decimal2f.valueOf(5.60)
        val exact = Decimal4f.valueOf(5.6088)

        //truncated or rounded 2 scale results and exact 4 scale results
        TestCase.assertEquals(halfUp, valueA.multiply(valueB))
        TestCase.assertEquals(halfUp, valueA.multiply(valueB, RoundingMode.HALF_UP))
        TestCase.assertEquals(down, valueA.multiply(valueB, RoundingMode.DOWN))
        TestCase.assertEquals(exact, valueA.multiplyExact(valueB))
        TestCase.assertEquals(exact, valueA.multiplyExact().by(valueB))

        //2) With mutable decimals, can be reused
        val mutable = MutableDecimal2f()

        mutable.setUnscaled(123).multiplyUnscaled(456)
        TestCase.assertEquals(halfUp, mutable)

        mutable.setUnscaled(123).multiplyUnscaled(456, RoundingMode.DOWN)
        TestCase.assertEquals(down, mutable)

        //3) Zero GC API
        val roundUp = Scale2f.INSTANCE.getDefaultArithmetic()
        val roundDown = Scale2f.INSTANCE.getRoundingDownArithmetic()
        TestCase.assertEquals(561, roundUp.multiply(123, 456))
        TestCase.assertEquals(560, roundDown.multiply(123, 456))

        //exact result, we need target arithmetic of scale 4
        val scale4 = Scale4f.INSTANCE.getRoundingDownArithmetic() //round down is fastest
        val valA = scale4.fromUnscaled(123, 2)
        val valB = scale4.fromUnscaled(456, 2)
        TestCase.assertEquals(56088, scale4.multiply(valA, valB))
    }

    /**
     * Example given in [Issue 24](https://github.com/tools4j/decimal4j/issues/24)
     */
    @Test
    fun issue24() {
        val pnl = doubleArrayOf(1.0, 2.0, 3.0, 3.5, 3.2, 4.1)

        //1) with immutable decimals
        val totalPnl = pnl.map { Decimal2f.valueOf(it) }
            .reduce { acc, value -> acc.add(value) }
        TestCase.assertEquals("16.80", totalPnl.toString())
        TestCase.assertEquals(Decimal2f.valueOf(16.8), totalPnl)

        //2) with mutable decimals
        val mutableTotalPnl = pnl.map { MutableDecimal2f(it) }
            .reduce { acc, value -> acc.add(value) }
        TestCase.assertEquals("16.80", mutableTotalPnl.toString())
        TestCase.assertEquals(MutableDecimal2f(16.8), mutableTotalPnl)

        //3) with zero-GC
        val arithRoundHalfEven = Scale2f.INSTANCE.getRoundingHalfEvenArithmetic()
        val arithNoRounding = Scale2f.INSTANCE.getRoundingUnnecessaryArithmetic()
        //NOTE: accessing instance method refs causes allocation, hence cache them usually in a constant
        val fromDouble2f = { value: Double -> arithRoundHalfEven.fromDouble(value) }
        val add2f = { uDecimal1: Long, uDecimal2: Long -> arithNoRounding.add(uDecimal1, uDecimal2) }
        val totalPnlUnscaled = pnl.map(fromDouble2f).reduce(add2f)
        TestCase.assertEquals("16.80", arithNoRounding.toString(totalPnlUnscaled))
        TestCase.assertEquals(arithRoundHalfEven.fromDouble(16.8), totalPnlUnscaled)
    }
}
