package ru.mail.park.database.kgulyy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mail.park.database.kgulyy.controllers.exceptions.UserNotFoundException;
import ru.mail.park.database.kgulyy.repositories.UserRepository;
import ru.mail.park.database.kgulyy.data.User;

import java.net.URI;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@RestController
@RequestMapping("/api/user/{nickname}")
public class UserController {
    private final UserRepository userRepository;

    @Autowired
    UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    ResponseEntity<User> createUser(@PathVariable String nickname, @RequestBody User user) {
        final Optional<User> conflictUser = userRepository.findByNickname(nickname);
        if (conflictUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictUser.get());
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
    ResponseEntity<User> updateUserProfile(@PathVariable String nickname, @RequestBody User user) {
        @SuppressWarnings("unused") final User foundUser = userRepository.findByNickname(nickname)
                .orElseThrow(() -> UserNotFoundException.throwEx(nickname));

        user.setNickname(nickname);
        userRepository.update(user);

        return ResponseEntity.ok(user);
    }
}
