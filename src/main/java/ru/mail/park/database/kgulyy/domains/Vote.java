package ru.mail.park.database.kgulyy.domains;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Konstantin Gulyy
 */
public class Vote {
    @JsonIgnore
    private int id;
    private String nickname;
    private short voice;

    @JsonCreator
    public Vote(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("voice") short voice) {
        this.nickname = nickname;
        this.voice = voice;
    }

    public Vote(int id, short voice) {
        this.id = id;
        this.voice = voice;
    }

    public int getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public short getVoice() {
        return voice;
    }
}
