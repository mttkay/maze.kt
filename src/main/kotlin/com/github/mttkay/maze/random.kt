package com.github.mttkay.maze

import java.util.*

internal fun Random.nextInt(min: Int, bound: Int): Int =
    Math.max(nextInt(bound), min)

internal fun <T> Random.item(coll: Collection<T>): T =
    coll.elementAt(nextInt(coll.size))

internal fun Random.oneIn(chance: Int): Boolean =
    if (chance == 0) false else nextInt(chance) == 0
