package ru.mail.park.database.kgulyy.repositories;

import org.springframework.stereotype.Service;
import ru.mail.park.database.kgulyy.data.Forum;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@Service
public class ForumRepository {
    private final Map<String, Forum> forums = new HashMap<>();

    public void save(Forum forum) {
        final String slug = forum.getSlug();
        forums.put(slug, forum);
    }

    public Optional<Forum> findBySlug(String slug) {
        return Optional.ofNullable(forums.get(slug));
    }
}
