package chess;

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
        //white starts. Perhaps set a variable that just changes after a turn is made?
        // Could swap it after movePiece since that is what ends a turn?
        //or maybe setTeamTurn handles that anyway
        //have setTeamTurn change a ChessGame class variable?
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
            throw new InvalidMoveException("It is not " + piece.getTeamColor() + "'s turn.");
        }

        if(move.getPromotionPiece() != null){
            piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
        }
        piece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());

        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        if(valid == null || !valid.contains(move)){
            throw new InvalidMoveException("Move unavailable (Illegal or in Check)");
        }

        myBoard.movePiece(move.getStartPosition(), move.getEndPosition(), piece);

        if(teamTurn == TeamColor.WHITE){
            teamTurn = TeamColor.BLACK;
        } else {
            teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
        //how can I tell if the king is in Check?
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
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return myBoard;
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
