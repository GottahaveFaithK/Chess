package server;

import com.google.gson.Gson;
import io.javalin.http.Context;
import service.ClearService;

import java.util.Map;

public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService) {
        this.clearService = clearService;
    }

    public void clear(Context ctx) {
        clearService.clear();
        var serializer = new Gson();
        ctx.result(serializer.toJson(Map.of()));
        ctx.status(200);
    }
}
