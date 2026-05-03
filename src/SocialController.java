package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;



public class SocialController {

	private String gamerGameFilter = "";
	private String gamerGenreFilter = "";
	private String gamerAgeFilter = "";

	private final Service service;
	private final Scanner scanner;
	private User currentUser;

	public SocialController(Service service, Scanner scanner) {
		this.service = service;
		this.scanner = scanner;
	}

	public void run() {
		boolean running = true;

		while (running) {
			System.out.println("\n--- MAIN MENU ---");
			System.out.println("1. Log In");
			System.out.println("2. Create Account");
			System.out.println("3. Exit");
			System.out.print("Choose an option (1-3): ");

			String choice = scanner.nextLine().trim();

			switch (choice) {
				case "1":
					handleLogin();
					break;
				case "2":
					handleCreateAccount();
					break;
				case "3":
					running = false;
					System.out.println("Shutting down... Goodbye!");
					break;
				default:
					System.out.println("Invalid option. Please enter 1, 2, or 3.");
			}
		}
	}

	private void handleCreateAccount() {
    System.out.println("\n--- CREATE ACCOUNT ---");
    User newUser = new User();

    System.out.print("Enter username: ");
    newUser.setUsername(scanner.nextLine().trim());

    System.out.print("Enter email: ");
    newUser.setEmail(scanner.nextLine().trim());

    System.out.print("Enter password: ");
    newUser.setPasswordHash(scanner.nextLine().trim());

    System.out.print("Enter age: ");
    try {
        newUser.setAge(Integer.parseInt(scanner.nextLine().trim()));
    } catch (NumberFormatException e) {
        System.out.println("Invalid age entered. Defaulting to 0.");
        newUser.setAge(0);
    }

    System.out.print("Enter a short bio: ");
    newUser.setBio(scanner.nextLine().trim());

    System.out.print("Enter favorite games (comma-separated): ");
    String gamesInput = scanner.nextLine().trim();

    List<String> gamesList = Arrays.stream(gamesInput.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());

    newUser.setFavoriteGames(gamesList);

    System.out.print("Enter favorite genres (comma-separated): ");
    String genresInput = scanner.nextLine().trim();

    List<String> genresList = Arrays.stream(genresInput.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());

    newUser.setFavoriteGenres(genresList);

    boolean success = service.createAccount(newUser);

    if (success) {
        System.out.println("\nAccount created successfully! Welcome, " + newUser.getUsername() + ".");
        System.out.println("Favorite Games: " + newUser.getFavoriteGames());
        System.out.println("Favorite Genres: " + newUser.getFavoriteGenres());
        System.out.println("Please log in with your new credentials.");
    } else {
        System.out.println("\nFailed to create account. That username or email might already be taken.");
    }
}

	private void handleLogin() {
		System.out.println("\n--- LOG IN ---");
		System.out.print("Email: ");
		String email = scanner.nextLine().trim();

		System.out.print("Password: ");
		String password = scanner.nextLine().trim();

		User loggedInUser = service.login(email, password);

		if (loggedInUser != null) {
			currentUser = loggedInUser;
			service.setCurrentUser(loggedInUser);
			System.out.println("\nLogin successful! Welcome back, " + loggedInUser.getUsername() + "!");
			showUserDashboard();
		} else {
			System.out.println("\nLogin failed. Incorrect email or password.");
		}
	}

	private void showUserDashboard() {
		boolean loggedIn = true;

		while (loggedIn) {
			System.out.println("\n=== MAIN DASHBOARD ===");
			System.out.println("1. Community Tab");
			System.out.println("2. Friends Tab");
			System.out.println("3. Find Gamers Tab");
			System.out.println("4. Profile Settings");
			System.out.println("5. Log Out");
			System.out.print("Choose a tab (1-5): ");

			String choice = scanner.nextLine().trim();

			switch (choice) {
				case "1":
					showCommunityTab();
					break;
				case "2":
					showFriendsTab();
					break;
				case "3":
					showFindGamersTab();
					break;
				case "4":
					  showProfileSettingsTab();
   					 break;
				case "5":
					loggedIn = false;
					System.out.println("Logging out... Returning to main menu.");
					currentUser = null;
					service.logout();
					break;
				default:
					System.out.println("Invalid option. Please choose 1-5.");
			}
		}
	}

	private void showCommunityTab() {
		boolean inTab = true;
		while (inTab) {
			System.out.println("\n--- COMMUNITY TAB ---");
			System.out.println("1. Discover & Search Communities");
			System.out.println("2. View Joined Communities");
			System.out.println("3. Back to Dashboard");
			System.out.print("Choose an option: ");

			String choice = scanner.nextLine().trim();

			switch (choice) {
				case "1":
					showCommunitySearchMenu();
					break;
				case "2":
					List<Community> joinedCommunities = service.getJoinedCommunities(currentUser);
					if (joinedCommunities == null || joinedCommunities.isEmpty()) {
						System.out.println("You haven't joined any communities yet! Try discovering some first.");
					} else {
						System.out.println("\n--- My Communities ---");
						for (int i = 0; i < joinedCommunities.size(); i++) {
							System.out.println((i + 1) + ". " + joinedCommunities.get(i).getName());
						}
						System.out.print(
								"Select a community to view (1-" + joinedCommunities.size() + ") or 0 to cancel: ");
						try {
							int selection = Integer.parseInt(scanner.nextLine().trim());
							if (selection > 0 && selection <= joinedCommunities.size()) {
								viewJoinedCommunity(joinedCommunities.get(selection - 1));
							}
						} catch (NumberFormatException e) {
							System.out.println("Invalid selection. Returning to menu.");
						}
					}
					break;
				case "3":
					inTab = false;
					break;
				default:
					System.out.println("Invalid option.");
			}
		}
	}

	private void showCommunitySearchMenu() {
		boolean searching = true;

		while (searching) {
			System.out.println("\n--- DISCOVER COMMUNITIES ---");
			System.out.println("1. Discover Random Community (Surprise me!)");
			System.out.println("2. Discover Random Community by Genre");
			System.out.println("3. Search by Exact Name");
			System.out.println("4. Back to Community Tab");
			System.out.print("Choose an option: ");

			String choice = scanner.nextLine().trim();

			switch (choice) {
				case "1":
					boolean rollingRandom = true;
					while (rollingRandom) {
						System.out.println("\n[Fetching community...]");
						Community randomComm = service.getRandomCommunity();
						if (randomComm != null) {
							boolean stop = interactWithCommunity(randomComm);
							if (stop) {
								rollingRandom = false;
							}
						} else {
							System.out.println("No communities found in the database!");
							rollingRandom = false;
						}
					}
					break;
				case "2":
					System.out.print("\nEnter a genre (e.g., RPG, Shooter, MMO): ");
					String genre = scanner.nextLine().trim();
					boolean rollingGenre = true;
					while (rollingGenre) {
						System.out.println("\n[Fetching " + genre + " community...]");
						Community genreComm = service.getRandomCommunityByGenre(genre);
						if (genreComm != null) {
							boolean stop = interactWithCommunity(genreComm);
							if (stop) {
								rollingGenre = false;
							}
						} else {
							System.out.println("No communities found for the genre: " + genre);
							rollingGenre = false;
						}
					}
					break;
				case "3":
					System.out.print("\nEnter community name to search: ");
					String searchQuery = scanner.nextLine().trim();
					List<Community> searchResults = service.getCommunitiesByName(searchQuery);
					if (searchResults == null || searchResults.isEmpty()) {
						System.out.println("No communities found matching: '" + searchQuery + "'");
					} else {
						System.out.println("\n--- Search Results ---");
						for (int i = 0; i < searchResults.size(); i++) {
							System.out.println((i + 1) + ". " + searchResults.get(i).getName());
						}
						System.out
								.print("Select a community to view (1-" + searchResults.size() + ") or 0 to cancel: ");
						try {
							int selection = Integer.parseInt(scanner.nextLine().trim());
							if (selection > 0 && selection <= searchResults.size()) {
								interactWithCommunity(searchResults.get(selection - 1));
							}
						} catch (NumberFormatException e) {
							System.out.println("Invalid selection. Returning to menu.");
						}
					}
					break;
				case "4":
					searching = false;
					break;
				default:
					System.out.println("Invalid option.");
			}
		}
	}

	private boolean interactWithCommunity(Community community) {
		while (true) {
			System.out.println("\n=================================");
			System.out.println(" COMMUNITY: " + community.getName());
			System.out.println(" GENRES: " + String.join(", ", community.getGenres()));
			System.out.println("=================================");
			System.out.println("1. Join Community");
			System.out.println("2. Skip (Show Next Community)");
			System.out.println("3. Exit to Search Menu");
			System.out.print("Action: ");

			String choice = scanner.nextLine().trim();
			if (choice.equals("1")) {
				boolean success = service.joinCommunity(currentUser.getUserID(), community.getCommunityID());
				if (success) {
					System.out.println("\n>>> Success! You are now a member of " + community.getName() + "!");
				} else {
					System.out.println("\n>>> Failed to join. You might already be a member of this community.");
				}
				return true;
			}
			if (choice.equals("2")) {
				return false;
			}
			if (choice.equals("3")) {
				return true;
			}
			System.out.println("Invalid choice.");
		}
	}

	private void viewJoinedCommunity(Community community) {
		boolean viewing = true;
		while (viewing) {
			System.out.println("\n--- " + community.getName().toUpperCase() + " FEED ---");
			List<Post> posts = service.getCommunityPosts(community);
			if (posts == null || posts.isEmpty()) {
				System.out.println("No posts yet.");
			} else {
				for (int i = 0; i < posts.size(); i++) {
					Post post = posts.get(i);
					// Safely grab the username
					String authorName = (post.getAuthor() != null && post.getAuthor().getUsername() != null) 
					                    ? post.getAuthor().getUsername() : "Unknown User";
					                    
					System.out.println((i + 1) + ". [" + authorName + "] " + post.getTextContent());
					System.out.println("   Likes: " + post.getLikeCount() + " | Dislikes: " + post.getDislikeCount() + " | Comments: " + post.getCommentCount());
				}
			}

			System.out.println("\n1. Make a Post | 2. View Post & Comments | 3. Back");
			System.out.print("Action: ");
			String choice = scanner.nextLine().trim();

			if (choice.equals("1")) {
				System.out.print("Post content: ");
				String content = scanner.nextLine().trim();
				service.createPost(community, currentUser, content);
			} else if (choice.equals("2") && posts != null && !posts.isEmpty()) {
				System.out.print("Select Post number: ");
				try {
					int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
					if (idx >= 0 && idx < posts.size()) {
						showPostInteractionMenu(posts.get(idx));
					}
				} catch (NumberFormatException e) {
					System.out.println("Invalid input.");
				}
			} else if (choice.equals("3")) {
				viewing = false;
			}
		}
	}

	private void showPostInteractionMenu(Post post) {
		boolean interacting = true;
		while (interacting) {
		    // Safely grab the username
		    String authorName = (post.getAuthor() != null && post.getAuthor().getUsername() != null) 
		                        ? post.getAuthor().getUsername() : "Unknown User";
		                        
			System.out.println("\n=================================");
			System.out.println("POST by " + authorName + ": " + post.getTextContent());
			System.out.println("Likes: " + post.getLikeCount() + " | Dislikes: " + post.getDislikeCount() + " | Comments: " + post.getCommentCount());
			System.out.println("=================================");

			// 1. Fetch and display existing comments through the Service
			List<Comment> comments = service.getCommentsByPostId(post.getPostID());
			System.out.println("\n--- COMMENTS ---");
			if (comments == null || comments.isEmpty()) {
				System.out.println("(No comments yet)");
			} else {
				for (int i = 0; i < comments.size(); i++) {
					Comment comment = comments.get(i);
					
					// Safely grab the username
					String commentAuthor = (comment.getAuthor() != null && comment.getAuthor().getUsername() != null) 
					                       ? comment.getAuthor().getUsername() : "Unknown User";
					                       
					System.out.println((i + 1) + ". [" + commentAuthor + "] " + comment.getTextContent());
					System.out.println("   Likes: " + comment.getLikesCount() + " | Dislikes: " + comment.getDislikeCount());
				}
			}

			System.out.println("\n1. Like Post");
			System.out.println("2. Dislike Post");
			System.out.println("3. Remove My Interaction");
			System.out.println("4. Add Comment");
			System.out.println("5. Like/Dislike a Comment");
			System.out.println("6. Back");
			System.out.print("Action: ");

			String choice = scanner.nextLine().trim();

			switch (choice) {
				case "1":
					// Delegate to service to use the interaction ledger
					if (service.likePost(post.getPostID(), currentUser.getUserID())) {
						System.out.println("Liked!");
						// Refresh post data to ensure counts stay accurate
						Post updatedPost = service.getPostById(post.getPostID());
						post.setLikeCount(updatedPost.getLikeCount());
						post.setDislikeCount(updatedPost.getDislikeCount());
						post.setCommentCount(updatedPost.getCommentCount());
					}
					break;
				case "2":
					// Delegate to service to use the interaction ledger
					if (service.dislikePost(post.getPostID(), currentUser.getUserID())) {
						System.out.println("Disliked!");
						// Refresh post data to ensure counts stay accurate
						Post updatedPost = service.getPostById(post.getPostID());
						post.setLikeCount(updatedPost.getLikeCount());
						post.setDislikeCount(updatedPost.getDislikeCount());
						post.setCommentCount(updatedPost.getCommentCount());
					}
					break;
				case "3":
					// Logic to clear existing likes or dislikes
					if (service.clearInteraction(post.getPostID(), currentUser.getUserID())) {
						System.out.println("Interaction removed.");
						// Refresh post data to ensure counts stay accurate
						Post updatedPost = service.getPostById(post.getPostID());
						post.setLikeCount(updatedPost.getLikeCount());
						post.setDislikeCount(updatedPost.getDislikeCount());
						post.setCommentCount(updatedPost.getCommentCount());
					} else {
						System.out.println("No interaction to remove.");
					}
					break;
				case "4":
					System.out.print("Your comment: ");
					String text = scanner.nextLine().trim();
					if (service.insertComment(post.getPostID(), currentUser.getUserID(), text)) {
						System.out.println("Comment added!");
						// Optional: Update local post comment count to reflect new comment
						post.setCommentCount(post.getCommentCount() + 1);
					}
					break;
				case "5":
					if (comments == null || comments.isEmpty())
						break;
					System.out.print("Select Comment number: ");
					try {
						int cIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
						if (cIdx >= 0 && cIdx < comments.size()) {
							Comment selectedComm = comments.get(cIdx);
							System.out.println("1. Like | 2. Dislike | 3. Remove Interaction");
							String action = scanner.nextLine().trim();

							if (action.equals("1")) {
								service.likeComment(selectedComm.getCommentID(), currentUser.getUserID());
							} else if (action.equals("2")) {
								service.dislikeComment(selectedComm.getCommentID(), currentUser.getUserID());
							} else if (action.equals("3")) {
								service.clearCommentInteraction(selectedComm.getCommentID(), currentUser.getUserID());
							}
						}
					} catch (Exception e) {
						System.out.println("Invalid selection.");
					}
					break;
				case "6":
					interacting = false;
					break;
				default:
					System.out.println("Invalid option.");
			}
		}
	}

	private void showFriendsTab() {
		boolean inTab = true;
		while (inTab) {
			System.out.println("\n--- FRIENDS TAB ---");
			System.out.println("1. View Online Friends");
			System.out.println("2. Send Notification to Friend");
			System.out.println("3. Add Friend");
			System.out.println("4. View Incoming Friend Requests");
			System.out.println("5. Remove Friend");
			System.out.println("6. View Groups");
			System.out.println("7. Create Group");
			System.out.println("8. Back to Dashboard");
			System.out.print("Choose an option: ");

			String choice = scanner.nextLine().trim();
			switch (choice) {
				case "1":
					System.out.println("\n--- ONLINE FRIENDS ---");
    				List<User> onlineFriends = service.getOnlineFriends(currentUser.getUserID());
    				if (onlineFriends == null || onlineFriends.isEmpty()) {
        				System.out.println("None of your friends are currently online.");
   					} else {
        				System.out.println(onlineFriends.size() + " friend(s) online:");
        				for (User friend : onlineFriends) {
            				System.out.println("  - " + friend.getUsername());
        				}
    				}
    				break;
				case "2":
					System.out.println("\n--- SEND EMAIL PING TO FRIEND ---");
    				List<User> onlineFriendsList = service.getOnlineFriends(currentUser.getUserID());
    				List<User> offlineFriendsList = service.getOfflineFriends(currentUser.getUserID());

    				if ((onlineFriendsList == null || onlineFriendsList.isEmpty()) &&
        				(offlineFriendsList == null || offlineFriendsList.isEmpty())) {
        				System.out.println("You have no friends to ping.");
        				break;
    				}

    				// Merge both lists and display with indicators
    				List<User> allFriends = new ArrayList<>();
    				allFriends.addAll(onlineFriendsList);
    				allFriends.addAll(offlineFriendsList);

    				char pingLetter = 'A';
    				for (User friend : allFriends) {
        				boolean isOnline = onlineFriendsList.contains(friend);
        				String indicator = isOnline ? "🟢 Online" : "⚫ Offline";
        				System.out.println(pingLetter + ". " + friend.getUsername() + " " + indicator);
        				pingLetter++;
    				}

    				System.out.println("\nEnter the letter or username of the friend you want to ping:");
    				String friendChoice = scanner.nextLine().trim();

    				// Find the selected friend
    				User selectedFriend = null;
    				if (friendChoice.length() == 1 && Character.isLetter(friendChoice.charAt(0))) {
        				int index = Character.toUpperCase(friendChoice.charAt(0)) - 'A';
        				if (index >= 0 && index < allFriends.size()) {
            				selectedFriend = allFriends.get(index);
        				}
    				} else {
        				for (User friend : allFriends) {
            				if (friend.getUsername().equalsIgnoreCase(friendChoice)) {
                				selectedFriend = friend;
                				break;
            				}
        				}
    				}

    				if (selectedFriend == null) {
        				System.out.println("❌ Friend not found.");
        				break;
    				}

    				service.sendEmailPingToFriend(currentUser, selectedFriend);
					break;
				case "3":
					System.out.print("\nEnter username to add: ");
					String addTarget = scanner.nextLine().trim();
					User targetUser = service.getUserByUsername(addTarget);
					if (targetUser == null) {
						System.out.println("User '" + addTarget + "' not found.");
						break;
					}
					if (targetUser.getUserID() == currentUser.getUserID()) {
						System.out.println("You cannot send a friend request to yourself.");
						break;
					}
					boolean requestSent = service.insertFriendRequest(currentUser.getUserID(), targetUser.getUserID());
					if (requestSent) {
						System.out.println("Friend request sent to " + addTarget + "!");
					} else {
						System.out.println("Failed to send friend request to " + addTarget + ".");
					}
					break;
				case "4":
					System.out.println("\n--- INCOMING FRIEND REQUESTS ---");
					List<User> pendingRequests = service.getIncomingFriendRequests(currentUser.getUserID());
					if (pendingRequests == null || pendingRequests.isEmpty()) {
						System.out.println("You have no incoming friend requests right now.");
					} else {
						for (User sender : pendingRequests) {
							System.out.println("\nRequest from: " + sender.getUsername());
							System.out.print("Do you want to accept? (yes/no/skip): ");
							String answer = scanner.nextLine().trim().toLowerCase();
							if (answer.equals("yes")) {
								if (service.acceptFriendRequest(currentUser.getUserID(), sender.getUserID())) {
									System.out.println(
											"Accepted! You are now friends with " + sender.getUsername() + ".");
								} else {
									System.out.println("Error accepting request.");
								}
							} else if (answer.equals("no")) {
								if (service.declineFriendRequest(currentUser.getUserID(), sender.getUserID())) {
									System.out.println("Request declined.");
								} else {
									System.out.println("Error declining request.");
								}
							} else {
								System.out.println("Skipped.");
							}
						}
					}
					break;
				case "5":
                    System.out.print("\nEnter username to remove: ");
                    String removeTarget = scanner.nextLine().trim();
                    
                    // 1. Find the target user by username
                    User userToRemove = service.getUserByUsername(removeTarget);
                    if (userToRemove == null) {
                        System.out.println("User '" + removeTarget + "' not found.");
                        break;
                    }

                    // 2. Prevent removing yourself
                    if (userToRemove.getUserID() == currentUser.getUserID()) {
                        System.out.println("You cannot remove yourself from your friends list.");
                        break;
                    }

                    // 3. Call the service to remove the friend
                    System.out.println("Removing " + removeTarget + " from friends list...");
                    boolean isRemoved = service.removeFriend(currentUser.getUserID(), userToRemove.getUserID());
                    
                    if (isRemoved) {
                        System.out.println("✅ Successfully removed " + removeTarget + " from your friends list.");
                    } else {
                        System.out.println("❌ Failed to remove " + removeTarget + ". Are you sure you are friends?");
                    }
                    break;
				case "6":
					System.out.println("\n[Feature: View Groups]");
					break;
				case "7":
					System.out.println("\n[Feature: Create Group]");
					break;
				case "8":
					inTab = false;
					break;
				default:
					System.out.println("Invalid option. Please choose 1-8.");
			}
		}
	}

	private void showFindGamersTab() {
    boolean inTab = true;

    while (inTab) {
        System.out.println("\n--- FIND GAMERS TAB ---");
        System.out.println("1. Start Queue");
        System.out.println("2. Apply Filters (Games, Genres, Age)");
        System.out.println("3. Clear Filters");
        System.out.println("4. Back to Dashboard");
        System.out.println("Current Filters -> Game: [" + gamerGameFilter + "], Genre: ["
                + gamerGenreFilter + "], Max Age: [" + gamerAgeFilter + "]");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                startFindGamersQueue();
                break;
            case "2":
                applyFindGamersFilters();
                break;
            case "3":
                gamerGameFilter = "";
                gamerGenreFilter = "";
                gamerAgeFilter = "";
                System.out.println("Filters cleared.");
                break;
            case "4":
                inTab = false;
                break;
            default:
                System.out.println("Invalid option.");
        }
    }
}

	private void showProfileSettingsTab() {
    boolean inSettings = true;

    while (inSettings) {
        System.out.println("\n--- PROFILE SETTINGS ---");
        System.out.println("1. View Profile");
        System.out.println("2. Edit Profile Information");
        System.out.println("3. Back to Dashboard");
        System.out.print("Choose an option: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1":
                System.out.println("\n--- YOUR PROFILE ---");
                System.out.println("Username: " + currentUser.getUsername());
                System.out.println("Email: " + currentUser.getEmail());
                System.out.println("Age: " + currentUser.getAge());
                System.out.println("Bio: " + currentUser.getBio());
                System.out.println("Favorite Games: " + currentUser.getFavoriteGames());
                System.out.println("Favorite Genres: " + currentUser.getFavoriteGenres());
                break;

           case "2":
                System.out.println("\n--- EDIT PROFILE ---");

                System.out.print("New email (leave blank to keep current): ");
                String newEmail = scanner.nextLine().trim();
                if (!newEmail.isEmpty()) {
                    currentUser.setEmail(newEmail);
                }

                System.out.print("New age (leave blank to keep current): ");
                String newAge = scanner.nextLine().trim();
                if (!newAge.isEmpty()) {
                    try {
                        currentUser.setAge(Integer.parseInt(newAge));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid age input. Keeping old age.");
                    }
                }

                System.out.print("New bio (leave blank to keep current): ");
                String newBio = scanner.nextLine().trim();
                if (!newBio.isEmpty()) {
                    currentUser.setBio(newBio);
                }

                // --- NEW GAMES/GENRES LOGIC ---
                System.out.println("Current Games: " + currentUser.getFavoriteGames());
                System.out.print("Enter new favorite games (comma-separated, leave blank to keep current): ");
                String newGamesInput = scanner.nextLine().trim();
                if (!newGamesInput.isEmpty()) {
                    List<String> gamesList = Arrays.stream(newGamesInput.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());
                    currentUser.setFavoriteGames(gamesList);
                }

                System.out.println("Current Genres: " + currentUser.getFavoriteGenres());
                System.out.print("Enter new favorite genres (comma-separated, leave blank to keep current): ");
                String newGenresInput = scanner.nextLine().trim();
                if (!newGenresInput.isEmpty()) {
                    List<String> genresList = Arrays.stream(newGenresInput.split(","))
                            .map(String::trim)
                            .filter(s -> !s.isEmpty())
                            .collect(Collectors.toList());
                    currentUser.setFavoriteGenres(genresList);
                }

                boolean updated = service.updateUserProfile(currentUser);

                if (updated) {
                    System.out.println("Profile updated successfully!");
                } else {
                    System.out.println("Failed to update profile.");
                }

                break;

            case "3":
                inSettings = false;
                break;

            default:
                System.out.println("Invalid option.");
        }
    }
}
	

	private void applyFindGamersFilters() {
    System.out.println("\n--- APPLY FILTERS ---");

    System.out.print("Filter by Game (leave blank to skip): ");
    gamerGameFilter = scanner.nextLine().trim();

    System.out.print("Filter by Genre (leave blank to skip): ");
    gamerGenreFilter = scanner.nextLine().trim();

    System.out.print("Filter by Max Age (leave blank to skip): ");
    gamerAgeFilter = scanner.nextLine().trim();

    System.out.println("Filters applied! Game: [" + gamerGameFilter + "], Genre: ["
            + gamerGenreFilter + "], Max Age: [" + gamerAgeFilter + "]");
}

	private void startFindGamersQueue() {
    List<User> recommendations = service.retrieveFriendRecommendations(currentUser);

    if (recommendations == null || recommendations.isEmpty()) {
        System.out.println("\nNo gamer recommendations found right now.");
        return;
    }

    if (!gamerGameFilter.isEmpty()) {
        recommendations = service.filterRecommendationsByGame(recommendations, gamerGameFilter);
    }

    if (!gamerGenreFilter.isEmpty()) {
        recommendations = service.filterRecommendationsByGenre(recommendations, gamerGenreFilter);
    }

    if (!gamerAgeFilter.isEmpty()) {
        try {
            int maxAge = Integer.parseInt(gamerAgeFilter);
            recommendations = recommendations.stream()
                    .filter(user -> user.getAge() <= maxAge)
                    .collect(Collectors.toList());
        } catch (NumberFormatException e) {
            System.out.println("Invalid age filter. Ignoring age filter.");
        }
    }

    if (recommendations.isEmpty()) {
        System.out.println("\nNo gamers matched your filters.");
        return;
    }

    int index = 0;

    while (index < recommendations.size()) {
        User gamer = recommendations.get(index);

        System.out.println("\n==============================");
        System.out.println("GAMER RECOMMENDATION");
        System.out.println("Username: " + gamer.getUsername());
        System.out.println("Age: " + gamer.getAge());
        System.out.println("Bio: " + gamer.getBio());
        System.out.println("Favorite Games: " + gamer.getFavoriteGames());
        System.out.println("Favorite Genres: " + gamer.getFavoriteGenres());
        System.out.println("==============================");

        System.out.println("1. Like / Send Friend Request");
        System.out.println("2. Dislike / Skip");
        System.out.println("3. Block User");
        System.out.println("4. Exit Queue");
        System.out.print("Action: ");

        String action = scanner.nextLine().trim();

        switch (action) {
            case "1":
                boolean sent = service.insertFriendRequest(currentUser.getUserID(), gamer.getUserID());
                if (sent) {
                    System.out.println("Friend request sent to " + gamer.getUsername() + "!");
                } else {
                    System.out.println("Could not send friend request.");
                }
                index++;
                break;

            case "2":
                service.skipAccount(currentUser, gamer);
                index++;
                break;

            case "3":
                service.blockUser(gamer);
                index++;
                break;

            case "4":
                System.out.println("Exiting Find Gamers queue.");
                return;

            default:
                System.out.println("Invalid option.");
        }
    }

    System.out.println("\nNo more gamer recommendations.");
}
  
   
}

	
