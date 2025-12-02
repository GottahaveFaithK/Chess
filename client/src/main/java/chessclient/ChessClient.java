package chessclient;


import model.GameData;
import ui.*;
import websocket.NotificationHandler;
import websocket.WebsocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.Scanner;

import static ui.Formatting.*;


public class ChessClient implements NotificationHandler {
    private String user;
    private String authToken;
    private UIState state;
    private ClientChessboard board;
    ServerFacade server;
    WebsocketFacade ws;

    public ChessClient(String serverUrl) throws ClientException {
        server = new ServerFacade(serverUrl);
        ws = new WebsocketFacade(serverUrl, this);
        state = new SignedOutUI(this, server, ws);
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification(((NotificationMessage) message).getMessage());
            case ERROR -> displayError(((ErrorMessage) message).getErrorMessage());
            case LOAD_GAME -> loadGame(((LoadGameMessage) message).getGame());
        }

    }

    public void run() {
        System.out.println("Welcome to chess : type \"help\" to get started");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            System.out.print(state.printPrompt());
            String line = scanner.nextLine();

            try {
                result = state.handle(line);
                System.out.print(LIGHT_BLUE_TEXT + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(ERROR_TEXT + msg + RESET);
            }
        }
        System.out.println();
    }

    public void setState(UIState newState) {
        this.state = newState;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setAuthToken(String token) {
        this.authToken = token;
    }

    public String getAuthToken() {
        return authToken;
    }

    public ClientChessboard getBoard() {
        return board;
    }

    void displayNotification(String message) {
        System.out.println(BLUE_TEXT + message);
        System.out.print(state.printPrompt());
    }

    void displayError(String errorMessage) {
        System.out.println(ERROR_TEXT + errorMessage + RESET);
        if (errorMessage.equals("Incorrect Game ID")) {
            setState(new SignedInUI(this, server, ws));
        }
        System.out.print(state.printPrompt());
    }

    void loadGame(GameData game) {
        if (board == null) {
            board = new ClientChessboard(game);
        } else {
            board.setCurrentGame(game);
        }

        if (state instanceof GameplayUI gameplayState) {
            if (gameplayState.getPlayerColor() == null || gameplayState.getPlayerColor().equals("WHITE")) {
                board.drawChessBoardWhite();
            } else {
                board.drawChessBoardBlack();
            }
        }
        System.out.print("\n");
    }
}
