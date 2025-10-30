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
    void duplicateAuthTokens() throws DataAccessException {
        AuthDAO authDAO = new MySqlAuthDAO();
        authDAO.deleteAllAuth();

        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData("user", token);
        AuthData secondAuth = new AuthData("other user", token);
        authDAO.createAuth(authData);

        DataAccessException ex = assertThrows(DataAccessException.class, () -> authDAO.createAuth(secondAuth));
        assertTrue(ex.getMessage().startsWith("Unable to insert auth data:"));
    }

    @Test
    void getAuth() throws DataAccessException {
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
    void noExistingAuth() throws DataAccessException {
        AuthDAO authDAO = new MySqlAuthDAO();
        authDAO.deleteAllAuth();
        DataAccessException ex = assertThrows(DataAccessException.class, () -> authDAO.getAuth("not valid"));
        assertEquals("Auth token doesn't exist", ex.getMessage());
    }

    @Test
    void deleteAuth() throws DataAccessException {
        AuthDAO authDAO = new MySqlAuthDAO();
        authDAO.deleteAllAuth();
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData("user", token);

        authDAO.createAuth(authData);
        authDAO.deleteAuth(token);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> authDAO.getAuth(token));
        assertEquals("Auth token doesn't exist", ex.getMessage());
    }

    @Test
    void deleteNonExistentAuth() throws DataAccessException {
        AuthDAO authDAO = new MySqlAuthDAO();
        authDAO.deleteAllAuth();
        DataAccessException ex = assertThrows(DataAccessException.class, () -> authDAO.deleteAuth("invalid"));
        assertEquals("Auth token doesn't exist", ex.getMessage());
    }

    @Test
    void deleteAllAuth() throws DataAccessException {
        AuthDAO authDAO = new MySqlAuthDAO();
        authDAO.deleteAllAuth();
        String token = UUID.randomUUID().toString();
        AuthData authData = new AuthData("user", token);

        authDAO.createAuth(authData);
        authDAO.deleteAllAuth();
        DataAccessException ex = assertThrows(DataAccessException.class, () -> authDAO.getAuth(token));
        assertEquals("Auth token doesn't exist", ex.getMessage());
    }
}
