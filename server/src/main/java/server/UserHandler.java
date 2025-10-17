package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import model.UserData;
import service.UserService;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    public void register(Context ctx) {
        var serializer = new Gson();
        UserData request = serializer.fromJson(ctx.body(), UserData.class);

        var res = userService.register(request);

        var response = serializer.toJson(request);
        ctx.result(response);
    }

    public void login(Context ctx) {
        //TODO login
    }

    public void logout(Context ctx) {
        //TODO logout
    }
}
