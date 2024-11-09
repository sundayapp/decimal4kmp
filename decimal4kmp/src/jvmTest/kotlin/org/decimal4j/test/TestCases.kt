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

import org.decimal4j.scale.ScaleMetrics

enum class TestCases(private val specialValueSet: SpecialValueSet) {
    /** Run full set of test cases  */
    ALL(SpecialValueSet.ALL),

    /** Run standard set of test cases  */
    LARGE(SpecialValueSet.LARGE),

    /** Run standard set of test cases  */
    STANDARD(SpecialValueSet.STANDARD),

    /** Run small set of test cases  */
    SMALL(SpecialValueSet.SMALL),

    /** Run tiny set of test cases  */
    TINY(SpecialValueSet.TINY);

    fun getSpecialValuesFor(scaleMetrics: ScaleMetrics): LongArray {
        return getSpecialValuesInternal(
            scaleMetrics, this != TINY,
            specialValueSet
        )
    }

    fun getSpecialValuesFor(scaleMetrics: ScaleMetrics, vararg extras: SpecialValueSet): LongArray {
        return getSpecialValuesInternal(
            scaleMetrics, this != TINY,
            specialValueSet, *extras
        )
    }

    companion object {
        private fun getSpecialValuesInternal(
            scaleMetrics: ScaleMetrics,
            addWithoutFractionalPart: Boolean,
            base: SpecialValueSet,
            vararg extras: SpecialValueSet
        ): LongArray {
            val set = mutableSetOf<Long>()
            add(scaleMetrics, addWithoutFractionalPart, set, base)
            for (extra in extras) {
                add(scaleMetrics, addWithoutFractionalPart, set, extra)
            }
            return set.sorted().toLongArray()
        }

        private fun add(
            scaleMetrics: ScaleMetrics,
            addWithoutFractionalPart: Boolean,
            result: MutableSet<Long>,
            add: SpecialValueSet
        ) {
            for (`val` in add.values) {
                result.add(`val`)
                if (addWithoutFractionalPart) {
                    result.add(`val` - scaleMetrics.moduloByScaleFactor(`val`))
                }
            }
        }
    }
}