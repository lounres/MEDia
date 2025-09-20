package version3

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.build
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverField
import dev.lounres.kone.computationalGeometry.dot


context(_: Field<Number>, _: EuclideanSpaceOverField<Number, Vector, *>)
fun <Number, Vector> KoneList<Vector>.orthogonalizeByGramSchmidt(): KoneList<Vector> =
    KoneList.build {
        val basis = this@orthogonalizeByGramSchmidt
        for (index in 0u ..< basis.size)
            +(0u ..< index).fold(basis[index]) { accumulator, subindex ->
                val subVector = this[subindex]
                accumulator - subVector * ((accumulator dot subVector) / (subVector dot subVector))
            }
    }