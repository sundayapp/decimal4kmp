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
package org.decimal4j.op.compare

import org.decimal4j.api.Decimal
import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.api.ImmutableDecimal
import org.decimal4j.api.MutableDecimal
import org.decimal4j.base.AbstractDecimal
import org.decimal4j.op.AbstractDecimalDecimalToDecimalTest
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.math.BigDecimal
import kotlin.collections.ArrayList

/**
 * Unit test for [Decimal.min] and [Decimal.max]
 * and all its overload variants.
 */
@RunWith(Parameterized::class)
class MinMaxTest(
    scaleMetrics: ScaleMetrics?,
    private val op: Op,
    private val mutability: Mutability,
    private val overloadVariant: OverloadVariant,
    arithmetic: DecimalArithmetic
) :
    AbstractDecimalDecimalToDecimalTest(arithmetic) {
    enum class Op {
        Min, Max
    }

    enum class Mutability {
        Immutable, Mutable
    }

    enum class OverloadVariant {
        Decimal, ImmutableMutable, Concrete
    }

    override fun operation(): String {
        return op.name.lowercase()
    }

    private val isMin: Boolean
        get() = op == Op.Min

    override fun <S : ScaleMetrics> newDecimal(
        scaleMetrics: S,
        unscaled: Long
    ): Decimal<S> {
        val decimal = super.newDecimal(scaleMetrics, unscaled)
        return when (mutability) {
            Mutability.Immutable -> decimal.toImmutableDecimal()
            Mutability.Mutable -> decimal.toMutableDecimal()
        }
    }

    override fun expectedResult(a: BigDecimal, b: BigDecimal): BigDecimal {
        return if (isMin) a.min(b) else a.max(b)
    }

    override fun <S : ScaleMetrics> actualResult(a: Decimal<S>, b: Decimal<S>): Decimal<S> {
        when (overloadVariant) {
            OverloadVariant.Decimal -> return if (isMin) a.min(b) else a.max(b)
            OverloadVariant.ImmutableMutable -> return if (a is ImmutableDecimal<*> && b is ImmutableDecimal<*>) {
                if (isMin) (a as ImmutableDecimal<S>).min(b as ImmutableDecimal<S>) else (a as ImmutableDecimal<S>).max(
                    b as ImmutableDecimal<S>
                )
            } else if (a is MutableDecimal<*> && b is MutableDecimal<*>) {
                if (isMin) (a as MutableDecimal<S>).min(b as MutableDecimal<S>) else (a as MutableDecimal<S>).max(b as MutableDecimal<S>)
            } else {
                throw IllegalArgumentException("a and b should have same mutability")
            }

            OverloadVariant.Concrete -> {
                val type: Class<*> = a.javaClass
                try {
                    return type.getMethod(operation(), AbstractDecimal::class.java).invoke(a, b) as Decimal<S>
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }

            else -> throw RuntimeException("unknown overflow variant: $overloadVariant")
        }
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: scale={0}, op={1}, mutability={2}, overload-variant={3}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                for (op in Op.entries) {
                    for (mutability in Mutability.entries) {
                        for (variant in OverloadVariant.entries) {
                            data.add(arrayOf(s, op, mutability, variant, s.getDefaultArithmetic()))
                        }
                    }
                }
            }
            return data
        }
    }
}
