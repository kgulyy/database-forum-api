package ru.mail.park.database.kgulyy.domains;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Konstantin Gulyy
 */
public class Vote {
    private String nickname;
    private short voice;

    @JsonCreator
    public Vote(
            @JsonProperty("nickname") String nickname,
            @JsonProperty("voice") short voice) {
        this.nickname = nickname;
        this.voice = voice;
    }

    public String getNickname() {
        return nickname;
    }

    public short getVoice() {
        return voice;
    }
}
