# maze.kt
A Kotlin port of [Hauberk's](https://github.com/munificent/hauberk) dungeon generator, 
as described in http://journal.stuffwithstuff.com/2014/12/21/rooms-and-mazes.

The original implementation is in Dart; this is a more or less direct port to Kotlin,
with some refactors for reusability and language idiomacy.

## Use

Import from https://jitpack.io/#mttkay/maze.kt, then:

```kotlin
val maze = ArrayMaze(width = 31, height = 21)
MazeBuilder().build(maze)
```

## Run

`./gradlew run`

![maze](https://github.com/mttkay/maze.kt/blob/master/maze_ascii.png)
