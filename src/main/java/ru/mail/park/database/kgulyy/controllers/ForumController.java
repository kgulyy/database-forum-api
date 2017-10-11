package ru.mail.park.database.kgulyy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mail.park.database.kgulyy.controllers.exceptions.ForumNotFoundException;
import ru.mail.park.database.kgulyy.controllers.exceptions.UserNotFoundException;
import ru.mail.park.database.kgulyy.data.Forum;
import ru.mail.park.database.kgulyy.data.Thread;
import ru.mail.park.database.kgulyy.data.User;
import ru.mail.park.database.kgulyy.repositories.ForumRepository;
import ru.mail.park.database.kgulyy.repositories.ThreadRepository;
import ru.mail.park.database.kgulyy.repositories.UserRepository;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@RestController
@RequestMapping("/api/forum")
public class ForumController {
    private final ForumRepository forumRepository;
    private final UserRepository userRepository;
    private final ThreadRepository threadRepository;

    @Autowired
    ForumController(ForumRepository forumRepository, UserRepository userRepository, ThreadRepository threadRepository) {
        this.forumRepository = forumRepository;
        this.userRepository = userRepository;
        this.threadRepository = threadRepository;
    }

    @PostMapping("/create")
    ResponseEntity<Forum> createForum(@RequestBody Forum forum) {
        final String userNickname = forum.getUser();
        @SuppressWarnings("unused") final User foundUser = userRepository.findByNickname(userNickname)
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

    @PostMapping("/{forumSlug}/create")
    ResponseEntity<Thread> createThread(@PathVariable String forumSlug, @RequestBody Thread thread) {
        final String authorNickname = thread.getAuthor();
        @SuppressWarnings("unused") final User foundUser = userRepository.findByNickname(authorNickname)
                .orElseThrow(() -> UserNotFoundException.throwEx(authorNickname));

        @SuppressWarnings("unused") final Forum foundForum = forumRepository.findBySlug(forumSlug)
                .orElseThrow(() -> ForumNotFoundException.throwEx(forumSlug));

        // TODO check conflict thread

        thread.setForum(forumSlug);
        threadRepository.save(thread);

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/api/thread/{slug_or_id}/details")
                .buildAndExpand(thread.getId()).toUri();

        return ResponseEntity.created(uri).body(thread);
    }

    @GetMapping("/{forumSlug}/threads")
    ResponseEntity<List<Thread>> getThreadsByForumSlug(@PathVariable String forumSlug) {
        @SuppressWarnings("unused") final Forum foundForum = forumRepository.findBySlug(forumSlug)
                .orElseThrow(() -> ForumNotFoundException.throwEx(forumSlug));

        return ResponseEntity.ok(threadRepository.findByForumSlug(forumSlug));
    }
}
