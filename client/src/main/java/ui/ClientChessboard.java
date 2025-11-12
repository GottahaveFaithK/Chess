package ui;

import chess.ChessGame;
import chess.ChessPiece;

public class ClientChessboard {

    public void drawChessboardWhite() {
        //draw chessboard from white's perspective
        drawHeadersFooters("White");
        drawBoard(1, 1); //do I want to give it a row and col or just a string
        drawHeadersFooters("White");
    }

    public void drawChessBoardBlack() {
        //draw chessboard from black's perspective
        //flippedRow = 8 - row + 1
        //flippedCol = 8 - col + 1
        drawHeadersFooters("Black");
        drawBoard(8, 8); //do I want to give it a row and col or just a string
        drawHeadersFooters("Black");
    }

    private void drawBoard(int startCol, int startRow) {
        //iterate through rows and columns
        //checkerboard pattern
        //get piece and draw it

    }

    private void drawHeadersFooters(String color) {
        if (color.equals("White")) {
            System.out.println("    a  b  c  d  e  f  g  h    ");
        } else {
            System.out.println("    h  g  f  e  d  c  b  a    ");
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
