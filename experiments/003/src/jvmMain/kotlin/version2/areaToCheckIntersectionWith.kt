package version2

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Monoid
import dev.lounres.kone.algebraic.basis.VectorSpaceBasis
import dev.lounres.kone.algebraic.isNegative
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.utils.filter
import dev.lounres.kone.collections.utils.flatMap
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.collections.utils.single
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.computationalGeometry.AffineSpaceOverField
import dev.lounres.kone.computationalGeometry.PointWrapper
import dev.lounres.kone.computationalGeometry.polytopes.AbstractPolytopicConstruction
import dev.lounres.kone.relations.Order


context(_: Field<Number>, _: Order<Number>, _: AffineSpaceOverField<Number, Vector, PointWrapper<Vector>>)
fun <Number, Vector> areaToCheckIntersectionWith(
    dimension: UInt,
    basis: VectorSpaceBasis.Finite<Number, Vector>
): AbstractPolytopicConstruction.Polytope<PointWrapper<Vector>> {
    var polytopicConstruction = defaultSimplex(dimension, basis)
    
    for (index in 0u ..< dimension) {
        val newPolytopicConstruction = AbstractPolytopicConstruction<PointWrapper<Vector>>(dimension)
        
        val polytopeMapping = polytopicConstruction.divideByAndPut(
            linearFunction = {  point ->
                val vectorDecomposition = basis.decompose(point.vector)
                context(contextOf<Monoid<Number>>()) {
                    (0u ..< dimension).toKoneList().sumOf { vectorDecomposition[it] } - vectorDecomposition[index] * 2
                }
            },
            target = newPolytopicConstruction
        )
        
        for (vertexPolytope in polytopeMapping[0u].valuesView.flatMap { it.filter { it.sign.isNegative() }.map { it.polytope } })
            vertexPolytope.remove()
        
        polytopicConstruction = newPolytopicConstruction
    }
    
    return polytopicConstruction.polytopesOfDimension(dimension - 1u).single()
}