package version1

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.basis.VectorSpaceBasis
import dev.lounres.kone.algebraic.basis.VectorSpaceBasisDecomposition
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.KoneMutableList
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.relations.equality
import dev.lounres.kone.collections.list.relations.hashing
import dev.lounres.kone.collections.map.component1
import dev.lounres.kone.collections.map.component2
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.utils.associateBy
import dev.lounres.kone.collections.utils.filter
import dev.lounres.kone.collections.utils.firstIndexThat
import dev.lounres.kone.collections.utils.flatten
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.collections.utils.mapIndexed
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverField
import dev.lounres.kone.computationalGeometry.PointWrapper
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order


@IgnorableReturnValue
public inline fun <E, R: Any, D: KoneMutableList<in R>> KoneIterable<E>.mapNotNullTo(destination: D, crossinline transform: (E) -> R?): D {
    for (element in this) {
        val _ = transform(element)?.also { destination.add(it) }
    }
    return destination
}

@IgnorableReturnValue
public inline fun <E, R: Any, D: KoneMutableSet<in R>> KoneIterable<E>.mapNotNullTo(destination: D, crossinline transform: (E) -> R?): D {
    for (element in this) {
        val _ = transform(element)?.also { destination.add(it) }
    }
    return destination
}

@IgnorableReturnValue
public inline fun <E, R: Any> KoneIterable<E>.mapNotNull(crossinline transform: (E) -> R?): KoneList<R> {
    val result = mapNotNullTo(KoneArrayFixedCapacityList(size), transform)
    return result
}

context(
    _: Field<Number>,
    _: Order<Number>,
    _: EuclideanSpaceOverField<Number, Vector, PointWrapper<Vector>>,
    covectorEquality: Equality<Covector>,
    covectorHashing: Hashing<Covector>,
)
fun <
    Number,
    Vector,
    Covector,
> KoneList<KoneList<EquationCase<Covector>>>.filterEquationSystems(
    dimension: UInt,
    vectorBasis: VectorSpaceBasis.Finite<Number, Vector>,
    covectorBasisDecomposition: VectorSpaceBasisDecomposition.Finite<Number, Covector>,
): KoneList<KoneList<Covector>> {
    val equationToCases = this.flatten().associateBy(
        keyEquality = KoneList.equality(covectorEquality),
        keyHashing = KoneList.hashing(covectorHashing),
        keySelector = { it.basis },
        valueTransform = { it.initialCovectors },
        resolve = { _, currentValue, newValue -> if (currentValue.size > newValue.size) currentValue else newValue }
    )
    
    data class SolutionCase(
        val initialCovectors: KoneList<Covector>,
        val solutions: KoneList<Vector>,
    )
    
    val solutionCases = equationToCases.nodesView.map { (basis, initialCovectors) ->
        SolutionCase(
            initialCovectors = initialCovectors,
            solutions = basis.reducedRowEchelonFormKernel(
                dimension = dimension,
                vectorBasis = vectorBasis,
                covectorBasisDecomposition = covectorBasisDecomposition,
            ),
        )
    }
    
    fun VectorSpaceBasisDecomposition.Result<Number, UInt>.sum(): Number =
        (0u ..< dimension).toKoneList().sumOf<_, Number> { this[it] }
    
    val filteredSolutionCases = solutionCases.mapNotNull { solutionCase ->
        val solutionBasisDecompositions = solutionCase.solutions.map { vectorBasis.decompose(it) }
        val nonZeroSumSolutionIndex = solutionBasisDecompositions.firstIndexThat { _, decomposition ->
            decomposition.sum().isNotZero()
        }
        if (nonZeroSumSolutionIndex == solutionCase.solutions.size) return@mapNotNull null
        val nonZeroSumSolution = solutionCase.solutions[nonZeroSumSolutionIndex]
        val nonZeroSumSolutionSum = solutionBasisDecompositions[nonZeroSumSolutionIndex].sum()
        SolutionCase(
            initialCovectors = solutionCase.initialCovectors,
            solutions = solutionCase.solutions.mapIndexed { index, vector ->
                val sum = solutionBasisDecompositions[index].sum()
                if (sum.isNotZero()) vector / sum else (nonZeroSumSolution + vector) / nonZeroSumSolutionSum
            }
        )
    }
    
    val areaToCheckIntersectionWith = areaToCheckIntersectionWith(dimension = dimension, basis = vectorBasis)
    
    val checkedSolutionCases = filteredSolutionCases.filter { solutionCase ->
        checkIntersectionOfAreaAndSolutions(
            area = areaToCheckIntersectionWith,
            solutions = solutionCase.solutions.map { PointWrapper(it) },
        )
    }
    
    return checkedSolutionCases.map { it.initialCovectors }
}