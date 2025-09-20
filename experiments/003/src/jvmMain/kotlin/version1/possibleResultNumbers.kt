package version1

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.algebraic.basis.VectorSpaceBasis
import dev.lounres.kone.algebraic.context
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.mapTo
import dev.lounres.kone.collections.utils.sorted
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverField
import dev.lounres.kone.computationalGeometry.PointWrapper
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order


context(
    _: Field<Number>,
    _: Hashing<Number>,
    _: Order<Number>,
    _: EuclideanSpaceOverField<Number, Vector, PointWrapper<Vector>>,
    _: Hashing<Vector>,
    _: VectorSpace<*, Covector>,
    _: Hashing<Covector>,
    _: BasisExtender<Covector>,
)
fun <Number, Vector, Covector> possibleResultNumbers(
    dimension: UInt,
    vectorBasis: VectorSpaceBasis.Finite<Number, Vector>,
    covectorBasis: VectorSpaceBasis.Finite<Number, Covector>,
): KoneList<UInt> {
    val equationCases = computePossibleEquationSystems(
        dimension = dimension,
        covectorBasis = covectorBasis,
    )
    
    val checkedSolutionCases = equationCases.filterEquationSystems(
        dimension = dimension,
        vectorBasis = vectorBasis,
        covectorBasisDecomposition = covectorBasis,
    )
    
    return checkedSolutionCases
        .mapTo(
            KoneMutableSet.of(
                elementEquality = UInt.context,
                elementHashing = UInt.context,
                elementOrder = UInt.context,
            )
        ) { it.size }
        .sorted()
}