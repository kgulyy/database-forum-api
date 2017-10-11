package ru.mail.park.database.kgulyy.controllers.exceptions;

import java.util.NoSuchElementException;

/**
 * @author Konstantin Gulyy
 */
public final class ForumNotFoundException extends NoSuchElementException {
    private final Object entity;

    private ForumNotFoundException(final Object entity) {
        this.entity = entity;
    }

    public static ForumNotFoundException throwEx(final Object entity) {
        return new ForumNotFoundException(entity);
    }

    Object getEntity() {
        return entity;
    }
}
