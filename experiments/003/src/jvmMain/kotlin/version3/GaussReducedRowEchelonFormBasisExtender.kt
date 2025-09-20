package version3

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.algebraic.basis.VectorSpaceBasisDecomposition
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.build


interface BasisExtender<Vector> {
    fun KoneList<Vector>.extend(newVector: Vector): KoneList<Vector>
}

context(basisExtender: BasisExtender<Vector>)
fun <Vector> KoneList<Vector>.extend(newVector: Vector): KoneList<Vector> = with(basisExtender) { this@extend.extend(newVector) }

class GaussReducedRowEchelonFormBasisExtender<Number, Vector>(
    private val field: Field<Number>,
    private val vectorSpace: VectorSpace<Number, Vector>,
    private val basisDecomposition: VectorSpaceBasisDecomposition.Finite<Number, Vector>,
) : BasisExtender<Vector> {
    override fun KoneList<Vector>.extend(newVector: Vector): KoneList<Vector> = context(field, vectorSpace) {
        KoneList.build(this.size + 1u) {
            var newVector = newVector
            var firstNonZeroIndex = 0u
            while (firstNonZeroIndex < basisDecomposition.size && basisDecomposition.decompose(newVector)[firstNonZeroIndex].isZero()) firstNonZeroIndex++
            
            var basisIndex = 0u
            var coefficientIndex = 0u
            while (true) {
                if (firstNonZeroIndex == basisDecomposition.size) {
                    addSeveral(this@extend.size - basisIndex) { this@extend[it + basisIndex] }
                    break
                }
                if (basisIndex == this@extend.size) {
                    newVector /= basisDecomposition.decompose(newVector)[firstNonZeroIndex]
                    for (index in 0u ..< basisIndex) {
                        this[index] -= newVector * basisDecomposition.decompose(this[index])[firstNonZeroIndex]
                    }
                    add(newVector)
                    break
                }
                
                val basisVectorDecomposition = basisDecomposition.decompose(this@extend[basisIndex])
                while (basisVectorDecomposition[coefficientIndex].isZero()) coefficientIndex++
                
                if (coefficientIndex > firstNonZeroIndex) {
                    newVector /= basisDecomposition.decompose(newVector)[firstNonZeroIndex]
                    val basisSeparator = basisIndex
                    while (basisIndex < this@extend.size) {
                        val basisVectorDecomposition = basisDecomposition.decompose(this@extend[basisIndex])
                        while (basisVectorDecomposition[coefficientIndex].isZero()) coefficientIndex++
                        newVector -= this@extend[basisIndex] * basisDecomposition.decompose(newVector)[coefficientIndex]
                        basisIndex++
                        coefficientIndex++
                    }
                    for (index in 0u ..< basisSeparator) {
                        this[index] -= newVector * basisDecomposition.decompose(this[index])[firstNonZeroIndex]
                    }
                    add(newVector)
                    addSeveral(this@extend.size - basisSeparator) { this@extend[it + basisSeparator] }
                    break
                }
                
                val newVectorCoef = basisDecomposition.decompose(newVector)[coefficientIndex]
                add(this@extend[basisIndex])
                newVector -= this@extend[basisIndex] * newVectorCoef
                while (firstNonZeroIndex < basisDecomposition.size && basisDecomposition.decompose(newVector)[firstNonZeroIndex].isZero()) firstNonZeroIndex++
                basisIndex++
                coefficientIndex++
            }
        }
    }
}