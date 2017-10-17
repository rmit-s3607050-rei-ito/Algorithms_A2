package mazeGenerator;

import maze.Wall;
import maze.Cell;
import maze.Maze;
import java.util.*;

public class RecursiveBacktrackerGenerator implements MazeGenerator {

  // Range for directions
  final static int NUM_NORMAL_DIR = 4;
  final static int[] normalMazeDirections = new int[] { Maze.NORTH, Maze.SOUTH,
                                                        Maze.EAST, Maze.WEST };
  final static int[] normalRowMovement = new int[] { 1, -1, 0, 0 };
  final static int[] normalColMovement = new int[] { 0, 0, 1, -1 };

  Cell start;           // Starting Cell
  static int cellsLeft = 0;

  @Override
  public void generateMaze(Maze maze) {
    // Create an array to check all individual cells in the maze, whether they have been visited
    boolean visited[][] = new boolean[maze.sizeR][maze.sizeC];
    // Create list to store previous directions, used for backtracking
    List<Cell> previous = new ArrayList<Cell>();

    cellsLeft = maze.sizeR * maze.sizeC;

    // 1. Randomly pick a starting cell, set it as visited
    start = createRandomStart(maze.sizeR, maze.sizeC);
    visited[start.r][start.c] = true;
    cellsLeft--;
    maze.drawFtPrt(start);

    if (maze.type == Maze.NORMAL)
      generateNormalMaze(maze, start, visited, previous);
    else (if maze.type == Maze.HEX)

  } // end of generateMaze()

  // Select random starting position
  private Cell createRandomStart(int maxRow, int maxCol) {
    Random rand = new Random();
    int min, row, col;

    min = 0;  // Arrays start from 0
    row = rand.nextInt(maxRow) + min;
    col = rand.nextInt(maxCol) + min;

    Cell starting = new Cell(row, col);

    return starting;
  }

  // Check if entire maze has been visited for termination case
  private boolean entireMazeVisited(int maxRow, int maxCol, boolean[][] visited) {
    // Return true when all have been visited, false when still possible cells
    for (int row = 0; row < maxRow; row++) {
      for (int col = 0; col < maxCol; col++) {
        if (!visited[row][col])
          return false;
      }
    }

    return true;
  }

  // Selecting a random direction to move to
  private int pickRandomDirection(List<Integer> dirs) {
    Random rand = new Random();       // Set up random generator
    int numDirections = dirs.size();  // Get number of possible directions
    int index = dirs.get(rand.nextInt(numDirections));  // Pick a random direction

    // Return index of direction picked
    return index;
  }

  // Generate the maze for normal type maze
  private void generateNormalMaze(Maze maze, Cell cell, boolean[][]visited, List<Cell> previous) {
    int rowEnd, colEnd;
    List<Integer> directions = new ArrayList<Integer>();
    Cell path;
    int index, direction, lastDir;
    int rowMove, colMove, prevR, prevC;
    int totalCells = maze.sizeR * maze.sizeC;

    // Terminating condition, every cell has been visited
    if (cellsLeft == 0)
      return;

    // Check in each direction for current cell
    for (int i = 0; i < NUM_NORMAL_DIR; i++) {
      // Calculate end point of direction
      rowEnd = cell.r + normalRowMovement[i];
      colEnd = cell.c + normalColMovement[i];

      // When cell is not out of bounds
      if (inBounds(rowEnd, colEnd, maze.sizeR, maze.sizeC)) {
        // If cell has not been visited add path to it as a possible direction
        if(!visited[rowEnd][colEnd])
          directions.add(i);
      }
    }

    // When there is a possible direction to move in, pick one and move to it
    if (directions.size() > 0) {
      // Pick a random direction from all possible directions
      index = pickRandomDirection(directions);
      direction = normalMazeDirections[index];
      // Store movement in 'path' variable
      rowMove = normalRowMovement[index];
      colMove = normalColMovement[index];
      path = new Cell(rowMove, colMove);
      // Remove and hide wall in path
      maze.map[cell.r][cell.c].wall[direction].present = false;
      maze.map[cell.r][cell.c].wall[direction].drawn = false;
      // Move to the selected direction and mark it as visited
      cell.r += rowMove;
      cell.c += colMove;
      // Set cell to be visited, reduce total number of cells left to visit by 1
      visited[cell.r][cell.c] = true;
      cellsLeft--;
      // Update the maze with cell drawn
      maze.drawFtPrt(cell);

      // Store previous direction for backtracking
      previous.add(path);

      // Recursively call and repeat
      generateNormalMaze(maze, cell, visited, previous);
    } else if (directions.size() == 0) { // Dead end has been reached, backtrack
      // Get direction of last move made
      lastDir = previous.size() - 1;    // Last index of array list
      prevR = previous.get(lastDir).r;
      prevC = previous.get(lastDir).c;
      previous.remove(lastDir);         // Remove last from the list

      // Move in opposite direction of previous move, multiply by -1 to reverse
      cell.r += (-1 * prevR);
      cell.c += (-1 * prevC);
      generateNormalMaze(maze, cell, visited, previous);
    }
  }

  // Utility function
  private boolean inBounds(int row, int col, int rowMax, int colMax) {
    // Check to ensure coordinates are inside the maze
    if (row < 0 || col < 0 || row >= rowMax || col >= colMax)
      return false;

    return true;
  }

} // end of class RecursiveBacktrackerGenerator
