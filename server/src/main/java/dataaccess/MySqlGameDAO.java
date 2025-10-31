package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

public class MySqlGameDAO implements GameDAO {

    public MySqlGameDAO() throws DataAccessException {
        configure();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }

    @Override
    public void updateGame(GameData game, ChessGame updatedGame, String color, String username) throws DataAccessException {

    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() {
        return List.of();
    }

    @Override
    public void deleteAllGames() {

    }

    private final String[] create = {
            """
            CREATE TABLE IF NOT EXISTS games (
            `gameID` int NOT NULL,
            `whiteUsername` varchar(256) DEFAULT NULL,
            `blackUsername` varchar(256) DEFAULT NULL,
            `gameName` varchar(256) NOT NULL,
            `game` JSON NOT NULL,
            PRIMARY KEY (`gameID`)
            )
            """
    };

    private void configure() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : create) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database");
        }
    }
}
