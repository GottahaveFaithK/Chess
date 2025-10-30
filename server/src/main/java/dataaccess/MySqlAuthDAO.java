package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlAuthDAO implements AuthDAO {

    public MySqlAuthDAO() throws DataAccessException {
        configure();
    }

    @Override
    public void createAuth(AuthData authData) {

    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public void deleteAllAuth() {

    }

    private final String[] create = {
            """
            CREATE TABLE IF NOT EXISTS auth (
            `token` varchar(256) NOT NULL,
            `username` VARCHAR(256) NOT NULL,
            PRIMARY KEY (`token`)
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
