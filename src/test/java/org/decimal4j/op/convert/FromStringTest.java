/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 decimal4j (tools4j), Marco Terzer
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
package org.decimal4j.op.convert;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.decimal4j.api.Decimal;
import org.decimal4j.api.DecimalArithmetic;
import org.decimal4j.api.MutableDecimal;
import org.decimal4j.arithmetic.JDKSupport;
import org.decimal4j.factory.DecimalFactory;
import org.decimal4j.op.AbstractOperandTest;
import org.decimal4j.scale.ScaleMetrics;
import org.decimal4j.test.ArithmeticResult;
import org.decimal4j.test.TestSettings;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test {@link DecimalArithmetic#parse(String)} via
 * {@link DecimalFactory#parse(String)}, {@link MutableDecimal#set(String)}
 * and the static {@code valueOf(String)} methods of the Immutable Decimal
 * implementations.
 */
@RunWith(Parameterized.class)
public class FromStringTest extends AbstractOperandTest {

	public FromStringTest(ScaleMetrics s, RoundingMode mode, DecimalArithmetic arithmetic) {
		super(arithmetic);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final RoundingMode mode : TestSettings.UNCHECKED_ROUNDING_MODES) {
				final DecimalArithmetic arith = s.getArithmetic(mode);
				data.add(new Object[] { s, mode, arith });
			}
		}
		return data;
	}

	@Override
	protected String operation() {
		return "fromString";
	}

	protected String randomStringOperand() {
		final String s = Long.toString(RND.nextLong());
		return toDecimalString(s, RND.nextInt(s.length() + 1));
	}
	private static String toDecimalString(String s, int decimalIndex) {
		if (decimalIndex < 0 || decimalIndex >= s.length()) {
			return s;
		}
		if (decimalIndex == 0 && s.startsWith("-")) {
			decimalIndex++; 
		}
		return s.substring(0, decimalIndex) + "." + s.substring(decimalIndex);
	}

	protected String[] getSpecialStringOperands() {
		final Set<String> values = new LinkedHashSet<String>();
		for (final long value : TestSettings.TEST_CASES.getSpecialValuesFor(getScaleMetrics())) {
			final String s = Long.toString(value);
			for (int i = 0; i <= s.length(); i++) {
				final String decimalString = toDecimalString(s, i);
				values.add(decimalString);
				if (value > 0) {
					values.add("+" + decimalString);
				}
				if (decimalString.indexOf('.') >= 0) {
					values.add(decimalString + "0");
					values.add(decimalString + "5");
					values.add(decimalString + "9");
					values.add(decimalString + "0000000000000000000000000000000");
					values.add(decimalString + "0000000000000000000000000000001");
					values.add(decimalString + "4999999999999999999999999999999");
					values.add(decimalString + "5000000000000000000000000000000");
					values.add(decimalString + "5000000000000000000000000000001");
					values.add(decimalString + "9999999999999999999999999999999");
				}
				//some invalid
				values.add(decimalString + "A");
				values.add(decimalString + "000000000000000000000000000000Z");
			}
		}
		//some potential overflow values
		values.add("9223372036854775808");//Long.MAX_VALUE + 1
		values.add("-9223372036854775809");//Long.MIN_VALUE - 1
		values.add("9223372036854775808".substring(0, 19-getScale()) + "." + "9223372036854775808".substring(19-getScale()));//Long.MAX_VALUE + 1, with decimal point
		values.add("-9223372036854775809".substring(0, 20-getScale()) + "." + "-9223372036854775809".substring(20-getScale()));//Long.MAX_VALUE + 1, with decimal point
		values.add(Long.MAX_VALUE + "0");
		values.add(Long.MAX_VALUE + "0.0");
		values.add(Long.MAX_VALUE + ".1");
		values.add(Long.MAX_VALUE + ".5");
		values.add(Long.MAX_VALUE + ".9");
		values.add(Long.MIN_VALUE + "0");
		values.add(Long.MIN_VALUE + "0.0");
		values.add(Long.MIN_VALUE + ".1");
		values.add(Long.MIN_VALUE + ".5");
		values.add(Long.MIN_VALUE + ".9");
		//some invalid values
		values.add("");
		values.add(" 1");
		values.add("1 ");
		values.add(" 1.0");
		values.add("1.0 ");
		values.add("A");
		values.add("+-1.0");
		values.add("--1.0");
		values.add("++1.0");
		values.add("1.");
		values.add("+1.");
		values.add("-1.");
		values.add("+1.A");
		values.add("-1.A");
		values.add(null);//test null input
		return values.toArray(new String[values.size()]);
	}

	@Override
	protected <S extends ScaleMetrics> void runRandomTest(S scaleMetrics, int index) {
		runTest(scaleMetrics, "[" + index + "]", randomStringOperand());
	}

	@Override
	protected <S extends ScaleMetrics> void runSpecialValueTest(S scaleMetrics) {
		final String[] specialOperands = getSpecialStringOperands();
		for (int i = 0; i < specialOperands.length; i++) {
			runTest(scaleMetrics, "[" + i + "]", specialOperands[i]);
		}
	}

	protected <S extends ScaleMetrics> void runTest(S scaleMetrics, String name, String operand) {

		//expected
		ArithmeticResult<Long> expected;
		try {
			expected = ArithmeticResult.forResult(arithmetic, expectedResult(operand));
		} catch (ArithmeticException e) {
			expected = ArithmeticResult.forException(e);
		} catch (IllegalArgumentException e) {
			expected = ArithmeticResult.forException(e);
		} catch (NullPointerException e) {
			expected = ArithmeticResult.forException(e);
		}

		//actual
		ArithmeticResult<Long> actual;
		try {
			actual = ArithmeticResult.forResult(actualResult(scaleMetrics, operand));
		} catch (ArithmeticException e) {
			actual = ArithmeticResult.forException(e);
		} catch (IllegalArgumentException e) {
			actual = ArithmeticResult.forException(e);
		} catch (NullPointerException e) {
			actual = ArithmeticResult.forException(e);
		}

		//assert
		actual.assertEquivalentTo(expected, getClass().getSimpleName() + name + ": " + operation() + " " + operand);
	}

	protected BigDecimal expectedResult(String operand) {
		final BigDecimal value = new BigDecimal(operand).setScale(getScale(), getRoundingMode());
		//check that the conversion does not overflow
		JDKSupport.bigIntegerToLongValueExact(value.unscaledValue());
		return value;
	}

	protected <S extends ScaleMetrics> Decimal<S> actualResult(S scaleMetrics, String operand) {
		if (RND.nextBoolean()) {
			//Factory, immutable
			if (isRoundingDefault() && RND.nextBoolean()) {
				return getDecimalFactory(scaleMetrics).parse(operand);
			} else {
				return getDecimalFactory(scaleMetrics).parse(operand, getRoundingMode());
			}
		} else if (RND.nextBoolean()) {
			//Factory, mutable
			if (isRoundingDefault() && RND.nextBoolean()) {
				return getDecimalFactory(scaleMetrics).newMutable().set(operand);
			} else {
				return getDecimalFactory(scaleMetrics).newMutable().set(operand, getRoundingMode());
			}
		} else {
			//Immutable, valueOf method
			return valueOf(scaleMetrics, operand);
		}
	}

	@SuppressWarnings("unchecked")
	private <S extends ScaleMetrics> Decimal<S> valueOf(S scaleMetrics, String operand) {
		try {
			final Class<?> clazz = Class.forName("org.decimal4j.immutable.Decimal" + getScale() + "f");
			if (isRoundingDefault() && RND.nextBoolean()) {
				return (Decimal<S>) clazz.getMethod("valueOf", String.class).invoke(null, operand);
			} else {
				return (Decimal<S>) clazz.getMethod("valueOf", String.class, RoundingMode.class).invoke(null, operand, getRoundingMode());
			}
		} catch (InvocationTargetException e) {
			if (e.getTargetException() instanceof RuntimeException) {
				throw (RuntimeException) e.getTargetException();
			}
			throw new RuntimeException("could not invoke valueOf method, e=" + e, e);
		} catch (Exception e) {
			throw new RuntimeException("could not invoke valueOf method, e=" + e, e);
		}
	}

}