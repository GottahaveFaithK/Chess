package websocket;

import org.eclipse.jetty.websocket.api.Session;

public record PlayerInfo(Session session, String authToken, int gameID, String color, boolean isObserver) {
}
