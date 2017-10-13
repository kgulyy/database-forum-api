package ru.mail.park.database.kgulyy.domains;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

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

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @JsonGetter
    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    @JsonGetter
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonGetter
    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }
}