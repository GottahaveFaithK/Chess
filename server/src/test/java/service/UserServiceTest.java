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
        assertEquals(res.username(), user.username());
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
        assertEquals(response.username(), user.username());
        assertNotNull(response.authToken());
    }

}