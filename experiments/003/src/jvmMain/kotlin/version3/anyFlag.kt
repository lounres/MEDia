package version3

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.build
import dev.lounres.kone.collections.utils.first
import dev.lounres.kone.collections.utils.last
import dev.lounres.kone.collections.utils.reverse
import dev.lounres.kone.computationalGeometry.polytopes.PolytopicConstruction
import dev.lounres.kone.repeat


fun <
    Polytope: PolytopicConstruction.Polytope<*, Polytope, *>
> Polytope.anyFlag(): KoneList<Polytope> =
    KoneList.build {
        add(this@anyFlag)
        repeat(this@anyFlag.dimension) {
            val lastPolytope = last()
            +lastPolytope.facesOfDimension(lastPolytope.dimension - 1u).first()
        }
        reverse()
    }