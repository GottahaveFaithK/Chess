package ui;

import chessclient.ChessClient;
import chessclient.ClientException;
import chessclient.ServerFacade;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import request.LogoutRequest;
import response.CreateGameResponse;
import response.ListGamesResponse;
import websocket.WebsocketFacade;

import java.util.Arrays;

import static ui.Formatting.*;

public class SignedInUI implements UIState {
    ChessClient client;
    ServerFacade server;
    WebsocketFacade ws;

    public SignedInUI(ChessClient client, ServerFacade server, WebsocketFacade ws) {
        this.client = client;
        this.server = server;
        this.ws = ws;
    }

    @Override
    public String handle(String input) {
        String[] tokens = input.toLowerCase().split(" ");
        String selection = (tokens.length > 0) ? tokens[0] : "help";
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
        return switch (selection) {
            case "create" -> createGame(params);
            case "join" -> joinGame(params);
            case "observe" -> observeGame(params);
            case "list" -> listGames();
            case "logout" -> logout();
            case "quit" -> "quit";
            default -> help();
        };
    }

    public String printPrompt() {
        return "\n" + BLUE_TEXT + "[SIGNED_IN] >>> ";
    }

    public String help() {
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - signs out of current account
                help - display possible commands (current menu)""";
    }


    public String logout() {
        try {
            LogoutRequest request = new LogoutRequest(client.getAuthToken());
            server.logout(request);
        } catch (ClientException e) {
            if (e.getCode() == 401) {
                return ERROR_TEXT + "You are already logged out" + RESET;
            } else {
                return ERROR_TEXT + "Unexpected error, please try again. " +
                        "If this fails again, please restart program" + RESET;
            }
        }
        client.setUser(null);
        client.setState(new SignedOutUI(client, server, ws));
        client.setAuthToken(null);
        return "Successfully signed out.";
    }

    public String createGame(String... params) {
        if (params.length != 1) {
            return ERROR_TEXT + "Expected: \"create <NAME>\"" + RESET;
        }

        try {
            CreateGameRequest gameRequest = new CreateGameRequest(client.getAuthToken(), params[0]);
            server.createGame(gameRequest);
        } catch (ClientException e) {
            return gameErrorText(e);
        }
        return "Created game: " + params[0];
    }

    String gameErrorText(ClientException e) {
        if (e.getCode() == 400) {
            return ERROR_TEXT + "Expected: \"create <NAME>\"" + RESET;
        } else if (e.getCode() == 401) {
            return ERROR_TEXT + "You must sign in" + RESET;
        } else {
            return ERROR_TEXT + "Unexpected error, please try again. " +
                    "If this fails again, please restart program" + RESET;
        }
    }

    public String listGames() {
        ListGamesResponse response;
        try {
            ListGamesRequest listGames = new ListGamesRequest(client.getAuthToken());
            response = server.listGames(listGames);
        } catch (ClientException e) {
            return gameErrorText(e);
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
        if (params.length != 2) {
            return ERROR_TEXT + "Expected: join <ID> [WHITE|BLACK]" + RESET;
        }
        int gameId;
        try {
            gameId = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return ERROR_TEXT + "\"<ID>\" must be a number, not " + params[0] + RESET;
        }
        String color = params[1].toUpperCase().replace("\"", "");
        try {
            JoinGameRequest joinGame = new JoinGameRequest(client.getAuthToken(), color, gameId);
            server.joinGame(joinGame);
        } catch (ClientException e) {
            if (e.getCode() == 400) {
                return ERROR_TEXT + "Please pick either \"WHITE\" or \"BLACK\" as a color." + RESET;
            } else if (e.getCode() == 401) {
                return ERROR_TEXT + "You must sign in before you can do that" + RESET;
            } else if (e.getCode() == 403) {
                return ERROR_TEXT + "Sorry, that color is already taken" + RESET;
            } else {
                return ERROR_TEXT + "Unexpected error, please try again. " +
                        "If this fails again, please restart program" + RESET;
            }
        }

        ws.joinGame(client.getAuthToken(), gameId);

        if (color.equals("WHITE")) {
            client.setState(new GameplayUI(client, server, "WHITE", gameId, ws));
            return "\nJoined game " + gameId + " as WHITE\n";
        } else {
            client.setState(new GameplayUI(client, server, "BLACK", gameId, ws));
            return "\nJoined game " + gameId + " as BLACK\n";
        }
    }

    public String observeGame(String... params) {

        if (params.length != 1) {
            return ERROR_TEXT + "Expected: \"observe <ID>\"" + RESET;
        }

        int gameId;
        try {
            gameId = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return ERROR_TEXT + "\"<ID>\" must be a number, not " + params[0] + RESET;
        }

        ws.joinGame(client.getAuthToken(), gameId);

        client.setState(new GameplayUI(client, server, null, gameId, ws));
        return "Attempting to observe game " + gameId;
    }
}
