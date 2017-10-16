package ru.mail.park.database.kgulyy.domains;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * @author Konstantin Gulyy
 */
public class Post {
    private long id;
    private long parent;
    private String author;
    private String message;
    private boolean isEdited;
    private String forum;
    private int thread;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date created;

    @JsonCreator
    public Post(
            @JsonProperty("parent") long parent,
            @JsonProperty("author") String author,
            @JsonProperty("message") String message,
            @JsonProperty("created") Date created) {
        this.id = 0;
        this.parent = parent;
        this.author = author;
        this.message = message;
        this.isEdited = false;
        this.created = created;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getParent() {
        return parent;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessage() {
        return message;
    }

    public boolean isEdited() {
        return isEdited;
    }

    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public int getThread() {
        return thread;
    }

    public void setThread(int thread) {
        this.thread = thread;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @JsonGetter
    public Date getCreated() {
        return created;
    }
}
