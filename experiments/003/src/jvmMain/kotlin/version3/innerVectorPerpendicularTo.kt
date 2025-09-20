package version3

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.addAllFrom
import dev.lounres.kone.collections.list.build
import dev.lounres.kone.collections.list.lastIndex
import dev.lounres.kone.collections.utils.firstThat
import dev.lounres.kone.collections.utils.last
import dev.lounres.kone.collections.utils.single
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverField
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.computationalGeometry.polytopes.PolytopicConstruction


context(_: Field<Number>, _: EuclideanSpaceOverField<Number, Vector, Point>)
fun <
    Number,
    Vector,
    Point,
    Polytope: PolytopicConstruction.Polytope<Point, Polytope, *>
> Polytope.innerVectorPerpendicularTo(face: Polytope): Vector {
    require(this.dimension > face.dimension && face in this.facesOfDimension(face.dimension))
    val flagAndPolytope = KoneList.build {
        addAllFrom(face.anyFlag())
        add(this@innerVectorPerpendicularTo)
    }
    val startPoint = flagAndPolytope[0u].vertices.single().position
    return KoneList.build<Vector> {
        for (index in 0u ..< flagAndPolytope.lastIndex) {
            val innerPolytope = flagAndPolytope[index]
            val outerPolytope = flagAndPolytope[index + 1u]
            +(outerPolytope.vertices.firstThat { it !in innerPolytope.vertices }.position - startPoint)
        }
    }.orthogonalizeByGramSchmidt().last()
}