package ru.mail.park.database.kgulyy.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.database.kgulyy.controllers.exceptions.ThreadNotFoundException;
import ru.mail.park.database.kgulyy.domains.*;
import ru.mail.park.database.kgulyy.domains.Thread;
import ru.mail.park.database.kgulyy.services.ForumService;
import ru.mail.park.database.kgulyy.services.PostService;
import ru.mail.park.database.kgulyy.services.ThreadService;
import ru.mail.park.database.kgulyy.services.UserService;

import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@RestController
@RequestMapping("/api/post/{id}/details")
public class PostController {
    private final UserService userService;
    private final ForumService forumService;
    private final ThreadService threadService;
    private final PostService postService;

    @Autowired
    PostController(UserService userService, ForumService forumService,
                   ThreadService threadService, PostService postService) {
        this.userService = userService;
        this.forumService = forumService;
        this.threadService = threadService;
        this.postService = postService;
    }

    @GetMapping
    ResponseEntity<PostFull> getPostDetails(
            @PathVariable long id,
            @RequestParam(required = false) List<String> related
    ) {
        final Post post = postService.findById(id)
                .orElseThrow(() -> ThreadNotFoundException.throwEx(id));

        final PostFull postFull = new PostFull(post);
        if (related != null) {
            if (related.contains("user")) {
                final Optional<User> foundAuthor = userService.findByNickname(post.getAuthor());
                if (foundAuthor.isPresent()) {
                    final User author = foundAuthor.get();
                    postFull.setAuthor(author);
                }
            }
            if (related.contains("thread")) {
                final Optional<Thread> foundThread = threadService.findById(post.getThread());
                if (foundThread.isPresent()) {
                    final Thread thread = foundThread.get();
                    postFull.setThread(thread);
                }
            }
            if (related.contains("forum")) {
                final Optional<Forum> foundForum = forumService.findBySlug(post.getForum());
                if (foundForum.isPresent()) {
                    final Forum forum = foundForum.get();
                    postFull.setForum(forum);
                }
            }
        }

        return ResponseEntity.ok(postFull);
    }

    @PostMapping
    ResponseEntity<Post> updatePost(@PathVariable long id, @RequestBody Post updatedPost) {
        final Post post = postService.findById(id)
                .orElseThrow(() -> ThreadNotFoundException.throwEx(id));

        final String updatedMesage = updatedPost.getMessage();
        post.setMessage(updatedMesage);
        post.setEdited(true);

        postService.update(post);

        return ResponseEntity.ok(post);
    }
}
