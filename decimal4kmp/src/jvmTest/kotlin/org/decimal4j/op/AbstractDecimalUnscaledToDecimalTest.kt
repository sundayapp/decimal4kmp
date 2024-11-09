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
import org.decimal4j.op.util.UnscaledUtil.getScales
import org.decimal4j.op.util.UnscaledUtil.getSpecialUnscaledOperands
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales
import org.decimal4j.test.TestSettings
import org.decimal4j.truncate.TruncationPolicy
import org.junit.runners.Parameterized
import java.math.BigDecimal

/**
 * Base class for unit tests with an unscaled decimal operand.
 */
abstract class AbstractDecimalUnscaledToDecimalTest(
    sm: ScaleMetrics?,
    tp: TruncationPolicy?,
    protected val scale: Int,
    arithmetic: DecimalArithmetic
) :
    AbstractDecimalLongValueToDecimalTest(arithmetic) {
    override fun getSpecialLongOperands(): LongArray {
        return getSpecialUnscaledOperands(scale)
    }

    override fun getRandomTestCount(): Int {
        return 1000
    }

    protected fun toBigDecimal(unscaled: Long): BigDecimal {
        require(scale <= Scales.MAX_SCALE) { "illegal scale: $scale" }
        return BigDecimal.valueOf(unscaled, scale)
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}, {1}, scale={2}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            for (s in TestSettings.SCALES) {
                for (tp in TestSettings.POLICIES) {
                    val arith = s.getArithmetic(tp)
                    for (scale in getScales(s)) {
                        data.add(arrayOf(s, tp, scale, arith))
                    }
                }
            }
            return data
        }
    }
}
