package com.github.mttkay.maze

import java.util.*

/// A two-dimensional fixed-size array of elements of type [T].
///
/// This class doesn't follow matrix notation which tends to put the column
/// index before the row. Instead, it mirrors graphics and games where x --
/// the horizontal component -- comes before y.
///
/// Internally, the elements are stored in a single contiguous list in row-major
/// order.
internal class Array2D<T>(val width: Int, val height: Int) : MutableIterable<T?> {
  override fun iterator(): MutableIterator<T?> = _elements.iterator()

  private val _elements: ArrayList<T?> = ArrayList(Collections.nCopies(width * height, null))

  /// Gets the element at [pos].
  operator fun get(pos: Vec): T? = _elements[pos.y * width + pos.x]

  /// Sets the element at [pos].
  operator fun set(pos: Vec, value: T) {
    _elements[pos.y * width + pos.x] = value
  }
}
