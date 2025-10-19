package src;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CheckmateTest {

    private static Position sq(String a) { return Position.fromAlgebraic(a); }

    @Test
    void foolsMate() throws Exception {
        // What: detect checkmate (Fool's Mate).
        // How: 1.f3 e5 2.g4 Qh4# from the standard start.
        Board b = Board.standardSetup();

        assertTrue(b.isLegalMove(sq("f2"), sq("f3"), Color.WHITE));
        b.move(sq("f2"), sq("f3"), Color.WHITE);

        assertTrue(b.isLegalMove(sq("e7"), sq("e5"), Color.BLACK));
        b.move(sq("e7"), sq("e5"), Color.BLACK);

        assertTrue(b.isLegalMove(sq("g2"), sq("g4"), Color.WHITE));
        b.move(sq("g2"), sq("g4"), Color.WHITE);

        assertTrue(b.isLegalMove(sq("d8"), sq("h4"), Color.BLACK));
        b.move(sq("d8"), sq("h4"), Color.BLACK);

        assertTrue(b.isKingInCheck(Color.WHITE));      // White in check
        assertFalse(b.hasAnyLegalMove(Color.WHITE));   // …and no legal moves => mate
    }
}
