package service;

import dataaccess.*;
import model.UserData;
import org.junit.jupiter.api.Test;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void registerPositive() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        RegisterRequest registerRequest = new RegisterRequest("joe", "j@j", "j@jmail.com");
        var res = userService.register(registerRequest);
        assertEquals(registerRequest.username(), res.username());
        assertNotNull(res.authToken());
        assertEquals(String.class, res.authToken().getClass());
    }

    @Test
    void registerNegative() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        RegisterRequest registerRequest = new RegisterRequest("joe", "j@j", "j");
        var res = userService.register(registerRequest);
        ResponseException ex = assertThrows(ResponseException.class, () -> userService.register(registerRequest));
        assertEquals(403, ex.getHttpResponseCode());
        assertEquals("Error: already taken", ex.getMessage());
    }

    @Test
    void loginWorks() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        RegisterRequest registerRequest = new RegisterRequest("joe", "j@j", "j@jmail.com");
        var res = userService.register(registerRequest);
        assertEquals(registerRequest.username(), res.username());
        assertNotNull(res.authToken());
    }

    @Test
    void loginWrongUsername() {
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        RegisterRequest registerRequest = new RegisterRequest("joe", "j@j", "j@jmail.com");
        userService.register(registerRequest);
        var fakeUser = new LoginRequest("bob", "j@j");
        ResponseException ex = assertThrows(ResponseException.class, () -> userService.login(fakeUser));
        assertEquals(401, ex.getHttpResponseCode());
        assertEquals("Error: unauthorized", ex.getMessage());
    }

    @Test
    void loginWrongPassword() {
        var user = new RegisterRequest("joe", "j@j", "j@jmail.com");
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        userService.register(user);
        var fakeUser = new LoginRequest("joe", "eeeee");
        ResponseException ex = assertThrows(ResponseException.class, () -> userService.login(fakeUser));
        assertEquals(401, ex.getHttpResponseCode());
        assertEquals("Error: unauthorized", ex.getMessage());
    }

    @Test
    void logoutPositive() {
        var user = new RegisterRequest("joe", "j@j", "j@jmail.com");
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        var res = userService.register(user);
        LogoutRequest logout = new LogoutRequest(res.authToken());
        userService.logout(logout);
        assertThrows(DataAccessException.class, () -> authDAO.getAuth(res.authToken()));

    }

    @Test
    void logoutNegative() {
        var user = new RegisterRequest("joe", "j@j", "j@jmail.com");
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(userDAO, authDAO);
        userService.register(user);
        LogoutRequest logout = new LogoutRequest("ion");
        ResponseException ex = assertThrows(ResponseException.class, () -> userService.logout(logout));
        assertEquals(401, ex.getHttpResponseCode());
        assertEquals("Error: unauthorized", ex.getMessage());
    }
}