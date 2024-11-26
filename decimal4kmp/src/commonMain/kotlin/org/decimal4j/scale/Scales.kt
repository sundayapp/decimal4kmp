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

/**
 * Utility class with static members to access [ScaleMetrics] instances.
 */
object Scales {
    //@formatter:off
    private val SCALES = arrayOf<ScaleMetrics>(Scale0f.INSTANCE, 
    Scale1f.INSTANCE, 
    Scale2f.INSTANCE, 
    Scale3f.INSTANCE, 
    Scale4f.INSTANCE, 
    Scale5f.INSTANCE, 
    Scale6f.INSTANCE, 
    Scale7f.INSTANCE, 
    Scale8f.INSTANCE, 
    Scale9f.INSTANCE, 
    Scale10f.INSTANCE, 
    Scale11f.INSTANCE, 
    Scale12f.INSTANCE, 
    Scale13f.INSTANCE, 
    Scale14f.INSTANCE, 
    Scale15f.INSTANCE, 
    Scale16f.INSTANCE, 
    Scale17f.INSTANCE, 
    Scale18f.INSTANCE
    )
    
     //@formatter:on
    /**
     * All scale metric constants in an immutable ordered list:
     * <br></br>
     * `VALUES=[Scale0f.INSTANCE, Scale1f.INSTANCE, ..., Scale18f.INSTANCE]`
     */
    val VALUES= listOf(*SCALES)

    /**
     * The minimum scale that can be passed to [.getScaleMetrics] without causing an
     * exception; the minimum scale is 0.
     */
    const val MIN_SCALE: Int = 0

    /**
     * The maximum scale that can be passed to [.getScaleMetrics] without causing an
     * exception; the maximum scale is 18.
     */
    const val MAX_SCALE: Int = 18

    //@formatter:off
    private val SCALE_FACTORS = longArrayOf(1, 
    10, 
    100, 
    1000, 
    10000, 
    100000, 
    1000000, 
    10000000, 
    100000000, 
    1000000000, 
    10000000000L, 
    100000000000L, 
    1000000000000L, 
    10000000000000L, 
    100000000000000L, 
    1000000000000000L, 
    10000000000000000L, 
    100000000000000000L, 
    1000000000000000000L
    )
    
     //@formatter:on
    /**
     * Returns the `ScaleMetrics` constant based on a given scale
     *
     * @param scale
     * the scale value; must be in `[0,18]` both ends inclusive
     * @return the scale metrics constant corresponding to `scale`
     * @throws IllegalArgumentException
     * if scale is not in `[0, 18]`
     * @see .MIN_SCALE
     *
     * @see .MAX_SCALE
     */
    fun getScaleMetrics(scale: Int): ScaleMetrics {
        if ((MIN_SCALE <= scale) and (scale <= MAX_SCALE)) {
            return SCALES[scale]
        }
        throw IllegalArgumentException("illegal scale, must be in [" + MIN_SCALE + "," + MAX_SCALE + "] but was: " + scale)
    }

    /**
     * Returns the `ScaleMetrics` constant that matches the given
     * `scaleFactor` if any and null otherwise.
     *
     * @param scaleFactor
     * the scale factor to find
     * @return the scale metrics constant with
     * [ScaleMetrics.getScaleFactor] equal to
     * `scaleFactor` if it exists and null otherwise
     * @see ScaleMetrics.getScaleFactor
     */
    fun findByScaleFactor(scaleFactor: Long): ScaleMetrics? {
        val index = SCALE_FACTORS.toList().binarySearch(scaleFactor)
        return if (index < 0) null else VALUES[index]
    }
}
