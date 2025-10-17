package chess;

public final class Position {

    private final int x; 
    private final int y; 

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Position fromAlgebraic(String algebraic) {
        char fileChar = Character.toLowerCase(algebraic.charAt(0));
        char rankChar = algebraic.charAt(1);
        int file = fileChar - 'a';
        int rank = rankChar - '1';
        return new Position(file, rank);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        char fileChar = (char) ('a' + x);
        char rankChar = (char) ('1' + y);
        return "" + fileChar + rankChar;
    }
}