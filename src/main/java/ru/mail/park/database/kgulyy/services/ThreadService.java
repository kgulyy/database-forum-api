package ru.mail.park.database.kgulyy.services;

import ru.mail.park.database.kgulyy.domains.Thread;

import java.util.List;

/**
 * @author Konstantin Gulyy
 */
public interface ThreadService {
    Thread save(Thread thread);

    List<Thread> getForumThreadsDesc(String forumSlug, int limit);

    List<Thread> getForumThreadsAsc(String forumSlug, int limit);

    List<Thread> getForumThreadsSinceDesc(String forumSlug, int limit, String since);

    List<Thread> getForumThreadsSinceAsc(String forumSlug, int limit, String since);
}
