package org.decimal4j.exact;

import org.decimal4j.api.Decimal
import org.decimal4j.immutable.*
import org.decimal4j.mutable.*
import org.decimal4j.scale.Scale${scale}f

/**
 * A {@code Multipliable${scale}f} encapsulates a Decimal of scale ${scale} and facilitates
 * exact typed multiplication. The multipliable object acts as first factor in the multiplication
 * and provides a set of overloaded methods for different scales. Each one of those methods 
 * delivers a different result scale which represents the appropriate scale for the product of
 * an exact multiplication.
 * <p>
 * A {@code Multipliable${scale}f} object is returned by {@link Decimal${scale}f#multiplyExact()},
 * hence an exact typed multiplication can be written as:
 * <pre>
 * Decimal${scale}f value = ... //some value
<#if (scale+2<=maxScale)>
 * Decimal${scale+2}f product = value.multiplyExact().by(Decimal2f.FIVE);
<#else>
 * Decimal${scale}f product = value.multiplyExact().by(Decimal0f.FIVE);
</#if>
 * </pre>
 */
class Multipliable${scale}f(private val value: Decimal<Scale${scale}f>) {
	
	/**
	 * Returns the value underlying this Multipliable${scale}f.
	 * @return the Decimal value wrapped by this multipliable object
	 */
	fun getValue(): Decimal<Scale${scale}f> {
		return value;
	}
<#if (scale+scale <= maxScale)>

	/**
	 * Returns a {@code Decimal} whose value is <code>(this<sup>2</sup>)</code>. The
	 * result is exact and has scale ${scale+scale} which is twice the scale of
	 * the Decimal that this multipliable object represents. An
	 * {@link ArithmeticException} is thrown if the product is out of the
	 * possible range for a {@code Decimal${scale+scale}f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @return <code>(this * this)</code>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal${scale+scale}f}
	 */
	fun square(): Decimal${scale+scale}f {
		return by(this.value);
	}

	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale ${scale+scale} which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal${scale+scale}f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <code>(this * factor)</code>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal${scale+scale}f}
	 */
	fun by(factor: Decimal<Scale${scale}f>): Decimal${scale+scale}f {
		return Decimal${scale+scale}f.valueOf(value.multiplyExact(factor));
	}
</#if>
<#list 0..maxScale as scale2>
<#if (scale != scale2) && (scale+scale2 <= maxScale)>

	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale ${scale+scale2} which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal${scale+scale2}f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <code>(this * factor)</code>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal${scale+scale2}f}
	 */
	fun by(factor: Decimal${scale2}f): Decimal${scale+scale2}f {
		return Decimal${scale+scale2}f.valueOf(value.multiplyExact(factor));
	}
	/**
	 * Returns a {@code Decimal} whose value is {@code (this * factor)}. The
	 * result is exact and has scale ${scale+scale2} which is the sum of the scales 
	 * of the Decimal that this multipliable object represents and the scale of
	 * the {@code factor} argument. An {@link ArithmeticException} is thrown if the 
	 * product is out of the possible range for a {@code Decimal${scale+scale2}f}.
	 * <p>
	 * Note that the result is <i>always</i> a new instance.
	 * 
	 * @param factor
	 *            the factor to multiply with the Decimal that this multipliable represents
	 * @return <code>(this * factor)</code>
	 * @throws ArithmeticException
	 *             if an overflow occurs and product is out of the possible
	 *             range for a {@code Decimal${scale+scale2}f}
	 */
	fun by(factor: MutableDecimal${scale2}f): Decimal${scale+scale2}f {
		return Decimal${scale+scale2}f.valueOf(value.multiplyExact(factor));
	}
</#if>
</#list>


	/**
	* Returns a hash code for this `Multipliable${scale}f` which happens to be the
	* hash code of the underlying `Decimal1f` value.
	*
	* @return a hash code value for this object
	* @see Decimal.hashCode
	*/
	override fun hashCode(): Int {
		return value.hashCode()
	}

	/**
	* Compares this Multipliable${scale}f to the specified object. The result is `true`
	* if and only if the argument is a `Multipliable${scale}f` with an equal underlying
	* [value][.getValue].
	*
	* @param obj
	* the object to compare with
	* @return `true` if the argument is a `Multipliable${scale}f` and if its value
	* is equal to this multipliables's value; `false` otherwise
	* @see .getValue
	* @see Decimal.equals
	*/
	override fun equals(obj: Any?): Boolean {
		if (this === obj) return true
		if (obj == null) return false
		if (javaClass != obj.javaClass) return false
		return value.equals((obj as Multipliable${scale}f).value)
	}

	/**
	* Returns a string representation of this `Multipliable${scale}f` which is
	* simply the string representation of the underlying Decimal [value][.getValue].
	*
	* @return a `String` Decimal representation of this `Multipliable${scale}f`'s
	* value with all the fraction digits (including trailing zeros)
	* @see .getValue
	* @see Decimal.toString
	*/
	override fun toString(): String {
		return value.toString()
	}
}