package ui;

import static ui.Formatting.blueText;

public class GameplayUI implements UIState {
    @Override
    public String handle(String input) {
        return "gameplay";
    }

    public String printPrompt() {
        return blueText + "[IN_GAME] >>> ";
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
