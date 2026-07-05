package student;

import game.Node;
import java.util.Collections;
import java.util.Map;

/**
 * --------------
 * DijkstraResult
 * --------------
 * Stores the complete result after executing the dijkstra 
 * shortest path algorithm.
 * 
 * -----------------------
 * Why we need this class?
 * -----------------------
 * 
 * Dijkstra compute the shortest path from a source node to 
 * every rechable node.
 * Knowing this distance is not enough,We need parent of each 
 * node to actually construct a path to reach the node.
 * 
 * Therefore we need to remember the parent of each node in 
 * the shortest path tree 
 * 
 * ## This calss help us in returning one object(complete information
 * in one onject form)
 * 
 * To prevent accidental modifications this class is immutable 
 */
public final class DijkstraResult{

  /**
   * Shortest distance form source node to every rechable node 
   * Key:
   *     destination node
   * Value:
   *      shortest travel distance from the source 
   */
  private final Map<Node, Integer>distanceMap;

  /**
   * parent nodes (will need this in creating the path)
   */
  private final Map<Node, Node>prevMap;

  /**
   * A new immutable Dijkstra result
   * 
   * @param distancemap 
   *        Shortest distance form source node to every rechable node
   * @param prevMap 
   *        parent nodes (will need this in creating the path)
   */
  DijkstraResult(Map<Node, Integer>distanceMap,Map<Node, Node>prevMap){
    this.distanceMap=Collections.unmodifiableMap(distanceMap);
    this.prevMap=Collections.unmodifiableMap(prevMap);
  }

  /**
   * Returns the complete shortest distance map
   * @return Immutable distance map  
   */
  public Map<Node, Integer> getDistanceMap(){
    return distanceMap;
  }

  /**
   * Returns parent nodes (will need this in creating the path)
   * @return Immutable previous nodes map 
   */
  public Map<Node, Node> getPrevMap(){
    return prevMap;
  }

  /**
   * Returns the shortest distance to a specific node
   * @param node destination node 
   * @return Shortest distance 
   */
  public Integer getDistance(Node node){
    return distanceMap.get(node);
  }

  /**
   * Returns the previous node on the shortest path
   * @param node destination node 
   * @return previous node  
   */
  public Node getPrev(Node node){
    return prevMap.get(node);
  }
}