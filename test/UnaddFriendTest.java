package test;

import org.junit.Test;
import static org.junit.Assert.*;
import src.*;

public class UnaddFriendTest {

    private Service getService() {
        DatabaseConnection db = ConnectionFactory.createConnection("SQL");
        DBOperation dbOps = new SQLOperation(db);
        return new Service(dbOps);
    }

    // Path 1: deleteFriendship returns false (users are not friends)
    @Test
    public void testUnaddFriend_DeleteFails() {
        DatabaseConnection db = ConnectionFactory.createConnection("SQL");
        DBOperation dbOps = new SQLOperation(db);

        boolean result = dbOps.deleteFriendship(1, 9999);
        assertFalse("Should return false when friendship doesn't exist", result);
    }

    // Path 2: deleteFriendship returns true (users are friends)
    @Test
    public void testUnaddFriend_DeleteSucceeds() {
        DatabaseConnection db = ConnectionFactory.createConnection("SQL");
        DBOperation dbOps = new SQLOperation(db);

        boolean result = dbOps.deleteFriendship(1, 22);
        assertTrue("Should return true when friendship exists", result);
    }

    // Path 3: user tries to unadd themselves
    @Test
    public void testUnaddFriend_SameUser() {
        DatabaseConnection db = ConnectionFactory.createConnection("SQL");
        DBOperation dbOps = new SQLOperation(db);

        boolean result = dbOps.deleteFriendship(1, 1);
        assertFalse("Should return false when both IDs are the same", result);
    }
}
