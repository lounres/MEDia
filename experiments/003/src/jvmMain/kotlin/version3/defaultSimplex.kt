package version3

import dev.lounres.kone.algebraic.basis.VectorSpaceBasis
import dev.lounres.kone.algebraic.context
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.implementations.KoneArrayFixedCapacityList
import dev.lounres.kone.collections.list.relations.equality
import dev.lounres.kone.collections.list.relations.hashing
import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.associate
import dev.lounres.kone.collections.map.build
import dev.lounres.kone.collections.map.get
import dev.lounres.kone.collections.map.mapsTo
import dev.lounres.kone.collections.set.KoneMutableReifiedSet
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.build
import dev.lounres.kone.collections.set.of
import dev.lounres.kone.collections.utils.map
import dev.lounres.kone.collections.utils.sum
import dev.lounres.kone.collections.utils.withIndex
import dev.lounres.kone.combinatorics.enumerative.cartesianProduct
import dev.lounres.kone.combinatorics.enumerative.permutationsWithoutRepetitions
import dev.lounres.kone.computationalGeometry.PointWrapper
import dev.lounres.kone.computationalGeometry.polytopes.AbstractPolytopicConstruction
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor


fun <Number, Vector> defaultSimplex(
    dimension: UInt,
    basis: VectorSpaceBasis.Finite<Number, Vector>,
): AbstractPolytopicConstruction<PointWrapper<Vector>> =
    AbstractPolytopicConstruction<PointWrapper<Vector>>(dimension).apply {
        val vertices = KoneList(dimension) { index -> addVertex(PointWrapper(basis[index])) }
        val faces = KoneArrayFixedCapacityList<KoneMap<KoneList<UInt>, AbstractPolytopicConstruction.Polytope<PointWrapper<Vector>>>>(dimension)
        faces.add(
            vertices.withIndex().associate(
                keyEquality = KoneList.equality(UInt.context),
                keyHashing = KoneList.hashing(UInt.context),
            ) { (index, vertex) ->
                KoneList(dimension) { if (it == index) 1u else 0u } mapsTo vertex.asPolytope()
            }
        )
        for (dim in 2u .. dimension) faces.add(
            KoneMap.build(
                keyEquality = KoneList.equality(UInt.context),
                keyHashing = KoneList.hashing(UInt.context),
            ) {
                for (flags in KoneList(dimension) { if (it < dim) 1u else 0u }.permutationsWithoutRepetitions(equality = UInt.context))
                    this[flags] = addPolytope(
                        dimension = dim - 1u,
                        vertices = KoneReifiedSet.build(
                            elementEquality = Equality.absoluteFor(),
                            elementHashing = Hashing.defaultFor(),
                        ) {
                            for ((index, flag) in flags.withIndex()) if (flag == 1u) add(vertices[index])
                        },
                        faces = KoneList(dim - 1u) {
                            KoneMutableReifiedSet.of<AbstractPolytopicConstruction.Polytope<PointWrapper<Vector>>>(
                                elementEquality = Equality.absoluteFor(),
                                elementHashing = Hashing.defaultFor(),
                            )
                        }.apply {
                            for (subflags in cartesianProduct(flags.map { (0u .. it).toKoneList() })) if (UInt.context { subflags.sum() } in 1u ..< dim)
                                this[UInt.context { subflags.sum() } - 1u].add(faces[UInt.context { subflags.sum() } - 1u][subflags])
                        }
                    )
            }
        )
    }