package version1

import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.algebraic.basis.VectorSpaceBasis
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.addAllFrom
import dev.lounres.kone.collections.list.build
import dev.lounres.kone.collections.list.empty
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.list.toKoneList
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.foldIndexed
import dev.lounres.kone.combinatorics.enumerative.cartesianPower


data class EquationCase<Covector>(
    val initialCovectors: KoneList<Covector>,
    val basis: KoneList<Covector>,
)

context(_: VectorSpace<*, Covector>, _: BasisExtender<Covector>)
fun <
    Covector,
> computePossibleEquationSystems(
    dimension: UInt,
    covectorBasis: VectorSpaceBasis.Finite<*, Covector>,
): KoneList<KoneList<EquationCase<Covector>>> {
    val equationCases = KoneList(dimension) {
        KoneArrayGrowableList<EquationCase<Covector>>()
    }
    
    equationCases[0u].add(
        EquationCase(
            initialCovectors = KoneList.empty(),
            basis = KoneList.empty(),
        )
    )
    
    for (coefficients in KoneList.of(1, -1).cartesianPower(dimension - 1u)) {
        if (coefficients.all { it == 1 }) continue
        val vector = coefficients.foldIndexed(covectorBasis[0u]) { index, accumulator, element ->
            accumulator + element * covectorBasis[index + 1u]
        }
        
        for (dim in dimension - 1u downTo 0u) for (case in equationCases[dim].toKoneList()) {
            val newBasis = case.basis.extend(vector)
            if (newBasis.size == dimension) continue
            equationCases[newBasis.size].add(
                EquationCase(
                    initialCovectors = KoneList.build(case.initialCovectors.size + 1u) {
                        addAllFrom(case.initialCovectors)
                        add(vector)
                    },
                    basis = newBasis,
                )
            )
        }
    }
    
    return equationCases
}