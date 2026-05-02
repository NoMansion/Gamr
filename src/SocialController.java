// SocialController class for Gamr
// Written by Brady Ehman
package src;

import java.util.List;

public class SocialController {
	private Service service;

	// Constructor to inject the shared Service instance
	public SocialController(Service service) {
		this.service = service;
	}

	public void sendFriendRequest(User sender, User receiver) {
		service.sendFriendRequest(sender, receiver);
	}

	public void skipAccount(User viewer, User viewAccount) {
		service.skipAccount(viewer, viewAccount);
	}

	public void acceptFriendRequest(User user, User sender) {
		service.acceptFriendRequest(user, sender);
	}

	public void declineFriendRequest(User user, User sender) {
		service.declineFriendRequest(user, sender);
	}

	public void unaddFriend(User user, User friendToRemove) {
		service.unaddFriend(user, friendToRemove);
	}

	public List<User> retrieveMutualFriends(User user1, User user2) {
		return service.retrieveMutualFriends(user1, user2);
	}

	public List<User> retrieveFriendRecommendations(User user) {
		return service.retrieveFriendRecommendations(user);
	}

	public List<User> filterRecommendationsByAge(List<User> recommendations, int targetAge) {
		return service.filterRecommendationsByAge(recommendations, targetAge);
	}

	public List<User> filterRecommendationsByGenre(List<User> recommendations, String genre) {
		return service.filterRecommendationsByGenre(recommendations, genre);
	}

	public List<User> filterRecommendationsByGame(List<User> recommendations, String game) {
		return service.filterRecommendationsByGame(recommendations, game);
	}

	public void sendEmailPingToFriend(User sender, User friend) {
		service.sendEmailPingToFriend(sender, friend);
	}

	public void sendEmailPingToGroup(User sender, Group group) {
		service.sendEmailPingToGroup(sender, group);
	}

	public void createAccount(String username, String email, String password, int age, List<String> games) {
		service.createAccount(username, email, password, age, games);
	}

	public void login(String username, String password) {
		service.login(username, password);
	}

	public void logout() {
		service.logout();
	}

	public void deleteAccount() {
		service.deleteAccount();
	}

	public void updateProfile(String bio, List<String> favoriteGames) {
		service.updateProfile(bio, favoriteGames);
	}

	public void blockUser(User userToBlock) {
		service.blockUser(userToBlock);
	}

	public void addMember(User user) {
		service.addMember(user);
	}

	public Post likePost(Post post) {
		return service.likePost(post);
	}

	public Post dislikePost(Post post) {
		return service.dislikePost(post);
	}

	public void createComment(Post parentPost, User author, String textContent) {
		service.createComment(parentPost, author, textContent);
	}

	public void likeComment(Comment comment) {
		service.likeComment(comment);
	}

	public void dislikeComment(Comment comment) {
		service.dislikeComment(comment);
	}

	// Fetch the list of communities this user is a member of
    public List<Community> getJoinedCommunities(User user) {
        return service.getJoinedCommunities(user);
    }

    // Fetch the recent posts for a specific community
    public List<Post> getCommunityPosts(Community community) {
        return service.getCommunityPosts(community);
    }

    // Create a new post (This signature matches your UML diagram perfectly!)
    public void createPost(Community community, User author, String textContent) {
        service.createPost(community, author, textContent);
    }
}