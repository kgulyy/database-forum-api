package ru.mail.park.database.kgulyy.controllers.messages;

import java.text.MessageFormat;

/**
 * @author Konstantin Gulyy
 */
public enum MessageEnum {
    USER_NOT_FOUND("User with nickname ''{0}'' not found");

    private MessageFormat message;

    MessageEnum(String message) {
        this.message = new MessageFormat(message);
    }

    public Message getMessage(String... str) {
        return new Message(message.format(str));
    }
}
