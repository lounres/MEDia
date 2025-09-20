package version1

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.getOrNull
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.addAllFrom
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.collections.utils.mapTo
import dev.lounres.kone.collections.utils.single
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverField
import dev.lounres.kone.computationalGeometry.dot
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.computationalGeometry.plus
import dev.lounres.kone.computationalGeometry.polytopes.AbstractPolytopicConstruction
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor


context(field: Field<Number>, _: EuclideanSpaceOverField<Number, Vector, Point>)
fun <
    Number,
    Vector,
    Point,
> projectThroughPointInGeneralPositionOntoNormalPlane(
    point: Point,
    normal: Vector,
    area: AbstractPolytopicConstruction.Polytope<Point>,
    polytopesRegistry: KoneList<KoneMutableMap<AbstractPolytopicConstruction.Polytope<Point>, AbstractPolytopicConstruction.Polytope<Point>>>,
    target: AbstractPolytopicConstruction<Point>,
): AbstractPolytopicConstruction.Polytope<Point> {
    polytopesRegistry[area.dimension].getOrNull(area)?.let { return it }
    
    if (area.dimension == 0u)
        return target.addVertex(
            area.vertices.single().position.let {
                val vector = (it - point)
                point + vector / (vector dot normal)
            }
        ).asPolytope().also { polytopesRegistry[0u][area] = it }
    
    val newFacets = area.facesOfDimension(area.dimension - 1u).map { facet ->
        projectThroughPointInGeneralPositionOntoNormalPlane(
            point = point,
            normal = normal,
            area = facet,
            polytopesRegistry = polytopesRegistry,
            target = target,
        )
    }
    
    val newFaces = KoneList(area.dimension) {
        KoneMutableReifiedSet.of<AbstractPolytopicConstruction.Polytope<Point>>(
            elementEquality = Equality.absoluteFor(),
            elementHashing = Hashing.defaultFor(),
        )
    }
    
    for (facet in newFacets) {
        for (dim in 0u ..< facet.dimension) newFaces[dim].addAllFrom(facet.facesOfDimension(dim))
        newFaces[facet.dimension].add(facet)
    }
    
    return target.addPolytope(
        dimension = area.dimension,
        vertices = newFaces[0u].mapTo(
            KoneMutableReifiedSet.of<AbstractPolytopicConstruction.Vertex<Point>>(
                elementEquality = Equality.absoluteFor(),
                elementHashing = Hashing.defaultFor(),
            )
        ) { it.vertices.single() },
        faces = newFaces,
    ).also { polytopesRegistry[area.dimension][area] = it }
}