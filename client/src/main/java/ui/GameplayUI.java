package ui;

import chessclient.ChessClient;
import chessclient.ServerFacade;

import java.util.Arrays;

import static ui.Formatting.blueText;

public class GameplayUI implements UIState {
    ChessClient client;
    ServerFacade server;
    String playerColor;

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
        return null;
    }

    public String leave() {
        return null;
    }

    public String move(String... params) {
        return null;
    }

    public String resign(String... params) {
        return null;
    }

    public String highlight(String... params) {
        return null;
    }

    String help() {
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - signs out of current account
                help - display possible commands (current menu)
                """;
    }
}
