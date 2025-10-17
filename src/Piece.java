package src;

abstract class Piece {
    private final Color color;
    private boolean moved;

    protected Piece(Color color) { this.color = color; }
    public Color getColor() { return color; }
    public boolean hasMoved() { return moved; }
    public void setMoved() { moved = true; }

    abstract PieceType getType();
    abstract char symbol(); 
    abstract boolean isLegalMove(Board board, Position from, Position to);

    boolean isPathClear(Board board, Position from, Position to) {
        int dx = Integer.compare(to.getX(), from.getX());
        int dy = Integer.compare(to.getY(), from.getY());
        int x = from.getX() + dx;
        int y = from.getY() + dy;
        while (x != to.getX() || y != to.getY()) {
            if (board.get(x, y) != null) return false;
            x += dx; y += dy;
        }
        return true;
    }
}

final class King extends Piece {
    King(Color c) { super(c); }
    @Override PieceType getType() { return PieceType.KING; }
    @Override char symbol() { return getColor() == Color.WHITE ? 'K' : 'k'; }
    @Override boolean isLegalMove(Board b, Position f, Position t) {
        int dx = Math.abs(t.getX() - f.getX());
        int dy = Math.abs(t.getY() - f.getY());
        return dx <= 1 && dy <= 1 && (dx + dy) > 0;
    }
}

final class Queen extends Piece {
    Queen(Color c) { super(c); }
    @Override PieceType getType() { return PieceType.QUEEN; }
    @Override char symbol() { return getColor() == Color.WHITE ? 'Q' : 'q'; }
    @Override boolean isLegalMove(Board b, Position f, Position t) {
        int dx = Math.abs(t.getX() - f.getX());
        int dy = Math.abs(t.getY() - f.getY());
        if (dx == 0 || dy == 0 || dx == dy) return isPathClear(b, f, t);
        return false;
    }
}

final class Rook extends Piece {
    Rook(Color c) { super(c); }
    @Override PieceType getType() { return PieceType.ROOK; }
    @Override char symbol() { return getColor() == Color.WHITE ? 'R' : 'r'; }
    @Override boolean isLegalMove(Board b, Position f, Position t) {
        if (f.getX() != t.getX() && f.getY() != t.getY()) return false;
        return isPathClear(b, f, t);
    }
}

final class Bishop extends Piece {
    Bishop(Color c) { super(c); }
    @Override PieceType getType() { return PieceType.BISHOP; }
    @Override char symbol() { return getColor() == Color.WHITE ? 'B' : 'b'; }
    @Override boolean isLegalMove(Board b, Position f, Position t) {
        int dx = Math.abs(t.getX() - f.getX());
        int dy = Math.abs(t.getY() - f.getY());
        if (dx != dy) return false;
        return isPathClear(b, f, t);
    }
}

final class Knight extends Piece {
    Knight(Color c) { super(c); }
    @Override PieceType getType() { return PieceType.KNIGHT; }
    @Override char symbol() { return getColor() == Color.WHITE ? 'N' : 'n'; }
    @Override boolean isLegalMove(Board b, Position f, Position t) {
        int dx = Math.abs(t.getX() - f.getX());
        int dy = Math.abs(t.getY() - f.getY());
        return dx * dy == 2; // (1,2) or (2,1)
    }
}

final class Pawn extends Piece {
    Pawn(Color c) { super(c); }
    @Override PieceType getType() { return PieceType.PAWN; }
    @Override char symbol() { return getColor() == Color.WHITE ? 'P' : 'p'; }
    @Override boolean isLegalMove(Board b, Position f, Position t) {
        int dir = (getColor() == Color.WHITE) ? 1 : -1;
        int startRank = (getColor() == Color.WHITE) ? 1 : 6;
        int dx = t.getX() - f.getX();
        int dy = t.getY() - f.getY();
        Piece target = b.get(t.getX(), t.getY());

        // forward
        if (dx == 0) {
            if (dy == dir && target == null) return true;
            if (f.getY() == startRank && dy == 2 * dir && target == null) {
                int midY = f.getY() + dir;
                if (b.get(f.getX(), midY) == null) return true;
            }
            return false;
        }
        
        if (Math.abs(dx) == 1 && dy == dir && target != null && target.getColor() != getColor()) {
            return true;
        }
        return false;
    }
}

class IllegalMoveException extends Exception {
    IllegalMoveException(String msg) { super(msg); }
}
