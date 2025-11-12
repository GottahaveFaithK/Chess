import chess.*;
import model.GameData;
import ui.ClientChessboard;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        //create new server facade thing.
        ClientChessboard clientChessboard = new ClientChessboard(
                new GameData(1, "W", "B", "myGame", new ChessGame()));
        clientChessboard.drawChessBoardBlack();
        clientChessboard.drawChessBoardWhite();
    }
}