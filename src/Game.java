package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {
    private final Scanner in;
    private Board board;
    private Color turn = Color.WHITE;
    private String whiteName = "White";
    private String blackName = "Black";
    private final List<String> history = new ArrayList<>();

    // wire up scanner + fresh board
    public Game(Scanner in) {
        this.in = in;
        this.board = Board.standardSetup();
    }

    // main loop
    public void run() {
        System.out.println("== Console Chess ==");
        System.out.print("Enter White player name: ");
        String w = in.nextLine().trim();
        if (!w.isEmpty()) whiteName = w;
        System.out.print("Enter Black player name: ");
        String b = in.nextLine().trim();
        if (!b.isEmpty()) blackName = b;

        while (true) {
            board.print(turn, history);
            System.out.print(nameOf(turn) + " to move > ");
            String cmd = in.nextLine().trim();

            // commands
            if (cmd.equalsIgnoreCase("q") || cmd.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye.");
                return;
            }
            if (cmd.equalsIgnoreCase("help") || cmd.equalsIgnoreCase("?")) {
                printHelp();
                continue;
            }
            if (cmd.equalsIgnoreCase("hint")) {
                showAllLegalMovesForCurrentPlayer();
                continue;
            }
            if (cmd.equalsIgnoreCase("resign") || cmd.equalsIgnoreCase("r")) {
                System.out.println(nameOf(turn) + " resigns. " + nameOf(turn.opposite()) + " wins!");
                return;
            }
            if (cmd.startsWith("pip")) {
                handlePip(cmd);
                continue;
            }

            // allow "O-O"/"O-O-O" or "0-0"/"0-0-0"
            if (cmd.equalsIgnoreCase("o-o") || cmd.equalsIgnoreCase("0-0")) {
                cmd = castleToMoveString(true);
            } else if (cmd.equalsIgnoreCase("o-o-o") || cmd.equalsIgnoreCase("0-0-0")) {
                cmd = castleToMoveString(false);
            }

            // moves like "e2e4"
            if (cmd.length() == 4) {
                String fromStr = cmd.substring(0, 2);
                String toStr   = cmd.substring(2, 4);
                try {
                    Position from = Position.fromAlgebraic(fromStr);
                    Position to   = Position.fromAlgebraic(toStr);

                    if (!Board.inBounds(from.getX(), from.getY()) || !Board.inBounds(to.getX(), to.getY())) {
                        System.out.println("Invalid square.");
                        continue;
                    }
                    if (!board.isLegalMove(from, to, turn)) {
                        System.out.println("Illegal move. Try 'hint' or 'pip e2'.");
                        continue;
                    }

                    Board.MoveResult res = board.move(from, to, turn);

                    // promotion prompt
                    String promoSuffix = "";
                    if (board.isPromotionPending(to)) {
                        PieceType choice = askPromotion(turn);
                        board.promote(to, choice);
                        promoSuffix = switch (choice) {
                            case QUEEN -> "=Q";
                            case ROOK -> "=R";
                            case BISHOP -> "=B";
                            case KNIGHT -> "=N";
                            default -> "=Q";
                        };
                    }

                    // minimal notation
                    String notation = fromStr + (res.wasCapture ? "x" : "") + toStr + promoSuffix + (res.gaveCheck ? "+" : "");
                    history.add(notation);

                    // checkmate?
                    Color opp = turn.opposite();
                    boolean oppInCheck = board.isKingInCheck(opp);
                    boolean oppHasMove = board.hasAnyLegalMove(opp);
                    if (oppInCheck && !oppHasMove) {
                        board.print(turn, history);
                        System.out.println("Checkmate — " + nameOf(turn) + " wins!");
                        return;
                    }

                    // swap turns
                    turn = opp;
                } catch (IllegalArgumentException | IllegalMoveException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
                continue;
            }

            System.out.println("Unknown command. Type 'help' to see all commands.");
        }
    }

    // list every legal move for the side to move
    private void showAllLegalMovesForCurrentPlayer() {
        List<String> all = new ArrayList<>();
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                Position from = new Position(x, y);
                Piece p = board.get(from);
                if (p == null || p.getColor() != turn) continue;
                all.addAll(board.legalMovesFrom(from, turn));
            }
        }
        if (all.isEmpty()) {
            System.out.println("No legal moves for " + nameOf(turn) + ".");
        } else {
            all.sort(String::compareTo);
            System.out.println(nameOf(turn) + " legal moves (" + all.size() + "):");
            System.out.println(String.join(" ", all));
        }
    }

    // "pip e2" -> show legal moves from that square
    private void handlePip(String cmd) {
        String[] parts = cmd.split("\\s+");
        if (parts.length != 2) {
            System.out.println("Usage: pip <square> (e.g. pip e2)");
            return;
        }
        try {
            Position from = Position.fromAlgebraic(parts[1]);
            List<String> moves = board.legalMovesFrom(from, turn);
            if (moves.isEmpty()) {
                System.out.println("No legal moves from " + parts[1] + " for " + nameOf(turn) + ".");
            } else {
                moves.sort(String::compareTo);
                System.out.println("Legal moves from " + parts[1] + ": " + String.join(" ", moves));
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Bad square.");
        }
    }

    // map castle commands to actual squares for current side
    private String castleToMoveString(boolean kingSide) {
        int y = (turn == Color.WHITE) ? 0 : 7;
        Position from = new Position(4, y);
        Position to = new Position(kingSide ? 6 : 2, y);
        return from.toString() + to.toString();
    }

    // show command list
    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  e2e4      Make a move (from-to).");
        System.out.println("  hint      Show ALL legal moves for the current player.");
        System.out.println("  pip e2    List legal moves from a specific square.");
        System.out.println("  o-o       Castle kingside (or e1g1 / e8g8).");
        System.out.println("  o-o-o     Castle queenside (or e1c1 / e8c8).");
        System.out.println("  resign    Resign the game (alias: r).");
        System.out.println("  help      Show commands (alias: ?).");
        System.out.println("  q         Quit.");
    }

    // ask for promotion piece (q/r/b/n)
    private PieceType askPromotion(Color color) {
        while (true) {
            System.out.print(nameOf(color) + " promotion (q/r/b/n): ");
            String s = in.nextLine().trim().toLowerCase();
            switch (s) {
                case "q": return PieceType.QUEEN;
                case "r": return PieceType.ROOK;
                case "b": return PieceType.BISHOP;
                case "n": return PieceType.KNIGHT;
                default:  System.out.println("Please type q, r, b, or n.");
            }
        }
    }

    // nice names
    private String nameOf(Color c) { return c == Color.WHITE ? whiteName : blackName; }
}
