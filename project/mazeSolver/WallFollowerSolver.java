package mazeSolver;

import java.util.*;

import maze.Maze;
import maze.Cell;

/**
 * Implements WallFollowerSolver
 */

public class WallFollowerSolver implements MazeSolver {

  /* Used from RecursiveBacktrackerGenerator class
   * 1. Cardinal directions for rectangle shape: N, S, E, W */
  final static int NUM_CARDINALS = 4;
  final static int[] CARDINALS = new int[] { Maze.NORTH, Maze.SOUTH, Maze.EAST, Maze.WEST };
  final static int[] CARDINAL_ROW_SHIFT = new int[] { 1, -1, 0, 0 };
  final static int[] CARDINAL_COL_SHIFT = new int[] { 0, 0, 1, -1 };

  // 2. Ordinal directions for hex shape: NE, NW, SE, SW, E, W
  final static int NUM_ORDINALS = Maze.NUM_DIR;
  final static int[] ORDINALS = new int[] { Maze.NORTHEAST, Maze.NORTHWEST,
                                            Maze.SOUTHEAST, Maze.SOUTHWEST,
                                            Maze.EAST, Maze.WEST,};
  final static int[] ORDINAL_ROW_SHIFT = new int[] { 1, 1, -1, -1, 0, 0 };
  final static int[] ORDINAL_COL_SHIFT = new int[] { 1, 0, 0, -1, 1, -1 };

  /* Used as per reccommendation made on discussion board:
   * "Create a class Boolean which you'll set (from some other method)
   * when the maze is solved. Then all isSolved() needs to do is to return the
   * value of that boolean." */

  private class SolverParameters {
    boolean solved;
    int numCellsExplored;

    // Constructor
    SolverParameters() {
      solved = false;         // Default not solved
      numCellsExplored = 0;   // Default none explored
    }

    // Getters
    public boolean getSolved() {
      return solved;
    }
    public int getNumCellsExplored() {
      return numCellsExplored;
    }

    // Setters
    public void setSolved() {
      solved = true;
    }
    public void setNumCellsExplored(int numExplored) {
      numCellsExplored = numExplored;
    }
  }
  private static SolverParameters solver;

  // Follows passages in the maze, and when it comes to an intersection,
  // always turn left or right (either is okay, but as long as consistent).

  @Override
  public void solveMaze(Maze maze) {
    // Initialize solver parameters
    solver = new SolverParameters();
    int numVisited = 0;

    // Start at entrance of maze
    Cell start = new Cell(maze.entrance.r, maze.entrance.c);
    // Mark it as visited and increment number of cells explored as 1
    maze.drawFtPrt(start);
    numVisited++;

    // Traverse through maze until solved
    wallFollowerSolveMaze(maze, start, numVisited);

  } // end of solveMaze()

  @Override
  public boolean isSolved() {
    return solver.getSolved();
  } // end if isSolved()

  @Override
  public int cellsExplored() {
    return solver.getNumCellsExplored();
  } // end of cellsExplored()

  // #################### Main solving method ####################
  private void wallFollowerSolveMaze(Maze maze, Cell cell, int numVisited) {
    // List of possible directions cell can move in
    List<Integer> paths;
    Cell move;

    // Keep going until at exit
    // while(!isAtExit(maze, cell)) {
    //
    // }

    // System.out.println("Num paths: " + paths.size());
    // for(int i = 0; i < paths.size(); i++) {
    //   int direction = paths.get(i);
    //   String dirString = "";
    //
    //   if (direction == Maze.EAST)
    //     dirString = "EAST";
    //   if (direction == Maze.WEST)
    //     dirString = "WEST";
    //   if (direction == Maze.NORTH)
    //     dirString = "NORTH";
    //   if (direction == Maze.SOUTH)
    //     dirString = "SOUTH";
    //
    //   System.out.println(" - " + dirString);
    // }

    // If only one possible path, move in that direction
    // if (paths.size() == 1) {
    //   move = getMovement(maze.type, paths.get(0));
    //   System.out.println("Movement in: " + move.r + "," + move.c);
    // }
    // Otherwise pick one consistent path each time (Left/Right)

    for (int j = 0; j < 5; j++) {
      System.out.println("\nAt Cell: " + cell.r + "," + cell.c);
      paths = getPossiblePaths(maze, cell);

      if(paths.size() > 1) {
        System.out.println("Multiple paths: ");
        for (int i = 0; i < paths.size(); i++) {
          move = getMovement(maze.type, paths.get(i));
          System.out.println(" - Move: " + move.r + "," + move.c);
          
        }
      }

      Cell chosen = getMovement(maze.type, paths.get(0));
      System.out.println("Selected: " + chosen.r + "," + chosen.c);
      cell.r += chosen.r;
      cell.c += chosen.c;
      maze.drawFtPrt(cell);
    }
  }

  // #################### Utility functions ####################
  private boolean isAtExit(Maze maze, Cell cell) {
    int currRow = cell.r;
    int currCol = cell.c;
    int exitRow = maze.exit.r;
    int exitCol = maze.exit.c;

    if (currRow == exitRow && currCol == exitCol) {
      solver.setSolved();
      return true;
    }

    return false;
  }

  private List<Integer> getPossiblePaths(Maze maze, Cell cell) {
    // Check from current cell the possible paths and store it in the list
    List<Integer> paths = new ArrayList<Integer>();

    // Check all walls, for those that aren't null, check whether they are present
    // If not it is possible to move in that direction

    for (int direction = 0; direction < Maze.NUM_DIR; direction++) {
      // 1. Check if there is a wall in that direction
      if (maze.map[cell.r][cell.c].wall[direction] != null) {
        // 2. If so check if it is present
        if (maze.map[cell.r][cell.c].wall[direction].present == false)
          paths.add(direction); // No wall: there is a possible path here
      }
    }

    return paths;
  }

  private Cell getMovement(int mazeType, int direction) {
    int[] directions, rowShift, colShift;
    int numDirs = 0, index = 0, moveR = 0, moveC = 0;

    // Determine direction type to use (4 points = cardinals vs 6 = ordinals)
    if(mazeType == Maze.NORMAL) {
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

    // Loop through to find matching index for the direction
    for (int i = 0; i < numDirs; i++) {
      if (directions[i] == direction)
        index = i;
    }

    moveR = rowShift[index];
    moveC = colShift[index];

    Cell movement = new Cell(moveR, moveC);
    return movement;
  }

} // end of class WallFollowerSolver
