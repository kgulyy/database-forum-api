package ru.mail.park.database.kgulyy.controllers;

import java.util.NoSuchElementException;

/**
 * @author Konstantin Gulyy
 */
final class NotFoundException extends NoSuchElementException {
    private final Object entity;

    private NotFoundException(final Object entity) {
        this.entity = entity;
    }

    static NotFoundException notFoundException(final Object entity) {
        return new NotFoundException(entity);
    }

    public Object getEntity() {
        return entity;
    }
}
