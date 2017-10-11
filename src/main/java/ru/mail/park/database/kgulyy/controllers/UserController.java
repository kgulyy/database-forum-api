package ru.mail.park.database.kgulyy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mail.park.database.kgulyy.controllers.exceptions.UserNotFoundException;
import ru.mail.park.database.kgulyy.data.User;
import ru.mail.park.database.kgulyy.repositories.UserService;

import java.net.URI;
import java.util.List;

import static ru.mail.park.database.kgulyy.controllers.messages.MessageEnum.NEW_USER_DATA_CONFLICT;

/**
 * @author Konstantin Gulyy
 */
@RestController
@RequestMapping("/api/user/{nickname}")
public class UserController {
    private final UserService userRepository;

    @Autowired
    UserController(UserService userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    ResponseEntity<?> createUser(@PathVariable String nickname, @RequestBody User user) {
        final List<User> conflictUsers = userRepository.findByNicknameOrEmail(nickname, user.getEmail());
        if (!conflictUsers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictUsers);
        }

        user.setNickname(nickname);
        userRepository.save(user);

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/api/user/{nickname}/profile")
                .buildAndExpand(nickname).toUri();

        return ResponseEntity.created(uri).body(user);
    }

    @GetMapping("/profile")
    ResponseEntity<User> getUserProfile(@PathVariable String nickname) {
        return userRepository
                .findByNickname(nickname)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> UserNotFoundException.throwEx(nickname));
    }

    @PostMapping("/profile")
    ResponseEntity<?> updateUserProfile(@PathVariable String nickname, @RequestBody User user) {
        @SuppressWarnings("unused") final User foundUser = userRepository.findByNickname(nickname)
                .orElseThrow(() -> UserNotFoundException.throwEx(nickname));

        if (userRepository.isExistOtherWithSameEmail(nickname, user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(NEW_USER_DATA_CONFLICT.getMessage());
        }

        user.setNickname(nickname);
        userRepository.update(user);

        return ResponseEntity.ok(user);
    }
}
