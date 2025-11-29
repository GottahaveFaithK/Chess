package websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ErrorMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionManager {
    private final Map<Session, PlayerInfo> playerMap = new HashMap<>();

    private final Map<Integer, List<Session>> sessionMap = new HashMap<>();
    //look at pet shop
    //instead of just storing sessions, store a map with the game id as the key
    //then the value for the game id would be a set of sessions that belong to that game

    //broadcast idea is the same

    public static void sendError(RemoteEndpoint remote, String message) {
        ErrorMessage errorMessage = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message);
        try {
            remote.sendString(new Gson().toJson(errorMessage));
        } catch (IOException e) {
            System.err.println("Error: failed to send error message: " + e.getMessage());
        }
    }

    public void addPlayer(PlayerInfo player) {
        playerMap.put(player.session(), player);
    }

    public void removePlayer(PlayerInfo player) {
        playerMap.remove(player.session());
    }

    public PlayerInfo getPlayer(Session session) {
        return playerMap.get(session);
    }

    public void addSession(int gameID, Session session) {
        List<Session> sessions = sessionMap.computeIfAbsent(gameID, k -> new ArrayList<>());
        // if no list exists yet
        // create a new list
        // put it in the map
        sessions.add(session);
    }

    public void removeSession(int gameID, Session session) {
        List<Session> sessions = sessionMap.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                sessionMap.remove(gameID);
            }
        }
    }

    public List<Session> getSessions(int gameID) {
        return sessionMap.get(gameID);
    }

    public void broadcast(Session excludeSession, ServerMessage message) {
        PlayerInfo player = getPlayer(excludeSession);
        List<Session> sessions = getSessions(player.gameID());
        String msg = message.toString();
        try {
            for (Session c : sessions) {
                if (c.isOpen()) {
                    if (!c.equals(excludeSession)) {
                        c.getRemote().sendString(msg);
                    }
                }
            }
        } catch (IOException e) {
            //if I implement logging, log this
        }
    }
}
