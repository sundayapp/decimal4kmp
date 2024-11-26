package org.decimal4j.scale

import java.math.BigDecimal
import java.math.BigInteger

object ScaleMetricsExtensions {

    /**
     * Returns the [scale factor][.getScaleFactor] as a [BigInteger] value.
     *
     * @return the scale factor as big integer
     */
    fun ScaleMetrics.getScaleFactorAsBigInteger(): BigInteger {
        return BigInteger.valueOf(getScaleFactor())
    }

    /**
     * Returns the [scale factor][.getScaleFactor] as a [BigDecimal] value with scale zero.
     *
     * @return the scale factor as big decimal with scale zero.
     */
    fun ScaleMetrics.getScaleFactorAsBigDecimal(): BigDecimal {
        return BigDecimal.valueOf(getScaleFactor())
    }
}