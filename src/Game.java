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

    public Game(Scanner in){ this.in = in; this.board = Board.standardSetup(); }

    public void run(){
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
            if (cmd.equalsIgnoreCase("q") || cmd.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye.");
                return;
            }
            if (cmd.equalsIgnoreCase("hint")) { printHelp(); continue; }
            if (cmd.startsWith("pip")) { handlePip(cmd); continue; }

            if (cmd.length()==4) {
                String fromStr = cmd.substring(0,2);
                String toStr   = cmd.substring(2,4);
                try {
                    Position from = Position.fromAlgebraic(fromStr);
                    Position to   = Position.fromAlgebraic(toStr);
                    if (!Board.inBounds(from.getX(), from.getY()) || !Board.inBounds(to.getX(), to.getY())) {
                        System.out.println("Invalid square."); continue;
                    }
                    if (!board.isLegalMove(from, to, turn)) {
                        System.out.println("Illegal move. Type 'pip e2' to see moves from e2."); continue;
                    }

                    Board.MoveResult res = board.move(from, to, turn);

                    String promoSuffix = "";
                    if (board.isPromotionPending(to)) {
                        PieceType choice = askPromotion(turn);
                        board.promote(to, choice);
                        switch (choice) {
                            case QUEEN:  promoSuffix = "=Q"; break;
                            case ROOK:   promoSuffix = "=R"; break;
                            case BISHOP: promoSuffix = "=B"; break;
                            case KNIGHT: promoSuffix = "=N"; break;
                            default:     promoSuffix = "=Q";
                        }
                    }

                    String notation = fromStr + (res.wasCapture ? "x" : "") + toStr + promoSuffix + (res.gaveCheck ? "+" : "");
                    history.add(notation);

                    if (res.capturedKing) {
                        board.print(turn, history);
                        System.out.println("King captured — " + nameOf(turn) + " wins!");
                        return;
                    }

                    turn = turn.opposite();
                } catch (IllegalArgumentException | IllegalMoveException ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
                continue;
            }

            System.out.println("Unknown command. Type 'hint' for help.");
        }
    }

    private void handlePip(String cmd) {
        String[] parts = cmd.split("\\s+");
        if (parts.length != 2) {
            System.out.println("Usage: pip <square>. Example: pip e2");
            return;
        }
        try {
            Position from = Position.fromAlgebraic(parts[1]);
            List<String> moves = board.legalMovesFrom(from, turn);
            if (moves.isEmpty()) System.out.println("No legal moves from " + parts[1] + " for " + nameOf(turn) + ".");
            else System.out.println("Legal moves: " + String.join(" ", moves));
        } catch (IllegalArgumentException e) {
            System.out.println("Bad square.");
        }
    }

    private String nameOf(Color c){ return c==Color.WHITE ? whiteName : blackName; }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println("  e2e4    Make a move (from-to).");
        System.out.println("  pip e2  List legal moves from a square for the side to move.");
        System.out.println("  hint    Show this help.");
        System.out.println("  q       Quit.");
    }

    private PieceType askPromotion(Color color) {
        while (true) {
            System.out.print(nameOf(color) + " promotion (q/r/b/n): ");
            String s = in.nextLine().trim().toLowerCase();
            switch (s) {
                case "q": return PieceType.QUEEN;
                case "r": return PieceType.ROOK;
                case "b": return PieceType.BISHOP;
                case "n": return PieceType.KNIGHT;
                default: System.out.println("Please type q, r, b, or n.");
            }
        }
    }
}
