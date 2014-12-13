package ch.javasoft.decimal.op;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.junit.runners.Parameterized.Parameters;

import ch.javasoft.decimal.arithmetic.DecimalArithmetics;
import ch.javasoft.decimal.scale.ScaleMetrics;
import ch.javasoft.decimal.test.TestSettings;
import ch.javasoft.decimal.truncate.TruncationPolicy;

/**
 * Base class for unit tests with a double operand.
 */
abstract public class AbstractDoubleOperandTest extends Abstract1DecimalArg1DoubleArgToDecimalResultTest {
	
	protected final MathContext MATH_CONTEXT_DOUBLE_TO_LONG_64 = new MathContext(19, RoundingMode.HALF_EVEN);

	public AbstractDoubleOperandTest(ScaleMetrics s, TruncationPolicy tp, DecimalArithmetics arithmetics) {
		super(arithmetics);
	}

	@Parameters(name = "{index}: {0}, {1}")
	public static Iterable<Object[]> data() {
		final List<Object[]> data = new ArrayList<Object[]>();
		for (final ScaleMetrics s : TestSettings.SCALES) {
			for (final TruncationPolicy tp : TestSettings.POLICIES) {
				final DecimalArithmetics arith = s.getArithmetics(tp);
				data.add(new Object[] {s, tp, arith});
			}
		}
		return data;
	}
	
	protected BigDecimal toBigDecimal(double operand) {
		return Doubles.doubleToBigDecimal(operand, getScale(), getRoundingMode());
	}
	
}
