package com.github.mttkay.maze

class StringRenderer {

  fun render(maze: Maze): String {
    val sb = StringBuilder()
    for (row in 0 until maze.height) {
      for (col in 0 until maze.width) {
        val tile = maze.getTile(col, row)
        val sym = when (tile) {
          Tile.SOLID -> '◼'
          Tile.OPEN -> '◻'
        }
        sb.append(sym)

        if (col == maze.width - 1) {
          sb.append('\n')
        }
      }
    }
    return sb.toString()
  }
}
