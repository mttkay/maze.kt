package com.github.mttkay.maze

import java.util.*

class MazeBuilder(private val numRoomTries: Int = 200,
                  private val extraConnectorChance: Int = 0,
                  private val roomExtraSize: Int = 0,
                  private val windingPercent: Int = 0,
                  private val removeDeadEnds: Boolean = true) {

  private lateinit var maze: Maze

  private lateinit var bounds: Rect

  /// For each open position in the dungeon, the index of the connected region
  /// that that position is a part of.
  private lateinit var regions: Array2D<Int?>

  private val random = Random()

  private val rooms = mutableListOf<Rect>()

  /// The index of the current region being carved.
  private var currentRegion = -1

  fun build(target: Maze) {
    require(target.width % 2 != 0 && target.height % 2 != 0) {
      "The maze must be odd-sized."
    }
    this.maze = target
    this.bounds = Rect(0, 0, maze.width, maze.height)
    this.regions = Array2D(maze.width, maze.height)

    // start with a solid map
    maze.fill(Tile.SOLID)

    // carve out rooms randomly
    addRooms()

    // Fill in all of the empty space with mazes.
    (1 until bounds.height step 2).forEach { y ->
      (1 until bounds.width step 2)
          .filter { x -> maze.getTile(x, y) == Tile.SOLID }
          .forEach { x -> growMaze(Vec(x, y)) }
    }

    // carve random openings between corridors
    connectRegions()

    // simplify the maze
    if (removeDeadEnds) {
      removeDeadEnds()
    }
  }

  /// Places rooms ignoring the existing maze corridors.
  private fun addRooms() {
    for (i in 0 until numRoomTries) {
      // Pick a random room size. The funny math here does two things:
      // - It makes sure rooms are odd-sized to line up with maze.
      // - It avoids creating rooms that are too rectangular: too tall and
      //   narrow or too wide and flat.
      // TODO: This isn't very flexible or tunable. Do something better here.
      val size = random.nextInt(1, 3 + roomExtraSize) * 2 + 1
      val rectangularity = random.nextInt(1 + size / 2) * 2
      var width = size
      var height = size
      if (random.oneIn(2)) {
        width += rectangularity
      } else {
        height += rectangularity
      }

      val x = random.nextInt((bounds.width - width) / 2) * 2 + 1
      val y = random.nextInt((bounds.height - height) / 2) * 2 + 1

      val room = Rect(x, y, width, height)

      val overlaps = rooms.any { room.distanceTo(it) <= 0 }

      if (!overlaps) {
        rooms.add(room)

        startRegion()
        for (cell in room) {
          carve(cell)
        }
      }
    }
  }

  /// Implementation of the "growing tree" algorithm from here:
  /// http://www.astrolog.org/labyrnth/algrithm.htm.
  private fun growMaze(start: Vec) {
    startRegion()
    carve(start)

    val cells = mutableListOf(start)
    var lastDirection = Direction.NONE

    while (cells.isNotEmpty()) {
      val cell = cells.last()

      // See which adjacent cells are open.
      val possibleDirections = Direction.CARDINALS.filter { direction ->
        canCarve(cell, direction)
      }

      lastDirection = if (possibleDirections.isNotEmpty()) {
        // Based on how "windy" passages are, try to prefer carving in the
        // same direction.
        val direction = if (possibleDirections.contains(lastDirection) && random.nextInt(100) > windingPercent) {
          lastDirection
        } else {
          random.item(possibleDirections)
        }

        val nextCell = direction + cell
        val destinationCell = direction * 2 + cell

        // carve out two cells, with the second being our new point of reference
        carve(nextCell)
        carve(destinationCell)

        cells.add(destinationCell)

        // keep carving in same direction
        direction
      } else {
        // No adjacent uncarved cells.
        cells.removeAt(cells.lastIndex)

        // This path has ended.
        Direction.NONE
      }
    }
  }

  private fun connectRegions() {
    // Find all of the SOLID tiles that can connect two (or more) regions.
    val connectorRegions = findRegionConnectors()

    // Keep track of which regions have been merged. This maps an original
    // region index to the one it has been merged to;
    // start with all regions being unmerged, i.e. map to themselves
    val merged = mutableMapOf(*(0..currentRegion).map { Pair(it, it) }.toTypedArray())
    val openRegions = mutableSetOf(*merged.keys.toTypedArray())
    val mappedRegionsFor = { connector: Vec ->
      connectorRegions.getValue(connector)
          .map { region -> merged[region] }
          .toSet()
    }

    // Keep connecting regions until we're down to one.
    var connectors: Collection<Vec> = connectorRegions.keys
    while (openRegions.size > 1 && connectors.isNotEmpty()) {
      val connector = random.item(connectors)

      // Carve the connection.
      carve(connector)

      // Merge the connected regions. We'll pick one region (arbitrarily) and
      // map all of the other regions to its index.
      val mappedRegions = mappedRegionsFor(connector)
      val targetRegion = mappedRegions.first()!! // this is safe, since there are at least 2 elements
      val sources = mappedRegions.toList().let { it.slice(1..it.lastIndex) }

      // Merge all of the affected regions. We have to look at *all* of the
      // regions because other regions may have previously been merged with
      // some of the ones we're merging now.
      (0..currentRegion)
          .filter { region -> sources.contains(merged[region]) }
          .forEach { region -> merged[region] = targetRegion }

      // The sources are no longer in use.
      openRegions.removeAll(sources)

      // Remove any connectors that aren't needed anymore.
      connectors = connectors.filterNot { cell ->
        // Don't allow connectors right next to each other.
        if ((connector - cell) lessThan 2) {
          true
        } else {
          // If the connector no longer spans different regions, we don't need it.
          if (mappedRegionsFor(cell).size > 1) {
            false
          } else {
            // This connecter isn't needed, but connect it occasionally so that the
            // dungeon isn't singly-connected.
            if (random.oneIn(extraConnectorChance)) {
              carve(cell)
            }
            true
          }
        }
      }
    }
  }

  private fun findRegionConnectors(): MutableMap<Vec, Set<Int>> {
    val connectorRegions = mutableMapOf<Vec, Set<Int>>().withDefault { emptySet<Int>() }
    bounds.inflate(-1).forEach { cell ->
      // Can't already be part of a region.
      if (maze.getTile(cell.x, cell.y) == Tile.SOLID) {
        val regions = Direction.CARDINALS
            .mapNotNull { direction -> this.regions[direction + cell] }

        if (regions.size >= 2) {
          connectorRegions[cell] = regions.toSet()
        }
      }
    }
    return connectorRegions
  }

  private fun removeDeadEnds() {
    var done = false

    while (!done) {
      done = true

      for (cell in bounds.inflate(-1)) {
        if (maze.getTile(cell.x, cell.y) == Tile.SOLID) {
          continue
        }

        // If it only has one exit, it's a dead end.
        val exits = Direction.CARDINALS.count {
          val (x, y) = cell + it.vec
          maze.getTile(x, y) != Tile.SOLID
        }

        if (exits != 1) {
          continue
        }

        done = false
        maze.setTile(cell.x, cell.y, Tile.SOLID)
      }
    }
  }

  // Gets whether or not an opening can be carved from the given starting
  // cell to the adjacent cell facing direction. Returns `true`
  // if the starting cell is in bounds and the destination cell is filled
  // (or out of bounds).
  private fun canCarve(cell: Vec, direction: Direction): Boolean {
    // Must end in bounds.
    if (!bounds.containsPoint(cell + direction * 3)) {
      return false
    }

    // Destination must not be open.
    val dest = cell + direction * 2
    return maze.getTile(dest.x, dest.y) == Tile.SOLID
  }

  private fun startRegion() = currentRegion++

  private fun carve(pos: Vec) {
    maze.setTile(pos.x, pos.y, Tile.OPEN)
    regions[pos] = currentRegion
  }
}
