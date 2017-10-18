package mazeSolver;

import java.util.*;

import maze.Maze;
import maze.Cell;

/**
 * Implements the BiDirectional recursive backtracking maze solving algorithm.
 */
public class BiDirectionalRecursiveBacktrackerSolver implements MazeSolver {
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

  // Performs DFS (Depth First Search) searches starting at both the entrance and exit.
  // Starting at the entrance of the maze, randomly choose an adjacent unvisited cell.

  // Moves to that cell update its visit status, then selects another random unvisited
  // neighbour.

  // Continues this process until deadend (no unvisited neighbours), then backtrack
  // to a previous cell that has an unvisited neighbour.

  // Randomly select one of the unvisited neighbours and repeat process until reached
  // exit (this is always possible for a perfect maze). The path from entrance to exit is the
  // solution.

  // When two DFS fronts first meet, the path from the entrance to the point they meet, and
  // path from exit to meeting point forms the two halves of shortest path (in terms of cell
  // visited) from entrance to exit. Combine these paths to get the final path solution.

  @Override
  public void solveMaze(Maze maze) {
    // Initialize solver parameters as not solved and no cells explored
    solver = new SolverParameters();

    // colSize for checking off visited cells, default value for normal / tunnel mazes
    int colSize = maze.sizeC;
    // Change column dimensions if hex maze used
    if (maze.type == Maze.HEX)
      colSize = maze.sizeC + (maze.sizeR + 1) / 2;
    // Create array that is used to mark off visited cells
    boolean visited[][] = new boolean[maze.sizeR][colSize];

    // Create list to store previous directions, used for backtracking
    List<Cell> previous = new ArrayList<Cell>();

    // Tracking number of cells explored
    int numExplored = 0;

    // Cell moving from start of maze
    Cell start = maze.entrance;
    // Cell moving from exit of maze
    // Cell end = maze.exit;

    System.out.println("Start at: " + start.r + "," + start.c);

    solveMazeRecursively(maze, start, visited, previous, numExplored);

  } // end of solveMaze()

  @Override
  public boolean isSolved() {
    return solver.getSolved();
  } // end if isSolved()

  @Override
  public int cellsExplored() {
    return solver.getNumCellsExplored();
  } // end of cellsExplored()

  // #################### Main solving function ####################
  private void solveMazeRecursively(Maze maze, Cell cell, boolean[][] visited, List<Cell> previous, int numExplored) {
    Cell neighbour;
    // 1. At cell collect all visitable neighbours and select one randomly to visit
    Cell neighbour = getRandomVisitableNeighbour(maze, cell, visited);

  }

  // #################### Utility functions ####################
  // NOTE: REMOVE FOR WHEN BI-DIRECTIONAL IS IMPLEMENTED
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

  private Cell getRandomFromList(List<Cell> list) {
    Random rand = new Random();
    int listSize = list.size();
    int index = rand.nextInt(listSize);

    return list.get(index);
  }

  private Cell getRandomVisitableNeighbour(Maze maze, Cell cell, boolean[][] visited) {
    List<Cell> visitableNeighbours = new ArrayList<Cell>();
    int index, row, col;
    Cell currCell = maze.map[cell.r][cell.c];

    // Store all visitable neighbours in a list
    for (int i = 0; i < Maze.NUM_DIR; i++) {
      // Access actual map stored in maze to get neighbours
      if(currCell.neigh[i] != null) {
        row = currCell.neigh[i].r;
        col = currCell.neigh[i].c;
        // Check if it has been visited or not, then check:
        if(!visited[row][col]) {
          // If a is wall between cell and neighbour, if not it is visitable
          if (!maze.map[cell.r][cell.c].wall[i].present)
            visitableNeighbours.add(maze.map[cell.r][cell.c].neigh[i]);
        }
      }
    }

    System.out.println("Can visit: ");
    for (Cell c : visitableNeighbours) {
      System.out.println(" - Neighbour: " + c.r + "," + c.c);
    }

    Cell randomNeighbour = getRandomFromList(visitableNeighbours);
    System.out.println("Chosen: " + randomNeighbour.r + "," + randomNeighbour.c);
    return randomNeighbour;
  }

} // end of class BiDirectionalRecursiveBackTrackerSolver
