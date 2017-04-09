package com.github.mttkay.maze

/**
 * A [Maze] implementation backed by a two-dimensional [Array]. Each row is
 * backed by an [IntArray] for efficient tile representation. Iteration happens
 * in row-major order.
 */
class ArrayMaze(override val width: Int, override val height: Int) : Maze, Iterable<Tile> {

  private val Int.asTile: Tile get() = Tile.values().find { it.ordinal == this }!!

  init {
    require(width > 0 && height > 0) {
      "Must be at least 1x1 in size"
    }
  }

  override fun iterator(): Iterator<Tile> = object : Iterator<Tile> {
    private val rowIter = tiles.iterator()
    private var colIter = rowIter.next().iterator()

    override fun hasNext(): Boolean {
      val hasMoreColumns = colIter.hasNext()
      return if (!hasMoreColumns) {
        rowIter.hasNext()
      } else hasMoreColumns
    }

    override fun next(): Tile {
      when {
        colIter.hasNext() ->
          // proceed reading from current row
          return colIter.next().asTile
        rowIter.hasNext() -> {
          // there are more rows; advance to next row
          colIter = rowIter.next().iterator()
          return colIter.next().asTile
        }
        else -> throw NoSuchElementException("No more tiles to read")
      }
    }
  }

  // models a rectangular stage in row major order
  private val tiles: Array<IntArray> = Array(height) { IntArray(width) { Tile.SOLID.ordinal } }

  override fun getTile(x: Int, y: Int): Tile {
    require(x < width) { "x ($x) must be smaller than width ($width)" }
    require(y < height) { "y ($y) must be smaller than height ($height)" }
    return tiles[y][x].asTile
  }

  override fun setTile(x: Int, y: Int, tile: Tile) {
    require(x < width) { "x ($x) must be smaller than width ($width)" }
    require(y < height) { "y ($y) must be smaller than height ($height)" }
    tiles[y][x] = tile.ordinal
  }

  override fun fill(tile: Tile) {
    tiles.forEach { column -> column.forEach { _ -> column.fill(tile.ordinal) } }
  }

}
