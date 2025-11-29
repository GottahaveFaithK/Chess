package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;


import java.io.IOException;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final Gson gson = new Gson();


    @Override
    public void handleClose(@NotNull WsCloseContext wsCloseContext) throws Exception {

    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) throws Exception {
        String msg = ctx.message();
        Session session = ctx.session;

        UserGameCommand command = gson.fromJson(msg, UserGameCommand.class);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String msg) throws Exception {
        UserGameCommand command = readJson(msg, GameCommand.class);

        var conn = getConnection(command.authToken, session);
        if (conn != null) {
            switch (command.getcommandType()) {
                case JOIN_PLAYER -> join(conn, msg);
                case JOIN_OBSERVER -> observe(conn, msg);
                case MAKE_MOVE -> move(conn, msg));
                case LEAVE -> leave(conn, msg);
                case RESIGN -> resign(conn, msg);
            }
        } else {
            Connection.sendError(session.getRemote(), "unknown user");
        }
    }


    private void enter() {

    }
    //connect
    //make move
    //leave
    //resign
    //look at video at 11:02

    //for deserializing make move commands may need to do it twice
    //deserialize it to see what type it is
    //if it is a make move command, deserialize again but as a make move command
}
