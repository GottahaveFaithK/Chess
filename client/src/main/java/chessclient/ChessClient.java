package chessclient;

import chess.ChessGame;

import model.GameData;
import ui.ClientChessboard;
import ui.SignedOutUI;
import ui.UIState;
import websocket.NotificationHandler;
import websocket.WebsocketFacade;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static ui.Formatting.*;


public class ChessClient implements NotificationHandler {
    private String user;
    private String authToken;
    private UIState state;
    List<Integer> gameIDs = new ArrayList<>();
    //make not final for phase 6
    private final ClientChessboard board = new ClientChessboard(
            new GameData(-1, "eee", "eee", "aaaa", new ChessGame())
    );

    public ChessClient(String serverUrl) throws ClientException {
        ServerFacade server = new ServerFacade(serverUrl);
        WebsocketFacade ws = new WebsocketFacade(serverUrl, this);
        state = new SignedOutUI(this, server);
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
                System.out.print(lightBlueText + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(errorText + msg + reset);
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

    public List<Integer> getGameIDs() {
        return gameIDs;
    }

    public void addGameID(int id) {
        gameIDs.add(id);
    }

    public ClientChessboard getBoard() {
        return board;
    }

    void displayNotification(String message) {

    }

    void displayError(String errorMessage) {

    }

    void loadGame(GameData game) {

    }
}
