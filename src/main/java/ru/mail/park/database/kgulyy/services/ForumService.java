package ru.mail.park.database.kgulyy.services;

import ru.mail.park.database.kgulyy.domains.Forum;

import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
public interface ForumService {
    void save(Forum forum);

    Optional<Forum> findBySlug(String slug);
}
