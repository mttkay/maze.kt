
import com.github.mttkay.maze.ArrayMaze
import com.github.mttkay.maze.MazeBuilder
import com.github.mttkay.maze.StringRenderer

fun main(vararg args: String) {

  val maze = ArrayMaze(width = 51, height = 31)

  MazeBuilder().build(maze)

  println(StringRenderer().render(maze))
}
