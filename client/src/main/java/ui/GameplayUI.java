package ui;

import java.util.Arrays;

import static ui.Formatting.lightBlueText;

public class GameplayUI implements UIState {

    @Override
    public String handle(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String selection = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return "gameplay";
    }

    public String printPrompt() {
        return lightBlueText + "[IN_GAME] >>> ";
    }

    String help() {
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - signs out of current account
                quit - quit chess program
                help - display possible commands (current menu)
                """;
    }
}
