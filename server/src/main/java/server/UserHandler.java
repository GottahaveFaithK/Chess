package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.UserData;
import service.ResponseException;
import service.UserService;

import java.util.Map;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        var serializer = new Gson();
        UserData request = serializer.fromJson(ctx.body(), UserData.class);

        if (request.username() == null || request.username().isEmpty() ||
                request.password() == null || request.password().isEmpty() ||
                request.email() == null || request.email().isEmpty()) {
            throw new ResponseException("Error: bad request", 400);
        }

        var res = userService.register(request);

        var response = serializer.toJson(res);
        ctx.result(response);

    }

    public void login(Context ctx) {
        var serializer = new Gson();
        UserData request = serializer.fromJson(ctx.body(), UserData.class);

        if (request.username() == null || request.username().isEmpty() ||
                request.password() == null || request.password().isEmpty()) {
            throw new ResponseException("Error: bad request", 400);
        }

        var res = userService.login(request);

        var response = serializer.toJson(res);
        ctx.result(response);
    }

    public void logout(Context ctx) {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");
        userService.logout(authToken);
        ctx.result(serializer.toJson(Map.of()));
        ctx.status(200);

    }
}
