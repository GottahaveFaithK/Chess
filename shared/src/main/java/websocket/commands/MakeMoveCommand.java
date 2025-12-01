package websocket.commands;

import chess.ChessMove;
import chess.ChessPosition;

public class MakeMoveCommand extends UserGameCommand {
    private final ChessMove move;
    private String startPos;
    private String endPos;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);
        this.move = move;
        calculatePosPrints();
    }

    void calculatePosPrints() {
        startPos = format(move.getStartPosition());
        endPos = format(move.getEndPosition());

    }

    String format(ChessPosition pos) {
        int preCol = pos.getColumn();
        int row = pos.getRow();
        char col = 'i';
        switch (preCol) {
            case 1 -> col = 'a';
            case 2 -> col = 'b';
            case 3 -> col = 'c';
            case 4 -> col = 'd';
            case 5 -> col = 'e';
            case 6 -> col = 'f';
            case 7 -> col = 'g';
            case 8 -> col = 'h';
        }
        return col + "" + row;
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
