package mazeGenerator;

import maze.Wall;
import maze.Cell;
import maze.Maze;
import java.util.*;

public class RecursiveBacktrackerGenerator implements MazeGenerator {

  // Cardinal directions for rectangle shape: N, S, E, W
  final static int NUM_CARDINALS = 4;
  final static int[] CARDINALS = new int[] { Maze.NORTH, Maze.SOUTH, Maze.EAST, Maze.WEST };
  final static int[] CARDINAL_ROW_SHIFT = new int[] { 1, -1, 0, 0 };
  final static int[] CARDINAL_COL_SHIFT = new int[] { 0, 0, 1, -1 };

  // Ordinal directions for hex shape: NE, NW, SE, SW, E, W
  final static int NUM_ORDINALS = Maze.NUM_DIR;
  final static int[] ORDINALS = new int[] { Maze.NORTHEAST, Maze.NORTHWEST,
                                            Maze.SOUTHEAST, Maze.SOUTHWEST,
                                            Maze.EAST, Maze.WEST,};
  final static int[] ORDINAL_ROW_SHIFT = new int[] { 1, 1, -1, -1, 0, 0 };
  final static int[] ORDINAL_COL_SHIFT = new int[] { 1, 0, 0, -1, 1, -1 };

  @Override
  public void generateMaze(Maze maze) {
    // rowSize and colSize for checking off visited cells, by default for normal/tunnel
    int rowSize = maze.sizeR;
    int colSize = maze.sizeC;
    // Create an array to check all individual cells in the maze, whether they have been visited
    boolean inMaze[][];
    // Create list to store previous directions, used for backtracking
    List<Cell> previous = new ArrayList<Cell>();
    // Initialize total number of cells to check
    int toVisit = maze.sizeR * maze.sizeC;
    // Randomly pick a starting cell / starting hex
    Cell startCell = randomizeStartCell(maze);

    // Change column dimensions if hex maze used
    if (maze.type == Maze.HEX)
      colSize = maze.sizeC + (maze.sizeR + 1) / 2;

    // Initialize array of bools for visited cells and visit the first starting cell
    inMaze = new boolean[rowSize][colSize];
    toVisit = visitCell(maze, startCell, inMaze, toVisit);

    // Recursively go through the rest of the maze to map it out
    recursivelyBacktrack(maze, startCell, inMaze, toVisit, previous);
  } // end of generateMaze()

  // #################### Maze generation ####################
  private void recursivelyBacktrack(Maze maze, Cell cell, boolean[][]inMaze, int toVisit, List<Cell> prev) {
    List<Integer> possibleDirs = new ArrayList<Integer>();
    Cell path;
    int index, direction, lastDir, numDirs;  // Directions and indexing
    int rowMove, colMove, rowEnd, colEnd, prevR, prevC; // Storing movement rows/cols
    int[] directions, rowShift, colShift; // Arrays accessed to get data

    // Determine direction type to use (4 points = cardinals vs 6 = ordinals)
    if(maze.type == Maze.NORMAL || maze.type == Maze.TUNNEL) {
      directions = CARDINALS;
      numDirs = NUM_CARDINALS;
      rowShift = CARDINAL_ROW_SHIFT;
      colShift = CARDINAL_COL_SHIFT;
    } else {
      directions = ORDINALS;
      numDirs = NUM_ORDINALS;
      rowShift = ORDINAL_ROW_SHIFT;
      colShift = ORDINAL_COL_SHIFT;
    }

    // Terminating condition, every cell has been inMaze
    if (toVisit == 0)
      return;

    // Check in each direction for current cell
    for (int i = 0; i < numDirs; i++) {
      // Calculate end point of direction
      rowEnd = cell.r + rowShift[i];
      colEnd = cell.c + colShift[i];

      // When cell is not out of bounds
      if (inBounds(maze, rowEnd, colEnd)) {
        // If cell has not been visited add path to it as a possible direction
        if(!inMaze[rowEnd][colEnd])
          possibleDirs.add(i);
      }
    }

    // When there is a possible direction to move in, pick one and move to it
    if (possibleDirs.size() > 0) {
      // Pick a random index for a direction and get it from the cardinals
      index = pickRandomDirection(possibleDirs);
      direction = directions[index];
      // Store movement in 'path' variable
      rowMove = rowShift[index];
      colMove = colShift[index];
      path = new Cell(rowMove, colMove);
      // Remove and hide wall in path
      maze.map[cell.r][cell.c].wall[direction].present = false;
      maze.map[cell.r][cell.c].wall[direction].drawn = false;
      // Move to the selected direction and mark it as visited
      cell.r += rowMove;
      cell.c += colMove;
      toVisit = visitCell(maze, cell, inMaze, toVisit);

      // Store previous direction for backtracking the recursively repeat
      prev.add(path);
      recursivelyBacktrack(maze, cell, inMaze, toVisit, prev);
    }
    else { // directions.size() == 0: Dead end has been reached, backtrack
      // Get direction of last move made
      lastDir = prev.size() - 1;    // Last index of array list
      prevR = prev.get(lastDir).r;
      prevC = prev.get(lastDir).c;
      prev.remove(lastDir);         // Remove last from the list

      // Move in opposite direction of previous move, multiply by -1 to reverse
      cell.r += (-1 * prevR);
      cell.c += (-1 * prevC);
      // Recursively call again moving in opposite direction
      recursivelyBacktrack(maze, cell, inMaze, toVisit, prev);
    }
  }

  // #################### Utility functions ####################
  // Select random starting position for a cell
  private Cell randomizeStartCell(Maze maze) {
    Random rand = new Random();
    int min, row, col;
    int maxRow = maze.sizeR;
    int maxCol = maze.sizeC;

    min = 0;  // Arrays start from 0
    row = rand.nextInt(maxRow) + min;

    // Further restrict values if hex type maze
    if (maze.type == Maze.HEX) {
      // Cases: When row is even or odd, which affects col generated
      if (row % 2 == 0) // Even
        min = row / 2;
      else              // Odd
        min = (row + 1) / 2;

      // Create new max from minimum (range should be maxCol - 1 due to array diff)
      maxCol += min - 1;
    }

    col = rand.nextInt(maxCol) + min;
    Cell cell = new Cell(row, col);

    return cell;
  }

  // Selecting a random direction to move to
  private int pickRandomDirection(List<Integer> dirs) {
    Random rand = new Random();       // Set up random generator
    int numDirections = dirs.size();  // Get number of possible directions
    int index = dirs.get(rand.nextInt(numDirections));  // Pick a random direction

    // Return index of direction picked
    return index;
  }

  // Mark cell as visited + reduce total number of cells left to visit by 1 and return
  private int visitCell(Maze maze, Cell cell, boolean[][]inMaze, int toVisit) {
    inMaze[cell.r][cell.c] = true;
    toVisit--;
    maze.drawFtPrt(cell);

    return toVisit;
  }

  // Check to ensure coordinates are inside the maze
  private boolean inBounds(Maze m, int r, int c) {
    // Minimum and maxiumum values for row and col
    int minR = 0, maxR = m.sizeR;
    int minC = 0, maxC = m.sizeC;

    /* Hex maze type use same check in randomizeStartHex:
     * Have to determine col boundary based on row */
    if (m.type == Maze.HEX) {
      if (r % 2 == 0)
        minC = r / 2;
      else
        minC = (r + 1) / 2;

      maxC += minC;
    }

    // Check row and col is between min and max
    return r >= minR && r < maxR && c >= minC && c < maxC;
  }
} // end of class RecursiveBacktrackerGenerator
