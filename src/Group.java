package src;

public class Group {
    private int groupId;
    private String groupName;
    private int creatorId;

    public Group() {}

    public Group(int groupId, String groupName, int creatorId) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.creatorId = creatorId;
    }

    public int getGroupId() { return groupId; }
    public void setGroupId(int groupId) { this.groupId = groupId; }
    
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    
    public int getCreatorId() { return creatorId; }
    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }
}