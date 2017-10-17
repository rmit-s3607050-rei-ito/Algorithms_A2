package mazeGenerator;

import java.util.*;

import maze.Maze;
import maze.Cell;

public class GrowingTreeGenerator implements MazeGenerator {
  // Growing tree maze generator. As it is very general, here we implement as
  // "usually pick the most recent cell, but occasionally pick a random cell"

  // 1. Pick a random starting cell and add it to set Z (initially Z is empty,
  // after addition it contains just the starting cell).

  // 2. Using a particular strategy (see below)
  // Select a cell b from Z. If cell b has unvisited neighbouring cells, randomly
  // select a neighbour, carve a path to it, and add the selected neighbour to set Z.
  // If b has no unvisited neighbours, remove it from Z.

  // 3. Repeat step 2 until Z is empty.
  // Depending on what strategy is used to select a cell from V , we obtain different behaviour. If we
  // select the newest cell added to V , then this is the same as recursive backtracker. If we randomly select
  // a cell in V , then this is similar to Prim’s generation approach. Other strategies can be a mixture of
  // both (have a try!).

  // Determine which method to use: prim's vs recursive
  private enum METHOD {
    RANDOM,
    RECENT;
  }

  // Likelihood of random cell selection vs selecting nearest cell
  double threshold = 0.1; // 10% chance to use random cell selection

  @Override
  public void generateMaze(Maze maze) {
    // rowSize and colSize for checking off visited cells, by default for normal/tunnel
    int rowSize = maze.sizeR;
    int colSize = maze.sizeC;
    // Create an array to check all individual cells in the maze, whether visited
    boolean visited[][];
    // Change column dimensions if hex maze used
    if (maze.type == Maze.HEX)
      colSize = maze.sizeC + (maze.sizeR + 1) / 2;
    // Initialize array of bools for visited cells
    visited = new boolean[rowSize][colSize];

    // Define 'set' (using list instead) 'Z' to use, cellSet for better naming convention
    List<Cell> cellSet = new ArrayList<Cell>();

    // Select a random starting cell and add it to the cell set
    Cell startCell = randomizeStartCell(maze);
    cellSet.add(startCell);
    // Visit it calling maze.drawFtPrt() to update internal maze
    visited[startCell.r][startCell.c] = true;
    maze.drawFtPrt(startCell);

    generateGrowingTreeMaze(maze, cellSet, visited);
  }

  // #################### Maze generation ####################
  private void generateGrowingTreeMaze(Maze maze, List<Cell> cellSet, boolean[][] visited) {
    METHOD method;
    int rowDiff, colDiff, direction;

    // Iterate through until set is empty
    while(!cellSet.isEmpty()) {
      // Choose method to use based on chance
      method = selectMethod(threshold);
      // Select a cell based on the method
      Cell cell = selectCell(method, cellSet);

      // Check if cell has unvisited neighbours
      if(!hasVisitableNeighbours(maze, cell, visited))
        cellSet.remove(cell); // Remove it if all of its neighbours has been visited
      else {
        // If it has visitable neighbours randomly select one and move to it
        Cell neighbour = selectRandomNeighbour(maze, cell, visited);

        // Calculate move difference and overall direction
        rowDiff = neighbour.r - cell.r;
        colDiff = neighbour.c - cell.c;
        // Use direction to carve a path using movement calculated
        direction = getDirection(maze.type, rowDiff, colDiff);
        maze.map[cell.r][cell.c].wall[direction].present = false;
        maze.map[cell.r][cell.c].wall[direction].drawn = false;

        // Set the neighbour as visited
        visited[neighbour.r][neighbour.c] = true;
        maze.drawFtPrt(neighbour);
        // Add the neighbour to the set
        cellSet.add(neighbour);
      }

      // [DEBUG] - Uncomment to view how many cells left to process
      System.out.println("Cells to process = " + cellSet.size());
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
    }

    col = rand.nextInt(maxCol) + min;
    Cell cell = new Cell(row, col);

    return cell;
  }

  // Determine which method to use to select a cell from 'set Z'
  private METHOD selectMethod(double randomChance) {
    double random = Math.random();
    METHOD method = METHOD.RECENT;

    // Randomly decide if random method is to be used based on passed in chance
    if (random <= randomChance)
      method = METHOD.RANDOM;

    return method;
  }

  // Select cell from set depending on method used
  private Cell selectCell(METHOD method, List<Cell> cells) {
    int last = cells.size() - 1;

    if (method == METHOD.RECENT) {  // Using method 1: selecting randomly
      /* Randomly choose a cell from the set, done by converting set to list then
       * Picking randomly from that list */
       int index = getRandomCell(cells);
       return cells.get(index);
    }

    // Using method 2: selecting recently added (last element)
    return cells.get(last);
  }

  // Return index of randomly selected cell from list
  private int getRandomCell(List<Cell> list) {
    Random rand = new Random();       // Set up random generator
    int listSize = list.size();  // Get number of possible directions
    int index = rand.nextInt(listSize);

    return index;
  }

  // Check if cell still has neighbours to visit
  private boolean hasVisitableNeighbours(Maze maze, Cell cell, boolean[][] visited) {
    int row, col;

    // Check in all directions for neighbours
    for (int i = 0; i < Maze.NUM_DIR; i++) {
      // Access actual map stored in maze to get neighbours
      if (maze.map[cell.r][cell.c].neigh[i] != null) {
        row = maze.map[cell.r][cell.c].neigh[i].r;
        col = maze.map[cell.r][cell.c].neigh[i].c;
        // Check if it has been visited or not, if not there is a visitable neighbour
        if(!visited[row][col])
          return true;
      }
    }

    // All neighbours visited or there are no neighbours
    return false;
  }

  // Select random unvisited neighbour
  private Cell selectRandomNeighbour(Maze maze, Cell cell, boolean[][] visited) {
    List<Cell> visitableNeighbours = new ArrayList<Cell>();
    int index, row, col;

    // 1. Store all visitable neighbours in a list
    for (int i = 0; i < Maze.NUM_DIR; i++) {
      // Access actual map stored in maze to get neighbours
      if(maze.map[cell.r][cell.c].neigh[i] != null) {
        row = maze.map[cell.r][cell.c].neigh[i].r;
        col = maze.map[cell.r][cell.c].neigh[i].c;
        // Check if it has been visited or not, if not there is a visitable neighbour
        if(!visited[row][col])
          visitableNeighbours.add(maze.map[cell.r][cell.c].neigh[i]);
      }
    }

    // 2. Randomly select one and return it
    index = getRandomCell(visitableNeighbours);
    return visitableNeighbours.get(index);
  }

  // Get direction based on row/col shift input
  private int getDirection(int type, int row, int col) {
    // Using definitions (row,col):
    // North = 1,0      | East = 0,1      | South = -1,0     | West = 0,-1
    // NorthEast = 1,1  | NorthWest = 1,0 | SouthEAST = -1,0 | SouthWest = -1,-1

    // Rectangle/Normal type maze
    if (type == Maze.NORMAL) {
      if (row == 1)
        return Maze.NORTH;
      if (row == -1)
        return Maze.SOUTH;
      if (col == -1)
        return Maze.WEST;
    }
    else {
      // Hex type maze
      if (row == 1 && col == 1)
        return Maze.NORTHEAST;
      if (row == 1 && col == 0)
        return Maze.NORTHWEST;
      if (row == -1 && col == 0)
        return Maze.SOUTHEAST;
      if (row == -1 && col == -1)
        return Maze.SOUTHWEST;
      if (row == 0 && col == -1)
        return Maze.WEST;
    }

    // Default just return east, so its not checked
    return Maze.EAST;
  }

} // end of class GrowingTreeGenerator
