package mazeGenerator;

import maze.Wall;
import maze.Cell;
import maze.Maze;
import java.util.*;

public class ModifiedPrimsGenerator implements MazeGenerator {

  @Override
  public void generateMaze(Maze maze) {
    Random rand = new Random();
    int colSize = maze.sizeC;
    if (maze.type == Maze.HEX)
      colSize += (maze.sizeR+1) / 2;
    boolean in[][] = new boolean[maze.sizeR][colSize];
    List<Cell> cellList = new ArrayList<Cell>();
    List<Cell> frontier = new ArrayList<Cell>();
    // 1. Pick random starting cell
    Cell start = randomizeStartCell(maze);

    // Start loop here
    // 1. Add starting cell to set cellList
    cellList.add(start);
    in[start.r][start.c] = true;

    // 1. Put all neighbouring cells of starting cell into frontier
    addNeighbours(maze, start, in, frontier);
    int totalCells = maze.sizeR * maze.sizeC;

    while (cellList.size() != totalCells) {
      // 2. Randomly select cell from frontier set
      Cell selected = frontier.get(rand.nextInt(frontier.size()));
      // 2. Remove it from frontier set
      frontier.remove(selected);
      // 2. Randomly select another cell 'closest' that is in the cellList that is adjacent to selected cell
      Cell closest = getClosestNeighbour(maze, cellList, selected);

      // 2. Get direction between selected cell and the closest
      int dir = getDirection(maze.type, selected, closest);
      // 2. Carve a path
      maze.map[selected.r][selected.c].wall[dir].present = false;
      maze.map[selected.r][selected.c].wall[dir].drawn = false;

      // 3. Add selected cell to set cellList
      cellList.add(selected);
      in[selected.r][selected.c] = true;

      // 3. Add neighbours of selected cell to the frontier
      addNeighbours(maze, selected, in, frontier);   // Remove

      // [DEBUG] - Uncomment to view how many cells left to process
      // System.out.println("Cells to process = " + cellList.size());
    }
  } // end of generateMaze()

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

  private void addNeighbours(Maze maze, Cell cell, boolean[][] visited, List<Cell> frontier) {
    int index, row, col;

    // 1. Store all visitable neighbours in a list
    for (int i = 0; i < Maze.NUM_DIR; i++) {
      // Access actual map stored in maze to get neighbours
      if(maze.map[cell.r][cell.c].neigh[i] != null) {
        row = maze.map[cell.r][cell.c].neigh[i].r;
        col = maze.map[cell.r][cell.c].neigh[i].c;
        // Check if it has been visited or not, if not there is a visitable neighbour
        if(!visited[row][col] && !isInFrontier(frontier, row, col))
          frontier.add(maze.map[cell.r][cell.c].neigh[i]);
      }
    }
  }

  private boolean isInFrontier(List<Cell> frontier, int row, int col) {
    for (Cell c : frontier) {
      if (c.r == row && c.c == col)
        return true;
    }

    return false;
  }

  private Cell getClosestNeighbour(Maze maze, List<Cell> cellList, Cell cell) {
    Cell closest = new Cell();
    List<Cell> closestNeighbours = new ArrayList<Cell>();

    for (int i = 0; i < Maze.NUM_DIR; i++) {
      // Access actual map stored in maze to get neighbours
      if(maze.map[cell.r][cell.c].neigh[i] != null) {
        int row = maze.map[cell.r][cell.c].neigh[i].r;
        int col = maze.map[cell.r][cell.c].neigh[i].c;
        // Check if it has been visited or not, if not there is a visitable neighbour
        for (Cell c : cellList) {
          if (row == c.r && col == c.c)
            closestNeighbours.add(c);
        }
      }
    }
    Random rand = new Random();
    closest = closestNeighbours.get(rand.nextInt(closestNeighbours.size()));

    return closest;
  }

  private int getDirection(int type, Cell start, Cell end) {
    int row = end.r - start.r;
    int col = end.c - start.c;

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
} // end of class ModifiedPrimsGenerator
