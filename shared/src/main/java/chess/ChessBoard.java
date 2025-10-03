package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPosition whiteKingPos;
    private ChessPosition blackKingPos;
    private boolean whiteRookKingsideMoved;
    private boolean whiteRookQueensideMoved;
    private boolean blackRookKingsideMoved;
    private boolean blackRookQueensideMoved;
    private boolean whiteKingMoved;
    private boolean blackKingMoved;
    private ChessMove lastMove;

    private final ChessPiece[][] board = new ChessPiece[8][8];
    public ChessBoard() {

    }

    public void setKingPos(ChessPosition pos, ChessGame.TeamColor color){
        if(color == ChessGame.TeamColor.WHITE){
            whiteKingPos = pos;
        } else {
            blackKingPos = pos;
        }

    }


    public ChessPosition getKingPos(ChessGame.TeamColor color){
        if(color == ChessGame.TeamColor.WHITE){
            return whiteKingPos;
        } else {
            return blackKingPos;
        }
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param pos where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition pos, ChessPiece piece) {
        board[pos.getRow()-1][pos.getColumn()-1] = piece;
    }

    public void removePiece(ChessPosition pos) {
        board[pos.getRow()-1][pos.getColumn()-1] = null;
    }

    public void movePiece(ChessPosition oldPos, ChessPosition newPos, ChessPiece piece){
        if(piece.getPieceType() == ChessPiece.PieceType.PAWN){
            if(oldPos.getColumn() != newPos.getColumn()) {
                if (getPiece(newPos) == null) {
                    int rowChange = (piece.getTeamColor() == ChessGame.TeamColor.WHITE) ? -1 : 1;
                    ChessPosition capturedPawnPos = new ChessPosition(newPos.getRow() + rowChange, newPos.getColumn());
                    removePiece(capturedPawnPos);
                }
            }
        }

        addPiece(newPos, piece);
        removePiece(oldPos);
        lastMove = new ChessMove(oldPos, newPos, null); //this doesn't track promotion history
        if(piece.getPieceType() == ChessPiece.PieceType.KING){
            setKingPos(newPos, piece.getTeamColor());
            if(piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                whiteKingMoved = true;
            } else {
                blackKingMoved = true;
            }
        }

        if(piece.getPieceType() == ChessPiece.PieceType.ROOK){
            if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                if (oldPos.equals(new ChessPosition(1, 1))) {
                    whiteRookQueensideMoved = true;
                } else if (oldPos.equals(new ChessPosition(1, 8))) {
                    whiteRookKingsideMoved = true;
                }
            } else {
                if(oldPos.equals(new ChessPosition(8,1))){
                    blackRookQueensideMoved = true;
                } else if (oldPos.equals(new ChessPosition(8,8))) {
                    blackRookKingsideMoved = true;
                }
            }
        }
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param pos The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition pos) {
        return board[pos.getRow()-1][pos.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                board[row][col] = null;
            }
        }
        for (int col = 0; col < 8; col++){
            addPiece(new ChessPosition(2, col+1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }
        for (int col = 0; col < 8; col++){
            addPiece(new ChessPosition(7, col+1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }
        addPiece(new ChessPosition(8, 1), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 8), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(8, 2), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 7), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(8, 3), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 6), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(8, 5), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(8, 4), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
        addPiece(new ChessPosition(1, 1), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 8), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
        addPiece(new ChessPosition(1, 2), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 7), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
        addPiece(new ChessPosition(1, 3), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 6), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
        addPiece(new ChessPosition(1, 5), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
        addPiece(new ChessPosition(1, 4), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
        whiteKingPos = new ChessPosition(1,5);
        blackKingPos = new ChessPosition(8,5);
        whiteRookKingsideMoved = false;
        whiteRookQueensideMoved = false;
        whiteKingMoved = false;
        blackRookKingsideMoved = false;
        blackRookQueensideMoved = false;
        blackKingMoved = false;
    }

    public void setBlackKingMoved(boolean moved) {
        blackKingMoved = moved;
    }

    public void setWhiteKingMoved(boolean moved) {
        whiteKingMoved = moved;
    }

    public void setBlackQueensideMoved(boolean moved) {
        blackRookQueensideMoved = moved;
    }

    public void setBlackKingsideMoved(boolean moved){
        blackRookKingsideMoved = moved;
    }

    public void setWhiteQueensideMoved(boolean moved){
        whiteRookQueensideMoved = moved;
    }

    public void setWhiteKingsideMoved(boolean moved){
        whiteRookKingsideMoved = moved;
    }

    public void setLastMove(ChessMove move) {
        lastMove = move;
    }

    public boolean hasBlackKingMoved() {
        return blackKingMoved;
    }

    public boolean hasWhiteKingMoved() {
        return whiteKingMoved;
    }

    public boolean hasBlackQueensideMoved() {
        return blackRookQueensideMoved;
    }

    public boolean hasBlackKingsideMoved(){
        return blackRookKingsideMoved;
    }

    public boolean hasWhiteQueensideMoved(){
        return whiteRookQueensideMoved;
    }

    public boolean hasWhiteKingsideMoved(){
        return whiteRookKingsideMoved;
    }

    public ChessMove getLastMove() {
        return lastMove;
    }

    @Override
    public String toString() {
        return Arrays.toString(board);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()){ return false; }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
