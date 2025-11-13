package server;

import com.google.gson.Gson;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import response.CreateGameResponse;
import response.Game;
import response.ListGamesResponse;
import service.GameService;
import io.javalin.http.Context;
import service.ResponseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        CreateGameResponse response = new CreateGameResponse(gameID);
        ctx.status(200);
        ctx.result(serializer.toJson(response));
    }

    public void listGames(Context ctx) {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");
        if (authToken == null || authToken.isEmpty()) {
            throw new ResponseException("Error: bad request", 400);
        }
        ListGamesRequest request = new ListGamesRequest(authToken);
        var res = gameService.listGames(request);
        List<Game> gamesFormatted = new ArrayList<>();
        for (GameData game : res) {
            gamesFormatted.add(new Game(
                    game.gameID(),
                    game.gameName(),
                    game.whiteUsername(),
                    game.blackUsername()
            ));
        }

        ListGamesResponse response = new ListGamesResponse(gamesFormatted);
        ctx.result(serializer.toJson(response));
        ctx.status(200);
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
