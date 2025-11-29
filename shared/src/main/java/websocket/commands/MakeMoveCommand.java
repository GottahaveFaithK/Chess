package websocket.commands;

import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {
    private final ChessMove move;
    private final String startPos;
    private final String endPos;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move,
                           String startPos, String endPos) {
        super(commandType, authToken, gameID);
        this.move = move;
        this.startPos = startPos;
        this.endPos = endPos;
    }

    public ChessMove getMove() {
        return move;
    }

    public String getStartPos() {
        return startPos;
    }

    public String getEndPos() {
        return endPos;
    }
}
