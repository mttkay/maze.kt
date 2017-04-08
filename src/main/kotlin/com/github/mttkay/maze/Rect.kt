package com.github.mttkay.maze

internal data class Rect(val x: Int, val y: Int, val width: Int, val height: Int) : Iterable<Vec> {

  private val pos = Vec(x, y)
  private val size = Vec(width, height)

  val left = Math.min(x, x + width)
  val top = Math.min(y, y + height)
  val right = Math.max(x, x + width)
  val bottom = Math.max(y, y + height)

  fun containsPoint(point: Vec): Boolean {
    if (point.x < pos.x) return false
    if (point.x >= pos.x + size.x) return false
    if (point.y < pos.y) return false
    if (point.y >= pos.y + size.y) return false

    return true
  }

  fun inflate(distance: Int): Rect {
    return Rect(x - distance, y - distance,
        width + distance * 2, height + distance * 2)
  }

  /// Returns the distance between this com.github.mttkay.maze.Rect and [other]. This is minimum
  /// length that a corridor would have to be to go from one com.github.mttkay.maze.Rect to the other.
  /// If the two Rects are adjacent, returns zero. If they overlap, returns -1.
  fun distanceTo(other: Rect): Int {
    val vertical = if (top >= other.bottom) {
      top - other.bottom
    } else if (bottom <= other.top) {
      other.top - bottom
    } else {
      -1
    }

    val horizontal = if (left >= other.right) {
      left - other.right
    } else if (right <= other.left) {
      other.left - right
    } else {
      -1
    }

    if ((vertical == -1) && (horizontal == -1)) return -1
    if (vertical == -1) return horizontal
    if (horizontal == -1) return vertical
    return horizontal + vertical
  }

  override fun iterator(): Iterator<Vec> = RectIterator(this)

  class RectIterator(private val rect: Rect) : Iterator<Vec> {
    private var x: Int
    private var y: Int

    init {
      x = rect.x - 1
      y = rect.y
    }

    override fun hasNext(): Boolean {
      x++
      if (x >= rect.right) {
        x = rect.x
        y++
      }

      return y < rect.bottom
    }

    override fun next(): Vec = Vec(x, y)
  }

}
