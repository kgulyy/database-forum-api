package ru.mail.park.database.kgulyy.services.exceptions;

import java.util.NoSuchElementException;

/**
 * @author Konstantin Gulyy
 */
public final class ParentPostNotFoundException extends NoSuchElementException {
    public static ParentPostNotFoundException throwEx() {
        return new ParentPostNotFoundException();
    }
}
