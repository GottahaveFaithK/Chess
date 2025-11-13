package client;

import chessclient.ClientException;
import chessclient.ServerFacade;
import org.junit.jupiter.api.*;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import response.LoginResponse;
import response.RegisterResponse;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @BeforeEach
    void clearDatabase() {
        facade.clearDatabase();
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void sampleTest() {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerPositive() {
        RegisterRequest request = new RegisterRequest("user", "secret", "e@gmail.com");
        RegisterResponse res = facade.register(request);
        assertEquals("user", res.username());
    }

    @Test
    public void usernameTaken() {
        RegisterRequest request = new RegisterRequest("user", "secret", "e@gmail.com");
        facade.register(request);
        ClientException ex = assertThrows(ClientException.class, () -> facade.register(request));
        assertEquals(403, ex.getCode());
        assertEquals("Error", ex.getMessage());
    }

    @Test
    public void missingField() {
        RegisterRequest request = new RegisterRequest("user", "", "e@gmail.com");
        ClientException ex = assertThrows(ClientException.class, () -> facade.register(request));
        assertEquals(400, ex.getCode());
        assertEquals("Error", ex.getMessage());
    }

    @Test
    public void loginPositive() {
        RegisterRequest request = new RegisterRequest("user", "secret", "e@gmail.com");
        facade.register(request);
        LoginRequest login = new LoginRequest("user", "secret");
        LoginResponse res = facade.login(login);
        assertEquals("user", res.username());
        assertNotNull(res.authToken());
    }

    @Test
    public void loginWrongPassword() {
        RegisterRequest request = new RegisterRequest("user", "secret", "e@gmail.com");
        facade.register(request);
        LoginRequest login = new LoginRequest("user", "notsecret");
        ClientException ex = assertThrows(ClientException.class, () -> facade.login(login));
        assertEquals(401, ex.getCode());
        assertEquals("Error", ex.getMessage());
    }

    @Test
    public void logoutPositive() {
        RegisterRequest request = new RegisterRequest("user", "secret", "e@gmail.com");
        facade.register(request);
        LoginRequest login = new LoginRequest("user", "secret");
        var auth = facade.login(login);
        LogoutRequest logout = new LogoutRequest(auth.authToken());
        facade.logout(logout);
        assertThrows(ClientException.class, () -> facade.logout(logout));
    }

    @Test
    public void logoutUnauthorized() {
        RegisterRequest request = new RegisterRequest("user", "secret", "e@gmail.com");
        facade.register(request);
        LoginRequest login = new LoginRequest("user", "secret");
        facade.login(login);
        LogoutRequest logout = new LogoutRequest("e");
        ClientException ex = assertThrows(ClientException.class, () -> facade.logout(logout));
        assertEquals(401, ex.getCode());
        assertEquals("Error", ex.getMessage());
    }
}
