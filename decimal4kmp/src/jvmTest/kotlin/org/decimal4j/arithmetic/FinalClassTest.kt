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

import org.decimal4j.test.AbstractFinalTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Unit test enforcing that all methods and fields of certain classes are final.
 */
@RunWith(Parameterized::class)
class FinalClassTest
/**
 * Constructor with tested class parameter.
 *
 * @param clazz the class under test
 */(private val clazz: Class<*>) : AbstractFinalTest() {
    @Test
    fun classShouldBeFinal() {
        val mod = clazz.modifiers
        Assert.assertTrue(
            "class should be abstract or final: $clazz",
            Modifier.isAbstract(mod) || Modifier.isFinal(mod)
        )
    }

    @Test
    fun allMethodsShouldBeFinal() {
        assertAllMethodsAreFinal(clazz)
    }

    @Test
    fun allFieldsShouldBeFinal() {
        assertAllFieldsAreFinal(clazz)
    }

    override fun isAllowedNonStaticField(field: Field): Boolean {
        return AbstractArithmetic::class.java.isAssignableFrom(clazz)
    }

    override fun isAllowedNonFinalField(field: Field): Boolean {
        if (UnsignedDecimal9i36f::class.java == clazz) {
            return mutableListOf("norm", "pow10", "ival", "val3", "val2", "val1", "val0").contains(field.name)
        }
        return false
    }

    companion object {
        /**
         * Returns a list with single-element object arrays containing the class under
         * test.
         * @return the parameter data
         */
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            data.add(arrayOf(AbstractArithmetic::class.java))
            data.add(arrayOf(AbstractCheckedArithmetic::class.java))
            data.add(arrayOf(AbstractCheckedScale0fArithmetic::class.java))
            data.add(arrayOf(AbstractCheckedScaleNfArithmetic::class.java))
            data.add(arrayOf(AbstractUncheckedArithmetic::class.java))
            data.add(arrayOf(AbstractUncheckedScale0fArithmetic::class.java))
            data.add(arrayOf(AbstractUncheckedScaleNfArithmetic::class.java))
            data.add(arrayOf(Add::class.java))
            data.add(arrayOf(Avg::class.java))
            data.add(arrayOf(BigDecimalConversion::class.java))
            data.add(arrayOf(BigIntegerConversion::class.java))
            data.add(arrayOf(Checked::class.java))
            data.add(arrayOf(CheckedScale0fRoundingArithmetic::class.java))
            data.add(arrayOf(CheckedScale0fTruncatingArithmetic::class.java))
            data.add(arrayOf(CheckedScaleNfRoundingArithmetic::class.java))
            data.add(arrayOf(CheckedScaleNfTruncatingArithmetic::class.java))
            data.add(arrayOf(Compare::class.java))
            data.add(arrayOf(Div::class.java))
            data.add(arrayOf(DoubleConversion::class.java))
            data.add(arrayOf(Exceptions::class.java))
            data.add(arrayOf(FloatConversion::class.java))
            data.add(arrayOf(Invert::class.java))
            data.add(arrayOf(JDKSupport::class.java))
            data.add(arrayOf(LongConversion::class.java))
            data.add(arrayOf(Mul::class.java))
            data.add(arrayOf(Pow::class.java))
            data.add(arrayOf(Pow10::class.java))
            data.add(arrayOf(Round::class.java))
            data.add(arrayOf(Rounding::class.java))
            data.add(arrayOf(RoundingInverse::class.java))
            data.add(arrayOf(Shift::class.java))
            data.add(arrayOf(SpecialDivisionResult::class.java))
            data.add(arrayOf(SpecialMultiplicationResult::class.java))
            data.add(arrayOf(SpecialPowResult::class.java))
            data.add(arrayOf(Sqrt::class.java))
            data.add(arrayOf(Square::class.java))
            data.add(arrayOf(StringConversion::class.java))
            data.add(arrayOf(Sub::class.java))
            data.add(arrayOf(UncheckedScale0fRoundingArithmetic::class.java))
            data.add(arrayOf(UncheckedScale0fTruncatingArithmetic::class.java))
            data.add(arrayOf(UncheckedScaleNfRoundingArithmetic::class.java))
            data.add(arrayOf(UncheckedScaleNfTruncatingArithmetic::class.java))
            data.add(arrayOf(UnscaledConversion::class.java))
            data.add(arrayOf(Unsigned::class.java))
            data.add(arrayOf(UnsignedDecimal9i36f::class.java))
            return data
        }
    }
}
