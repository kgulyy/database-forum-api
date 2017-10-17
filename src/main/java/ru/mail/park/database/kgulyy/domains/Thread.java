package ru.mail.park.database.kgulyy.domains;


import com.fasterxml.jackson.annotation.*;

import java.util.Date;

/**
 * @author Konstantin Gulyy
 */
public class Thread {
    private int id;
    private String title;
    private String author;
    private String forum;
    private String message;
    private int votes;
    private String slug;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date created;

    @JsonCreator
    public Thread(
            @JsonProperty("title") String title,
            @JsonProperty("author") String author,
            @JsonProperty("message") String message,
            @JsonProperty("slug") String slug,
            @JsonProperty("created") Date created) {
        this.id = 0;
        this.title = title;
        this.author = author;
        this.message = message;
        this.votes = 0;
        this.slug = slug;
        this.created = created;
    }

    public Thread(int id, String title, String author, String forum, String message, int votes, String slug, Date created) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.forum = forum;
        this.message = message;
        this.votes = votes;
        this.slug = slug;
        this.created = created;
    }

    @JsonGetter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @JsonGetter
    public String getSlug() {
        return slug;
    }

    @JsonGetter
    public String getTitle() {
        return title;
    }

    @JsonGetter
    public String getMessage() {
        return message;
    }

    @JsonGetter
    public String getForum() {
        return forum;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    @JsonGetter
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @JsonGetter
    public Date getCreated() {
        return created;
    }

    @JsonGetter
    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
