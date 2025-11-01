package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class MySqlGameDAO implements GameDAO {

    public MySqlGameDAO() throws DataAccessException {
        configure();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        GameData gameData;

        if (gameName != null && !gameName.isBlank()) {
            gameData = new GameData(0, null, null, gameName, new ChessGame());
        } else {
            throw new DataAccessException("Game name is null");
        }

        var statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        String json = new Gson().toJson(gameData.game());

        try (Connection conn = DatabaseManager.getConnection();
             var preparedStatement = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, gameData.whiteUsername());
            preparedStatement.setString(2, gameData.blackUsername());
            preparedStatement.setString(3, gameName);
            preparedStatement.setString(4, json);
            preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new DataAccessException("Didn't create game ID");
                }
            }

        } catch (Exception e) {
            throw new DataAccessException("Unable to insert game data: " + e.getMessage());
        }
    }

    @Override
    public void updateColor(GameData game, ChessGame updatedGame, String color, String username)
            throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            GameData existing = getGame(game.gameID());

            if (color.equals("WHITE")) {
                if (existing.whiteUsername() != null) {
                    throw new DataAccessException("Color already taken");
                }

                var statement = "UPDATE games SET whiteUsername = ? WHERE gameID = ?";
                try (PreparedStatement ps = conn.prepareStatement(statement)) {
                    ps.setString(1, username);
                    ps.setInt(2, game.gameID());
                    ps.executeUpdate();
                }

            } else if (color.equals("BLACK")) {
                if (existing.blackUsername() != null) {
                    throw new DataAccessException("Color already taken");
                }

                var statement = "UPDATE games SET blackUsername = ? WHERE gameID = ?";
                try (PreparedStatement ps = conn.prepareStatement(statement)) {
                    ps.setString(1, username);
                    ps.setInt(2, game.gameID());
                    ps.executeUpdate();
                }

            } else {
                throw new DataAccessException("Invalid color");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Cant read data" + e.getMessage());
        }
    }

    @Override
    public void updateGame(int id, GameData updatedData) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {

            String json = new Gson().toJson(updatedData.game());
            var statement = "UPDATE games SET game = ? WHERE gameID = ?";

            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, json);
                ps.setInt(2, id);

                int affected = ps.executeUpdate();
                if (affected == 0) {
                    throw new DataAccessException("Game doesn't exist");
                }

            }
        } catch (SQLException e) {
            throw new DataAccessException("Cant update game" + e.getMessage());
        }
    }

    @Override
    public GameData getGame(int id) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID = ?";
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setInt(1, id);
                try (ResultSet rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        var json = rs.getString("game");
                        var game = new Gson().fromJson(json, ChessGame.class);
                        return new GameData(rs.getInt("gameID"), rs.getString("whiteUsername"),
                                rs.getString("blackUsername"), rs.getString("gameName"), game);
                    } else {
                        throw new DataAccessException("Game doesn't exist");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Cant read data" + e.getMessage());
        }
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        Collection<GameData> gamesList = new java.util.ArrayList<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games";
            try (PreparedStatement preparedStatement = conn.prepareStatement(statement)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        ChessGame game = new Gson().fromJson(resultSet.getString("game"), ChessGame.class);
                        gamesList.add(new GameData(
                                resultSet.getInt("gameID"),
                                resultSet.getString("whiteUsername"),
                                resultSet.getString("blackUsername"),
                                resultSet.getString("gameName"),
                                game));
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Cant read data" + e.getMessage());
        }
        return gamesList;
    }

    @Override
    public void deleteAllGames() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM games");
             PreparedStatement resetInc = conn.prepareStatement("ALTER TABLE games AUTO_INCREMENT = 1")) {
            preparedStatement.executeUpdate();
            resetInc.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Unable to delete all games: " + e.getMessage());
        }
    }

    private final String[] create = {
            """
            CREATE TABLE IF NOT EXISTS games (
            `gameID` int NOT NULL AUTO_INCREMENT,
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
