package src;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PieceMovementTest {

    private static Position sq(String a) { return Position.fromAlgebraic(a); }

    @Test
    void pawnSingleAndDoubleFromStart() {
        // What: white pawn can move 1 or 2 from start.
        // How: isLegalMove on e2->e3 and e2->e4.
        Board b = Board.standardSetup();
        assertTrue(b.isLegalMove(sq("e2"), sq("e3"), Color.WHITE));
        assertTrue(b.isLegalMove(sq("e2"), sq("e4"), Color.WHITE));
    }

    @Test
    void knightLeapsOver() {
        // What: knights ignore blockers.
        // How: start pos g1->f3 and b1->c3 are legal.
        Board b = Board.standardSetup();
        assertTrue(b.isLegalMove(sq("g1"), sq("f3"), Color.WHITE));
        assertTrue(b.isLegalMove(sq("b1"), sq("c3"), Color.WHITE));
    }

    @Test
    void bishopInitiallyBlocked() {
        // What: bishops can’t pass through own pawns from the start.
        // How: c1->g5 should be illegal at initial setup.
        Board b = Board.standardSetup();
        assertFalse(b.isLegalMove(sq("c1"), sq("g5"), Color.WHITE));
    }

    @Test
    void cannotCaptureOwnPiece() {
        // What: moving onto friendly piece is illegal.
        // How: e1->e2 is illegal at start because e2 has white pawn.
        Board b = Board.standardSetup();
        assertFalse(b.isLegalMove(sq("e1"), sq("e2"), Color.WHITE));
    }
}
