package chessclient;

import chess.ChessGame;

import java.net.http.HttpRequest;
import java.util.List;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
        //TODO ask Bill about client
    }

    public List<ChessGame> listGames() {
        return null;
        //TODO ask Bill
    }

    private HttpRequest buildRequest() {
        return null;
    }
    //figure out what this does
}
