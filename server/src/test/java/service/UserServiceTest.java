package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void registerPositive() {
        var user = new UserData("joe", "j@j", "j");

        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        var res = userService.register(user);
        assertEquals(user.username(), res.username());
        assertNotNull(res.authToken());
        assertEquals(String.class, res.authToken().getClass());
    }

    @Test
    void registerNegative() {
        var user = new UserData("joe", "j@j", "j");

        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        userService.register(user);
        ResponseException ex = assertThrows(ResponseException.class, () -> userService.register(user));
        assertEquals(403, ex.getHttpResponseCode());
        assertEquals("Error: already taken", ex.getMessage());
    }

    @Test
    void loginWorks() {
        var user = new UserData("joe", "j@j", "j@jmail.com");
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        userService.register(user);
        var response = userService.login(user);
        assertEquals(user.username(), response.username());
        assertNotNull(response.authToken());
    }

    @Test
    void loginWrongUsername() {
        var user = new UserData("joe", "j@j", "j@jmail.com");
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        userService.register(user);
        var fakeUser = new UserData("bob", "j@j", "j@jmail.com");
        ResponseException ex = assertThrows(ResponseException.class, () -> userService.login(fakeUser));
        assertEquals(401, ex.getHttpResponseCode());
        assertEquals("Error: unauthorized", ex.getMessage());
    }

    @Test
    void loginWrongPassword() {
        var user = new UserData("joe", "j@j", "j@jmail.com");
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        userService.register(user);
        var fakeUser = new UserData("joe", "eeeee", "j@jmail.com");
        ResponseException ex = assertThrows(ResponseException.class, () -> userService.login(fakeUser));
        assertEquals(401, ex.getHttpResponseCode());
        assertEquals("Error: unauthorized", ex.getMessage());
    }

    @Test
    void logoutPositive() {
        var user = new UserData("joe", "j@j", "j@jmail.com");
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        var res = userService.register(user);
        userService.logout(res.authToken());
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(res.authToken()));

    }

    @Test
    void logoutNegative() {
        var user = new UserData("joe", "j@j", "j@jmail.com");
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        userService.register(user);
        ResponseException ex = assertThrows(ResponseException.class, () -> userService.logout("ion"));
        assertEquals(401, ex.getHttpResponseCode());
        assertEquals("Error: unauthorized", ex.getMessage());
    }
}