package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLOperation implements DBOperation {

    private DatabaseConnection dbConnection;

    // Constructor to inject the connection dependency
    public SQLOperation(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // ==========================================
    // 1. USER MANAGEMENT & AUTHENTICATION
    // ==========================================

    @Override
    public boolean insertUser(User user) {
        String sql = "INSERT INTO Users (username, email, password_hash, age, bio) VALUES (?, ?, ?, ?, ?)";
        Connection conn = dbConnection.getConnection();
        
        // Notice the String array here instead of Statement.RETURN_GENERATED_KEYS
        try (PreparedStatement pstmt = conn.prepareStatement(sql, new String[]{"user_id"})) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setInt(4, user.getAge());
            pstmt.setString(5, user.getBio());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Now this will safely grab the numeric ID!
                        user.setUserId(generatedKeys.getInt(1)); 
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

    @Override
    public User getUserByUsername(String username) {
        String sql = "SELECT * FROM Users WHERE username = ?";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setAge(rs.getInt("age"));
                    user.setBio(rs.getString("bio"));
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateUserProfile(User user) {
        String sql = "UPDATE Users SET email = ?, age = ?, bio = ? WHERE user_id = ?";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setInt(2, user.getAge());
            pstmt.setString(3, user.getBio());
            pstmt.setInt(4, user.getUserId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteUser(String username) {
        String sql = "DELETE FROM Users WHERE username = ?";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // 2. SOCIAL CONNECTIONS & FRIENDS
    // ==========================================

    @Override
    public boolean insertFriendRequest(int senderId, int receiverId) {
        String sql = "INSERT INTO FriendRequests (sender_id, receiver_id) VALUES (?, ?)";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, senderId);
            pstmt.setInt(2, receiverId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteFriendRequest(int senderId, int receiverId) {
        String sql = "DELETE FROM FriendRequests WHERE sender_id = ? AND receiver_id = ?";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, senderId);
            pstmt.setInt(2, receiverId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean insertFriendship(int userId1, int userId2) {
        String sql = "INSERT INTO Friends (user1_id, user2_id) VALUES (?, ?)";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId1);
            pstmt.setInt(2, userId2);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteFriendship(int userId1, int userId2) {
        String sql = "DELETE FROM Friends WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
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

    @Override
    public boolean insertBlockedUser(String blockerUsername, String blockedUsername) {
        // You will likely need to join with Users to convert usernames to IDs based on your schema
        String sql = "INSERT INTO BlockedUsers (blocker_username, blocked_username) VALUES (?, ?)";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, blockerUsername);
            pstmt.setString(2, blockedUsername);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // 3. POSTS & COMMENTS
    // ==========================================

    @Override
    public boolean insertPost(Post post) {
        String sql = "INSERT INTO Posts (author_id, community_id, text_content, likes_count, dislikes_count) VALUES (?, ?, ?, ?, ?)";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, post.getAuthor().getUserId());
            
            // Community handling logic... assuming getCommunity exists
            pstmt.setNull(2, java.sql.Types.INTEGER); 
            
            pstmt.setString(3, post.getTextContent());
            pstmt.setInt(4, post.getLikeCount());
            pstmt.setInt(5, post.getDislikeCount());

            int affectedRows = pstmt.executeUpdate();
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

    @Override
    public List<Post> getPostsByUserId(int authorId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM Posts WHERE author_id = ?";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, authorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User author = new User();
                    author.setUserId(rs.getInt("author_id"));
                    
                    Post post = new Post(
                        rs.getInt("post_id"),
                        author,
                        null,
                        rs.getString("text_content"),
                        rs.getInt("likes_count"),
                        rs.getInt("dislikes_count"),
                        new ArrayList<>()
                    );
                    posts.add(post);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    @Override
    public boolean updatePostVotes(int postId, boolean isLike) {
        String sql = isLike ? "UPDATE Posts SET likes_count = likes_count + 1 WHERE post_id = ?" 
                            : "UPDATE Posts SET dislikes_count = dislikes_count + 1 WHERE post_id = ?";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean insertComment(Comment comment, int parentPostId) {
        String sql = "INSERT INTO Comments (post_id, author_id, text_content, likes_count, dislikes_count) VALUES (?, ?, ?, ?, ?)";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, parentPostId);
            pstmt.setInt(2, comment.getAuthor().getUserId());
            pstmt.setString(3, comment.getTextContent());
            pstmt.setInt(4, comment.getLikesCount());
            pstmt.setInt(5, comment.getDislikeCount());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        comment.setCommentId(generatedKeys.getInt(1));
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

    @Override
    public boolean updateCommentVotes(int commentID, boolean isLike) {
        String sql = isLike ? "UPDATE Comments SET likes_count = likes_count + 1 WHERE comment_id = ?" 
                            : "UPDATE Comments SET dislikes_count = dislikes_count + 1 WHERE comment_id = ?";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, commentID);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ==========================================
    // 4. COMMUNITIES & GROUPS
    // ==========================================

    @Override
    public boolean insertGroupMember(int groupId, int userId) {
        String sql = "INSERT INTO GroupMembers (group_id, user_id) VALUES (?, ?)";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Community> getCommunitiesByName(String nameQuery) {
        List<Community> communities = new ArrayList<>();
        String sql = "SELECT * FROM Communities WHERE name LIKE ?";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + nameQuery + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Populate Community logic here
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return communities;
    }

    @Override
    public User loginUser(String email, String password) {
        // Note: In a production app, you would hash the input password and compare hashes.
        String sql = "SELECT * FROM Users WHERE email = ? AND password_hash = ?";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setPasswordHash(rs.getString("password_hash"));
                    user.setAge(rs.getInt("age"));
                    user.setBio(rs.getString("bio"));
                    return user; // Login successful
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Login failed
    }
}