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

import org.decimal4j.truncate.DecimalRounding
import org.junit.Assert
import org.junit.Test
import org.decimal4j.truncate.RoundingMode

/**
 * Unit test for [DecimalRounding.valueOf]
 */
class DecimalRoundingValueOfTest {
    @Test
    fun shouldGetByRoundingMode() {
        Assert.assertEquals(
            "different number of constant in RoundingMode and DecimalRounding",
            RoundingMode.entries.size.toLong(),
            DecimalRounding.entries.size.toLong()
        )
        for (mode in RoundingMode.entries) {
            val rounding = DecimalRounding.valueOf(mode)
            Assert.assertSame("wrong RoundingMode in DecimalRounding", mode, rounding.getRoundingMode())
            Assert.assertEquals(
                "constant name of RoundingMode and DecimalRounding does not match",
                mode.name,
                rounding.name
            )
        }
    }
}
