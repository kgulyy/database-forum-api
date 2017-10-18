package ru.mail.park.database.kgulyy.domains;

/**
 * @author Konstantin Gulyy
 */
public class PostFull {
    private Post post;
    private User author;
    private Thread thread;
    private Forum forum;

    public PostFull(Post post) {
        this.post = post;
    }

    @SuppressWarnings("unused")
    public Post getPost() {
        return post;
    }

    public User getAuthor() {
        return author;
    }

    public Thread getThread() {
        return thread;
    }

    public Forum getForum() {
        return forum;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public void setThread(Thread thread) {
        this.thread = thread;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }
}
