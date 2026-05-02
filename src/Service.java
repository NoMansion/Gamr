package src;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Service {

    private DBOperation dbOp;
    private User currentUser;

    // Inject the DBOperation dependency through the constructor
    public Service(DBOperation dbOp) {
        this.dbOp = dbOp;
        this.currentUser = null;
    }
    
    // You can also add a setter for currentUser so you can update it after login
    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
    // ==========================================
    // 1. ACCOUNT MANAGEMENT & AUTHENTICATION
    // ==========================================

    public void createAccount(String username, String email, String password, int age, List<String> games) {
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(password); // Should be hashed in production
        newUser.setAge(age);
        newUser.setBio("Hello, I am new here!"); 
        newUser.setFavoriteGames(games);

        boolean success = dbOp.insertUser(newUser);
        if (success) {
            System.out.println("Account created successfully for: " + username);
        } else {
            System.err.println("Failed to create account.");
        }
    }

    public void login(String username, String password) {
        User user = dbOp.getUserByUsername(username);
        if (user != null && user.getPasswordHash().equals(password)) {
            this.currentUser = user;
            System.out.println("Login successful. Welcome, " + username + "!");
        } else {
            System.err.println("Login failed.");
        }
    }

    public void logout() {
        if (this.currentUser != null) {
            System.out.println("User " + currentUser.getUsername() + " logged out.");
            this.currentUser = null;
        }
    }

    public void deleteAccount() {
        if (currentUser == null) return;
        boolean success = dbOp.deleteUser(currentUser.getUsername());
        if (success) {
            System.out.println("Account deleted.");
            this.currentUser = null;
        }
    }

    public void updateProfile(String bio, List<String> favoriteGames) {
        if (currentUser == null) return;
        currentUser.setBio(bio);
        currentUser.setFavoriteGames(favoriteGames);
        boolean success = dbOp.updateUserProfile(currentUser);
        if (success) System.out.println("Profile updated.");
    }

    public void blockUser(User userToBlock) {
        if (currentUser == null) return;
        boolean success = dbOp.insertBlockedUser(currentUser.getUsername(), userToBlock.getUsername());
        if (success) System.out.println("Blocked user: " + userToBlock.getUsername());
    }

    public void skipAccount(User viewer, User viewAccount) {
        // Logic to skip a user in recommendations (e.g., logging it so they don't show up again)
        System.out.println(viewer.getUsername() + " skipped " + viewAccount.getUsername() + " in recommendations.");
    }

    // ==========================================
    // 2. SOCIAL & FRIENDS
    // ==========================================

    public void sendFriendRequest(User sender, User receiver) {
        boolean success = dbOp.insertFriendRequest(sender.getUserID(), receiver.getUserID());
        if (success) System.out.println("Friend request sent to " + receiver.getUsername());
    }

    public void acceptFriendRequest(User user, User sender) {
        boolean success = dbOp.insertFriendship(user.getUserID(), sender.getUserID());
        if (success) System.out.println("Friend request accepted from " + sender.getUsername());
    }

    public void declineFriendRequest(User user, User sender) {
        // Assume dbOp has a method to remove the pending request
        System.out.println("Friend request declined from " + sender.getUsername());
    }

    public void unaddFriend(User user, User friendToRemove) {
        boolean success = dbOp.deleteFriendship(user.getUserID(), friendToRemove.getUserID());
        if (success) System.out.println("Removed friend: " + friendToRemove.getUsername());
    }

    public List<User> retrieveMutualFriends(User user1, User user2) {
        // In a real app, dbOp would execute a SQL query to find the intersection of their friends
        System.out.println("Retrieving mutual friends...");
        return new ArrayList<>(); 
    }

    public List<User> retrieveFriendRecommendations(User user) {
        // Fetch a base list of users from the DB to recommend
        System.out.println("Fetching recommendations for " + user.getUsername());
        return new ArrayList<>();
    }

    // ==========================================
    // 3. RECOMMENDATION FILTERS
    // ==========================================

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

    // ==========================================
    // 4. NOTIFICATIONS (PINGS)
    // ==========================================

    public void sendEmailPingToFriend(User sender, User friend) {
        // Integrates with an Email API or prints a simulation
        System.out.println("Ping! " + sender.getUsername() + " sent an email notification to " + friend.getUsername());
    }

    public void sendEmailPingToGroup(User sender, Group group) {
        System.out.println("Ping! " + sender.getUsername() + " notified everyone in group ID: " + group.getGroupId());
    }

    // ==========================================
    // 5. POSTS & COMMENTS
    // ==========================================

    public List<Post> retrieveUserPosts(User user) {
        return dbOp.getPostsByUserID(user.getUserID());
    }

    // Note: Added Post parameter so the service knows what to like
    public Post likePost(Post post) {
        dbOp.updatePostVotes(post.getPostID(), true);
        post.setLikeCount(post.getLikeCount() + 1);
        return post;
    }

    // Note: Added Post parameter
    public Post dislikePost(Post post) {
        dbOp.updatePostVotes(post.getPostID(), false);
        post.setDislikeCount(post.getDislikeCount() + 1);
        return post;
    }

    // Note: Added Post parameter so the DB can link the comment
    public void createComment(Post parentPost, User author, String textContent) {
        Comment comment = new Comment(-1, author, parentPost, textContent);
        dbOp.insertComment(comment, parentPost.getPostID());
    }

    // Note: Added Comment parameter
    public void likeComment(Comment comment) {
        dbOp.updateCommentVotes(comment.getCommentId(), true);
    }

    // Note: Added Comment parameter
    public void dislikeComment(Comment comment) {
        dbOp.updateCommentVotes(comment.getCommentId(), false);
    }

    // ==========================================
    // 6. COMMUNITIES & GROUPS
    // ==========================================

    public void addMember(User user) {
        // Needs a target community or group to add them to.
        System.out.println("Added " + user.getUsername() + " to the entity.");
    }

    // Note: Returning void based on your UML, though normally this returns a List<Community>
    public void searchCommunitiesByName(String nameQuery) {
        List<Community> results = dbOp.getCommunitiesByName(nameQuery);
        System.out.println("Found " + results.size() + " communities matching: " + nameQuery);
    }

    // Note: Returning void based on your UML
    public void filterCommunitySearchByGenre(List<Community> communities, String genre) {
        List<Community> filtered = communities.stream()
                .filter(c -> c.getGenres() != null && c.getGenres().contains(genre))
                .collect(Collectors.toList());
        System.out.println("Filtered down to " + filtered.size() + " communities for genre: " + genre);
    }

    // Getter for controllers to use
    public User getCurrentUser() {
        return this.currentUser;
    }

    public List<Community> getJoinedCommunities(User user) {
        if (user == null || user.getUserID() <= 0) return new ArrayList<>();
        return dbOp.getJoinedCommunities(user.getUserID());
    }

    public List<Post> getCommunityPosts(Community community) {
        if (community == null || community.getCommunityID() <= 0) return new ArrayList<>();
        return dbOp.getCommunityPosts(community.getCommunityID());
    }

    public void createPost(Community community, User author, String textContent) {
        // Business logic: don't allow empty posts!
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
}
