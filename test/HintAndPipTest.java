package src;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class HintAndPipTest {

    private static Position sq(String a) { return Position.fromAlgebraic(a); }

    @Test
    void legalMovesFromSquare() {
        // What: list of legal moves from e2 at start.
        // How: legalMovesFrom returns "e2e3" and "e2e4".
        Board b = Board.standardSetup();
        List<String> moves = b.legalMovesFrom(sq("e2"), Color.WHITE);
        assertTrue(moves.contains("e2e3"));
        assertTrue(moves.contains("e2e4"));
    }
}
 