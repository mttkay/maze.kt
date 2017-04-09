
import com.github.mttkay.maze.Maze
import com.github.mttkay.maze.MazeView
import com.github.mttkay.maze.StringRenderer
import com.github.mttkay.maze.Tile

fun main(vararg args: String) {

  val width = 51
  val height = 31

  val view = TestMazeView(width, height)

  Maze(view).generate()

  println(StringRenderer().render(view))
}

private class TestMazeView(override val width: Int, override val height: Int) : MazeView {

  // models a rectangular stage in column major order
  private val tiles: Array<Array<Tile>> = Array(width) { Array(height) { Tile.SOLID } }

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
