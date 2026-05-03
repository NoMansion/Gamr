package test; 

import org.junit.Test; 
import static org.junit.Assert.*; 
import java.util.List; 
import src.*; 

public class FriendTests {
     /** 
    * Tests remove friend
    */ 
    @Test 
    public void testRemoveFriend() { 
        DBOperation dbOps = getDbOps(); 
        int userId = 1; 
        int friendId = 2; 
        boolean result = dbOps.deleteFriendship(userId, friendId); 
        assertTrue("Friend should be removed successfully", result); 
    } 
    /** 
    * Tests Remove Friend error path: 
    * Removing a non-existing friendship should fail. 
    */ 
    @Test 
    public void testRemoveNonExistingFriend() { 
        DBOperation dbOps = getDbOps(); 
        int userId = 1; 
        int fakeFriendId = 9999; 
        boolean result = dbOps.deleteFriendship(userId, fakeFriendId); 
        assertFalse("Should fail when friendship does not exist", result); 
    } 
    /** 
    * Tests Create Group locally using the Group class. 
    */ 
    @Test 
    public void testCreateLocalGroup() { 
        User user1 = new User(); 
        user1.setUserID(1); 
        user1.setUsername("PlayerOne"); 
        User user2 = new User(); 
        user2.setUserID(2); 
        user2.setUsername("PlayerTwo"); 
        Group group = new Group(); 
        group.getGroupMembers().add(user1); 
        group.getGroupMembers().add(user2); 
        assertEquals("Group should contain 2 members", 2, group.getGroupMembers().size()); 
        assertTrue("Group should contain user1", group.getGroupMembers().contains(user1)); 
        assertTrue("Group should contain user2", group.getGroupMembers().contains(user2)); 
    } 
} 
