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

import org.decimal4j.arithmetic.Checked.add
import org.decimal4j.arithmetic.Checked.multiplyByLong
import org.decimal4j.arithmetic.Checked.subtract
import org.decimal4j.arithmetic.StringConversion.longToString
import org.decimal4j.scale.Scale0f

/**
 * Base class for arithmetic implementations with overflow check for the special
 * case with [Scale0f], that is, for longs.
 */
abstract class AbstractCheckedScale0fArithmetic : AbstractCheckedArithmetic() {
    override val scaleMetrics =  Scale0f.INSTANCE

    override val scale = 0

    override fun one(): Long {
        return 1
    }

    override fun addLong(uDecimal: Long, lValue: Long): Long {
        return add(this, uDecimal, lValue)
    }

    override fun subtractLong(uDecimal: Long, lValue: Long): Long {
        return subtract(this, uDecimal, lValue)
    }

    override fun multiply(uDecimal1: Long, uDecimal2: Long): Long {
        return multiplyByLong(this, uDecimal1, uDecimal2)
    }

    override fun square(uDecimal: Long): Long {
        return multiplyByLong(this, uDecimal, uDecimal)
    }

    override fun fromLong(value: Long): Long {
        return value
    }

    override fun toLong(uDecimal: Long): Long {
        return uDecimal
    }

    override fun toString(uDecimal: Long): String {
        return longToString(uDecimal)
    }

    override fun toString(uDecimal: Long, appendable: Appendable) {
        longToString(uDecimal, appendable)
    }
}
