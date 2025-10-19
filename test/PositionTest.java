package src;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PositionTest {

    @Test
    void parsesAndPrintsAlgebraic() {
        // What: parse "e2", then toString back to "e2".
        // How: fromAlgebraic → Position, then toString.
        Position p = Position.fromAlgebraic("e2");
        assertEquals(4, p.getX());
        assertEquals(1, p.getY());
        assertEquals("e2", p.toString());
    }

    @Test
    void equalityAndHash() {
        // What: positions with same coords are equal.
        // How: equals + hashCode comparison.
        Position a = new Position(3, 3);
        Position b = new Position(3, 3);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void badSquaresThrow() {
        // What: invalid algebraic throws.
        // How: call fromAlgebraic with bad inputs.
        assertThrows(IllegalArgumentException.class, () -> Position.fromAlgebraic("z9"));
        assertThrows(IllegalArgumentException.class, () -> Position.fromAlgebraic("e"));
        assertThrows(IllegalArgumentException.class, () -> Position.fromAlgebraic(null));
    }
}
