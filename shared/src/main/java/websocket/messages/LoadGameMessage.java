package websocket.messages;

import model.GameData;

public class LoadGameMessage extends ServerMessage {
    GameData game;

    public LoadGameMessage(ServerMessageType type, GameData game) {
        super(type);
        this.game = game;
    }

    public GameData getGame() {
        return game;
    }

    public void setGame(GameData game) {
        this.game = game;
    }
}
