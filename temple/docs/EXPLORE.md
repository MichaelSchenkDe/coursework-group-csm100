# Explore phase — Michael

Documentation for the **exploration** part of Temple of Gloom (finding the Orb).

---

## 1. Start the game

Run everything from the **repo root** (`coursework-group-1`).

**Prerequisites:** Java 21, and the Gradle wrapper (`gradlew.bat` on Windows, `./gradlew` on Mac/Linux).

### Play once — terminal (text output, no window)

Fixed seed (reproducible maze):

```powershell
.\gradlew.bat ":temple:run" "-PchooseMain=main.TXTmain" "--args=-s 42"
```

Mac/Linux:

```bash
./gradlew ":temple:run" "-PchooseMain=main.TXTmain" "--args=-s 42"
```

Random maze each run (omit `-s`, or use seed `0`):

```powershell
.\gradlew.bat ":temple:run" "-PchooseMain=main.TXTmain"
```

### Play once — GUI (visual window)

```powershell
.\gradlew.bat ":temple:run" "-PchooseMain=main.GUImain" "--args=-s 42"
```

A Swing window opens: maze tiles, explorer sprite, bonus multiplier, time, and gold.

**PowerShell tip:** Always quote `-PchooseMain=...` and `--args=...`. Otherwise `main.TXTmain` may be split incorrectly.

### What you should see (terminal)

After explore + escape finish:

```
Seed : 42
Gold collected   : ...
Bonus multiplier : 1.2
Score            : ...
```

- **Bonus multiplier** measures explore quality (fewer steps → higher bonus, up to 1.3).
- **Score** also depends on escape (gold collected). With a stub escape, score may be 0 even when explore works.

### Command-line arguments

| Flag | Meaning | Example |
|------|---------|---------|
| `-s <number>` | Fixed random seed | `--args=-s 42` |
| `-n <count>` | Repeat N runs (terminal only) | `--args=-n 100 -s 42` |
| (none) | Random seed | `--args=` or omit `--args` |

---

## 2. What this code does

| Phase | Class | Algorithm |
|-------|-------|-----------|
| **Explore** | `ExploreSolver` | Guided DFS |
| **Wiring** | `Explorer.explore()` | Calls `ExploreSolver.solve(state)` |
| **Escape** | Not owned here | Handled by teammates |

**Goal:** Move through the maze, find the Orb, and stop while standing on it.

**Score impact:** Fewer explore steps → higher **bonus multiplier** (up to 1.3).

---

## 3. Algorithm (guided DFS)

1. Mark the current tile as visited.
2. If `getDistanceToTarget() == 0`, you are on the Orb → stop.
3. Sort neighbours by Manhattan distance to the Orb (closest first).
4. For each unvisited neighbour:
   - move there
   - recurse
   - if Orb found, return
   - otherwise backtrack one step

This is depth-first search with a **heuristic**: always try the direction that looks closest to the Orb first.

---

## 4. Project layout (explore only)

```
temple/
├── src/main/java/student/
│   ├── ExploreSolver.java      ← explore algorithm
│   └── Explorer.java           ← explore() delegates here
├── src/test/java/student/explore/
│   ├── ExploreSolverCorrectnessTest.java
│   ├── ExploreSolverHeuristicTest.java
│   ├── ExploreSolverGraphTest.java
│   ├── ExploreSolverEfficiencyTest.java
│   ├── ExplorerWiringTest.java
│   ├── ExploreGameSmokeTest.java
│   ├── COMMIT_PLAN.md
│   └── support/
│       ├── MockExplorationState.java
│       └── ExploreTestGraphs.java
└── docs/
    └── EXPLORE.md
```

---

## 5. Testing — overview

There are **four levels** of testing. Use them in this order while developing:

| Level | What | How | Automated? |
|-------|------|-----|------------|
| **1. Unit** | Algorithm on tiny graphs | JUnit + `MockExplorationState` | Yes |
| **2. Wiring** | `Explorer` calls `ExploreSolver` | JUnit | Yes |
| **3. Smoke** | Full game engine, no GUI | JUnit + `GameState.runNewGame(seed, false)` | Yes |
| **4. Manual GUI** | Watch movement visually | Run `GUImain` | No (human checks) |
| **5. Benchmark** | Score / bonus on real seeds | Terminal `TXTmain` with `-n` | Manual |

**Can we use the GUI for tests?**

- **Automated CI tests:** No. The project has no headless GUI test framework. All JUnit tests run without opening a window.
- **Manual visual testing:** Yes — this is the main purpose of the GUI. Use it to:
  - Confirm the explorer reaches the Orb
  - Watch backtracking and dead-end behaviour
  - Compare bonus updates step-by-step with terminal output for the same seed
  - Spot illegal moves or crashes with an on-screen error dialog

**Recommended workflow**

1. Run unit tests after every code change (fast, ~5 seconds).
2. Run smoke tests before pushing (real maze generation).
3. Open the GUI once with seed `42` to sanity-check visually.
4. Run a 100× benchmark when tuning the algorithm.

---

## 6. Running tests

### Run all explore tests

```powershell
.\gradlew.bat :temple:test
```

### Run one test class

```powershell
.\gradlew.bat :temple:test --tests "student.explore.ExploreSolverCorrectnessTest"
```

### Run one test method

```powershell
.\gradlew.bat :temple:test --tests "student.explore.ExploreSolverCorrectnessTest.findsOrbOnLinearGraph"
```

### Re-run after a failure (verbose)

```powershell
.\gradlew.bat :temple:test --rerun-tasks --info
```

### Compile only (no tests)

```powershell
.\gradlew.bat :temple:compileJava
.\gradlew.bat :temple:compileTestJava
```

---

## 7. Unit tests (level 1–2)

### What each test class checks

| Test class | What it checks |
|------------|----------------|
| `ExploreSolverCorrectnessTest` | Orb is always found (linear, branching, orb at start) |
| `ExploreSolverHeuristicTest` | Closer neighbours tried first; misleading dead ends explored and backtracked |
| `ExploreSolverGraphTest` | Zig-zag graph; zero moves when orb is already at start |
| `ExploreSolverEfficiencyTest` | Move count is optimal on graphs without misleading branches |
| `ExplorerWiringTest` | `Explorer.explore()` delegates to `ExploreSolver` |

### How unit tests work

Tests use a **mock** `ExplorationState` (`MockExplorationState`) with small hand-made graphs (`ExploreTestGraphs`). No full game engine or GUI is needed.

### Example graphs

**Linear:** `1 — 2 — 3 — 4 (orb)` — minimum 3 moves from start.

**Branching:** `1 — 2 — 3 (orb)` with dead end at `4`.

**Misleading dead end:** node `4` looks closer to the orb than `2`, but is a dead end.

**Cycle:** square loop with orb on one corner — ensures visited tracking prevents infinite loops.

### Adding a new unit test

1. Add a graph factory in `ExploreTestGraphs.java`.
2. Create a test method in the appropriate test class.
3. Assert `getDistanceToTarget() == 0` and `getCurrentLocation()` is the orb node.
4. Optionally assert `state.moveHistory()` for order or efficiency.

---

## 8. Smoke tests (level 3)

`ExploreGameSmokeTest` runs the **real** `GameState` in terminal mode for seeds `1`, `42`, and `123`.

It checks the game **completes without throwing**. It does not assert a specific score (escape may be a stub).

```powershell
.\gradlew.bat :temple:test --tests "student.explore.ExploreGameSmokeTest"
```

Use smoke tests when you change `Explorer` wiring or anything that touches the game engine.

---

## 9. Manual GUI testing (level 4)

### Setup — copy image assets

The GUI loads PNG files from the classpath. They must be in:

```
temple/src/main/resources/
```

Required files (from `gui/Constants.java`):

| File | Purpose |
|------|---------|
| `orb.png` | Orb tile |
| `path.png` | Floor |
| `wall.png` | Wall |
| `entrance.png` | Entrance |
| `coins.png` | Gold |
| `notes.png` | Tasty tile |
| `info_texture.png` | UI background |
| `explorer_sprites.png` | Explorer sprite sheet |

If images are missing, the GUI may fail to start or print errors in the console.

### Run the GUI

```powershell
.\gradlew.bat ":temple:run" "-PchooseMain=main.GUImain" "--args=-s 42"
```

### What to check manually

| Check | Good sign | Bad sign |
|-------|-----------|----------|
| Explore phase | Explorer reaches orb tile; bonus stays high | Stops early, wanders forever, or error dialog |
| Backtracking | Explorer returns out of dead ends | Illegal move crash |
| Escape phase | Lighting turns on; explorer moves (if escape implemented) | Immediate failure message |
| Same seed | Same maze as terminal run with `-s 42` | Different layout → wrong seed |

### Compare GUI vs terminal

Run the same seed in both modes and compare the printed **bonus multiplier**:

```powershell
# Terminal
.\gradlew.bat ":temple:run" "-PchooseMain=main.TXTmain" "--args=-s 42"

# GUI (watch window, read bonus in UI and console at end)
.\gradlew.bat ":temple:run" "-PchooseMain=main.GUImain" "--args=-s 42"
```

---

## 10. Benchmarking (level 5)

Team comparison on seed 42 (single run):

```powershell
.\gradlew.bat ":temple:run" "-PchooseMain=main.TXTmain" "--args=-s 42"
```

100-run average (team benchmark):

```powershell
.\gradlew.bat ":temple:run" "-PchooseMain=main.TXTmain" "--args=-n 100 -s 42"
```

League-table seed (needs full escape for high scores):

```powershell
.\gradlew.bat ":temple:run" "-PchooseMain=main.TXTmain" "--args=-s -4152836868077314850"
```

### Reference scores

| Seed | Purpose | Typical explore bonus* | Typical avg score** |
|------|---------|------------------------|---------------------|
| `42` | Team comparison | ~1.2–1.24 | ~25 000 |
| `-4152836868077314850` | League high-score | varies | ~63 000–69 000 |

\* Bonus multiplier from terminal output.  
\** Full score needs a working **escape** implementation.

---

## 11. Troubleshooting

| Problem | Likely cause | Fix |
|---------|--------------|-----|
| `main.TXTmain` not found | PowerShell split the argument | Quote `-PchooseMain=main.TXTmain` |
| Score is 0 | Escape stub collects no gold | Expected for explore-only; check bonus instead |
| `moveTo: Node must be adjacent` | Backtracking bug in solver | Fix `ExploreSolver` DFS return path |
| GUI blank / crash | Missing PNGs in `resources/` | Add image files (see section 9) |
| Tests pass, game fails | Mock graphs ≠ real mazes | Run smoke test + GUI with seed 42 |

---

## 12. Quick reference

| Task | Command |
|------|---------|
| Compile | `.\gradlew.bat :temple:compileJava` |
| All tests | `.\gradlew.bat :temple:test` |
| One test class | `.\gradlew.bat :temple:test --tests "student.explore.ExploreSolverCorrectnessTest"` |
| Play (terminal) | `.\gradlew.bat ":temple:run" "-PchooseMain=main.TXTmain" "--args=-s 42"` |
| Play (GUI) | `.\gradlew.bat ":temple:run" "-PchooseMain=main.GUImain" "--args=-s 42"` |
| Benchmark 100× | `.\gradlew.bat ":temple:run" "-PchooseMain=main.TXTmain" "--args=-n 100 -s 42"` |
