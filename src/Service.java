package src;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.stream.Collectors;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.*;

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
            dbOp.setUserOffline(currentUser.getUserID());
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
        boolean success = dbOp.insertBlockedUser(currentUser.getUserID(), userToBlock.getUserID());
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
            System.out.println("Post published successfully to " + community.getName() + "!");
        } else {
            System.out.println("Failed to publish post.");
        }
    }

    public List<Comment> getCommentsByPostId(int postId) {
        return dbOp.getCommentsByPostId(postId);
    }

    public boolean updatePostVotes(int postId, boolean isLike) {
        return dbOp.updatePostVotes(postId, isLike);
    }

    public boolean insertComment(int postId, int authorId, String text) {
        if (text == null || text.trim().isEmpty()) {
            System.out.println("Error: Comment cannot be empty.");
            return false;
        }
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
        if (user1 == null || user2 == null) {
            return new ArrayList<>();
        }
        return dbOp.getMutualFriends(user1.getUserID(), user2.getUserID());
    }
    
    public List<User> retrieveFriendRecommendations(User user) {
        if (user == null || user.getUserID() <= 0) {
            return new ArrayList<>();
        }
        return dbOp.getFriendRecommendations(user.getUserID());
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

    public void sendEmailPingToFriend(User sender, User friend, String customMessage) {
        Properties config = new Properties();
        try (FileInputStream in = new FileInputStream("config.properties")) {
            config.load(in);
        } catch (IOException e) {
            System.out.println("Could not load email configuration.");
            e.printStackTrace();
            return;
        }

        final String senderEmail = config.getProperty("email.address");
        final String appPassword = config.getProperty("email.password");

        boolean isDefault = customMessage.isEmpty();
        String subject = isDefault
            ? sender.getUsername() + " sent a ping!"
            : sender.getUsername() + " sent a custom ping!";
        String body = isDefault
            ? "Hey! I'm online and ready to play a game together. Hope to see you soon!"
            : customMessage;

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(friend.getEmail()));
            message.setSubject(subject);
            message.setText(body);
            Transport.send(message);
            System.out.println("Email ping sent to " + friend.getUsername() + "!");
        } catch (MessagingException e) {
            System.out.println("Failed to send email.");
            e.printStackTrace();
        }
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

    public boolean likePost(int postId, int userId) {
        return dbOp.likePost(postId, userId); 
    }

    public boolean dislikePost(int postId, int userId) {
        return dbOp.dislikePost(postId, userId); 
    }

    public boolean clearInteraction(int postId, int userId) {
        return dbOp.clearInteraction(postId, userId); 
    }

    public Post getPostById(int postId) {
        return dbOp.getPostById(postId); 
    }
    
    public boolean likeComment(int commentId, int userId) {
        return dbOp.likeComment(commentId, userId);
    }

    public boolean dislikeComment(int commentId, int userId) {
        return dbOp.dislikeComment(commentId, userId);
    }

    public boolean clearCommentInteraction(int commentId, int userId) {
        return dbOp.clearCommentInteraction(commentId, userId);
    }

    public List<User> getOnlineFriends(int userID) {
        return dbOp.getOnlineFriends(userID);
    }

    public List<User> getOfflineFriends(int userID) {
        return dbOp.getOfflineFriends(userID);
    }
    
    public boolean removeFriend(int currentUserId, int targetUserId) {
        return dbOp.removeFriend(currentUserId, targetUserId);
    }
    public Group createGroup(String groupName, User creator) {
        return dbOp.createGroup(groupName, creator.getUserID());
    }

    public List<Group> getJoinedGroupsList(User user) {
        return dbOp.getJoinedGroupsList(user.getUserID());
    }

    public boolean addFriendToGroup(int groupId, int friendId) {
        return dbOp.insertGroupMember(groupId, friendId);
    }

    public boolean leaveGroup(int groupId, int userId) {
        return dbOp.removeGroupMember(groupId, userId);
    }

    public void sendEmailPingToGroup(User sender, Group group, String customMessage) {
        List<User> members = dbOp.getGroupMembers(group.getGroupId());
        
        if (members.size() <= 1) {
            System.out.println("You are the only one in this group. Invite some friends first!");
            return;
        }

        // 1. Load email config
        Properties config = new Properties();
        try (FileInputStream in = new FileInputStream("config.properties")) {
            config.load(in);
        } catch (IOException e) {
            System.out.println("Could not load email configuration.");
            return;
        }
        final String senderEmail = config.getProperty("email.address");
        final String appPassword = config.getProperty("email.password");

        // 2. Setup Session
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, appPassword);
            }
        });

        // 3. Setup message body
        String subject = "Group Ping: " + group.getGroupName() + "!";
        String body = customMessage.isEmpty() 
            ? sender.getUsername() + " is looking to play with the group! Hop online!" 
            : sender.getUsername() + " says: " + customMessage;

        // 4. Send to everyone (except the sender)
        int sentCount = 0;
        for (User member : members) {
            if (member.getUserID() == sender.getUserID() || member.getEmail() == null) continue;
            
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(senderEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(member.getEmail()));
                message.setSubject(subject);
                message.setText(body);
                Transport.send(message);
                sentCount++;
            } catch (MessagingException e) {
                System.out.println("Failed to send to " + member.getUsername());
            }
        }
        System.out.println("Group ping sent successfully to " + sentCount + " members!");
    }

    public List<User> getAllFriends(int userID) {
        return dbOp.getAllFriends(userID);
    }

    public List<User> getGroupMembers(int groupId) {
        return dbOp.getGroupMembers(groupId);
    }
}