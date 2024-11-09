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

import java.math.BigInteger

/**
 * Provides ports of methods that are available in JDK 1.8 to make code run in
 * earlier JDK's.
 */
object JDKSupport {
    /**
     * Copied from `BigInteger.longValueExact()` added in Java 1.8.
     *
     *
     * Converts the `BigInteger` argument to a `long`, checking for lost
     * information. If the value of this `BigInteger` is out of the range
     * of the `long` type, then an `ArithmeticException` is thrown.
     *
     * @param value the `BigInteger` value to convert to a long
     * @return `value` converted to a `long`.
     * @throws ArithmeticException
     * if the `value` will not exactly fit in a `long`.
     * @since JDK 1.8
     */
    fun bigIntegerToLongValueExact(value: BigInteger): Long {
        if (value.bitLength() <= 63) return value.toLong()
        else throw ArithmeticException("BigInteger out of long range: $value")
    }
}
