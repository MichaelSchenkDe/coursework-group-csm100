# SDP Temple of Gloom - Group Coursework

## Team Members
- Marijke
- Ajay
- Michael
- Jordy

## Trello Board
https://trello.com/invite/b/6a30f5910fdb37f934d44899/ATTIbf1db34d20b211867dcd49d584a0f7734262067E/sdp-2026

## Project Description
Java implementation of the Temple of Gloom explorer game.
Two-phase solution: exploration (find the Orb) and escape (collect gold and exit).

## Algorithms (branch `jordy-implementation`)

Two algorithms are implemented in the `student` package — standard algorithms
**adapted** to two game constraints:

1. **No teleporting.** `moveTo(...)` only works on a tile *adjacent* to the
   explorer; movement is one physical step at a time, 4-directional (N/E/S/W).
2. **Escape correctness is absolute.** Returning anywhere but the exit, or running
   out of time, scores **zero**, so gold optimisation must never risk it.

### Code structure
Logic sits in small single-responsibility classes; `Explorer` only delegates.

| Class | Responsibility |
|---|---|
| `Explorer` | Entry point — delegates `explore()`/`escape()` to a strategy. |
| `GuidedExplorer` | Explore algorithm (guided DFS + backtracking). |
| `PathFinder` | Reusable Dijkstra utility — `from(node)` gives distances + paths. |
| `GoldEscaper` | Escape algorithm — uses `PathFinder` to pick safe gold detours. |
| `EscapeRouter` | Plain shortest-path escape, kept as a baseline/fallback. |

| Phase | Class | One line |
|---|---|---|
| Explore | `GuidedExplorer` | Depth-first search, but the neighbour closest to the Orb is tried first. |
| Escape | `GoldEscaper` | Dijkstra to the best-value gold tile, but only when the exit stays reachable in time. |

### 1. Exploration — `GuidedExplorer`

A **heuristic-guided depth-first search (DFS) with backtracking**. It keeps the
DFS guarantee (the Orb is always found; the search never gets permanently stuck)
but uses the grid-distance hint `getDistanceToTarget()` to choose the direction
tried first. At every tile:

1. The current tile is marked visited; if `getDistanceToTarget() == 0` the Orb is
   reached → return.
2. Neighbours are **sorted by distance to the Orb**, closest first; the closest
   unvisited one is entered, and the search repeats.
3. At a dead end (no unvisited neighbours), the path is **retraced one tile** and
   its next-best branch is tried.

The single line that makes it *guided* rather than blind:
`neighbours.sort(Comparator.comparingInt(NodeStatus::distanceToTarget));`

#### Worked example
`S` = start, `O` = Orb, `██` = wall; the number is each tile's distance-to-Orb
hint (straight-line grid distance, **ignoring walls**):

```
      col0   col1   col2   col3
row0   S:3    a:2    ██     O:0
row1   b:4    ██     ██     d:1
row2   c:5    e:4    f:3    g:2
```

The only real route is `S → b → c → e → f → g → d → O`. Tile `a` looks close
(distance 2) but is a **dead-end trap** back to `S`. From `S`, `a` is tried first,
hits the dead end, is backtracked, then `b` is taken to the Orb — two wasted
steps, but the Orb is always found.

#### Versus the suggested explore options

| Option | What it does | Difference |
|---|---|---|
| **A — DFS + backtracking** | Deep search, backtracks on dead ends, but **arbitrary** neighbour order → wanders. | This *is* option A plus the distance-sorted step 2: same guarantee, far fewer steps → higher bonus. |
| **B — BFS** | Expanding rings; first arrival = fewest steps. | BFS assumes jumping to any frontier tile, but only *adjacent* moves exist. DFS matches walk-only movement, so A is the base. |
| **C — Greedy (min Manhattan)** | Step to the closest-looking neighbour, **no backtracking**. | Pure greedy walks into trap `a` and **stalls**; the guided version adds DFS backtracking to escape it. |
| **D — A\*** | Best-first on step-count + heuristic. | Same teleport problem as BFS; heuristic unreliable on mazes — little gain on small maps. |

### 2. Escape — `GoldEscaper`

A **Dijkstra-driven safe greedy gold collector**. Dijkstra gives the least-cost
path between any two tiles; edge weights are fixed during escape, so:

1. Dijkstra is run **once from the exit** for every tile's cost-to-exit.
2. Loop: Dijkstra is run **from the current tile**; the gold tile with the best
   **gold-per-step value** (`gold ÷ cost-to-reach`) is chosen *if safe*, and the
   shortest path there is walked, collecting any gold crossed for free.
3. When no safe gold tile remains, the shortest path to the exit is walked.

#### Safety invariant (why it cannot fail)
A detour to gold tile `g` is allowed **only if**
`cost(current → g) + cost(g → exit) ≤ timeRemaining`.
Walking to `g` costs *exactly* `cost(current → g)`, so on arrival the exit stays
reachable. Since the brief guarantees enough time for the shortest path out at the
start, the invariant holds throughout. Verified over 200 random maps: **0 failed
escapes**. The gold-per-step choice is greedy, not maximal — true maximisation is
the NP-hard orienteering problem, too risky under the ~10s timeout.

#### Versus the suggested escape options

| Option | What it does | Relation |
|---|---|---|
| **Dijkstra to exit** (baseline) | Least-cost path out; only gold on that path. | The `EscapeRouter` baseline; `GoldEscaper` reuses the same Dijkstra but re-targets gold tiles. |
| **A\*** | Faster single-target pathfinding. | Negligible gain on small graphs; Dijkstra's all-targets output is reused per gold tile. |
| **Greedy gold + Dijkstra** ⭐ | Safe gold detours, re-checking the budget. | Exactly what is implemented. |
| **Orienteering / TSP** | The truly gold-maximal route. | NP-hard, too slow; greedy's near-optimal result is accepted instead. |

### 3. Results

Same maps, same explore, escape swapped. Score = gold × exploration bonus (≤ 1.3).

| Seed | Dijkstra-to-exit baseline | `GoldEscaper` |
|---|---|---|
| 42 | 8,673 | **24,877** |
| 1 | 47 | **20,791** |
| 2 | 11,849 | **35,556** |
| 3 | 6,554 | **37,746** |
| 1050 | 1,904 | **15,655** |

Seed 42 over 100 runs: **8,548 → 24,519** (team reference 20,875). 200 random
maps: **0 failed escapes**, minimum score 1,147, well under the ~10s per-map limit.
