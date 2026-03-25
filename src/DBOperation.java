package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBOperation {

    // ==========================================
    // 1. USER MANAGEMENT & AUTHENTICATION
    // ==========================================

    public boolean insertUser(User user) {
        // userID is omitted from the INSERT statement assuming it is an AUTO_INCREMENT column in the database
        String sql = "INSERT INTO Users (username, email, password_hash, age, bio) VALUES (?, ?, ?, ?, ?)";
        
        // We use RETURN_GENERATED_KEYS to fetch the new integer ID that the database assigned
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setInt(4, user.getAge());
            pstmt.setString(5, user.getBio());

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setUserID(generatedKeys.getInt(1)); // Update the object with its new DB ID
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            System.err.println("Error inserting user: " + user.getUsername());
            e.printStackTrace();
            return false;
        }
    }

    public User getUserByUsername(String username) {
        // Still useful for logging in, as users type their username, not their ID
        String sql = "SELECT * FROM Users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserID(rs.getInt("user_id")); // Fetching the new ID!
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setAge(rs.getInt("age"));
                    user.setBio(rs.getString("bio"));
                    return user;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user: " + username);
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateUserProfile(User user) {
        // Now we can update using the fast integer userID
        String sql = "UPDATE Users SET email = ?, age = ?, bio = ? WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, user.getEmail());
            pstmt.setInt(2, user.getAge());
            pstmt.setString(3, user.getBio());
            pstmt.setInt(4, user.getUserID());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating profile for userID: " + user.getUserID());
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // 2. SOCIAL CONNECTIONS & FRIENDS
    // ==========================================

    public boolean insertFriendRequest(int senderId, int receiverId) {
        // Linking using integer IDs
        String sql = "INSERT INTO FriendRequests (sender_id, receiver_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, senderId);
            pstmt.setInt(2, receiverId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertFriendship(int userId1, int userId2) {
        String sql = "INSERT INTO Friends (user1_id, user2_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId1);
            pstmt.setInt(2, userId2);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFriendship(int userId1, int userId2) {
        String sql = "DELETE FROM Friends WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId1);
            pstmt.setInt(2, userId2);
            pstmt.setInt(3, userId2);
            pstmt.setInt(4, userId1);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // 3. POSTS & COMMENTS
    // ==========================================

    public boolean insertPost(Post post) {
        // We now store author_id and community_id as foreign keys
        String sql = "INSERT INTO Posts (author_id, community_id, text_content, likes_count, dislikes_count) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, post.getAuthor().getUserID());
            
            // Handle optional community (if a post is made directly to a user's feed, it might not have a community)
            if (post.getCommunity() != null) {
                pstmt.setInt(2, post.getCommunity().getCommunityID());
            } else {
                pstmt.setNull(2, java.sql.Types.INTEGER);
            }
            
            pstmt.setString(3, post.getTextContent());
            pstmt.setInt(4, post.getLikeCount());
            pstmt.setInt(5, post.getDislikeCount());

            int affectedRows = pstmt.executeUpdate();
            
            // Assign the newly generated DB postID back to the Java object
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        post.setPostID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Post> getPostsByUserId(int authorId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM Posts WHERE author_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, authorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Post post = new Post();
                    post.setPostID(rs.getInt("post_id"));
                    post.setTextContent(rs.getString("text_content"));
                    post.setLikeCount(rs.getInt("likes_count"));
                    post.setDislikeCount(rs.getInt("dislikes_count"));
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public boolean updatePostVotes(int postId, boolean isLike) {
        String sql = isLike ? "UPDATE Posts SET likes_count = likes_count + 1 WHERE post_id = ?" 
                            : "UPDATE Posts SET dislikes_count = dislikes_count + 1 WHERE post_id = ?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertComment(Comment comment, int parentPostId) {
        String sql = "INSERT INTO Comments (post_id, author_id, text_content, likes_count, dislikes_count) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, parentPostId);
            pstmt.setInt(2, comment.getAuthor().getUserID());
            pstmt.setString(3, comment.getTextContent());
            pstmt.setInt(4, comment.getLikesCount());
            pstmt.setInt(5, comment.getDislikeCount());

            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        comment.setCommentID(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // 4. COMMUNITIES & GROUPS
    // ==========================================

    public boolean insertGroupMember(int groupId, int userId) {
        String sql = "INSERT INTO GroupMembers (group_id, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, groupId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}