package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public class MySqlUserDAO implements UserDAO {
    public void MySqlAuthDAO() throws DataAccessException {
        configure();
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAllUsers() throws DataAccessException {

    }


    private final String[] create = {
            """
            CREATE TABLE IF NOT EXISTS users (
            `username` varchar(256) NOT NULL,
            `password` VARCHAR(256) NOT NULL,
            `email` VARCHAR(256) NOT NULL,
            PRIMARY KEY (`username`)
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
