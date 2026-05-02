package src;

import java.util.List;
import java.util.ArrayList;

public class Community
{
    private int communityID;
    private String name;
    private List<String> genres;
    private List<User> members;
    private List<Post> communityPosts;

    public Community(){}

    public Community(int communityID, String name, List<String> genres, List<User> members, List<Post> communityPosts)
    {
        this.communityID = communityID;
        this.name = name;
        this.genres = new ArrayList<>();
        this.members = new ArrayList<>();
        this.communityPosts = new ArrayList<>();
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public void addMember(User user)
    {
        this.members.add(user);
    }

    public void createPost(int postId, User author, String textContent)
    {
        this.communityPosts.add(new Post(postId, author, this, textContent, 0, 0, new ArrayList<>()));
    }

    public void likePost(Post post)
    {
        post.setLikeCount(post.getLikeCount() + 1);
    }

    public void dislikePost(Post post)
    {
        post.setDislikeCount(post.getDislikeCount() + 1);
    }

    public void setCommunityID(int communityID) {
        this.communityID = communityID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public void setCommunityPosts(List<Post> communityPosts) {
        this.communityPosts = communityPosts;
    }

    public String getName() {
        return name;
    }

    public int getCommunityID() {
        return communityID;
    }
}