package server;

import com.google.gson.Gson;
import service.GameService;
import io.javalin.http.Context;


public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void createGame(Context ctx) {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");
        String gameName = serializer.fromJson(ctx.body(), String.class);
        var res = gameService.createGame(gameName, authToken);
        var response = serializer.toJson(res);
        ctx.result(response);
    }

    public void listGames(Context ctx) {
        //TODO impl file
    }

    public void joinGame(Context ctx) {
        //TODO impl file
    }
}
