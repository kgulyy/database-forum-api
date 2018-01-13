package ru.mail.park.database.kgulyy.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mail.park.database.kgulyy.domains.Forum;
import ru.mail.park.database.kgulyy.domains.User;
import ru.mail.park.database.kgulyy.repositories.ForumRepository;
import ru.mail.park.database.kgulyy.repositories.UserRepository;
import ru.mail.park.database.kgulyy.services.exceptions.ForumNotFoundException;
import ru.mail.park.database.kgulyy.services.exceptions.UserNotFoundException;

import java.net.URI;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@Service
public class ForumService {
    private UserRepository userRepository;
    private ForumRepository forumRepository;

    public ForumService(UserRepository userRepository, ForumRepository forumRepository) {
        this.userRepository = userRepository;
        this.forumRepository = forumRepository;
    }

    public ResponseEntity<Forum> createForum(Forum forum) {
        final String authorNickname = forum.getAuthor();
        final User author = userRepository.findByNickname(authorNickname)
                .orElseThrow(() -> UserNotFoundException.throwEx(authorNickname));

        final Optional<Forum> conflictForum = forumRepository.findBySlug(forum.getSlug());
        if (conflictForum.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictForum.get());
        }

        forum.setAuthor(author.getNickname());
        forumRepository.create(forum);

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/api/forum/{slug}/details")
                .buildAndExpand(forum.getSlug()).toUri();

        return ResponseEntity.created(uri).body(forum);
    }

    public ResponseEntity<Forum> getForumDetails(String slug) {
        return forumRepository
                .findBySlug(slug)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ForumNotFoundException.throwEx(slug));
    }
}
