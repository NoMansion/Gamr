package src;

import java.util.List;

public interface DBOperation {
    boolean insertUser(User user);
    User getUserByUsername(String username);
    boolean updateUserProfile(User user);
    boolean deleteUser(String username);
    boolean insertFriendRequest(int senderId, int receiverId);
    boolean deleteFriendRequest(int senderId, int receiverId);
    boolean insertFriendship(int userId1, int userId2);
    boolean deleteFriendship(int userId1, int userId2);
    boolean insertBlockedUser(String blockerUsername, String blockedUsername);
    boolean insertPost(Post post);
    List<Post> getPostsByUserId(int authorId);
    boolean updatePostVotes(int postId, boolean isLike);
    boolean insertComment(Comment comment, int parentPostId);
    boolean updateCommentVotes(int commentID, boolean isLike);
    boolean insertGroupMember(int groupId, int userId);
    List<Community> getCommunitiesByName(String nameQuery);
    User loginUser(String email, String password);
}