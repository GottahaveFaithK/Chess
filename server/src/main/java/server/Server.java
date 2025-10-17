package server;

import dataaccess.*;
import io.javalin.*;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    private final Javalin server;
    private final UserHandler userHandler;
    private final GameHandler gameHandler;
    private final ClearHandler clearHandler;
    private UserDAO userDAO;

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

        registerRoutes();

    }

    private void registerRoutes() {
        //server.delete("db", ctx -> ctx.result("{}")); //ctx means context
        server.delete("db", clearHandler::clear);
        server.post("user", userHandler::register);
        server.post("session", userHandler::login);
        server.delete("session", userHandler::logout);
        server.get("game", gameHandler::listGames);
        server.post("game", gameHandler::createGame);
        server.put("game", gameHandler::joinGame);
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
