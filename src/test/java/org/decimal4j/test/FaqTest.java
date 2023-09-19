/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015-2023 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.test;

import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.immutable.Decimal2f;
import org.decimal4j.immutable.Decimal4f;
import org.decimal4j.mutable.MutableDecimal2f;
import org.decimal4j.scale.Scale2f;
import org.decimal4j.scale.Scale4f;
import org.junit.Test;

import java.math.RoundingMode;

import static junit.framework.TestCase.assertEquals;

public class FaqTest {

    /**
     * Examples given in <a href="https://github.com/tools4j/decimal4j/issues/23">Issue 23</a>
     */
    @Test
    @SuppressWarnings("AssertBetweenInconvertibleTypes")
    public void issue23() {
        //1) With immutable decimals
        final Decimal2f valueA = Decimal2f.valueOf("1.23");
        final Decimal2f valueB = Decimal2f.valueOf("4.56");
        final Decimal2f halfUp = Decimal2f.valueOf(5.61);
        final Decimal2f down = Decimal2f.valueOf(5.60);
        final Decimal4f exact = Decimal4f.valueOf(5.6088);

        //truncated or rounded 2 scale results and exact 4 scale results
        assertEquals(halfUp, valueA.multiply(valueB));
        assertEquals(halfUp, valueA.multiply(valueB, RoundingMode.HALF_UP));
        assertEquals(down, valueA.multiply(valueB, RoundingMode.DOWN));
        assertEquals(exact, valueA.multiplyExact(valueB));
        assertEquals(exact, valueA.multiplyExact().by(valueB));

        //2) With mutable decimals, can be reused
        final MutableDecimal2f mutable = new MutableDecimal2f();

        mutable.setUnscaled(123).multiplyUnscaled(456);
        assertEquals(halfUp, mutable);

        mutable.setUnscaled(123).multiplyUnscaled(456, RoundingMode.DOWN);
        assertEquals(down, mutable);

        //3) Zero GC API
        final DecimalArithmetic roundUp = Scale2f.INSTANCE.getDefaultArithmetic();
        final DecimalArithmetic roundDown = Scale2f.INSTANCE.getRoundingDownArithmetic();
        assertEquals(561, roundUp.multiply(123, 456));
        assertEquals(560, roundDown.multiply(123, 456));

        //exact result, we need target arithmetic of scale 4
        final DecimalArithmetic scale4 = Scale4f.INSTANCE.getRoundingDownArithmetic();//round down is fastest
        final long valA = scale4.fromUnscaled(123, 2);
        final long valB = scale4.fromUnscaled(456, 2);
        assertEquals(56088, scale4.multiply(valA, valB));
    }
}
