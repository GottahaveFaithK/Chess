package websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;

public record PlayerInfo(Session session, String authToken, int gameID,
                         ChessGame.TeamColor color, boolean isObserver, String username) {
}
