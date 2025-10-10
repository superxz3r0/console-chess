package chess;

import java.util.Scanner;

public class Game {
    private final Board board = new Board();
    private String whitePlayer = "White";
    private String blackPlayer = "Black";
    private Color toMove = Color.WHITE;

    public void start() {
        try (Scanner sc = new Scanner(System.in)) {
            System.out.print("Enter White player's name: ");
            String w = sc.nextLine().trim();
            if (!w.isEmpty()) whitePlayer = w;

            System.out.print("Enter Black player's name: ");
            String b = sc.nextLine().trim();
            if (!b.isEmpty()) blackPlayer = b;

            System.out.println();
            System.out.println("Players: " + whitePlayer + " (White) vs " + blackPlayer + " (Black)");
            System.out.println();
            showBoardAndTurn();
            System.out.println("(Move input coming next — e.g., e2e4, and 'quit' to exit.)");
        }
    }

    private void showBoardAndTurn() {
        System.out.println(board);
        System.out.println("Current turn: " + (toMove == Color.WHITE ? whitePlayer : blackPlayer)
                + " (" + toMove + ")");
    }
}
