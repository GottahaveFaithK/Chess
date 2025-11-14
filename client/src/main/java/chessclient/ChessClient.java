package chessclient;

import chess.ChessGame;
import model.GameData;
import request.*;
import response.CreateGameResponse;
import response.ListGamesResponse;
import ui.ClientChessboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;


public class ChessClient {
    private String user;
    private String authToken;
    private final ServerFacade server;
    //make not final for phase 6
    private final ClientChessboard board = new ClientChessboard(
            new GameData(-1, "eee", "eee", "aaaa", new ChessGame())
    );
    List<Integer> gameIDs = new ArrayList<>();
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
                case "list" -> listGames();
                case "logout" -> logout();
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

    public String logout() {
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
        try {
            assertSignedIn();
        } catch (Exception e) {
            return e.getMessage();
        }
        if (params.length != 1) {
            return "Expected: \"create <NAME>\"";
        }
        CreateGameResponse response;
        try {
            CreateGameRequest gameRequest = new CreateGameRequest(authToken, params[0]);
            response = server.createGame(gameRequest);
        } catch (ClientException e) {
            if (e.getCode() == 400) {
                return "Expected: \"create <NAME>\"";
            } else if (e.getCode() == 401) {
                return "You must sign in";
            } else {
                return "Unexpected error, please try again. If this fails again, please restart program";
            }
        }
        gameIDs.add(response.gameID());
        return "Created game: " + params[0];
    }

    public String listGames() {
        try {
            assertSignedIn();
        } catch (Exception e) {
            return e.getMessage();
        }
        ListGamesResponse response;
        try {
            ListGamesRequest listGames = new ListGamesRequest(authToken);
            response = server.listGames(listGames);
        } catch (ClientException e) {
            if (e.getCode() == 400) {
                return "Expected: \"create <NAME>\"";
            } else if (e.getCode() == 401) {
                return "You must sign in";
            } else {
                return "Unexpected error, please try again. If this fails again, please restart program";
            }
        }
        StringBuilder gamesList = new StringBuilder();
        for (GameData game : response.games()) {
            gamesList.append(String.format(
                    "%d : %s | White Player: %s | Black Player: %s%n",
                    game.gameID(),
                    game.gameName(),
                    game.whiteUsername() == null ? "<none>" : game.whiteUsername(),
                    game.blackUsername() == null ? "<none>" : game.blackUsername()
            ));
        }
        return gamesList.toString();
    }

    public String joinGame(String... params) {
        try {
            assertNotInGame();
        } catch (Exception e) {
            return e.getMessage();
        }
        try {
            assertSignedIn();
        } catch (Exception e) {
            return e.getMessage();
        }
        if (params.length != 2) {
            return "Expected: join <ID> [WHITE|BLACK]";
        }
        int gameId;
        try {
            gameId = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return "\"<ID>\" must be a number, not " + params[0];
        }
        String color = params[1].toUpperCase().replace("\"", "");
        try {
            JoinGameRequest joinGame = new JoinGameRequest(authToken, color, gameId);
            server.joinGame(joinGame);
        } catch (ClientException e) {
            if (e.getCode() == 400) {
                return "Please pick either \"WHITE\" or \"BLACK\" as a color.";
            } else if (e.getCode() == 401) {
                return "You must sign in before you can do that";
            } else if (e.getCode() == 403) {
                return "Sorry, that color is already taken";
            } else {
                return "Unexpected error, please try again. If this fails again, please restart program";
            }
        }
        if (color.equals("WHITE")) {
            board.drawChessBoardWhite();
            state = State.INGAME;
            return "\nJoined game " + gameId + " as WHITE";
        } else {
            board.drawChessBoardBlack();
            state = State.INGAME;
            return "\nJoined game " + gameId + " as BLACK";
        }
    }

    public String observeGame(String... params) {
        try {
            assertNotInGame();
        } catch (Exception e) {
            return e.getMessage();
        }

        try {
            assertSignedIn();
        } catch (Exception e) {
            return e.getMessage();
        }

        if (params.length != 1) {
            return "Expected: \"observe <ID>\"";
        }

        int gameId;
        try {
            gameId = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return "\"<ID>\" must be a number, not " + params[0];
        }
        if (!gameIDs.contains(gameId)) {
            return "Game with ID " + gameId + " doesn't exist. Please list games and try again";
        }
        board.drawChessBoardWhite();
        state = State.INGAME;
        return "Successfully observing game " + gameId;
    }

    private void assertSignedIn() throws Exception {
        if (state == State.SIGNEDOUT) {
            throw new Exception("You must sign in before you can do that");
        }
    }

    private void assertNotInGame() throws Exception {
        if (state == State.INGAME) {
            throw new Exception("You can't do that while in a game");
        }
    }

    private void assertSignedOut() throws Exception {
        if (state == State.SIGNEDIN) {
            throw new Exception("You are already signed in");
        }
    }

}
