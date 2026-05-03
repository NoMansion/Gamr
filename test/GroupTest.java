//Written by Sam Good
package test;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;
import src.*;

public class GroupTest {

    // Helper to initialize the service layer with a real SQL connection
    private Service getService() {
        DatabaseConnection db = ConnectionFactory.createConnection("SQL");
        DBOperation dbOps = new SQLOperation(db);
        return new Service(dbOps);
    }

    /**
     * Corresponds to: "List current group members" in diagram
     */
    @Test
    public void testListCurrentGroupMembers() {
        Service service = getService();
        int testGroupId = 1; // Existing ID from your SQL merge

        List<User> members = service.getGroupMembers(testGroupId);

        assertNotNull("Roster should not be null", members);
        // Should contain at least the creator (ID 10)
        assertTrue("Group should have existing members", members.size() > 0);
    }

    /**
     * Corresponds to: "Add selected friends" -> "Add another friend? No"
     */
    @Test
    public void testAddSingleFriendToGroup() {
        Service service = getService();
        int testGroupId = 1; 
        int friendIdToAdd = 11; // Sam (10) adding Ben (11)

        boolean success = service.addFriendToGroup(testGroupId, friendIdToAdd);

        assertTrue("Should successfully add a single valid friend", success);
    }

    /**
     * Corresponds to: "Add another friend? Yes" loop in diagram
     */
    @Test
    public void testAddMultipleFriendsToGroup() {
        Service service = getService();
        int testGroupId = 2; // Group B from your SQL merge
        int[] friendsToAdd = {11, 12}; // Ben and David

        int successCount = 0;
        for (int id : friendsToAdd) {
            if (service.addFriendToGroup(testGroupId, id)) {
                successCount++;
            }
        }

        assertEquals("Should successfully loop and add 2 friends", 2, successCount);
    }

    /**
     * System logic test: Handles the error if a user is already a member
     */
    @Test
    public void testAddDuplicateFriendToGroup() {
        Service service = getService();
        int testGroupId = 1;
        int friendId = 10; // Creator is already in the group

        boolean success = service.addFriendToGroup(testGroupId, friendId);

        // System should block this due to PK constraint
        assertFalse("Should fail when adding a duplicate member", success);
    }

    /**
     * System logic test: Handles error for non-existent users
     */
    @Test
    public void testAddInvalidUserToGroup() {
        Service service = getService();
        int testGroupId = 1;
        int fakeUserId = 9999; // ID not in the USERS table

        boolean success = service.addFriendToGroup(testGroupId, fakeUserId);

        // System should block this due to FK constraint
        assertFalse("Should fail when adding an invalid user ID", success);
    }
}
