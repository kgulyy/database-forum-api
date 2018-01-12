package ru.mail.park.database.kgulyy.services.exceptions;

import java.util.NoSuchElementException;

/**
 * @author Konstantin Gulyy
 */
public final class ThreadNotFoundException extends NoSuchElementException {
    private final Object entity;

    private ThreadNotFoundException(final Object entity) {
        this.entity = entity;
    }

    public static ThreadNotFoundException throwEx(final Object entity) {
        return new ThreadNotFoundException(entity);
    }

    Object getEntity() {
        return entity;
    }
}
