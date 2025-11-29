package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor teamTurn;
    private ChessBoard myBoard;
    private Winner winner;

    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        winner = Winner.NONE;
        myBoard = new ChessBoard();
        myBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public enum Winner {
        NONE,
        WHITE,
        BLACK,
        STALEMATE
    }

    public void setWinner(Winner winner) {
        this.winner = winner;
    }

    public Winner getWinner() {
        return winner;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessBoard testBoard = createTestBoard();
        ChessPiece piece = testBoard.getPiece(startPosition);
        Collection<ChessMove> validMoveList = new ArrayList<>();
        Collection<ChessMove> moveList = piece.pieceMoves(testBoard, startPosition);
        chess.ChessGame.TeamColor pieceColor = piece.getTeamColor();
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN) {
            moveList.addAll(getEnPassantMoves(startPosition, piece));
        }
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            moveList.addAll(getCastlingMoves(startPosition, piece));
        }
        for (ChessMove move : moveList) {
            testBoard = createTestBoard();
            piece = testBoard.getPiece(startPosition);
            if (move.getPromotionPiece() != null) {
                piece = new ChessPiece(pieceColor, move.getPromotionPiece());
            }
            testBoard.movePiece(move.getStartPosition(), move.getEndPosition(), piece);
            ChessBoard originalBoard = myBoard;
            myBoard = testBoard;
            if (!isInCheck(piece.getTeamColor())) {
                validMoveList.add(move);
            }
            myBoard = originalBoard;
        }
        return validMoveList;

    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        ChessPiece piece = myBoard.getPiece(move.getStartPosition());

        if (piece == null) {
            throw new InvalidMoveException("There is no piece at " + move.getStartPosition());
        }

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("It is " + piece.getTeamColor() + "'s turn.");
        }

        if (move.getPromotionPiece() != null) {
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }

        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        if (valid == null || !valid.contains(move)) {
            throw new InvalidMoveException("Invalid move");
        }

        myBoard.movePiece(move.getStartPosition(), move.getEndPosition(), piece);

        teamTurn = (piece.getTeamColor() == TeamColor.WHITE) ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = myBoard.getKingPos(teamColor);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition myPos = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = myBoard.getPiece(myPos);
                if (checkMoves(piece, myPos, kingPos, teamColor)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkMoves(ChessPiece piece, ChessPosition myPos, ChessPosition kingPos, TeamColor teamColor) {
        if (piece != null && piece.getTeamColor() != teamColor) {
            Collection<ChessMove> possibleMoves = piece.pieceMoves(myBoard, myPos);
            for (ChessMove move : possibleMoves) {
                if (move.getEndPosition().equals(kingPos)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && noValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */

    //for is in stalemate, maybe eventually store past boards and implement the 3 turn rule or something?
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && noValidMoves(teamColor);
    }

    private boolean noValidMoves(TeamColor teamColor) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition myPos = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = myBoard.getPiece(myPos);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(myPos);
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        myBoard = board;
        setKingStartPos(TeamColor.WHITE);
        setKingStartPos(TeamColor.BLACK);
        setHasMoved();
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return myBoard;
    }

    private void setKingStartPos(TeamColor color) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition myPos = new ChessPosition(row + 1, col + 1);
                ChessPiece piece = myBoard.getPiece(myPos);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == color) {
                    myBoard.setKingPos(myPos, color);
                }
            }
        }
    }

    private void setHasMoved() {
        ChessPiece piece = myBoard.getPiece(new ChessPosition(1, 8));
        myBoard.setWhiteKingsideMoved(piece == null || piece.getPieceType() != ChessPiece.PieceType.ROOK || piece.getTeamColor() != TeamColor.WHITE);
        piece = myBoard.getPiece(new ChessPosition(1, 1));
        myBoard.setWhiteQueensideMoved(piece == null || piece.getPieceType() != ChessPiece.PieceType.ROOK || piece.getTeamColor() != TeamColor.WHITE);
        piece = myBoard.getPiece(new ChessPosition(8, 8));
        myBoard.setBlackKingsideMoved(piece == null || piece.getPieceType() != ChessPiece.PieceType.ROOK || piece.getTeamColor() != TeamColor.BLACK);
        piece = myBoard.getPiece(new ChessPosition(8, 1));
        myBoard.setBlackQueensideMoved(piece == null || piece.getPieceType() != ChessPiece.PieceType.ROOK || piece.getTeamColor() != TeamColor.BLACK);
        piece = myBoard.getPiece(new ChessPosition(1, 5));
        myBoard.setWhiteKingMoved(piece == null || piece.getPieceType() != ChessPiece.PieceType.KING || piece.getTeamColor() != TeamColor.WHITE);
        piece = myBoard.getPiece(new ChessPosition(8, 5));
        myBoard.setBlackKingMoved(piece == null || piece.getPieceType() != ChessPiece.PieceType.KING || piece.getTeamColor() != TeamColor.BLACK);
    }

    private ChessBoard createTestBoard() {
        ChessBoard testBoard = new ChessBoard();
        testBoard.setKingPos(myBoard.getKingPos(TeamColor.WHITE), TeamColor.WHITE);
        testBoard.setKingPos(myBoard.getKingPos(TeamColor.BLACK), TeamColor.BLACK);
        testBoard.setLastMove(myBoard.getLastMove());
        testBoard.setWhiteKingMoved(myBoard.hasWhiteKingMoved());
        testBoard.setBlackKingMoved(myBoard.hasBlackKingMoved());
        testBoard.setWhiteKingsideMoved(myBoard.hasWhiteKingsideMoved());
        testBoard.setWhiteQueensideMoved(myBoard.hasWhiteQueensideMoved());
        testBoard.setBlackKingsideMoved(myBoard.hasBlackKingsideMoved());
        testBoard.setBlackQueensideMoved(myBoard.hasBlackQueensideMoved());
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = myBoard.getPiece(pos);

                if (piece != null) {
                    ChessPiece copyPiece = new ChessPiece(piece.getTeamColor(), piece.getPieceType());
                    testBoard.addPiece(pos, copyPiece);
                }
            }
        }

        testBoard.setKingPos(myBoard.getKingPos(TeamColor.WHITE), TeamColor.WHITE);
        testBoard.setKingPos(myBoard.getKingPos(TeamColor.BLACK), TeamColor.BLACK);
        return testBoard;
    }

    private Collection<ChessMove> getCastlingMoves(ChessPosition pos, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        int row = pos.getRow();
        if (piece.getTeamColor() == TeamColor.WHITE) {
            if (!myBoard.hasWhiteKingMoved() && !myBoard.hasWhiteKingsideMoved()) {
                moves.addAll(castleKingside(piece, pos, row));
            }
            if (!myBoard.hasWhiteKingMoved() && !myBoard.hasWhiteQueensideMoved()) {
                moves.addAll(castleQueenside(piece, pos, row));
            }
        } else {
            if (!myBoard.hasBlackKingMoved() && !myBoard.hasBlackKingsideMoved()) {
                moves.addAll(castleKingside(piece, pos, row));
            }

            if (!myBoard.hasBlackKingMoved() && !myBoard.hasBlackQueensideMoved()) {
                moves.addAll(castleQueenside(piece, pos, row));
            }
        }

        return moves;
    }

    private Collection<ChessMove> castleKingside(ChessPiece piece, ChessPosition pos, int row) {
        Collection<ChessMove> moves = new ArrayList<>();
        if (myBoard.getPiece(new ChessPosition(row, 6)) == null &&
                myBoard.getPiece(new ChessPosition(row, 7)) == null) {
            if (isCastlingSafe(pos, new ChessPosition(row, 7), piece.getTeamColor())) {
                moves.add(new ChessMove(pos, new ChessPosition(row, 7), null));
            }
        }
        return moves;
    }

    private Collection<ChessMove> castleQueenside(ChessPiece piece, ChessPosition pos, int row) {
        Collection<ChessMove> moves = new ArrayList<>();
        if (myBoard.getPiece(new ChessPosition(row, 4)) == null &&
                myBoard.getPiece(new ChessPosition(row, 3)) == null &&
                myBoard.getPiece(new ChessPosition(row, 2)) == null) {
            if (isCastlingSafe(pos, new ChessPosition(row, 3), piece.getTeamColor())) {
                moves.add(new ChessMove(pos, new ChessPosition(row, 3), null));
            }
        }
        return moves;
    }

    private boolean isCastlingSafe(ChessPosition oldPos, ChessPosition newPos, TeamColor color) {
        int startCol = oldPos.getColumn();
        int endCol = newPos.getColumn();
        int step = (endCol > startCol) ? 1 : -1;

        for (int col = startCol; col != endCol + step; col += step) {
            ChessBoard testBoard = createTestBoard();
            ChessPiece piece = testBoard.getPiece(oldPos);
            ChessPosition testPos = new ChessPosition(oldPos.getRow(), col);
            testBoard.movePiece(oldPos, testPos, piece);
            ChessBoard originalBoard = myBoard;
            myBoard = testBoard;
            boolean inCheck = isInCheck(color);
            myBoard = originalBoard;

            if (inCheck) {
                return false;
            }
        }
        return true;
    }

    private Collection<ChessMove> getEnPassantMoves(ChessPosition pos, ChessPiece piece) {
        Collection<ChessMove> moves = new ArrayList<>();
        ChessMove lastMove = myBoard.getLastMove();
        if (lastMove == null) {
            return moves;
        }
        ChessPosition lastPosStart = lastMove.getStartPosition();
        ChessPosition lastPosEnd = lastMove.getEndPosition();
        if (myBoard.getPiece(lastPosEnd).getPieceType() == ChessPiece.PieceType.PAWN) {
            int rowChange = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? 1 : -1;
            if (Math.abs(lastPosStart.getRow() - lastPosEnd.getRow()) == 2) {
                if (Math.abs(lastPosEnd.getColumn() - pos.getColumn()) == 1 &&
                        lastPosEnd.getRow() == pos.getRow()) {
                    ChessPosition capturePos = new ChessPosition(pos.getRow() + rowChange, lastPosEnd.getColumn());
                    moves.add(new ChessMove(pos, capturePos, null));
                }
            }
        }
        return moves;
    }

    //note, if I add more variables I will need to update these
    @Override
    public String toString() {
        return String.format("%s%s", teamTurn, myBoard);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(myBoard, chessGame.myBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, myBoard);
    }
}
