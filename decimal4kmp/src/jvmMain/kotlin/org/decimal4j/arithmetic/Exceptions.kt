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

/**
 * Utility for exception conversion and re-throwing.
 */
object Exceptions {
    private const val ROUNDING_NECESSARY = "Rounding necessary"

    /**
     * Returns a new [ArithmeticException] with the given `message`
     * and nested `cause`.
     *
     * @param message
     * the exception message
     * @param cause
     * the causing exception
     * @return an arithmetic exception with the given message and cause
     */
    @JvmStatic
    fun newArithmeticExceptionWithCause(message: String?, cause: Exception?): ArithmeticException {
        return ArithmeticException(message).initCause(cause) as ArithmeticException
    }

    /**
     * Returns new [ArithmeticException] indicating that rounding was
     * necessary when attempting to apply rounding with
     * [RoundingMode.UNNECESSARY].
     *
     * @return an arithmetic exception with the message "Rounding necessary"
     */
    @JvmStatic
    fun newRoundingNecessaryArithmeticException(): ArithmeticException {
        return ArithmeticException(ROUNDING_NECESSARY)
    }

    /**
     * Rethrows the given arithmetic exception if its message equals
     * "Rounding necessary". Otherwise the method does nothing.
     *
     * @param e
     * the exception to rethrow if it is of the "Rounding necessary"
     * type
     * @throws ArithmeticException
     * rethrows the given exception `e` if its message equals
     * "Rounding necessary"
     */
    @JvmStatic
    fun rethrowIfRoundingNecessary(e: ArithmeticException) {
        if (ROUNDING_NECESSARY == e.message) {
            throw e
        }
    }
}
