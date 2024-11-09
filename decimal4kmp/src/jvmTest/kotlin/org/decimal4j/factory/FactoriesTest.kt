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

import org.decimal4j.api.ImmutableDecimal
import org.decimal4j.api.MutableDecimal
import org.decimal4j.generic.GenericImmutableDecimal
import org.decimal4j.generic.GenericMutableDecimal
import org.decimal4j.immutable.Decimal0f
import org.decimal4j.mutable.MutableDecimal0f
import org.decimal4j.scale.Scales
import org.junit.Assert
import org.junit.Test

/**
 * Unit test for [Factories] and implementations of [DecimalFactory].
 */
class FactoriesTest {
    @Test
    fun shouldGetFactoryByScale() {
        for (scale in Scales.MIN_SCALE..Scales.MAX_SCALE) {
            //when
            val factory = Factories.getDecimalFactory(scale)
            //then
            Assert.assertNotNull("factory should not be null", factory)
            Assert.assertEquals("factory should have scale $scale", scale.toLong(), factory.scale.toLong())
            Assert.assertEquals("factory should have scale $scale", scale.toLong(), factory.scaleMetrics.getScale().toLong())
            //when
            val generic: DecimalFactory<*> = Factories.getGenericDecimalFactory(scale)
            //then
            Assert.assertNotNull("generic factory should not be null", generic)
            Assert.assertEquals("generic factory should have scale $scale", scale.toLong(), generic.scale.toLong())
            Assert.assertEquals(
                "generic factory should have scale $scale",
                scale.toLong(),
                generic.scaleMetrics.getScale().toLong()
            )
        }
    }

    @Test
    fun shouldGetFactoryByScaleMetrics() {
        for (scaleMetrics in Scales.VALUES) {
            //when
            val factory: DecimalFactory<*> = Factories.getDecimalFactory(scaleMetrics)
            //then
            Assert.assertNotNull("factory should not be null", factory)
            Assert.assertSame("factory should have scale metrics $scaleMetrics", scaleMetrics, factory.scaleMetrics)
            //when
            val generic: DecimalFactory<*> = Factories.getGenericDecimalFactory(scaleMetrics)
            //then
            Assert.assertNotNull("generic factory should not be null", generic)
            Assert.assertSame(
                "generic factory should have scale metrics $scaleMetrics",
                scaleMetrics,
                generic.scaleMetrics
            )
        }
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowExceptionForNegativeScale() {
        Factories.getDecimalFactory(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun genericShouldThrowExceptionForNegativeScale() {
        Factories.getGenericDecimalFactory(-1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun shouldThrowExceptionForScaleExceedingMax() {
        Factories.getDecimalFactory(Scales.MAX_SCALE + 1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun genericShouldThrowExceptionForScaleExceedingMax() {
        Factories.getGenericDecimalFactory(Scales.MAX_SCALE + 1)
    }

    @Test
    @Throws(Exception::class)
    fun shouldCreateDecimalArray() {
        for (sm in Scales.VALUES) {
            //given
            val len = (Math.random() * 100).toInt()

            // when
            val immutables = Factories.getDecimalFactory(sm).newArray(len)
            val mutables = Factories.getDecimalFactory(sm).newMutableArray(len)
            val genericImmutables =
                Factories.getGenericDecimalFactory(sm).newArray(len)
            val genericMutables =
                Factories.getGenericDecimalFactory(sm).newMutableArray(len)


            // then
            Assert.assertEquals("immutable array should have length $len", len.toLong(), immutables.size.toLong())
            Assert.assertEquals("mutable array should have length $len", len.toLong(), mutables.size.toLong())
            Assert.assertEquals(
                "genericImmutables array should have length $len",
                len.toLong(),
                genericImmutables.size.toLong()
            )
            Assert.assertEquals(
                "genericMutables array should have length $len",
                len.toLong(),
                genericMutables.size.toLong()
            )

            for (i in 0 until len) {
                Assert.assertNull("immutables[$i' should be null", immutables[i])
                Assert.assertNull("mutables[$i' should be null", mutables[i])
                Assert.assertNull("genericImmutables[$i' should be null", genericImmutables[i])
                Assert.assertNull("genericMutables[$i' should be null", genericMutables[i])
            }

            val scale = sm.getScale()
            val ipkg = Decimal0f::class.java.getPackage().name
            val mpkg = MutableDecimal0f::class.java.getPackage().name
            Assert.assertEquals(
                "immutable array should have specific component type",
                Class.forName(ipkg + ".Decimal" + scale + "f"),
                immutables.javaClass.componentType
            )
            Assert.assertEquals(
                "mutable array should have specific component type",
                Class.forName(mpkg + ".MutableDecimal" + scale + "f"),
                mutables.javaClass.componentType
            )
        }
    }

    @Test
    fun valuesListShouldBeSortedByScale() {
        Assert.assertEquals(
            "Factories.VALUES size not equal to all scales",
            (Scales.MAX_SCALE - Scales.MIN_SCALE + 1).toLong(),
            Factories.VALUES.size.toLong()
        )
        var scale = 0
        for (factory in Factories.VALUES) {
            Assert.assertEquals("should have scale $scale", scale.toLong(), factory.scaleMetrics.getScale().toLong())
            scale++
        }
    }

    @Test
    @Throws(Exception::class)
    fun testFactorySingleton() {
        for (factory in Factories.VALUES) {
            //when
            val instance = factory.javaClass.getMethod("valueOf", String::class.java).invoke(null, "INSTANCE")
            //then
            Assert.assertSame("should be factory instance", factory, instance)
            //when
            val instances = factory.javaClass.getMethod("values").invoke(null)
            //then
            Assert.assertTrue("should be an array", instances is Array<*> && instances.isArrayOf<Any>())
            Assert.assertEquals("should be array length 1", 1, (instances as Array<Any?>).size.toLong())
            Assert.assertSame("should be same factory", factory, instances[0])
        }
    }
}
