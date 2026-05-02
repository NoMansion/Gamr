package src;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {

    // Shared scanner and database operation objects
    private static Scanner scanner;
    private static DBOperation dbOps;

    public static void main(String[] args) {
        scanner = new Scanner(System.in);

        // 1. Initialize the Database Connection
        System.out.println("Initializing system...");
        DatabaseConnection dbConnection = ConnectionFactory.createConnection("SQL");

        if (dbConnection == null || dbConnection.getConnection() == null) {
            System.out.println("Fatal Error: Could not connect to the database. Exiting application.");
            return;
        }

        // Inject the connection into our SQL operations
        dbOps = new SQLOperation(dbConnection);

        boolean running = true;
        System.out.println("\n=================================");
        System.out.println("  Welcome to the Social Console  ");
        System.out.println("=================================");

        // 2. Main Application Loop
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

        // 3. Clean up resources
        dbConnection.closeConnection();
        scanner.close();
    }

    private static void handleCreateAccount() {
        System.out.println("\n--- CREATE ACCOUNT ---");
        User newUser = new User();

        System.out.print("Enter username: ");
        newUser.setUsername(scanner.nextLine().trim());

        System.out.print("Enter email: ");
        newUser.setEmail(scanner.nextLine().trim());

        System.out.print("Enter password: ");
        // Storing directly for now. In a real app, hash this before setting!
        newUser.setPasswordHash(scanner.nextLine().trim());

        System.out.print("Enter age: ");
        try {
            int age = Integer.parseInt(scanner.nextLine().trim());
            newUser.setAge(age);
        } catch (NumberFormatException e) {
            System.out.println("Invalid age entered. Defaulting to 0.");
            newUser.setAge(0);
        }

        System.out.print("Enter a short bio: ");
        newUser.setBio(scanner.nextLine().trim());

        System.out.print("Enter favorite games (comma-separated): ");
        String gamesInput = scanner.nextLine();
        List<String> gamesList = Arrays.stream(gamesInput.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        newUser.setFavoriteGames(gamesList);

        System.out.print("Enter favorite genres (comma-separated): ");
        String genresInput = scanner.nextLine();
        List<String> genresList = Arrays.stream(genresInput.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
        newUser.setFavoriteGenres(genresList);

        // Attempt to save to database
        boolean success = dbOps.insertUser(newUser);

        if (success) {
            System.out.println("\nAccount created successfully! Welcome, " + newUser.getUsername() + ".");
            System.out.println("Please log in with your new credentials.");
            // Returning here drops them back to the Main Menu so they can select "1. Log
            // In"
        } else {
            System.out.println("\nFailed to create account. That username or email might already be taken.");
        }
    }

    private static void handleLogin() {
        System.out.println("\n--- LOG IN ---");
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        User loggedInUser = dbOps.loginUser(email, password);

        if (loggedInUser != null) {
            System.out.println("\nLogin successful! Welcome back, " + loggedInUser.getUsername() + "!");
            // Transition to the main dashboard!
            showUserDashboard(loggedInUser);
        } else {
            System.out.println("\nLogin failed. Incorrect email or password.");
        }
    }

    // ==========================================
    // MAIN DASHBOARD
    // ==========================================
    private static void showUserDashboard(User user) {
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
                    showCommunityTab(user);
                    break;
                case "2":
                    showFriendsTab(user);
                    break;
                case "3":
                    showFindGamersTab(user);
                    break;
                case "4":
                    // If this returns true, the user deleted their account, so we log them out.
                    boolean accountDeleted = showProfileSettingsTab(user);
                    if (accountDeleted) {
                        loggedIn = false;
                    }
                    break;
                case "5":
                    loggedIn = false;
                    System.out.println("Logging out... Returning to main menu.");
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-5.");
            }
        }
    }

    // ==========================================
    // 1. COMMUNITY TAB
    // ==========================================
    private static void showCommunityTab(User user) {
        boolean inTab = true;
        while (inTab) {
            System.out.println("\n--- COMMUNITY TAB ---");
            System.out.println("1. Discover & Search Communities");
            System.out.println("2. View Joined Communities"); // UPDATE THIS SECTION
            System.out.println("3. Back to Dashboard");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    showCommunitySearchMenu(user);
                    break;
                case "2":
                    System.out.println("\n[Fetching your communities...]");
                    List<Community> joinedComms = dbOps.getJoinedCommunities(user.getUserID());

                    if (joinedComms == null || joinedComms.isEmpty()) {
                        System.out.println("You haven't joined any communities yet! Try discovering some first.");
                    } else {
                        System.out.println("\n--- My Communities ---");
                        for (int i = 0; i < joinedComms.size(); i++) {
                            System.out.println((i + 1) + ". " + joinedComms.get(i).getName());
                        }
                        System.out.print("Select a community to view (1-" + joinedComms.size() + ") or 0 to cancel: ");

                        try {
                            int selection = Integer.parseInt(scanner.nextLine().trim());
                            if (selection > 0 && selection <= joinedComms.size()) {
                                // Jump into the specific community view
                                viewJoinedCommunity(joinedComms.get(selection - 1), user);
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

    // --- NEW SEARCH MENU ---
    // --- UPDATED SEARCH MENU ---
    private static void showCommunitySearchMenu(User user) {
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
                        Community randomComm = dbOps.getRandomCommunity();
                        if (randomComm != null) {
                            boolean stop = interactWithCommunity(randomComm, user);
                            if (stop)
                                rollingRandom = false;
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
                        Community genreComm = dbOps.getRandomCommunityByGenre(genre);

                        if (genreComm != null) {
                            boolean stop = interactWithCommunity(genreComm, user);
                            if (stop)
                                rollingGenre = false;
                        } else {
                            System.out.println("No communities found for the genre: " + genre);
                            rollingGenre = false;
                        }
                    }
                    break;

                case "3":
                    System.out.print("\nEnter community name to search: ");
                    String searchQuery = scanner.nextLine().trim();
                    List<Community> searchResults = dbOps.getCommunitiesByName(searchQuery);

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
                                interactWithCommunity(searchResults.get(selection - 1), user);
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

    // --- NEW METHOD TO HANDLE VIEWING AND JOINING ---
    // --- UPDATED METHOD TO HANDLE VIEWING AND JOINING ---
    // Returns true to exit back to the menu, or false to skip and view another
    private static boolean interactWithCommunity(Community c, User user) {
        boolean viewing = true;

        while (viewing) {
            System.out.println("\n=================================");
            System.out.println(" COMMUNITY: " + c.getName());
            System.out.println(" GENRES: " + String.join(", ", c.getGenres()));
            System.out.println("=================================");
            System.out.println("1. Join Community");
            System.out.println("2. Skip (Show Next Community)");
            System.out.println("3. Exit to Search Menu");
            System.out.print("Action: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                boolean success = dbOps.joinCommunity(user.getUserID(), c.getCommunityID());
                if (success) {
                    System.out.println("\n>>> Success! You are now a member of " + c.getName() + "!");
                } else {
                    System.out.println("\n>>> Failed to join. You might already be a member of this community.");
                }
                return true; // Stop searching, kick them back to menu after joining
            } else if (choice.equals("2")) {
                return false; // Tells the search loop to fetch another community
            } else if (choice.equals("3")) {
                return true; // Stop searching, kick them back to menu
            } else {
                System.out.println("Invalid choice.");
            }
        }
        return true;
    }

    // --- UPDATED METHOD FOR VIEWING A SPECIFIC COMMUNITY ---
    private static void viewJoinedCommunity(Community c, User user) {
        boolean viewing = true;

        while (viewing) {
            System.out.println("\n=================================");
            System.out.println(" 🌟 " + c.getName().toUpperCase() + " 🌟 ");
            System.out.println("=================================");

            // Show recent posts
            List<Post> recentPosts = dbOps.getCommunityPosts(c.getCommunityID());
            if (recentPosts == null || recentPosts.isEmpty()) {
                System.out.println("It's quiet here... Be the first to post!");
            } else {
                // Sort posts by like count in descending order
                recentPosts.sort((p1, p2) -> Integer.compare(p2.getLikeCount(), p1.getLikeCount()));

                for (Post p : recentPosts) {
                    System.out.println("Post ID [" + p.getPostID() + "] | 👍 " + p.getLikeCount() + " | 👎 "
                            + p.getDislikeCount());
                    System.out.println("💬 " + p.getTextContent());
                    System.out.println("---------------------------------");
                }
            }

            System.out.println("\n1. Make a New Post");
            System.out.println("2. Like a Post");
            System.out.println("3. Dislike a Post");
            System.out.println("4. Clear Like/Dislike"); // Add this line!
            System.out.println("5. Back to Communities"); // Update this to 5
            System.out.print("Action: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n--- NEW POST ---");
                    System.out.print("What's on your mind? (Type your post and press Enter):\n> ");
                    String content = scanner.nextLine().trim();

                    if (!content.isEmpty()) {
                        boolean success = dbOps.createPost(c.getCommunityID(), user.getUserID(), content);
                        if (success) {
                            System.out.println("✅ Post published successfully!");
                        } else {
                            System.out.println("❌ Failed to publish post.");
                        }
                    } else {
                        System.out.println("Post cancelled (cannot be empty).");
                    }
                    break;
                case "2":
                    System.out.print("Enter the Post ID to Like: ");
                    try {
                        int postId = Integer.parseInt(scanner.nextLine().trim());
                        if (dbOps.likePost(postId, user.getUserID())) {
                            System.out.println("✅ Post liked!");
                        } else {
                            System.out.println("❌ Failed to like post. (You may have already voted)");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid Post ID number.");
                    }
                    break;
                case "3":
                    System.out.print("Enter the Post ID to Dislike: ");
                    try {
                        int postId = Integer.parseInt(scanner.nextLine().trim());
                        if (dbOps.dislikePost(postId, user.getUserID())) {
                            System.out.println("✅ Post disliked!");
                        } else {
                            System.out.println("❌ Failed to dislike post. (You may have already voted)");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input. Please enter a valid Post ID number.");
                    }
                    break;
                case "4": 
                    System.out.print("Enter the Post ID to clear your Like/Dislike: ");
                    try {
                        int postId = Integer.parseInt(scanner.nextLine().trim());
                        if (dbOps.clearInteraction(postId, user.getUserID())) {
                            System.out.println("✅ Interaction cleared!");
                        } else {
                            System.out.println("❌ Nothing to clear for this post.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid input.");
                    }
                    break;
                case "5":
                    viewing = false;
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }

    // ==========================================
    // 2. FRIENDS TAB
    // ==========================================
    private static void showFriendsTab(User user) {
        boolean inTab = true;
        while (inTab) {
            System.out.println("\n--- FRIENDS TAB ---");
            System.out.println("1. View Online Friends");
            System.out.println("2. Send Notification to Friend");
            System.out.println("3. Add Friend");
            System.out.println("4. View Incoming Friend Requests"); // NEW OPTION
            System.out.println("5. Remove Friend");
            System.out.println("6. View Groups");
            System.out.println("7. Create Group");
            System.out.println("8. Back to Dashboard");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n[Feature: View Online Friends]");
                    break;
                case "2":
                    System.out.println("\n[Feature: Send Notification]");
                    break;
                case "3":
                    System.out.print("\nEnter username to add: ");
                    String addTarget = scanner.nextLine().trim();
                    User targetUser = dbOps.getUserByUsername(addTarget);
                    if (targetUser == null) {
                        System.out.println("User '" + addTarget + "' not found.");
                        break;
                    }
                    if (targetUser.getUserID() == user.getUserID()) {
                        System.out.println("You cannot send a friend request to yourself.");
                        break;
                    }
                    boolean requestSent = dbOps.insertFriendRequest(user.getUserID(), targetUser.getUserID());
                    if (requestSent) {
                        System.out.println("Friend request sent to " + addTarget + "!");
                    } else {
                        System.out.println("Failed to send friend request to " + addTarget + ".");
                    }
                    break;
                case "4":
                    System.out.println("\n--- INCOMING FRIEND REQUESTS ---");
                    List<User> pendingRequests = dbOps.getIncomingFriendRequests(user.getUserID());

                    if (pendingRequests == null || pendingRequests.isEmpty()) {
                        System.out.println("You have no incoming friend requests right now.");
                    } else {
                        for (User sender : pendingRequests) {
                            System.out.println("\nRequest from: " + sender.getUsername());
                            System.out.print("Do you want to accept? (yes/no/skip): ");
                            String answer = scanner.nextLine().trim().toLowerCase();

                            if (answer.equals("yes")) {
                                if (dbOps.acceptFriendRequest(user.getUserID(), sender.getUserID())) {
                                    System.out.println(
                                            "Accepted! You are now friends with " + sender.getUsername() + ".");
                                } else {
                                    System.out.println("Error accepting request.");
                                }
                            } else if (answer.equals("no")) {
                                if (dbOps.declineFriendRequest(user.getUserID(), sender.getUserID())) {
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
                    System.out.println("Removing " + removeTarget + " from friends list...");
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

    // ==========================================
    // 3. FIND GAMERS TAB
    // ==========================================
    private static void showFindGamersTab(User user) {
        boolean inTab = true;
        while (inTab) {
            System.out.println("\n--- FIND GAMERS TAB ---");
            System.out.println("1. Start Queue");
            System.out.println("2. Apply Filters (Games, Genres, Age)");
            System.out.println("3. Back to Dashboard");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n[Feature: Starting Matchmaking Queue...] Looking for gamers!");
                    break;
                case "2":
                    System.out.println("\n--- APPLY FILTERS ---");
                    System.out.print("Filter by Game (leave blank to skip): ");
                    String gameFilter = scanner.nextLine().trim();

                    System.out.print("Filter by Genre (leave blank to skip): ");
                    String genreFilter = scanner.nextLine().trim();

                    System.out.print("Filter by Max Age (leave blank to skip): ");
                    String ageFilter = scanner.nextLine().trim();

                    System.out.println("Filters applied! Game: [" + gameFilter + "], Genre: [" + genreFilter
                            + "], Max Age: [" + ageFilter + "]");
                    break;
                case "3":
                    inTab = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // ==========================================
    // 4. PROFILE SETTINGS
    // ==========================================
    private static boolean showProfileSettingsTab(User user) {
        boolean inTab = true;

        while (inTab) {
            System.out.println("\n--- PROFILE SETTINGS ---");
            System.out.println("1. View My Posts");
            System.out.println("2. Edit Profile Information");
            System.out.println("3. Delete Account");
            System.out.println("4. Back to Dashboard");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.println("\n[Fetching your posts...]");
                    List<Post> myPosts = dbOps.getPostsByUserID(user.getUserID());

                    if (myPosts == null || myPosts.isEmpty()) {
                        System.out.println("You haven't made any posts yet.");
                    } else {
                        System.out.println("\n--- My Posts ---");
                        for (Post p : myPosts) {
                            System.out.println("Post ID: " + p.getPostID());
                            System.out.println("Content: " + p.getTextContent());
                            System.out.println("Likes: " + p.getLikeCount() + " | Dislikes: " + p.getDislikeCount());
                            System.out.println("-------------------");
                        }
                    }
                    break;

                case "2":
                    System.out.println("\n--- EDIT PROFILE ---");
                    System.out.println("Leave a field blank and press Enter to keep current value.");

                    System.out.print("Update Bio (Current: " + user.getBio() + "): ");
                    String newBio = scanner.nextLine().trim();
                    if (!newBio.isEmpty())
                        user.setBio(newBio);

                    System.out.print("Update Age (Current: " + user.getAge() + "): ");
                    String newAgeStr = scanner.nextLine().trim();
                    if (!newAgeStr.isEmpty()) {
                        try {
                            user.setAge(Integer.parseInt(newAgeStr));
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid age format. Keeping old age.");
                        }
                    }

                    if (dbOps.updateUserProfile(user)) {
                        System.out.println("Profile updated successfully!");
                    } else {
                        System.out.println("Failed to update profile.");
                    }
                    break;

                case "3":
                    System.out.println("\n--- DELETE ACCOUNT ---");
                    System.out.print(
                            "Are you SURE you want to delete your account? This cannot be undone! (type 'yes' to confirm): ");
                    String confirm = scanner.nextLine().trim().toLowerCase();

                    if (confirm.equals("yes")) {
                        if (dbOps.deleteUser(user.getUserID())) {
                            System.out.println("Account deleted successfully. We're sad to see you go!");
                            return true; // Return true to trigger the logout in the dashboard
                        } else {
                            System.out.println("Error: Could not delete account.");
                        }
                    } else {
                        System.out.println("Account deletion cancelled.");
                    }
                    break;

                case "4":
                    inTab = false;
                    break;

                default:
                    System.out.println("Invalid option.");
            }
        }
        return false; // Return false so the user stays logged in
    }
}