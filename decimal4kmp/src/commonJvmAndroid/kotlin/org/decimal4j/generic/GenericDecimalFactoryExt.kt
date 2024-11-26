package org.decimal4j.generic

import org.decimal4j.api.DecimalArithmeticExtensions.fromBigDecimal
import org.decimal4j.api.DecimalArithmeticExtensions.fromBigInteger
import org.decimal4j.factory.DecimalFactoryJvm
import org.decimal4j.scale.ScaleMetrics
import org.decimal4j.truncate.RoundingMode
import java.math.BigDecimal
import java.math.BigInteger

class GenericDecimalFactoryExt<S : ScaleMetrics>(scaleMetrics: S): GenericDecimalFactory<S>(scaleMetrics), DecimalFactoryJvm<S> {

    override fun valueOf(value: BigInteger): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(scaleMetrics, scaleMetrics.getDefaultCheckedArithmetic().fromBigInteger(value))
    }

    override fun valueOf(value: BigDecimal): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(scaleMetrics, scaleMetrics.getDefaultCheckedArithmetic().fromBigDecimal(value))
    }

    override fun valueOf(value: BigDecimal, roundingMode: RoundingMode): GenericImmutableDecimal<S> {
        return GenericImmutableDecimal(
            scaleMetrics, scaleMetrics.getCheckedArithmetic(roundingMode).fromBigDecimal(
                value
            )
        )
    }
}