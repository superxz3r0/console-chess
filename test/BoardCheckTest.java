package src;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BoardCheckTest {

    private static Position sq(String a) { return Position.fromAlgebraic(a); }

    @Test
    void detectsCheckAlongOpenFile() throws Exception {
        // What: detect check on open file.
        // How: place white king e1, black rook e8, empty in between → e1 is attacked.
        Board b = new Board();
        b.set(sq("e1"), new King(Color.WHITE));
        b.set(sq("e8"), new Rook(Color.BLACK));
        assertTrue(b.isSquareAttacked(sq("e1"), Color.BLACK));
        assertTrue(b.isKingInCheck(Color.WHITE));
    }

    @Test
    void noCheckWhenBlocked() {
        // What: attack blocked by intervening piece.
        // How: put white piece on e4 to block black rook e8 to e1.
        Board b = new Board();
        b.set(sq("e1"), new King(Color.WHITE));
        b.set(sq("e4"), new Pawn(Color.WHITE));
        b.set(sq("e8"), new Rook(Color.BLACK));
        assertFalse(b.isSquareAttacked(sq("e1"), Color.BLACK));
        assertFalse(b.isKingInCheck(Color.WHITE));
    }
}
