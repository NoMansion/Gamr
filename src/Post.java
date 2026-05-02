package src;

import java.util.ArrayList;
import java.util.List;

public class Post
{
    private int postID;
    private User author;
    private Community community;
    private String textContent;
    private int likeCount;
    private int dislikeCount;
    private List<Comment> comments;

    public Post(){
        
    }

    public Post(int postId, User author, Community community, String textContent, int likeCount, int dislikeCount, List<Comment> comments)
    {
        this.postID = postId;
        this.author = author;
        this.community = community;
        this.textContent = textContent;
        this.likeCount = likeCount;
        this.dislikeCount = dislikeCount;
        this.comments = new ArrayList<>();
    }

    public Comment createComment(int ID, User author, String textContent)
    {
        Comment comment = new Comment(ID, author, this, textContent);
        this.comments.add(comment);
        return comment;
    }

    public int getPostID() {
        return postID;
    }

    public void setPostID(int postID) {
        this.postID = postID;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }
}