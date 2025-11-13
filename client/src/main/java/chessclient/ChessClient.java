package chessclient;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;


public class ChessClient {
    private final ServerFacade server;
    State state = State.SIGNEDOUT;
    String reset = RESET_TEXT_COLOR + RESET_BG_COLOR + RESET_TEXT_ITALIC;

    public ChessClient(String serverUrl) throws ClientException {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to chess : type \"help\" to get started");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_LIGHT_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(SET_TEXT_COLOR_LIGHT_RED + SET_TEXT_ITALIC + msg + reset);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print("\n" + reset);
        if (state == State.SIGNEDOUT) {
            System.out.print(SET_TEXT_COLOR_BLUE + "[SIGNED_OUT] ");
        } else {
            System.out.print(SET_TEXT_COLOR_BLUE + "[SIGNED_IN] ");
        }
        System.out.print(">>> ");
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String selection = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (selection) {
                case "list" -> listGames();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ClientException ex) {
            return ex.getMessage();
        }
    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    register <USERNAME> <PASSWORD> <EMAIL> - to create an account
                    login <USERNAME> <PASSWORD> - to sign into existing account
                    quit - quit chess program
                    help - display possible commands (current menu)
                    """;
        } else {
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

    public String register(String... params) {
        //register
        return null;
    }

    public String login(String... params) {
        //login
        return null;
    }

    public String logout(String... params) {
        //logout
        return null;
    }

    public String createGame(String... params) {
        //let's play
        return null;
    }

    public String listGames(String... params) {
        //fresh off the press
        //if it gets a client exception debug and name based off error message
        return null;
    }

    public String playGame(String... params) {
        //join game
        return null;
    }

    public String observeGame(String... params) {
        //watch game
        return null;
    }

    private void assertSignedIn() throws Exception {
        if (state == State.SIGNEDOUT) {
            throw new Exception("You must sign in");
        }
    }

}
