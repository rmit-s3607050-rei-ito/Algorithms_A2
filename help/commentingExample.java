import java.util.*;
import java.io.*;

/**
 * Compute depth first search traversal on a graph.
 * 
 *  @author jefcha
 */
public class DFS 
{
    /** Name of the class. */
    private static final String progName = "DFS";       

    
    /** 
     * Depth first search traversal of input graph g from seed vertex s.
     * 
     * ******************************************************************************************
     * 
     * ALGORITHM DFS ( G )
     * Perform a Depth first search traversal of a graph.
     * Input: Graph G = 〈 V , E 〉 , seed/starting vertex s .
     * OUTPUT : Graph G with its vertices marked with consecutive integers in the order they were visited/processed.
     * 
     * 1: traversalOrder = {}
     * // mark all vertices unvisited
     * 2: for i = 0 to v do
     * 3:    marked[ i ] = 0
     * 4: end for
     * // initiate DFS from seed vertex 
     * 5: DFSR (s, traversalOrder)
     * 
     * ******************************************************************************************
     * 
     * @param g Input graph.
     * @param s Seed vertex to start traversal from.
     * 
     * @returns ArrayList of vertices, stored in the order they were visited in 
     *      DFS traversal.
     */
    public static ArrayList<Integer> traverse(Graph g, int s) {
	// array of vertices that have been visited
        boolean[] marked = new boolean[g.vertNum()];
	// current list of vertices in dfs traversal so far; initially empty
    	ArrayList<Integer> traversalOrder = new ArrayList<Integer>();
    	
    	// label all vertices as "not not visited" or "unmarked"
    	for (int i = 0; i < marked.length; i++) {
    		marked[i] = false;
    	}        
                
    	// start dfs from source
    	dfs(g, s, marked, traversalOrder);
        
        // return the list of vertices traversed for DFS
        return traversalOrder;
    } // end of traverse()

    
    /**
     * Recursive DFS method, that implements DFS visitation semantics.
     * 
     * ******************************************************************************************
     * 
     * ALGORITHM DFSR(v, traversalOrder)
     * Recursively visit all connected vertices.
     * INPUT: A seed/starting vertex v, array of vertices visited, traversal order of vertices visited so far
     * OUTPUT : None
     * 
     * 1: traversalOrder.add(v)
     * 2: marked[v] = true
     * 3: for w ∈ neighbour(V) do
     * 4:     if not marked[ w ] then
     * 5:         DFSR (w, traversalOrder)
     * 6:     end if
     * 7: end for
     * 
     * ******************************************************************************************
     * 
     * @param g Input graph.
     * @param v Current vertex visited.
     * @param marked Array of booleans, that indicate whether a vertex has been
     *      visited yet.
     * @param traversalOrder ArrayList of vertices, stored in the order they were visited so far in the
     *      DFS traversal.
     */
    protected static void dfs(Graph g, int v, boolean[] marked, ArrayList<Integer> traversalOrder) {
    	// mark the current vertex as visited
        marked[v] = true;
        // add vertex to traversal order
        traversalOrder.add(v);

        // for each neighbour of vertex v, perform DFS from that neighbour if it hasn't been visited yet
        for (int w : g.neighbours(v)) {
            if (!marked[w]) {
                dfs(g, w, marked, traversalOrder);
            }
        }    	
    } // end of dfs()

    
} // end of class DFS
