package student;

import game.Node;

/**
 * A utility calss responsibel for creating a 
 * shortest path usign previosu node information 
 * produced by dijkstra result.
 * 
 * ----------------------------
 * Why this calss is needed?
 * ----------------------------
 * 
 * The previous map (prevMap) form dijkstra run 
 * tell us about the parent info , measn how did we
 * reach this node using previous node.
 * 
 * We are allowed to move to an adjecent node at a time, 
 * Therefore, aafter selecting the target we need to 
 * know the first move that begins the path. 
 */
public final class NextStep{

  private NextStep(){
    //prevent object creation
  }
  
  /**
   * find next step toward target
   * note: moveTo() allowed only single move to adjacent nodes
   * 
   * @param current
   *        current position of a player
   * @param target 
   *        Destination selected by the escape strategy 
   * @param dijkstraResult
   *        Result produced by dijkstra algorithm
   * 
   * @return
   *        The adjecent node that should be visited next 
   */
  private Node nextStep(Node current,Node target, DijkstraResult dijkstraResult){
    /**
     * If we already standing on the target
     * no mopve is required 
     */
    if(current.equals(target)){
      return current;
    }

    /**
     * Start tracing from the destination node 
     * (traceNode is on target)
     */
    Node traceNode=target;// the node currently being traced 

    /**
     * Walk backwards through the shortest path tree
     * until the parent of the currently traced node is the 
     * player's current location.
     * This will give us next step toward exit 
     */
    while(dijkstraResult.getPrev(traceNode)!=null && 
          ! dijkstraResult.getPrev(traceNode).equals(current)){
      traceNode=dijkstraResult.getPrev(traceNode);
    }

    /**
     * traceNode is now the first node after the current node in 
     * the shortest path to the target 
     */
    return traceNode;
  }
}