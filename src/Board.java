package src;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final Piece[][] grid = new Piece[8][8];

    public Board() {}

    public static Board standardSetup() {
        Board b = new Board();
        // Pawns
        for (int x = 0; x < 8; x++) {
            b.grid[x][1] = new Pawn(Color.WHITE);
            b.grid[x][6] = new Pawn(Color.BLACK);
        }
        // Rooks
        b.grid[0][0] = new Rook(Color.WHITE); b.grid[7][0] = new Rook(Color.WHITE);
        b.grid[0][7] = new Rook(Color.BLACK); b.grid[7][7] = new Rook(Color.BLACK);
        // Knights
        b.grid[1][0] = new Knight(Color.WHITE); b.grid[6][0] = new Knight(Color.WHITE);
        b.grid[1][7] = new Knight(Color.BLACK); b.grid[6][7] = new Knight(Color.BLACK);
        // Bishops
        b.grid[2][0] = new Bishop(Color.WHITE); b.grid[5][0] = new Bishop(Color.WHITE);
        b.grid[2][7] = new Bishop(Color.BLACK); b.grid[5][7] = new Bishop(Color.BLACK);
        // Queens
        b.grid[3][0] = new Queen(Color.WHITE);
        b.grid[3][7] = new Queen(Color.BLACK);
        // Kings
        b.grid[4][0] = new King(Color.WHITE);
        b.grid[4][7] = new King(Color.BLACK);
        return b;
    }

    public Piece get(int x, int y) {
        if (!inBounds(x, y)) return null;
        return grid[x][y];
    }
    public Piece get(Position p) { return get(p.getX(), p.getY()); }
    public void set(Position p, Piece piece) { grid[p.getX()][p.getY()] = piece; }

    public static boolean inBounds(int x, int y) { return x >= 0 && x < 8 && y >= 0 && y < 8; }

    public boolean isKingInCheck(Color color) {
        Position kingPos = findKing(color);
        if (kingPos == null) return false;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece enemy = grid[x][y];
                if (enemy != null && enemy.getColor() != color) {
                    if (isPseudoLegal(new Position(x, y), kingPos)) return true;
                }
            }
        }
        return false;
    }

    public Position findKing(Color color) {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece p = grid[x][y];
                if (p != null && p.getType() == PieceType.KING && p.getColor() == color) {
                    return new Position(x, y);
                }
            }
        }
        return null;
    }

    public boolean isPseudoLegal(Position from, Position to) {
        Piece mover = get(from);
        if (mover == null) return false;
        if (!inBounds(to.getX(), to.getY())) return false;
        if (from.equals(to)) return false;
        Piece target = get(to);
        if (target != null && target.getColor() == mover.getColor()) return false;
        return mover.isLegalMove(this, from, to);
    }

    public boolean isLegalMove(Position from, Position to, Color turn) {
        Piece mover = get(from);
        if (mover == null) return false;
        if (mover.getColor() != turn) return false;
        if (!isPseudoLegal(from, to)) return false;

        Board copy = this.copy();
        copy.forceMove(from, to);
        return !copy.isKingInCheck(turn);
    }

    public MoveResult move(Position from, Position to, Color turn) throws IllegalMoveException {
        if (!isLegalMove(from, to, turn)) throw new IllegalMoveException("Illegal move");

        Piece target = get(to);
        Piece mover  = get(from);

        forceMove(from, to);


        boolean capturedKing = (target != null && target.getType() == PieceType.KING);
        boolean check = isKingInCheck(turn.opposite());
        return new MoveResult(capturedKing, check, target != null);
    }

    private void forceMove(Position from, Position to) {
        Piece p = get(from);
        set(to, p);
        set(from, null);
        if (p != null) p.setMoved();
    }

    public Board copy() {
        Board b = new Board();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Piece p = grid[x][y];
                if (p == null) { b.grid[x][y] = null; continue; }
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
                b.grid[x][y] = np;
            }
        }
        return b;
    }

    public java.util.List<String> legalMovesFrom(Position from, Color turn) {
        List<String> out = new ArrayList<>();
        if (!inBounds(from.getX(), from.getY())) return out;
        Piece p = get(from);
        if (p == null || p.getColor() != turn) return out;
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Position to = new Position(x, y);
                if (isLegalMove(from, to, turn)) out.add(from.toString() + to.toString());
            }
        }
        return out;
    }

    public void print(Color turn, List<String> history) {
        System.out.println("    a  b  c  d  e  f  g  h");
        System.out.println("   -------------------------");
        for (int y = 7; y >= 0; y--) {
            System.out.print((y + 1) + " | ");
            for (int x = 0; x < 8; x++) {
                Piece p = grid[x][y];
                char c = '.';
                if (p != null) c = p.symbol();
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

    // ===== Promotion helpers used by Game (Option B) =====

    public boolean isPromotionPending(Position to) {
        Piece p = get(to);
        return (p instanceof Pawn) &&
               ((p.getColor() == Color.WHITE && to.getY() == 7) ||
                (p.getColor() == Color.BLACK && to.getY() == 0));
    }

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
