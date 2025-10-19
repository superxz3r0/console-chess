package src;

public enum Color {
    WHITE, BLACK;

    // flip the side
    public Color opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}
        