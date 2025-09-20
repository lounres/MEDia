package version2

import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.algebraic.basis.VectorSpaceBasis
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.empty
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.list.relations.equality
import dev.lounres.kone.collections.list.relations.hashing
import dev.lounres.kone.collections.map.*
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.addAllFrom
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.foldIndexed
import dev.lounres.kone.collections.utils.setOrChange
import dev.lounres.kone.combinatorics.enumerative.cartesianPower
import dev.lounres.kone.relations.Hashing


fun interface BasisValidator<Covector> {
    fun validate(basis: KoneList<Covector>): Boolean
}

context(covectorSpace: VectorSpace<*, Covector>, covectorHashing: Hashing<Covector>, _: BasisExtender<Covector>)
fun <
    Covector,
> computePossibleEquationSystems(
    dimension: UInt,
    covectorBasis: VectorSpaceBasis.Finite<*, Covector>,
    basisValidator: BasisValidator<Covector>,
): KoneList<KoneMap<KoneList<Covector>, KoneSet<Covector>>> {
    val equationCases = KoneList(dimension) {
        KoneMutableMap.of<KoneList<Covector>, KoneMutableSet<Covector>>(
            keyEquality = KoneList.equality(covectorSpace),
            keyHashing = KoneList.hashing(covectorHashing),
        )
    }
    
    equationCases[0u][KoneList.empty()] =
        KoneMutableSet.of(
            elementEquality = covectorSpace,
            elementHashing = covectorHashing,
        )
    
    for (coefficients in KoneList.of(1, -1).cartesianPower(dimension - 1u)) {
        if (coefficients.all { it == 1 }) continue
        val vector = coefficients.foldIndexed(covectorBasis[0u]) { index, accumulator, element ->
            accumulator + element * covectorBasis[index + 1u]
        }
        
        for (dim in 0u ..< dimension) for ((basis, initialCovectors) in equationCases[dim]) {
            if (vector in initialCovectors) continue
            val newBasis = basis.extend(vector)
            when (newBasis.size) {
                basis.size -> {
                    initialCovectors.add(vector)
                }
                basis.size + 1u -> {
                    if (dim != dimension - 1u && basisValidator.validate(newBasis))
                        equationCases[dim + 1u].setOrChange(
                            newBasis,
                            {
                                KoneMutableSet.of(
                                    elementEquality = covectorSpace,
                                    elementHashing = covectorHashing,
                                ).apply {
                                    addAllFrom(initialCovectors)
                                    add(vector)
                                }
                            },
                            { oldInitialCovectors ->
                                oldInitialCovectors.add(vector)
                                oldInitialCovectors
                            }
                        )
                }
                else -> error("Unexpected size of new basis")
            }
        }
    }
    
    return equationCases
}