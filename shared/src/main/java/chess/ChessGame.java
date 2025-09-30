package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    public ChessGame() {
        teamTurn = TeamColor.WHITE;
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

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
        //what if I made a new board to simulate each move and check every opponents move against the king??
        //discard any moves that put me in check/checkmate
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        ChessPiece piece = myBoard.getPiece(move.getStartPosition());

        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException("It is " + piece.getTeamColor() + "'s turn.");
        }

        if(move.getPromotionPiece() != null){
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }

        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        if(valid == null || !valid.contains(move)){
            throw new InvalidMoveException("Move unavailable (Illegal or in Check)");
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
                ChessPosition myPos = new ChessPosition(row+1, col+1);
                ChessPiece piece = myBoard.getPiece(myPos);
                if(piece != null && piece.getTeamColor() != teamColor){
                    Collection<ChessMove> possibleMoves = piece.pieceMoves(myBoard, myPos);
                    for (ChessMove move : possibleMoves) {
                        if (move.getEndPosition().equals(kingPos)) {
                            return true;
                        }
                    }
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
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
        //not as bad as I thought. It's: my king is not currently in check, but I have zero moves that wouldn't put it in check
        //basically: every move is invalid
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
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return myBoard;
    }

    private void setKingStartPos(TeamColor color){
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                ChessPosition myPos = new ChessPosition(row+1, col+1);
                ChessPiece piece = myBoard.getPiece(myPos);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == color){
                    myBoard.setKingPos(myPos, color);
                }
            }
        }
    }

    private ChessBoard createTestBoard(){
        ChessBoard testBoard = new ChessBoard();

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

    //note, if I add more variables I will need to update these
    @Override
    public String toString() {
        return String.format("%s%s", teamTurn, myBoard);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(myBoard, chessGame.myBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, myBoard);
    }
}
