package student;

import game.Node;
/**
 * ------------------
 * TargetSearchResult
 * ------------------
 * 
 * Stores the result returned after searching one possible 
 * future branch of current node during the escape phase.
 * 
 * -----------------------
 * Why we need this class?
 * -----------------------
 * 
 * During the escape phase for every neighbor we perform a 
 * limited depth future safe exit search to estimate:
 * How much gold can be collected from this branch.
 * How much travel time is required.
 * Can we still reach to the exit safely from here.
 * Where is should go next (next target node based on results.)
 * 
 * We will group this information in a single object
 * for ease of use.
 * 
 * This helps in comparing different neighbor branches easier.
 * 
 * TargetSearchResult is immutable, once created , its value
 * never change.
 */

public final class TargetSearchResult{
  /**
   * Total amount of gold that can be collected from this branch
   * at certain fixed depth (<=5)
   */
  private final int totalGold;

  /**
   * Total travel cost required to collect the gold
   * travel cost is measured using edge weight.
   */
  private final int travelCost;

  /**
   * original neighbor node for next move,
   * (assuming each neighbor as target, we will decide
   * where to move based on results of each neighbor)
   * after careful consideration of all above
   * results.
   */
  private final Node targetNode;

  /**
   * dfs depth reached while exploring 
   * this branch 
   */
  private final int searchDepth;

  /**
   * Whether stoppage point of this branch has 
   * unvisited neighbor 
   * 
   * To indicate whether the branch can continue beyond the
   * current search limit 
   * 
   * true, if branch can continue beyond the
   * current search limit 
   * false, otherwise.
   */
  private final boolean canContinueSearch;
  
  /**
   * A immutable TargetSearchResult
   * 
   * @param totalGold
   *        Total safe collectable gold from a branch 
   * @param travelCost
   *        Time required to collect the gold 
   * @param targetNode 
   *        target node during the search for 
   *        selection of next move, original neighbor 
   * @param searchDepth
   *        Maximum dfs depth reached while exploring
   *        this branch 
   * @param canContinueSearch
   *        To indicate whether the branch can continue beyond the
   *        current search limit       
   */
  public TargetSearchResult(int totalGold,int travelCost,
      int searchDepth,Node targetNode,boolean canContinueSearch){
    this.totalGold=totalGold;
    this.travelCost=travelCost;
    this.canContinueSearch=canContinueSearch;
    this.searchDepth=searchDepth;
    this.targetNode=targetNode;
  }

  /**
   * Returns the total gold collected for the branch
   * @return total collectable gold
   */
  public int getTotalGold(){
    return totalGold;
  }

  /**
   * Returns the total travel cost for the branch
   * @return travel cost 
   */
  public int getTravelCost(){
    return travelCost;
  }

  /**
   * Returns the gold efficiency (totalGold/travelCost)
   * Higher the ratio means more gold collected 
   * per unit time.
   * But overall selection is based on combine Result
   * of this class. It is calculate wherever needed 
   * because it is a derived entity
   * @return gold ratio (double)totalGold/travelCost 
   *          if travelCost not zero
   */
  public double getGoldRatio(){
    //a safer approach
    if(travelCost==0){
      return totalGold;
    }
    return (double)totalGold/travelCost;
  }

  /**
   * Returns the candidate target node represented by 
   * (this) search result.
   * @return candidate target node
   */
  public Node getTargetNode(){
    return targetNode;
  }

  /**
   * Returned the maximum search depth reached
   * @return searchDepth
   */
  public int getSearchDepth(){
    return searchDepth;
  }
  
  /**
   * returns whether the branch can continue beyond the
   * current search limit 
   * @return true, if can continue beyond the
   *         current search limit 
   */
  public boolean canContinueSearch(){
    return canContinueSearch;
  }

  /**
   * It will be easy if we can compare two search result 
   * objects according to our need 
   * result1.isBetterThan(result2), will provide the way.
   * 
   * 1.Safe branch is given high priority 
   * 2.Higher gold ratio wins if both branches are safe
   * 3.If equal ratio, smaller travelCost wins.
   * 4.If all aspects are equal prefer branch with 
   * more depth 
   * 
   * @param  
   * other TargetSearchResult to compare against 
   * @return
   * true if (this) result is better than (other) result
   */

  public boolean isBetterThan(TargetSearchResult other){
  
    /*
     * . Higher gold ratio wins 
     */
    double thisRatio=this.getGoldRatio();
    double otherRatio=other.getGoldRatio();

    // NOTE (flagged for review): the ">=" here returns true as soon as the
    // ratios are equal, so every tiebreaker below (travelCost, totalGold,
    // canContinueSearch, searchDepth) is unreachable. It was likely meant to be
    // ">" so that equal ratios fall through to those tiebreakers. Left as-is for
    // now because changing it alters escape behaviour and needs a re-benchmark.
    if(Double.compare(thisRatio,otherRatio)>=0){
      return true;
    }
    if(Double.compare(thisRatio,otherRatio)<0){
      return false;
    }

    
    /*
     * If ratios are identical
     * select the branch requiring less travel cost
     */
    if(this.travelCost < other.travelCost){
      return true;
    }
    if(this.travelCost > other.travelCost){
      return false;
    }

    /*
     * If travelCost are identical
     * select the branch collecting more gold
     */
    if(this.totalGold > other.totalGold){
      return true;
    }
    if(this.totalGold < other.totalGold){
      return false;
    }

    /*
     *  if both collect same gold,
     * select branch with future possibility
     * (this) can continue search
     */
    if(this.canContinueSearch && ! other.canContinueSearch){
      return true;
    }
    if(!this.canContinueSearch && other.canContinueSearch){
      return false;
    }
    /*
     * As final tie-breaker
     * select ther branch with more depth
     */
    return this.searchDepth > other.searchDepth;
  }
}