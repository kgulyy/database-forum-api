package ru.mail.park.database.kgulyy.domains;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * @author Konstantin Gulyy
 */
public class Status {
    private int user;
    private int forum;
    private int thread;
    private long post;

    @JsonCreator
    public Status(int user, int forum, int thread, long post) {
        this.user = user;
        this.forum = forum;
        this.thread = thread;
        this.post = post;
    }

    public int getUser() {
        return user;
    }

    public int getForum() {
        return forum;
    }

    public int getThread() {
        return thread;
    }

    @JsonGetter
    public long getPost() {
        return post;
    }
}
