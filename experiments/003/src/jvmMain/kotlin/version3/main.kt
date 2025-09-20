package version3

import dev.lounres.kone.algebraic.BigLongRational
import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.basis.VectorSpaceBasis
import dev.lounres.kone.algebraic.basis.VectorSpaceBasisDecomposition
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverField
import dev.lounres.kone.computationalGeometry.PointWrapper
import dev.lounres.kone.computationalGeometry.mdList1
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.relations.hashing
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import kotlin.time.measureTimedValue


typealias Number = BigLongRational
typealias Vector = MDList1<Number>

fun main() {
    print("n = ")
    val dimension = readln().toUInt()
    
    val field: Field<Number> = Number.context
    val numberHashing: Hashing<Number> = Number.context
    val numberOrder: Order<Number> = Number.context
    val euclideanSpace: EuclideanSpaceOverField<Number, Vector, PointWrapper<Vector>> =
        EuclideanSpaceOverField.mdList1(field, dimension)
    val vectorHashing: Hashing<Vector> = MDList1.hashing(Number.context)
    val vectorBasis: VectorSpaceBasis.Finite<Number, Vector> =
        object : VectorSpaceBasis.Finite<Number, Vector> {
            override val size: UInt get() = dimension
            
            override fun decompose(vector: Vector): VectorSpaceBasisDecomposition.Result<Number, UInt> =
                VectorSpaceBasisDecomposition.Result { index -> vector[index] }
            
            override fun get(index: UInt): Vector =
                MDList1(dimension) { if (it == index) field.one else field.zero }
        }
    val basisExtender: BasisExtender<Vector> =
        GaussReducedRowEchelonFormBasisExtender(
            field = field,
            vectorSpace = euclideanSpace,
            basisDecomposition = vectorBasis
        )
    
    context(
        field,
        numberHashing,
        numberOrder,
        euclideanSpace,
        vectorHashing,
        basisExtender
    ) {
        val resultAndDuration = measureTimedValue {
            possibleResultNumbers(dimension, vectorBasis, vectorBasis)
        }
        println(
            """
                ${resultAndDuration.value}
                Computed in ${resultAndDuration.duration}
            """.trimIndent()
        )
    }
}