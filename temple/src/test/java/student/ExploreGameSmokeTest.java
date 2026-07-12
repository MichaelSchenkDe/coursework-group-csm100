package student;

import game.GameState;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Smoke tests that run the real game engine (terminal mode, no GUI).
 * <p>
 * These do not replace unit tests. They check that explore + escape
 * complete without crashing for common seeds.
 */
class ExploreGameSmokeTest {

    @ParameterizedTest
    @ValueSource(longs = {1, 42, 123})
    void gameRunsInTerminalMode(long seed) {
        assertDoesNotThrow(() -> GameState.runNewGame(seed, false));
    }
}
