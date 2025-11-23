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

import java.util.Arrays;

import static ui.Formatting.*;

public class SignedInUI implements UIState {
    ChessClient client;
    ServerFacade server;

    public SignedInUI(ChessClient client, ServerFacade server) {
        this.client = client;
        this.server = server;
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
        return "\n" + blueText + "[SIGNED_IN] >>> ";
    }

    public String help() {
        return """
                create <NAME> - a game
                list - games
                join <ID> [WHITE|BLACK] - a game
                observe <ID> - a game
                logout - signs out of current account
                quit - quit chess program
                help - display possible commands (current menu)""";
    }


    public String logout() {
        try {
            LogoutRequest request = new LogoutRequest(client.getAuthToken());
            server.logout(request);
        } catch (ClientException e) {
            if (e.getCode() == 401) {
                return errorText + "You are already logged out" + reset;
            } else {
                return errorText + "Unexpected error, please try again. " +
                        "If this fails again, please restart program" + reset;
            }
        }
        client.setUser(null);
        client.setState(new SignedOutUI(client, server));
        client.setAuthToken(null);
        return "Successfully signed out.";
    }

    public String createGame(String... params) {
        if (params.length != 1) {
            return errorText + "Expected: \"create <NAME>\"" + reset;
        }
        CreateGameResponse response;
        try {
            CreateGameRequest gameRequest = new CreateGameRequest(client.getAuthToken(), params[0]);
            response = server.createGame(gameRequest);
        } catch (ClientException e) {
            if (e.getCode() == 400) {
                return errorText + "Expected: \"create <NAME>\"" + reset;
            } else if (e.getCode() == 401) {
                return errorText + "You must sign in" + reset;
            } else {
                return errorText + "Unexpected error, please try again. " +
                        "If this fails again, please restart program" + reset;
            }
        }

        client.addGameID(response.gameID());
        return "Created game: " + params[0];
    }

    public String listGames() {
        ListGamesResponse response;
        try {
            ListGamesRequest listGames = new ListGamesRequest(client.getAuthToken());
            response = server.listGames(listGames);
        } catch (ClientException e) {
            if (e.getCode() == 400) {
                return errorText + "Expected: \"create <NAME>\"" + reset;
            } else if (e.getCode() == 401) {
                return errorText + "You must sign in" + reset;
            } else {
                return errorText + "Unexpected error, please try again. " +
                        "If this fails again, please restart program" + reset;
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
        if (params.length != 2) {
            return errorText + "Expected: join <ID> [WHITE|BLACK]" + reset;
        }
        int gameId;
        try {
            gameId = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return errorText + "\"<ID>\" must be a number, not " + params[0] + reset;
        }
        String color = params[1].toUpperCase().replace("\"", "");
        try {
            JoinGameRequest joinGame = new JoinGameRequest(client.getAuthToken(), color, gameId);
            server.joinGame(joinGame);
        } catch (ClientException e) {
            if (e.getCode() == 400) {
                return errorText + "Please pick either \"WHITE\" or \"BLACK\" as a color." + reset;
            } else if (e.getCode() == 401) {
                return errorText + "You must sign in before you can do that" + reset;
            } else if (e.getCode() == 403) {
                return errorText + "Sorry, that color is already taken" + reset;
            } else {
                return errorText + "Unexpected error, please try again. " +
                        "If this fails again, please restart program" + reset;
            }
        }
        if (color.equals("WHITE")) {
            client.getBoard().drawChessBoardWhite();
            client.setPlayerColor("WHITE");
            return "\nJoined game " + gameId + " as WHITE";
        } else {
            client.getBoard().drawChessBoardBlack();
            client.setPlayerColor("BLACK");
            return "\nJoined game " + gameId + " as BLACK";
        }
    }

    public String observeGame(String... params) {

        if (params.length != 1) {
            return errorText + "Expected: \"observe <ID>\"" + reset;
        }

        int gameId;
        try {
            gameId = Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return errorText + "\"<ID>\" must be a number, not " + params[0] + reset;
        }
        if (!client.getGameIDs().contains(gameId)) {
            return errorText + "Game with ID " + gameId + " doesn't exist. Please list games and try again" + reset;
        }
        client.getBoard().drawChessBoardWhite();
        return "Successfully observing game " + gameId;
    }
}
