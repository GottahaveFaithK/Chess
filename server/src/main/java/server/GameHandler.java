package server;

import com.google.gson.Gson;
import model.GameData;
import service.GameService;
import io.javalin.http.Context;

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
        var res = gameService.createGame(request.gameName(), authToken);
        var response = Map.of("gameID", res);
        ctx.result(serializer.toJson(response));
        ctx.status(200);
    }

    public void listGames(Context ctx) {
        //TODO impl file
    }

    public void joinGame(Context ctx) {
        //TODO impl file
    }
}
