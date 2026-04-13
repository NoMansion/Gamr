// Written by Kyle Flatt
// Refactored to delegate logic to the Service layer

package src;

import java.util.List;

/**
 * Handles searching functionality for communities and posts
 */
public class SearchController {

    // The controller needs a reference to the Service layer
    private Service service;

    // Constructor to inject the shared Service instance
    public SearchController(Service service) {
        this.service = service;
    }

    /**
     * Searches communities by name by delegating to the Service layer.
     */
    public void searchCommunitiesByName(String nameQuery) {
        // The Service layer now handles the DB call and the printing
        service.searchCommunitiesByName(nameQuery);
    }

    /**
     * Filters a list of communities by genre. 
     * (Replaced findCommunity to match the UML diagram exactly)
     */
    public void filterCommunitySearchByGenre(List<Community> communities, String genre) {
        service.filterCommunitySearchByGenre(communities, genre);
    }

    /**
     * Retrieves all posts made by a specific user.
     */
    public List<Post> retrieveUserPosts(User user) {
        // Delegate the database fetch to the Service layer
        return service.retrieveUserPosts(user);
    }
}