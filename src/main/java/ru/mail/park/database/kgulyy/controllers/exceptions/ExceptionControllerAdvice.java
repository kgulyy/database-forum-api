package ru.mail.park.database.kgulyy.controllers.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.mail.park.database.kgulyy.controllers.messages.Message;

import static ru.mail.park.database.kgulyy.controllers.messages.MessageEnum.*;

/**
 * @author Konstantin Gulyy
 */
@ControllerAdvice
final class ExceptionControllerAdvice {
    @ExceptionHandler(UserNotFoundException.class)
    ResponseEntity<Message> acceptNotFoundException(final UserNotFoundException ex) {
        final String nickname = (String) ex.getEntity();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(USER_NOT_FOUND.getMessage(nickname));
    }

    @ExceptionHandler(ForumNotFoundException.class)
    ResponseEntity<Message> acceptNotFoundException(final ForumNotFoundException ex) {
        final String slug = (String) ex.getEntity();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(FORUM_NOT_FOUND.getMessage(slug));
    }

    @ExceptionHandler(ThreadNotFoundException.class)
    ResponseEntity<Message> acceptNotFoundException(final ThreadNotFoundException ex) {
        final String slugOrId = (String) ex.getEntity();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(THREAD_NOT_FOUND.getMessage(slugOrId));
    }
}
