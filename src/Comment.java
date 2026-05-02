package src;
// Class for Comment
// Written by Sam Good

public class Comment {

    private int commentId;
    private User author;
    private Post parentPost;
    private String textContent;
    private int likesCount;
    private int dislikeCount;

    public Comment() {
    }
    
    public Comment(int commentId, User author, Post parentPost, String textContent) {
        this.commentId = commentId;
        this.author = author;
        this.parentPost = parentPost;
        this.textContent = textContent;
        this.likesCount = 0;
        this.dislikeCount = 0;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Post getParentPost() {
        return parentPost;
    }

    public void setParentPost(Post parentPost) {
        this.parentPost = parentPost;
    }

    public String getTextContent() {
        return textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getDislikeCount() {
        return dislikeCount;
    }

    public void setDislikeCount(int dislikeCount) {
        this.dislikeCount = dislikeCount;
    }

    public int getCommentID() {
        return commentId;
    }

    public void setCommentID(int commentId) {
        this.commentId = commentId;
    }
    
}
