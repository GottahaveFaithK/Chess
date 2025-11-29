package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.*;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    UserService userService;
    GameService gameService;
    private final Gson gson = new Gson();
    ConnectionManager connectionManager = new ConnectionManager();

    public WebSocketHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;
    }


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
        String authToken = command.getAuthToken();
        int gameID = command.getGameID();
        boolean authorized = userService.verify(authToken);
        if (!authorized) {
            ConnectionManager.sendError(session.getRemote(), "unknown user");
            return;
        }

        String color = gameService.getPlayerColor(authToken, gameID);
        boolean observer = color.equals("null");
        PlayerInfo player = new PlayerInfo(session, authToken, gameID, color, observer);

        connectionManager.addPlayer(player);
        connectionManager.addSession(gameID, session);
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
