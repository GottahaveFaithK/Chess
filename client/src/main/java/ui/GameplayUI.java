package ui;

import chessclient.ChessClient;
import chessclient.ServerFacade;

import java.util.Arrays;

import static ui.Formatting.blueText;

public class GameplayUI implements UIState {
    ChessClient client;
    ServerFacade server;
    String playerColor; //observer's will be null, maybe change for security

    public GameplayUI(ChessClient client, ServerFacade server, String color) {
        this.client = client;
        this.server = server;
        playerColor = color;
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
            case "resign" -> resign(params);
            case "highlight" -> highlight(params);
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String printPrompt() {
        return blueText + "[IN_GAME] >>> ";
    }

    public String redraw() {
        if (playerColor.equals("BLACK")) {
            client.getBoard().drawChessBoardBlack();
        } else {
            client.getBoard().drawChessBoardWhite();
        }
        return "\nRedrew Board";
    }

    public String leave() {
        //will need websocket
        return null;
    }

    public String move(String... params) {
        //will need websocket
        return null;
    }

    public String resign(String... params) {
        //does it delete the game from the list? probably
        //other player wins
        //doesn't kick players out of game
        //can't be called by observer
        //will need websocket
        //prompt to confirm
        //doesn't remove the player from the game
        return null;
    }

    public String highlight(String... params) {
        return null;
    }

    String help() {
        return """
                redraw - redraw the chess board
                leave - leave the game
                move <piece position> <new position> - ex: e1 e2
                resign - forfeit the game
                highlight <piece position> - highlights all possible moves for the piece
                help - display possible commands (current menu)
                """;
    }
}
