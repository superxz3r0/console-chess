package chess;

import java.util.Arrays;
import java.util.List;


public class Board {

    private static final int BOARD_SIZE = 8;

    private final Piece[][] grid;

    /**
     * Creates an empty 8x8 board.
     */
    public Board() {
        this.grid = new Piece[BOARD_SIZE][BOARD_SIZE];
    }

    /**
     * Gets the piece at a position.
     *
     * @param position the board coordinate
     * @return the piece at that coordinate, or null if empty
     */
    public Piece get(Position position) {
        return grid[position.getX()][position.getY()];
    }

    /**
     * Sets a piece at a position.
     */
    public void set(Position position, Piece piece) {
        grid[position.getX()][position.getY()] = piece;
    }

    /**
     * Moves a piece from one position to another. Does not validate legality; callers must validate first.
     */
    public void move(Position from, Position to) {
        Piece piece = get(from);
        set(to, piece);
        set(from, null);
        if (piece != null) {
            piece.setHasMoved(true);
        }
    }

    /**
     * Creates a deep copy of the board (pieces are cloned; positions are value objects).
     */
    public Board copy() {
        Board copy = new Board();
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                Piece original = grid[x][y];
                if (original != null) {
                    copy.grid[x][y] = original.clonePiece();
                }
            }
        }
        return copy;
    }

    /**
     * Sets up the standard chess initial position.
     */
    public void setupInitial() {
        for (int x = 0; x < BOARD_SIZE; x++) {
            Arrays.fill(grid[x], null);
        }

        // Pawns
        for (int x = 0; x < BOARD_SIZE; x++) {
            set(new Position(x, 1), new Pawn(Color.WHITE));
            set(new Position(x, 6), new Pawn(Color.BLACK));
        }

        // Rooks
        set(new Position(0, 0), new Rook(Color.WHITE));
        set(new Position(7, 0), new Rook(Color.WHITE));
        set(new Position(0, 7), new Rook(Color.BLACK));
        set(new Position(7, 7), new Rook(Color.BLACK));

        // Knights
        set(new Position(1, 0), new Knight(Color.WHITE));
        set(new Position(6, 0), new Knight(Color.WHITE));
        set(new Position(1, 7), new Knight(Color.BLACK));
        set(new Position(6, 7), new Knight(Color.BLACK));

        // Bishops
        set(new Position(2, 0), new Bishop(Color.WHITE));
        set(new Position(5, 0), new Bishop(Color.WHITE));
        set(new Position(2, 7), new Bishop(Color.BLACK));
        set(new Position(5, 7), new Bishop(Color.BLACK));

        // Queens
        set(new Position(3, 0), new Queen(Color.WHITE));
        set(new Position(3, 7), new Queen(Color.BLACK));

        // Kings
        set(new Position(4, 0), new King(Color.WHITE));
        set(new Position(4, 7), new King(Color.BLACK));
    }

    /**
     * Checks that every intermediate square from {@code from} to {@code to} is empty (exclusive of endpoints).
     *
     * @return true if nothing blocks the straight/diagonal line, false otherwise
     */
    public boolean isPathClear(Position from, Position to) {
        int dx = Integer.compare(to.getX(), from.getX());
        int dy = Integer.compare(to.getY(), from.getY());

        int x = from.getX() + dx;
        int y = from.getY() + dy;

        while (x != to.getX() || y != to.getY()) {
            if (grid[x][y] != null) {
                return false;
            }
            x += dx;
            y += dy;
        }
        return true;
    }

    /**
     * Determines whether a square is attacked by any piece of the given color.
     */
    public boolean isSquareAttacked(Position square, Color byColor) {
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                Piece piece = grid[x][y];
                if (piece != null && piece.getColor() == byColor) {
                    Position from = new Position(x, y);
                    boolean canAttack = piece.canAttack(this, from, square);
                    if (canAttack) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return true if the specified color's king is in check.
     */
    public boolean isKingInCheck(Color kingColor) {
        Position kingPosition = findKing(kingColor);
        if (kingPosition == null) {
            return false; // King already captured on a trial board
        }
        return isSquareAttacked(kingPosition, kingColor.opposite());
    }

    private Position findKing(Color color) {
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                Piece piece = grid[x][y];
                if (piece instanceof King && piece.getColor() == color) {
                    return new Position(x, y);
                }
            }
        }
        return null;
    }

    /**
     * Prints the board and context (players, turn, simple move list) to the console.
     */
    public void print(String white, String black, Color turn, List<String> history) {
        System.out.println();
        System.out.println("Players: White=" + white + "  Black=" + black);
        if (turn == Color.WHITE) {
            System.out.println("Turn: " + white + " (White)");
        } else {
            System.out.println("Turn: " + black + " (Black)");
        }

        for (int rank = BOARD_SIZE - 1; rank >= 0; rank--) {
            System.out.print((rank + 1) + " ");
            for (int file = 0; file < BOARD_SIZE; file++) {
                Piece piece = grid[file][rank];
                System.out.print(" ");
                if (piece == null) {
                    System.out.print("..");
                } else {
                    System.out.print(piece.symbol());
                }
            }
            System.out.println();
        }
        System.out.println("   a  b  c  d  e  f  g  h");

        if (history != null && !history.isEmpty()) {
            System.out.println("Moves: " + String.join(" ", history));
        }

        if (isKingInCheck(turn)) {
            System.out.println("! Your King is in check. You must respond.");
        }
        System.out.println();
    }
}