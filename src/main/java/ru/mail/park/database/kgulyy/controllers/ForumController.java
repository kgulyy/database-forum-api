package ru.mail.park.database.kgulyy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mail.park.database.kgulyy.controllers.exceptions.ForumNotFoundException;
import ru.mail.park.database.kgulyy.controllers.exceptions.UserNotFoundException;
import ru.mail.park.database.kgulyy.domains.Forum;
import ru.mail.park.database.kgulyy.domains.Thread;
import ru.mail.park.database.kgulyy.domains.User;
import ru.mail.park.database.kgulyy.services.ForumService;
import ru.mail.park.database.kgulyy.services.ThreadService;
import ru.mail.park.database.kgulyy.services.UserService;
import ru.mail.park.database.kgulyy.services.dao.ForumDao;
import ru.mail.park.database.kgulyy.services.dao.ThreadDao;
import ru.mail.park.database.kgulyy.services.dao.UserDao;

import java.net.URI;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@RestController
@RequestMapping("/api/forum")
public class ForumController {
    private final ForumService forumService;
    private final UserService userService;
    private final ThreadService threadService;

    @Autowired
    ForumController(ForumDao forumDao, UserDao userDao, ThreadDao threadDao) {
        this.forumService = forumDao;
        this.userService = userDao;
        this.threadService = threadDao;
    }

    @PostMapping("/create")
    ResponseEntity<Forum> createForum(@RequestBody Forum forum) {
        final String authorNickname = forum.getAuthor();
        @SuppressWarnings("unused") final User author = userService.findByNickname(authorNickname)
                .orElseThrow(() -> UserNotFoundException.throwEx(authorNickname));

        final Optional<Forum> conflictForum = forumService.findBySlug(forum.getSlug());
        if (conflictForum.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictForum.get());
        }

        forum.setAuthor(author.getNickname());
        forumService.save(forum);

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/api/forum/{slug}/details")
                .buildAndExpand(forum.getSlug()).toUri();

        return ResponseEntity.created(uri).body(forum);
    }

    @GetMapping("/{slug}/details")
    ResponseEntity<Forum> getForumDetails(@PathVariable String slug) {
        return forumService
                .findBySlug(slug)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ForumNotFoundException.throwEx(slug));
    }

    @PostMapping("/{forumSlug}/create")
    ResponseEntity<Thread> createThread(@PathVariable String forumSlug, @RequestBody Thread thread) {
        final String authorNickname = thread.getAuthor();
        @SuppressWarnings("unused") final User foundUser = userService.findByNickname(authorNickname)
                .orElseThrow(() -> UserNotFoundException.throwEx(authorNickname));

        @SuppressWarnings("unused") final Forum foundForum = forumService.findBySlug(forumSlug)
                .orElseThrow(() -> ForumNotFoundException.throwEx(forumSlug));

        // TODO check conflict thread

        thread.setForum(forumSlug);
        final Thread createdThread = threadService.save(thread);

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/api/thread/{slug_or_id}/details")
                .buildAndExpand(createdThread.getId()).toUri();

        return ResponseEntity.created(uri).body(createdThread);
    }
}
