package ru.mail.park.database.kgulyy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.database.kgulyy.domains.Post;
import ru.mail.park.database.kgulyy.domains.Thread;
import ru.mail.park.database.kgulyy.domains.Vote;
import ru.mail.park.database.kgulyy.services.PostService;
import ru.mail.park.database.kgulyy.services.ThreadService;

import java.util.List;

/**
 * @author Konstantin Gulyy
 */
@RestController
@RequestMapping("/api/thread/{slugOrId}")
public class ThreadController {
    private final ThreadService threadService;
    private final PostService postService;

    ThreadController(ThreadService threadService, PostService postService) {
        this.threadService = threadService;
        this.postService = postService;
    }

    @PostMapping("/create")
    ResponseEntity<List<Post>> createPosts(@PathVariable String slugOrId, @RequestBody List<Post> listOfPosts) {
        return postService.createPosts(slugOrId, listOfPosts);
    }

    @GetMapping("/details")
    ResponseEntity<Thread> getThreadDetails(@PathVariable String slugOrId) {
        return threadService.getThreadDetails(slugOrId);
    }

    @PostMapping("/vote")
    ResponseEntity<Thread> voteThread(@PathVariable String slugOrId, @RequestBody Vote vote) {
        return threadService.voteThread(slugOrId, vote);
    }

    @GetMapping("/posts")
    ResponseEntity<List<Post>> getThreadPosts(
            @PathVariable String slugOrId,
            @RequestParam Long limit,
            @RequestParam(required = false) Long since,
            @RequestParam(required = false, defaultValue = "false") Boolean desc,
            @RequestParam(required = false, defaultValue = "flat") String sort
    ) {
        return postService.getThreadPosts(slugOrId, limit, since, desc, sort);
    }

    @PostMapping("/details")
    ResponseEntity<Thread> updateThread(@PathVariable String slugOrId, @RequestBody Thread updatedThread) {
        return threadService.updateThread(slugOrId, updatedThread);
    }
}
