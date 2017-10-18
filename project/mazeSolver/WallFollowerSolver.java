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
    int colSize = maze.sizeC;
    if (maze.type == Maze.HEX)
      colSize += (maze.sizeR+1) / 2;
    boolean[][] visited = new boolean[maze.sizeR][colSize];
    Cell curr = maze.entrance;
    int facing = getStartFace(curr);
    maze.drawFtPrt(maze.entrance);
    explored++;

    while (!solved) {
      int[] priority;
      if (maze.type == Maze.NORMAL) {
        priority = new int[4];
        switch (facing) {
          case Maze.NORTH:
            priority[0] = Maze.EAST;
            priority[1] = Maze.NORTH;
            priority[2] = Maze.WEST;
            priority[3] = Maze.SOUTH;
            break;
          case Maze.SOUTH:
            priority[0] = Maze.WEST;
            priority[1] = Maze.SOUTH;
            priority[2] = Maze.EAST;
            priority[3] = Maze.NORTH;
            break;
          case Maze.EAST:
            priority[0] = Maze.SOUTH;
            priority[1] = Maze.EAST;
            priority[2] = Maze.NORTH;
            priority[3] = Maze.WEST;
            break;
          case Maze.WEST:
            priority[0] = Maze.NORTH;
            priority[1] = Maze.WEST;
            priority[2] = Maze.SOUTH;
            priority[3] = Maze.EAST;
            break;
        }
      } else {
        priority = new int[6];
        switch (facing) {
          case Maze.NORTHEAST:
            priority[0] = Maze.EAST;
            priority[1] = Maze.NORTHEAST;
            priority[2] = Maze.SOUTHEAST;
            priority[3] = Maze.NORTHWEST;
            priority[4] = Maze.WEST;
            priority[5] = Maze.SOUTHWEST;
            break;
          case Maze.SOUTHWEST:
            priority[0] = Maze.WEST;
            priority[1] = Maze.SOUTHWEST;
            priority[2] = Maze.NORTHWEST;
            priority[3] = Maze.SOUTHEAST;
            priority[4] = Maze.EAST;
            priority[5] = Maze.NORTHEAST;
            break;
          case Maze.NORTHWEST:
            priority[0] = Maze.NORTHEAST;
            priority[1] = Maze.NORTHWEST;
            priority[2] = Maze.EAST;
            priority[3] = Maze.WEST;
            priority[4] = Maze.SOUTHWEST;
            priority[5] = Maze.SOUTHEAST;
            break;
          case Maze.SOUTHEAST:
            priority[0] = Maze.SOUTHWEST;
            priority[1] = Maze.SOUTHEAST;
            priority[2] = Maze.WEST;
            priority[3] = Maze.EAST;
            priority[4] = Maze.NORTHEAST;
            priority[5] = Maze.NORTHWEST;
            break;
          case Maze.EAST:
            priority[0] = Maze.SOUTHEAST;
            priority[1] = Maze.EAST;
            priority[2] = Maze.SOUTHWEST;
            priority[3] = Maze.NORTHEAST;
            priority[4] = Maze.NORTHWEST;
            priority[5] = Maze.WEST;
            break;
          case Maze.WEST:
            priority[0] = Maze.NORTHWEST;
            priority[1] = Maze.WEST;
            priority[2] = Maze.NORTHEAST;
            priority[3] = Maze.SOUTHWEST;
            priority[4] = Maze.SOUTHEAST;
            priority[5] = Maze.EAST;
            break;
        }
      }

      for (int i = 0; i < priority.length; i++) {
        if (curr == maze.exit) {
          solved = true;
          explored++;
          break;
        } else if (!curr.wall[priority[i]].present) {
          facing = priority[i];
          maze.drawFtPrt(curr.neigh[priority[i]]);
          if (!visited[curr.r][curr.c]) {
            visited[curr.r][curr.c] = true;
            explored++;
          }
          curr = curr.neigh[priority[i]];
          break;
        }
      }
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
