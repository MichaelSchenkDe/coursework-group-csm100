package student;

//import packages 
import game.Node;
import game.Edge;

import java.util.*;

/**
 * Every edge has a weight which represents the time
 * required to travel between two adjacent nodes 
 * 
 * We need shortest path of each node to exit to
 * calculate safe escape.
 * We are using Dijkstra because single source and
 * non negative weight
 * 
 * ----------------------------
 * What does this class returns
 * ----------------------------
 * 
 * A DijkstraResult object containing
 * 1. distanceMap: shortest distance from source node to every 
 *                reachable node
 * 2. prevMap: parent nodes (will need this in creating the path)
 */
public final class Dijkstra{
  /**
   * Computes the shortest path from one source node 
   * to every reachable node 
   *
   * @param source the node to measure all shortest distances from
   * @return the shortest distances and parent links to every reachable node
   */
  public DijkstraResult computePath(Node source){
    /*
     * Stores the shortest known distance from source
     * node to every reachable node 
     */
    Map<Node, Integer>distanceMap=new HashMap<>();

    /*
     * Stores the parent nodes (will need this in creating
     * the path)
     */
    Map<Node, Node>prevMap=new HashMap<>();

    /*
     * Use prioity queue, because we ened small distance first
     * How to compare this node with distance, obviously we need a comparator
     * nodeDistance has nodeDistance.distance, we can compare that 
     */
    PriorityQueue<NodeDistance> priorityQueue=
                        new PriorityQueue<>(Comparator.comparingInt(
                        nodeDistance->nodeDistance.getDistance()));
    
    /*
     * distance from source to itself is always zero
     */
    distanceMap.put(source,0);

    /*
     * Insert the source node into the priority queue
     */
    priorityQueue.add(new NodeDistance(source,0));

    /*
     * Continue until every reachable node has been processed
     */
    while(!priorityQueue.isEmpty()){
      /*
       * Remove the node with the smallest known
       * distance 
       */
      NodeDistance nodeDistance=priorityQueue.poll();
  
      Node currNode=nodeDistance.getNode();
      
      /*
       * Ignore the old version of node
       */
      if(nodeDistance.getDistance()>distanceMap.get(currNode)){
        continue;
      }

      // we will have new version once we will check all the edge things
      
      /*
       * Visit every neighbor node 
       */
      for(Node neighbor:currNode.getNeighbours()){
        
        /*
         * Get the edge connecting two nodes
         */ 
        Edge edge=currNode.getEdge(neighbor);

        /*
         * Distance if we travel through the current node 
         */
        int newDistance=distanceMap.get(currNode)+edge.length();

        /*
         * Have we discovered the shortest route 
         */
        Integer oldDistance=distanceMap.get(neighbor);

        // we got new short path
        // check if thi distance path already there
        // or this distance is less than already exisiting distance

        if( oldDistance==null || 
          newDistance < oldDistance){
          
          /*
           * update the shortest distance
           * add that neighbor->distance
           */
          distanceMap.put(neighbor,newDistance);

          /*
           * remember the parent used to reach this node 
           * add prev Node for path creation 
           */
          prevMap.put(neighbor,currNode);

          /*
           * Insert the new updated distance 
           * Older entries will later be ignored automatically
           * add this in priority queue
           */
          priorityQueue.add(new NodeDistance(neighbor,newDistance));
        }
      } 
    }
    return new DijkstraResult(distanceMap,prevMap);
  }
}