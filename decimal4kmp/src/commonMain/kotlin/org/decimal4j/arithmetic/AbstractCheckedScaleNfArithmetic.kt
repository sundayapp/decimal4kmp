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

import org.decimal4j.arithmetic.Add.addUnscaledLongChecked
import org.decimal4j.arithmetic.StringConversion.unscaledToString
import org.decimal4j.arithmetic.Sub.subtractUnscaledLongChecked
import org.decimal4j.scale.ScaleMetrics

/**
 * Base class for arithmetic implementations with overflow check for scales
 * other than zero.
 */
abstract class AbstractCheckedScaleNfArithmetic
/**
 * Constructor with scale metrics for this arithmetic.
 *
 * @param scaleMetrics
 * the scale metrics
 */(private val scaleMetrics2: ScaleMetrics) : AbstractCheckedArithmetic() {
    override val scaleMetrics =  scaleMetrics2

    override val scale = scaleMetrics2.getScale()

    override fun one(): Long {
        return scaleMetrics.getScaleFactor()
    }

    override fun addLong(uDecimal: Long, lValue: Long): Long {
        return addUnscaledLongChecked(this, uDecimal, lValue)
    }

    override fun subtractLong(uDecimal: Long, lValue: Long): Long {
        return subtractUnscaledLongChecked(this, uDecimal, lValue)
    }

    override fun toString(uDecimal: Long): String {
        return unscaledToString(this, uDecimal)
    }

    override fun toString(uDecimal: Long, appendable: Appendable) {
        unscaledToString(this, uDecimal, appendable)
    }
}
