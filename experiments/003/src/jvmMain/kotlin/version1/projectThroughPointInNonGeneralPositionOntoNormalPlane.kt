package version1

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Sign
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isPositive
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.sign
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.getOrNull
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.set.addAllFrom
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.set.removeAllFrom
import dev.lounres.kone.collections.utils.filter
import dev.lounres.kone.collections.utils.first
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
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor


context(field: Field<Number>, _: Order<Number>, _: EuclideanSpaceOverField<Number, Vector, Point>)
fun <
    Number,
    Vector,
    Point,
> projectThroughPointInNonGeneralPositionOntoNormalPlane(
    point: Point,
    normal: Vector,
    area: AbstractPolytopicConstruction.Polytope<Point>,
    polytopesRegistry: KoneList<KoneMutableMap<AbstractPolytopicConstruction.Polytope<Point>, AbstractPolytopicConstruction.Polytope<Point>>>,
    target: AbstractPolytopicConstruction<Point>,
): AbstractPolytopicConstruction.Polytope<Point> {
    polytopesRegistry[area.dimension].getOrNull(area)?.let { return it }
    
    when (area.dimension) {
        0u -> error("Cannot have normal vector in 0-dimensional non-general position")
        1u -> {
            val vector = area.vertices.first().position - point
            return target.addVertex(point + vector / (vector dot normal)).asPolytope().also { polytopesRegistry[1u][area] = it }
        }
    }
    
    data class FacetWithSign(
        val facet: AbstractPolytopicConstruction.Polytope<Point>,
        val sign: Sign,
    )
    
    val facetsSigns = area.facesOfDimension(area.dimension - 1u).map { facet ->
        val start = facet.vertices.first().position
        val facetNormal = area.innerVectorPerpendicularTo(facet)
        
        FacetWithSign(facet, ((point - start) dot facetNormal).sign())
    }
    
    val positiveFacets = facetsSigns.filter { it.sign.isPositive() }.map { it.facet }
    val zeroFacets = facetsSigns.filter { it.sign.isZero() }.map { it.facet }
    
    val faces = KoneList(area.dimension - 1u) {
        KoneMutableReifiedSet.of<AbstractPolytopicConstruction.Polytope<Point>>(
            elementReification = Reification.defaultFor(),
            elementEquality = Equality.absoluteFor(),
            elementHashing = Hashing.defaultFor(),
        )
    }
    
    val ridgesToProject = KoneMutableSet.of<AbstractPolytopicConstruction.Polytope<Point>>(
        elementEquality = Equality.absoluteFor(),
        elementHashing = Hashing.defaultFor(),
    )
    
    for (facet in positiveFacets) for (ridge in facet.facesOfDimension(facet.dimension - 1u))
        if (ridge in ridgesToProject) ridgesToProject.remove(ridge)
        else ridgesToProject.add(ridge)
    
    for (facet in zeroFacets) {
        ridgesToProject.removeAllFrom(facet.facesOfDimension(facet.dimension - 1u))
        
        val projection = projectThroughPointInNonGeneralPositionOntoNormalPlane(
            point = point,
            normal = normal,
            area = facet,
            polytopesRegistry = polytopesRegistry,
            target = target,
        )
        
        for (dim in 0u ..< projection.dimension) faces[dim].addAllFrom(projection.facesOfDimension(dim))
        faces[projection.dimension].add(projection)
    }
    
    for (ridge in ridgesToProject) {
        val projection = projectThroughPointInGeneralPositionOntoNormalPlane(
            point = point,
            normal = normal,
            area = ridge,
            polytopesRegistry = polytopesRegistry,
            target = target,
        )
        
        for (dim in 0u ..< projection.dimension) faces[dim].addAllFrom(projection.facesOfDimension(dim))
        faces[projection.dimension].add(projection)
    }
    
    return target.addPolytope(
        dimension = area.dimension - 1u,
        vertices = faces.first().mapTo(
            KoneMutableReifiedSet.of(
                elementReification = Reification.defaultFor(),
                elementEquality = Equality.absoluteFor(),
                elementHashing = Hashing.defaultFor(),
            )
        ) { it.vertices.single() },
        faces = faces,
    ).also { polytopesRegistry[area.dimension][area] = it }
}