package ru.mail.park.database.kgulyy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mail.park.database.kgulyy.controllers.exceptions.UserNotFoundException;
import ru.mail.park.database.kgulyy.domains.User;
import ru.mail.park.database.kgulyy.services.UserService;
import ru.mail.park.database.kgulyy.services.dao.UserDao;

import java.net.URI;
import java.util.List;

import static ru.mail.park.database.kgulyy.controllers.messages.MessageEnum.NEW_USER_PROFILE_CONFLICT;

/**
 * @author Konstantin Gulyy
 */
@RestController
@RequestMapping("/api/user/{nickname}")
public class UserController {
    private final UserService userService;

    @Autowired
    UserController(UserDao userDao) {
        this.userService = userDao;
    }

    @PostMapping("/create")
    ResponseEntity<?> createUser(@PathVariable String nickname, @RequestBody User user) {
        final List<User> conflictUsers = userService.findByNicknameOrEmail(nickname, user.getEmail());
        if (!conflictUsers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictUsers);
        }

        user.setNickname(nickname);
        userService.save(user);

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/api/user/{nickname}/profile")
                .buildAndExpand(nickname).toUri();

        return ResponseEntity.created(uri).body(user);
    }

    @GetMapping("/profile")
    ResponseEntity<User> getUserProfile(@PathVariable String nickname) {
        return userService
                .findByNickname(nickname)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> UserNotFoundException.throwEx(nickname));
    }

    @PostMapping("/profile")
    ResponseEntity<?> updateUserProfile(@PathVariable String nickname, @RequestBody User updatedUser) {
        @SuppressWarnings("unused") final User user = userService.findByNickname(nickname)
                .orElseThrow(() -> UserNotFoundException.throwEx(nickname));

        final String updatedFullname = updatedUser.getFullname();
        final String updatedEmail = updatedUser.getEmail();
        final String updatedAbout = updatedUser.getAbout();

        if (updatedFullname == null && updatedEmail == null && updatedAbout == null) {
            return ResponseEntity.ok(user);
        }

        if (updatedFullname != null)
            user.setFullname(updatedFullname);
        if (updatedEmail != null)
            user.setEmail(updatedEmail);
        if (updatedAbout != null)
            user.setAbout(updatedAbout);

        if (userService.isExistOtherWithSameEmail(nickname, updatedEmail)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(NEW_USER_PROFILE_CONFLICT.getMessage());
        }

        userService.update(user);

        return ResponseEntity.ok(user);
    }
}
