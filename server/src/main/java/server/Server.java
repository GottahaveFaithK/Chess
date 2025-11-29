package server;

import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import service.ClearService;
import service.GameService;
import service.ResponseException;
import service.UserService;
import dataaccess.DatabaseManager;
import websocket.WebSocketHandler;

import java.util.Map;

public class Server {

    private final Javalin server;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;
    private final ClearHandler clearHandler;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        initializeDatabase();
        UserDAO userDAO;
        GameDAO gameDAO;
        AuthDAO authDAO;
        try {
            authDAO = new MySqlAuthDAO();
            userDAO = new MySqlUserDAO();
            gameDAO = new MySqlGameDAO();
        } catch (DataAccessException e) {
            throw new ResponseException("Error: failed to create the table", 500);
        }

        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);
        ClearService clearService = new ClearService(gameDAO, userDAO, authDAO);

        webSocketHandler = new WebSocketHandler(userService, gameService);
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
        server.ws("/ws", ws -> {
            ws.onConnect(webSocketHandler);
            ws.onMessage(webSocketHandler);
            ws.onClose(webSocketHandler);
        });
    }

    private void exceptionHandler(ResponseException ex, Context ctx) {
        ctx.status(ex.getHttpResponseCode());
        ctx.json(ex.toJson());
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }


    private static void initializeDatabase() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            throw new ResponseException("Error: failed to create the database", 500);
        }
    }

    public void stop() {
        server.stop();
    }
}
