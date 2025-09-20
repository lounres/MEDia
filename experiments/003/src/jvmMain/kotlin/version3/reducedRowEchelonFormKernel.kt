package version3

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.algebraic.basis.VectorSpaceBasis
import dev.lounres.kone.algebraic.basis.VectorSpaceBasisDecomposition
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.isOne
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.valueOf
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.build
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.utils.withIndex


context(_: Field<Number>, vectorSpace: VectorSpace<Number, Vector>)
fun <
    Number,
    Vector,
    Covector,
> KoneList<Covector>.reducedRowEchelonFormKernel(
    dimension: UInt,
    vectorBasis: VectorSpaceBasis.Finite<Number, Vector>,
    covectorBasisDecomposition: VectorSpaceBasisDecomposition.Finite<Number, Covector>,
): KoneList<Vector> {
    val cobasis = this
    return KoneList.build(dimension - cobasis.size) {
        val startIndices = KoneArrayFixedCapacityList<UInt>(cobasis.size)
        var basisIndex = 0u
        for (coefficientIndex in 0u ..< dimension) {
            if (basisIndex < cobasis.size && covectorBasisDecomposition.decompose(cobasis[basisIndex])[coefficientIndex].isNotZero()) {
                check(covectorBasisDecomposition.decompose(cobasis[basisIndex])[coefficientIndex].isOne())
                startIndices.add(coefficientIndex)
                basisIndex++
                continue
            }
            
            var vector = vectorSpace.zero
            for ((index, start) in startIndices.withIndex())
                vector += covectorBasisDecomposition.decompose(cobasis[index])[coefficientIndex] * vectorBasis[start]
            vector += valueOf(-1) * vectorBasis[coefficientIndex]
            
            add(vector)
        }
    }
}