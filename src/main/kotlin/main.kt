import com.github.mttkay.maze.Maze
import com.github.mttkay.maze.MazeView
import com.github.mttkay.maze.Tile

fun main(vararg args: String) {

  val width = 51
  val height = 31

  val view = TestMazeView(width, height)

  Maze(view).generate()

  AsciiRenderer().render(view)
}

private class TestMazeView(override val width: Int, override val height: Int) : MazeView {

  // models a rectangular stage in column major order
  private val tiles: Array<Array<Tile>> = Array(width) { Array(height) { Tile.WALL } }

  override fun getTile(x: Int, y: Int): Tile {
    require(x < width) { "x ($x) must be smaller than width ($width)" }
    require(y < height) { "y ($y) must be smaller than height ($height)" }
    return tiles[x][y]
  }

  override fun setTile(x: Int, y: Int, tile: Tile) {
    require(x < width) { "x ($x) must be smaller than width ($width)" }
    require(y < height) { "y ($y) must be smaller than height ($height)" }
    tiles[x][y] = tile
  }

  override fun fill(tile: Tile) {
    tiles.forEach { column -> column.forEach { _ -> column.fill(tile) } }
  }

}

class AsciiRenderer {

  fun render(mazeView: MazeView) {

    for (row in 0 until mazeView.height) {
      for (col in 0 until mazeView.width) {
        val tile = mazeView.getTile(col, row)
        val sym: String = when (tile) {
          Tile.WALL -> "▦"
          Tile.FLOOR -> '◻'.toString()
          Tile.CLOSED_DOOR -> '◼'.toString()
          Tile.OPEN_DOOR -> '◻'.toString()
        }
        print(sym)

        if (col == mazeView.width - 1) {
          println()
        }
      }
    }
  }
}
