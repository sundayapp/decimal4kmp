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
package org.decimal4j.op.arith;

import java.math.BigDecimal;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.op.AbstractDecimalUnscaledToDecimalTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.truncate.TruncationPolicy;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 * Unit test for {@link Decimal#addUnscaled(long)}
 */
@RunWith(Parameterized.class)
public class MultiplyUnscaledTest extends AbstractDecimalUnscaledToDecimalTest {
	
	public MultiplyUnscaledTest(ScaleMetrics sm, TruncationPolicy tp, int scale, DecimalArithmetic arithmetic) {
		super(sm, tp, scale, arithmetic);
	}

	@Override
	protected String operation() {
		return "* 10^" + (-scale) + " *";
	}
	
	@Override
	protected BigDecimal expectedResult(BigDecimal a, long b) {
		return a.multiply(toBigDecimal(b));
	}
	
	@Override
	protected <S extends ScaleMetrics> Decimal<S> actualResult(Decimal<S> a, long b) {
		if (isStandardTruncationPolicy() && RND.nextBoolean()) {
			if (scale == getScale() && RND.nextBoolean()) {
				return a.multiplyUnscaled(b);
			}
			return a.multiplyUnscaled(b, scale);
		}
		if (isUnchecked() && RND.nextBoolean()) {
			if (scale == getScale() && RND.nextBoolean()) {
				return a.multiplyUnscaled(b, getRoundingMode());
			}
			return a.multiplyUnscaled(b, scale, getRoundingMode());
		}
		if (scale == getScale() && RND.nextBoolean()) {
			return a.multiplyUnscaled(b, getTruncationPolicy());
		}
		return a.multiplyUnscaled(b, scale, getTruncationPolicy());
	}
}
