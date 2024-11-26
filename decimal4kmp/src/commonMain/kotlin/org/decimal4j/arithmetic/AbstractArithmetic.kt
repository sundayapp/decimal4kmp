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
package org.decimal4j.arithmetic

import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.arithmetic.Compare.compareUnscaled
import org.decimal4j.scale.Scales
import org.decimal4j.truncate.OverflowMode
import org.decimal4j.truncate.TruncationPolicy
import org.decimal4j.truncate.RoundingMode
import kotlin.math.sign

/**
 * Base class for all arithmetic implementations providing operations which are
 * common irrespective of [scale][.getScale], [ rounding mode][RoundingMode] and [overflow mode][.getOverflowMode].
 */
abstract class AbstractArithmetic : DecimalArithmetic {
    override fun deriveArithmetic(scale: Int): DecimalArithmetic {
        if (scale != this.scale) {
            return Scales.getScaleMetrics(scale).getArithmetic(truncationPolicy)
        }
        return this
    }

    override fun deriveArithmetic(roundingMode: RoundingMode): DecimalArithmetic {
        return deriveArithmetic(roundingMode, overflowMode)
    }

    override fun deriveArithmetic(roundingMode: RoundingMode, overflowMode: OverflowMode): DecimalArithmetic {
        if ((roundingMode != this.roundingMode) or (overflowMode != this.overflowMode)) {
            return if (overflowMode.isChecked) scaleMetrics.getCheckedArithmetic(roundingMode) else scaleMetrics.getArithmetic(
                roundingMode
            )
        }
        return this
    }

    override fun deriveArithmetic(overflowMode: OverflowMode): DecimalArithmetic {
        return deriveArithmetic(this.roundingMode, overflowMode)
    }

    override fun deriveArithmetic(truncationPolicy: TruncationPolicy): DecimalArithmetic {
        return deriveArithmetic(truncationPolicy.getRoundingMode(), truncationPolicy.getOverflowMode())
    }

    override fun signum(uDecimal: Long): Int {
        return uDecimal.sign
    }

    override fun compare(uDecimal1: Long, uDecimal2: Long): Int {
        return uDecimal1.compareTo(uDecimal2)
    }

    override fun compareToUnscaled(uDecimal: Long, unscaled: Long, scale: Int): Int {
        return compareUnscaled(uDecimal, this.scale, unscaled, scale)
    }
}
