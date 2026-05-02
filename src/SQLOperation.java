package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
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
        try (PreparedStatement pstmt = conn.prepareStatement(sql, new String[] { "user_id" })) {
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
                        user.setUserID(generatedKeys.getInt(1));
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
                    user.setUserID(rs.getInt("user_id"));
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
            pstmt.setInt(4, user.getUserID());

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

    @Override
    public boolean deleteUser(int userID) {
        String sql = "DELETE FROM Users WHERE user_id = ?";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
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
    public boolean insertFriendship(int userID1, int userID2) {
        String sql = "INSERT INTO Friends (user1_id, user2_id) VALUES (?, ?)";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID1);
            pstmt.setInt(2, userID2);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteFriendship(int userID1, int userID2) {
        String sql = "DELETE FROM Friends WHERE (user1_id = ? AND user2_id = ?) OR (user1_id = ? AND user2_id = ?)";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID1);
            pstmt.setInt(2, userID2);
            pstmt.setInt(3, userID2);
            pstmt.setInt(4, userID1);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean insertBlockedUser(String blockerUsername, String blockedUsername) {
        // You will likely need to join with Users to convert usernames to IDs based on
        // your schema
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
            pstmt.setInt(1, post.getAuthor().getUserID());

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
    public List<Post> getPostsByUserID(int authorId) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM Posts WHERE author_id = ?";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, authorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User author = new User();
                    author.setUserID(rs.getInt("author_id"));

                    Post post = new Post(
                            rs.getInt("post_id"),
                            author,
                            null,
                            rs.getString("text_content"),
                            rs.getInt("likes_count"),
                            rs.getInt("dislikes_count"),
                            new ArrayList<>());
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
            pstmt.setInt(2, comment.getAuthor().getUserID());
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
    public boolean insertGroupMember(int groupId, int userID) {
        String sql = "INSERT INTO GroupMembers (group_id, user_id) VALUES (?, ?)";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            pstmt.setInt(2, userID);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Community> getCommunitiesByName(String nameQuery) {
        List<Community> communities = new ArrayList<>();
        String sql = "SELECT * FROM Communities WHERE name LIKE ?";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // The % signs act as wildcards, so "rpg" will match "Action RPG" and "rpg
            // maker"
            pstmt.setString(1, "%" + nameQuery + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // 1. Create a new Community object
                    Community community = new Community();

                    // 2. Extract basic fields
                    // Make sure "communityID" and "name" exactly match your database column names
                    community.setCommunityID(rs.getInt("community_ID"));
                    community.setName(rs.getString("name"));

                    // 3. Extract genres (Assuming they are stored as a comma-separated string in
                    // the DB)
                    // If you store genres in a separate table, you would leave this as an empty
                    // list for now.
                    String genresRaw = rs.getString("genres");
                    if (genresRaw != null && !genresRaw.trim().isEmpty()) {
                        List<String> genresList = Arrays.asList(genresRaw.split("\\s*,\\s*"));
                        community.setGenres(new ArrayList<>(genresList));
                    } else {
                        community.setGenres(new ArrayList<>());
                    }

                    // 4. Initialize empty lists for complex relations to avoid
                    // NullPointerExceptions later
                    // You would typically write separate methods like getMembersByCommunityId() to
                    // fill these later.
                    community.setMembers(new ArrayList<>());
                    community.setCommunityPosts(new ArrayList<>());

                    // 5. Add the fully populated object to our list
                    communities.add(community);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching communities by name: " + e.getMessage());
            e.printStackTrace();
        }

        return communities;
    }

    @Override
    public User loginUser(String email, String password) {
        // Note: In a production app, you would hash the input password and compare
        // hashes.
        String sql = "SELECT * FROM Users WHERE email = ? AND password_hash = ?";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserID(rs.getInt("user_id"));
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

    @Override
    public List<User> getIncomingFriendRequests(int receiverId) {
        List<User> senders = new ArrayList<>();
        Connection conn = dbConnection.getConnection();
        
        // This query joins the Users table with the FriendRequests table 
        // to grab the full User profile of the person who sent the request.
        String sql = "SELECT u.* FROM Users u JOIN FriendRequests fr ON u.USER_ID = fr.SENDER_ID WHERE fr.RECEIVER_ID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, receiverId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserID(rs.getInt("USER_ID"));
                    u.setUsername(rs.getString("USERNAME"));
                    // ... set any other fields you want to show
                    senders.add(u);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching requests: " + e.getMessage());
        }
        return senders;
    }

    @Override
    public boolean acceptFriendRequest(int receiverId, int senderId) {
        Connection conn = dbConnection.getConnection();
        
        // We have to do TWO things here: 
        // 1. Add them to the Friends table.
        // 2. Delete the pending request from the FriendRequests table.
        String insertFriendSql = "INSERT INTO Friends (USER1_ID, USER2_ID) VALUES (?, ?)";
        String deleteRequestSql = "DELETE FROM FriendRequests WHERE SENDER_ID = ? AND RECEIVER_ID = ?";
        
        try {
            // It's good practice to turn off auto-commit when doing multiple linked updates
            conn.setAutoCommit(false);
            
            try (PreparedStatement insertStmt = conn.prepareStatement(insertFriendSql);
                 PreparedStatement deleteStmt = conn.prepareStatement(deleteRequestSql)) {
                
                // 1. Insert into friends table
                insertStmt.setInt(1, receiverId);
                insertStmt.setInt(2, senderId);
                insertStmt.executeUpdate();
                
                // 2. Remove the request
                deleteStmt.setInt(1, senderId);
                deleteStmt.setInt(2, receiverId);
                deleteStmt.executeUpdate();
                
                conn.commit(); // Make changes permanent
                return true;
            } catch (SQLException e) {
                conn.rollback(); // If something failed, undo any partial changes
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true); // Put it back to normal
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean declineFriendRequest(int receiverId, int senderId) {
        Connection conn = dbConnection.getConnection();
        String deleteRequestSql = "DELETE FROM FriendRequests WHERE SENDER_ID = ? AND RECEIVER_ID = ?";
        
        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteRequestSql)) {
            deleteStmt.setInt(1, senderId);
            deleteStmt.setInt(2, receiverId);
            
            int rowsAffected = deleteStmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error declining request: " + e.getMessage());
            return false;
        }
    }

    @Override
    public Community getRandomCommunity() {
        // Oracle syntax to grab exactly 1 random row
        String sql = "SELECT * FROM Communities ORDER BY DBMS_RANDOM.VALUE FETCH NEXT 1 ROWS ONLY";
        return fetchSingleCommunity(sql, null);
    }

    @Override
    public Community getRandomCommunityByGenre(String genre) {
        String sql = "SELECT * FROM Communities WHERE LOWER(GENRES) LIKE LOWER(?) ORDER BY DBMS_RANDOM.VALUE FETCH NEXT 1 ROWS ONLY";
        return fetchSingleCommunity(sql, "%" + genre + "%");
    }

    // --- HELPER METHOD TO PREVENT CODE DUPLICATION ---
    private Community fetchSingleCommunity(String sql, String searchParam) {
        Connection conn = dbConnection.getConnection();
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (searchParam != null) {
                pstmt.setString(1, searchParam);
            }
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Community c = new Community();
                    c.setCommunityID(rs.getInt("COMMUNITY_ID"));
                    c.setName(rs.getString("NAME"));
                    
                    String genresRaw = rs.getString("GENRES");
                    if (genresRaw != null && !genresRaw.trim().isEmpty()) {
                        c.setGenres(new ArrayList<>(Arrays.asList(genresRaw.split("\\s*,\\s*"))));
                    } else {
                        c.setGenres(new ArrayList<>());
                    }
                    return c;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching community: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean joinCommunity(int userId, int communityId) {
        String sql = "INSERT INTO CommunityMembers (COMMUNITY_ID, USER_ID) VALUES (?, ?)";
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, communityId);
            pstmt.setInt(2, userId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            // If the user is already in the community, the Primary Key constraint will throw an error here.
            // We catch it and return false instead of crashing the program.
            return false;
        }
    }

    @Override
    public List<Community> getJoinedCommunities(int userId) {
        List<Community> communities = new ArrayList<>();
        Connection conn = dbConnection.getConnection();
        
        // This query joins Communities with CommunityMembers to find what the user joined
        String sql = "SELECT c.* FROM Communities c " +
                     "JOIN CommunityMembers cm ON c.COMMUNITY_ID = cm.COMMUNITY_ID " +
                     "WHERE cm.USER_ID = ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Community c = new Community();
                    c.setCommunityID(rs.getInt("COMMUNITY_ID"));
                    c.setName(rs.getString("NAME"));
                    
                    String genresRaw = rs.getString("GENRES");
                    if (genresRaw != null && !genresRaw.trim().isEmpty()) {
                        c.setGenres(new ArrayList<>(Arrays.asList(genresRaw.split("\\s*,\\s*"))));
                    } else {
                        c.setGenres(new ArrayList<>());
                    }
                    communities.add(c);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching joined communities: " + e.getMessage());
        }
        return communities;
    }

    @Override
    public List<Post> getCommunityPosts(int communityId) {
        List<Post> posts = new ArrayList<>();
        Connection conn = dbConnection.getConnection();
        
        // Fetch posts for this community, newest first (ORDER BY POST_ID DESC)
        String sql = "SELECT * FROM Posts WHERE COMMUNITY_ID = ? ORDER BY POST_ID DESC";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, communityId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Post p = new Post();
                    p.setPostID(rs.getInt("POST_ID"));
                    p.setTextContent(rs.getString("TEXT_CONTENT"));
                    p.setLikeCount(rs.getInt("LIKES_COUNT"));
                    p.setDislikeCount(rs.getInt("DISLIKES_COUNT"));
                    // You could also fetch the Author's username here using a JOIN if you want!
                    posts.add(p);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching posts: " + e.getMessage());
        }
        return posts;
    }

    public boolean createPost(int communityId, int authorId, String textContent) {
        String sql = "INSERT INTO POSTS (COMMUNITY_ID, AUTHOR_ID, TEXT_CONTENT, LIKES_COUNT, DISLIKES_COUNT) VALUES (?, ?, ?, ?, ?)";
        
        // MOVED OUTSIDE
        Connection conn = dbConnection.getConnection(); 
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, communityId);
            pstmt.setInt(2, authorId);
            pstmt.setString(3, textContent);
            pstmt.setInt(4, 0); 
            pstmt.setInt(5, 0); 
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error creating post: " + e.getMessage());
            return false;
        }
    }

    public boolean createPost(Post newPost) {
    // FIXED: Changed table to POSTS and columns to LIKES_COUNT and DISLIKES_COUNT
    String sql = "INSERT INTO POSTS (TEXT_CONTENT, AUTHOR_ID, LIKES_COUNT, DISLIKES_COUNT) VALUES (?, ?, ?, ?)";
    
    // Assuming you have a way to get your connection, like your DatabaseConnection class
    try (Connection conn = dbConnection.getConnection(); 
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, newPost.getTextContent());
        pstmt.setInt(2, newPost.getAuthor().getUserID()); 
        pstmt.setInt(3, 0); // New posts start with 0 likes
        pstmt.setInt(4, 0); // New posts start with 0 dislikes
        
        int rowsAffected = pstmt.executeUpdate();
        return rowsAffected == 1;
        
    } catch (SQLException e) {
        System.err.println("Error creating post: " + e.getMessage());
        e.printStackTrace();
        return false;
    }
}

@Override
    public boolean likePost(int postId, int userId) {
        Connection conn = dbConnection.getConnection(); 
        
        // 1. Check if they have already interacted with this post
        String checkSql = "SELECT INTERACTION_TYPE FROM POST_INTERACTIONS WHERE POST_ID = ? AND USER_ID = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, postId);
            checkStmt.setInt(2, userId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("You have already interacted with this post!");
                    return false; 
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking interaction: " + e.getMessage());
            return false;
        }

        // 2. If no record was found, log the new 'LIKE' and update the count
        String insertSql = "INSERT INTO POST_INTERACTIONS (POST_ID, USER_ID, INTERACTION_TYPE) VALUES (?, ?, 'LIKE')";
        String updateSql = "UPDATE POSTS SET LIKES_COUNT = LIKES_COUNT + 1 WHERE POST_ID = ?";
        
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            
            // Insert the tracking record
            insertStmt.setInt(1, postId);
            insertStmt.setInt(2, userId);
            insertStmt.executeUpdate();
            
            // Increment the counter
            updateStmt.setInt(1, postId);
            int rowsAffected = updateStmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error liking post: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean dislikePost(int postId, int userId) {
        Connection conn = dbConnection.getConnection();
        
        // 1. Check if they have already interacted
        String checkSql = "SELECT INTERACTION_TYPE FROM POST_INTERACTIONS WHERE POST_ID = ? AND USER_ID = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, postId);
            checkStmt.setInt(2, userId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("You have already interacted with this post!");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking interaction: " + e.getMessage());
            return false;
        }

        // 2. Log the 'DISLIKE' and update the count
        String insertSql = "INSERT INTO POST_INTERACTIONS (POST_ID, USER_ID, INTERACTION_TYPE) VALUES (?, ?, 'DISLIKE')";
        String updateSql = "UPDATE POSTS SET DISLIKES_COUNT = DISLIKES_COUNT + 1 WHERE POST_ID = ?";
        
        try (PreparedStatement insertStmt = conn.prepareStatement(insertSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
            
            insertStmt.setInt(1, postId);
            insertStmt.setInt(2, userId);
            insertStmt.executeUpdate();
            
            updateStmt.setInt(1, postId);
            int rowsAffected = updateStmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error disliking post: " + e.getMessage());
            return false;
        }
    }
    @Override
    public boolean clearInteraction(int postId, int userId) {
        Connection conn = dbConnection.getConnection();
        String findSql = "SELECT INTERACTION_TYPE FROM POST_INTERACTIONS WHERE POST_ID = ? AND USER_ID = ?";
        String deleteSql = "DELETE FROM POST_INTERACTIONS WHERE POST_ID = ? AND USER_ID = ?";

        try (PreparedStatement findStmt = conn.prepareStatement(findSql)) {
            findStmt.setInt(1, postId);
            findStmt.setInt(2, userId);

            try (ResultSet rs = findStmt.executeQuery()) {
                if (rs.next()) {
                    String type = rs.getString("INTERACTION_TYPE");
                    
                    // Choose the correct column to decrement
                    String column = type.equalsIgnoreCase("LIKE") ? "LIKES_COUNT" : "DISLIKES_COUNT";
                    String updateSql = "UPDATE POSTS SET " + column + " = " + column + " - 1 WHERE POST_ID = ?";

                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                         PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        
                        // 1. Remove from ledger
                        deleteStmt.setInt(1, postId);
                        deleteStmt.setInt(2, userId);
                        deleteStmt.executeUpdate();

                        // 2. Decrement the counter
                        updateStmt.setInt(1, postId);
                        return updateStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error clearing interaction: " + e.getMessage());
        }
        return false;
    }
}