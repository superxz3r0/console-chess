package chess;

abstract class Piece {

    private final Color color;
    private boolean hasMoved;

    protected Piece(Color color) {
        this.color = color;
        this.hasMoved = false;
    }

    public Color getColor() {
        return color;
    }

    public boolean hasMoved() {
        return hasMoved;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    abstract String shortName();
    abstract boolean isValidBasicMove(Board board, Position from, Position to);
    abstract boolean canAttack(Board board, Position from, Position to);

    String symbol() {
        String side = (color == Color.WHITE) ? "w" : "b";
        return side + shortName();
    }

    Piece clonePiece() {
        Piece copy;
        if (this instanceof Pawn) {
            copy = new Pawn(color);
        } else if (this instanceof Rook) {
            copy = new Rook(color);
        } else if (this instanceof Knight) {
            copy = new Knight(color);
        } else if (this instanceof Bishop) {
            copy = new Bishop(color);
        } else if (this instanceof Queen) {
            copy = new Queen(color);
        } else {
            copy = new King(color);
        }
        copy.setHasMoved(this.hasMoved);
        return copy;
    }


    String algebraic(Position from, Position to, Piece captured, boolean capturedKing) {
        String pieceName = shortName();
        String connector;
        if (captured != null) {
            if (capturedKing) {
                connector = "xK";
            } else {
                connector = "x";
            }
        } else {
            connector = "-";
        }
        return pieceName + "@" + from + connector + to;
    }
}

// ------------------- Pawn -------------------
class Pawn extends Piece {

    Pawn(Color color) {
        super(color);
    }

    @Override
    String shortName() {
        return "P";
    }

    @Override
    boolean isValidBasicMove(Board board, Position from, Position to) {
        int direction;
        int startRank;
        if (getColor() == Color.WHITE) {
            direction = 1;
            startRank = 1;
        } else {
            direction = -1;
            startRank = 6;
        }

        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        Piece target = board.get(to);

        if (dx == 0) {
            if (dy == direction && target == null) {
                return true;
            }
            if (from.getY() == startRank && dy == 2 * direction && target == null) {
                Position mid = new Position(from.getX(), from.getY() + direction);
                Piece middle = board.get(mid);
                return middle == null;
            }
            return false;
        }

        if (Math.abs(dx) == 1 && dy == direction && target != null && target.getColor() != getColor()) {
            return true;
        }

        return false;
    }

    @Override
    boolean canAttack(Board board, Position from, Position to) {
        int direction = (getColor() == Color.WHITE) ? 1 : -1;
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        return Math.abs(dx) == 1 && dy == direction;
    }
}

// ------------------- Rook -------------------
class Rook extends Piece {

    Rook(Color color) {
        super(color);
    }

    @Override
    String shortName() {
        return "R";
    }

    @Override
    boolean isValidBasicMove(Board board, Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        boolean isStraight = (dx == 0 || dy == 0);
        if (!isStraight) {
            return false;
        }

        boolean clear = board.isPathClear(from, to);
        if (!clear) {
            return false;
        }

        Piece target = board.get(to);
        return target == null || target.getColor() != getColor();
    }

    @Override
    boolean canAttack(Board board, Position from, Position to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        boolean isStraight = (dx == 0 || dy == 0);
        return isStraight && board.isPathClear(from, to);
    }
}

// ------------------- Knight -------------------
class Knight extends Piece {

    Knight(Color color) {
        super(color);
    }

    @Override
    String shortName() {
        return "N";
    }

    @Override
    boolean isValidBasicMove(Board board, Position from, Position to) {
        int dx = Math.abs(to.getX() - from.getX());
        int dy = Math.abs(to.getY() - from.getY());
        boolean correctShape = (dx == 1 && dy == 2) || (dx == 2 && dy == 1);
        if (!correctShape) {
            return false;
        }
        Piece target = board.get(to);
        return target == null || target.getColor() != getColor();
    }

    @Override
    boolean canAttack(Board board, Position from, Position to) {
        int dx = Math.abs(to.getX() - from.getX());
        int dy = Math.abs(to.getY() - from.getY());
        return (dx == 1 && dy == 2) || (dx == 2 && dy == 1);
    }
}

// ------------------- Bishop -------------------
class Bishop extends Piece {

    Bishop(Color color) {
        super(color);
    }

    @Override
    String shortName() {
        return "B";
    }

    @Override
    boolean isValidBasicMove(Board board, Position from, Position to) {
        int dx = Math.abs(to.getX() - from.getX());
        int dy = Math.abs(to.getY() - from.getY());
        boolean isDiagonal = (dx == dy);
        if (!isDiagonal) {
            return false;
        }
        boolean clear = board.isPathClear(from, to);
        if (!clear) {
            return false;
        }
        Piece target = board.get(to);
        return target == null || target.getColor() != getColor();
    }

    @Override
    boolean canAttack(Board board, Position from, Position to) {
        int dx = Math.abs(to.getX() - from.getX());
        int dy = Math.abs(to.getY() - from.getY());
        boolean isDiagonal = (dx == dy);
        return isDiagonal && board.isPathClear(from, to);
    }
}

// ------------------- Queen -------------------
class Queen extends Piece {

    Queen(Color color) {
        super(color);
    }

    @Override
    String shortName() {
        return "Q";
    }

    @Override
    boolean isValidBasicMove(Board board, Position from, Position to) {
        int dx = Math.abs(to.getX() - from.getX());
        int dy = Math.abs(to.getY() - from.getY());
        boolean isStraight = (from.getX() == to.getX()) || (from.getY() == to.getY());
        boolean isDiagonal = (dx == dy);
        if (!isStraight && !isDiagonal) {
            return false;
        }
        boolean clear = board.isPathClear(from, to);
        if (!clear) {
            return false;
        }
        Piece target = board.get(to);
        return target == null || target.getColor() != getColor();
    }

    @Override
    boolean canAttack(Board board, Position from, Position to) {
        int dx = Math.abs(to.getX() - from.getX());
        int dy = Math.abs(to.getY() - from.getY());
        boolean isStraight = (from.getX() == to.getX()) || (from.getY() == to.getY());
        boolean isDiagonal = (dx == dy);
        return (isStraight || isDiagonal) && board.isPathClear(from, to);
    }
}

// ------------------- King -------------------
class King extends Piece {

    King(Color color) {
        super(color);
    }

    @Override
    String shortName() {
        return "K";
    }

    @Override
    boolean isValidBasicMove(Board board, Position from, Position to) {
        int dx = Math.abs(to.getX() - from.getX());
        int dy = Math.abs(to.getY() - from.getY());
        boolean isOneSquare = (dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0));
        if (!isOneSquare) {
            return false;
        }
        Piece target = board.get(to);
        if (target != null && target.getColor() == getColor()) {
            return false;
        }
        Board trial = board.copy();
        trial.move(from, to);
        boolean unsafe = trial.isSquareAttacked(to, getColor().opposite());
        return !unsafe;
    }

    @Override
    boolean canAttack(Board board, Position from, Position to) {
        int dx = Math.abs(to.getX() - from.getX());
        int dy = Math.abs(to.getY() - from.getY());
        return dx <= 1 && dy <= 1 && !(dx == 0 && dy == 0);
    }
}

// ------------------- Exception -------------------
class IllegalMoveException extends Exception {
    IllegalMoveException(String message) {
        super(message);
    }
}