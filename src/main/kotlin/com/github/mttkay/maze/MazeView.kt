package com.github.mttkay.maze

interface MazeView {

  val width: Int

  val height: Int

  fun getTile(x: Int, y: Int): Tile

  fun setTile(x: Int, y: Int, tile: Tile): Unit

  fun fill(tile: Tile): Unit
}
