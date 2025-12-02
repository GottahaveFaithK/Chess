package ui;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import chessclient.ChessClient;
import chessclient.ClientException;
import chessclient.ServerFacade;
import websocket.WebsocketFacade;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import static ui.Formatting.*;
import static ui.Formatting.ERROR_TEXT;
import static ui.Formatting.RESET;

public class GameplayUI implements UIState {
    ChessClient client;
    ServerFacade server;
    String playerColor;
    int gameID;
    WebsocketFacade ws;

    public GameplayUI(ChessClient client, ServerFacade server, String color, int gameID, WebsocketFacade ws) {
        this.client = client;
        this.server = server;
        playerColor = color;
        this.gameID = gameID;
        this.ws = ws;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    @Override
    public String handle(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String selection = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (selection) {
            case "redraw" -> redraw();
            case "leave" -> leave();
            case "move" -> move(params);
            case "resign" -> resign();
            case "highlight" -> highlight(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String printPrompt() {
        return "\n" + BLUE_TEXT + "[IN_GAME] >>> ";
    }

    public String redraw() {
        if ("black".equalsIgnoreCase(playerColor)) {
            client.getBoard().drawChessBoardBlack(null);
        } else {
            client.getBoard().drawChessBoardWhite(null);
        }
        return "\nRedrew Board\n";
    }

    public String leave() {
        ws.leave(client.getAuthToken(), gameID);
        client.setState(new SignedInUI(client, server, ws));
        return "\nLeft the Game";
    }

    public String move(String... params) {
        if (params.length != 2 & params.length != 3) {
            return ERROR_TEXT + "Expected: move <piece position> <new position>" + RESET;
        }

        ChessPiece.PieceType promotionType = null;

        if (params.length == 3) {
            try {
                promotionType = evalPromotion(params[2]);
            } catch (ClientException ex) {
                return ERROR_TEXT + "Incorrect promotion type. \n Options are Queen, Bishop, Knight, Rook";
            }
        }

        ChessPosition startPos;
        ChessPosition endPos;

        try {
            startPos = calculateChesPos(params[0]);
            endPos = calculateChesPos(params[1]);
        } catch (ClientException e) {
            return ERROR_TEXT + "Incorrect move. Example move: h8 h7" + RESET;
        }

        ChessMove myMove = new ChessMove(startPos, endPos, promotionType);
        ws.makeMove(client.getAuthToken(), gameID, myMove);
        return "making move...";
    }

    ChessPiece.PieceType evalPromotion(String piece) {
        return switch (piece.toLowerCase()) {
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            case "rook" -> ChessPiece.PieceType.ROOK;
            default -> throw new ClientException("Incorrect piece type", 0);
        };
    }

    ChessPosition calculateChesPos(String move) throws ClientException {
        if (move.length() != 2) {
            throw new ClientException("incorrect move", 0);
        }

        char col = Character.toLowerCase(move.charAt(0));
        char row = move.charAt(1);

        if (col < 'a' || col > 'h') {
            throw new ClientException("File out of range: ", 0);
        }
        if (row < '1' || row > '8') {
            throw new ClientException("File out of range: ", 0);
        }

        int y = col - 'a' + 1;
        int x = row - '0';

        return new ChessPosition(x, y);

    }

    public String resign() {
        System.out.println("You are about to resign. Please type \"yes\" to confirm");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        if (line.equalsIgnoreCase("yes")) {
            ws.resign(client.getAuthToken(), gameID);
        }
        return "Response = " + line;
    }

    public String highlight(String... params) {
        ChessPosition highlightPiece;
        try {
            highlightPiece = new ChessPosition(Integer.parseInt(String.valueOf(params[0].toLowerCase().charAt(1))),
                    params[0].toLowerCase().charAt(0) - 'a' + 1);
        } catch (Exception e) {
            return ERROR_TEXT + "Invalid spot on chessboard" + RESET;
        }

        if (Objects.equals(client.getBoard().getPieceChar(client.getBoard().getCurrentGame().game().getBoard()
                .getPiece(highlightPiece)), EscapeSequences.EMPTY)) {
            return ERROR_TEXT + "No piece at that position" + RESET;
        }

        client.getBoard().highlight(highlightPiece, playerColor);
        return "";
    }

    String help() {
        return """
                redraw - redraw the chess board
                leave - leave the game
                move <piece position> <new position> - ex: e1 e2
                    If you are to promote a pawn, please type
                    <piece position> <new position> <QUEEN/BISHOP/KNIGHT/ROOK>
                resign - forfeit the game
                highlight <piece position> - highlights all possible moves for the piece
                help - display possible commands (current menu)
                """;
    }
}
