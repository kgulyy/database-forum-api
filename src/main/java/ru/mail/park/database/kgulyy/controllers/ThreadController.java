package ru.mail.park.database.kgulyy.controllers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mail.park.database.kgulyy.controllers.exceptions.ParentPostNotFoundException;
import ru.mail.park.database.kgulyy.controllers.exceptions.ThreadNotFoundException;
import ru.mail.park.database.kgulyy.controllers.exceptions.UserNotFoundException;
import ru.mail.park.database.kgulyy.domains.Post;
import ru.mail.park.database.kgulyy.domains.Thread;
import ru.mail.park.database.kgulyy.domains.User;
import ru.mail.park.database.kgulyy.domains.Vote;
import ru.mail.park.database.kgulyy.services.PostService;
import ru.mail.park.database.kgulyy.services.ThreadService;
import ru.mail.park.database.kgulyy.services.UserService;

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
    private final UserService userService;
    private final ThreadService threadService;
    private final PostService postService;

    @Autowired
    ThreadController(UserService userService, ThreadService threadService, PostService postService) {
        this.userService = userService;
        this.threadService = threadService;
        this.postService = postService;
    }

    @PostMapping("/create")
    ResponseEntity<List<Post>> createPosts(@PathVariable String slugOrId, @RequestBody List<Post> listOfPosts) {
        final Optional<Thread> threadOptional;
        if (StringUtils.isNumeric(slugOrId)) {
            final int id = Integer.valueOf(slugOrId);
            threadOptional = threadService.findById(id);
        } else {
            threadOptional = threadService.findBySlug(slugOrId);
        }
        final Thread foundThread = threadOptional
                .orElseThrow(() -> ThreadNotFoundException.throwEx(slugOrId));

        if (listOfPosts.isEmpty()) {
            return ResponseEntity.created(URI.create("")).body(Collections.<Post>emptyList());
        }

        for (Post post : listOfPosts) {
            final int threadId = foundThread.getId();
            final long parentId = post.getParent();
            if (parentId != 0L) {
                @SuppressWarnings("unused") final Post parentPost = postService.findByIdInThread(parentId, threadId)
                        .orElseThrow(ParentPostNotFoundException::throwEx);
            }

            final String authorNickname = post.getAuthor();
            @SuppressWarnings("unused") final User author = userService.findByNickname(authorNickname)
                    .orElseThrow(() -> UserNotFoundException.throwEx(authorNickname));
        }

        final List<Post> createdPosts = postService.create(foundThread, listOfPosts);

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
        final Thread foundThread = threadOptional
                .orElseThrow(() -> ThreadNotFoundException.throwEx(slugOrId));

        final String voteAuthorNickname = vote.getNickname();
        @SuppressWarnings("unused") final User voteAothor = userService.findByNickname(voteAuthorNickname)
                .orElseThrow(() -> UserNotFoundException.throwEx(voteAuthorNickname));

        final Thread votedThread = threadService.vote(foundThread, vote);

        return ResponseEntity.ok(votedThread);
    }

    @GetMapping("/posts")
    ResponseEntity<List<Post>> getThreadPosts(
            @PathVariable String slugOrId,
            @RequestParam Long limit,
            @RequestParam(required = false) Long since,
            @RequestParam(required = false, defaultValue = "false") Boolean desc,
            @RequestParam(required = false, defaultValue = "flat") String sort
    ) {
        final Optional<Thread> threadOptional;
        if (StringUtils.isNumeric(slugOrId)) {
            final int id = Integer.valueOf(slugOrId);
            threadOptional = threadService.findById(id);
        } else {
            threadOptional = threadService.findBySlug(slugOrId);
        }

        final Thread foundThread = threadOptional.orElseThrow(() -> ThreadNotFoundException.throwEx(slugOrId));

        List<Post> posts = Collections.emptyList();
        final SortType sortType = SortType.valueOf(sort.toUpperCase());

        switch (sortType) {
            case FLAT:
                posts = postService.findAndFlatSort(foundThread.getId(), limit, since, desc);
                break;
            case TREE:
                posts = postService.findAndTreeSort(foundThread.getId(), limit, since, desc);
                break;
            case PARENT_TREE:
                posts = postService.findAndParentTreeSort(foundThread.getId(), limit, since, desc);
                break;
        }

        return ResponseEntity.ok(posts);
    }

    @PostMapping("/details")
    ResponseEntity<Thread> updateThread(@PathVariable String slugOrId, @RequestBody Thread updatedThread) {
        final Optional<Thread> threadOptional;
        if (StringUtils.isNumeric(slugOrId)) {
            final int id = Integer.valueOf(slugOrId);
            threadOptional = threadService.findById(id);
        } else {
            threadOptional = threadService.findBySlug(slugOrId);
        }
        final Thread thread = threadOptional.
                orElseThrow(() -> ThreadNotFoundException.throwEx(slugOrId));

        final String updatedTitle = updatedThread.getTitle();
        final String updatedMessage = updatedThread.getMessage();

        if (updatedTitle == null && updatedMessage == null) {
            return ResponseEntity.ok(thread);
        }

        if (updatedTitle != null)
            thread.setTitle(updatedTitle);
        if (updatedMessage != null)
            thread.setMessage(updatedMessage);

        threadService.update(thread);

        return ResponseEntity.ok(thread);
    }
}
