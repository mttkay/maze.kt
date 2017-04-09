package com.github.mttkay.maze

class StringRenderer {

  fun render(mazeView: MazeView): String {
    val sb = StringBuilder()
    for (row in 0 until mazeView.height) {
      for (col in 0 until mazeView.width) {
        val tile = mazeView.getTile(col, row)
        val sym = when (tile) {
          Tile.SOLID -> '◼'
          Tile.OPEN -> '◻'
        }
        sb.append(sym)

        if (col == mazeView.width - 1) {
          sb.append('\n')
        }
      }
    }
    return sb.toString()
  }
}
