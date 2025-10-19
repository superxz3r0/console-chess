package src;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SpecialMovesTest {

    private static Position sq(String a) { return Position.fromAlgebraic(a); }

    @Test
    void whiteKingsideCastlingMinimal() throws Exception {
        // What: O-O works with minimal pieces.
        // How: e1 king (unmoved), h1 rook (unmoved), e8 black king; f1/g1 clear & not attacked.
        Board b = new Board();
        b.set(sq("e1"), new King(Color.WHITE));
        b.set(sq("h1"), new Rook(Color.WHITE));
        b.set(sq("e8"), new King(Color.BLACK));

        assertTrue(b.isLegalMove(sq("e1"), sq("g1"), Color.WHITE));
        b.move(sq("e1"), sq("g1"), Color.WHITE);

        assertTrue(b.get(sq("g1")) instanceof King);
        assertTrue(b.get(sq("f1")) instanceof Rook);
    }

    @Test
    void enPassantImmediateCapture() throws Exception {
        // What: EP capture only right after the double-step.
        // How: place white pawn e5, black pawn d7 → d7->d5; then e5->d6 (ep) removes pawn on d5.
        Board b = new Board();
        b.set(sq("e1"), new King(Color.WHITE));
        b.set(sq("e8"), new King(Color.BLACK));
        b.set(sq("e5"), new Pawn(Color.WHITE));
        b.set(sq("d7"), new Pawn(Color.BLACK));

        assertTrue(b.isLegalMove(sq("d7"), sq("d5"), Color.BLACK));
        b.move(sq("d7"), sq("d5"), Color.BLACK);

        assertTrue(b.isLegalMove(sq("e5"), sq("d6"), Color.WHITE));
        b.move(sq("e5"), sq("d6"), Color.WHITE);

        assertNull(b.get(sq("d5")));                      // victim removed
        assertTrue(b.get(sq("d6")) instanceof Pawn);      // white pawn now at d6
        assertEquals(Color.WHITE, b.get(sq("d6")).getColor());
    }
}
