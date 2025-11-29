package websocket;

import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConnectionManager {
    private Map<Session, PlayerInfo> sessionMap = new HashMap<>();
    
    private Map<Integer, List<Session>> gameSessions = new HashMap<>();
    //look at petshop
    //instead of just storing sessions, store a map with the game id as the key
    //then the value for the game id would be a set of sessions that belong to that game

    //broadcast idea is the same

    public static void sendError(RemoteEndpoint remote, String message) {
        //do something idk
    }
}
