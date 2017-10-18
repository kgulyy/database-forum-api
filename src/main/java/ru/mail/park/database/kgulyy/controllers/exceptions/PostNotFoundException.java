package ru.mail.park.database.kgulyy.controllers.exceptions;

import java.util.NoSuchElementException;

/**
 * @author Konstantin Gulyy
 */
public final class PostNotFoundException extends NoSuchElementException {
    private final Object entity;

    private PostNotFoundException(final Object entity) {
        this.entity = entity;
    }

    public static PostNotFoundException throwEx(final Object entity) {
        return new PostNotFoundException(entity);
    }

    Object getEntity() {
        return entity;
    }
}
