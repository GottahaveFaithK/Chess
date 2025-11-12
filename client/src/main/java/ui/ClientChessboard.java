package ui;

import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

public class ClientChessboard {

    private GameData currentGame;

    public ClientChessboard(GameData game) {
        currentGame = game;
    }

    public GameData getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(GameData game) {
        currentGame = game;
    }

    public void drawChessBoardWhite() {
        System.out.println();
        drawHeadersFooters("White");
        drawBoard(8, 8);
        System.out.println();
        drawHeadersFooters("White");
    }

    public void drawChessBoardBlack() {
        System.out.println();
        drawHeadersFooters("Black");
        drawBoard(1, 1);
        System.out.println();
        drawHeadersFooters("Black");
    }

    private void drawBoard(int startCol, int startRow) {
        String displayColor = (startCol == 8 ? EscapeSequences.SET_TEXT_COLOR_LIGHT_BLUE :
                EscapeSequences.SET_TEXT_COLOR_BLUE);
        int change = (startCol == 1 ? 1 : -1);
        for (int i = startRow; i != (startRow == 1 ? 9 : 0); i += (change)) {
            System.out.print("\n " + displayColor + i + " " + EscapeSequences.RESET_TEXT_COLOR);
            for (int j = startCol; j != (startCol == 1 ? 9 : 0); j += (change)) {
                boolean backgroundDark = ((i % 2 == 1) && (j % 2 == 1) || (i % 2 == 0) && (j % 2 == 0));
                String backgroundColor = (backgroundDark ? EscapeSequences.SET_BG_COLOR_BLUE :
                        EscapeSequences.SET_BG_COLOR_LIGHT_BLUE);
                String pieceData = getPieceChar(getCurrentGame().game().getBoard().getPiece(new ChessPosition(i, j)));
                System.out.print(backgroundColor + pieceData +
                        EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
            }
        }
    }

    private void drawHeadersFooters(String color) {
        String empty = EscapeSequences.EMPTY;
        String emptySide = EscapeSequences.EMPTYSIDE;
        if (color.equals("White")) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_LIGHT_BLUE + " " + emptySide + emptySide + "a " + emptySide
                    + "b " + emptySide + "c " + emptySide + "d " + emptySide + "e " + emptySide + "f " + emptySide
                    + "g " + emptySide + "h " + emptySide + empty);
        } else {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_BLUE + " " + emptySide + emptySide + "h " + emptySide
                    + "g " + emptySide + "f " + emptySide + "e " + emptySide + "d " + emptySide + "c " + emptySide
                    + "b " + emptySide + "a " + emptySide + empty);
        }
    }

    private String getPieceChar(ChessPiece piece) {
        if (piece == null) {
            return EscapeSequences.EMPTY;
        } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return EscapeSequences.SET_TEXT_COLOR_WHITE + whitePieceChars(piece);
        } else {
            return EscapeSequences.SET_TEXT_COLOR_BLACK + blackPieceChars(piece);
        }
    }

    private static String whitePieceChars(ChessPiece piece) {
        ChessPiece.PieceType type = piece.getPieceType();
        String myPiece = "";
        switch (type) {
            case KING -> myPiece = EscapeSequences.WHITE_KING;
            case QUEEN -> myPiece = EscapeSequences.WHITE_QUEEN;
            case BISHOP -> myPiece = EscapeSequences.WHITE_BISHOP;
            case KNIGHT -> myPiece = EscapeSequences.WHITE_KNIGHT;
            case ROOK -> myPiece = EscapeSequences.WHITE_ROOK;
            case PAWN -> myPiece = EscapeSequences.WHITE_PAWN;
        }
        return myPiece;
    }

    public String blackPieceChars(ChessPiece piece) {
        ChessPiece.PieceType type = piece.getPieceType();
        String myPiece = "";
        switch (type) {
            case KING -> myPiece = EscapeSequences.BLACK_KING;
            case QUEEN -> myPiece = EscapeSequences.BLACK_QUEEN;
            case BISHOP -> myPiece = EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> myPiece = EscapeSequences.BLACK_KNIGHT;
            case ROOK -> myPiece = EscapeSequences.BLACK_ROOK;
            case PAWN -> myPiece = EscapeSequences.BLACK_PAWN;
        }
        return myPiece;
    }
}
