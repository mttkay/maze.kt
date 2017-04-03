import com.github.mttkay.maze.Maze
import com.github.mttkay.maze.Stage
import com.github.mttkay.maze.Tile
import com.github.mttkay.maze.Vec

fun main(vararg args: String) {

  val stage = TestStage(width = 51, height = 31)

  Maze().generate(stage)

  // render stage
  AsciiRenderer().render(stage)
}

class TestStage(override val width: Int, override val height: Int) : Stage {

  // models a rectangular stage in column major order
  private val tiles: Array<Array<Tile>> = Array(width) { Array(height) { Tile.WALL } }

  override fun getTile(pos: Vec): Tile {
    require(pos.x < width) { "x (${pos.x}) must be smaller than width ($width)" }
    require(pos.y < height) { "y (${pos.y}) must be smaller than height ($height)" }
    return tiles[pos.x][pos.y]
  }

  override fun setTile(pos: Vec, tile: Tile) {
    require(pos.x < width) { "x (${pos.x}) must be smaller than width ($width)" }
    require(pos.y < height) { "y (${pos.y}) must be smaller than height ($height)" }
    tiles[pos.x][pos.y] = tile
  }

  override fun fill(tile: Tile) {
    tiles.forEach { column -> column.forEach { _ -> column.fill(tile) } }
  }

}

class AsciiRenderer {

  fun render(stage: Stage) {

    for (row in 0 until stage.height) {
      for (col in 0 until stage.width) {
        val tile = stage.getTile(Vec(col, row))
        val sym: String = when (tile) {
          Tile.WALL -> "▦"
          Tile.FLOOR -> '◻'.toString()
          Tile.CLOSED_DOOR -> '◼'.toString()
          Tile.OPEN_DOOR -> '◻'.toString()
        }
        print(sym)

        if (col == stage.width - 1) {
          println()
        }
      }
    }
  }
}
