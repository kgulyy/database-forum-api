package ru.mail.park.database.kgulyy.controllers.exceptions;

import java.util.NoSuchElementException;

/**
 * @author Konstantin Gulyy
 */
public final class UserNotFoundException extends NoSuchElementException {
    private final Object entity;

    private UserNotFoundException(final Object entity) {
        this.entity = entity;
    }

    public static UserNotFoundException throwEx(final Object entity) {
        return new UserNotFoundException(entity);
    }

    Object getEntity() {
        return entity;
    }
}
