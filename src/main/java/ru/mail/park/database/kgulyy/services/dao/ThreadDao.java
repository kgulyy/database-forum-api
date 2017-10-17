package ru.mail.park.database.kgulyy.services.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.database.kgulyy.domains.Thread;
import ru.mail.park.database.kgulyy.services.ThreadService;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@Service
@Transactional
public class ThreadDao implements ThreadService {
    private final NamedParameterJdbcTemplate namedTemplate;

    public ThreadDao(NamedParameterJdbcTemplate namedTemplate) {
        this.namedTemplate = namedTemplate;
    }

    private static final RowMapper<Thread> THREAD_ROW_MAPPER = (res, num) -> {
        Integer id = res.getInt("id");
        String title = res.getString("title");
        String author = res.getString("author");
        String forum = res.getString("forum");
        String message = res.getString("message");
        Integer votes = res.getInt("votes");
        String slug = res.getString("slug");
        if (res.wasNull()) {
            slug = null;
        }
        Date created = res.getTimestamp("created");
        if (res.wasNull()) {
            created = null;
        }

        return new Thread(id, title, author, forum, message, votes, slug, created);
    };

    @Override
    public Thread create(Thread thread) {
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

    @Override
    public Optional<Thread> findBySlug(String slug) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("slug", slug);

        final List<Thread> threads = namedTemplate.query("SELECT * FROM threads" +
                " WHERE LOWER(slug)=LOWER(:slug)", params, THREAD_ROW_MAPPER);

        if (threads.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(threads.get(0));
    }

    @Override
    public Optional<Thread> findById(int id) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        final List<Thread> threads = namedTemplate.query("SELECT * FROM threads" +
                " WHERE id=:id", params, THREAD_ROW_MAPPER);

        if (threads.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(threads.get(0));
    }

    @Override
    public List<Thread> getForumThreadsDesc(String forumSlug, int limit) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("forumSlug", forumSlug);
        params.addValue("limit", limit);

        return namedTemplate.query("SELECT * FROM threads" +
                " WHERE LOWER(forum)=LOWER(:forumSlug)" +
                " ORDER BY created DESC LIMIT :limit", params, THREAD_ROW_MAPPER);
    }

    @Override
    public List<Thread> getForumThreadsAsc(String forumSlug, int limit) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("forumSlug", forumSlug);
        params.addValue("limit", limit);

        return namedTemplate.query("SELECT * FROM threads" +
                " WHERE LOWER(forum)=LOWER(:forumSlug)" +
                " ORDER BY created ASC LIMIT :limit", params, THREAD_ROW_MAPPER);
    }

    @Override
    public List<Thread> getForumThreadsSinceDesc(String forumSlug, int limit, String since) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("forumSlug", forumSlug);
        params.addValue("limit", limit);
        params.addValue("since", since);

        return namedTemplate.query("SELECT * FROM threads" +
                " WHERE LOWER(forum)=LOWER(:forumSlug) AND created <= :since::TIMESTAMPTZ" +
                " ORDER BY created DESC LIMIT :limit", params, THREAD_ROW_MAPPER);
    }

    @Override
    public List<Thread> getForumThreadsSinceAsc(String forumSlug, int limit, String since) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("forumSlug", forumSlug);
        params.addValue("limit", limit);
        params.addValue("since", since);

        return namedTemplate.query("SELECT * FROM threads" +
                " WHERE LOWER(forum)=LOWER(:forumSlug) AND created >= :since::TIMESTAMPTZ" +
                " ORDER BY created ASC LIMIT :limit", params, THREAD_ROW_MAPPER);
    }


}
