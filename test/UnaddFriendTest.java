package test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import src.*;

public class UnaddFriendTest {

    private DBOperation dbOp;
    private Service service;

    @BeforeEach
    void setUp() {
        dbOp = mock(DBOperation.class);
        service = new Service(dbOp);
    }

    // Path 1: user is null
    @Test
    void testUnaddFriend_NullUser() {
        User friendToRemove = new User();
        friendToRemove.setUserID(2);
        friendToRemove.setUsername("friendUser");

        service.unaddFriend(null, friendToRemove);

        verify(dbOp, never()).deleteFriendship(anyInt(), anyInt());
    }

    // Path 2: friendToRemove is null
    @Test
    void testUnaddFriend_NullFriend() {
        User user = new User();
        user.setUserID(1);
        user.setUsername("testUser");

        service.unaddFriend(user, null);

        verify(dbOp, never()).deleteFriendship(anyInt(), anyInt());
    }

    // Path 3: both valid but deleteFriendship returns false
    @Test
    void testUnaddFriend_DeleteFails() {
        User user = new User();
        user.setUserID(1);
        user.setUsername("testUser");

        User friendToRemove = new User();
        friendToRemove.setUserID(2);
        friendToRemove.setUsername("friendUser");

        when(dbOp.deleteFriendship(1, 2)).thenReturn(false);

        service.unaddFriend(user, friendToRemove);

        verify(dbOp).deleteFriendship(1, 2);
    }

    // Path 4: both valid and deleteFriendship returns true
    @Test
    void testUnaddFriend_DeleteSucceeds() {
        User user = new User();
        user.setUserID(1);
        user.setUsername("testUser");

        User friendToRemove = new User();
        friendToRemove.setUserID(2);
        friendToRemove.setUsername("friendUser");

        when(dbOp.deleteFriendship(1, 2)).thenReturn(true);

        service.unaddFriend(user, friendToRemove);

        verify(dbOp).deleteFriendship(1, 2);
    }
}
