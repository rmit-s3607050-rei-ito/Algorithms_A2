package mazeSolver;

import java.util.*;

import maze.Maze;
import maze.Cell;

/**
 * Implements the BiDirectional recursive backtracking maze solving algorithm.
 */
public class BiDirectionalRecursiveBacktrackerSolver implements MazeSolver {
  static boolean solved = false;
  static int numExplored = 0;

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
    // colSize for checking off visited cells, default value for normal / tunnel mazes
    int colSize = maze.sizeC;
    // Change column dimensions if hex maze used
    if (maze.type == Maze.HEX)
      colSize = maze.sizeC + (maze.sizeR + 1) / 2;
    // Create array that is used to mark off visited cells
    boolean visited[][] = new boolean[maze.sizeR][colSize];

    // Create frontier list for both start and end
    List<Cell> sFrontier = new ArrayList<Cell>();
    List<Cell> eFrontier = new ArrayList<Cell>();

    // Create list to store prev directions for both start and end, used for backtracking
    List<Cell> sPrev = new ArrayList<Cell>();
    List<Cell> ePrev = new ArrayList<Cell>();

    // Tracking number of cells explored
    int numExplored = 0;

    // Cell moving from start of maze
    Cell start = maze.entrance;
    // Cell moving from exit of maze
    Cell end = maze.exit;

    // Set starting and end point as visited
    visited[end.r][end.c] = true;
    visited[start.r][start.c] = true;
    maze.drawFtPrt(start);
    maze.drawFtPrt(end);

    System.out.println("Start at: " + start.r + "," + start.c);
    System.out.println("End at: " + end.r + "," + end.c + "\n");

    // Add neighbouring cells at adjacent to start and end to respective frontiers
    addNeighbours(maze, start, visited, sFrontier);
    addNeighbours(maze, end, visited, eFrontier);

    solveMazeRecursively(maze, start, end, visited, sPrev, ePrev, sFrontier, eFrontier);

    // Once maze solved calculate number of steps taken
    int numCellsVisited = 0;
    for(int i = 0; i < maze.sizeR; i++) {
      for (int j = 0; j < colSize; j++) {
        if (visited[i][j])
          numExplored++;
      }
    }

  } // end of solveMaze()

  @Override
  public boolean isSolved() {
    return solved;
  } // end if isSolved()

  @Override
  public int cellsExplored() {
    return numExplored;
  } // end of cellsExplored()

  // #################### Main solving function ####################
  private void solveMazeRecursively(Maze maze, Cell start, Cell end, boolean[][] visited,
                                    List<Cell> sPrev, List<Cell> ePrev,
                                    List<Cell> sFrontier, List<Cell> eFrontier)
  {
    // Move starting cell 1 step
    start = performStep(maze, start, visited, sPrev);
    // Remove it from the frontier and update the frontier with its neighbours
    removeFromFrontier(start, sFrontier);
    addNeighbours(maze, start, visited, sFrontier);

    System.out.println("Start moved to: " + start.r + "," + start.c);

    // System.out.println("For start cell Frontier is: ");
    // for (Cell c : sFrontier) {
    //   System.out.println(" - At: " + c.r + "," + c.c);
    // }
    // System.out.println();

    // Move end cell 1 step and perform same calculations as start
    end = performStep(maze, end, visited, ePrev);
    removeFromFrontier(end, eFrontier);
    addNeighbours(maze, end, visited, eFrontier);

    System.out.println("End moved to: " + end.r + "," + end.c);

    // System.out.println("For end cell Frontier is: ");
    // for (Cell c : eFrontier) {
    //   System.out.println(" - At: " + c.r + "," + c.c);
    // }
    // System.out.println();

    if (frontiersOverlap(maze, sFrontier, eFrontier)) {
      solved = true;
    } else
      solveMazeRecursively(maze, start, end, visited, sPrev, ePrev, sFrontier, eFrontier);

    // if (start == maze.exit) {
    //   // Set maze to being solved
    //   solved = true;
    // }
    // else
    //   solveMazeRecursively(maze, start, visited, sPrev, sFrontier);
  }

  private Cell performStep(Maze maze, Cell cell, boolean[][] visited, List<Cell> prev) {
    // At cell collect all visitable neighbours and select one randomly to visit
    List<Cell> visitableNeighbours = getAllVisitableNeighbours(maze, cell, visited);

    // Possible neighbour to move to pick a random one a visit them
    if (visitableNeighbours.size() > 0) {
      Cell neighbour = getRandomFromList(visitableNeighbours);
      // System.out.println("Moved to: " + neighbour.r + "," + neighbour.c);
      // Store movement to list of previously made moves to backtrack later
      int row = neighbour.r - cell.r;
      int col = neighbour.c - cell.c;
      Cell previousMove = new Cell(row, col);
      prev.add(previousMove);

      // Mark it as visited and show it on the maze
      visited[neighbour.r][neighbour.c] = true;
      maze.drawFtPrt(neighbour);

      // Return neighbour to move to
      return neighbour;
    }

    // Otherwise backtrack is required, no possible neighbours to visit
    // Get move made before most recent
    int lastDir = prev.size() - 1;
    Cell lastMove = prev.get(lastDir);

    // Move cell back in opposite direction from which it moved
    cell.r += (-1 * lastMove.r);
    cell.c += (-1 * lastMove.c);
    // maze.drawFtPrt(cell);

    // System.out.println("Backtrack to: " + cell.r + "," + cell.c);

    // Remove that move from the previous move list
    prev.remove(lastDir);

    return cell;
  }

  // #################### Utility functions ####################
  private Cell getRandomFromList(List<Cell> list) {
    Random rand = new Random();
    int listSize = list.size();
    int index = rand.nextInt(listSize);

    return list.get(index);
  }

  private void addNeighbours(Maze maze, Cell cell, boolean[][] visited, List<Cell> frontier) {
    int index, row, col;

    // 1. Store all visitable neighbours in a list
    for (int i = 0; i < Maze.NUM_DIR; i++) {
      // Access actual map stored in maze to get neighbours
      if(maze.map[cell.r][cell.c].neigh[i] != null) {
        row = maze.map[cell.r][cell.c].neigh[i].r;
        col = maze.map[cell.r][cell.c].neigh[i].c;
        // Check if it has been visited or not and that there is not a wall inbetween
        if(!visited[row][col] && !maze.map[cell.r][cell.c].wall[i].present) {
          // Check if it is not already in the frontier, if so dont add it
          if (!isInFrontier(frontier, row, col))
            frontier.add(maze.map[cell.r][cell.c].neigh[i]);
        }
      }
    }
  }

  private boolean isInFrontier(List<Cell> frontier, int row, int col) {
    // Ensure that added cell is not already in the frontier (don't add twice)
    for (Cell cell : frontier) {
      if (cell.r == row && cell.c == col)
        return true;
    }

    return false;
  }

  private void removeFromFrontier(Cell cell, List<Cell> frontier) {
    for (Cell c : frontier) {
      if (c.r == cell.r && c.c == cell.c) {
        frontier.remove(c);
        return;
      }
    }
  }

  private boolean frontiersOverlap(Maze maze, List<Cell> startFrontier, List<Cell> endFrontier) {
    for (Cell s : startFrontier) {
      for (Cell e : endFrontier) {
        if (s.r == e.r && s.c == e.c) {
          // When they overlap, drawFtPrt at that position to connect the frontiers
          Cell connection = new Cell(s.r, s.c);
          maze.drawFtPrt(connection);
          System.out.println("Frontiers overlap at: " + s.r + "," + s.c);
          return true;
        }
      }
    }

    return false;
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
        // Check if it has been visited or not and that there is not a wall inbetween
        if(!visited[row][col] && !currCell.wall[i].present)
          visitableNeighbours.add(currCell.neigh[i]);
      }
    }

    // System.out.println("Cell: " + cell.r + "," + cell.c);
    // System.out.println("Can visit: ");
    // for (int i = 0; i < visitableNeighbours.size(); i++) {
    //   System.out.println(" - Neighbour: " + visitableNeighbours.get(i).r + "," + visitableNeighbours.get(i).c);
    // }

    return visitableNeighbours;
  }

} // end of class BiDirectionalRecursiveBackTrackerSolver
