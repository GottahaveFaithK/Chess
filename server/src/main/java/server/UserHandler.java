package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.LogoutResponse;
import response.RegisterResponse;
import service.ResponseException;
import service.UserService;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        var serializer = new Gson();
        RegisterRequest request = serializer.fromJson(ctx.body(), RegisterRequest.class);

        if (request.username() == null || request.username().isEmpty() ||
                request.password() == null || request.password().isEmpty() ||
                request.email() == null || request.email().isEmpty()) {
            throw new ResponseException("Error: bad request", 400);
        }

        var res = userService.register(request);
        RegisterResponse response = new RegisterResponse(res.username(), res.authToken());
        ctx.status(200);
        ctx.result(serializer.toJson(response));
    }

    public void login(Context ctx) {
        var serializer = new Gson();
        LoginRequest request = serializer.fromJson(ctx.body(), LoginRequest.class);

        if (request.username() == null || request.username().isEmpty() ||
                request.password() == null || request.password().isEmpty()) {
            throw new ResponseException("Error: bad request", 400);
        }

        var res = userService.login(request);

        LoginResponse response = new LoginResponse(res.username(), res.authToken());
        ctx.status(200);
        ctx.result(serializer.toJson(response));
    }

    public void logout(Context ctx) {
        var serializer = new Gson();
        String authToken = ctx.header("authorization");
        if (authToken == null || authToken.isEmpty()) {
            throw new ResponseException("Error: bad request", 400);
        }
        LogoutRequest request = new LogoutRequest(authToken);
        userService.logout(request);
        LogoutResponse response = new LogoutResponse();
        ctx.status(200);
        ctx.result(serializer.toJson(response));
    }
}
