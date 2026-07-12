# SDP Temple of Gloom - Group Coursework

## Team Members
- Marijke: Explore phase (ExploreSolver), PathFinder utility, testing infrastructure
- Ajay: Escape phase architecture (TargetSearch, SelectNextTarget, Dijkstra)
- Michael: Explore phase implementation
- Jordy: Integration, tooling, escape phase

## Trello Board
https://trello.com/invite/b/6a30f5910fdb37f934d44899/ATTIbf1db34d20b211867dcd49d584a0f7734262067E/sdp-2026

## Project Description
Java implementation of the Temple of Gloom explorer game.
Two-phase solution: exploration (find the Orb) and escape (collect gold and exit).

## Algorithm Design

### Explore Phase - Biased Depth-First Search
The exploration phase uses a depth-first search biased toward neighbours
with the smallest distance to the Orb. At each step, unvisited neighbours
are sorted by their `distanceToTarget` value ascending, so the explorer
moves in roughly the correct direction while retaining the ability to
backtrack and explore dead ends when necessary.

This approach guarantees the Orb is always found and consistently achieves
a higher bonus multiplier than unordered DFS, as confirmed by empirical
testing across 100 random seeds (sorted DFS average: 21541 vs unsorted: 19213).

Implemented in: `student/ExploreSolver.java`

### Escape Phase - Depth-Limited Branch Evaluation with Dijkstra
The escape phase pre-computes shortest distances from every node to the
exit using Dijkstra's algorithm. At each step, a depth-limited DFS
simulation (MAX_DEPTH = 12) is performed from each immediate neighbour
without physically moving the explorer. This estimates the total gold
collectible and travel cost for each branch.

A branch is only considered if it is provably safe — the travel cost
to the branch plus the shortest distance from the branch endpoint to
the exit must not exceed the remaining time budget.

Among all safe branches, the one with the highest gold-per-cost ratio
is selected. If no safe branch exists, the explorer falls back to the
shortest path to the exit.

The branch selection criteria (in priority order):
1. Highest gold-per-travel-cost ratio
2. Lower travel cost on equal ratio
3. Higher total gold on equal ratio and cost
4. Branch can continue beyond search depth
5. Greater search depth as final tiebreaker

Implemented across: `student/Dijkstra.java`, `student/DijkstraResult.java`,
`student/NodeDistance.java`, `student/NextStep.java`,
`student/TargetSearch.java`, `student/TargetSearchResult.java`,
`student/SelectNextTarget.java`

### Design Principles
The implementation follows the Single Responsibility Principle taught
throughout the module, each class has one clearly defined purpose.
`Explorer.java` acts as a thin delegation layer, keeping the entry
points clean and each component independently testable.

## Testing
40 unit tests covering all major components:
- `PathFinderTest` - Dijkstra correctness on known graphs (10 tests)
- `EscapeSolverTest` - gold collection and safe detour logic (8 tests)
- `ExploreSolverTest` - DFS explore on linear and branching graphs (6 tests)
- `TargetSearchResultTest` - branch comparison logic and tiebreakers (6 tests)
- `TargetSearchTest` - branch evaluation and SelectNextTarget (10 tests)

Test graphs are defined in `src/test/java/game/GraphHelper.java` covering:
linear, diamond, time-constrained, no-gold, single-node, deep-gold,
equal-ratio, and explore-specific graph structures.

Run tests: `gradle :temple:test`

## Running the Project
Headless mode (single run):
`gradle :temple:run -PchooseMain=main.TXTmain --args="-s 12345"`

Headless mode (100 runs):
`gradle :temple:run -PchooseMain=main.TXTmain --args="-n 100"`

GUI mode:
`gradle :temple:run -PchooseMain=main.GUImain --args="-s 12345"`
