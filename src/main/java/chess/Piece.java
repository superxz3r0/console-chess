package chess;

public abstract class Piece {
    protected final Color color;
    protected final PieceType type;

    protected Piece(Color color, PieceType type) {
        this.color = color;
        this.type = type;
    }

    public Color color() { return color; }
    public PieceType type() { return type; }

    /** Uppercase = White, lowercase = Black for printing. */
    public char symbol() {
        char c;
        switch (type) {
            case KING:   c = 'K'; break;
            case QUEEN:  c = 'Q'; break;
            case ROOK:   c = 'R'; break;
            case BISHOP: c = 'B'; break;
            case KNIGHT: c = 'N'; break;
            case PAWN:   c = 'P'; break;
            default:     c = '?';
        }
        return color == Color.WHITE ? c : Character.toLowerCase(c);
    }
}
