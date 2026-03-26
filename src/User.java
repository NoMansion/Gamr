//Author: Ryan Schuitema
//Class for User

package src;

import java.util.ArrayList;
import java.util.List;

public class User{
    private int userID;
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
    private List<Group> joinedGroups;
    private List<Post> posts;

    public User(int userID, String username, String email, String passwordHash, int age, List<String> games){
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.age = age;
        this.bio = "";
        this.favoriteGames = new ArrayList<>();
        this.favoriteGenres = new ArrayList<>();
        this.friendsList = new ArrayList<>();
        this.blockedUsers = new ArrayList<>();
        this.blockedUsers = new ArrayList<>();
        this.incomingFriendRequests = new ArrayList<>();
        this.joinedGroups = new ArrayList<>();
        this.posts = new ArrayList<>();
    }
    // Getters
    public int getUserID(){
        return userID;
    }public String getUsername(){
        return username;
    }public String getEmail(){
        return email;
    }public String getPasswordHash(){
        return passswordHash;
    }public int getAge(){
        return age;
    }public String getBio(){
        return bio;
    }public List<String> getFavoriteGames(){
        return favoriteGames;
    }public List<String> getFavoriteGenres(){
        return favoriteGenres;
    }public List<User> getFriendsList(){
        return friendsList;
    }public List<User> getBlockedUsers(){
        return blockedUsers;
    }public List<User> getIncomingFriendRequests(){
        return incomingFriendRequests;
    }public List<Group> getJoinedGroups(){
        return joinedGroups;
    }public List<Post> getPosts(){
        return posts;
    }
    //Setters
    public void setBio(String bio){
        this.bio =  bio;
    }
    public void setFavoriteGames(List<String> favoriteGames){
        this.favoriteGames = new ArrayList<>(favoriteGames);
    }
    public void setFavoriteGenres(List<String> favoriteGenres){
        this.favoriteGenres = new ArrayList<>(favoriteGenres);
    }
}
