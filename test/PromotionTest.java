package src;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PromotionTest {

    private static Position sq(String a) { return Position.fromAlgebraic(a); }

    @Test
    void straightPromotionChoice() throws Exception {
        // What: straight pawn promotion with chosen piece.
        // How: white pawn e7->e8 on empty e8, then promote to Knight.
        Board b = new Board();
        b.set(sq("e1"), new King(Color.WHITE));
        b.set(sq("a8"), new King(Color.BLACK)); // keep e8 empty for straight promotion
        b.set(sq("e7"), new Pawn(Color.WHITE));

        assertTrue(b.isLegalMove(sq("e7"), sq("e8"), Color.WHITE));
        b.move(sq("e7"), sq("e8"), Color.WHITE);

        assertTrue(b.isPromotionPending(sq("e8")));
        b.promote(sq("e8"), PieceType.KNIGHT);
        assertTrue(b.get(sq("e8")) instanceof Knight);
        assertEquals(Color.WHITE, b.get(sq("e8")).getColor());
    }
}
