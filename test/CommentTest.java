/*
Basis path testing for the "Comment under community post" use case.

This use case is implemented in the insertComment method in the Service 
class. This method has three independent paths: one where the comment text 
is null, one where the comment text is empty or whitespace, and one where 
the comment text is valid. So, a test case is written to test each of these 
paths.

Written by Brady Ehman
*/
package test;

import org.junit.Test;
import static org.junit.Assert.*;
import src.*;

public class CommentTest {

    // Helper to initialize the service layer with a real SQL connection
    private Service getService() {
        DatabaseConnection db = ConnectionFactory.createConnection("SQL");
        DBOperation dbOps = new SQLOperation(db);
        return new Service(dbOps);
    }

    // Path 1: text == null
    @Test
    public void testInsertComment_NullText() {
        Service service = getService();

        boolean result = service.insertComment(1, 31, null);

        assertFalse("Comment should not be posted because it is null.", result);

    }

    // Path 2: text is empty or whitespace
    @Test
    public void testInsertComment_EmptyText() {
        Service service = getService();

        boolean result = service.insertComment(1, 31, "   ");

        assertFalse("Comment should not be posted because it is empty or contains only whitespace.", result);

    }

    // Path 3: valid text
    @Test
    public void testInsertComment_ValidText() {
        Service service = getService();

        boolean result = service.insertComment(1, 31, "JUnit Test Comment");

        assertTrue("Comment should be posted because the text is valid.", result);

    }
}
