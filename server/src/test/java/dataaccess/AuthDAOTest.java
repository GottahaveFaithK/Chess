package dataaccess;

import model.AuthData;
import org.junit.jupiter.api.Test;
import service.ResponseException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTest {
    @Test
    void createAuth() throws DataAccessException {
        AuthDAO authDAO = new MySqlAuthDAO();
        authDAO.deleteAllAuth();
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData("user", token);

        authDAO.createAuth(authData);
        var res = authDAO.getAuth(token);
        assertNotNull(res);
        assertEquals(authData.username(), res.username());
        assertEquals(authData.authToken(), res.authToken());
    }

    @Test
    void getAuth() throws DataAccessException {

    }
}
