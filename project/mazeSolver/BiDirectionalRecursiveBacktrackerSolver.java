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
  // to a prev cell that has an unvisited neighbour.

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

    // Create list to store prev directions, used for backtracking
    List<Cell> prev = new ArrayList<Cell>();

    // Tracking number of cells explored
    int numExplored = 0;

    // Cell moving from start of maze
    Cell start = maze.entrance;
    // Cell moving from exit of maze
    // Cell end = maze.exit;

    // Set starting point as visited, add it as a previously moved location
    visited[start.r][start.c] = true;
    maze.drawFtPrt(start);

    System.out.println("Start at: " + start.r + "," + start.c);

    solveMazeRecursively(maze, start, visited, prev);

    // Once maze solved calculate number of steps taken
    int numCellsVisited = 0;
    for(int i = 0; i < maze.sizeR; i++) {
      for (int j = 0; j < colSize; j++) {
        if (visited[i][j]) {
          // System.out.println("Visited: " + i + "," + j);
          numCellsVisited++;
        }
      }
    }
    solver.setNumCellsExplored(numCellsVisited);

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
  private void solveMazeRecursively(Maze maze, Cell cell, boolean[][] visited, List<Cell> prev) {
    // 1. At cell collect all visitable neighbours and select one randomly to visit
    List<Cell> visitableNeighbours = getAllVisitableNeighbours(maze, cell, visited);

    // Pick a random neighbour a visit them
    if (visitableNeighbours.size() > 0) {
      Cell neighbour = getRandomFromList(visitableNeighbours);
      System.out.println("Moved to: " + neighbour.r + "," + neighbour.c);
      // Store movement to list of previously made moves to backtrack later
      int row = neighbour.r - cell.r;
      int col = neighbour.c - cell.c;
      Cell previousMove = new Cell(row, col);
      prev.add(previousMove);

      // Mark it as visited and show it on the maze
      visited[neighbour.r][neighbour.c] = true;
      maze.drawFtPrt(neighbour);

      // Move cell to neighbour's location
      cell = neighbour;

      // Check whether end spot is actually exit, if so then just exit
      if (cell == maze.exit) {
        solver.setSolved();
        return;
      }

      // Otherwise recursively call to continue
      solveMazeRecursively(maze, cell, visited, prev);
    } else {
      // Get move made before most recent
      int lastDir = prev.size() - 1;
      Cell lastMove = prev.get(lastDir);
      // System.out.println("Move back: " + (-1 * lastMove.r) + "," + (-1 * lastMove.c));

      // Move cell back in opposite direction from which it moved
      cell.r += (-1 * lastMove.r);
      cell.c += (-1 * lastMove.c);
      maze.drawFtPrt(cell);

      System.out.println("Moved back to: " + cell.r + "," + cell.c);

      // Remove that move from the previous move list
      prev.remove(lastDir);
      // Recursively call to continue
      solveMazeRecursively(maze, cell, visited, prev);
    }
  }

  // #################### Utility functions ####################
  private Cell getRandomFromList(List<Cell> list) {
    Random rand = new Random();
    int listSize = list.size();
    int index = rand.nextInt(listSize);

    return list.get(index);
  }

  private List<Cell> getAllVisitableNeighbours(Maze maze, Cell cell, boolean[][] visited) {
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

    // System.out.println("Can visit: ");
    // for (int i = 0; i < visitableNeighbours.size(); i++) {
    //   System.out.println(" - Neighbour: " + visitableNeighbours.get(i).r + "," + visitableNeighbours.get(i).c);
    // }

    return visitableNeighbours;
  }

} // end of class BiDirectionalRecursiveBackTrackerSolver
