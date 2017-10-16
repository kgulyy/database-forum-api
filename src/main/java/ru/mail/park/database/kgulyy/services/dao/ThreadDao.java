package ru.mail.park.database.kgulyy.services.dao;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import ru.mail.park.database.kgulyy.domains.Thread;
import ru.mail.park.database.kgulyy.services.ThreadService;

/**
 * @author Konstantin Gulyy
 */
@Service
public class ThreadDao implements ThreadService {
    private final NamedParameterJdbcTemplate namedTemplate;

    public ThreadDao(NamedParameterJdbcTemplate namedTemplate) {
        this.namedTemplate = namedTemplate;
    }

    @Override
    public Thread save(Thread thread) {
        final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("title", thread.getTitle());
        params.addValue("author", thread.getAuthor());
        params.addValue("forum", thread.getForum());
        params.addValue("message", thread.getMessage());
        params.addValue("votes", thread.getVotes());
        params.addValue("slug", thread.getSlug());
        params.addValue("created", thread.getCreated());

        namedTemplate.update("INSERT INTO threads(title, author, forum, message, votes, slug, created)" +
                " VALUES(:title, :author, :forum, :message, :votes, :slug, :created) RETURNING id", params, keyHolder);

        thread.setId(keyHolder.getKey().intValue());

        namedTemplate.update("UPDATE forums SET threads=threads + 1 WHERE slug=:forum", params);

        return thread;
    }
}
