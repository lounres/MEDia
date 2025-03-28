/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("ClassName", "NonAsciiCharacters", "PropertyName", "LocalVariableName", "ObjectPropertyName")

import dev.lounres.kone.algebraic.Rational
import dev.lounres.kone.algebraic.one
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.utils.component1
import dev.lounres.kone.collections.utils.component2
import dev.lounres.kone.collections.utils.component3
import dev.lounres.kone.collections.utils.component4
import dev.lounres.kone.collections.utils.component5
import dev.lounres.kone.collections.utils.component6
import dev.lounres.kone.collections.utils.component7
import dev.lounres.kone.combinatorics.enumerative.permutations
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.compareTo
import dev.lounres.kone.relations.eq


/**
 * The data class is going to store solutions of the rebus.
 *
 * P.S. I called it and variables in it in Russian to not confuse myself later.
 */
data class ФЕВРАЛЬ(val Ф: Int, val Е: Int, val В: Int, val Р: Int, val А: Int, val Л: Int, val Ь: Int)

/**
 * Plain fabric that just uses the first 7 elements to construct the possible solution.
 */
fun ФЕВРАЛЬ(args: KoneList<Int>): ФЕВРАЛЬ {
    require(args.size == 7u) { "Cannot construct ФЕВРАЛЬ from list $args" }
    val (Ф, Е, В, Р, А, Л, Ь) = args
    return ФЕВРАЛЬ(Ф = Ф, Е = Е, В = В, Р = Р, А = А, Л = Л, Ь = Ь)
}

/**
 * A property that represents the first fraction in the rebus.
 */
val ФЕВРАЛЬ.frac1: Rational get() = Rational(Ф, Е)
/**
 * A property that represents the second fraction in the rebus.
 */
val ФЕВРАЛЬ.frac2: Rational get() = Rational(В * 10 + Р, (А * 10 + Л) * 10 + Ь)

/**
 * A function that validates that the sum is actually one.
 * So, if the digits are all different, the instance is the sought solution of the rebus.
 */
fun ФЕВРАЛЬ.check(): Boolean = Ф != 0 && Е != 0 && В != 0 && А != 0 && Rational.context { frac1 + frac2 eq one }

/**
 * Just a plain list of digits.
 */
val digits = (0 until 10).toKoneList()

/**
 * A list of all the rebus solutions.
 */
val февральPermutations: Sequence<ФЕВРАЛЬ> =
    digits.permutations(7u) // We generate all 7-permutations of digits list, so we get all possible combinations of non-equal digits.
        .map { ФЕВРАЛЬ(it) } // We put them in our data class.
        .filter { it.check() } // Then we filter out all non-solutions.
        .sortedWith( // In the end, we sort the solutions by value of the first fraction, then by value of Ф. I was asked to do it.
            Comparator<ФЕВРАЛЬ> { perm1, perm2 -> Rational.context { perm1.frac1.compareTo(perm2.frac1) } }.thenBy { it.Ф }
        )

fun main() {
    // So here we print all the rebus solutions in three different formats.
    println("Ф Е В Р А Л Ь | ФЕВРАЛЬ | Ф/Е+ВР/АЛЬ")
    for (февраль in февральPermutations) println(февраль.run { "$Ф $Е $В $Р $А $Л $Ь | $Ф$Е$В$Р$А$Л$Ь | $Ф/$Е+$В$Р/$А$Л$Ь" })
}