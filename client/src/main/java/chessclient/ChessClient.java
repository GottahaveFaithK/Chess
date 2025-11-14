package chessclient;

import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;

import java.util.Arrays;
import java.util.Scanner;

import static ui.EscapeSequences.*;


public class ChessClient {
    private String user;
    private String authToken;
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
                case "register" -> register(params);
                case "login" -> login(params);
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "list" -> listGames(params);
                case "logout" -> logout(params);
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
        try {
            assertSignedOut();
        } catch (Exception e) {
            return e.getMessage();
        }
        if (params.length != 3) {
            return "Expected: \"register <USERNAME> <PASSWORD> <EMAIL>\"";
        }
        try {
            RegisterRequest request = new RegisterRequest(params[0], params[1], params[2]);
            server.register(request);
        } catch (ClientException e) {
            if (e.getCode() == 400) {
                return "Expected: \"register <USERNAME> <PASSWORD> <EMAIL>\"";
            } else if (e.getCode() == 403) {
                return "Username is already taken";
            } else {
                return "Unexpected error, please try again. If this fails again, please restart program";
            }
        }
        return login(params[0], params[1]);
    }

    public String login(String... params) {
        try {
            assertSignedOut();
        } catch (Exception e) {
            return e.getMessage();
        }
        if (params.length != 2) {
            return "Expected: \"login <USERNAME> <PASSWORD>\"";
        }
        try {
            LoginRequest loginRequest = new LoginRequest(params[0], params[1]);
            authToken = server.login(loginRequest).authToken();
        } catch (ClientException e) {
            if (e.getCode() == 400) {
                return "Expected: \"login <USERNAME> <PASSWORD>\"";
            } else if (e.getCode() == 401) {
                return "Invalid username or password";
            } else {
                return "Unexpected error, please try again. If this fails again, please restart program";
            }
        }
        user = params[0];
        state = State.SIGNEDIN;
        return "Signed in as " + user;
    }

    public String logout(String... params) {
        try {
            assertSignedIn();
        } catch (Exception e) {
            return e.getMessage();
        }
        try {
            LogoutRequest request = new LogoutRequest(authToken);
            server.logout(request);
        } catch (ClientException e) {
            if (e.getCode() == 401) {
                return "You are already logged out";
            } else {
                return "Unexpected error, please try again. If this fails again, please restart program";
            }
        }
        user = null;
        state = State.SIGNEDOUT;
        authToken = null;
        return "Successfully signed out.";
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

    public String joinGame(String... params) {
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

    private void assertSignedOut() throws Exception {
        if (state == State.SIGNEDIN) {
            throw new Exception("You are already signed in");
        }
    }

}
