package student;

import game.EscapeState;
import game.ExplorationState;

//import packages
import game.Node;
import game.Edge;
import java.util.*;

import game.NodeStatus;

public class Explorer {

  /**
   * Explore the cavern, trying to find the orb in as few steps as possible.
   * Once you find the orb, you must return from the function in order to pick
   * it up. If you continue to move after finding the orb rather
   * than returning, it will not count.
   * If you return from this function while not standing on top of the orb,
   * it will count as a failure.
   * <p>
   * There is no limit to how many steps you can take, but you will receive
   * a score bonus multiplier for finding the orb in fewer steps.
   * <p>
   * At every step, you only know your current tile's ID and the ID of all
   * open neighbor tiles, as well as the distance to the orb at each of these tiles
   * (ignoring walls and obstacles).
   * <p>
   * To get information about the current state, use functions
   * getCurrentLocation(),
   * getNeighbours(), and
   * getDistanceToTarget()
   * in ExplorationState.
   * You know you are standing on the orb when getDistanceToTarget() is 0.
   * <p>
   * Use function moveTo(long id) in ExplorationState to move to a neighboring
   * tile by its ID. Doing this will change state to reflect your new position.
   * <p>
   * A suggested first implementation that will always find the orb, but likely won't
   * receive a large bonus multiplier, is a depth-first search.
   *
   * @param state the information available at the current state
   */
  public void explore(ExplorationState state) {
    //TODO : Explore the cavern and find the orb
    Set<Long>visited=new HashSet<>();
    dfs(state,state.getCurrentLocation(),visited);
  }

  private boolean  dfs(ExplorationState state, long curr, Set<Long>visited){
    visited.add(curr);
    if(state.getDistanceToTarget()==0){
      return true ;
    }

    for(NodeStatus neighbor : state.getNeighbours()){
      long next=neighbor.nodeID();

      if (!visited.contains(next)){
        
        state.moveTo(next);
        if(dfs(state,next,visited)){
          return true;
        }
        state.moveTo(curr);
      }
    }
    return false;
  }

  /**
   * Escape from the cavern before the ceiling collapses, trying to collect as much
   * gold as possible along the way. Your solution must ALWAYS escape before time runs
   * out, and this should be prioritized above collecting gold.
   * <p>
   * You now have access to the entire underlying graph, which can be accessed through EscapeState.
   * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
   * will return a collection of all nodes on the graph.
   * <p>
   * Note that time is measured entirely in the number of steps taken, and for each step
   * the time remaining is decremented by the weight of the edge taken. You can use
   * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
   * on your current tile (this will fail if no such gold exists), and moveTo() to move
   * to a destination node adjacent to your current node.
   * <p>
   * You must return from this function while standing at the exit. Failing to do so before time
   * runs out or returning from the wrong location will be considered a failed run.
   * <p>
   * You will always have enough time to escape using the shortest path from the starting
   * position to the exit, although this will not collect much gold.
   *
   * @param state the information available at the current state
   */
  public void escape(EscapeState state) {
    //TODO: Escape from the cavern before time runs out
    /*
     * goal:
     * 1. never ran out of time
     * 2. exit with safe gold
     * 
     * Algorithm:
     * 
     * Dijkstra because single source and non negative weight
     * this will give shortest path from source to all vertices 
     */

    //get exit
    Node exit= state.getExit();

    //calculate shortest distance to exit for each node

    DijkstraResult exitDistances=dijkstra(exit);

    while(!state.getCurrentNode().equals(exit)){

      Node curr=state.getCurrentNode();

      //pick gold
      if(curr.getTile().getGold()>0){
        state.pickUpGold();
      }

      // new target, exit or take gold 
      // need state , current node , exit node(becasue we ened to check safe return to exit)
      // , distance of each node to exit (pre-computed)

      Node target=selectBestTargetRatio(state,curr,exit,exitDistances);
      //Node target=selectBestTargetDetour(state,curr,exit,exitDistances);

      //decide the next node to move to , generally next step is neighbor we will move 

      Node next=nextStep(curr,target);// which is the next step on the way to target , give that node 

      state.moveTo(next);// move to taht node, single move 
    }
    //collect gold at exit if any
    if(state.getCurrentNode().getTile().getGold()>0){
      state.pickUpGold();
    }
  }

  /**
   * store distance from curr node
   *              and
   * path to that node (using prev node )
   */
  private static class DijkstraResult{
    Map<Node, Integer>distance;
    Map<Node, Node>prev;// will need this in creating the path 

    DijkstraResult(Map<Node, Integer>distance,Map<Node, Node>prev){
      this.distance=distance;
      this.prev=prev;
    }
  }

  /**
   * for ease of use , create same for 
   * Node and distance too
   */

  private static class NodeDistance{
    Node node;
    int distance;

    NodeDistance(Node node,int distance){
      this.node=node;
      this.distance=distance;
    }
  }

  /**
   * Write Dijkstra
   */

  private DijkstraResult dijkstra(Node start){
    Map<Node, Integer>distance= new HashMap<>();//store distance from curr node
    
    Map<Node, Node>prev= new HashMap<>() ;// will help in path to that node (using prev node )

    //use prioity queue, bcasue we ened small distance first
    // how to compare this node with distance, obviously we need a comparator
    // x has x.distance, we can compare that 
    PriorityQueue<NodeDistance> pq=new PriorityQueue<>(Comparator.comparingInt(x->x.distance));

    distance.put(start,0);

    pq.add(new NodeDistance(start,0));

    while(!pq.isEmpty()){
      NodeDistance curr=pq.poll();

      Node node=curr.node;
      
      //old version of node
      if(curr.distance>distance.get(node)){
        continue;
      }

      // we will have new version once we will cehck all the edge things

      for(Node neighbor:node.getNeighbours()){
        
        //get the edsge grom node ->neighbor 
        Edge edge=node.getEdge(neighbor);

        int newDistnce=distance.get(node)+edge.length();//NodeDistance == Node:->distance

        // we got new short path
        // check if thi distance path already there
        // or this distance is less than already exisiting distance

        if(! distance.containsKey(neighbor) || 
          newDistnce<distance.get(neighbor)){
          
          //add that neighbor->distance
          distance.put(neighbor,newDistnce);

          // add prev for path creation 
          prev.put(neighbor,node);

          // add this in priority queue
          pq.add(new NodeDistance(neighbor,newDistnce));
        }
      }
    }
    return new DijkstraResult(distance,prev);
  }

  /**
   * select the best gold target, 
   * gold(try max possible) with safe exit 
   * 
   */
  private Node selectBestTargetRatio( EscapeState state, Node curr, Node exit, 
    DijkstraResult exitDistances){
    
    // now we need distance from current node to every node 
    DijkstraResult currDistances=dijkstra(curr);

    //check time remaining
    int timeLeft=state.getTimeRemaining();

    // best node is anyway exit node
    //go directly to exit 
    Node bestNode=exit;

    //assume best ratio 
    //will update it 
    double bestRatio=0;

    // if direct exit form here
    // need distance form currDistances to exit
    int directExit=currDistances.distance.get(exit);

    //check every node in the map 
    for(Node node: state.getVertices()){
      //gold on the tile?
      int gold=node.getTile().getGold();

      // if no gold or already collected
      if(gold==0){
        continue;
      }

      // how much cost to rreach this gold 
      //we already calculated these distance so only get operation needed
      Integer toGold=currDistances.distance.get(node);

      // now distance from this gold pos to exit 
      //we already calculated these distance so only get operation needed
      Integer toExit=exitDistances.distance.get(node);

      // if we cant make to exit ignore this node
      // this is important step 
      // add null too in case disconnected but ther are not any but still safe 
      if(toExit ==null || toGold==null || (toExit+toGold >timeLeft)){
        continue;
      }

      double ratio=(double)gold/(toExit+toGold);

      //keep the ebst option 
      if(ratio>bestRatio){
        bestRatio=ratio;
        bestNode=node;
      }
      
    }
    //if no safe gold exist
    //return exit
    return bestNode;
  }

  /**
   * select the best gold target, 
   * gold(try max possible) with safe exit 
   * 
   */
  /*private Node selectBestTargetDetour( EscapeState state, Node curr, Node exit, 
    DijkstraResult exitDistances){
    
    // now we need distance from current node to every node 
    DijkstraResult currDistances=dijkstra(curr);

    //check time remaining
    int timeLeft=state.getTimeRemaining();

    // best node is anyway exit node
    //go directly to exit 
    Node bestNode=exit;

    //smallest detour found
    //small detour==safe  
    int bestDetour=Integer.MAX_VALUE;

    // if direct exit form here
    // need distance form currDistances to exit
    int directExit=currDistances.distance.get(exit);

    //check every node in the map 
    for(Node node: state.getVertices()){
      //gold on the tile?
      int gold=node.getTile().getGold();

      // if no gold or already collected
      if(gold<=0){
        continue;
      }

      // how much cost to rreach this gold 
      //we already calculated these distance so only get operation needed
      Integer toGold=currDistances.distance.get(node);

      // now distance from this gold pos to exit 
      //we already calculated these distance so only get operation needed
      Integer toExit=exitDistances.distance.get(node);

      // if we cant make to exit ignore this node
      // this is important step 
      // add null too in case disconnected but ther are not any but still safe 
      if(toExit ==null || toGold==null || (toExit+toGold >timeLeft)){
        continue;
      }

      // extra time collecting this gold on detour
      int detour=(toExit+toGold) - directExit;

      //select the gold that gives the best detour  which is smallest detour

      if(detour < bestDetour){
        bestDetour=detour;
        bestNode=node;
      }
    }
    //if no safe gold exist
    //return exit
    return bestNode;
  }*/

  /**
   * find next step toward target
   * note: moveTo() allowed only single move to adjacent nodes
   */

  private Node nextStep(Node start,Node target){

    DijkstraResult result=dijkstra(start);

    //go backward follow parents
    //assume target is current node
    //create a abckward path to start
    //follow the parents

    Node current=target;

    while(result.prev.get(current)!=null && ! result.prev.get(current).equals(start)){
      current=result.prev.get(current);
    }

    return current;
  }
}
