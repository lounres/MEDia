package version1

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isNegative
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.isPositive
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.sign
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.iterables.isEmpty
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.map.KoneMutableMap
import dev.lounres.kone.collections.map.of
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.drop
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.collections.utils.firstThat
import dev.lounres.kone.collections.utils.firstThatOrNull
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverField
import dev.lounres.kone.computationalGeometry.dot
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.computationalGeometry.plus
import dev.lounres.kone.computationalGeometry.polytopes.AbstractPolytopicConstruction
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor


context(field: Field<Number>, _: Order<Number>, _: EuclideanSpaceOverField<Number, Vector, Point>)
tailrec fun <
    Number,
    Vector,
    Point,
> checkIntersectionOfAreaAndSolutions(
    area: AbstractPolytopicConstruction.Polytope<Point>,
    solutions: KoneList<Point>,
): Boolean {
    if (solutions.isEmpty()) return false
    
    val primarySolution = solutions.first()
    val otherSolutions = solutions.drop(1u)
    
    if (area.dimension == 0u) return true
    
    data class FacetNormalAndIsStrictlyInside(
        val facet: AbstractPolytopicConstruction.Polytope<Point>,
        val normal: Vector,
        val isStrictlyInside: Boolean,
    )
    
    val facetNormalAndIsStrictlyInsides = area.facesOfDimension(area.dimension - 1u).map { facet ->
        val start = facet.vertices.first().position
        val facetNormal = area.innerVectorPerpendicularTo(facet)
        
        val sign = ((primarySolution - start) dot facetNormal).sign()
        
        if (sign.isNegative()) {
            val nonParallelSolution = otherSolutions.firstThatOrNull { ((it - primarySolution) dot facetNormal).isNotZero() } ?: return false
            val otherSolutionProjections = otherSolutions.map {
                val vector = it - primarySolution
                val vectorDotNormal = vector dot facetNormal
                if (vectorDotNormal.isNotZero()) primarySolution + vector / (vector dot facetNormal)
                else {
                    val newVector = vector + (nonParallelSolution - primarySolution)
                    primarySolution + newVector / (newVector dot facetNormal)
                }
            }
            val areaProjection = projectThroughPointInNonGeneralPositionOntoNormalPlane(
                point = primarySolution,
                normal = facetNormal,
                area = area,
                polytopesRegistry = KoneList(area.dimension + 1u) {
                    KoneMutableMap.of(
                        keyEquality = Equality.absoluteFor(),
                        keyHashing = Hashing.defaultFor(),
                    )
                },
                target = AbstractPolytopicConstruction(facet.dimension),
            )
            
            return checkIntersectionOfAreaAndSolutions(
                area = areaProjection,
                solutions = otherSolutionProjections,
            )
        }
        
        FacetNormalAndIsStrictlyInside(facet, facetNormal, sign.isPositive())
    }
    
    if (facetNormalAndIsStrictlyInsides.all { it.isStrictlyInside }) return true
    if (otherSolutions.isEmpty()) return false
    
    val shiftVector = otherSolutions.first() - primarySolution
    
    val firstFacetNormalAndIsStrictlyInside = facetNormalAndIsStrictlyInsides.firstThat { (it.normal dot shiftVector).isNotZero() }
    val normal = firstFacetNormalAndIsStrictlyInside.normal
    val start = firstFacetNormalAndIsStrictlyInside.facet.vertices.first().position
    val newPrimarySolution = primarySolution + shiftVector * (((start - primarySolution) dot normal) / (shiftVector dot normal) - (shiftVector dot normal))
    
    val areaProjection = projectThroughPointInNonGeneralPositionOntoNormalPlane(
        point = newPrimarySolution,
        normal = normal,
        area = area,
        polytopesRegistry = KoneList(area.dimension + 1u) {
            KoneMutableMap.of(
                keyEquality = Equality.absoluteFor(),
                keyHashing = Hashing.defaultFor(),
            )
        },
        target = AbstractPolytopicConstruction(area.dimension - 1u),
    )
    val otherSolutionProjections = otherSolutions.map {
        val vector = it - newPrimarySolution
        newPrimarySolution + (vector / (vector dot normal))
    }
    
    return checkIntersectionOfAreaAndSolutions(
        area = areaProjection,
        solutions = otherSolutionProjections,
    )
}