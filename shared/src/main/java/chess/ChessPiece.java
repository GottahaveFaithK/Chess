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

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
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
     *if (piece.getPieceType() == PieceType.BISHOP){
     *             return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
     *         }
     *         return List.of();
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        switch (piece.getPieceType()){
            //each piece needs to return a list of ChessMove objects. ChessMove contains 2 chessPosition objects (start and finish) and promotion type
            case KING:
                return king(board, myPosition);
            case QUEEN:
                List<ChessMove> diagonalMoves = diagonalMove(board, myPosition);
                List<ChessMove> slideMoves = slideMove(board, myPosition);
                List<ChessMove> queenMoves = new ArrayList<>(diagonalMoves);
                queenMoves.addAll(slideMoves);
                return queenMoves;
            case BISHOP:
                return diagonalMove(board, myPosition);
            case KNIGHT:
                return knight(board, myPosition);
            case ROOK:
                return slideMove(board, myPosition);
            case PAWN:
                return pawn(board, myPosition);
            default:
                throw new IllegalArgumentException("Invalid input in switch statement, go debug the switch statement");
        }
    }

    public List<ChessMove> diagonalMove(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        moves.addAll(getDiagonals(board, myPosition, +1, +1, currentRow, currentCol));
        moves.addAll(getDiagonals(board, myPosition, -1, +1, currentRow, currentCol));
        moves.addAll(getDiagonals(board, myPosition, +1, -1, currentRow, currentCol));
        moves.addAll(getDiagonals(board, myPosition, -1, -1, currentRow, currentCol));
        return moves;
    }

    public List<ChessMove> slideMove(ChessBoard board, ChessPosition myPosition){
        return List.of();
    }

    public List<ChessMove> king(ChessBoard board, ChessPosition myPosition){
        return List.of();
    }

    public List<ChessMove> pawn(ChessBoard board, ChessPosition myPosition){
        return List.of();
    }

    public List<ChessMove> knight(ChessBoard board, ChessPosition myPosition){
        return List.of();
    }

    public List<ChessMove> getDiagonals(ChessBoard board, ChessPosition myPosition, int rowChange, int colChange, int currentRow, int currentCol){
        List<ChessMove> moves = new ArrayList<>();
        while(true){
            currentRow += rowChange;
            currentCol += colChange;

            if(currentCol < 1 || currentCol > 8 || currentRow > 8 || currentRow < 1){
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
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
