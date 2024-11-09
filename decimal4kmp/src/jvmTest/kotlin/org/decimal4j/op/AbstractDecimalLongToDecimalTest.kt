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
package org.decimal4j.op

import org.decimal4j.api.DecimalArithmetic
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.test.TestSettings
import org.decimal4j.truncate.OverflowMode
import org.junit.runners.Parameterized
import java.math.BigDecimal

/**
 * Base class for unit tests with a long value operand (not an unscaled
 * decimal).
 */
abstract class AbstractDecimalLongToDecimalTest(sm: ScaleMetrics?, om: OverflowMode?, arithmetic: DecimalArithmetic) :
    AbstractDecimalLongValueToDecimalTest(arithmetic) {
    override fun getSpecialLongOperands(): LongArray {
        return getSpecialValues(scaleMetrics)
    }

    protected fun toBigDecimal(value: Long): BigDecimal {
        return BigDecimal.valueOf(value)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}, {1}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                for (om in OverflowMode.entries) {
                    val arith = if (om.isChecked) s.getDefaultCheckedArithmetic() else s.getDefaultArithmetic()
                    data.add(arrayOf(s, om, arith))
                }
            }
            return data
        }
    }
}
