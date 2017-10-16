package ru.mail.park.database.kgulyy.services;

import ru.mail.park.database.kgulyy.domains.Thread;

import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
public interface ThreadService {
    Thread save(Thread thread);

    Optional<Thread> findBySlug(String slug);

    Optional<Thread> findById(int id);

    List<Thread> getForumThreadsDesc(String forumSlug, int limit);

    List<Thread> getForumThreadsAsc(String forumSlug, int limit);

    List<Thread> getForumThreadsSinceDesc(String forumSlug, int limit, String since);

    List<Thread> getForumThreadsSinceAsc(String forumSlug, int limit, String since);
}
