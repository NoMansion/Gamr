package src;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Service {

    private DBOperation dbOp;
    private User currentUser;

    public Service(DBOperation dbOp) {
        this.dbOp = dbOp;
        this.currentUser = null;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean createAccount(User user) {
        if (user == null) {
            return false;
        }
        return dbOp.insertUser(user);
    }

    public User login(String email, String password) {
        User user = dbOp.loginUser(email, password);
        if (user != null) {
            this.currentUser = user;
        }
        return user;
    }

    public void logout() {
        if (this.currentUser != null) {
            System.out.println("User " + currentUser.getUsername() + " logged out.");
            this.currentUser = null;
        }
    }

    public boolean deleteUser(int userID) {
        return dbOp.deleteUser(userID);
    }

    public boolean updateUserProfile(User user) {
        if (user == null) {
            return false;
        }
        return dbOp.updateUserProfile(user);
    }

    public void blockUser(User userToBlock) {
        if (currentUser == null || userToBlock == null) {
            return;
        }
        boolean success = dbOp.insertBlockedUser(currentUser.getUsername(), userToBlock.getUsername());
        if (success) {
            System.out.println("Blocked user: " + userToBlock.getUsername());
        }
    }

    public void skipAccount(User viewer, User viewAccount) {
        if (viewer == null || viewAccount == null) {
            return;
        }
        System.out.println(viewer.getUsername() + " skipped " + viewAccount.getUsername() + " in recommendations.");
    }

    public boolean insertFriendRequest(int senderID, int receiverID) {
        return dbOp.insertFriendRequest(senderID, receiverID);
    }

    public boolean acceptFriendRequest(int userID, int senderID) {
        return dbOp.acceptFriendRequest(userID, senderID);
    }

    public boolean declineFriendRequest(int userID, int senderID) {
        return dbOp.declineFriendRequest(userID, senderID);
    }

    public List<User> getIncomingFriendRequests(int userID) {
        return dbOp.getIncomingFriendRequests(userID);
    }

    public User getUserByUsername(String username) {
        return dbOp.getUserByUsername(username);
    }

    public List<Community> getJoinedCommunities(User user) {
        if (user == null || user.getUserID() <= 0) {
            return new ArrayList<>();
        }
        return dbOp.getJoinedCommunities(user.getUserID());
    }

    public Community getRandomCommunity() {
        return dbOp.getRandomCommunity();
    }

    public Community getRandomCommunityByGenre(String genre) {
        return dbOp.getRandomCommunityByGenre(genre);
    }

    public List<Community> getCommunitiesByName(String nameQuery) {
        return dbOp.getCommunitiesByName(nameQuery);
    }

    public boolean joinCommunity(int userId, int communityId) {
        return dbOp.joinCommunity(userId, communityId);
    }

    public List<Post> getCommunityPosts(Community community) {
        if (community == null || community.getCommunityID() <= 0) {
            return new ArrayList<>();
        }
        return dbOp.getCommunityPosts(community.getCommunityID());
    }

    public void createPost(Community community, User author, String textContent) {
        if (textContent == null || textContent.trim().isEmpty()) {
            System.out.println("Error: Post content cannot be empty.");
            return;
        }
        boolean success = dbOp.createPost(community.getCommunityID(), author.getUserID(), textContent);
        if (success) {
            System.out.println("✅ Post published successfully to " + community.getName() + "!");
        } else {
            System.out.println("❌ Failed to publish post.");
        }
    }

    public List<Comment> getCommentsByPostId(int postId) {
        return dbOp.getCommentsByPostId(postId);
    }

    public boolean updatePostVotes(int postId, boolean isLike) {
        return dbOp.updatePostVotes(postId, isLike);
    }

    public boolean insertComment(int postId, int authorId, String text) {
        return dbOp.insertComment(postId, authorId, text);
    }

    public boolean updateCommentVotes(int commentId, boolean isLike) {
        return dbOp.updateCommentVotes(commentId, isLike);
    }

    public List<Post> retrieveUserPosts(User user) {
        return dbOp.getPostsByUserID(user.getUserID());
    }

    public void sendFriendRequest(User sender, User receiver) {
        boolean success = dbOp.insertFriendRequest(sender.getUserID(), receiver.getUserID());
        if (success) {
            System.out.println("Friend request sent to " + receiver.getUsername());
        }
    }

    public void acceptFriendship(User user, User sender) {
        boolean success = dbOp.insertFriendship(user.getUserID(), sender.getUserID());
        if (success) {
            System.out.println("Friend request accepted from " + sender.getUsername());
        }
    }

    public void unaddFriend(User user, User friendToRemove) {
        boolean success = dbOp.deleteFriendship(user.getUserID(), friendToRemove.getUserID());
        if (success) {
            System.out.println("Removed friend: " + friendToRemove.getUsername());
        }
    }

    public List<User> retrieveMutualFriends(User user1, User user2) {
        System.out.println("Retrieving mutual friends...");
        return new ArrayList<>();
    }

    public List<User> retrieveFriendRecommendations(User user) {
        System.out.println("Fetching recommendations for " + user.getUsername());
        return new ArrayList<>();
    }

    public List<User> filterRecommendationsByAge(List<User> recommendations, int targetAge) {
        return recommendations.stream()
                .filter(u -> Math.abs(u.getAge() - targetAge) <= 2)
                .collect(Collectors.toList());
    }

    public List<User> filterRecommendationsByGenre(List<User> recommendations, String genre) {
        return recommendations.stream()
                .filter(u -> u.getFavoriteGenres() != null && u.getFavoriteGenres().contains(genre))
                .collect(Collectors.toList());
    }

    public List<User> filterRecommendationsByGame(List<User> recommendations, String game) {
        return recommendations.stream()
                .filter(u -> u.getFavoriteGames() != null && u.getFavoriteGames().contains(game))
                .collect(Collectors.toList());
    }

    public void sendEmailPingToFriend(User sender, User friend) {
        System.out.println("Ping! " + sender.getUsername() + " sent an email notification to " + friend.getUsername());
    }

    public void sendEmailPingToGroup(User sender, Group group) {
        System.out.println("Ping! " + sender.getUsername() + " notified everyone in group ID: " + group.getGroupId());
    }

    public void searchCommunitiesByName(String nameQuery) {
        List<Community> results = dbOp.getCommunitiesByName(nameQuery);
        System.out.println("Found " + results.size() + " communities matching: " + nameQuery);
    }

    public void filterCommunitySearchByGenre(List<Community> communities, String genre) {
        List<Community> filtered = communities.stream()
                .filter(c -> c.getGenres() != null && c.getGenres().contains(genre))
                .collect(Collectors.toList());
        System.out.println("Filtered down to " + filtered.size() + " communities for genre: " + genre);
    }
}
