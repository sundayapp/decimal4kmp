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

import org.decimal4j.truncate.OverflowMode
import org.decimal4j.truncate.UncheckedRounding

/**
 * Base class for arithmetic implementations with [ UNCHECKED][OverflowMode.UNCHECKED] overflow mode.
 */
abstract class AbstractUncheckedArithmetic : AbstractArithmetic() {
    //override to refine return type
    abstract override val truncationPolicy: UncheckedRounding

    override val overflowMode: OverflowMode = OverflowMode.UNCHECKED

    override fun abs(uDecimal: Long): Long {
        return kotlin.math.abs(uDecimal)
    }

    override fun negate(uDecimal: Long): Long {
        return -uDecimal
    }

    override fun add(uDecimal1: Long, uDecimal2: Long): Long {
        return uDecimal1 + uDecimal2
    }

    override fun subtract(uDecimalMinuend: Long, uDecimalSubtrahend: Long): Long {
        return uDecimalMinuend - uDecimalSubtrahend
    }

    override fun multiplyByLong(uDecimal: Long, lValue: Long): Long {
        return uDecimal * lValue
    }
}
