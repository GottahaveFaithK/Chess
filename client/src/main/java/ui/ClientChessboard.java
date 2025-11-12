package ui;

import chess.ChessGame;
import chess.ChessPiece;

public class ClientChessboard {

    public void drawChessBoardWhite() {
        //draw chessboard from white's perspective
        drawHeadersFooters("White");
        drawBoard(1, 1);
        System.out.println("\n");
        drawHeadersFooters("White");
    }

    public void drawChessBoardBlack() {
        //draw chessboard from black's perspective
        //flippedRow = 8 - row + 1
        //flippedCol = 8 - col + 1
        drawHeadersFooters("Black");
        drawBoard(8, 8);
        System.out.println("\n");
        drawHeadersFooters("Black");
    }

    private void drawBoard(int startCol, int startRow) {
        //iterate through rows and columns
        //checkerboard pattern
        //get piece and draw it
        int change = (startCol == 1 ? 1 : -1);
        for (int i = startRow; i != (startRow == 1 ? 9 : 0); i += (change)) {
            int displayRow = (9 - i);
            System.out.print("\n " + displayRow + " ");
            for (int j = startCol; j != (startCol == 1 ? 9 : 0); j += (change)) {
                boolean backgroundDark = ((i % 2 == 1) && (j % 2 == 1) || (i % 2 == 0) && (j % 2 == 0));
                String backgroundColor = (backgroundDark ? EscapeSequences.SET_BG_COLOR_BLUE :
                        EscapeSequences.SET_BG_COLOR_LIGHT_BLUE);
                System.out.print(backgroundColor + EscapeSequences.SET_TEXT_COLOR_WHITE + " a "
                        + EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
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

    private String getPieceChar(ChessPiece piece) {
        if (piece.getPieceType() == null) {
            return "   ";
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
