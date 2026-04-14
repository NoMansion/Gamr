package src;

import java.util.ArrayList;
import java.util.List;

public class Group {
    // Attributes
    private int groupId;
    private List<User> groupMembers;

    // Constructor
    public Group() {
        this.groupId = 0;
        this.groupMembers = new ArrayList<>();
    }

    public Group(List<User> groupMembers) {
        this.groupMembers = groupMembers;
    }

    // Methods
    public void addFriendsToGroup(List<User> users) {
        // add given users to the group
        if(users == null){
            return;
        }for(User user : users){
            if(user != null && !groupMembers.contains(user)){
                groupMembers.add(user);
            }
        }
    }

    public void addMember(User user){
        if(user != null && !groupMembers.contains(user)){
            groupMembers.add(user);
        }
    }

    public void removeMember(User user) {
        // remove user from the group
        if(user == null){
            return;
        }
        if(groupMembers.conatins(user)){
            groupMembers.remove(user);
        }
    }

    // getters/setters
    public List<User> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<User> groupMembers) {
        this.groupMembers = groupMembers;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupSize(){
        return groupMembers.size();
    }

    @Override
    public String toString(){
        return "Group{"+groupId+"}, group members = "+groupMembers;
    }
}