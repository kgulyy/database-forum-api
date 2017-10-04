package ru.mail.park.database.kgulyy.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.mail.park.database.kgulyy.controllers.messages.Message;

import static ru.mail.park.database.kgulyy.controllers.messages.MessageEnum.USER_NOT_FOUND;

/**
 * @author Konstantin Gulyy
 */
@ControllerAdvice
final class ExceptionControllerAdvice {
    @ExceptionHandler(NotFoundException.class)
    ResponseEntity<Message> acceptNotFoundException(final NotFoundException ex) {
        final String nickname = (String) ex.getEntity();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(USER_NOT_FOUND.getMessage(nickname));
    }

}
