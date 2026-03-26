public class Post
{
    private int postID
    private User author;
    private Community community;
    private String textContent;
    private int likeCount;
    private int dislikeCount;
    private List<Comment> comments;

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

    //Getters and setters will be implemented later.

    public createComment(User author, String textContent)
    {
        this.comments.add(Comment(author, parentPost, textContent, 0, 0));
    }
}
