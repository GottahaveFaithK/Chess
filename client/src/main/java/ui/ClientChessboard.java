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
        drawHeadersFooters("White");
        drawBoard(8, 8);
        System.out.println("\n");
        drawHeadersFooters("White");
    }

    public void drawChessBoardBlack() {
        drawHeadersFooters("Black");
        drawBoard(1, 1);
        System.out.println("\n");
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
                String invisibleInk = (backgroundDark ? EscapeSequences.SET_TEXT_COLOR_BLUE :
                        EscapeSequences.SET_TEXT_COLOR_LIGHT_BLUE);
                String pieceData = getTextChar(getCurrentGame().game().getBoard().getPiece(new ChessPosition(i, j)));
                System.out.print(backgroundColor + pieceData +
                        EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
            }
        }
    }


    private void drawHeadersFooters(String color) {
        if (color.equals("White")) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_LIGHT_BLUE + "    a  b  c  d  e  f  g  h    ");
        } else {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_BLUE + "    h  g  f  e  d  c  b  a    ");
        }
    }

    private String getTextChar(ChessPiece piece) {
        if (piece == null) {
            return "   ";
        } else if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return EscapeSequences.SET_TEXT_COLOR_WHITE + whiteTextChars(piece);
        } else {
            return EscapeSequences.SET_TEXT_COLOR_BLACK + blackTextChars(piece);
        }
    }

    private String getPieceChar(ChessPiece piece, String invisibleInk) {
        if (piece == null) {
            return invisibleInk + EscapeSequences.WHITE_PAWN;
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

    public String whiteTextChars(ChessPiece piece) {
        ChessPiece.PieceType type = piece.getPieceType();
        String myPiece = "";
        switch (type) {
            case KING -> myPiece = " K ";
            case QUEEN -> myPiece = " Q ";
            case BISHOP -> myPiece = " B ";
            case KNIGHT -> myPiece = " N ";
            case ROOK -> myPiece = " R ";
            case PAWN -> myPiece = " P ";
        }
        return myPiece;
    }

    public String blackTextChars(ChessPiece piece) {
        ChessPiece.PieceType type = piece.getPieceType();
        String myPiece = "";
        switch (type) {
            case KING -> myPiece = " k ";
            case QUEEN -> myPiece = " q ";
            case BISHOP -> myPiece = " b ";
            case KNIGHT -> myPiece = " n ";
            case ROOK -> myPiece = " r ";
            case PAWN -> myPiece = " p ";
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
