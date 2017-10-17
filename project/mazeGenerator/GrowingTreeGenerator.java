package mazeGenerator;

import java.util.*;

import maze.Maze;
import maze.Cell;

public class GrowingTreeGenerator implements MazeGenerator {
  // Growing tree maze generator. As it is very general, here we implement as
  // "usually pick the most recent cell, but occasionally pick a random cell"

  // 1. Pick a random starting cell and add it to set Z (initially Z is empty,
  // after addition it contains just the starting cell).

  // 2. Using a particular strategy (see below) select a cell b from Z. If cell b has unvisited neighbouring
  // cells, randomly select a neighbour, carve a path to it, and add the selected neighbour to set Z.
  // If b has no unvisited neighbours, remove it from Z.

  // 3. Repeat step 2 until Z is empty.
  // Depending on what strategy is used to select a cell from V , we obtain different behaviour. If we
  // select the newest cell added to V , then this is the same as recursive backtracker. If we randomly select
  // a cell in V , then this is similar to Prim’s generation approach. Other strategies can be a mixture of
  // both (have a try!).

  // Likelihood of random cell selection vs selecting nearest cell
  double threshold = 0.1;

  @Override
  public void generateMaze(Maze maze) {
    // Define set 'Z' to use, cellSet in this case for better naming convention
    Set<Cell> cellSet = new HashSet<Cell>();

    // Select a random starting cell and add it to the cell set
    Cell start = randomizeStartCell(maze);
  }

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
}
