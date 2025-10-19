package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.ClearService;
import service.GameService;
import service.ResponseException;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;
    private final ClearHandler clearHandler;

    public Server() {

        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        ClearService clearService = new ClearService(gameDAO, userDAO, authDAO);

        userHandler = new UserHandler(userService);
        gameHandler = new GameHandler(gameService);
        clearHandler = new ClearHandler(clearService);

        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.exception(Exception.class, (ex, ctx) -> {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + ex.getMessage()));
        });

        registerRoutes();

    }

    private void registerRoutes() {
        //server.delete("db", ctx -> ctx.result("{}")); //ctx means context
        server.exception(ResponseException.class, this::exceptionHandler);
        server.delete("db", clearHandler::clear);
        server.post("user", userHandler::register);
        server.post("session", userHandler::login);
        server.delete("session", userHandler::logout);
        server.get("game", gameHandler::listGames);
        server.post("game", gameHandler::createGame);
        server.put("game", gameHandler::joinGame);
    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.getHttpResponseCode());
        ctx.json(ex.toJson());
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
