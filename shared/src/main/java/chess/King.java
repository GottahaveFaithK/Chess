package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class King extends ChessPiece{
    public King(ChessGame.TeamColor color){
        super(color, PieceType.KING);
    }

    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition){
        List<ChessMove> moves = new ArrayList<>();
        moves.addAll(diagonalMove(board, myPosition, 1));
        moves.addAll(slideMove(board, myPosition, 1));
        return moves;
    }
}
