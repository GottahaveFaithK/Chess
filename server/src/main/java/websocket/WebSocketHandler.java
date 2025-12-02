package websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
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
                    case LEAVE -> leave(conn);
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

        String color = null;
        try {
            color = gameService.getPlayerColor(authToken, gameID);
        } catch (Exception e) {
            ConnectionManager.sendError(session.getRemote(), "Incorrect Game ID");
        }
        boolean observer = color == null;
        ChessGame.TeamColor formattedColor;
        if (!observer) {
            if (color.equalsIgnoreCase("white")) {
                formattedColor = ChessGame.TeamColor.WHITE;
            } else if (color.equalsIgnoreCase("black")) {
                formattedColor = ChessGame.TeamColor.BLACK;
            } else {
                return;
            }
        } else {
            formattedColor = null;
        }
        String username = userService.getUsername(authToken);
        PlayerInfo player = new PlayerInfo(session, authToken, gameID, formattedColor, observer, username);

        connectionManager.addPlayer(player);
        connectionManager.addSession(gameID, session);

        NotificationMessage notification;
        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME,
                gameService.getGame(gameID));
        if (observer) {
            notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " joined as observer");
        } else {
            notification = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " joined as " + color);

        }
        connectionManager.broadcastPlayer(session, loadGameMessage);
        connectionManager.broadcast(session, notification);
    }


    private void leave(PlayerInfo player) {
        GameData gameData = gameService.getGame(player.gameID());
        if (!player.isObserver()) {
            gameService.leaveGame(gameData, player);
        }

        NotificationMessage notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                player.username() + " left the game");
        connectionManager.broadcast(player.session(), notificationMessage);
        connectionManager.removePlayer(player);
        connectionManager.removeSession(player.gameID(), player.session());
    }

    private void resign(PlayerInfo player, String msg) {
        if (player.isObserver()) {
            ConnectionManager.sendError(player.session().getRemote(), "Observer can't resign");
            return;
        }

        if (gameService.getState(player.gameID(), player.color()) != GameService.GameState.IN_PROGRESS &&
                gameService.getState(player.gameID(), player.color()) != GameService.GameState.CHECK) {
            ConnectionManager.sendError(player.session().getRemote(), "Game has already ended");
            return;
        }

        NotificationMessage notificationMessage;
        if (player.color() == ChessGame.TeamColor.WHITE) {
            gameService.updateWinner(player.gameID(), ChessGame.Winner.BLACK);
            notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    player.username() + " resigned. BLACK won!");
        } else {
            gameService.updateWinner(player.gameID(), ChessGame.Winner.WHITE);
            notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    player.username() + " resigned. WHITE won!");
        }

        connectionManager.broadcastAll(player.session(), notificationMessage);
    }

    private void move(PlayerInfo player, MakeMoveCommand moveCommand) {
        ChessMove move = moveCommand.getMove();
        ChessGame.TeamColor color = player.color();

        try {
            validateMove(player);
            gameService.makeMove(player.gameID(), move);
        } catch (InvalidMoveException e) {
            ConnectionManager.sendError(player.session().getRemote(), e.getMessage());
            return;
        }

        gameService.evaluateState(player.gameID(), color);
        GameService.GameState gameState = gameService.getState(player.gameID(), color);
        updatePlayersMove(player, gameState, moveCommand);
    }

    private void validateMove(PlayerInfo player) throws InvalidMoveException {
        if (player.isObserver()) {
            throw new InvalidMoveException("Observer can't make moves");
        }

        GameService.GameState gameState = gameService.getState(player.gameID(), player.color());

        if (gameState != GameService.GameState.IN_PROGRESS && gameState != GameService.GameState.CHECK) {
            throw new InvalidMoveException("Game has ended");
        }

        GameData gameData = gameService.getGame(player.gameID());
        ChessGame.TeamColor turnColor = gameData.game().getTeamTurn();

        if (turnColor != player.color()) {
            throw new InvalidMoveException("Not your turn");
        }
    }


    private void updatePlayersMove(PlayerInfo player, GameService.GameState gameState, MakeMoveCommand moveCommand) {
        Session session = player.session();
        ChessGame.TeamColor color = player.color();

        GameData gameData = gameService.getGame(player.gameID());
        LoadGameMessage loadGameMessage = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameData);
        NotificationMessage notificationMessage;
        boolean broadcastALl = false;
        if (gameState == GameService.GameState.IN_PROGRESS) {
            notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    color + " made move " + moveCommand.getStartPos() + " to "
                            + moveCommand.getEndPos());
        } else if (gameState == GameService.GameState.CHECK) {
            if (color == ChessGame.TeamColor.WHITE) {
                notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        color + " made move " + moveCommand.getStartPos() + " to "
                                + moveCommand.getEndPos() + "\nBLACK is in check");
            } else {
                notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        color + " made move " + moveCommand.getStartPos() + " to "
                                + moveCommand.getEndPos() + "\nWHITE is in check");
            }
        } else if (gameState == GameService.GameState.WINNER_BLACK) {
            notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    color + " made move " + moveCommand.getStartPos() + " to "
                            + moveCommand.getEndPos() + "\nBLACK won!");
            broadcastALl = true;
        } else if (gameState == GameService.GameState.WINNER_WHITE) {
            notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    color + " made move " + moveCommand.getStartPos() + " to "
                            + moveCommand.getEndPos() + "\nWHITE won!");
            broadcastALl = true;
        } else {
            notificationMessage = new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    color + " made move " + moveCommand.getStartPos() + " to "
                            + moveCommand.getEndPos() + "\nStalemate!");
            broadcastALl = true;
        }

        connectionManager.broadcastAll(session, loadGameMessage);
        if (broadcastALl) {
            connectionManager.broadcastAll(session, notificationMessage);
        } else {
            connectionManager.broadcast(session, notificationMessage);
        }
    }

    private PlayerInfo getConnection(String authToken, Session session) {
        PlayerInfo player = connectionManager.getPlayer(session);
        if (player != null && authToken.equals(player.authToken())) {
            return player;
        } else {
            return null;
        }
    }

}
