package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static ui.EscapeSequences.SET_BG_COLOR_LIGHT_GREY;
import static ui.EscapeSequences.SET_BG_COLOR_YELLOW;

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

    public void drawChessBoardWhite(ChessPosition highlight) {
        System.out.println();
        drawHeadersFooters("White");
        drawBoard(1, 8, highlight);
        System.out.println();
        drawHeadersFooters("White");
    }

    public void drawChessBoardBlack(ChessPosition highlight) {
        System.out.println();
        drawHeadersFooters("Black");
        drawBoard(8, 1, highlight);
        System.out.println();
        drawHeadersFooters("Black");
    }

    private void drawBoard(int startCol, int startRow, ChessPosition highlight) {
        String displayColor = (startRow == 8 ? EscapeSequences.SET_TEXT_COLOR_LIGHT_BLUE :
                EscapeSequences.SET_TEXT_COLOR_BLUE);
        int change = (startRow == 1 ? 1 : -1);
        int colChange = (startCol == 1 ? 1 : -1);

        Set<ChessPosition> highlightMoves = Set.of();
        ;
        if (highlight != null) {
            highlightMoves = getHighlightSquares(highlight);
        }

        for (int i = startRow; i != (startRow == 1 ? 9 : 0); i += (change)) {
            System.out.print("\n " + displayColor + i + " " + EscapeSequences.RESET_TEXT_COLOR);
            for (int j = startCol; j != (startCol == 1 ? 9 : 0); j += (colChange)) {
                boolean backgroundDark =
                        ((i % 2 == 1) && (j % 2 == 1) || (i % 2 == 0) && (j % 2 == 0));
                String backgroundColor;
                if (highlight != null && highlightMoves.contains(new ChessPosition(i, j))) {
                    backgroundColor = (backgroundDark ? EscapeSequences.SET_BG_COLOR_DARK_GREEN :
                            EscapeSequences.SET_BG_COLOR_GREEN);
                } else {
                    backgroundColor = (backgroundDark ? EscapeSequences.SET_BG_COLOR_BLUE :
                            EscapeSequences.SET_BG_COLOR_LIGHT_BLUE);
                }
                if (highlight != null && highlight.equals(new ChessPosition(i, j))) {
                    backgroundColor = SET_BG_COLOR_LIGHT_GREY;
                }
                String pieceData = getPieceChar(getCurrentGame().game().getBoard().getPiece(new ChessPosition(i, j)));
                System.out.print(backgroundColor + pieceData +
                        EscapeSequences.RESET_BG_COLOR + EscapeSequences.RESET_TEXT_COLOR);
            }
        }
    }

    private Set<ChessPosition> getHighlightSquares(ChessPosition highlightPiece) {
        Collection<ChessMove> validMoves = currentGame.game().validMoves(highlightPiece);
        Set<ChessPosition> destinations = new HashSet<>();
        for (ChessMove move : validMoves) {
            destinations.add(move.getEndPosition());
        }
        return destinations;
    }

    public void highlight(ChessPosition piece, String playerColor) {
        if ("black".equalsIgnoreCase(playerColor)) {
            drawChessBoardBlack(piece);
        } else {
            drawChessBoardWhite(piece);
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

    public String getPieceChar(ChessPiece piece) {
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
