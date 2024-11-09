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

import org.decimal4j.generic.GenericDecimalFactory
import org.decimal4j.scale.ScaleMetrics

/**
 * Utility class with static methods to access [DecimalFactory] instances.
 */
object Factories {
    //@formatter:off
    private val FACTORIES = arrayOf<DecimalFactory<*>>(Factory0f.INSTANCE, 
    Factory1f.INSTANCE, 
    Factory2f.INSTANCE, 
    Factory3f.INSTANCE, 
    Factory4f.INSTANCE, 
    Factory5f.INSTANCE, 
    Factory6f.INSTANCE, 
    Factory7f.INSTANCE, 
    Factory8f.INSTANCE, 
    Factory9f.INSTANCE, 
    Factory10f.INSTANCE, 
    Factory11f.INSTANCE, 
    Factory12f.INSTANCE, 
    Factory13f.INSTANCE, 
    Factory14f.INSTANCE, 
    Factory15f.INSTANCE, 
    Factory16f.INSTANCE, 
    Factory17f.INSTANCE, 
    Factory18f.INSTANCE
    )
    
     //@formatter:on
     private val GENERIC_FACTORIES = initGenericFactories()

    private fun initGenericFactories(): Array<GenericDecimalFactory<*>?> {
        val genericFactories: Array<GenericDecimalFactory<*>?> = arrayOfNulls(FACTORIES.size)
        for (i in FACTORIES.indices) {
            genericFactories[i] = GenericDecimalFactory(FACTORIES[i].scaleMetrics)
        }
        return genericFactories
    }

    /**
     * All decimal factory constants in an immutable ordered list:
     * <br></br>
     * `VALUES=[Factory0f.INSTANCE, Factory1f.INSTANCE, ..., Factory18f.INSTANCE]`
     */
    val VALUES = listOf(*FACTORIES)

    /**
     * Returns the `DecimalFactory` constant based on a given scale.
     *
     * @param scale
     * the scale value; must be in `[0,18]` both ends inclusive
     * @return the factory constant corresponding to `scale`
     * @throws IllegalArgumentException
     * if scale is not in `[0, 18]`
     */
	@JvmStatic
	fun getDecimalFactory(scale: Int): DecimalFactory<*> {
        if ((0 <= scale) and (scale <= 18)) {
            return FACTORIES[scale]
        }
        throw IllegalArgumentException("Illegal scale, must be in [0,18] but was: $scale")
    }

    /**
     * Returns the `DecimalFactory` for the given scale metrics.
     *
     * @param scaleMetrics
     * the scale metrics
     * @param <S> the generic type for `scaleMetrics`
     * @return the factory constant corresponding to `scaleMetrics`
    </S> */
    @JvmStatic
    fun <S : ScaleMetrics> getDecimalFactory(scaleMetrics: S): DecimalFactory<S> {
        val factory = getDecimalFactory(scaleMetrics!!.getScale()) as DecimalFactory<S>
        return factory
    }

    /**
     * Returns the `GenericDecimalFactory` based on a given scale.
     *
     * @param scale
     * the scale value; must be in `[0,18]` both ends inclusive
     * @return the generic factory corresponding to `scale`
     * @throws IllegalArgumentException
     * if scale is not in `[0, 18]`
     */
    fun getGenericDecimalFactory(scale: Int): GenericDecimalFactory<*> {
        if ((0 <= scale) and (scale <= 18)) {
            return GENERIC_FACTORIES[scale]!!
        }
        throw IllegalArgumentException("Illegal scale, must be in [0,18] but was: $scale")
    }

    /**
     * Returns the `GenericDecimalFactory` for the given scale metrics.
     *
     * @param scaleMetrics
     * the scale metrics
     * @param <S> the generic type for `scaleMetrics`
     * @return the generic factory corresponding to `scaleMetrics`
    </S> */
    fun <S : ScaleMetrics> getGenericDecimalFactory(scaleMetrics: S): GenericDecimalFactory<S> {
        val factory = getGenericDecimalFactory(scaleMetrics.getScale()) as GenericDecimalFactory<S>
        return factory
    }
}
