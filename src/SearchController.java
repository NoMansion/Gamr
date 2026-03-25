// Written by Kyle Flatt

package src;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles searching functionality for communities and posts
 */
public class SearchController {

    /**
     * Searches communities by name and prints results to console
     */
    public void searchCommunitiesByName(String nameQuery) {

        // SQL query to find communities with names matching the search
        String query = "SELECT * FROM communities WHERE name LIKE ?";

        try (
            // Get database connection from singleton
            Connection conn = DatabaseConnection.getInstance().getConnection();

            // Prepare the SQL statement
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {

            // Replace ? with user input (with wildcards for partial match)
            stmt.setString(1, "%" + nameQuery + "%");

            // Execute query and store results
            ResultSet rs = stmt.executeQuery();

            // Loop through results and print each community name
            while (rs.next()) {
                String name = rs.getString("name");
                System.out.println("Found community: " + name);
            }

        } catch (SQLException e) {
            // Handle database errors
            e.printStackTrace();
        }
    }

    /**
     * Returns a list of communities that match the search term
     */
    public List<Community> findCommunity(String searchTerm) {

        // List to store results
        List<Community> results = new ArrayList<>();

        // SQL query for searching communities
        String query = "SELECT * FROM communities WHERE name LIKE ?";

        try (
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {

            // Insert search term into query
            stmt.setString(1, "%" + searchTerm + "%");

            ResultSet rs = stmt.executeQuery();

            // Convert each database row into a Community object
            while (rs.next()) {
                Community c = new Community();

                // Map database column to object field
                c.setName(rs.getString("name"));

                results.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Retrieves all posts made by a specific user
     */
    public List<Post> retrieveUserPosts(User user) {

        // List to store posts
        List<Post> posts = new ArrayList<>();

        // SQL query to get posts by user id
        String query = "SELECT * FROM posts WHERE author_id = ?";

        try (
            Connection conn = DatabaseConnection.getInstance().getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)
        ) {

            // Set the user ID in the query
            stmt.setInt(1, user.getId());

            ResultSet rs = stmt.executeQuery();

            // Convert each row into a Post object
            while (rs.next()) {
                Post p = new Post();

                // Map database column to object field
                p.setContent(rs.getString("content"));

                posts.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return posts;
    }
}