package src;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLOperation implements DBOperation {

    private DatabaseConnection dbConnection;

    public SQLOperation(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    // ==========================================
    // 1. USER MANAGEMENT & AUTHENTICATION
    // ==========================================

    @Override
    public boolean insertUser(User user) {
        String sql = "INSERT INTO Users (username, email, password_hash, age, bio, favorite_games, favorite_genres) VALUES (?, ?, ?, ?, ?, ?, ?)";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql, new String[] { "user_id" })) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setInt(4, user.getAge());
            pstmt.setString(5, user.getBio());
            pstmt.setString(6, String.join(",", user.getFavoriteGames()));
            pstmt.setString(7, String.join(",", user.getFavoriteGenres()));

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
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
        String sql = "UPDATE Users SET email = ?, age = ?, bio = ?, favorite_games = ?, favorite_genres = ? WHERE user_id = ?";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setInt(2, user.getAge());
            pstmt.setString(3, user.getBio());
            pstmt.setString(4, String.join(",", user.getFavoriteGames()));
            pstmt.setString(5, String.join(",", user.getFavoriteGenres()));
            pstmt.setInt(6, user.getUserID());

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

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("A friend request has already been sent to this user.");
            return false;

        } catch (SQLException e) {
            System.err.println("Database error occurred while sending friend request: " + e.getMessage());
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
    public boolean insertBlockedUser(int blockerId, int blockedId) {
        String sql = "INSERT INTO BlockedUsers (blocker_id, blocked_id) VALUES (?, ?)";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, blockerId);
            pstmt.setInt(2, blockedId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<User> getOnlineFriends(int userID) {
        List<User> onlineFriends = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.email, u.age, u.bio " +
                 "FROM Users u " +
                 "JOIN Friends f ON (f.user1_id = ? AND f.user2_id = u.user_id) " +
                 "               OR (f.user2_id = ? AND f.user1_id = u.user_id) " +
                 "WHERE u.is_online = 1";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            pstmt.setInt(2, userID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserID(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setAge(rs.getInt("age"));
                    user.setBio(rs.getString("bio"));
                    onlineFriends.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return onlineFriends;
    }

    @Override
    public List<User> getOfflineFriends(int userID) {
        List<User> friends = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.email, u.age, u.bio " +
                    "FROM Users u " +
                    "JOIN Friends f ON (f.user1_id = ? AND f.user2_id = u.user_id) " +
                    "               OR (f.user2_id = ? AND f.user1_id = u.user_id) " +
                    "WHERE u.is_online = 0";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            pstmt.setInt(2, userID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserID(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setAge(rs.getInt("age"));
                    user.setBio(rs.getString("bio"));
                    friends.add(user);
                }
            }   
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }


    @Override
    public boolean setUserOffline(int userID) {
        String sql = "UPDATE Users SET is_online = 0 WHERE user_id = ?";
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
        // LEFT JOIN pulls the community name if the post belongs to a community
        String sql = "SELECT p.*, c.name AS community_name FROM Posts p " +
                     "LEFT JOIN Communities c ON p.community_id = c.community_id " +
                     "WHERE p.author_id = ? ORDER BY p.post_id DESC";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, authorId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User author = new User();
                    author.setUserID(rs.getInt("author_id"));

                    // Setup Community if it exists
                    Community community = null;
                    int commId = rs.getInt("community_id");
                    if (!rs.wasNull()) {
                        community = new Community();
                        community.setCommunityID(commId);
                        community.setName(rs.getString("community_name"));
                    }

                    Post post = new Post(
                            rs.getInt("post_id"),
                            author,
                            community,
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

    @Override
    public boolean updateCommentVotes(int commentID, boolean isLike) {
        String col = isLike ? "LIKE_COUNT" : "DISLIKE_COUNT";
        String sql = "UPDATE Comments SET " + col + " = " + col + " + 1 WHERE comment_id = ?";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, commentID);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating comment vote: " + e.getMessage());
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
            pstmt.setString(1, "%" + nameQuery + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Community community = new Community();

                    community.setCommunityID(rs.getInt("community_ID"));
                    community.setName(rs.getString("name"));

                    String genresRaw = rs.getString("genres");
                    if (genresRaw != null && !genresRaw.trim().isEmpty()) {
                        List<String> genresList = Arrays.asList(genresRaw.split("\\s*,\\s*"));
                        community.setGenres(new ArrayList<>(genresList));
                    } else {
                        community.setGenres(new ArrayList<>());
                    }

                    community.setMembers(new ArrayList<>());
                    community.setCommunityPosts(new ArrayList<>());

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
                    
                    String gamesRaw = rs.getString("favorite_games");
                    if (gamesRaw != null && !gamesRaw.trim().isEmpty()) {
                        user.setFavoriteGames(new ArrayList<>(Arrays.asList(gamesRaw.split("\\s*,\\s*"))));
                    } else {
                        user.setFavoriteGames(new ArrayList<>());
                    }

                    String genresRaw = rs.getString("favorite_genres");
                    if (genresRaw != null && !genresRaw.trim().isEmpty()) {
                        user.setFavoriteGenres(new ArrayList<>(Arrays.asList(genresRaw.split("\\s*,\\s*"))));
                    } else {
                        user.setFavoriteGenres(new ArrayList<>());
                    }
                    
                    conn.createStatement().executeUpdate("UPDATE Users SET is_online = 1 WHERE user_id = " + user.getUserID());
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> getIncomingFriendRequests(int receiverId) {
        List<User> senders = new ArrayList<>();
        Connection conn = dbConnection.getConnection();

        String sql = "SELECT u.* FROM Users u JOIN FriendRequests fr ON u.USER_ID = fr.SENDER_ID WHERE fr.RECEIVER_ID = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, receiverId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserID(rs.getInt("USER_ID"));
                    u.setUsername(rs.getString("USERNAME"));
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

        String insertFriendSql = "INSERT INTO Friends (USER1_ID, USER2_ID) VALUES (?, ?)";
        String deleteRequestSql = "DELETE FROM FriendRequests WHERE SENDER_ID = ? AND RECEIVER_ID = ?";

        try {
            conn.setAutoCommit(false);

            try (PreparedStatement insertStmt = conn.prepareStatement(insertFriendSql);
                    PreparedStatement deleteStmt = conn.prepareStatement(deleteRequestSql)) {

                insertStmt.setInt(1, receiverId);
                insertStmt.setInt(2, senderId);
                insertStmt.executeUpdate();

                deleteStmt.setInt(1, senderId);
                deleteStmt.setInt(2, receiverId);
                deleteStmt.executeUpdate();

                conn.commit(); 
                return true;
            } catch (SQLException e) {
                conn.rollback(); 
                e.printStackTrace();
                return false;
            } finally {
                conn.setAutoCommit(true); 
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
        String sql = "SELECT * FROM Communities ORDER BY DBMS_RANDOM.VALUE FETCH NEXT 1 ROWS ONLY";
        return fetchSingleCommunity(sql, null);
    }

    @Override
    public Community getRandomCommunityByGenre(String genre) {
        String sql = "SELECT * FROM Communities WHERE LOWER(GENRES) LIKE LOWER(?) ORDER BY DBMS_RANDOM.VALUE FETCH NEXT 1 ROWS ONLY";
        return fetchSingleCommunity(sql, "%" + genre + "%");
    }

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
            return false;
        }
    }

    @Override
    public List<Community> getJoinedCommunities(int userId) {
        List<Community> communities = new ArrayList<>();
        Connection conn = dbConnection.getConnection();

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

        String sql = "SELECT p.*, u.username, " +
                     "(SELECT COUNT(*) FROM Comments c WHERE c.PARENT_POST_ID = p.POST_ID) AS comment_count " +
                     "FROM Posts p " +
                     "JOIN Users u ON p.author_id = u.user_id " +
                     "WHERE p.COMMUNITY_ID = ? ORDER BY p.POST_ID DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, communityId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Post p = new Post();
                    p.setPostID(rs.getInt("POST_ID"));
                    p.setTextContent(rs.getString("TEXT_CONTENT"));
                    p.setLikeCount(rs.getInt("LIKES_COUNT"));
                    p.setDislikeCount(rs.getInt("DISLIKES_COUNT"));
                    p.setCommentCount(rs.getInt("comment_count")); 
                    
                    User author = new User();
                    author.setUserID(rs.getInt("AUTHOR_ID"));
                    author.setUsername(rs.getString("username"));
                    p.setAuthor(author);
                    
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
        String sql = "INSERT INTO POSTS (TEXT_CONTENT, AUTHOR_ID, LIKES_COUNT, DISLIKES_COUNT) VALUES (?, ?, ?, ?)";

        try (Connection conn = dbConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newPost.getTextContent());
            pstmt.setInt(2, newPost.getAuthor().getUserID());
            pstmt.setInt(3, 0); 
            pstmt.setInt(4, 0); 

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
        String checkSql = "SELECT INTERACTION_TYPE FROM POST_INTERACTIONS WHERE POST_ID = ? AND USER_ID = ?";
        String insertSql = "INSERT INTO POST_INTERACTIONS (POST_ID, USER_ID, INTERACTION_TYPE) VALUES (?, ?, 'LIKE')";
        String updateSql = "UPDATE POSTS SET LIKES_COUNT = LIKES_COUNT + 1 WHERE POST_ID = ?";
        
        return handleInteraction(postId, userId, checkSql, insertSql, updateSql);
    }

    @Override
    public boolean dislikePost(int postId, int userId) {
        String checkSql = "SELECT INTERACTION_TYPE FROM POST_INTERACTIONS WHERE POST_ID = ? AND USER_ID = ?";
        String insertSql = "INSERT INTO POST_INTERACTIONS (POST_ID, USER_ID, INTERACTION_TYPE) VALUES (?, ?, 'DISLIKE')";
        String updateSql = "UPDATE POSTS SET DISLIKES_COUNT = DISLIKES_COUNT + 1 WHERE POST_ID = ?";
        
        return handleInteraction(postId, userId, checkSql, insertSql, updateSql);
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

                    String column = type.equalsIgnoreCase("LIKE") ? "LIKES_COUNT" : "DISLIKES_COUNT";
                    String updateSql = "UPDATE POSTS SET " + column + " = " + column + " - 1 WHERE POST_ID = ?";

                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                            PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

                        deleteStmt.setInt(1, postId);
                        deleteStmt.setInt(2, userId);
                        deleteStmt.executeUpdate();

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

    @Override
    public List<Comment> getCommentsByPostId(int postId) {
        List<Comment> comments = new ArrayList<>();
        String sql = "SELECT c.*, u.username FROM Comments c " +
                     "JOIN Users u ON c.author_id = u.user_id " +
                     "WHERE c.PARENT_POST_ID = ?";
                     
        try (PreparedStatement pstmt = dbConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Comment c = new Comment();
                c.setCommentID(rs.getInt("COMMENT_ID"));
                c.setTextContent(rs.getString("TEXT_CONTENT"));
                c.setLikesCount(rs.getInt("LIKE_COUNT"));
                c.setDislikeCount(rs.getInt("DISLIKE_COUNT"));
                
                User author = new User();
                author.setUserID(rs.getInt("AUTHOR_ID"));
                author.setUsername(rs.getString("username"));
                c.setAuthor(author);
                
                comments.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    @Override
    public Post getPostById(int postId) {
        String sql = "SELECT p.*, u.username, " +
                     "(SELECT COUNT(*) FROM Comments c WHERE c.PARENT_POST_ID = p.post_id) AS comment_count " +
                     "FROM Posts p " +
                     "JOIN Users u ON p.author_id = u.user_id " +
                     "WHERE p.post_id = ?";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Post post = new Post();
                    post.setPostID(rs.getInt("post_id"));
                    post.setTextContent(rs.getString("text_content"));
                    post.setLikeCount(rs.getInt("likes_count"));
                    post.setDislikeCount(rs.getInt("dislikes_count"));
                    post.setCommentCount(rs.getInt("comment_count")); 
                    
                    User author = new User();
                    author.setUserID(rs.getInt("author_id"));
                    author.setUsername(rs.getString("username"));
                    post.setAuthor(author);
                    
                    return post;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insertComment(int postId, int authorId, String text) {
        String sql = "INSERT INTO Comments (PARENT_POST_ID, AUTHOR_ID, TEXT_CONTENT, LIKE_COUNT, DISLIKE_COUNT) VALUES (?, ?, ?, 0, 0)";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.setInt(2, authorId);
            pstmt.setString(3, text);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error inserting comment: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean likeComment(int commentId, int userId) {
        String checkSql = "SELECT INTERACTION_TYPE FROM COMMENT_INTERACTIONS WHERE COMMENT_ID = ? AND USER_ID = ?";
        String insertSql = "INSERT INTO COMMENT_INTERACTIONS (COMMENT_ID, USER_ID, INTERACTION_TYPE) VALUES (?, ?, 'LIKE')";
        String updateSql = "UPDATE COMMENTS SET LIKE_COUNT = LIKE_COUNT + 1 WHERE COMMENT_ID = ?";
        
        return handleInteraction(commentId, userId, checkSql, insertSql, updateSql);
    }

    @Override
    public boolean clearCommentInteraction(int commentId, int userId) {
        Connection conn = dbConnection.getConnection();
        String findSql = "SELECT INTERACTION_TYPE FROM COMMENT_INTERACTIONS WHERE COMMENT_ID = ? AND USER_ID = ?";
        String deleteSql = "DELETE FROM COMMENT_INTERACTIONS WHERE COMMENT_ID = ? AND USER_ID = ?";

        try (PreparedStatement findStmt = conn.prepareStatement(findSql)) {
            findStmt.setInt(1, commentId);
            findStmt.setInt(2, userId);

            try (ResultSet rs = findStmt.executeQuery()) {
                if (rs.next()) {
                    String type = rs.getString("INTERACTION_TYPE");
                    String column = type.equalsIgnoreCase("LIKE") ? "LIKE_COUNT" : "DISLIKE_COUNT";
                    String updateSql = "UPDATE COMMENTS SET " + column + " = " + column + " - 1 WHERE COMMENT_ID = ?";

                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql);
                         PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                        
                        deleteStmt.setInt(1, commentId);
                        deleteStmt.setInt(2, userId);
                        deleteStmt.executeUpdate();

                        updateStmt.setInt(1, commentId);
                        return updateStmt.executeUpdate() > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error clearing comment interaction: " + e.getMessage());
        }
        return false;
    }

    private boolean handleInteraction(int targetId, int userId, String checkSql, String insertSql, String updateSql) {
        Connection conn = dbConnection.getConnection();
        
        try {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, targetId);
                checkStmt.setInt(2, userId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("You have already interacted with this item!");
                        return false;
                    }
                }
            }

            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                 PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                
                insertStmt.setInt(1, targetId);
                insertStmt.setInt(2, userId);
                insertStmt.executeUpdate();
                
                updateStmt.setInt(1, targetId);
                return updateStmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("Database error during interaction: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean dislikeComment(int commentId, int userId) {
        String checkSql = "SELECT INTERACTION_TYPE FROM COMMENT_INTERACTIONS WHERE COMMENT_ID = ? AND USER_ID = ?";
        String insertSql = "INSERT INTO COMMENT_INTERACTIONS (COMMENT_ID, USER_ID, INTERACTION_TYPE) VALUES (?, ?, 'DISLIKE')";
        String updateSql = "UPDATE COMMENTS SET DISLIKE_COUNT = DISLIKE_COUNT + 1 WHERE COMMENT_ID = ?";
        
        return handleInteraction(commentId, userId, checkSql, insertSql, updateSql);
    }

    public boolean removeFriend(int userId1, int userId2) {
        String sql = "DELETE FROM Friends WHERE (USER1_ID = ? AND USER2_ID = ?) OR (USER1_ID = ? AND USER2_ID = ?)";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId1);
            pstmt.setInt(2, userId2);
            pstmt.setInt(3, userId2);
            pstmt.setInt(4, userId1);
            
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Database error occurred while removing friend: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<User> getFriendRecommendations(int userID) {
        List<User> recommendations = new ArrayList<>();

        String sql =
            "SELECT u.user_id, u.username, u.email, u.age, u.bio, u.favorite_games, u.favorite_genres " +
            "FROM Users u " +
            "WHERE u.user_id <> ? " +
            "AND u.user_id NOT IN (SELECT blocked_id FROM BlockedUsers WHERE blocker_id = ?)";

        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            pstmt.setInt(2, userID);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();

                    user.setUserID(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setAge(rs.getInt("age"));
                    user.setBio(rs.getString("bio"));

                    String gamesRaw = rs.getString("favorite_games");
                    if (gamesRaw != null && !gamesRaw.trim().isEmpty()) {
                        user.setFavoriteGames(Arrays.asList(gamesRaw.split("\\s*,\\s*")));
                    }

                    String genresRaw = rs.getString("favorite_genres");
                    if (genresRaw != null && !genresRaw.trim().isEmpty()) {
                        user.setFavoriteGenres(Arrays.asList(genresRaw.split("\\s*,\\s*")));
                    }

                    recommendations.add(user);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error getting friend recommendations: " + e.getMessage());
        }

        return recommendations;
    }
    @Override
    public List<User> getMutualFriends(int userId1, int userId2) {
        List<User> mutuals = new ArrayList<>();
        // Finds users who are friends with BOTH userId1 and userId2
        String sql = 
            "SELECT u.user_id, u.username FROM Users u " +
            "WHERE u.user_id IN (SELECT CASE WHEN user1_id = ? THEN user2_id ELSE user1_id END FROM Friends WHERE user1_id = ? OR user2_id = ?) " +
            "AND u.user_id IN (SELECT CASE WHEN user1_id = ? THEN user2_id ELSE user1_id END FROM Friends WHERE user1_id = ? OR user2_id = ?)";
        
        Connection conn = dbConnection.getConnection();
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId1); pstmt.setInt(2, userId1); pstmt.setInt(3, userId1);
            pstmt.setInt(4, userId2); pstmt.setInt(5, userId2); pstmt.setInt(6, userId2);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserID(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    mutuals.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mutuals;
    }
    @Override
    public Group createGroup(String groupName, int creatorId) {
        String sql = "INSERT INTO Groups (GROUP_NAME, CREATOR_ID) VALUES (?, ?)";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql, new String[] { "GROUP_ID" })) {
            pstmt.setString(1, groupName);
            pstmt.setInt(2, creatorId);
            
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        Group g = new Group(rs.getInt(1), groupName, creatorId);
                        // Automatically add the creator to the group
                        insertGroupMember(g.getGroupId(), creatorId);
                        return g;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating group: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Group> getJoinedGroupsList(int userId) {
        List<Group> groups = new ArrayList<>();
        String sql = "SELECT g.* FROM Groups g JOIN GroupMembers gm ON g.GROUP_ID = gm.GROUP_ID WHERE gm.USER_ID = ?";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    groups.add(new Group(rs.getInt("GROUP_ID"), rs.getString("GROUP_NAME"), rs.getInt("CREATOR_ID")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groups;
    }

    @Override
    public List<User> getGroupMembers(int groupId) {
        List<User> members = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.email FROM Users u JOIN GroupMembers gm ON u.user_id = gm.user_id WHERE gm.group_id = ?";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, groupId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User u = new User();
                    u.setUserID(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    members.add(u);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return members;
    }

    @Override
    public boolean removeGroupMember(int groupId, int userId) {
        String sql = "DELETE FROM GroupMembers WHERE GROUP_ID = ? AND USER_ID = ?";
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
    public List<User> getAllFriends(int userID) {
        List<User> friends = new ArrayList<>();
        String sql = "SELECT u.user_id, u.username, u.email, u.age, u.bio " +
                     "FROM Users u " +
                     "JOIN Friends f ON (f.user1_id = ? AND f.user2_id = u.user_id) " +
                     "               OR (f.user2_id = ? AND f.user1_id = u.user_id)";
        Connection conn = dbConnection.getConnection();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            pstmt.setInt(2, userID);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = new User();
                    user.setUserID(rs.getInt("user_id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    user.setAge(rs.getInt("age"));
                    user.setBio(rs.getString("bio"));
                    friends.add(user);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }
}