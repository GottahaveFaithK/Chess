package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import datamodel.UserData;
import io.javalin.*;
import io.javalin.http.Context;
import service.UserService;

public class Server {

    private final Javalin server;
    private UserService userService;
    private DataAccess dataAccess;

    public Server() {

        userService = new UserService(dataAccess);
        server = Javalin.create(config -> config.staticFiles.add("web"));

        server.delete("db", ctx -> ctx.result("{}")); //ctx means context
        server.post("user", this::register);
        server.post("session", this::login);
        server.delete("session", this::logout);
        server.get("game", this::listGames);
        server.post("game", this::createGame);
        server.put("game", this::joinGame);
        // Register your endpoints and exception handlers here.

    }

    private void register(Context ctx) {
        var serializer = new Gson();
        var request = serializer.fromJson(ctx.body(), UserData.class);

        //call to the service and register
        var res = userService.register(request);

        var response = serializer.toJson(request);
        ctx.result(response);
    }

    private void login(Context ctx) {
        //impl login
    }

    private void logout(Context ctx) {
        //impl logout
    }

    private void listGames(Context ctx) {
        //impl listGames
    }

    private void createGame(Context ctx) {
        //impl createGame
    }

    private void joinGame(Context ctx) {

    }


    public int run(int desiredPort) {
        server.start(desiredPort);
        return server.port();
    }

    public void stop() {
        server.stop();
    }
}
