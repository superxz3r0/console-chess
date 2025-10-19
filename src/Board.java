package src;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final Piece[][] grid = new Piece[8][8];

    // en passant state (one-move window)
    private Position enPassantSquare = null;     // landing square (passed-over)
    private Position enPassantVictimPos = null;  // pawn to remove if captured EP

    public Board() {}

    // initial setup
    public static Board standardSetup() {
        Board b = new Board();
        for (int x = 0; x < 8; x++) {
            b.grid[x][1] = new Pawn(Color.WHITE);
            b.grid[x][6] = new Pawn(Color.BLACK);
        }
        b.grid[0][0] = new Rook(Color.WHITE); b.grid[7][0] = new Rook(Color.WHITE);
        b.grid[0][7] = new Rook(Color.BLACK); b.grid[7][7] = new Rook(Color.BLACK);
        b.grid[1][0] = new Knight(Color.WHITE); b.grid[6][0] = new Knight(Color.WHITE);
        b.grid[1][7] = new Knight(Color.BLACK); b.grid[6][7] = new Knight(Color.BLACK);
        b.grid[2][0] = new Bishop(Color.WHITE); b.grid[5][0] = new Bishop(Color.WHITE);
        b.grid[2][7] = new Bishop(Color.BLACK); b.grid[5][7] = new Bishop(Color.BLACK);
        b.grid[3][0] = new Queen(Color.WHITE);  b.grid[3][7] = new Queen(Color.BLACK);
        b.grid[4][0] = new King(Color.WHITE);   b.grid[4][7] = new King(Color.BLACK);
        return b;
    }

    // piece getters/setters
    public Piece get(int x, int y) { return inBounds(x, y) ? grid[x][y] : null; }
    public Piece get(Position p) { return get(p.getX(), p.getY()); }
    public void set(Position p, Piece piece) { grid[p.getX()][p.getY()] = piece; }

    // board bounds
    public static boolean inBounds(int x, int y) { return x >= 0 && x < 8 && y >= 0 && y < 8; }

    // is my king under attack
    public boolean isKingInCheck(Color color) {
        Position k = findKing(color);
        return k != null && isSquareAttacked(k, color.opposite());
    }

    // locate the king
    public Position findKing(Color color) {
        for (int x = 0; x < 8; x++) for (int y = 0; y < 8; y++) {
            Piece p = grid[x][y];
            if (p != null && p.getType() == PieceType.KING && p.getColor() == color) {
                return new Position(x, y);
            }
        }
        return null;
    }

    // piece rules + specials (no king safety)
    public boolean isPseudoLegal(Position from, Position to) {
        Piece mover = get(from);
        if (mover == null) return false;
        if (!inBounds(to.getX(), to.getY())) return false;
        if (from.equals(to)) return false;

        Piece target = get(to);
        if (target != null && target.getColor() == mover.getColor()) return false;

        // castling: king moves 2 along rank
        if (mover.getType() == PieceType.KING &&
            from.getY() == to.getY() &&
            Math.abs(to.getX() - from.getX()) == 2) {
            return canCastle(from, to, mover.getColor());
        }

        // en passant: pawn moves diagonally into empty enPassantSquare
        if (mover.getType() == PieceType.PAWN && target == null) {
            int dx = to.getX() - from.getX();
            int dy = to.getY() - from.getY();
            int dir = (mover.getColor() == Color.WHITE) ? 1 : -1;
            if (Math.abs(dx) == 1 && dy == dir && enPassantSquare != null && to.equals(enPassantSquare)) {
                if (enPassantVictimPos != null) {
                    Piece victim = get(enPassantVictimPos);
                    return victim instanceof Pawn && victim.getColor() != mover.getColor();
                }
            }
        }

        return mover.isLegalMove(this, from, to);
    }

    // full legality (includes king safety via simulate)
    public boolean isLegalMove(Position from, Position to, Color turn) {
        Piece mover = get(from);
        if (mover == null || mover.getColor() != turn) return false;
        if (!isPseudoLegal(from, to)) return false;

        Board copy = this.copy();
        copy.applyMoveWithoutSafety(from, to, true); // simulate
        return !copy.isKingInCheck(turn);
    }

    // run the move and return flags for UI/notation
    public MoveResult move(Position from, Position to, Color turn) throws IllegalMoveException {
        if (!isLegalMove(from, to, turn)) throw new IllegalMoveException("Illegal move");

        Piece targetBefore = get(to);
        boolean capturedOnTarget = (targetBefore != null);
        boolean enPassant = isEnPassantMove(from, to);

        // apply (handles castling rook shift, EP victim removal, EP state updates)
        applyMoveWithoutSafety(from, to, false);

        boolean capturedKing = capturedOnTarget && targetBefore.getType() == PieceType.KING;
        boolean gaveCheck = isKingInCheck(turn.opposite());
        boolean wasCaptureEffective = capturedOnTarget || enPassant;

        return new MoveResult(capturedKing, gaveCheck, wasCaptureEffective);
    }

    // actually apply a move (no re-checks)
    private void applyMoveWithoutSafety(Position from, Position to, boolean forSimulation) {
        Piece mover = get(from);

        boolean castle = isCastleMove(from, to);
        boolean enPassant = isEnPassantMove(from, to);

        // move piece
        forceMove(from, to);

        // move rook if castling
        if (castle) {
            handleCastleRookMove(to, mover.getColor());
        }

        // remove EP victim
        if (enPassant) {
            int dir = (mover.getColor() == Color.WHITE) ? 1 : -1;
            Position victimPos = new Position(to.getX(), to.getY() - dir);
            set(victimPos, null);
        }

        // update EP state only on real move
        if (!forSimulation) {
            if (mover.getType() == PieceType.PAWN) {
                int dy = to.getY() - from.getY();
                int dir = (mover.getColor() == Color.WHITE) ? 1 : -1;
                if (dy == 2 * dir) {
                    enPassantSquare = new Position(from.getX(), from.getY() + dir);
                    enPassantVictimPos = to;
                } else {
                    clearEnPassantState();
                }
            } else {
                clearEnPassantState();
            }
        }
    }

    // wipe EP window
    private void clearEnPassantState() {
        enPassantSquare = null;
        enPassantVictimPos = null;
    }

    // raw board move + flag as moved
    private void forceMove(Position from, Position to) {
        Piece p = get(from);
        set(to, p);
        set(from, null);
        if (p != null) p.setMoved();
    }

    // is king-side/queen-side castle attempt
    private boolean isCastleMove(Position from, Position to) {
        Piece mover = get(from);
        return mover != null
            && mover.getType() == PieceType.KING
            && from.getY() == to.getY()
            && Math.abs(to.getX() - from.getX()) == 2;
    }

    // is EP attempt (pawn diagonal into empty EP square)
    private boolean isEnPassantMove(Position from, Position to) {
        Piece mover = get(from);
        if (mover == null || mover.getType() != PieceType.PAWN) return false;
        if (get(to) != null) return false;
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int dir = (mover.getColor() == Color.WHITE) ? 1 : -1;
        return Math.abs(dx) == 1 && dy == dir && enPassantSquare != null && to.equals(enPassantSquare);
    }

    // move rook when castling
    private void handleCastleRookMove(Position kingTo, Color color) {
        int y = (color == Color.WHITE) ? 0 : 7;
        if (kingTo.getX() == 6 && kingTo.getY() == y) {          // O-O
            forceMove(new Position(7, y), new Position(5, y));
        }
        if (kingTo.getX() == 2 && kingTo.getY() == y) {          // O-O-O
            forceMove(new Position(0, y), new Position(3, y));
        }
    }

    // castle pre-checks (paths clear, unmoved rook/king, not through check)
    private boolean canCastle(Position from, Position to, Color color) {
        Piece king = get(from);
        if (!(king instanceof King) || king.hasMoved()) return false;
        int y = (color == Color.WHITE) ? 0 : 7;
        if (from.getY() != y || to.getY() != y) return false;

        int dx = to.getX() - from.getX();
        if (dx == 2) { // king-side
            Piece rook = get(new Position(7, y));
            if (!(rook instanceof Rook) || rook.getColor() != color || rook.hasMoved()) return false;
            if (get(5, y) != null || get(6, y) != null) return false;
            if (isKingInCheck(color)) return false;
            if (isSquareAttacked(new Position(5, y), color.opposite())) return false;
            if (isSquareAttacked(new Position(6, y), color.opposite())) return false;
            return true;
        } else if (dx == -2) { // queen-side
            Piece rook = get(new Position(0, y));
            if (!(rook instanceof Rook) || rook.getColor() != color || rook.hasMoved()) return false;
            if (get(1, y) != null || get(2, y) != null || get(3, y) != null) return false;
            if (isKingInCheck(color)) return false;
            if (isSquareAttacked(new Position(3, y), color.opposite())) return false;
            if (isSquareAttacked(new Position(2, y), color.opposite())) return false;
            return true;
        }
        return false;
    }

    // is this square attacked by "byColor"
    public boolean isSquareAttacked(Position square, Color byColor) {
        int sx = square.getX(), sy = square.getY();

        // pawn origins that attack (reverse dir)
        int pdir = (byColor == Color.WHITE) ? 1 : -1;
        int px1 = sx - 1, py1 = sy - pdir;
        int px2 = sx + 1, py2 = sy - pdir;
        if (inBounds(px1, py1)) {
            Piece p = get(px1, py1);
            if (p instanceof Pawn && p.getColor() == byColor) return true;
        }
        if (inBounds(px2, py2)) {
            Piece p = get(px2, py2);
            if (p instanceof Pawn && p.getColor() == byColor) return true;
        }

        // knights
        int[][] kj = {{1,2},{2,1},{-1,2},{-2,1},{1,-2},{2,-1},{-1,-2},{-2,-1}};
        for (int[] d : kj) {
            int x = sx + d[0], y = sy + d[1];
            if (inBounds(x, y)) {
                Piece p = get(x, y);
                if (p instanceof Knight && p.getColor() == byColor) return true;
            }
        }

        // adjacent king
        for (int dx = -1; dx <= 1; dx++) for (int dy = -1; dy <= 1; dy++) {
            if (dx == 0 && dy == 0) continue;
            int x = sx + dx, y = sy + dy;
            if (inBounds(x, y)) {
                Piece p = get(x, y);
                if (p instanceof King && p.getColor() == byColor) return true;
            }
        }

        // rook/queen lines
        int[][] ortho = {{1,0},{-1,0},{0,1},{0,-1}};
        for (int[] d : ortho) if (rayHits(sx, sy, d[0], d[1], byColor, true)) return true;

        // bishop/queen diagonals
        int[][] diag = {{1,1},{1,-1},{-1,1},{-1,-1}};
        for (int[] d : diag) if (rayHits(sx, sy, d[0], d[1], byColor, false)) return true;

        return false;
    }

    // scan along a ray; first hit must be our attacking piece type
    private boolean rayHits(int sx, int sy, int dx, int dy, Color byColor, boolean orthogonal) {
        int x = sx + dx, y = sy + dy;
        while (inBounds(x, y)) {
            Piece p = get(x, y);
            if (p == null) { x += dx; y += dy; continue; }
            if (p.getColor() != byColor) return false;
            if (orthogonal && (p instanceof Rook || p instanceof Queen)) return true;
            if (!orthogonal && (p instanceof Bishop || p instanceof Queen)) return true;
            return false;
        }
        return false;
    }

    // needs promotion?
    public boolean isPromotionPending(Position to) {
        Piece p = get(to);
        return (p instanceof Pawn) &&
               ((p.getColor() == Color.WHITE && to.getY() == 7) ||
                (p.getColor() == Color.BLACK && to.getY() == 0));
    }

    // swap pawn at 'to' with chosen piece
    public void promote(Position to, PieceType toType) {
        Piece p = get(to);
        if (!(p instanceof Pawn)) return;
        Color c = p.getColor();
        switch (toType) {
            case QUEEN:  set(to, new Queen(c));  break;
            case ROOK:   set(to, new Rook(c));   break;
            case BISHOP: set(to, new Bishop(c)); break;
            case KNIGHT: set(to, new Knight(c)); break;
            default:     set(to, new Queen(c));
        }
    }

    // all legal moves (strings) from a square
    public List<String> legalMovesFrom(Position from, Color turn) {
        List<String> out = new ArrayList<>();
        if (!inBounds(from.getX(), from.getY())) return out;
        Piece p = get(from);
        if (p == null || p.getColor() != turn) return out;
        for (int x = 0; x < 8; x++) for (int y = 0; y < 8; y++) {
            Position to = new Position(x, y);
            if (isLegalMove(from, to, turn)) out.add(from.toString() + to.toString());
        }
        return out;
    }

    // does side have any legal move at all
    public boolean hasAnyLegalMove(Color color) {
        for (int x = 0; x < 8; x++) for (int y = 0; y < 8; y++) {
            Piece p = grid[x][y];
            if (p == null || p.getColor() != color) continue;
            Position from = new Position(x, y);
            for (int tx = 0; tx < 8; tx++) for (int ty = 0; ty < 8; ty++) {
                if (isLegalMove(from, new Position(tx, ty), color)) return true;
            }
        }
        return false;
    }

    // console print
    public void print(Color turn, List<String> history) {
        System.out.println("    a  b  c  d  e  f  g  h");
        System.out.println("   -------------------------");
        for (int y = 7; y >= 0; y--) {
            System.out.print((y + 1) + " | ");
            for (int x = 0; x < 8; x++) {
                Piece p = grid[x][y];
                char c = (p == null) ? '.' : p.symbol();
                System.out.print(c + "  ");
            }
            System.out.println("| " + (y + 1));
        }
        System.out.println("   -------------------------");
        System.out.println("    a  b  c  d  e  f  g  h");
        if (history != null && !history.isEmpty()) {
            System.out.println("Moves: " + String.join(" ", history));
        }
        if (isKingInCheck(turn)) System.out.println("! Your King is in check.");
        System.out.println();
    }

    // copy board (for simulations)
    public Board copy() {
        Board b = new Board();
        for (int x = 0; x < 8; x++) for (int y = 0; y < 8; y++) {
            Piece p = grid[x][y];
            if (p == null) continue;
            Color c = p.getColor();
            Piece np;
            switch (p.getType()) {
                case KING:   np = new King(c);   break;
                case QUEEN:  np = new Queen(c);  break;
                case ROOK:   np = new Rook(c);   break;
                case BISHOP: np = new Bishop(c); break;
                case KNIGHT: np = new Knight(c); break;
                case PAWN:   np = new Pawn(c);   break;
                default: throw new IllegalStateException();
            }
            if (p.hasMoved()) np.setMoved();
            b.grid[x][y] = np;
        }
        // carry EP state for accurate simulation
        b.enPassantSquare = (this.enPassantSquare == null) ? null : new Position(enPassantSquare.getX(), enPassantSquare.getY());
        b.enPassantVictimPos = (this.enPassantVictimPos == null) ? null : new Position(enPassantVictimPos.getX(), enPassantVictimPos.getY());
        return b;
    }

    // return type for UI to decorate notation
    public static final class MoveResult {
        public final boolean capturedKing;
        public final boolean gaveCheck;
        public final boolean wasCapture;
        MoveResult(boolean cK, boolean gC, boolean wC) {
            this.capturedKing = cK;
            this.gaveCheck = gC;
            this.wasCapture = wC;
        }
    }
}
