package org.decimal4j.mutable;

import org.decimal4j.api.Decimal
import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.api.ImmutableDecimal
import org.decimal4j.base.AbstractMutableDecimal
import org.decimal4j.exact.Multipliable${scale}f
import org.decimal4j.factory.Factory${scale}f
import org.decimal4j.immutable.Decimal${scale}f
import org.decimal4j.immutable.Decimal${scale}f.Companion.valueOf
import org.decimal4j.scale.Scale${scale}f
import java.math.BigDecimal
import java.math.BigInteger

/**
 * <code>MutableDecimal${scale}f</code> represents a mutable decimal number with a fixed
 * number of ${scale} digits to the right of the decimal point.
 * <p>
 * All methods for this class throw {@code NullPointerException} when passed a
 * {@code null} object reference for any input parameter.
 */
class MutableDecimal${scale}f : AbstractMutableDecimal<Scale${scale}f, MutableDecimal${scale}f>, Cloneable {

	/**
	* Constructs a new `MutableDecimal0f` with value zero.
	* @see .zero
	*/
	constructor() : super(0)

	/**
	* Private constructor with unscaled value.
	*
	* @param unscaledValue the unscaled value
	* @param scale        the scale metrics used to distinguish this constructor signature
	* from [.MutableDecimal0f]
	*/
	private constructor(unscaledValue: Long, scale: Scale${scale}f) : super(unscaledValue)

	/**
	 * Translates the string representation of a {@code Decimal} into a
	 * {@code MutableDecimal${scale}f}. The string representation consists 
	 * of an optional sign, {@code '+'} or {@code '-'} , followed by a sequence 
	 * of zero or more decimal digits ("the integer"), optionally followed by a
	 * fraction.
	 * <p>
	 * The fraction consists of a decimal point followed by zero or more decimal
	 * digits. The string must contain at least one digit in either the integer
	 * or the fraction. If the fraction contains more than ${scale} digits, the 
	 * value is rounded using {@link RoundingMode#HALF_UP HALF_UP} rounding. An 
	 * exception is thrown if the value is too large to be represented as a 
	 * {@code MutableDecimal${scale}f}.
	 *
	 * @param value
	 *            String value to convert into a {@code MutableDecimal${scale}f}
	 * @throws NumberFormatException
	 *             if {@code value} does not represent a valid {@code Decimal}
	 *             or if the value is too large to be represented as a 
	 *             {@code MutableDecimal${scale}f}
	 * @see #set(String, RoundingMode)
	 */
	constructor(value: String) : this() {
		set(value)
	}

 	/**
	 * Constructs a {@code MutableDecimal${scale}f} whose value is numerically equal 
	 * to that of the specified {@code long} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal${scale}f}.
	 *
	 * @param value
	 *            long value to convert into a {@code MutableDecimal${scale}f}
	 * @throws IllegalArgumentException
	 *            if {@code value} is too large to be represented as a 
	 *            {@code MutableDecimal${scale}f}
	 */
	constructor(value: Long) : this() {
		set(value)
	}

	/**
	 * Constructs a {@code MutableDecimal${scale}f} whose value is calculated by
	 * rounding the specified {@code double} argument to scale ${scale} using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal${scale}f}. 
	 *
	 * @param value
	 *            double value to convert into a {@code MutableDecimal${scale}f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is NaN or infinite or if the magnitude is too large
	 *             for the double to be represented as a {@code MutableDecimal${scale}f}
	 * @see #set(double, RoundingMode)
	 * @see #set(float)
	 * @see #set(float, RoundingMode)
	 */
	constructor(value: Double) : this() {
		set(value)
	}

	/**
	 * Constructs a {@code MutableDecimal${scale}f} whose value is numerically equal to
	 * that of the specified {@link BigInteger} value. An exception is thrown if the
	 * specified value is too large to be represented as a {@code MutableDecimal${scale}f}.
	 *
	 * @param value
	 *            {@code BigInteger} value to convert into a {@code MutableDecimal${scale}f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal${scale}f}
	 */
	constructor(value: BigInteger) : this() {
		set(value)
	}

	/**
	 * Constructs a {@code MutableDecimal${scale}f} whose value is calculated by
	 * rounding the specified {@link BigDecimal} argument to scale ${scale} using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if the 
	 * specified value is too large to be represented as a {@code MutableDecimal${scale}f}.
	 *
	 * @param value
	 *            {@code BigDecimal} value to convert into a {@code MutableDecimal${scale}f}
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal${scale}f}
	 * @see #set(BigDecimal, RoundingMode)
	 */
	constructor(value: BigDecimal) : this() {
		set(value)
	}

	/**
	 * Constructs a {@code MutableDecimal${scale}f} whose value is numerically equal to
	 * that of the specified {@link Decimal${scale}f} value.
	 *
	 * @param value
	 *            {@code Decimal${scale}f} value to convert into a {@code MutableDecimal${scale}f}
	 */
	constructor(value: Decimal${scale}f) : this(value.unscaledValue(), Decimal${scale}f.METRICS)

	/**
	 * Constructs a {@code MutableDecimal${scale}f} whose value is calculated by
	 * rounding the specified {@link Decimal} argument to scale ${scale} using
	 * {@link RoundingMode#HALF_UP HALF_UP} rounding. An exception is thrown if 
	 * the specified value is too large to be represented as a {@code MutableDecimal${scale}f}. 
	 *
	 * @param value
	 *            Decimal value to convert into a {@code MutableDecimal${scale}f} 
	 * @throws IllegalArgumentException
	 *             if {@code value} is too large to be represented as a {@code MutableDecimal${scale}f}
	 * @see #set(Decimal, RoundingMode)
	 */
	constructor(value: Decimal<*>) : this() {
		setUnscaled(value.unscaledValue(), value.scale)
	}

	override fun create(unscaled: Long): MutableDecimal${scale}f {
		return MutableDecimal${scale}f(unscaled, Decimal${scale}f.METRICS)
	}

	override fun createArray(length: Int): Array<MutableDecimal${scale}f?> {
		return arrayOfNulls(length)
	}

	override fun self(): MutableDecimal${scale}f {
		return this
	}

	override val scaleMetrics = Decimal${scale}f.METRICS
	override val scale = Decimal${scale}f.SCALE
	override val factory = Decimal${scale}f.FACTORY
	override val defaultArithmetic = Decimal${scale}f.DEFAULT_ARITHMETIC
	override val defaultCheckedArithmetic = Decimal${scale}f.METRICS.getDefaultCheckedArithmetic()
	override val roundingDownArithmetic = Decimal${scale}f.METRICS.getRoundingDownArithmetic()
	override val roundingFloorArithmetic = Decimal${scale}f.METRICS.getRoundingFloorArithmetic()
	override val roundingHalfEvenArithmetic = Decimal${scale}f.METRICS.getRoundingHalfEvenArithmetic()
	override val roundingUnnecessaryArithmetic = Decimal${scale}f.METRICS.getRoundingUnnecessaryArithmetic()
	override fun clone() = MutableDecimal${scale}f(unscaledValue(), Decimal${scale}f.METRICS)

	/**
	* Returns this `Decimal` as a multipliable factor for exact
	* typed exact multiplication. The second factor is passed to one of
	* the `by(..)` methods of the returned multiplier. The scale of
	* the result is the sum of the scales of `this` Decimal and the
	* second factor passed to the `by(..)` method.
	*
	*
	* The method is similar to [multiplyExact(Decimal)][.multiplyExact] but the result
	* is retrieved in exact typed form with the correct result scale.
	*
	*
	* For instance one can write:
	* <pre>
     * Decimal2f product = this.multiplyExact().by(Decimal2f.FIVE);
    </pre> *
	*
	* @return a multipliable object encapsulating this Decimal as first factor
	* of an exact multiplication
	*/
	fun multiplyExact(): Multipliable${scale}f {
		return Multipliable${scale}f(this)
	}

	override fun toImmutableDecimal(): ImmutableDecimal<Scale${scale}f> {
		return valueOf(this)
	}

	override fun toMutableDecimal(): MutableDecimal${scale}f {
		return this
	}

	companion object {
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to
		 * <code>unscaledValue * 10<sup>-${scale}</sup></code>.
		 *
		 * @param unscaledValue
		 *            the unscaled decimal value to convert
		 * @return a new {@code MutableDecimal${scale}f} value initialised with <code>unscaledValue * 10<sup>-${scale}</sup></code>
		 * @see #setUnscaled(long, int)
		 * @see #setUnscaled(long, int, RoundingMode)
		 */
		@JvmStatic
		fun unscaled(unscaledValue: Long): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(unscaledValue, Decimal${scale}f.METRICS)
		}


		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to zero.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 0.
		 */
		@JvmStatic
		fun zero(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f()
		}

		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one ULP.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>-${scale}</sup><#if scale==0>=1</#if>.
		 */
		@JvmStatic
		fun  ulp(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.ULP);
		}

		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 1.
		 */
		@JvmStatic
		fun  one(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.ONE);
		}

		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to two.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 2.
		 */
		@JvmStatic
		fun  two(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.TWO);
		}

		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to three.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 3.
		 */
		@JvmStatic
		fun  three(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.THREE);
		}

		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to four.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 4.
		 */
		@JvmStatic
		fun  four(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.FOUR);
		}

		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to five.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 5.
		 */
		@JvmStatic
		fun  five(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.FIVE);
		}

		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to six.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 6.
		 */
 		@JvmStatic
		fun  six(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.SIX);
		}

		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to seven.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 7.
		 */
		@JvmStatic
		fun  seven(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.SEVEN);
		}

		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to eight.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 8.
		 */
		@JvmStatic
		fun  eight(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.EIGHT);
		}

		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to nine.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 9.
		 */
		@JvmStatic
		fun  nine(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.NINE);
		}

<#if (scale <= 17)>
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to ten.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 10.
		 */
		@JvmStatic
		fun  ten(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.TEN);
		}
<#if (scale <= 16)>
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one hundred.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 100.
		 */
		@JvmStatic
		fun  hundred(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.HUNDRED);
		}
<#if (scale <= 15)>
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one thousand.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 1000.
		 */
		@JvmStatic
		fun  thousand(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.THOUSAND);
		}
<#if (scale <= 12)>
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one million.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>6</sup>.
		 */
		@JvmStatic
		fun  million(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.MILLION);
		}
<#if (scale <= 9)>
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one billion.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>9</sup>.
		 */
		@JvmStatic
		fun  billion(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.BILLION);
		}
<#if (scale <= 6)>
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one trillion.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>12</sup>.
		 */
		@JvmStatic
		fun  trillion(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.TRILLION);
		}
<#if (scale <= 3)>
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one quadrillion.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>15</sup>.
		 */
		@JvmStatic
		fun  quadrillion(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.QUADRILLION);
		}
<#if (scale <= 0)>
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one quintillion.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>18</sup>.
		 */
		@JvmStatic
		fun  quintillion(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.QUINTILLION);
		}
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>

		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to minus one.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with -1.
		 */
		@JvmStatic
		fun  minusOne(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.MINUS_ONE);
		}

<#if (scale >= 1)>
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one half.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 0.5.
		 */
		@JvmStatic
		fun  half(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.HALF);
		}

		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one tenth.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 0.1.
		 */
		@JvmStatic
		fun  tenth(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.TENTH);
		}

<#if (scale >= 2)>
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one hundredth.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 0.01.
		 */
		@JvmStatic
		fun  hundredth(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.HUNDREDTH);
		}

<#if (scale >= 3)>
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one thousandth.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 0.001.
		 */
		@JvmStatic
		fun  thousandth(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.THOUSANDTH);
		}

<#if (scale >= 6)>
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one millionth.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>-6</sup>.
		 */
		@JvmStatic
		fun  millionth(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.MILLIONTH);
		}

<#if (scale >= 9)>
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one billionth.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>-9</sup>.
		 */
		@JvmStatic
		fun  billionth(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.BILLIONTH);
		}

<#if (scale >= 12)>
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one trillionth.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>-12</sup>.
		 */
		@JvmStatic
		fun  trillionth(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.TRILLIONTH);
		}

<#if (scale >= 15)>
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one quadrillionth.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>-15</sup>.
		 */
		@JvmStatic
		fun  quadrillionth(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.QUADRILLIONTH);
		}

<#if (scale >= 18)>
		/**
		 * Returns a new {@code MutableDecimal${scale}f} whose value is equal to one quintillionth.
		 *
		 * @return a new {@code MutableDecimal${scale}f} value initialised with 10<sup>-18</sup>.
		 */
		@JvmStatic
		fun  quintillionth(): MutableDecimal${scale}f {
			return MutableDecimal${scale}f(Decimal${scale}f.QUINTILLIONTH);
		}
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>
</#if>


	}
}