package com.github.mttkay.maze

interface Stage {

  val width: Int

  val height: Int

  fun getTile(pos: Vec): Tile

  fun setTile(pos: Vec, tile: Tile): Unit

  fun fill(tile: Tile): Unit
}
