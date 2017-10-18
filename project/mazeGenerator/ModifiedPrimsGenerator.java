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
    //Cell start = maze.map[rand.nextInt(maze.sizeR)][rand.nextInt(maze.sizeC)];
    Cell start = randomizeStartCell(maze);

    // Start loop here
    // 1. Add starting cell to set cellList
    cellList.add(start);
    in[start.r][start.c] = true;

    // System.out.println("Starting at: " + start.r + " , " + start.c);
    // 1. Put all neighbouring cells of starting cell into frontier
    addNeighbours(maze, start, in, frontier);
    int totalCells = maze.sizeR * maze.sizeC;

    while (cellList.size() != totalCells) {
    // for (int i = 0; i < 5; i++) {
      // 2. Randomly select cell from frontier set
      Cell selected = frontier.get(rand.nextInt(frontier.size()));
      // System.out.println("Start with (removed from frontier): " + selected.r + " , " + selected.c);
      // 2. Remove it from frontier set
      frontier.remove(selected);
      // 2. Randomly select another cell 'closest' that is in the cellList that is adjacent to selected cell
      Cell closest = getClosestNeighbour(maze, cellList, selected);
      // System.out.println("Random closest selected from cellList: " + closest.r + " , " + closest.c);

      // 2. Get direction between selected cell and the closest
      int dir = getDirection(maze.type, selected, closest);
      // System.out.println("Carved in direction: " + dir);
      // 2. Carve a path
      maze.map[selected.r][selected.c].wall[dir].present = false;
      maze.map[selected.r][selected.c].wall[dir].drawn = false;
      // System.out.println("direction = " + dir);

      // 3. Add selected cell to set cellList
      // System.out.println("Added to cellList: " + selected.r + "," + selected.c);
      cellList.add(selected);
      in[selected.r][selected.c] = true;
      // start = selected;

      // System.out.println("In Frontier (Before add neighbours): ");
      // for (int i = 0; i < frontier.size(); i++) {
      //   System.out.println(" - At: " + frontier.get(i).r + " , " + frontier.get(i).c);
      // }

      // 3. Add neighbours of selected cell to the frontier
      addNeighbours(maze, selected, in, frontier);   // Remove

      // System.out.println("In Frontier (After add neighbour): ");
      // for (int i = 0; i < frontier.size(); i++) {
      //   System.out.println(" - At: " + frontier.get(i).r + " , " + frontier.get(i).c);
      // }

      // [DEBUG] - Uncomment to view how many cells left to process
      // System.out.println("Cells to process = " + cellList.size());
    }

    //  System.out.println("Remove selected");
    //  for (int i = 0; i < frontier.size(); i++) {
    //    System.out.println("Neighbour at: " + frontier.get(i).r + " , " + frontier.get(i).c);
    //  }


    //  if (maze.type == Maze.NORMAL)
    //    generateNormalMaze(maze, start, in, frontier);
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

    // System.out.println("Neighbours in cellSet: ");
    // for (Cell c : closestNeighbours) {
    //   System.out.println(" - At: " + c.r + " , " + c.c);
    // }
    // System.out.println();

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

    //if (start.r < end.r)
    //  return Maze.NORTH;
    //else if (start.r > end.r)
    //  return Maze.SOUTH;
    //else if (start.c > end.c)
    //  return Maze.WEST;
    //else
    //  return Maze.EAST;
  }

  // @Override
  // public void generateMaze(Maze maze) {
  //   Random rand = new Random();
  //   boolean in[][] = new boolean[maze.sizeR][maze.sizeC];
  //   List<Cell> frontier = new ArrayList<Cell>();
  //   //Cell start = maze.map[rand.nextInt(maze.sizeR)][rand.nextInt(maze.sizeC)];
  //   Cell start = maze.map[2][0];
  //
  //   if (maze.type == Maze.NORMAL)
  //     generateNormalMaze(maze, start, in, frontier);
  // } // end of generateMaze()
  //
  // // Select random starting position
  // private Cell createRandomStart(int maxRow, int maxCol) {
  //   Random rand = new Random();
  //   int min = 0, row, col;
  //
  //   row = rand.nextInt(maxRow) + min;
  //   col = rand.nextInt(maxCol) + min;
  //
  //   Cell starting = new Cell(row, col);
  //
  //   return starting;
  // }
  //
  // private void addToFrontier(Cell cell, boolean[][]in, List<Cell> front) {
  //   int xMax = in.length;
  //   int yMax = in[0].length;
  //
  //   if (inBounds(cell, xMax, yMax) && !in[cell.r][cell.c]) {
  //     front.add(cell);
  //   }
  // }
  //
  // private boolean inBounds(Cell cell, int rowMax, int colMax) {
  //   // Check to ensure coordinates are inside the maze
  //   if (cell.r < 0 || cell.c < 0 || cell.r >= rowMax || cell.c >= colMax)
  //     return false;
  //
  //   return true;
  // }
  //
  // private void mark(Cell cell, boolean[][]in, List<Cell> front) {
  //   in[cell.r][cell.c] = true;
  //
  //   if (cell.neigh[Maze.NORTH] != null)
  //     addToFrontier(cell.neigh[Maze.NORTH], in, front);
  //   if (cell.neigh[Maze.SOUTH] != null)
  //     addToFrontier(cell.neigh[Maze.SOUTH], in, front);
  //   if (cell.neigh[Maze.WEST] != null)
  //     addToFrontier(cell.neigh[Maze.WEST], in, front);
  //   if (cell.neigh[Maze.EAST] != null)
  //     addToFrontier(cell.neigh[Maze.EAST], in, front);
  // }
  //
  // private void generateNormalMaze(Maze maze, Cell start, boolean[][]in, List<Cell> front) {
  //   Random rand = new Random();
  //   Cell currCell = start;
  //   mark(maze.map[start.r][start.c], in, front);
  //
  //   int step = 4;
  //
  //   while (!front.isEmpty()) {
  //   //while (step > 0 ) {
  //     System.out.println("Frontier List: ");
  //     for (Cell p : front)
  //       System.out.println("x: " + p.r + " y: " + p.c);
  //     System.out.println("Start: " + currCell.r + ", " + currCell.c);
  //
  //     front.remove(currCell);
  //     mark(currCell, in, front);
  //
  //     Cell randFront = front.get(rand.nextInt(front.size()));
  //     //while (randFront.r == currCell.r && randFront.c == currCell.c) {
  //     //  //if (!front.isEmpty())
  //     //  randFront = front.get(rand.nextInt(front.size()));
  //     //}
  //
  //     System.out.println("Front: " + randFront.r + ", " + randFront.c);
  //
  //     currCell = randFront;
  //
  //     ArrayList<Cell> inNeigh = new ArrayList<Cell>();
  //     for (int i = 0; i < randFront.neigh.length; i++) {
  //       if (randFront.neigh[i] != null) {
  //         if (in[randFront.neigh[i].r][randFront.neigh[i].c]) {
  //           inNeigh.add(randFront.neigh[i]);
  //         }
  //       }
  //     }
  //
  //     System.out.println("In List: ");
  //     for (Cell p : inNeigh)
  //       System.out.println("x: " + p.r + " y: " + p.c);
  //
  //     Cell randNeigh = inNeigh.get(rand.nextInt(inNeigh.size()));
  //     int dir = direction(randFront, randNeigh);
  //     maze.map[randFront.r][randFront.c].wall[dir].present = false;
  //     maze.map[randFront.r][randFront.c].wall[dir].drawn = false;
  //     System.out.println("direction = " + dir);
  //
  //     step--;
  //   }
  // }
  //
} // end of class ModifiedPrimsGenerator
