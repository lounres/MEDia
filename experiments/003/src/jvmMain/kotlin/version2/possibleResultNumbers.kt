package version2

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.algebraic.basis.VectorSpaceBasis
import dev.lounres.kone.algebraic.basis.VectorSpaceBasisDecomposition
import dev.lounres.kone.algebraic.context
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.firstIndexThat
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.collections.utils.mapIndexed
import dev.lounres.kone.collections.utils.sorted
import dev.lounres.kone.collections.utils.sumOf
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
    val areaToCheckIntersectionWith = areaToCheckIntersectionWith(dimension = dimension, basis = vectorBasis)
    
    fun VectorSpaceBasisDecomposition.Result<Number, UInt>.sum(): Number =
        (0u ..< dimension).toKoneList().sumOf<_, Number> { this[it] }
    
    val equationCases = computePossibleEquationSystems(
        dimension = dimension,
        covectorBasis = covectorBasis,
        basisValidator = BasisValidator { basis ->
            val solutions = basis.reducedRowEchelonFormKernel(dimension, vectorBasis, covectorBasis)
            val solutionBasisDecompositions = solutions.map { vectorBasis.decompose(it) }
            val nonZeroSumSolutionIndex = solutionBasisDecompositions.firstIndexThat { _, decomposition ->
                (0u ..< dimension).toKoneList().sumOf<_, Number> { decomposition[it] }.isNotZero()
            }
            if (nonZeroSumSolutionIndex == solutions.size) return@BasisValidator false
            val nonZeroSumSolution = solutions[nonZeroSumSolutionIndex]
            val nonZeroSumSolutionSum = solutionBasisDecompositions[nonZeroSumSolutionIndex].sum()
            
            checkIntersectionOfAreaAndSolutions(
                area = areaToCheckIntersectionWith,
                solutions = solutions.mapIndexed { index, vector ->
                    val sum = solutionBasisDecompositions[index].sum()
                    PointWrapper(if (sum.isNotZero()) vector / sum else (nonZeroSumSolution + vector) / nonZeroSumSolutionSum)
                },
            )
        }
    )
    
    println(equationCases.map { it.size })
    
    val result =
        KoneMutableSet.of(
            elementEquality = UInt.context,
            elementHashing = UInt.context,
            elementOrder = UInt.context,
        )
    
    for (dimensionEquations in equationCases) for (equation in dimensionEquations.valuesView) result.add(equation.size)
    
    return result.sorted()
}