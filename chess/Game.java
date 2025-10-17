package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Game {

    private static final String QUIT_SHORT = "q";
    private static final String QUIT_LONG = "quit";

    private final Scanner scanner;
    private final Board board;
    private String whitePlayerName;
    private String blackPlayerName;
    private Color currentTurn;
    private boolean gameOver;
    private final List<String> moveHistory;

    /**
     * Creates a new Game instance with fresh state.
     */
    public Game() {
        this.scanner = new Scanner(System.in);
        this.board = new Board();
        this.currentTurn = Color.WHITE;
        this.gameOver = false;
        this.moveHistory = new ArrayList<>();
    }

    /**
     * Runs the main game loop until the game ends or the user quits.
     */
    public void run() {
        printWelcome();
        promptPlayerNames();
        board.setupInitial();

        while (!gameOver) {
            board.print(whitePlayerName, blackPlayerName, currentTurn, moveHistory);

            String input = promptForMove();
            if (shouldQuit(input)) {
                System.out.println("Goodbye!");
                return;
            }

            if (!isCoordinateMove(input)) {
                System.out.println("Invalid input. Use a four‑character move like e2e4.\n");
                continue;
            }

            Position from = Position.fromAlgebraic(input.substring(0, 2));
            Position to = Position.fromAlgebraic(input.substring(2, 4));

            try {
                MoveResult result = attemptMove(from, to);
                if (result == MoveResult.MOVED) {
                    currentTurn = currentTurn.opposite();
                } else if (result == MoveResult.CAPTURED_KING) {
                    board.print(whitePlayerName, blackPlayerName, currentTurn, moveHistory);
                    String winnerName = (currentTurn == Color.WHITE) ? whitePlayerName : blackPlayerName;
                    System.out.println("\n*** " + winnerName + " wins by capturing the King. Game over. ***");
                    gameOver = true;
                }
            } catch (IllegalMoveException ex) {
                System.out.println("Illegal move: " + ex.getMessage() + "\n");
            }
        }
    }

    private void printWelcome() {
        System.out.println("Console Chess — Required Features Only");
        System.out.println();
    }

    private void promptPlayerNames() {
        System.out.print("Enter White player name: ");
        this.whitePlayerName = readNonEmptyOrDefault("White");

        System.out.print("Enter Black player name: ");
        this.blackPlayerName = readNonEmptyOrDefault("Black");
        System.out.println();
    }

    private String readNonEmptyOrDefault(String defaultValue) {
        String value = scanner.nextLine();
        if (value == null) {
            return defaultValue;
        }
        value = value.trim();
        if (value.isEmpty()) {
            return defaultValue;
        }
        return value;
    }

    private String promptForMove() {
        System.out.print("Enter move (e.g., e2e4), or 'q' to quit: ");
        return scanner.nextLine().trim();
    }

    private boolean shouldQuit(String input) {
        return QUIT_SHORT.equalsIgnoreCase(input) || QUIT_LONG.equalsIgnoreCase(input);
    }

    private boolean isCoordinateMove(String input) {
        return input != null && input.matches("(?i)^[a-h][1-8][a-h][1-8]$");
    }

    private MoveResult attemptMove(Position from, Position to) throws IllegalMoveException {
        Piece movingPiece = board.get(from);
        if (movingPiece == null) {
            throw new IllegalMoveException("No piece on " + from + ".");
        }

        if (movingPiece.getColor() != currentTurn) {
            throw new IllegalMoveException("It's not your turn.");
        }

        Piece targetPiece = board.get(to);
        if (targetPiece != null && targetPiece.getColor() == movingPiece.getColor()) {
            throw new IllegalMoveException("Destination square is occupied by your own piece.");
        }

        boolean basicValid = movingPiece.isValidBasicMove(board, from, to);
        if (!basicValid) {
            throw new IllegalMoveException("That piece cannot move like that.");
        }

        Board trialBoard = board.copy();
        trialBoard.move(from, to);
        boolean leavesKingInCheck = trialBoard.isKingInCheck(currentTurn);
        if (leavesKingInCheck) {
            throw new IllegalMoveException("Move leaves your King in check.");
        }

        if (targetPiece != null && (targetPiece instanceof King)) {
            String notation = movingPiece.algebraic(from, to, targetPiece, true);
            board.move(from, to);
            moveHistory.add(notation);
            return MoveResult.CAPTURED_KING;
        }

        String notation = movingPiece.algebraic(from, to, targetPiece, false);
        board.move(from, to);
        moveHistory.add(notation);
        return MoveResult.MOVED;
    }

    private enum MoveResult {
        MOVED,
        CAPTURED_KING
    }
}