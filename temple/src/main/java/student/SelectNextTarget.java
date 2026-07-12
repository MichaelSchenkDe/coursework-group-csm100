package student;

import game.Node;
import game.Edge;

import java.util.Map;

/**
 * -----------------
 * SelectNextTarget
 * -----------------
 * 
 * This select the best target node from current node's 
 * neighbor to move durting the escape phase.
 * decision is based on various fields of TargetSearchResult.
 * 
 * Every neighbor node is checked for safe exit first,
 * then it is send for beanch evaluation.
 * Later the ebst TargetSearchResult is selected and
 * its node is returned. 
 */
public final class SelectNextTarget{

  /**
   * Shortest distance from every node to exit.
   */
  private final Map<Node,Integer>exitDistanceMap;

  /**
   * Evaluation of 1 neighbor branch 
   */
  private final TargetSearch targetSearch;

  /**
   * A SelectNextTarget
   * @param exitDistanceMap
   *        shortest distance from each node to exit 
   */
  public SelectNextTarget(Map<Node,Integer>exitDistanceMap){
    this.exitDistanceMap=exitDistanceMap;
    this.targetSearch=new TargetSearch(exitDistanceMap);
  }

  /**
   * Select the best neighbor node to move to
   * @param currNode
   *        explorer's current node 
   * @param remainingTime
   *        remaining escape time from this node 
   * @return 
   *        best neighbor node or null if no safe neighbor
   */
  public Node selectBestTarget(Node currNode ,
          int remainingTime){
    
    TargetSearchResult bestResult=null;

    /*
     * evaluate every safe branch
     */
    for(Node neighbor:currNode.getNeighbours()){
      /*
       * edge connecting the current node to this neighbor 
       */
      Edge edge=currNode.getEdge(neighbor);

      /*
       * simulates the behavior of move,
       * it is not a actual move  
       * remainingTime decreases by edge weight 
       */

      int travelCost=edge.length();

      int newRemainingTime=remainingTime-travelCost;
      /*
       * can this neighbor safely reach exit 
       */
      Integer exitDistance=exitDistanceMap.get(neighbor);

      if(exitDistance==null || newRemainingTime<exitDistance){
        continue;//simply discard the neighbor 
      }
      /*
       * Now evaluate this safe branch
       */
      TargetSearchResult result=targetSearch.evaluateTarget(
        neighbor,newRemainingTime,travelCost);
      
      /*
       * Keep the ebst result
       */
      if(bestResult==null ||
          result.isBetterThan(bestResult)){
        bestResult=result;    
      }
    }
    /*
     * if no safe branch exist 
     */
    if(bestResult==null){
      return null;
    }
    /*
     * return the neighbor node 
     * representing the best branch 
     */
    return bestResult.getTargetNode();
  }
}
