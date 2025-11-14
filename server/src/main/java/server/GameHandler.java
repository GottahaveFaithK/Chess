package server;

import com.google.gson.Gson;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import response.CreateGameResponse;
import response.FormattedGamesData;
import response.ListGamesResponse;
import service.GameService;
import io.javalin.http.Context;
import service.ResponseException;

import java.util.*;


public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void createGame(Context ctx) {
        var serializer = new Gson();
        CreateGameRequest bodyRequest = serializer.fromJson(ctx.body(), CreateGameRequest.class);
        String authToken = ctx.header("authorization");
        CreateGameRequest request = new CreateGameRequest(authToken, bodyRequest.gameName());

        if (authToken == null || authToken.isEmpty() ||
                request.gameName() == null || request.gameName().isEmpty()) {
            throw new ResponseException("Error: bad request", 400);
        }

        int gameID = gameService.createGame(request);
        ctx.status(200);
        ctx.result(serializer.toJson(new CreateGameResponse(gameID)));
        System.out.println(serializer.toJson(new CreateGameResponse(gameID)));
    }

    public void listGames(Context ctx) {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");
        if (authToken == null || authToken.isEmpty()) {
            throw new ResponseException("Error: bad request", 400);
        }
        ListGamesRequest request = new ListGamesRequest(authToken);
        Collection<GameData> games = gameService.listGames(request);
        Collection<FormattedGamesData> formattedGames = new HashSet<>();
        for (GameData data : games) {
            formattedGames.add(new FormattedGamesData(data.gameID(), data.gameName(),
                    data.whiteUsername(), data.blackUsername()));
        }

        Map<String, Object> response = Map.of("games", formattedGames);
        ctx.status(200);
        ctx.result(serializer.toJson(response));
    }

    public void joinGame(Context ctx) {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");
        JoinGameRequest body = serializer.fromJson(ctx.body(), JoinGameRequest.class);
        if (authToken == null || authToken.isEmpty() ||
                body.playerColor() == null || body.playerColor().isEmpty() || body.gameID() == null) {
            throw new ResponseException("Error: bad request", 400);
        }

        JoinGameRequest request = new JoinGameRequest(authToken, body.playerColor(), body.gameID());
        gameService.joinGame(request);
        ctx.status(200);
        ctx.result(serializer.toJson(Map.of()));
    }
}
