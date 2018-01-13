package ru.mail.park.database.kgulyy.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mail.park.database.kgulyy.domains.*;
import ru.mail.park.database.kgulyy.domains.Thread;
import ru.mail.park.database.kgulyy.repositories.ForumRepository;
import ru.mail.park.database.kgulyy.repositories.PostRepository;
import ru.mail.park.database.kgulyy.repositories.ThreadRepository;
import ru.mail.park.database.kgulyy.repositories.UserRepository;
import ru.mail.park.database.kgulyy.services.exceptions.*;

import java.net.URI;
import java.util.*;

/**
 * @author Konstantin Gulyy
 */
@Service
public class PostService {
    private UserRepository userRepository;
    private ForumRepository forumRepository;
    private ThreadRepository threadRepository;
    private PostRepository postRepository;

    public PostService(UserRepository userRepository, ForumRepository forumRepository,
                       ThreadRepository threadRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.forumRepository = forumRepository;
        this.threadRepository = threadRepository;
        this.postRepository = postRepository;
    }

    public ResponseEntity<List<Post>> createPosts(String slugOrId, List<Post> listOfPosts) {
        final Optional<Thread> threadOptional;
        if (StringUtils.isNumeric(slugOrId)) {
            final int id = Integer.valueOf(slugOrId);
            threadOptional = threadRepository.findById(id);
        } else {
            threadOptional = threadRepository.findBySlug(slugOrId);
        }
        final Thread foundThread = threadOptional
                .orElseThrow(() -> ThreadNotFoundException.throwEx(slugOrId));

        if (listOfPosts.isEmpty()) {
            return ResponseEntity.created(URI.create("")).body(Collections.<Post>emptyList());
        }

        final Set<Integer> authorsIds = new HashSet<>();

        for (Post post : listOfPosts) {
            final int threadId = foundThread.getId();
            final long parentId = post.getParent();
            if (parentId != 0L) {
                @SuppressWarnings("unused") final Integer parentPostId = postRepository
                        .findByIdInThread(parentId, threadId)
                        .orElseThrow(ParentPostNotFoundException::throwEx);
            }

            final String authorNickname = post.getAuthor();
            final Integer authorId = userRepository.getIdByNickname(authorNickname)
                    .orElseThrow(() -> UserNotFoundException.throwEx(authorNickname));
            authorsIds.add(authorId);
        }

        final List<Post> createdPosts = postRepository.create(foundThread, listOfPosts);

        final String forumSlug = foundThread.getForum();
        int forumId = forumRepository.getIdBySlug(forumSlug).
                orElseThrow(() -> ForumNotFoundException.throwEx(forumSlug));

        forumRepository.incrementPostsCounter(forumId, listOfPosts.size());

        for (int id : authorsIds) {
            forumRepository.addForumUser(forumId, id);
        }

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/api/thread/{id}/posts")
                .buildAndExpand(slugOrId).toUri();

        return ResponseEntity.created(uri).body(createdPosts);
    }

    public ResponseEntity<PostFull> getPostDetails(long id, List<String> related) {
        final Post post = postRepository.findById(id)
                .orElseThrow(() -> PostNotFoundException.throwEx(String.valueOf(id)));

        final PostFull postFull = new PostFull(post);
        if (related != null) {
            if (related.contains("user")) {
                final Optional<User> foundAuthor = userRepository.findByNickname(post.getAuthor());
                if (foundAuthor.isPresent()) {
                    final User author = foundAuthor.get();
                    postFull.setAuthor(author);
                }
            }
            if (related.contains("thread")) {
                final Optional<Thread> foundThread = threadRepository.findById(post.getThread());
                if (foundThread.isPresent()) {
                    final Thread thread = foundThread.get();
                    postFull.setThread(thread);
                }
            }
            if (related.contains("forum")) {
                final Optional<Forum> foundForum = forumRepository.findBySlug(post.getForum());
                if (foundForum.isPresent()) {
                    final Forum forum = foundForum.get();
                    postFull.setForum(forum);
                }
            }
        }

        return ResponseEntity.ok(postFull);
    }

    public ResponseEntity<Post> updatePost(long id, Post updatedPost) {
        final Post post = postRepository.findById(id)
                .orElseThrow(() -> PostNotFoundException.throwEx(String.valueOf(id)));

        final String updatedMessage = updatedPost.getMessage();
        if (updatedMessage == null) {
            return ResponseEntity.ok(post);
        }

        if (!updatedMessage.equals(post.getMessage())) {
            post.setMessage(updatedMessage);
            post.setEdited(true);
            postRepository.update(post);
        }

        return ResponseEntity.ok(post);
    }

    public ResponseEntity<List<Post>> getThreadPosts(String slugOrId, Long limit, Long since, Boolean desc, String sort) {
        final Optional<Thread> threadOptional;
        if (StringUtils.isNumeric(slugOrId)) {
            final int id = Integer.valueOf(slugOrId);
            threadOptional = threadRepository.findById(id);
        } else {
            threadOptional = threadRepository.findBySlug(slugOrId);
        }

        final Thread foundThread = threadOptional.orElseThrow(() -> ThreadNotFoundException.throwEx(slugOrId));

        List<Post> posts = Collections.emptyList();
        final SortType sortType = SortType.valueOf(sort.toUpperCase());

        switch (sortType) {
            case FLAT:
                posts = postRepository.findAndFlatSort(foundThread.getId(), limit, since, desc);
                break;
            case TREE:
                posts = postRepository.findAndTreeSort(foundThread.getId(), limit, since, desc);
                break;
            case PARENT_TREE:
                posts = postRepository.findAndParentTreeSort(foundThread.getId(), limit, since, desc);
                break;
        }

        return ResponseEntity.ok(posts);
    }
}
