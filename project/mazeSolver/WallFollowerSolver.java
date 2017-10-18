package mazeSolver;

import java.util.*;

import maze.Maze;
import maze.Cell;

/**
 * Implements WallFollowerSolver
 */

public class WallFollowerSolver implements MazeSolver {

  boolean solved = false;
  int explored = 0;

  @Override
  public void solveMaze(Maze maze) {
    Cell curr = maze.entrance;
    int facing = getStartFace(curr);

    int count = 3;
    //while (count > 0) {
    while (!solved) {
      int[] priority = new int[4];
      System.out.println("Facing: " + facing);
      switch (facing) {
        case Maze.NORTH:
          //System.out.println("North");
          priority[0] = Maze.EAST;
          priority[1] = Maze.NORTH;
          priority[2] = Maze.WEST;
          priority[3] = Maze.SOUTH;
          break;
        case Maze.SOUTH:
          //System.out.println("South");
          priority[0] = Maze.WEST;
          priority[1] = Maze.SOUTH;
          priority[2] = Maze.EAST;
          priority[3] = Maze.NORTH;
          break;
        case Maze.EAST:
          //System.out.println("East");
          priority[0] = Maze.NORTH;
          priority[1] = Maze.EAST;
          priority[2] = Maze.SOUTH;
          priority[3] = Maze.WEST;
          break;
        case Maze.WEST:
          //System.out.println("West");
          priority[0] = Maze.SOUTH;
          priority[1] = Maze.WEST;
          priority[2] = Maze.NORTH;
          priority[3] = Maze.EAST;
          break;
      }

      System.out.println("Curr pos: " + curr.r + " , " + curr.c);
      System.out.println("Exit: " + maze.exit.r + " , " + maze.exit.c);
      for (int i = 0; i < priority.length; i++) {
        //System.out.println("Priority: " + priority[i]);
        if (curr.r == maze.exit.r && curr.c == maze.exit.r) {
          System.out.println("solved!");
          solved = true;
          maze.drawFtPrt(curr.neigh[priority[i]]);
          break;
        } else if (!curr.wall[priority[i]].present) {
          System.out.println("No wall at: " + priority[i]);
          facing = priority[i];
          maze.drawFtPrt(curr.neigh[priority[i]]);
          curr = curr.neigh[priority[i]];
          explored++;
          System.out.println("New pos: " + curr.r + " , " + curr.c);
          break;
        } else {
          solved = false;
        }
      }

      //System.out.println("Facing after: " + facing);
      //count--;
    }
  } // end of solveMaze()

  private int getStartFace(Cell start) {
    Random rand = new Random();
    List<Integer> randDir = new ArrayList<Integer>();
    for (int i = 0; i < start.wall.length; i++) {
      if (start.wall[i] != null) {
        if (!start.wall[i].present && !start.wall[i].drawn) {
          randDir.add(i);
        }
      }
    }

    return randDir.get(rand.nextInt(randDir.size()));
  }

  @Override
  public boolean isSolved() {
    return solved;
  } // end if isSolved()


  @Override
  public int cellsExplored() {
    return explored;
  } // end of cellsExplored()

} // end of class WallFollowerSolver
