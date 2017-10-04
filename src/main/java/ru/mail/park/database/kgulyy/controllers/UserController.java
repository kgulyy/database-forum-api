package ru.mail.park.database.kgulyy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
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
    ResponseEntity<?> createUser(@PathVariable String nickname, @RequestBody User input) {
        final Optional<User> conflictUser = userRepository.findByNickname(nickname);
        if (conflictUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictUser.get());
        }

        final User user = new User(nickname, input.getFullname(), input.getEmail(), input.getAbout());
        userRepository.save(user);

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/api/user/{nickname}/profile")
                .buildAndExpand(nickname).toUri();

        return ResponseEntity.created(uri).body(user);
    }

    @GetMapping("/profile")
    ResponseEntity<?> getUserProfile(@PathVariable String nickname) {
        return userRepository
                .findByNickname(nickname)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> NotFoundException.notFoundException(nickname));
    }
}
