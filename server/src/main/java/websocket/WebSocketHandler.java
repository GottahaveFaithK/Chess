package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.jetbrains.annotations.NotNull;
import service.GameService;
import service.UserService;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;


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
                    case MAKE_MOVE -> {
                        MakeMoveCommand move = gson.fromJson(msg, MakeMoveCommand.class);
                        move(conn, move);
                    }
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
        boolean observer = color == null;
        String username = userService.getUsername(authToken);
        PlayerInfo player = new PlayerInfo(session, authToken, gameID, color, observer, username);

        connectionManager.addPlayer(player);
        connectionManager.addSession(gameID, session);

        NotificationMessage notification;
        if (observer) {
            notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " joined as observer");
        } else {
            notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " joined as " + color);

        }
        connectionManager.broadcast(session, notification);
    }

    private void move(PlayerInfo player, MakeMoveCommand moveCommand) {
        ChessMove move = moveCommand.getMove();

        try {
            gameService.makeMove(player.gameID(), move);
        } catch (InvalidMoveException e) {
            ConnectionManager.sendError(player.session().getRemote(), "Invalid move");
        }

        ChessGame.TeamColor color;
        String tempColor = player.color().toLowerCase();

        if (tempColor.equals("white")) {
            color = ChessGame.TeamColor.WHITE;
        } else if (tempColor.equals("black")) {
            color = ChessGame.TeamColor.BLACK;
        } else {
            ConnectionManager.sendError(player.session().getRemote(), "Observer can't make moves");
            return;
        }

        gameService.evaluateState(player.gameID(), color);
        GameService.GameState gameState = gameService.getState(player.gameID(), color);
        updatePlayersMove(player, gameState, color, moveCommand);
    }

    private void updatePlayersMove(PlayerInfo player, GameService.GameState gameState, ChessGame.TeamColor color,
                                   MakeMoveCommand moveCommand) {
        Session session = player.session();
        String playerColor = player.color();
        String printColor;

        GameData gameData = gameService.getGame(player.gameID());
        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData);
        NotificationMessage notificationMessage;
        if (gameState == GameService.GameState.IN_PROGRESS) {
            notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    playerColor + " made move " + moveCommand.getStartPos() + " to "
                            + moveCommand.getEndPos());
        } else if (gameState == GameService.GameState.CHECK) {
            if (color == ChessGame.TeamColor.WHITE) {
                notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        playerColor + " made move " + moveCommand.getStartPos() + " to "
                                + moveCommand.getEndPos() + "\nBlack is in check");
            } else {
                notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        playerColor + " made move " + moveCommand.getStartPos() + " to "
                                + moveCommand.getEndPos() + "\nWhite is in check");
            }
        } else if (gameState == GameService.GameState.WINNER_BLACK) {
            notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    playerColor + " made move " + moveCommand.getStartPos() + " to "
                            + moveCommand.getEndPos() + "\nBlack won!");
        } else if (gameState == GameService.GameState.WINNER_WHITE) {
            notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    playerColor + " made move " + moveCommand.getStartPos() + " to "
                            + moveCommand.getEndPos() + "\nWhite won!");
        } else {
            notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    playerColor + " made move " + moveCommand.getStartPos() + " to "
                            + moveCommand.getEndPos() + "\nStalemate!");
        }

        connectionManager.broadcastAll(session, loadGameMessage);
        connectionManager.broadcastAll(session, notificationMessage);
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

    //for deserializing make move commands may need to do it twice
    //deserialize it to see what type it is
    //if it is a make move command, deserialize again but as a make move command
}
