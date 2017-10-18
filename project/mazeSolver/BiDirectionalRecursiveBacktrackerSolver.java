package mazeSolver;

import maze.Maze;

/**
 * Implements the BiDirectional recursive backtracking maze solving algorithm.
 */
public class BiDirectionalRecursiveBacktrackerSolver implements MazeSolver {
  boolean solved = false;


  @Override
  public void solveMaze(Maze maze) {
    // TODO Auto-generated method stub

  } // end of solveMaze()


  @Override
  public boolean isSolved() {
    return solved;
  } // end if isSolved()


  @Override
  public int cellsExplored() {
    // TODO Auto-generated method stub
    return 0;
  } // end of cellsExplored()

} // end of class BiDirectionalRecursiveBackTrackerSolver
