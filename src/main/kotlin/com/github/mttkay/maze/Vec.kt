package com.github.mttkay.maze

internal data class Vec(val x: Int, val y: Int) {

  val lengthSquared: Int get() = x * x + y * y

  operator fun plus(other: Vec): Vec {
    return Vec(x + other.x, y + other.y)
  }

  operator fun plus(other: Int): Vec {
    return Vec(x + other, y + other)
  }

  operator fun minus(other: Vec): Vec {
    return Vec(x - other.x, y - other.y)
  }

  operator fun minus(other: Int): Vec {
    return Vec(x - other, y - other)
  }

  /// Returns `true` if the magnitude of this vector is less than [other].
  infix fun lessThan(other: Int): Boolean = lengthSquared < other * other

  operator fun times(other: Int) = Vec(x * other, y * other)

}
