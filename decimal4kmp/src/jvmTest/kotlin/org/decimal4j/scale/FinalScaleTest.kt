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
package org.decimal4j.scale

import org.decimal4j.test.AbstractFinalTest
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.lang.reflect.Field
import java.lang.reflect.Modifier

/**
 * Unit test enforcing that all methods and fields of [Scales] and of
 * [ScaleMetrics] implementations are final.
 */
@RunWith(Parameterized::class)
class FinalScaleTest(private val clazz: Class<*>) : AbstractFinalTest() {
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
        return false
    }

    override fun isAllowedNonFinalField(field: Field): Boolean {
        return false
    }

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{index}: {0}")
        fun data(): Iterable<Array<Any>> {
            val data: MutableList<Array<Any>> = ArrayList()
            data.add(arrayOf(Scales::class.java))
            data.add(arrayOf(Scale0f::class.java))
            data.add(arrayOf(Scale1f::class.java))
            data.add(arrayOf(Scale2f::class.java))
            data.add(arrayOf(Scale3f::class.java))
            data.add(arrayOf(Scale4f::class.java))
            data.add(arrayOf(Scale5f::class.java))
            data.add(arrayOf(Scale6f::class.java))
            data.add(arrayOf(Scale7f::class.java))
            data.add(arrayOf(Scale8f::class.java))
            data.add(arrayOf(Scale9f::class.java))
            data.add(arrayOf(Scale10f::class.java))
            data.add(arrayOf(Scale11f::class.java))
            data.add(arrayOf(Scale12f::class.java))
            data.add(arrayOf(Scale13f::class.java))
            data.add(arrayOf(Scale14f::class.java))
            data.add(arrayOf(Scale15f::class.java))
            data.add(arrayOf(Scale16f::class.java))
            data.add(arrayOf(Scale17f::class.java))
            data.add(arrayOf(Scale18f::class.java))
            return data
        }
    }
}
