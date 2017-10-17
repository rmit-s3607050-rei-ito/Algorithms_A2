package mazeGenerator;

import maze.Wall;
import maze.Cell;
import maze.Maze;
import java.util.*;

public class RecursiveBacktrackerGenerator implements MazeGenerator {

  // Range for directions
  final static int NUM_NORMAL_DIR = 4;
  final static int[] CARDINALS = new int[] { Maze.NORTH, Maze.SOUTH, Maze.EAST, Maze.WEST };
  final static int[] CARDINAL_ROW_SHIFT = new int[] { 1, -1, 0, 0 };
  final static int[] CARDINAL_COL_SHIFT = new int[] { 0, 0, 1, -1 };

  @Override
  public void generateMaze(Maze maze) {
    // Create an array to check all individual cells in the maze, whether they have been visited
    boolean visited[][] = new boolean[maze.sizeR][maze.sizeC];
    // Create list to store previous directions, used for backtracking
    List<Cell> previous = new ArrayList<Cell>();
    // Initialize total number of cells to check
    int toVisit = maze.sizeR * maze.sizeC;

    // Randomly pick a starting cell and visit it, update number left to visit
    Cell start = randomizeStartCell(maze.sizeR, maze.sizeC);
    toVisit = visitCell(maze, start, visited, toVisit);

    // Go through and generate the maze depending on the type
    if (maze.type == Maze.NORMAL)
      generateNormalMaze(maze, start, visited, toVisit, previous);
    else if (maze.type == Maze.HEX)
      generateHexMaze(maze);

  } // end of generateMaze()

  // #################### Maze generation ####################
  // Normal type maze
  private void generateNormalMaze(Maze maze, Cell cell, boolean[][]visited, int toVisit, List<Cell> prev) {
    List<Integer> directions = new ArrayList<Integer>();
    Cell path;
    int index, direction, lastDir;
    int rowMove, colMove, rowEnd, colEnd, prevR, prevC;

    // Terminating condition, every cell has been visited
    if (toVisit == 0)
      return;

    // Check in each direction for current cell
    for (int i = 0; i < NUM_NORMAL_DIR; i++) {
      // Calculate end point of direction
      rowEnd = cell.r + CARDINAL_ROW_SHIFT[i];
      colEnd = cell.c + CARDINAL_COL_SHIFT[i];

      // When cell is not out of bounds
      if (inBounds(rowEnd, colEnd, maze.sizeR, maze.sizeC)) {
        // If cell has not been visited add path to it as a possible direction
        if(!visited[rowEnd][colEnd])
          directions.add(i);
      }
    }

    // When there is a possible direction to move in, pick one and move to it
    if (directions.size() > 0) {
      // Pick a random index for a direction and get it from the cardinals
      index = pickRandomDirection(directions);
      direction = CARDINALS[index];
      // Store movement in 'path' variable
      rowMove = CARDINAL_ROW_SHIFT[index];
      colMove = CARDINAL_COL_SHIFT[index];
      path = new Cell(rowMove, colMove);
      // Remove and hide wall in path
      maze.map[cell.r][cell.c].wall[direction].present = false;
      maze.map[cell.r][cell.c].wall[direction].drawn = false;
      // Move to the selected direction and mark it as visited
      cell.r += rowMove;
      cell.c += colMove;
      toVisit = visitCell(maze, cell, visited, toVisit);

      // Store previous direction for backtracking the recursively repeat
      prev.add(path);
      generateNormalMaze(maze, cell, visited, toVisit, prev);
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
      generateNormalMaze(maze, cell, visited, toVisit, prev);
    }
  }

  private void generateHexMaze(Maze maze) {

  }

  // #################### Utility functions ####################
  // Select random starting position for a cell
  private Cell randomizeStartCell(int maxRow, int maxCol) {
    Random rand = new Random();
    int min, row, col;

    min = 0;  // Arrays start from 0
    row = rand.nextInt(maxRow) + min;
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

  // Mark cell as visited + reduce total number of cells left to visit by 1
  private int visitCell(Maze maze, Cell cell, boolean[][]visited, int toVisit) {
    visited[cell.r][cell.c] = true;
    toVisit--;
    maze.drawFtPrt(cell);

    return toVisit;
  }

  // Check to ensure coordinates are inside the maze
  private boolean inBounds(int row, int col, int rowMax, int colMax) {
    if (row < 0 || col < 0 || row >= rowMax || col >= colMax)
      return false;

    return true;
  }

} // end of class RecursiveBacktrackerGenerator
