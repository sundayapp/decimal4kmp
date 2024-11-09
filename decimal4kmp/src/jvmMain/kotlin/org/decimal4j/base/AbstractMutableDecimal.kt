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
package org.decimal4j.base

import org.decimal4j.api.Decimal
import org.decimal4j.api.MutableDecimal
import org.decimal4j.arithmetic.Exceptions.newArithmeticExceptionWithCause
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales
import java.math.BigDecimal
import java.math.BigInteger
import org.decimal4j.api.RoundingMode

/**
 * Base class for mutable [Decimal] classes of different scales.
 *
 * @param <S>
 * the scale metrics type associated with this Decimal
 * @param <D>
 * the concrete class implementing this `MutableDecimal`
</D></S> */
abstract class AbstractMutableDecimal<S : ScaleMetrics, D : AbstractMutableDecimal<S, D>>
/**
 * Constructor with unscaled value.
 *
 * @param unscaled
 * the unscaled value
 */(private var unscaled: Long) : AbstractDecimal<S, D>(), MutableDecimal<S> {
    override fun unscaledValue(): Long {
        return unscaled
    }

    /**
     * Returns `this` decimal after assigning the value
     * `(unscaled  10<sup>-scale</sup>)`.
     *
     * @param unscaled
     * unscaled value to assign to this `Decimal`
     * @return `this` decimal value now representing
     * `(unscaled  10<sup>-scale</sup>)`
     */
    override fun createOrAssign(unscaled: Long): D {
        this.unscaled = unscaled
        return self()
    }

    override fun scale(scale: Int): MutableDecimal<*> {
        return scale(scale, RoundingMode.HALF_UP)
    }

    override fun <S : ScaleMetrics> scale(scaleMetrics: S): MutableDecimal<S> {
        return scale(scaleMetrics, RoundingMode.HALF_UP)
    }

    override fun scale(scale: Int, roundingMode: RoundingMode): MutableDecimal<*> {
        val myScale = this.scale
        if (scale == myScale) {
            return this
        }
        val targetMetrics = Scales.getScaleMetrics(scale)
        try {
            val targetUnscaled = targetMetrics.getArithmetic(roundingMode).fromUnscaled(unscaled, myScale)
            return factory.deriveFactory(scale).newMutable().setUnscaled(targetUnscaled)
        } catch (e: IllegalArgumentException) {
            throw newArithmeticExceptionWithCause("Overflow: cannot convert $this to scale $scale", e)
        }
    }

    override fun <S : ScaleMetrics> scale(scaleMetrics: S, roundingMode: RoundingMode): MutableDecimal<S> {
        if (scaleMetrics === this.scaleMetrics) {
            val self = this as MutableDecimal<S>
            return self
        }
        try {
            val targetUnscaled = scaleMetrics!!.getArithmetic(roundingMode).fromUnscaled(unscaled, scale)
            return factory.deriveFactory(scaleMetrics).newMutable().setUnscaled(targetUnscaled)
        } catch (e: IllegalArgumentException) {
            throw newArithmeticExceptionWithCause(
                "Overflow: cannot convert " + this + " to scale " + scaleMetrics!!.getScale(),
                e
            )
        }
    }

    override fun multiplyExact(multiplicand: Decimal<*>): MutableDecimal<*> {
        val targetScale = scale + multiplicand.scale
        require(targetScale <= Scales.MAX_SCALE) { "sum of scales exceeds max scale: " + targetScale + " > " + Scales.MAX_SCALE }
        try {
            val unscaledProduct =
                getCheckedArithmeticFor(RoundingMode.DOWN).multiplyByLong(unscaled, multiplicand.unscaledValue())
            return factory.deriveFactory(targetScale).newMutable().setUnscaled(unscaledProduct)
        } catch (e: ArithmeticException) {
            throw ArithmeticException("Overflow: $this * $multiplicand")
        }
    }

    override fun setZero(): D {
        unscaled = 0
        return self()
    }

    override fun setOne(): D {
        unscaled = scaleMetrics!!.getScaleFactor()
        return self()
    }

    override fun setMinusOne(): D {
        unscaled = -scaleMetrics!!.getScaleFactor()
        return self()
    }

    override fun setUlp(): D {
        unscaled = 1
        return self()
    }

    override fun set(value: Decimal<S>): D {
        return setUnscaled(value.unscaledValue())
    }

    override fun set(value: Decimal<*>, roundingMode: RoundingMode): D {
        return setUnscaled(value.unscaledValue(), value.scale, roundingMode)
    }

    override fun set(value: Long): D {
        unscaled = defaultCheckedArithmetic.fromLong(value)
        return self()
    }

    override fun set(value: BigInteger): D {
        unscaled = defaultCheckedArithmetic.fromBigInteger(value)
        return self()
    }

    override fun set(value: Float): D {
        unscaled = defaultCheckedArithmetic.fromFloat(value)
        return self()
    }

    override fun set(value: Float, roundingMode: RoundingMode): D {
        unscaled = getCheckedArithmeticFor(roundingMode).fromFloat(value)
        return self()
    }

    override fun set(value: Double): D {
        unscaled = defaultCheckedArithmetic.fromDouble(value)
        return self()
    }

    override fun set(value: Double, roundingMode: RoundingMode): D {
        unscaled = getCheckedArithmeticFor(roundingMode).fromDouble(value)
        return self()
    }

    override fun set(value: BigDecimal): D {
        unscaled = defaultCheckedArithmetic.fromBigDecimal(value)
        return self()
    }

    override fun set(value: BigDecimal, roundingMode: RoundingMode): D {
        unscaled = getCheckedArithmeticFor(roundingMode).fromBigDecimal(value)
        return self()
    }

    override fun setUnscaled(unscaledValue: Long): D {
        unscaled = unscaledValue
        return self()
    }

    override fun setUnscaled(unscaledValue: Long, scale: Int): D {
        unscaled = defaultCheckedArithmetic.fromUnscaled(unscaledValue, scale)
        return self()
    }

    override fun setUnscaled(unscaledValue: Long, scale: Int, roundingMode: RoundingMode): D {
        unscaled = getCheckedArithmeticFor(roundingMode).fromUnscaled(unscaledValue, scale)
        return self()
    }

    override fun set(value: String): D {
        unscaled = defaultCheckedArithmetic.parse(value)
        return self()
    }

    override fun set(value: String, roundingMode: RoundingMode): D {
        unscaled = getCheckedArithmeticFor(roundingMode).parse(value)
        return self()
    }

    override fun min(`val`: MutableDecimal<S>): MutableDecimal<S> {
        return if (isLessThanOrEqualTo(`val`)) this else `val`
    }

    override fun max(`val`: MutableDecimal<S>): MutableDecimal<S> {
        return if (isGreaterThanOrEqualTo(`val`)) this else `val`
    }

    abstract override fun clone(): D
}
