package service;

import dataaccess.MemoryUserDAO;
import model.UserData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void register() {
        var user = new UserData("joe", "j@j", "j");
        var at = "xyz";

        var da = new MemoryUserDAO();
        var service = new UserService(da);
        var res = service.register(user);
        assertEquals(res.username(), user.username());
        assertNotNull(res.authToken());
        assertEquals(String.class, res.authToken().getClass());
    }

}