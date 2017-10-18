package ru.mail.park.database.kgulyy.controllers.messages;

import java.text.MessageFormat;

/**
 * @author Konstantin Gulyy
 */
public enum MessageEnum {
    USER_NOT_FOUND("User with nickname ''{0}'' not found"),
    FORUM_NOT_FOUND("Forum with slug ''{0}'' not found"),
    THREAD_NOT_FOUND("Thread with slug or id ''{0}'' not found"),
    POST_NOT_FOUND("Can't find post with id: {0}"),
    NEW_USER_PROFILE_CONFLICT("New user profile conflict with other profiles"),
    PARENT_POST_NOT_FOUND("Parent post was created in another thread");

    private MessageFormat message;

    MessageEnum(String message) {
        this.message = new MessageFormat(message);
    }

    public Message getMessage(String... str) {
        return new Message(message.format(str));
    }
}
