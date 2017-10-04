package ru.mail.park.database.kgulyy.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Konstantin Gulyy
 */
public class User {
    private String nickname;
    private String fullname;
    private String email;
    private String about;

    @JsonCreator
    public User(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("fullname") String fullname,
            @JsonProperty("email") String email,
            @JsonProperty("about") String about) {
        this.nickname = nickname;
        this.fullname = fullname;
        this.email = email;
        this.about = about;
    }

    @JsonGetter
    public String getNickname() {
        return nickname;
    }

    @JsonGetter
    public String getFullname() {
        return fullname;
    }

    @JsonGetter
    public String getEmail() {
        return email;
    }

    @JsonGetter
    public String getAbout() {
        return about;
    }
}
