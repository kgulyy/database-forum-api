package ru.mail.park.database.kgulyy.services;

import ru.mail.park.database.kgulyy.domains.Thread;
import ru.mail.park.database.kgulyy.domains.Vote;

import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
public interface ThreadService {
    Thread create(Thread thread, int forumId, int userId);

    Optional<Thread> findBySlug(String slug);

    Optional<Thread> findById(int id);

    List<Thread> findForumThreads(String forumSlug, Integer limit, String since, Boolean desc);

    Thread vote(Thread thread, Vote vote);

    void update(Thread thread);
}
