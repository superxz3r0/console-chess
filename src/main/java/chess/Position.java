package chess;

import java.util.Objects;

public final class Position {
    private final int file;
    private final int rank;

    public Position(int file, int rank) {
        if (file < 0 || file > 7 || rank < 0 || rank > 7) {
            throw new IllegalArgumentException("Position out of bounds: f=" + file + ", r=" + rank);
        }
        this.file = file;
        this.rank = rank;
    }

    public int file() { return file; }
    public int rank() { return rank; }

    public static Position fromAlgebraic(String sq) {
        if (sq == null || sq.length() != 2) throw new IllegalArgumentException("Bad square: " + sq);
        char f = Character.toLowerCase(sq.charAt(0));
        char r = sq.charAt(1);
        return new Position(f - 'a', r - '1');
    }

    public String toAlgebraic() {
        return "" + (char) ('a' + file) + (char) ('1' + rank);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position p = (Position) o;
        return file == p.file && rank == p.rank;
    }

    @Override public int hashCode() { return Objects.hash(file, rank); }
    @Override public String toString() { return toAlgebraic(); }
}
