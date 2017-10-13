package ru.mail.park.database.kgulyy.domains;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Konstantin Gulyy
 */
public class Forum {
    private String slug;
    private String title;
    private long posts;
    private int threads;
    private String user;

    @JsonCreator
    public Forum(
            @JsonProperty("slug") String slug,
            @JsonProperty("title") String title,
            @JsonProperty("user") String user) {
        this.slug = slug;
        this.title = title;
        this.user = user;
        posts = 0;
        threads = 0;
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
    public long getPosts() {
        return posts;
    }

    @JsonGetter
    public int getThreads() {
        return threads;
    }

    @JsonGetter
    public String getUser() {
        return user;
    }
}
