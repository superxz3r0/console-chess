package chess;

import chess.pieces.*;

public class Board {
    private final Piece[][] squares = new Piece[8][8];

    public Board() {
        setupInitialPosition();
    }

    public Piece get(int file, int rank) { return squares[file][rank]; }
    public Piece get(Position p) { return get(p.file(), p.rank()); }
    public void set(int file, int rank, Piece piece) { squares[file][rank] = piece; }

    private void setupInitialPosition() {
        for (int f = 0; f < 8; f++) {
            set(f, 1, new Pawn(Color.WHITE));
            set(f, 6, new Pawn(Color.BLACK));
        }
        set(0, 0, new Rook(Color.WHITE));
        set(1, 0, new Knight(Color.WHITE));
        set(2, 0, new Bishop(Color.WHITE));
        set(3, 0, new Queen(Color.WHITE));
        set(4, 0, new King(Color.WHITE));
        set(5, 0, new Bishop(Color.WHITE));
        set(6, 0, new Knight(Color.WHITE));
        set(7, 0, new Rook(Color.WHITE));


        set(0, 7, new Rook(Color.BLACK));
        set(1, 7, new Knight(Color.BLACK));
        set(2, 7, new Bishop(Color.BLACK));
        set(3, 7, new Queen(Color.BLACK));
        set(4, 7, new King(Color.BLACK));
        set(5, 7, new Bishop(Color.BLACK));
        set(6, 7, new Knight(Color.BLACK));
        set(7, 7, new Rook(Color.BLACK));
    }


    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("    a   b   c   d   e   f   g   h\n");
        sb.append("  +---+---+---+---+---+---+---+---+\n");
        for (int r = 7; r >= 0; r--) {
            sb.append(r + 1).append(" |");
            for (int f = 0; f < 8; f++) {
                Piece p = get(f, r);
                char ch = p == null ? ' ' : p.symbol();
                sb.append(' ').append(ch).append(' ').append('|');
            }
            sb.append(' ').append(r + 1).append('\n');
            sb.append("  +---+---+---+---+---+---+---+---+\n");
        }
        sb.append("    a   b   c   d   e   f   g   h\n");
        return sb.toString();
    }
}
