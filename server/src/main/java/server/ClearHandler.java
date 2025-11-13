package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import request.ClearDatabaseRequest;
import response.ClearDatabaseResponse;
import service.ClearService;


public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear(Context ctx) {
        Gson serializer = new Gson();
        ClearDatabaseRequest request = serializer.fromJson(ctx.body(), ClearDatabaseRequest.class);
        clearService.clear();
        ClearDatabaseResponse response = new ClearDatabaseResponse();
        ctx.result(serializer.toJson(response));
        ctx.status(200);
    }
}
