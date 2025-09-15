package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    //private boolean hasMoved;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
        //this.hasMoved = false;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     * @return Collection of valid moves
     */

    //ToDo contemplate changing the movement style to a Class instead of having all the functions here
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        return switch (piece.getPieceType()) {
            case KING -> king(board, myPosition);
            case QUEEN -> queen(board, myPosition);
            case BISHOP -> diagonalMove(board, myPosition, 7);
            case KNIGHT -> knight(board, myPosition);
            case ROOK -> slideMove(board, myPosition, 7);
            case PAWN -> pawn(board, myPosition);
        };
    }

    public List<ChessMove> diagonalMove(ChessBoard board, ChessPosition myPosition, int pieceRange){
        List<ChessMove> moves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        moves.addAll(getSlides(board, myPosition, 1, 1, currentRow, currentCol, pieceRange));
        moves.addAll(getSlides(board, myPosition, -1, 1, currentRow, currentCol, pieceRange));
        moves.addAll(getSlides(board, myPosition, 1, -1, currentRow, currentCol, pieceRange));
        moves.addAll(getSlides(board, myPosition, -1, -1, currentRow, currentCol, pieceRange));
        return moves;
    }

    public List<ChessMove> slideMove(ChessBoard board, ChessPosition myPosition, int pieceRange){
        List<ChessMove> moves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        moves.addAll(getSlides(board, myPosition, 0, 1, currentRow, currentCol, pieceRange));
        moves.addAll(getSlides(board, myPosition, 0, -1, currentRow, currentCol,pieceRange));
        moves.addAll(getSlides(board, myPosition, -1, 0, currentRow, currentCol,pieceRange));
        moves.addAll(getSlides(board, myPosition, 1, 0, currentRow, currentCol,pieceRange));
        return moves;
    }

    public List<ChessMove> king(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        moves.addAll(diagonalMove(board, myPosition, 1));
        moves.addAll(slideMove(board, myPosition, 1));
        return moves;
    }

    public List<ChessMove> queen(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        moves.addAll(diagonalMove(board, myPosition, 7));
        moves.addAll(slideMove(board, myPosition, 7));
        return moves;
    }

    public List<ChessMove> knight(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        moves.addAll(getKnightMoves(board, myPosition, 1, 2,currentRow, currentCol));
        moves.addAll(getKnightMoves(board, myPosition, -1, 2,currentRow, currentCol));
        moves.addAll(getKnightMoves(board, myPosition, 1, -2,currentRow, currentCol));
        moves.addAll(getKnightMoves(board, myPosition, -1, -2,currentRow, currentCol));
        moves.addAll(getKnightMoves(board, myPosition, 2, 1,currentRow, currentCol));
        moves.addAll(getKnightMoves(board, myPosition, -2, 1,currentRow, currentCol));
        moves.addAll(getKnightMoves(board, myPosition, 2, -1,currentRow, currentCol));
        moves.addAll(getKnightMoves(board, myPosition, -2, -1,currentRow, currentCol));
        return moves;
    }

    public List<ChessMove> pawn(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        int rowChange = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 1 : -1;
        boolean isAtStart = (this.pieceColor == ChessGame.TeamColor.WHITE && currentRow == 2) ||
                (this.pieceColor == ChessGame.TeamColor.BLACK && currentRow == 7);
        int promotionRow = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 8 : 1;
        int nextRow = currentRow + rowChange;
        if(nextRow >= 1 && nextRow<= 8){
            ChessPosition newPosition = new ChessPosition(nextRow, currentCol);
            ChessPiece blockPiece = board.getPiece(newPosition);
            if (blockPiece == null){
                if(nextRow == promotionRow){
                    moves.addAll(promotePawn(myPosition, newPosition));
                } else {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
                if(isAtStart){
                    ChessPosition jumpPositon = new ChessPosition(currentRow + rowChange*2, currentCol);
                    ChessPiece jumpBlockPiece = board.getPiece(jumpPositon);
                    if(jumpBlockPiece == null){
                        moves.add(new ChessMove(myPosition, jumpPositon, null));
                    }
                }
            }
        }
        moves.addAll(pawnKillCheck(board, myPosition, rowChange, 1, currentRow, currentCol));
        moves.addAll(pawnKillCheck(board, myPosition, rowChange, -1, currentRow, currentCol));
        return moves;
    }

    public List<ChessMove> getSlides(ChessBoard board, ChessPosition myPosition, int rowChange, int colChange,
                                     int currentRow, int currentCol, int pieceRange){
        List<ChessMove> moves = new ArrayList<>();
        for(int i = 0; i < pieceRange; i++){
            currentRow += rowChange;
            currentCol += colChange;

            if(currentCol > 8 || currentCol < 1 || currentRow > 8 || currentRow < 1){
                break;
            }

            ChessPosition newPosition = new ChessPosition(currentRow, currentCol);
            ChessPiece blockPiece = board.getPiece(newPosition);

            if(blockPiece == null){
                moves.add(new ChessMove(myPosition, newPosition, null));
            } else if (blockPiece.getTeamColor() != this.pieceColor){
                moves.add(new ChessMove(myPosition, newPosition, null));
                break;
            } else {
                break;
            }
        }
        return moves;
    }

    public List<ChessMove> getKnightMoves(ChessBoard board, ChessPosition myPosition, int rowChange, int colChange, int currentRow, int currentCol){
        List<ChessMove> moves = new ArrayList<>();
        currentCol += colChange;
        currentRow += rowChange;
        if(currentCol <= 8 && currentCol >= 1 && currentRow <= 8 && currentRow >= 1){
            ChessPosition newPosition = new ChessPosition(currentRow, currentCol);
            ChessPiece blockPiece = board.getPiece(newPosition);

            if(blockPiece == null){
                moves.add(new ChessMove(myPosition, newPosition, null));
            } else if (blockPiece.getTeamColor() != this.pieceColor) {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
        return moves;
    }

    public List<ChessMove> pawnKillCheck(ChessBoard board, ChessPosition myPosition, int rowChange, int colChange, int currentRow, int currentCol){
        List<ChessMove> moves = new ArrayList<>();
        currentCol += colChange;
        currentRow += rowChange;
        int promotionRow = (this.pieceColor == ChessGame.TeamColor.WHITE) ? 8 : 1;
        if(currentCol <= 8 && currentCol >= 1 && currentRow <= 8 && currentRow >= 1){
            ChessPosition newPosition = new ChessPosition(currentRow, currentCol);
            ChessPiece blockPiece = board.getPiece(newPosition);
            if (blockPiece != null && blockPiece.getTeamColor() != this.pieceColor){
                if(currentRow == promotionRow){
                    moves.addAll(promotePawn(myPosition, newPosition));
                } else {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }
        return moves;
    }

    public List<ChessMove> promotePawn(ChessPosition myPosition, ChessPosition newPosition){
        List<ChessMove> moves = new ArrayList<>();
        moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
        moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
        moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
        moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()){ return false;}
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    @Override
    public String toString() {
        return String.format("[%s,%s]", pieceColor, type);
    }
}
