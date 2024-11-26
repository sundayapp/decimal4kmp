package org.decimal4j.immutable;

import org.decimal4j.api.Decimal
import org.decimal4j.base.AbstractImmutableDecimal
import org.decimal4j.exact.Multipliable${scale}f
import org.decimal4j.factory.Factory${scale}f
import org.decimal4j.mutable.MutableDecimal${scale}f
import org.decimal4j.scale.Scale${scale}f
import org.decimal4j.truncate.RoundingMode

/**
 * <code>Decimal${scale}f</code> represents an immutable decimal number with a fixed
 * number of ${scale} digits to the right of the decimal point.
 * <p>
 * All methods for this class throw {@code NullPointerException} when passed a
 * {@code null} object reference for any input parameter.
 */
class Decimal${scale}f(unscaled: Long) : AbstractImmutableDecimal<Scale${scale}f, Decimal${scale}f>(unscaled) {

	/**
	* Translates the string representation of a {@code Decimal} into a
	* {@code Decimal${scale}f}. The string representation consists of an
	* optional sign, {@code '+'} or {@code '-'} , followed by a sequence of
	* zero or more decimal digits ("the integer"), optionally followed by a
	* fraction.
	* <p>
	* The fraction consists of a decimal point followed by zero or more decimal
	* digits. The string must contain at least one digit in either the integer
	* or the fraction. If the fraction contains more than ${scale} digits, the
	* value is rounded using {@link RoundingMode#HALF_UP HALF_UP} rounding. An
	* exception is thrown if the value is too large to be represented as a
	* {@code Decimal${scale}f}.
	*
	* @param value
	*            String value to convert into a {@code Decimal${scale}f}
	* @throws NumberFormatException
	*             if {@code value} does not represent a valid {@code Decimal}
	*             or if the value is too large to be represented as a
	*             {@code Decimal${scale}f}
	*/
	constructor(value: String): this(DEFAULT_CHECKED_ARITHMETIC.parse(value))

	companion object {

		/** Scale value ${scale} for {@code Decimal${scale}f} returned by {@link #getScale()}.*/
		val SCALE = ${scale};

		/** Scale metrics constant for {@code Decimal${scale}f} returned by {@link #getScaleMetrics()}.*/
		val METRICS = Scale${scale}f.INSTANCE;

		/** Factory constant for {@code Decimal${scale}f} returned by {@link #getFactory()}.*/
		val FACTORY = Factory${scale}f.INSTANCE;

		/**
		 * Default arithmetic for {@code Decimal${scale}f} performing unchecked operations with rounding mode
		 * {@link RoundingMode#HALF_UP HALF_UP}.
		 */
		val DEFAULT_ARITHMETIC = METRICS.getDefaultArithmetic();

		/**
		 * Default arithmetic for {@code Decimal${scale}f} performing checked operations with rounding mode
		 * {@link RoundingMode#HALF_UP HALF_UP}.
		 */
		val DEFAULT_CHECKED_ARITHMETIC = METRICS.getDefaultCheckedArithmetic();

		/** The unscaled long value that represents one.*/
		val ONE_UNSCALED: Long = METRICS.getScaleFactor();

		/** The {@code Decimal${scale}f} constant zero.*/
		val ZERO = Decimal${scale}f(0L);
		/**
		 * A constant holding the smallest positive value a {@code Decimal${scale}f}
		 * can have, 10<sup>-${scale}</sup><#if scale==0>=1</#if>.
		 */
		val ULP = Decimal${scale}f(1L);

		/**
		 * Initialize static constant array when class is loaded.
		 */
<#if (scale <= 17)>
		private val MAX_CONSTANT = 10;
<#else>
		private val MAX_CONSTANT = 9;
</#if>
		private val POS_CONST = arrayOfNulls<Decimal${scale}f>(MAX_CONSTANT + 1)
		private val NEG_CONST = arrayOfNulls<Decimal${scale}f>(MAX_CONSTANT + 1)

		init {
			for (i in 1..MAX_CONSTANT) {
				POS_CONST[i] = Decimal${scale}f(ONE_UNSCALED * i)
				NEG_CONST[i] = Decimal${scale}f(-ONE_UNSCALED * i)
			}
		}

		/** The {@code Decimal${scale}f} constant 1.*/
		val ONE = valueOf(1);
		/** The {@code Decimal${scale}f} constant 2.*/
		val TWO = valueOf(2);
		/** The {@code Decimal${scale}f} constant 3.*/
		val THREE = valueOf(3);
		/** The {@code Decimal${scale}f} constant 4.*/
		val FOUR = valueOf(4);
		/** The {@code Decimal${scale}f} constant 5.*/
		val FIVE = valueOf(5);
		/** The {@code Decimal${scale}f} constant 6.*/
		val SIX = valueOf(6);
		/** The {@code Decimal${scale}f} constant 7.*/
		val SEVEN = valueOf(7);
		/** The {@code Decimal${scale}f} constant 8.*/
		val EIGHT = valueOf(8);
		/** The {@code Decimal${scale}f} constant 9.*/
		val NINE = valueOf(9);
<#if (scale <= 17)>
		/** The {@code Decimal${scale}f} constant 10.*/
		val TEN = valueOf(10);
<#if (scale <= 16)>
		/** The {@code Decimal${scale}f} constant 100.*/
		val HUNDRED = Decimal${scale}f(100 * ONE_UNSCALED);
<#if (scale <= 15)>
		/** The {@code Decimal${scale}f} constant 1000.*/
		val THOUSAND = Decimal${scale}f(1000 * ONE_UNSCALED);
<#if (scale <= 12)>
		/** The {@code Decimal${scale}f} constant 10<sup>6</sup>.*/
		val MILLION = Decimal${scale}f(1000000 * ONE_UNSCALED);
<#if (scale <= 9)>
		/** The {@code Decimal${scale}f} constant 10<sup>9</sup>.*/
		val BILLION = Decimal${scale}f(1000000000 * ONE_UNSCALED);
<#if (scale <= 6)>
		/** The {@code Decimal${scale}f} constant 10<sup>12</sup>.*/
		val TRILLION = Decimal${scale}f(1000000000000L * ONE_UNSCALED);
<#if (scale <= 3)>
		/** The {@code Decimal${scale}f} constant 10<sup>15</sup>.*/
		val QUADRILLION = Decimal${scale}f(1000000000000000L * ONE_UNSCALED);
<#if (scale <= 0)>
		/** The {@code Decimal${scale}f} constant 10<sup>18</sup>.*/
		val QUINTILLION = Decimal${scale}f(1000000000000000000L * ONE_UNSCALED);
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>

		/** The {@code Decimal${scale}f} constant -1.*/
		val MINUS_ONE = valueOf(-1);

<#if (scale >= 1)>
		/** The {@code Decimal${scale}f} constant 0.5.*/
		val HALF = Decimal${scale}f(ONE_UNSCALED / 2);
		/** The {@code Decimal${scale}f} constant 0.1.*/
		val TENTH = Decimal${scale}f(ONE_UNSCALED / 10);
<#if (scale >= 2)>
		/** The {@code Decimal${scale}f} constant 0.01.*/
		val HUNDREDTH = Decimal${scale}f(ONE_UNSCALED / 100);
<#if (scale >= 3)>
		/** The {@code Decimal${scale}f} constant 0.001.*/
		val THOUSANDTH = Decimal${scale}f(ONE_UNSCALED / 1000);
<#if (scale >= 6)>
		/** The {@code Decimal${scale}f} constant 10<sup>-6</sup>.*/
		val MILLIONTH = Decimal${scale}f(ONE_UNSCALED / 1000000);
<#if (scale >= 9)>
		/** The {@code Decimal${scale}f} constant 10<sup>-9</sup>.*/
		val BILLIONTH = Decimal${scale}f(ONE_UNSCALED / 1000000000);
<#if (scale >= 12)>
		/** The {@code Decimal${scale}f} constant 10<sup>-12</sup>.*/
		val TRILLIONTH = Decimal${scale}f(ONE_UNSCALED / 1000000000000L);
<#if (scale >= 15)>
		/** The {@code Decimal${scale}f} constant 10<sup>-15</sup>.*/
		val QUADRILLIONTH = Decimal${scale}f(ONE_UNSCALED / 1000000000000000L);
<#if (scale >= 18)>
		/** The {@code Decimal${scale}f} constant 10<sup>-18</sup>.*/
		val QUINTILLIONTH = Decimal${scale}f(ONE_UNSCALED / 1000000000000000000L);
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>

		/**
		 * A constant holding the maximum value a {@code Decimal${scale}f} can have,
		 * ${"9223372036854775807"?substring(0, 19-scale)}<#if (scale>0)>.${"9223372036854775807"?substring(19-scale)}</#if>.
		 */
		val MAX_VALUE = Decimal${scale}f(Long.MAX_VALUE);
		/**
		 * A constant holding the maximum integer value a {@code Decimal${scale}f}
		 * can have, ${"9223372036854775807"?substring(0, 19-scale)}<#if (scale>0)>.${"0000000000000000000"?substring(19-scale)}</#if>.
		 */
		val MAX_INTEGER_VALUE = Decimal${scale}f((Long.MAX_VALUE / ONE_UNSCALED) * ONE_UNSCALED);
		/**
		 * A constant holding the minimum value a {@code Decimal${scale}f} can have,
		 * -${"9223372036854775807"?substring(0, 19-scale)}<#if (scale>0)>.${"9223372036854775808"?substring(19-scale)}</#if>.
		 */
		val MIN_VALUE = Decimal${scale}f(Long.MIN_VALUE);
		/**
		 * A constant holding the minimum integer value a {@code Decimal${scale}f}
		 * can have, -${"9223372036854775808"?substring(0, 19-scale)}<#if (scale>0)>.${"0000000000000000000"?substring(19-scale)}</#if>.
		 */
		val MIN_INTEGER_VALUE = Decimal${scale}f((Long.MIN_VALUE / ONE_UNSCALED) * ONE_UNSCALED);


		/**
		* Returns a {@code Decimal${scale}f} whose value is numerically equal to
		* that of the specified {@code long} value. An exception is thrown if the
		* specified value is too large to be represented as a {@code Decimal${scale}f}.
		*
		* @param value
		*            long value to convert into a {@code Decimal${scale}f}
		* @return a {@code Decimal${scale}f} value numerically equal to the specified
		*            {@code long} value
		* @throws IllegalArgumentException
		*            if {@code value} is too large to be represented as a
		*            {@code Decimal${scale}f}
		*/
		fun valueOf(value: Long): Decimal${scale}f {
			if (value == 0L) return ZERO
			if ((value > 0) and (value <= MAX_CONSTANT)) return POS_CONST[value.toInt()]!!
			else if ((value < 0) and (value >= -MAX_CONSTANT)) return NEG_CONST[-value.toInt()]!!
			return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromLong(value))
		}

		/**
		* Returns a {@code Decimal${scale}f} whose value is calculated by
		* rounding the specified {@code float} argument to scale ${scale}
		* using {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown
		* if the specified value is too large to be represented as a {@code Decimal${scale}f}.
		*
		* @param value
		*            float value to convert into a {@code Decimal${scale}f}
		* @return a {@code Decimal${scale}f} calculated as: <code>round<sub>HALF_UP</sub>(value)</code>
		* @throws IllegalArgumentException
		*             if {@code value} is NaN or infinite or if the magnitude is
		*             too large for the float to be represented as a {@code Decimal${scale}f}
		*/
		fun valueOf(value: Float): Decimal${scale}f {
			return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromFloat(value));
		}

		/**
		* Returns a {@code Decimal${scale}f} whose value is calculated by
		* rounding the specified {@code float} argument to scale ${scale}
		* using the specified {@code roundingMode}. An exception is thrown
		* if the specified value is too large to be represented as a {@code Decimal${scale}f}.
		*
		* @param value
		*            float value to convert into a {@code Decimal${scale}f}
		* @param roundingMode
		*            the rounding mode to apply during the conversion if necessary
		* @return a {@code Decimal${scale}f} calculated as: <code>round(value)</code>
		* @throws IllegalArgumentException
		*             if {@code value} is NaN or infinite or if the magnitude is
		*             too large for the float to be represented as a {@code Decimal${scale}f}
		* @throws ArithmeticException
		*             if {@code roundingMode==UNNECESSARY} and rounding is
		*             necessary
		*/
		fun valueOf(value: Float, roundingMode: RoundingMode): Decimal${scale}f {
			return valueOfUnscaled(METRICS.getCheckedArithmetic(roundingMode).fromFloat(value));
		}

		/**
		* Returns a {@code Decimal${scale}f} whose value is calculated by
		* rounding the specified {@code double} argument to scale ${scale}
		* using {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown
		* if the specified value is too large to be represented as a {@code Decimal${scale}f}.
		*
		* @param value
		*            double value to convert into a {@code Decimal${scale}f}
		* @return a {@code Decimal${scale}f} calculated as: <code>round<sub>HALF_UP</sub>(value)</code>
		* @throws IllegalArgumentException
		*             if {@code value} is NaN or infinite or if the magnitude is
		*             too large for the double to be represented as a {@code Decimal${scale}f}
		*/
		fun valueOf(value: Double): Decimal${scale}f {
			return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromDouble(value));
		}

		/**
		* Returns a {@code Decimal${scale}f} whose value is calculated by
		* rounding the specified {@code double} argument to scale ${scale}
		* using the specified {@code roundingMode}. An exception is thrown
		* if the specified value is too large to be represented as a {@code Decimal${scale}f}.
		*
		* @param value
		*            double value to convert into a {@code Decimal${scale}f}
		* @param roundingMode
		*            the rounding mode to apply during the conversion if necessary
		* @return a {@code Decimal${scale}f} calculated as: <code>round(value)</code>
		* @throws IllegalArgumentException
		*             if {@code value} is NaN or infinite or if the magnitude is
		*             too large for the double to be represented as a {@code Decimal${scale}f}
		* @throws ArithmeticException
		*             if {@code roundingMode==UNNECESSARY} and rounding is
		*             necessary
		*/
		fun valueOf(value: Double, roundingMode: RoundingMode): Decimal${scale}f {
			return valueOfUnscaled(METRICS.getCheckedArithmetic(roundingMode).fromDouble(value));
		}

		/**
		* Returns a {@code Decimal${scale}f} whose value is calculated by rounding
		* the specified {@link Decimal} argument to scale ${scale} using
		* {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the
		* specified value is too large to be represented as a {@code Decimal${scale}f}.
		*
		* @param value
		*            Decimal value to convert into a {@code Decimal${scale}f}
		* @return a {@code Decimal${scale}f} calculated as: <code>round<sub>HALF_UP</sub>(value)</code>
		* @throws IllegalArgumentException
		*             if {@code value} is too large to be represented as a {@code Decimal${scale}f}
		*/
		fun valueOf(value: Decimal<*>): Decimal${scale}f {
			if (value is Decimal${scale}f) {
				return value as Decimal${scale}f;
			}
			return valueOfUnscaled(value.unscaledValue(), value.scale);
		}

		/**
		* Returns a {@code Decimal${scale}f} whose value is calculated by rounding
		* the specified {@link Decimal} argument to scale ${scale} using
		* the specified {@code roundingMode}. An exception is thrown if the
		* specified value is too large to be represented as a {@code Decimal${scale}f}.
		*
		* @param value
		*            Decimal value to convert into a {@code Decimal${scale}f}
		* @param roundingMode
		*            the rounding mode to apply during the conversion if necessary
		* @return a {@code Decimal${scale}f} calculated as: <code>round(value)</code>
		* @throws IllegalArgumentException
		*             if {@code value} is too large to be represented as a {@code Decimal${scale}f}
		* @throws ArithmeticException
		*             if {@code roundingMode==UNNECESSARY} and rounding is
		*             necessary
		*/
		fun valueOf(value: Decimal<*>, roundingMode: RoundingMode): Decimal${scale}f {
			if (value is Decimal${scale}f) {
				return value as Decimal${scale}f;
			}
			return valueOfUnscaled(value.unscaledValue(), value.scale, roundingMode);
		}

		/**
		* Translates the string representation of a {@code Decimal} into a
		* {@code Decimal${scale}f}. The string representation consists of an
		* optional sign, {@code '+'} or {@code '-'} , followed by a sequence of
		* zero or more decimal digits ("the integer"), optionally followed by a
		* fraction.
		* <p>
		* The fraction consists of a decimal point followed by zero or more decimal
		* digits. The string must contain at least one digit in either the integer
		* or the fraction. If the fraction contains more than ${scale} digits, the
		* value is rounded using {@link RoundingMode#HALF_UP HALF_UP} rounding. An
		* exception is thrown if the value is too large to be represented as a
		* {@code Decimal${scale}f}.
		*
		* @param value
		*            String value to convert into a {@code Decimal${scale}f}
		* @return a {@code Decimal${scale}f} calculated as: <code>round<sub>HALF_UP</sub>(value)</code>
		* @throws NumberFormatException
		*             if {@code value} does not represent a valid {@code Decimal}
		*             or if the value is too large to be represented as a
		*             {@code Decimal${scale}f}
		*/
		fun valueOf(value: String): Decimal${scale}f {
			return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.parse(value));
		}

		/**
		* Translates the string representation of a {@code Decimal} into a
		* {@code Decimal${scale}f}. The string representation consists of an
		* optional sign, {@code '+'} or {@code '-'} , followed by a sequence of
		* zero or more decimal digits ("the integer"), optionally followed by a
		* fraction.
		* <p>
		* The fraction consists of a decimal point followed by zero or more decimal
		* digits. The string must contain at least one digit in either the integer
		* or the fraction. If the fraction contains more than ${scale} digits, the
		* value is rounded using the specified {@code roundingMode}. An exception
		* is thrown if the value is too large to be represented as a {@code Decimal${scale}f}.
		*
		* @param value
		*            String value to convert into a {@code Decimal${scale}f}
		* @param roundingMode
		*            the rounding mode to apply if the fraction contains more than
		*            ${scale} digits
		* @return a {@code Decimal${scale}f} calculated as: <code>round(value)</code>
		* @throws NumberFormatException
		*             if {@code value} does not represent a valid {@code Decimal}
		*             or if the value is too large to be represented as a
		*             {@code Decimal${scale}f}
		* @throws ArithmeticException
		*             if {@code roundingMode==UNNECESSARY} and rounding is
		*             necessary
		*/
		fun valueOf(value: String, roundingMode: RoundingMode): Decimal${scale}f {
			return valueOfUnscaled(METRICS.getCheckedArithmetic(roundingMode).parse(value));
		}

		/**
		* Returns a {@code Decimal${scale}f} whose value is numerically equal to
		* <code>(unscaledValue &times; 10<sup>-${scale}</sup>)</code>.
		*
		* @param unscaledValue
		*            unscaled value to convert into a {@code Decimal${scale}f}
		* @return a {@code Decimal${scale}f} calculated as:
		*         <code>unscaledValue &times; 10<sup>-${scale}</sup></code>
		*/
		fun valueOfUnscaled(unscaledValue: Long): Decimal${scale}f {
			if (unscaledValue == 0L) {
				return ZERO;
			}
			if (unscaledValue == 1L) {
				return ULP;
			}
			if (unscaledValue == ONE_UNSCALED) {
				return ONE;
			}
			if (unscaledValue == -ONE_UNSCALED) {
				return MINUS_ONE;
			}
			return Decimal${scale}f(unscaledValue);
		}

		/**
		* Returns a {@code Decimal${scale}f} whose value is numerically equal to
		* <code>(unscaledValue &times; 10<sup>-scale</sup>)</code>. The result is
		* rounded to scale ${scale} using {@link RoundingMode#HALF_UP HALF_UP}
		* rounding. An exception is thrown if the specified value is too large
		* to be represented as a {@code Decimal${scale}f}.
		*
		* @param unscaledValue
		*            unscaled value to convert into a {@code Decimal${scale}f}
		* @param scale
		*            the scale to apply to {@code unscaledValue}
		* @return a {@code Decimal${scale}f} calculated as:
		*         <code>round<sub>HALF_UP</sub>(unscaledValue &times; 10<sup>-scale</sup>)</code>
		* @throws IllegalArgumentException
		*             if the specified value is too large to be represented as a
		*             {@code Decimal${scale}f}
		*/
		fun valueOfUnscaled(unscaledValue: Long, scale: Int): Decimal${scale}f {
			return valueOfUnscaled(DEFAULT_CHECKED_ARITHMETIC.fromUnscaled(unscaledValue, scale));
		}

		/**
		* Returns a {@code Decimal${scale}f} whose value is numerically equal to
		* <code>(unscaledValue &times; 10<sup>-scale</sup>)</code>. The result
		* is rounded to scale ${scale} using the specified {@code roundingMode}.
		* An exception is thrown if the specified value is too large to be
		* represented as a {@code Decimal${scale}f}.
		*
		* @param unscaledValue
		*            unscaled value to convert into a Decimal${scale}
		* @param scale
		*            the scale to apply to {@code unscaledValue}
		* @param roundingMode
		*            the rounding mode to apply during the conversion if necessary
		* @return a {@code Decimal${scale}f} calculated as:
		*         <code>round(unscaledValue &times; 10<sup>-scale</sup>)</code>
		* @throws IllegalArgumentException
		*             if the specified value is too large to be represented as a {@code Decimal${scale}f}
		*/
		fun valueOfUnscaled(unscaledValue: Long, scale: Int, roundingMode: RoundingMode): Decimal${scale}f {
			return valueOfUnscaled(METRICS.getCheckedArithmetic(roundingMode).fromUnscaled(unscaledValue, scale));
		}

	}

	override val scaleMetrics = METRICS
	override val scale = SCALE
	override val factory = FACTORY
	override fun self() = this
	override val defaultArithmetic = DEFAULT_ARITHMETIC
	override val defaultCheckedArithmetic = DEFAULT_CHECKED_ARITHMETIC
	override val roundingDownArithmetic = METRICS.getRoundingDownArithmetic()
	override val roundingFloorArithmetic = METRICS.getRoundingFloorArithmetic()
	override val roundingHalfEvenArithmetic = METRICS.getRoundingHalfEvenArithmetic()
	override val roundingUnnecessaryArithmetic = METRICS.getRoundingUnnecessaryArithmetic()
	override fun createOrAssign(unscaled: Long) = valueOfUnscaled(unscaled)
	override fun create(unscaled: Long) = valueOfUnscaled(unscaled)

	override fun createArray(length: Int): Array<Decimal${scale}f?> {
		return arrayOfNulls(length)
	}
	
	/**
	 * Returns this {@code Decimal} as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of {@code this} Decimal and the
	 * second factor passed to the {@code by(..)} method.
	 * <p>
	 * The method is similar to {@link #multiplyExact(Decimal) multiplyExact(Decimal)} but the result
	 * is retrieved in exact typed form with the correct result scale. 
	 * <p>
	 * For instance one can write:
	 * <pre>
<#if (scale+2<=maxScale)>
	 * Decimal${scale+2}f product = this.multiplyExact().by(Decimal2f.FIVE);
<#else>
	 * Decimal${scale}f product = this.multiplyExact().by(Decimal0f.FIVE);
</#if>
	 * </pre>
	 * 
	 * @return a multipliable object encapsulating this Decimal as first factor
	 *             of an exact multiplication
	 */
	fun multiplyExact(): Multipliable${scale}f {
		return Multipliable${scale}f(this)
	}

	override fun toMutableDecimal(): MutableDecimal${scale}f {
		return MutableDecimal${scale}f(this)
	}

	override fun toImmutableDecimal(): Decimal${scale}f {
		return this
	}
}