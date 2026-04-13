// SocialController class for Gamr
// Written by Brady Ehman
package src;

import java.util.List;

public class SocialController {
	// No instance variables because this is a controller class
	
	public static void sendFriendRequest(User sender, User receiver) {
		
	}
	
	public static void skipAccount(User viewer, User viewAccount) {
		
	}
	
	public static void acceptFriendRequest(User user, User sender) {
		
	}
	
	public static void declineFriendRequest(User user, User sender) {
		
	}
	
	public static void unaddFriend(User user, User friendToRemove) {
		
	}
	
	public static List<User> retrieveMutualFriends(User user1, User user2){
		
	}
	
	public static List<User> retrieveFriendRecommendations(User user){
		
	}
	
	public static List<User> filterRecommendationsByAge(List<User> recommendations, int targetAge){
		
	}
	
	public static List<User> filterRecommendationsByGenre(List<User> recommendations, String genre){
		
	}
	
	public static List<User> filterRecommendationsByGame(List<User> recommendations, String game){
		
	}
	
	public static void sendEmailPingToFriend(User sender, User friend) {
		
	}
	
	public static void sendEmailPingToGroup(User sender, Group group) {
		
	}
	
	public static void createAccount(String username, String email, String password, int age, List<String> games) {
		
	}
	
	public static void login(String username, String password) {
		
	}
	
	public static void logout() {
		
	}
	
	public static void deleteAccount() {
		
	}
	
	public static void updateProfile(String bio, List<String> favoriteGames) {
		
	}
	
	public static void blockUser(User userToBlock) {
		
	}
	
	public static void addMember(User user) {
		
	}
	
	public static void createPost(User author, String textContent) {
		
	}
	
	public static Post likePost(Post post) {
		
	}
	
	public static Post dislikePost(Post post) {
		
	}
	
	public static void createComment(User author, String textContent, Post parentPost) {
		
	}
	
	public static void likeComment(Comment comment) {
		
	}
	
	public static void dislikeComment(Comment comment) {
		
	}
	
}