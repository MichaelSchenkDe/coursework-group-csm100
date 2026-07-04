package student;

import game.Node;
import game.Edge;

import java.util.HashSet;
import java.util.Set;
import java.util.Map;

/**
 * ---------------
 * TargetSearch
 * ---------------
 * 
 * calculate the various specific result such as totalGold 
 * travelCost etc associated with the neighbor of current 
 * node.
 * During the escape phase each neighbor node of current 
 * node is consider as potential target node.
 * Starting from this target a depth-limited(max depth=5) 
 * DFS is performed.
 * 
 * Therefore we can stop anywhere <=5 based on safety.
 * 
 * This search estimates:
 * totalGold that can be safely collected from this branch.
 * travelCost required.
 * Gold efficiency (totalGold/travelCost)
 * whether we can escape safely 
 * 
 * Branch that cannot be reached safely can be 
 * directly discarded sometime.
 * 
 * This class does not decide which neighbor is best
 * , it simply eveluates a neighbor.
 */

public final class TargetSearch{
  /**
   * Maximum number of edges explored from the 
   * start node, we are considerign the edges 
   * beasue it is weighted graph adn time is 
   * directly related to edge weight during escape.
   */
  private static final int MAX_DEPTH=5;

  /**
   * Precompute the shortest distance for every node
   * to exit.
   * Will use this information in checking whether we
   * can safely exit from a node.
   */
  private final Map<Node,Integer>exitDistanceMap;

  /**
   * A target search 
   * @param exitDistanceMap
   *        Shortest distance from every node to exit.
   */
  public TargetSearch(Map<Node,Integer>exitDistanceMap){
    this.exitDistanceMap=exitDistanceMap;
  }

  /**
   * Starts the future bracnh search for one neighbor
   * node(assumed target node of current node) of current node.
   * the explorer does not acutally moves to this target neighbor 
   * node , it calculate parameter which will then help in deciding 
   * whether it is a good decision to move to this node.
   * 
   * Each call evaluates only one branch adn return the TargetSearchResult.
   * 
   * @param targetNode
   *        neighbor currently being evaluated.
   * @param remainingTime
   *        logically remainig time till this point,
   *        No actual movement has happened.
   * @param travelCost
   *        logical travel cost
   * 
   * @return best TargetSearchResult found in this branch
   */
  public TargetSearchResult evaluateTarget(
    Node targetNode, int remainingTime, int travelCost){
    /**
     * Get account of visited nodes, this wil prevent
     * visiting same node again.
     */
    Set<Node>visited=new HashSet<>();

    /**
     * the target node is now the part of current 
     * dfs path so add it.
     */
    visited.add(targetNode);

    /**
     * Starting the recursive depth first search for 
     * this branch.
     * 
     * prameters order:
     * 
     * currNode 
     *        current node being explored. initally we are 
     *        on target node, neighbor node for which we started 
     *        the recursive search.
     *         
     * targetNode
     *        result belong to this node, this is branch start point.
     *        original neighbor which is being evaluated , 
     *        this will never change during search.
     *  visited
     *        already visited Nodes in search
     * currDepth
     *       search begin at depth 1 intially.
     * remainingTime
     *        logical reamining time of the game, it will decrease 
     *        inside the simulation as we will decifde the thigns such as 
     *        what if we make a move , this weight will get consume.
     * travelCost
     *        total edges weight , traveled from currentnode 
     *        to this node
     * totalGold
     *        gold collected so far in this branch.
     *        will collect with each dfs .
     */
    return evaluateBranch(targetNode,targetNode,visited,
            1,remainingTime ,travelCost, 0);
  }

  private TargetSearchResult evaluateBranch(Node currNode,
    Node targetNode,Set<Node>visited, int currDepth,
    int remainingTime,int travelCost,int totalGold){

    /**
     * calculate the exit distance from the cuirrnt node to exit.
     * this we already have in precomputed dijkstra. just extract.
     */
    Integer exitDistance=exitDistanceMap.get(currNode);

    /**
     * check whether we can safely exit from curent node
     * or not 
     */
    boolean safe=(exitDistance!=null && remainingTime>=exitDistance);

    /**
     * If the current node cannot reach the exit safely
     * stop exploring this branch immediately.
     */
    if(!safe){
      return new TargetSearchResult(totalGold,travelCost,false,
                      currDepth,targetNode,false);
    }


    /**
     * collect the gold available at this current node 
     * if this is safe every recursive call will perform this step
     * this make gold collection to a complete branch
     * effective.
     */
    totalGold+=currNode.getTile().getGold();
    
    /**
     * Initially assume further dearching this target is
     * not allowed
     * later we will get to know if it is safe to evaluate
     * further deep this branch for neighbors node if any.
     */
    boolean canContinueSearch=false;

    /**
     * calculate if we can continue our search in this branch
     * any unvisited node within current depth limit 
     */
    for(Node neighbor: currNode.getNeighbours()){
      if(!visited.contains(neighbor)){
        canContinueSearch=true;
        break;
      }
    }
    /**
     * the current node is a valid stoppage point , so
     * evaluate TargetSearchResult for this node , which will 
     * represent the information available on this node 
     * to make decisions.
     * Now we have every property of current node 
     */
    TargetSearchResult targetResult=new TargetSearchResult
                (totalGold,travelCost,true,currDepth,
                targetNode,canContinueSearch);
    


    /**
     * stop once max depth has reached and return
     */
    if(currDepth>=MAX_DEPTH){
      return targetResult;
    }

    /**
     * explore every neighbor node of current node 
     */
    for(Node neighbor : currNode.getNeighbours()){
      /**
       * ignore already visited nodes
       */
      if(visited.contains(neighbor)){
        continue;
      }
      /**
       * since an unvisited neighbor exist , this branch can 
       * continue search , so set it true;
       */
      canContinueSearch=true;

      /**
       * edge connecting the current node to this neighbor 
       */
      Edge edge=currNode.getEdge(neighbor);

      /**
       * simulates the behavior of move,
       * it is not a actual move 
       * means
       * travelCost increases by edgeWeight 
       * remainingTime decreases by edge weight 
       */

      int newTravelCost=travelCost+edge.length();

      int newRemainingTime=remainingTime-edge.length();

      /**
       * mark this neighbor as visited.
       */
      visited.add(neighbor);

      /**
       * recursive evaluation of this branch 
       * with new values , depth.
       */
      TargetSearchResult neighborResult=evaluateBranch(neighbor,
          targetNode,visited, currDepth+1,newRemainingTime ,
          newTravelCost, totalGold);

      /**
       * now we backtrae, this neighbor might be explored
       * from other branch as well so remove it from visited
       */
      visited.remove(neighbor);

      /**
       * compare this result with the earlier known result , 
       * a bracnh at last will return the best result.
       */
      if(neighborResult.isBetterThan(targetResult)){
        targetResult=neighborResult;
      }
    }
    return targetResult;
  }
}