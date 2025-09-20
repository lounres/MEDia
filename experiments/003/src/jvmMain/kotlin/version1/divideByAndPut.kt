package version1

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Sign
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isNonNegative
import dev.lounres.kone.algebraic.isNonPositive
import dev.lounres.kone.algebraic.isNonZero
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.sign
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.of
import dev.lounres.kone.collections.list.toKoneList
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.associate
import dev.lounres.kone.collections.map.associateWith
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.map.mapsTo
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneMutableSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.addAllFrom
import dev.lounres.kone.collections.set.build
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.component1
import dev.lounres.kone.collections.utils.component2
import dev.lounres.kone.collections.utils.filter
import dev.lounres.kone.collections.utils.firstOfThatOrNull
import dev.lounres.kone.collections.utils.flatMapTo
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.collections.utils.mapTo
import dev.lounres.kone.collections.utils.single
import dev.lounres.kone.computationalGeometry.AffineSpaceOverField
import dev.lounres.kone.computationalGeometry.minus
import dev.lounres.kone.computationalGeometry.plus
import dev.lounres.kone.computationalGeometry.polytopes.AbstractPolytopicConstruction
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.maybe.ifSome
import dev.lounres.kone.maybe.map
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Order
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor


data class PolytopeAndSign<Polytope>(
    val polytope: Polytope,
    val sign: Sign,
)

@IgnorableReturnValue
private inline fun <E, R, D: KoneMutableSet<in R>> KoneIterable<E>.mapSomeTo(destination: D, crossinline transform: (E) -> Maybe<R>): D {
    for (element in this) transform(element).ifSome { destination.add(it) }
    return destination
}

@IgnorableReturnValue
context(_: Field<Number>, _: Order<Number>, _: AffineSpaceOverField<Number, Vector, Point>)
fun <
    Number,
    Vector,
    Point,
> AbstractPolytopicConstruction<Point>.divideByAndPut(
    linearFunction: (Point) -> Number,
    target: AbstractPolytopicConstruction<Point>,
): KoneList<KoneMap<AbstractPolytopicConstruction.Polytope<Point>, KoneList<PolytopeAndSign<AbstractPolytopicConstruction.Polytope<Point>>>>> {
    val vertexMapping = vertices.associateWith(
        keyEquality = Equality.absoluteFor(),
        keyHashing = Hashing.defaultFor(),
        keyOrder = null
    ) { PolytopeAndSign(target.addVertex(it.position), linearFunction(it.position).sign()) }
    val polytopeMapping = KoneArrayFixedCapacityList<KoneMap<AbstractPolytopicConstruction.Polytope<Point>, KoneList<PolytopeAndSign<AbstractPolytopicConstruction.Polytope<Point>>>>>(spaceDimension + 1u)
    polytopeMapping.add(
        vertexMapping.nodesView.associate(
            keyEquality = Equality.absoluteFor(),
            keyHashing = Hashing.defaultFor(),
            keyOrder = null,
        ) { it.key.asPolytope() mapsTo KoneList.of(PolytopeAndSign(it.value.polytope.asPolytope(), it.value.sign)) }
    )
    
    val edges = polytopesOfDimension(1u)
    val polytopeSectionMapping = KoneArrayFixedCapacityList<KoneMap<AbstractPolytopicConstruction.Polytope<Point>, Maybe<AbstractPolytopicConstruction.Polytope<Point>>>>(spaceDimension)
    polytopeSectionMapping.add(
        edges.associateWith(
            keyEquality = Equality.absoluteFor(),
            keyHashing = Hashing.defaultFor(),
            keyOrder = null,
        ) {
            val (end1, end2) = it.vertices.toKoneList()
            val end1Sign = polytopeMapping[0u][end1.asPolytope()].single().sign
            val end2Sign = polytopeMapping[0u][end2.asPolytope()].single().sign
            if (end1Sign == end2Sign || end1Sign.isZero() || end2Sign.isZero()) None
            else Some(
                target.addVertex(
                    end2.position + (end1.position - end2.position) * (-linearFunction(end2.position) / (linearFunction(end1.position) - linearFunction(end2.position))),
                ).asPolytope()
            )
        }
    )
    polytopeMapping.add(
        edges.associateWith(
            keyEquality = Equality.absoluteFor(),
            keyHashing = Hashing.defaultFor(),
            keyOrder = null,
        ) {
            val (end1, end2) = it.vertices.toKoneList()
            when (val section = polytopeSectionMapping[0u][it]) {
                None -> KoneList.of(
                    PolytopeAndSign(
                        polytope = target.addPolytope(
                            dimension = 1u,
                            vertices = KoneReifiedSet.of(
                                vertexMapping[end1].polytope, vertexMapping[end2].polytope,
                                elementReification = Reification.defaultFor(),
                                elementEquality = Equality.absoluteFor(),
                                elementHashing = Hashing.defaultFor(),
                                elementOrder = null,
                            ),
                            faces = KoneList.of(
                                KoneReifiedSet.of(
                                    vertexMapping[end1].polytope.asPolytope(), vertexMapping[end2].polytope.asPolytope(),
                                    elementReification = Reification.defaultFor(),
                                    elementEquality = Equality.absoluteFor(),
                                    elementHashing = Hashing.defaultFor(),
                                    elementOrder = null,
                                ),
                            ),
                        ),
                        sign = vertexMapping[end1].sign.takeIf { it.isNonZero() } ?: vertexMapping[end2].sign,
                    ),
                )
                is Some<AbstractPolytopicConstruction.Polytope<Point>> -> KoneList.of(
                    PolytopeAndSign(
                        polytope = target.addPolytope(
                            dimension = 1u,
                            vertices = KoneReifiedSet.of(
                                vertexMapping[end1].polytope, section.value.vertices.single(),
                                elementReification = Reification.defaultFor(),
                                elementEquality = Equality.absoluteFor(),
                                elementHashing = Hashing.defaultFor(),
                                elementOrder = null,
                            ),
                            faces = KoneList.of(
                                KoneReifiedSet.of(
                                    vertexMapping[end1].polytope.asPolytope(), section.value,
                                    elementReification = Reification.defaultFor(),
                                    elementEquality = Equality.absoluteFor(),
                                    elementHashing = Hashing.defaultFor(),
                                    elementOrder = null,
                                ),
                            ),
                        ),
                        sign = vertexMapping[end1].sign
                    ),
                    PolytopeAndSign(
                        polytope = target.addPolytope(
                            dimension = 1u,
                            vertices = KoneReifiedSet.of(
                                vertexMapping[end2].polytope, section.value.vertices.single(),
                                elementReification = Reification.defaultFor(),
                                elementEquality = Equality.absoluteFor(),
                                elementHashing = Hashing.defaultFor(),
                                elementOrder = null,
                            ),
                            faces = KoneList.of(
                                KoneReifiedSet.of(
                                    vertexMapping[end2].polytope.asPolytope(), section.value,
                                    elementReification = Reification.defaultFor(),
                                    elementEquality = Equality.absoluteFor(),
                                    elementHashing = Hashing.defaultFor(),
                                    elementOrder = null,
                                ),
                            ),
                        ),
                        sign = vertexMapping[end2].sign
                    ),
                )
            }
        }
    )
    
    for (dim in 2u .. spaceDimension) {
        val polytopes = polytopesOfDimension(dim)
        polytopeSectionMapping.add(
            polytopes.associateWith(
                keyEquality = Equality.absoluteFor(),
                keyHashing = Hashing.defaultFor(),
                keyOrder = null,
            ) { polytope ->
                val signs = polytope.vertices.map { vertexMapping[it].sign }
                
                if (signs.all { it.isNonNegative() } || signs.all { it.isNonPositive() }) return@associateWith None
                
                Some(
                    target.addPolytope(
                        dimension = dim - 1u,
                        vertices = KoneReifiedSet.build(
                            elementReification = Reification.defaultFor(),
                            elementEquality = Equality.absoluteFor(),
                            elementHashing = Hashing.defaultFor(),
                            elementOrder = null,
                        ) {
                            polytope.facesOfDimension(1u).mapSomeTo(this) {
                                polytopeSectionMapping[0u][it].map { it.vertices.single() }
                            }
                            polytope.vertices.map { vertexMapping[it] }.filter { it.sign.isZero() }.mapTo(this) { it.polytope }
                        },
                        faces = KoneList(dim - 1u) { subdim ->
                            KoneReifiedSet.build(
                                elementReification = Reification.defaultFor(),
                                elementEquality = Equality.absoluteFor(),
                                elementHashing = Hashing.defaultFor(),
                                elementOrder = null,
                            ) {
                                polytope.facesOfDimension(subdim + 1u).mapSomeTo(this) {
                                    polytopeSectionMapping[subdim][it]
                                }
                                polytope.facesOfDimension(subdim).flatMapTo(this) { polytopeMapping[subdim][it].filter { it.sign.isZero() }.map { it.polytope } }
                            }
                        },
                    )
                )
            }
        )
        polytopeMapping.add(
            polytopes.associateWith(
                keyEquality = Equality.absoluteFor(),
                keyHashing = Hashing.defaultFor(),
                keyOrder = null,
            ) { polytope ->
                when (val section = polytopeSectionMapping[dim - 1u][polytope]) {
                    None -> KoneList.of(
                        PolytopeAndSign(
                            polytope = target.addPolytope(
                                dimension = dim,
                                vertices = polytope.vertices.mapTo(
                                    KoneMutableReifiedSet.of(
                                        elementReification = Reification.defaultFor(),
                                        elementEquality = Equality.absoluteFor(),
                                        elementHashing = Hashing.defaultFor(),
                                        elementOrder = null,
                                    )
                                ) { vertexMapping[it].polytope },
                                faces = KoneList(dim) { subdim ->
                                    polytope.facesOfDimension(subdim).mapTo(
                                        KoneMutableReifiedSet.of(
                                            elementReification = Reification.defaultFor(),
                                            elementEquality = Equality.absoluteFor(),
                                            elementHashing = Hashing.defaultFor(),
                                            elementOrder = null,
                                        )
                                    ) { polytopeMapping[subdim][it].single().polytope }
                                }
                            ),
                            sign = polytope.vertices.firstOfThatOrNull({ vertexMapping[it].sign }) { it.isNonZero() } ?: Sign.Zero,
                        ),
                    )
                    is Some<AbstractPolytopicConstruction.Polytope<Point>> -> KoneList.of(
                        PolytopeAndSign(
                            polytope = target.addPolytope(
                                dimension = dim,
                                vertices = KoneReifiedSet.build(
                                    elementReification = Reification.defaultFor(),
                                    elementEquality = Equality.absoluteFor(),
                                    elementHashing = Hashing.defaultFor(),
                                    elementOrder = null,
                                ) {
                                    addAllFrom(section.value.vertices)
                                    polytope.vertices.map { vertexMapping[it] }.filter { it.sign.isNonNegative() }.mapTo(this) { it.polytope }
                                },
                                faces = KoneList(dim) { subdim ->
                                    KoneReifiedSet.build(
                                        elementReification = Reification.defaultFor(),
                                        elementEquality = Equality.absoluteFor(),
                                        elementHashing = Hashing.defaultFor(),
                                        elementOrder = null,
                                    ) {
                                        if (subdim < dim - 1u) addAllFrom(section.value.facesOfDimension(subdim))
                                        else add(section.value)
                                        polytope.facesOfDimension(subdim).flatMapTo(this) { polytopeMapping[subdim][it].filter { it.sign.isNonNegative() }.map { it.polytope } }
                                    }
                                },
                            ),
                            sign = Sign.Positive,
                        ),
                        PolytopeAndSign(
                            polytope = target.addPolytope(
                                dimension = dim,
                                vertices = KoneReifiedSet.build(
                                    elementReification = Reification.defaultFor(),
                                    elementEquality = Equality.absoluteFor(),
                                    elementHashing = Hashing.defaultFor(),
                                    elementOrder = null,
                                ) {
                                    addAllFrom(section.value.vertices)
                                    polytope.vertices.map { vertexMapping[it] }.filter { it.sign.isNonPositive() }.mapTo(this) { it.polytope }
                                },
                                faces = KoneList(dim) { subdim ->
                                    KoneReifiedSet.build(
                                        elementReification = Reification.defaultFor(),
                                        elementEquality = Equality.absoluteFor(),
                                        elementHashing = Hashing.defaultFor(),
                                        elementOrder = null,
                                    ) {
                                        if (subdim < dim - 1u) addAllFrom(section.value.facesOfDimension(subdim))
                                        else add(section.value)
                                        polytope.facesOfDimension(subdim).flatMapTo(this) { polytopeMapping[subdim][it].filter { it.sign.isNonPositive() }.map { it.polytope } }
                                    }
                                },
                            ),
                            sign = Sign.Negative,
                        ),
                    )
                }
            }
        )
    }
    
    return polytopeMapping
}