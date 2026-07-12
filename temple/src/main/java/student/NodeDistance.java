package student;

// import package
import game.Node;

/**
 * Helper class NodeDistance used by Dijkstra
 * 
 * ------------------------
 * Why is this class needed?
 * ------------------------
 * 
 * Java's Priority Queue stores objects.
 * During dijkstra algorithm we repeatedly needs to ask:
 * 
 * "which node currently has the smallest known distance
 * from the source node?"
 * 
 * Because a Node object only stores information about the 
 * graph itself.
 * It does not store the temporary distancebeing calculated by
 * Dijkstra.
 * Therefore we created this class which contain
 * 
 * The graph Node 
 * Current shortest known distance 
 * because it needs the node with smallest distance from source
 */
public final class NodeDistance{

  /**
   * Graph node represented by this object 
   */
  private final Node node;

  /**
   * Current shortes known distance from source 
   * This value may later became outdated if Dijkstra
   * discover an even shorter path. In this case a new 
   * NodeDistance object will be inserted into 
   * the PriorityQueue with the improved distance.
   */
  private final int distance; 

  /**
   * A NodeDistance object
   * 
   * @param node 
   *       Graph node being stored
   * @param distance 
   *       Current shortest known distance from source node
   */
  NodeDistance(Node node,int distance){
    this.node=node;
    this.distance=distance;
  }

  /**
   * Returns the graph node 
   * @return stored graph node 
   */
  public Node getNode(){
    return node;
  }

  /**
   * Returns the current shortest known distance from source
   * @return the current shortest known distance from source
   */
  public int getDistance(){
    return distance;
  }
}