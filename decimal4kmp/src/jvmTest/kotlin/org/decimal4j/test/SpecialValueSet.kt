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

enum class SpecialValueSet(values: Set<Long>) {
    //for-loop-sets
    MINUS_TEN_TO_TEN(forLoop(-10, 10)),
    MINUS_TWENTY_TO_TWENTY(forLoop(-20, 20)),
    MINUS_HUNDRED_TO_HUNDRED(forLoop(-100, 100)),

    //power-of-10-sets
    POW_10_POSITIVE(powLoop(1, 1000000000000000000L, 10)),
    POW_10_NEGATIVE(neg(POW_10_POSITIVE)),
    POW_10(POW_10_NEGATIVE, POW_10_POSITIVE),

    //min/max-value-sets
    MIN_MAX_LONG_INT(Long.MIN_VALUE, Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong(), Long.MAX_VALUE),
    MIN_MAX_ALL(
        Long.MIN_VALUE,
        Int.MIN_VALUE.toLong(),
        Short.MIN_VALUE.toLong(),
        Byte.MIN_VALUE.toLong(),
        Byte.MAX_VALUE.toLong(),
        Short.MAX_VALUE.toLong(),
        Int.MAX_VALUE.toLong(),
        Long.MAX_VALUE
    ),

    //plus/minus 1 of base sets
    POW_10_PLUS_MINUS_ONE(plus(POW_10, -1, 0, 1)),
    MIN_MAX_LONG_INT_PLUS_MINUS_ONE(plus(MIN_MAX_LONG_INT, -1, 0, 1)),
    MIN_MAX_ALL_PLUS_MINUS_ONE(plus(MIN_MAX_ALL, -1, 0, 1)),

    //half of base sets
    POW_10_HALF(div(POW_10, 2)),
    MIN_MAX_ALL_HALF(div(MIN_MAX_ALL, 2)),
    MIN_MAX_ALL_HALF_PLUS_MINUS_ONE(plus(MIN_MAX_ALL_HALF, -1, 0, 1)),

    //predefined sets
    TINY(MINUS_TEN_TO_TEN, POW_10_POSITIVE, MIN_MAX_LONG_INT),
    SMALL(MINUS_TEN_TO_TEN, POW_10, MIN_MAX_LONG_INT_PLUS_MINUS_ONE),
    STANDARD(MINUS_TWENTY_TO_TWENTY, POW_10, MIN_MAX_ALL_PLUS_MINUS_ONE),
    LARGE(MINUS_TWENTY_TO_TWENTY, POW_10_PLUS_MINUS_ONE, MIN_MAX_ALL_PLUS_MINUS_ONE),
    ALL(
        MINUS_TWENTY_TO_TWENTY,
        POW_10_PLUS_MINUS_ONE,
        POW_10_HALF,
        MIN_MAX_ALL_PLUS_MINUS_ONE,
        MIN_MAX_ALL_HALF_PLUS_MINUS_ONE
    );

    val values: Set<Long> = values.toSortedSet()

    constructor(vararg values: Long) : this(values.toSet())
    constructor(vararg sets: SpecialValueSet) : this(sets.flatMap { it.values }.toSet())

}

private fun forLoop(from: Long, to: Long, increment: Int = 1): Set<Long> {
    val vals = mutableSetOf<Long>()
    var value = from
    while (value <= to) {
        vals.add(value)
        value += increment.toLong()
    }
    return vals
}

private fun powLoop(from: Long, to: Long, factor: Int): Set<Long> {
    val vals = mutableSetOf<Long>()
    var value = from
    while (value <= to) {
        vals.add(value)
        value *= factor.toLong()
    }
    return vals
}

private fun neg(set: SpecialValueSet): Set<Long> {
    return set.values.map { -it }.toSet()
}

private fun plus(set: SpecialValueSet, vararg inc: Long): Set<Long> {
    return set.values.flatMap { value -> inc.map { value + it } }.toSet()
}

private fun div(set: SpecialValueSet, divisor: Long): Set<Long> {
    return set.values.map { it / divisor }.toSet()
}