package ru.mail.park.database.kgulyy.controllers.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Konstantin Gulyy
 */
public class Message {
    private final String message;

    @JsonCreator
    public Message(@JsonProperty("message") String message) {
        this.message = message;
    }

    @JsonGetter
    public String getMessage() {
        return message;
    }
}
