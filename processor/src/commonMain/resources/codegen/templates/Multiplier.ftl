package org.decimal4j.exact;

import org.decimal4j.immutable.*
import org.decimal4j.mutable.*

/**
 * {@code Multiplier} provides static {@code multiplyExact(..)} methods for 
 * {@link org.decimal4j.api.Decimal Decimal} values of different scales. The multipliable
 * object returned by those methods encapsulates the Decimal argument and facilitates
 * exact typed multiplication. The multipliable object acts as first factor in the multiplication
 * and provides a set of overloaded methods for different scales. Each one of those methods 
 * delivers a different result scale which represents the appropriate scale for the product of
 * an exact multiplication.
 * <p>
 * An exact typed multiplication can for instance be written as:
 * <pre>
 * Decimal&lt;Scale5f&gt; value = ... //some value
 * Decimal7f product7 = Multiplier.multiplyExact(value).by(Decimal2f.FIVE);
 * Decimal9f product9 = Multiplier.multiplyExact(value).by(Decimal4f.NINE);
 * </pre>
 */
object Multiplier {
<#list 0..maxScale as s>

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	fun multiplyExact(value: Decimal${s}f): Multipliable${s}f {
		return Multipliable${s}f(value);
	}

	/**
	 * Returns the {@code value} argument as a multipliable factor for typed 
	 * exact multiplication. The second factor is passed to one of the
	 * {@code by(..)} methods of the returned multiplier. The scale of
	 * the result is the sum of the scales of the {@code value} and the
	 * second factor passed to the {@code by(..)} method.
	 * 
	 * @param value the first factor of the multiplication to be wrapped as a 
	 * 				multipliable object
	 * @return a multipliable object encapsulating {@code value} as first factor
	 *				of an exact multiplication
	 */
	fun multiplyExact(value: MutableDecimal${s}f): Multipliable${s}f {
		return Multipliable${s}f(value);
	}
</#list>
}