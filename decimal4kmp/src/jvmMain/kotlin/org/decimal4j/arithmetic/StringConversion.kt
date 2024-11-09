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
import org.decimal4j.arithmetic.Checked.add
import org.decimal4j.arithmetic.Exceptions.newRoundingNecessaryArithmeticException
import org.decimal4j.scale.Scales
import org.decimal4j.truncate.DecimalRounding
import org.decimal4j.truncate.TruncatedPart
import kotlin.math.min

/**
 * Contains methods to convert from and to String.
 */
internal object StringConversion {
    /**
     * Thread-local used to build Decimal strings. Allocated big enough to avoid growth.
     */
    @JvmField
    val STRING_BUILDER_THREAD_LOCAL: ThreadLocal<StringBuilder> = object : ThreadLocal<StringBuilder>() {
        override fun initialValue(): StringBuilder {
            return StringBuilder(19 + 1 + 2) // unsigned long: 19 digits,
            // sign: 1, decimal point
            // and leading 0: 2
        }
    }

    /**
     * Parses the given string into a long and returns it, rounding extra digits if necessary.
     *
     * @param arith
     * the arithmetic of the target value
     * @param rounding
     * the rounding to apply if a fraction is present
     * @param s
     * the string to parse
     * @param start
     * the start index to read characters in `s`, inclusive
     * @param end
     * the end index where to stop reading in characters in `s`, exclusive
     * @return the parsed value
     * @throws IndexOutOfBoundsException
     * if `start < 0` or `end > s.length()`
     * @throws NumberFormatException
     * if `value` does not represent a valid `Decimal` or if the value is too large to be
     * represented as a long
     */
    @JvmStatic
    fun parseLong(arith: DecimalArithmetic, rounding: DecimalRounding, s: CharSequence, start: Int, end: Int): Long {
        return parseUnscaledDecimal(arith, rounding, s, start, end)
    }

    /**
     * Parses the given string into an unscaled decimal and returns it, rounding extra digits if necessary.
     *
     * @param arith
     * the arithmetic of the target value
     * @param rounding
     * the rounding to apply if extra fraction digits are present
     * @param s
     * the string to parse
     * @param start
     * the start index to read characters in `s`, inclusive
     * @param end
     * the end index where to stop reading in characters in `s`, exclusive
     * @return the parsed value
     * @throws IndexOutOfBoundsException
     * if `start < 0` or `end > s.length()`
     * @throws NumberFormatException
     * if `value` does not represent a valid `Decimal` or if the value is too large to be
     * represented as a Decimal with the scale of the given arithmetic
     */
    @JvmStatic
    fun parseUnscaledDecimal(
        arith: DecimalArithmetic,
        rounding: DecimalRounding,
        s: CharSequence,
        start: Int,
        end: Int
    ): Long {
        if ((start < 0) or (end > s.length)) {
            throw IndexOutOfBoundsException(
                ("Start or end index is out of bounds: [" + start + ", " + end
                        + " must be <= [0, " + s.length + "]")
            )
        }
        val scaleMetrics = arith.scaleMetrics
        val scale = scaleMetrics.getScale()
        val indexOfDecimalPoint = indexOfDecimalPoint(s, start, end)
        if ((indexOfDecimalPoint == end) and (scale > 0)) {
            throw newNumberFormatExceptionFor(arith, s, start, end)
        }

        // parse a decimal number
        val integralPart: Long // unscaled
        val fractionalPart: Long // scaled
        val truncatedPart: TruncatedPart
        val negative: Boolean
        if (indexOfDecimalPoint < 0) {
            integralPart = parseIntegralPart(arith, s, start, end, ParseMode.Long)
            fractionalPart = 0
            truncatedPart = TruncatedPart.ZERO
            negative = integralPart < 0
        } else {
            val fractionalEnd = min(end.toDouble(), (indexOfDecimalPoint + 1 + scale).toDouble()).toInt()
            if (indexOfDecimalPoint == start) {
                // allowed format .45
                integralPart = 0
                fractionalPart = parseFractionalPart(arith, s, start + 1, fractionalEnd)
                truncatedPart = parseTruncatedPart(arith, s, fractionalEnd, end)
                negative = false
            } else {
                // allowed formats: "0.45", "+0.45", "-0.45", ".45", "+.45",
                // "-.45"
                integralPart = parseIntegralPart(arith, s, start, indexOfDecimalPoint, ParseMode.IntegralPart)
                fractionalPart = parseFractionalPart(arith, s, indexOfDecimalPoint + 1, fractionalEnd)
                truncatedPart = parseTruncatedPart(arith, s, fractionalEnd, end)
                negative = (integralPart < 0) or (integralPart == 0L && s[start] == '-')
            }
        }
        if (truncatedPart.isGreaterThanZero() and (rounding === DecimalRounding.UNNECESSARY)) {
            throw newRoundingNecessaryArithmeticException()
        }
        try {
            val unscaledIntegeral = scaleMetrics.multiplyByScaleFactorExact(integralPart)
            val unscaledFractional = if (negative) -fractionalPart else fractionalPart // < Scale18.SCALE_FACTOR hence
            // no overflow
            val truncatedValue = add(arith, unscaledIntegeral, unscaledFractional)
            val roundingIncrement = rounding.calculateRoundingIncrement(
                if (negative) -1 else 1, truncatedValue,
                truncatedPart
            )
            return if (roundingIncrement == 0) truncatedValue else add(
                arith,
                truncatedValue,
                roundingIncrement.toLong()
            )
        } catch (e: ArithmeticException) {
            throw newNumberFormatExceptionFor(arith, s, start, end, e)
        }
    }

    private fun parseFractionalPart(arith: DecimalArithmetic, s: CharSequence, start: Int, end: Int): Long {
        val len = end - start
        if (len > 0) {
            var i = start
            var value: Long = 0
            while (i < end) {
                val digit = getDigit(arith, s, start, end, s[i++])
                value = value * 10 + digit
            }
            val scale = arith.scale
            if (len < scale) {
                val diffScale = Scales.getScaleMetrics(scale - len)
                return diffScale.multiplyByScaleFactor(value)
            }
            return value
        }
        return 0
    }

    private fun parseTruncatedPart(arith: DecimalArithmetic, s: CharSequence, start: Int, end: Int): TruncatedPart {
        if (start < end) {
            val firstChar = s[start]
            var truncatedPart: TruncatedPart
            truncatedPart = if (firstChar == '0') {
                TruncatedPart.ZERO
            } else if (firstChar == '5') {
                TruncatedPart.EQUAL_TO_HALF
            } else if ((firstChar > '0') and (firstChar < '5')) {
                TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO
            } else if ((firstChar > '5') and (firstChar <= '9')) {
                TruncatedPart.GREATER_THAN_HALF
            } else {
                throw newNumberFormatExceptionFor(arith, s, start, end)
            }
            var i = start + 1
            while (i < end) {
                val ch = s[i++]
                if ((ch > '0') and (ch <= '9')) {
                    if (truncatedPart === TruncatedPart.ZERO) {
                        truncatedPart = TruncatedPart.LESS_THAN_HALF_BUT_NOT_ZERO
                    } else if (truncatedPart === TruncatedPart.EQUAL_TO_HALF) {
                        truncatedPart = TruncatedPart.GREATER_THAN_HALF
                    }
                } else if (ch != '0') {
                    throw newNumberFormatExceptionFor(arith, s, start, end)
                }
            }
            return truncatedPart
        }
        return TruncatedPart.ZERO
    }

    private fun indexOfDecimalPoint(s: CharSequence, start: Int, end: Int): Int {
        for (i in start..<end) {
            if (s[i] == '.') {
                return i
            }
        }
        return -1
    }

    // copied from Long.parseLong(String, int) but for fixed radix 10
    private fun parseIntegralPart(
        arith: DecimalArithmetic,
        s: CharSequence,
        start: Int,
        end: Int,
        mode: ParseMode
    ): Long {
        var result: Long = 0
        var negative = false
        var i = start
        var limit = -Long.MAX_VALUE

        if (end > start) {
            val firstChar = s[start]
            if (firstChar < '0') { // Possible leading "+" or "-"
                if (firstChar == '-') {
                    negative = true
                    limit = Long.MIN_VALUE
                } else {
                    if (firstChar != '+') {
                        // invalid first character
                        throw newNumberFormatExceptionFor(arith, s, start, end)
                    }
                }

                if (end - start == 1) {
                    if (mode == ParseMode.IntegralPart) {
                        // we allow something like "-.75" or "+.75"
                        return 0
                    }
                    // Cannot have lone "+" or "-"
                    throw newNumberFormatExceptionFor(arith, s, start, end)
                }
                i++
            }

            val end2 = end - 1
            while (i < end2) {
                val digit0 = getDigit(arith, s, start, end, s[i++])
                val digit1 = getDigit(arith, s, start, end, s[i++])
                val inc = TENS[digit0] + digit1
                if (result < (-Long.MAX_VALUE / 100)) { //same limit with Long.MIN_VALUE
                    throw newNumberFormatExceptionFor(arith, s, start, end)
                }
                result *= 100
                if (result < limit + inc) {
                    throw newNumberFormatExceptionFor(arith, s, start, end)
                }
                result -= inc.toLong()
            }
            if (i < end) {
                val digit = getDigit(arith, s, start, end, s[i++])
                if (result < (-Long.MAX_VALUE / 10)) { //same limit with Long.MIN_VALUE
                    throw newNumberFormatExceptionFor(arith, s, start, end)
                }
                result *= 10
                if (result < limit + digit) {
                    throw newNumberFormatExceptionFor(arith, s, start, end)
                }
                result -= digit.toLong()
            }
        } else {
            throw newNumberFormatExceptionFor(arith, s, start, end)
        }
        return if (negative) result else -result
    }

    private fun getDigit(
        arith: DecimalArithmetic, s: CharSequence,
        start: Int, end: Int, ch: Char
    ): Int {
        if ((ch >= '0') and (ch <= '9')) {
            return (ch.code - '0'.code)
        } else {
            throw newNumberFormatExceptionFor(arith, s, start, end)
        }
    }

    private val TENS = intArrayOf(0, 10, 20, 30, 40, 50, 60, 70, 80, 90)

    /**
     * Returns a `String` object representing the specified `long`. The argument is converted to signed
     * decimal representation and returned as a string, exactly as if passed to [Long.toString].
     *
     * @param value
     * a `long` to be converted.
     * @return a string representation of the argument in base&nbsp;10.
     */
    @JvmStatic
    fun longToString(value: Long): String {
        return value.toString()
    }

    /**
     * Creates a `String` object representing the specified `long` and appends it to the given
     * `appendable`.
     *
     * @param value
     * a `long` to be converted.
     * @param appendable
     * t the appendable to which the string is to be appended
     * @throws IOException
     * If an I/O error occurs when appending to `appendable`
     */
    @JvmStatic
    fun longToString(value: Long, appendable: Appendable) {
        val sb = STRING_BUILDER_THREAD_LOCAL.get()
        sb.setLength(0)
        sb.append(value)
        appendable.append(sb)
    }

    /**
     * Returns a `String` object representing the specified unscaled Decimal value `uDecimal`. The argument
     * is converted to signed decimal representation and returned as a string with `scale` decimal places event if
     * trailing fraction digits are zero.
     *
     * @param uDecimal
     * a unscaled Decimal to be converted
     * @param arith
     * the decimal arithmetics providing the scale to apply
     * @return a string representation of the argument
     */
    @JvmStatic
    fun unscaledToString(arith: DecimalArithmetic, uDecimal: Long): String {
        return unscaledToStringBuilder(arith, uDecimal).toString()
    }

    /**
     * Constructs a `String` object representing the specified unscaled Decimal value `uDecimal` and appends
     * the constructed string to the given appendable argument. The value is converted to signed decimal representation
     * and converted to a string with `scale` decimal places event if trailing fraction digits are zero.
     *
     * @param uDecimal
     * a unscaled Decimal to be converted to a string
     * @param arith
     * the decimal arithmetics providing the scale to apply
     * @param appendable
     * t the appendable to which the string is to be appended
     * @throws IOException
     * If an I/O error occurs when appending to `appendable`
     */
    @JvmStatic
    fun unscaledToString(arith: DecimalArithmetic, uDecimal: Long, appendable: Appendable) {
        val sb = unscaledToStringBuilder(arith, uDecimal)
        appendable.append(sb)
    }

    private fun unscaledToStringBuilder(arith: DecimalArithmetic, uDecimal: Long): StringBuilder {
        val sb = STRING_BUILDER_THREAD_LOCAL.get()
        sb.setLength(0)

        val scale = arith.scale
        sb.append(uDecimal)
        val len = sb.length
        val negativeOffset = if (uDecimal < 0) 1 else 0
        if (len <= scale + negativeOffset) {
            // Long.MAX_VALUE = 9,223,372,036,854,775,807
            sb.insert(negativeOffset, "0.00000000000000000000", 0, 2 + scale - len + negativeOffset)
        } else {
            sb.insert(len - scale, '.')
        }
        return sb
    }

    private fun newNumberFormatExceptionFor(
        arith: DecimalArithmetic,
        s: CharSequence,
        start: Int,
        end: Int
    ): NumberFormatException {
        return NumberFormatException(
            "Cannot parse Decimal value with scale " + arith.scale + " for input string: \"" + s.subSequence(
                start,
                end
            ) + "\""
        )
    }

    private fun newNumberFormatExceptionFor(
        arith: DecimalArithmetic,
        s: CharSequence,
        start: Int,
        end: Int,
        cause: Exception
    ): NumberFormatException {
        val ex = newNumberFormatExceptionFor(arith, s, start, end)
        ex.initCause(cause)
        return ex
    }

    private enum class ParseMode {
        Long, IntegralPart
    }
}
