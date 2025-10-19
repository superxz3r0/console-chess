package src;

public final class Position {
    private final int x; // file 0..7 (a..h)
    private final int y; // rank 0..7 (1..8)

    // raw coords
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // parse "e2" -> (4,1)
    public static Position fromAlgebraic(String algebraic) {
        if (algebraic == null || algebraic.length() != 2) {
            throw new IllegalArgumentException("Bad square: " + algebraic);
        }
        char f = Character.toLowerCase(algebraic.charAt(0));
        char r = algebraic.charAt(1);
        int x = f - 'a';
        int y = r - '1';
        if (x < 0 || x > 7 || y < 0 || y > 7) {
            throw new IllegalArgumentException("Bad square: " + algebraic);
        }
        return new Position(x, y);
    }

    public int getX() { return x; }
    public int getY() { return y; }

    // back to "e2"
    @Override public String toString() {
        return String.valueOf((char) ('a' + x)) + (char) ('1' + y);
    }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return p.x == x && p.y == y;
    }

    @Override public int hashCode() {
        return (x << 3) ^ y;
    }
}
