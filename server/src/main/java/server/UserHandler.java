package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.UserData;
import service.ResponseException;
import service.UserService;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");
        String email = ctx.formParam("email");

        if (username == null || username.isEmpty() ||
                password == null || password.isEmpty() ||
                email == null || email.isEmpty()) {
            throw new ResponseException("Error: bad request", 400);
        } else {
            var serializer = new Gson();
            UserData request = serializer.fromJson(ctx.body(), UserData.class);

            var res = userService.register(request);

            var response = serializer.toJson(res);
            ctx.result(response);
        }
    }

    public void login(Context ctx) {
        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            throw new ResponseException("Error: bad request", 400);
        } else {
            var serializer = new Gson();
            UserData request = serializer.fromJson(ctx.body(), UserData.class);

            var res = userService.login(request);

            var response = serializer.toJson(res);
            ctx.result(response);
        }
    }

    public void logout(Context ctx) {
        //TODO logout
    }
}
