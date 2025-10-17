package server;

import service.GameService;
import io.javalin.http.Context;


public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void createGame(Context ctx) {
        //TODO impl this
    }

    public void listGames(Context ctx) {
        //TODO impl file
    }

    public void joinGame(Context ctx) {
        //TODO impl file
    }
}
