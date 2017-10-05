package ru.mail.park.database.kgulyy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mail.park.database.kgulyy.controllers.exceptions.ForumNotFoundException;
import ru.mail.park.database.kgulyy.controllers.exceptions.UserNotFoundException;
import ru.mail.park.database.kgulyy.data.Forum;
import ru.mail.park.database.kgulyy.data.User;
import ru.mail.park.database.kgulyy.repositories.ForumRepository;
import ru.mail.park.database.kgulyy.repositories.UserRepository;

import java.net.URI;
import java.util.Optional;

/**
 * @author Konstantin Gulyy.
 */
@RestController
@RequestMapping("/api/forum")
public class ForumController {
    private final ForumRepository forumRepository;
    private final UserRepository userRepository;

    @Autowired
    ForumController(ForumRepository forumRepository, UserRepository userRepository) {
        this.forumRepository = forumRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/create")
    ResponseEntity<Forum> createForum(@RequestBody Forum forum) {
        final String userNickname = forum.getUser();
        final User foundUser = userRepository.findByNickname(userNickname)
                .orElseThrow(() -> UserNotFoundException.throwEx(userNickname));

        final Optional<Forum> conflictForum = forumRepository.findBySlug(forum.getSlug());
        if (conflictForum.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictForum.get());
        }

        forumRepository.save(forum);

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/api/forum/{slug}/details")
                .buildAndExpand(forum.getSlug()).toUri();

        return ResponseEntity.created(uri).body(forum);
    }

    @GetMapping("/{slug}/details")
    ResponseEntity<Forum> getForumDetails(@PathVariable String slug) {
        return forumRepository
                .findBySlug(slug)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ForumNotFoundException.throwEx(slug));
    }
}
