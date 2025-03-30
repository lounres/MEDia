/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

import dev.lounres.kone.misc.planimetricsCalculation.Point
import dev.lounres.kone.misc.planimetricsCalculation.circumcenter
import dev.lounres.kone.misc.planimetricsCalculation.circumcircle
import dev.lounres.kone.misc.planimetricsCalculation.collinearityTest
import dev.lounres.kone.misc.planimetricsCalculation.inPlanimetricsCalculationSpaceScopeFor
import dev.lounres.kone.misc.planimetricsCalculation.intersectionOf
import dev.lounres.kone.misc.planimetricsCalculation.lineThrough
import dev.lounres.kone.misc.planimetricsCalculation.midpoint
import dev.lounres.kone.misc.planimetricsCalculation.origin
import dev.lounres.kone.misc.planimetricsCalculation.orthocenter
import dev.lounres.kone.misc.planimetricsCalculation.perpendicular
import dev.lounres.kone.misc.planimetricsCalculation.polarBy
import dev.lounres.kone.misc.planimetricsCalculation.poleBy
import dev.lounres.kone.misc.planimetricsCalculation.xBasis
import util.koneContextRegistry
import util.rationalType


fun main(): Unit = koneContextRegistry.inPlanimetricsCalculationSpaceScopeFor(rationalType) {
    /*
    Let H be an orthocenter of triangle △ABC.
    Tangent to circle ω(BHC) at H intersects lines (AB) and (AC) at P and Q respectively.
    Circle ω(APQ) intersect circle ω(ABC) again at K.
    Tangents to ω(APQ) at A and K intersect at T.
    Prove that T, H, and midpoint M of segment [AB] are collinear.

    Link: https://artofproblemsolving.com/community/c3317313_2023_kazakhstan_national_olympiad
     */
    
    // Let's declare the vertices of our triangle.
    // We assign concrete points to A and B because
    // without loss of generality, we lessen computer's work.
    // Obviously, we define C as an arbitrary point.
    val A = origin
    val B = xBasis
    val C by Point
    
    // Then we calculate the orthocenter and the tangent at it.
    val H = orthocenter(A, B, C)
    val h = H.polarBy(circumcircle(B, C, H))
    
    // Then we intersect it with the edges to get P and Q.
    val P = intersectionOf(h, lineThrough(A, B))
    val Q = intersectionOf(h, lineThrough(A, C))
    
    // Then we calculate T. But we use a different approach for it.
    // We calculate line (OK) by constructing perpendicular to
    // (ABC) and (APQ) centres line. Then T is its polar by (APQ).
    val l = perpendicular(lineThrough(circumcenter(A, B, C), circumcenter(A, P, Q)), A)
    val T = l.poleBy(circumcircle(A, P, Q))
    
    // Then we compute the midpoint M.
    val M = midpoint(B, C)
    
    // Finally, we test if the fact is really true.
    println(collinearityTest(T, H, M))
}