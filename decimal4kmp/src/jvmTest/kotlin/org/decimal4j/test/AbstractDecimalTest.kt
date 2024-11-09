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
package org.decimal4j.test

import org.decimal4j.api.Decimal
import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.factory.DecimalFactory
import org.decimal4j.factory.Factories
import org.decimal4j.immutable.Decimal0f
import org.decimal4j.mutable.MutableDecimal0f
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.OverflowMode
import org.decimal4j.truncate.TruncationPolicy
import java.math.MathContext
import org.decimal4j.api.RoundingMode
import org.decimal4j.api.toJavaRoundingMode
import kotlin.random.Random

/**
 * Base class for [Decimal] tests with random and special values.
 */
abstract class AbstractDecimalTest(@JvmField protected val arithmetic: DecimalArithmetic) {
    @JvmField
	protected val mathContextLong64: MathContext = MathContext(19, arithmetic.roundingMode.toJavaRoundingMode())
    @JvmField
	protected val mathContextLong128: MathContext = MathContext(39, arithmetic.roundingMode.toJavaRoundingMode())

    protected fun getArithmeticScale() = arithmetic.scale

    protected val scaleMetrics: ScaleMetrics
        get() = arithmetic.scaleMetrics

    protected val truncationPolicy: TruncationPolicy
        get() = arithmetic.truncationPolicy
    protected val roundingMode: RoundingMode
        get() = arithmetic.roundingMode
    protected val overflowMode: OverflowMode
        get() = arithmetic.overflowMode

    protected val isStandardTruncationPolicy: Boolean
        get() = arithmetic.roundingMode == TruncationPolicy.DEFAULT.getRoundingMode() && arithmetic.overflowMode == TruncationPolicy.DEFAULT.getOverflowMode()

    protected val isRoundingDown: Boolean
        get() = arithmetic.roundingMode == RoundingMode.DOWN
    protected val isRoundingDefault: Boolean
        get() = arithmetic.roundingMode == TruncationPolicy.DEFAULT.getRoundingMode()
    protected val isUnchecked: Boolean
        get() = !arithmetic.overflowMode.isChecked

    protected fun getSpecialValues(scaleMetrics: ScaleMetrics): LongArray {
        return TestSettings.TEST_CASES.getSpecialValuesFor(scaleMetrics)
    }

    protected open fun <S : ScaleMetrics> randomDecimal(scaleMetrics: S): Decimal<S> {
        return newDecimal(scaleMetrics, nextLongOrInt())
    }

    protected open fun <S : ScaleMetrics> newDecimal(scaleMetrics: S, unscaled: Long): Decimal<S> {
        return when (RND.nextInt(4)) {
            0 -> Factories.getDecimalFactory(scaleMetrics).valueOfUnscaled(unscaled)
            1 -> Factories.getDecimalFactory(scaleMetrics).newMutable().setUnscaled(unscaled)
            2 -> Factories.getGenericDecimalFactory(scaleMetrics).valueOfUnscaled(unscaled)
            3 -> Factories.getGenericDecimalFactory(scaleMetrics).newMutable().setUnscaled(unscaled)
            else ->            //should not get here
                throw RuntimeException("random out of bounds")
        }
    }

    protected fun <S : ScaleMetrics> getDecimalFactory(scaleMetrics: S): DecimalFactory<S> {
        return if (RND.nextBoolean()) Factories.getDecimalFactory(scaleMetrics) else Factories.getGenericDecimalFactory(
            scaleMetrics
        )
    }

    protected val immutableClassName: String
        get() = Decimal0f::class.java.name.replace("0f", getArithmeticScale().toString() + "f")

    protected val mutableClassName: String
        get() = MutableDecimal0f::class.java.name.replace("0f", getArithmeticScale().toString() + "f")

    companion object {
        @JvmField
		val RND = Random.Default

        @JvmStatic
		protected fun nextLongOrInt(): Long {
            return if (RND.nextBoolean()) RND.nextLong() else RND.nextInt().toLong()
        }
    }
}
