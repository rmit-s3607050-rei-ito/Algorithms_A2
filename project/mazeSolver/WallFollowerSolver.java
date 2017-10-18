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
    int colSize = maze.sizeC; //get the correct size depending on maze type
    if (maze.type == Maze.HEX)
      colSize += (maze.sizeR+1) / 2;
    boolean[][] visited = new boolean[maze.sizeR][colSize]; //visited array
    Cell curr = maze.entrance; //set the current to the entrance cell
    int facing = getStartFace(curr); //pick a random start direction to face
    maze.drawFtPrt(maze.entrance); //draw dot at start
    visited[curr.r][curr.c] = true; //set visited to true
    explored++; //increment explored nodes

    while (!solved) { //solver loop until solved
      int[] priority; //priority direction array
      if (maze.type == Maze.NORMAL || maze.type == Maze.TUNNEL) {
        priority = new int[4]; //normal maze priority
        switch (facing) { //depending where it's facing change priorities
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
      } else { //NOTE: hex maze solver doesn't work
        priority = new int[6]; //hex maze priority
        switch (facing) { //depending where it's facing change priorities
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

      for (int i = 0; i < priority.length; i++) { //loop through priority directions
        if (curr == maze.exit) { //if reached end
          solved = true; //solved is true
          explored++; //add last explored cell
          break; //end found, break from loop
        } else if (!curr.wall[priority[i]].present) {
          facing = priority[i]; //change facing direction
          maze.drawFtPrt(curr.neigh[priority[i]]); //draw dot on neighbor
          if (!visited[curr.r][curr.c]) { //visited check
            visited[curr.r][curr.c] = true; //set visited location to true
            explored++; //increment explored count
          }
          curr = curr.neigh[priority[i]]; //change curr to neighbor
          break; //cell moved, break from loop
        }
      }
    }
  } // end of solveMaze()

  // random start direction
  private int getStartFace(Cell start) {
    Random rand = new Random();
    List<Integer> randDir = new ArrayList<Integer>(); //list of possible directions
    for (int i = 0; i < start.wall.length; i++) { //loop through all cell walls
      if (start.wall[i] != null) { //check for null walls
        if (!start.wall[i].present && !start.wall[i].drawn) //if no wall exists
          randDir.add(i); //add to direction list
      }
    }

    return randDir.get(rand.nextInt(randDir.size())); //return a random direction from list
  }

  @Override
  public boolean isSolved() {
    return solved; //return solved boolean
  } // end if isSolved()


  @Override
  public int cellsExplored() {
    return explored; //return explored count
  } // end of cellsExplored()

} // end of class WallFollowerSolver
