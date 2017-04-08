package com.github.mttkay.maze

import java.util.*

internal enum class Direction(val x: Int, val y: Int) {
  NONE(0, 0), N(0, -1), E(1, 0), S(0, 1), W(-1, 0);

  companion object {
    val CARDINALS: EnumSet<Direction> = EnumSet.of(N, E, S, W)
  }

  val vec: Vec = Vec(x, y)

  operator fun plus(other: Vec): Vec {
    return Vec(x + other.x, y + other.y)
  }

  operator fun plus(other: Int): Vec {
    return Vec(x + other, y + other)
  }

  operator fun times(other: Int) = Vec(x * other, y * other)
}
