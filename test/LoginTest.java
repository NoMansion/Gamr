//Written by Kyle Flatt
package test;
import org.junit.Test;
import static org.junit.Assert.*;

import src.*;

public class LoginTest {

    @Test
    public void testLoginSuccess() {
        DatabaseConnection db = new SQLConnection();
        DBOperation dbOps = new SQLOperation(db);
        Service service = new Service(dbOps);

        User user = service.login("test@email.com", "password123");

        assertNotNull("Login should succeed with correct credentials", user);
    }

    @Test
    public void testLoginWrongPassword() {
        DatabaseConnection db = new SQLConnection();
        DBOperation dbOps = new SQLOperation(db);
        Service service = new Service(dbOps);

        User user = service.login("test@email.com", "wrongPassword");

        assertNull("Login should fail with wrong password", user);
    }

    @Test
    public void testLoginEmailNotFound() {
        DatabaseConnection db = new SQLConnection();
        DBOperation dbOps = new SQLOperation(db);
        Service service = new Service(dbOps);

        User user = service.login("fake@email.com", "password123");

        assertNull("Login should fail if email not found", user);
    }
}
