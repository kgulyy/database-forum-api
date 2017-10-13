package ru.mail.park.database.kgulyy.services;

import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ru.mail.park.database.kgulyy.domains.Thread;

import java.util.List;

/**
 * @author Konstantin Gulyy
 */
@Service
public class ThreadRepository {
    private final MultiValueMap<String, Thread> threads = new LinkedMultiValueMap<>();

    public void save(Thread thread) {
        final String forumSlug = thread.getForum();
        threads.add(forumSlug, thread);
    }

    public List<Thread> findByForumSlug(String forumSlug) {
        return threads.get(forumSlug);
    }
}
