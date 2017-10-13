package ru.mail.park.database.kgulyy.domains;


import com.fasterxml.jackson.annotation.*;

import java.util.Date;

/**
 * @author Konstantin Gulyy
 */
public class Thread {
    private int id;
    private String slug;
    private String title;
    private String message;
    private String forum;
    private String author;
    private int votes;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date created;

    @JsonCreator
    public Thread(
            @JsonProperty("slug") String slug,
            @JsonProperty("title") String title,
            @JsonProperty("message") String message,
            @JsonProperty("author") String author,
            @JsonProperty("created") Date created) {
        this.id = 0;
        this.slug = slug;
        this.title = title;
        this.message = message;
        this.author = author;
        this.created = created;
        this.votes = 0;
    }

    @JsonGetter
    public int getId() {
        return id;
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

    @JsonSetter
    public void setForum(String forum) {
        this.forum = forum;
    }

    @JsonGetter
    public String getAuthor() {
        return author;
    }

    @JsonGetter
    public Date getCreated() {
        return created;
    }

    @JsonGetter
    public int getVotes() {
        return votes;
    }
}
