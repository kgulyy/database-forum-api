package ru.mail.park.database.kgulyy.domains;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Konstantin Gulyy
 */
public class Forum {
    @JsonIgnore
    private int id;
    private String slug;
    private String title;
    private String author;
    private long posts;
    private int threads;

    @JsonCreator
    public Forum(
            @JsonProperty("slug") String slug,
            @JsonProperty("title") String title,
            @JsonProperty("user") String author) {
        this.slug = slug;
        this.title = title;
        this.author = author;
    }

    public Forum(int id, String slug, String title, String author, long posts, int threads) {
        this.id = id;
        this.slug = slug;
        this.title = title;
        this.author = author;
        this.posts = posts;
        this.threads = threads;
    }

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

    @JsonGetter(value = "user")
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @JsonGetter
    public long getPosts() {
        return posts;
    }

    @JsonGetter
    public int getThreads() {
        return threads;
    }
}
