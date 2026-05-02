package src;

import java.util.List;

public interface DBOperation {
    boolean insertUser(User user);
    User getUserByUsername(String username);
    boolean updateUserProfile(User user);
    boolean deleteUser(String username);
    boolean deleteUser(int userID);
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
    List<User> getIncomingFriendRequests(int userID);
    boolean acceptFriendRequest(int userID, int senderID);
    boolean declineFriendRequest(int userID, int senderID);
    Community getRandomCommunity();
    Community getRandomCommunityByGenre(String genre);
    boolean joinCommunity(int userId, int communityId);
    List<Community> getJoinedCommunities(int userId);
    List<Post> getCommunityPosts(int communityId);
    boolean createPost(int communityId, int authorId, String textContent);
    boolean likePost(int postId, int userId);
    boolean dislikePost(int postId, int userId);
    boolean clearInteraction(int postId, int userId);
    List<Comment> getCommentsByPostId(int postId);
    boolean insertComment(int postId, int authorId, String text);
}