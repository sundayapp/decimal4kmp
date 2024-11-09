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
package org.decimal4j.generic

import org.decimal4j.api.Decimal
import org.decimal4j.factory.Factories
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.scale.Scales
import org.hamcrest.CoreMatchers
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.random.Random

/**
 * Unit test for [GenericImmutableDecimal] and
 * [GenericMutableDecimal].
 */
@RunWith(Parameterized::class)
class GenericDecimalTest(private val scaleMetrics: ScaleMetrics) {


    @Test
    fun shouldCreateGenericValueZero() {
        // when
        val immutable: GenericImmutableDecimal<*> = GenericImmutableDecimal.valueOfUnscaled(scaleMetrics, 0)
        val mutable: GenericMutableDecimal<*> = GenericMutableDecimal.valueOfUnscaled(scaleMetrics, 0)

        // then
        assertBase(immutable)
        assertBase(mutable)
        Assert.assertTrue("immutable should be zero", immutable.isZero())
        Assert.assertTrue("mutable should be zero", mutable.isZero())
        Assert.assertEquals("immutable and mutable value should be equal", immutable, mutable)
    }

    @Test
    fun shouldCreateGenericValue() {
        // given
        val unscaled = RND.nextLong()

        // when
        val immutable: GenericImmutableDecimal<*> = GenericImmutableDecimal.valueOfUnscaled(scaleMetrics, unscaled)
        val mutable: GenericMutableDecimal<*> = GenericMutableDecimal.valueOfUnscaled(scaleMetrics, unscaled)

        // then
        assertBase(immutable)
        assertBase(mutable)
        Assert.assertEquals("immutable should have unscaled value $unscaled", unscaled, immutable.unscaledValue())
        Assert.assertEquals("mutable should have unscaled value $unscaled", unscaled, mutable.unscaledValue())
        Assert.assertEquals("immutable and mutable value should be equal", immutable, mutable)
    }

    @Test
    fun shouldCreateGenericValueArray() {
        for (sm in Scales.VALUES) {
            // given
            val len = RND.nextInt(100)

            // when
            val immutables = Factories.getGenericDecimalFactory(sm).newArray(len)
            val mutables = Factories.getGenericDecimalFactory(sm).newMutableArray(len)

            // then
            Assert.assertEquals("immutable array should have length $len", len.toLong(), immutables.size.toLong())
            Assert.assertEquals("mutable array should have length $len", len.toLong(), mutables.size.toLong())

            for (i in 0 until len) {
                Assert.assertNull("immutables[$i' should be null", immutables[i])
                Assert.assertNull("mutables[$i' should be null", mutables[i])
            }
        }
    }

    @Test
    fun shouldCloneGenericValue() {
        // given
        val immutable: GenericImmutableDecimal<*> = GenericImmutableDecimal.valueOfUnscaled(
            scaleMetrics,
            RND.nextLong()
        )
        val mutable: GenericMutableDecimal<*> = GenericMutableDecimal.valueOfUnscaled(scaleMetrics, RND.nextLong())

        // when
        val immutableCloneI = GenericImmutableDecimal.valueOf(immutable)
        val immutableCloneM = GenericImmutableDecimal.valueOf(mutable)
        val mutableCloneI = GenericMutableDecimal.valueOf(immutable)
        val mutableCloneM = GenericMutableDecimal.valueOf(mutable)
        val mutableCloneII = mutableCloneI.clone()
        val mutableCloneMM = mutableCloneM.clone()

        // then
        assertBase(immutableCloneI)
        assertBase(immutableCloneM)
        assertBase(mutableCloneI)
        assertBase(mutableCloneM)
        assertBase(mutableCloneII)
        assertBase(mutableCloneMM)
        Assert.assertEquals("immutableCloneI should be equal to immutable", immutable, immutableCloneI)
        Assert.assertEquals("immutableCloneM should be equal to mutable", mutable, immutableCloneM)
        Assert.assertEquals("mutableCloneI should be equal to immutable", immutable, mutableCloneI)
        Assert.assertEquals("mutableCloneM should be equal to mutable", mutable, mutableCloneM)
        Assert.assertEquals("mutableCloneII should be equal to immutable", immutable, mutableCloneII)
        Assert.assertEquals("mutableCloneMM should be equal to mutable", mutable, mutableCloneMM)
    }

    @Test
    fun shouldScaleGenericValue() {
        // given
        val oldScale = scaleMetrics.getScale()
        val newScale = RND.nextInt(Scales.MAX_SCALE + 1)
        val immutable: GenericImmutableDecimal<*>
        val mutable: GenericMutableDecimal<*>
        if (newScale > oldScale) {
            val diffMetrics = Scales.getScaleMetrics(newScale - oldScale)
            val maxVal = diffMetrics.getMaxIntegerValue()
            immutable =
                GenericImmutableDecimal.valueOfUnscaled(scaleMetrics, RND.nextLong(maxVal) - RND.nextLong(maxVal))
            mutable = GenericMutableDecimal.valueOfUnscaled(scaleMetrics, RND.nextLong(maxVal) - RND.nextLong(maxVal))
        } else {
            immutable = GenericImmutableDecimal.valueOfUnscaled(scaleMetrics, RND.nextLong())
            mutable = GenericMutableDecimal.valueOfUnscaled(scaleMetrics, RND.nextLong())
        }

        // when
        val scaledI = immutable.scale(newScale)
        val scaledM = mutable.scale(newScale)

        // then
        assertBase(Scales.getScaleMetrics(newScale), scaledI)
        assertBase(Scales.getScaleMetrics(newScale), scaledM)
        Assert.assertThat(
            "should be instance of GenericImmutableDecimal", scaledI, CoreMatchers.instanceOf(
                GenericImmutableDecimal::class.java
            )
        )
        Assert.assertThat(
            "should be instance of GenericMutableDecimal", scaledM, CoreMatchers.instanceOf(
                GenericMutableDecimal::class.java
            )
        )
    }

    @Test
    fun shouldMultiplyExactToGenericValue() {
        // given
        val immutable: GenericImmutableDecimal<*> = GenericImmutableDecimal.valueOfUnscaled(
            scaleMetrics,
            RND.nextInt().toLong()
        )
        val mutable: GenericMutableDecimal<*> =
            GenericMutableDecimal.valueOfUnscaled(scaleMetrics, RND.nextInt().toLong())
        val scaleI = RND.nextInt(18 - immutable.scale + 1)
        val scaleM = RND.nextInt(18 - mutable.scale + 1)
        val factorI = GenericImmutableDecimal.valueOfUnscaled(scaleI, RND.nextInt().toLong())
        val factorM = GenericMutableDecimal.valueOfUnscaled(scaleM, RND.nextInt().toLong())

        // when
        val productI: Decimal<*> = immutable.multiplyExact(factorI)
        val productM: Decimal<*> = mutable.multiplyExact(factorM)

        // then
        assertBase(Scales.getScaleMetrics(scaleMetrics.getScale() + scaleI), productI)
        assertBase(Scales.getScaleMetrics(scaleMetrics.getScale() + scaleM), productM)
        Assert.assertThat(
            "should be instance of GenericImmutableDecimal", productI, CoreMatchers.instanceOf(
                GenericImmutableDecimal::class.java
            )
        )
        Assert.assertThat(
            "should be instance of GenericMutableDecimal", productM, CoreMatchers.instanceOf(
                GenericMutableDecimal::class.java
            )
        )
        Assert.assertEquals(
            "productI should be sum of factor scales",
            (scaleMetrics.getScale() + scaleI).toLong(),
            productI.scale.toLong()
        )
        Assert.assertEquals(
            "productM should be sum of factor scales",
            (scaleMetrics.getScale() + scaleM).toLong(),
            productM.scale.toLong()
        )
        Assert.assertEquals(
            "productI should be long product of factors", immutable.unscaledValue() * factorI.unscaledValue(),
            productI.unscaledValue()
        )
        Assert.assertEquals(
            "productM should be long product of factors", mutable.unscaledValue() * factorM.unscaledValue(),
            productM.unscaledValue()
        )
    }

    private fun assertBase(decimal: Decimal<*>) {
        assertBase(scaleMetrics, decimal)
    }

    companion object {
        private val RND = Random.Default

        @JvmStatic
        @get:Parameterized.Parameters(name = "{index}: {0}")
        val parameters: Collection<Array<Any>>
            get() {
                val params: MutableList<Array<Any>> = ArrayList()
                for (sm in Scales.VALUES) {
                    params.add(arrayOf(sm))
                }
                return params
            }

        private fun assertBase(scaleMetrics: ScaleMetrics, decimal: Decimal<*>) {
            Assert.assertNotNull("decimal should not be null", decimal)
            Assert.assertEquals(
                "decimal should have scale " + scaleMetrics.getScale(), scaleMetrics.getScale().toLong(),
                decimal.scale.toLong()
            )
            Assert.assertSame(
                "decimal should have default scale metrics instance" + scaleMetrics.getScale(), scaleMetrics,
                decimal.scaleMetrics
            )
            Assert.assertSame(
                "decimal should have default factory instance", Factories.getGenericDecimalFactory(scaleMetrics),
                decimal.factory
            )
        }
    }
}
