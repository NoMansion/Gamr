// Written by Kyle Flatt

import java.util.ArrayList;
import java.util.List;

public class Group {

    // Attributes
    private List<User> groupMembers;

    // Constructor
    public Group() {
        this.groupMembers = new ArrayList<>();
    }

    public Group(List<User> groupMembers) {
        this.groupMembers = groupMembers;
    }

    // Methods
    public void addFriendsToGroup(List<User> users) {
        // add given users to the group
    }

    public void removeMember(User user) {
        // remove user from the group
    }

    // getters/setters
    public List<User> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<User> groupMembers) {
        this.groupMembers = groupMembers;
    }
}