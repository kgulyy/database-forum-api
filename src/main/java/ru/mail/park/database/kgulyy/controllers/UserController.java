package ru.mail.park.database.kgulyy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.database.kgulyy.domains.User;
import ru.mail.park.database.kgulyy.services.UserService;

/**
 * @author Konstantin Gulyy
 */
@RestController
@RequestMapping("/api/user/{nickname}")
public class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    ResponseEntity<?> createUser(@PathVariable String nickname, @RequestBody User user) {
        return userService.createUser(nickname, user);
    }

    @GetMapping("/profile")
    ResponseEntity<User> getUserProfile(@PathVariable String nickname) {
        return userService.getUserProfile(nickname);
    }

    @PostMapping("/profile")
    ResponseEntity<?> updateUserProfile(@PathVariable String nickname, @RequestBody User updatedUser) {
        return userService.updateUserProfile(nickname, updatedUser);
    }
}
