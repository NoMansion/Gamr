package src;

import java.util.List;
import java.util.ArrayList;

public class User {

    // --- Attributes ---
    private int userId;
    private String username;
    private String email;
    private String passwordHash;
    private int age;
    private String bio;
    private List<String> favoriteGames;
    private List<String> favoriteGenres;
    private List<User> friendsList;
    private List<User> blockedUsers;
    private List<User> incomingFriendRequests;
    private List<Group> joinedGroups; // Requires a Group class to exist in the project
    private List<Post> posts;         // Requires a Post class to exist in the project

    // --- Constructor ---
    public User() {
        // Initialize lists to avoid NullPointerExceptions
        this.favoriteGames = new ArrayList<>();
        this.favoriteGenres = new ArrayList<>();
        this.friendsList = new ArrayList<>();
        this.blockedUsers = new ArrayList<>();
        this.incomingFriendRequests = new ArrayList<>();
        this.joinedGroups = new ArrayList<>();
        this.posts = new ArrayList<>();
    }

    // --- Getters and Setters ---

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

 public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public List<String> getFavoriteGames() {
        return favoriteGames;
    }

    public void setFavoriteGames(List<String> favoriteGames) {
        this.favoriteGames = (favoriteGames != null) ? favoriteGames : new ArrayList<>();
    }

    public List<String> getFavoriteGenres() {
        return favoriteGenres;
    }

    public void setFavoriteGenres(List<String> favoriteGenres) {
        this.favoriteGenres = (favoriteGenres != null) ? favoriteGenres : new ArrayList<>();
    }

    public List<User> getFriendsList() {
        return friendsList;
    }

    public void setFriendsList(List<User> friendsList) {
        this.friendsList = (friendsList != null) ? friendsList : new ArrayList<>();
    }

    public List<User> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(List<User> blockedUsers) {
        this.blockedUsers = (blockedUsers != null) ? blockedUsers : new ArrayList<>();
    }

    public List<User> getIncomingFriendRequests() {
        return incomingFriendRequests;
    }

    public void setIncomingFriendRequests(List<User> incomingFriendRequests) {
        this.incomingFriendRequests = (incomingFriendRequests != null) ? incomingFriendRequests : new ArrayList<>();
    }

    public List<Group> getJoinedGroups() {
        return joinedGroups;
    }

    public void setJoinedGroups(List<Group> joinedGroups) {
        this.joinedGroups = (joinedGroups != null) ? joinedGroups : new ArrayList<>();
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = (posts != null) ? posts : new ArrayList<>();
    }
}
