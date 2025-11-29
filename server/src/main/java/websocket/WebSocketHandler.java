package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import websocket.commands.UserGameCommand;


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

        if (command.getCommandType() == UserGameCommand.CommandType.CONNECT) {
            join(session, command);
        } else {
            PlayerInfo conn = getConnection(command.getAuthToken(), session);
            if (conn != null) {
                switch (command.getCommandType()) {
                    case MAKE_MOVE -> move(conn, msg);
                    case LEAVE -> leave(conn, msg);
                    case RESIGN -> resign(conn, msg);
                }
            } else {
                ConnectionManager.sendError(session.getRemote(), "unknown user");
            }
        }
    }

    private void join(Session session, UserGameCommand command) {

    }

    private void move(PlayerInfo player, String msg) {

    }

    private void leave(PlayerInfo player, String msg) {

    }

    private void resign(PlayerInfo player, String msg) {

    }

    private PlayerInfo getConnection(String authToken, Session session) {
        return null;
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
