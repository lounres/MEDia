package util

import dev.lounres.kone.algebraic.Rational
import dev.lounres.kone.algebraic.installRationalContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.misc.planimetricsCalculation.installPlanimetricsCalculationSpaceFor
import dev.lounres.kone.suppliedTypes.SuppliedType


/**
 * Just supplied type describing `Rational` class.
 */
val rationalType = SuppliedType.Regular<Rational>(
    kClass = Rational::class,
    typeArguments = listOf(),
    isNullable = false,
)

/**
 * Context registry that is used for all stories in the experiment.
 */
val koneContextRegistry = KoneContextRegistry {
    installRationalContext()
    installPlanimetricsCalculationSpaceFor(rationalType)
}