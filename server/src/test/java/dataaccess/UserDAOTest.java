package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserDAOTest {
    @Test
    void createUser() throws DataAccessException {
        UserDAO userDAO = new MySqlUserDAO();
        userDAO.deleteAllUsers();
        UserData userData = new UserData("User", "Password", "myEmail@gmail.com");
        userDAO.createUser(userData);
        var res = userDAO.getUser("User");
        assertNotNull(res);
        assertEquals(userData.username(), res.username());
        assertEquals(userData.password(), res.password());
        assertEquals(userData.email(), res.email());
    }

    @Test
    void duplicateUsername() throws DataAccessException {
        UserDAO userDAO = new MySqlUserDAO();
        userDAO.deleteAllUsers();
        UserData userData = new UserData("User", "Password", "myEmail@gmail.com");
        UserData secondUser = new UserData("User", "differentPerson", "Other@gmail.com");
        userDAO.createUser(userData);
        DataAccessException ex = assertThrows(DataAccessException.class, () -> userDAO.createUser(secondUser));
        assertTrue(ex.getMessage().startsWith("Username already taken"));
    }

    @Test
    void getUser() throws DataAccessException {
        UserDAO userDAO = new MySqlUserDAO();
        userDAO.deleteAllUsers();
        UserData userData = new UserData("User", "Password", "myEmail@gmail.com");
        userDAO.createUser(userData);
        var res = userDAO.getUser("User");
        assertNotNull(res);
        assertEquals(userData.username(), res.username());
        assertEquals(userData.password(), res.password());
        assertEquals(userData.email(), res.email());
    }

    @Test
    void getNonexistentUser() throws DataAccessException {
        UserDAO userDAO = new MySqlUserDAO();
        userDAO.deleteAllUsers();
        DataAccessException ex = assertThrows(DataAccessException.class, () -> userDAO.getUser("User"));
        assertEquals("User doesn't exist", ex.getMessage());
    }

    @Test
    void deleteAllUsers() throws DataAccessException {
        UserDAO userDAO = new MySqlUserDAO();
        userDAO.deleteAllUsers();
        UserData userData = new UserData("User", "Password", "myEmail@gmail.com");

        userDAO.createUser(userData);
        userDAO.deleteAllUsers();
        DataAccessException ex = assertThrows(DataAccessException.class, () -> userDAO.getUser("User"));
        assertEquals("User doesn't exist", ex.getMessage());
    }
}
