import chess.*;
import ui.ClientChessboard;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        //create new server facade thing.
        ClientChessboard clientChessboard = new ClientChessboard();
        clientChessboard.drawChessBoardBlack();
        clientChessboard.drawChessBoardWhite();
    }
}