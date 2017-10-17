package ru.mail.park.database.kgulyy.controllers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mail.park.database.kgulyy.controllers.exceptions.ThreadNotFoundException;
import ru.mail.park.database.kgulyy.domains.Post;
import ru.mail.park.database.kgulyy.domains.Thread;
import ru.mail.park.database.kgulyy.domains.Vote;
import ru.mail.park.database.kgulyy.services.PostService;
import ru.mail.park.database.kgulyy.services.ThreadService;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@RestController
@RequestMapping("api/thread/{slugOrId}")
public class ThreadController {
    private final ThreadService threadService;
    private final PostService postService;

    @Autowired
    ThreadController(ThreadService threadService, PostService postService) {
        this.threadService = threadService;
        this.postService = postService;
    }

    @PostMapping("/create")
    ResponseEntity<List<Post>> createPosts(@PathVariable String slugOrId, @RequestBody List<Post> listOfPosts) {
        if (listOfPosts.isEmpty()) {
            return ResponseEntity.created(URI.create("")).body(Collections.<Post>emptyList());
        }

        final List<Post> createdPosts;
        if (StringUtils.isNumeric(slugOrId)) {
            final int id = Integer.valueOf(slugOrId);
            createdPosts = postService.create(id, listOfPosts);
        } else {
            createdPosts = postService.create(slugOrId, listOfPosts);
        }

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/api/thread/{id}/posts")
                .buildAndExpand(slugOrId).toUri();

        return ResponseEntity.created(uri).body(createdPosts);
    }

    @GetMapping("/details")
    ResponseEntity<Thread> getThreadDetails(@PathVariable String slugOrId) {
        final Optional<Thread> foundThread;
        if (StringUtils.isNumeric(slugOrId)) {
            final int id = Integer.valueOf(slugOrId);
            foundThread = threadService.findById(id);
        } else {
            foundThread = threadService.findBySlug(slugOrId);
        }

        return foundThread
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ThreadNotFoundException.throwEx(slugOrId));
    }

    @PostMapping("/vote")
    ResponseEntity<Thread> voteThread(@PathVariable String slugOrId, @RequestBody Vote vote) {
        final Optional<Thread> threadOptional;
        if (StringUtils.isNumeric(slugOrId)) {
            final int id = Integer.valueOf(slugOrId);
            threadOptional = threadService.findById(id);
        } else {
            threadOptional = threadService.findBySlug(slugOrId);
        }

        final Thread foundThread = threadOptional.orElseThrow(() -> ThreadNotFoundException.throwEx(slugOrId));
        final Thread votedThread = threadService.vote(foundThread, vote);

        return ResponseEntity.ok(votedThread);
    }
}
