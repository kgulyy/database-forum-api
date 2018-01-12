package ru.mail.park.database.kgulyy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.database.kgulyy.domains.Forum;
import ru.mail.park.database.kgulyy.domains.Thread;
import ru.mail.park.database.kgulyy.domains.User;
import ru.mail.park.database.kgulyy.services.ForumService;
import ru.mail.park.database.kgulyy.services.ThreadService;
import ru.mail.park.database.kgulyy.services.UserService;

import java.util.List;

/**
 * @author Konstantin Gulyy
 */
@RestController
@RequestMapping("/api/forum")
public class ForumController {
    private final UserService userService;
    private final ForumService forumService;
    private final ThreadService threadService;

    ForumController(UserService userService, ForumService forumService, ThreadService threadService) {
        this.userService = userService;
        this.forumService = forumService;
        this.threadService = threadService;
    }

    @PostMapping("/create")
    ResponseEntity<Forum> createForum(@RequestBody Forum forum) {
        return forumService.createForum(forum);
    }

    @GetMapping("/{slug}/details")
    ResponseEntity<Forum> getForumDetails(@PathVariable String slug) {
        return forumService.getForumDetails(slug);
    }

    @PostMapping("/{forumSlug}/create")
    ResponseEntity<Thread> createThread(@PathVariable String forumSlug, @RequestBody Thread thread) {
        return threadService.createThread(forumSlug, thread);
    }

    @GetMapping("/{forumSlug}/threads")
    ResponseEntity<List<Thread>> getListOfThreads(
            @PathVariable String forumSlug,
            @RequestParam(required = false, defaultValue = "100") Integer limit,
            @RequestParam(required = false) String since,
            @RequestParam(required = false, defaultValue = "false") Boolean desc
    ) {
        return threadService.getListOfThreads(forumSlug, limit, since, desc);
    }

    @GetMapping("/{forumSlug}/users")
    ResponseEntity<List<User>> getForumUsers(
            @PathVariable String forumSlug,
            @RequestParam(required = false, defaultValue = "100") Integer limit,
            @RequestParam(required = false) String since,
            @RequestParam(required = false, defaultValue = "false") Boolean desc
    ) {
        return userService.getForumUsers(forumSlug, limit, since, desc);
    }
}
