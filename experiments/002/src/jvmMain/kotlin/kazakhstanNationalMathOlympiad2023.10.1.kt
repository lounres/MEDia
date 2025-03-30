/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("LocalVariableName")

import dev.lounres.kone.misc.planimetricsCalculation.Point
import dev.lounres.kone.misc.planimetricsCalculation.circumcenter
import dev.lounres.kone.misc.planimetricsCalculation.circumcircle
import dev.lounres.kone.misc.planimetricsCalculation.inPlanimetricsCalculationSpaceScopeFor
import dev.lounres.kone.misc.planimetricsCalculation.intersectionOf
import dev.lounres.kone.misc.planimetricsCalculation.isLyingOn
import dev.lounres.kone.misc.planimetricsCalculation.lineThrough
import dev.lounres.kone.misc.planimetricsCalculation.origin
import dev.lounres.kone.misc.planimetricsCalculation.perpendicular
import dev.lounres.kone.misc.planimetricsCalculation.polarBy
import dev.lounres.kone.misc.planimetricsCalculation.reflectThrough
import dev.lounres.kone.misc.planimetricsCalculation.xBasis
import util.koneContextRegistry
import util.rationalType


fun main(): Unit = koneContextRegistry.inPlanimetricsCalculationSpaceScopeFor(rationalType) {
    /*
    Let angle C of triangle △ABC be obtuse and AC>BC.
    The triangle is inscribed in a circle ω with a centre at O.
    Tangent to ω at C intersects line (AB) at point D.
    Let Ω be circumcircle of triangle △ABO.
    Lines (OD) and (AC) intersect Ω again at E and F respectively.
    Let K be intersection of (BC) and (DO)
    and T be intersection of (OF) and (CE).
    Prove that points O, T, B, K are cocircular.

    Link: https://artofproblemsolving.com/community/c3317313_2023_kazakhstan_national_olympiad
     */
    
    // Let's declare the vertices of our triangle.
    // We assign concrete points to A and B because
    // without loss of generality, we lessen computer's work.
    // Obviously, we define C as an arbitrary point.
    val A = origin
    val B = xBasis
    val C by Point
    
    // Then we calculate the centres of the circles ω and Ω.
    // O' will be used later.
    val O = circumcenter(A, B, C)
    val Oprime = circumcenter(A, B, O)
    
    // Then we calculate the point D.
    // Note that the tangent is calculated as a polar of its touch point.
    val D = intersectionOf(lineThrough(A, B), C.polarBy(circumcircle(A, B, C)))
    
    // Then we calculate the points E and F as reflections
    // of already lying on respective circles points.
    val E = O.reflectThrough(perpendicular(lineThrough(O, D), Oprime))
    val F = A.reflectThrough(perpendicular(lineThrough(A, C), Oprime))
    
    // Then we calculate the K and T in the most obvious way.
    val K = intersectionOf(lineThrough(B, C), lineThrough(O, D))
    val T = intersectionOf(lineThrough(O, F), lineThrough(C, E))
    
    // Finally, we check that T lies on circumcircle of △OBK.
    println(T isLyingOn circumcircle(O, B, K))
}