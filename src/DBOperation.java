package src;

import java.util.List;

public interface DBOperation {
    boolean insertUser(User user);
    User getUserByUsername(String username);
    boolean updateUserProfile(User user);
    boolean deleteUser(String username);
    boolean insertFriendRequest(int senderID, int receiverID);
    boolean deleteFriendRequest(int senderID, int receiverID);
    boolean insertFriendship(int userID1, int userID2);
    boolean deleteFriendship(int userID1, int userID2);
    boolean insertBlockedUser(String blockerUsername, String blockedUsername);
    boolean insertPost(Post post);
    List<Post> getPostsByUserID(int authorID);
    boolean updatePostVotes(int postID, boolean isLike);
    boolean insertComment(Comment comment, int parentPostID);
    boolean updateCommentVotes(int commentID, boolean isLike);
    boolean insertGroupMember(int groupID, int userID);
    List<Community> getCommunitiesByName(String nameQuery);
    User loginUser(String email, String password);
}