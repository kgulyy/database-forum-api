package ru.mail.park.database.kgulyy.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.mail.park.database.kgulyy.domains.Post;
import ru.mail.park.database.kgulyy.domains.PostFull;
import ru.mail.park.database.kgulyy.services.PostService;

import java.util.List;

/**
 * @author Konstantin Gulyy
 */
@RestController
@RequestMapping("/api/post/{id}/details")
public class PostController {
    private final PostService postService;

    PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    ResponseEntity<PostFull> getPostDetails(
            @PathVariable long id,
            @RequestParam(required = false) List<String> related
    ) {
        return postService.getPostDetails(id, related);
    }

    @PostMapping
    ResponseEntity<Post> updatePost(@PathVariable long id, @RequestBody Post updatedPost) {
        return postService.updatePost(id, updatedPost);
    }
}
