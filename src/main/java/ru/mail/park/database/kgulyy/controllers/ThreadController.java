package ru.mail.park.database.kgulyy.controllers;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mail.park.database.kgulyy.domains.Post;
import ru.mail.park.database.kgulyy.services.PostService;

import java.net.URI;
import java.util.Collections;
import java.util.List;

/**
 * @author Konstantin Gulyy
 */
@RestController
@RequestMapping("api/thread/{slugOrId}")
public class ThreadController {
    private final PostService postService;

    @Autowired
    ThreadController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/create")
    ResponseEntity<List<Post>> createPosts(@PathVariable String slugOrId, @RequestBody List<Post> listOfPosts) {
        if (listOfPosts.isEmpty()) {
            return ResponseEntity.created(URI.create("")).body(Collections.<Post>emptyList());
        }

        List<Post> createdPosts;
        if (StringUtils.isNumeric(slugOrId)) {
            int id = Integer.valueOf(slugOrId);
            createdPosts = postService.save(id, listOfPosts);
        } else {
            createdPosts = postService.save(slugOrId, listOfPosts);
        }

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/api/thread/{id}/posts")
                .buildAndExpand(slugOrId).toUri();

        return ResponseEntity.created(uri).body(createdPosts);
    }
}
