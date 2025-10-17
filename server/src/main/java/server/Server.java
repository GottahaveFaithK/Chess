package server;

import dataaccess.*;
import io.javalin.*;
import service.UserService;

public class Server {

    private final Javalin server;
    private final UserHandler userHandler;
    private UserDAO userDAO;

    public Server() {

        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        UserService userService = new UserService(userDAO, authDAO);
        //GameService gameService = new GameService(gameDAO, authDAO);
        //ClearService clearService = new ClearService(userDAO, gameDAO, authDAO);

        userHandler = new UserHandler(userService);

        server = Javalin.create(config -> config.staticFiles.add("web"));

        registerRoutes();

    }

    private void registerRoutes() {
        server.delete("db", ctx -> ctx.result("{}")); //ctx means context
        server.post("user", userHandler::register);
        server.post("session", userHandler::login);
        server.delete("session", userHandler::logout);
        //server.get("game", this::listGames);
        //server.post("game", this::createGame);
        //server.put("game", this::joinGame);
    }

    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
