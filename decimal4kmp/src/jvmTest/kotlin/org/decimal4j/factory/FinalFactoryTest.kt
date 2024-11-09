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
package org.decimal4j.factory

import org.decimal4j.test.AbstractFinalTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.lang.reflect.Modifier

/**
 * Unit test enforcing that all methods and fields of [Factories] and all
 * [DecimalFactory] implementations are final.
 */
@RunWith(Parameterized::class)
class FinalFactoryTest(private val clazz: Class<*>) : AbstractFinalTest() {
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

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            data.add(arrayOf(Factories::class.java))
            data.add(arrayOf(Factory0f::class.java))
            data.add(arrayOf(Factory1f::class.java))
            data.add(arrayOf(Factory2f::class.java))
            data.add(arrayOf(Factory3f::class.java))
            data.add(arrayOf(Factory4f::class.java))
            data.add(arrayOf(Factory5f::class.java))
            data.add(arrayOf(Factory6f::class.java))
            data.add(arrayOf(Factory7f::class.java))
            data.add(arrayOf(Factory8f::class.java))
            data.add(arrayOf(Factory9f::class.java))
            data.add(arrayOf(Factory10f::class.java))
            data.add(arrayOf(Factory11f::class.java))
            data.add(arrayOf(Factory12f::class.java))
            data.add(arrayOf(Factory13f::class.java))
            data.add(arrayOf(Factory14f::class.java))
            data.add(arrayOf(Factory15f::class.java))
            data.add(arrayOf(Factory16f::class.java))
            data.add(arrayOf(Factory17f::class.java))
            data.add(arrayOf(Factory18f::class.java))
            return data
        }
    }
}
