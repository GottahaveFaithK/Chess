package server;

import com.google.gson.Gson;
import model.GameData;
import service.GameService;
import io.javalin.http.Context;
import service.ResponseException;

import java.util.Map;


public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void createGame(Context ctx) {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");
        GameData request = serializer.fromJson(ctx.body(), GameData.class);
        if (authToken == null || authToken.isEmpty() ||
                request.gameName() == null || request.gameName().isEmpty()) {
            throw new ResponseException("Error: bad request", 400);
        }
        var res = gameService.createGame(request.gameName(), authToken);
        var response = Map.of("gameID", res);
        ctx.result(serializer.toJson(response));
        ctx.status(200);
    }

    public void listGames(Context ctx) {
        //TODO impl file
    }

    public void joinGame(Context ctx) {
        //make this prettier
        //maybe figure out a better way to get the color and id
        //maybe create a class for them???
        var serializer = new Gson();
        String authToken = ctx.header("authorization");
        Map<String, Object> body = serializer.fromJson(ctx.body(), Map.class);
        String color = (String) body.get("playerColor");
        Double id = (Double) body.get("gameID");

        if (authToken == null || authToken.isEmpty() ||
                color == null || color.isEmpty() || id == null) {
            throw new ResponseException("Error: bad request", 400);
        }
        int gameID = id.intValue();
        gameService.joinGame(color, gameID, authToken);
        ctx.result(serializer.toJson(Map.of()));
        ctx.status(200);
    }
}
