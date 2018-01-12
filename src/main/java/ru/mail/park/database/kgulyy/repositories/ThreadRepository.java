package ru.mail.park.database.kgulyy.repositories;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.database.kgulyy.domains.Thread;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@Repository
@Transactional
public class ThreadRepository {
    private final NamedParameterJdbcTemplate namedTemplate;

    public ThreadRepository(NamedParameterJdbcTemplate namedTemplate) {
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

        return thread;
    }

    public Optional<Thread> findBySlug(String slug) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("slug", slug);

        final List<Thread> threads = namedTemplate.query(
                "SELECT * FROM threads WHERE slug = :slug::CITEXT", params, THREAD_ROW_MAPPER);

        if (threads.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(threads.get(0));
    }

    public Optional<Thread> findById(int id) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        final List<Thread> threads = namedTemplate.query(
                "SELECT * FROM threads WHERE id=:id", params, THREAD_ROW_MAPPER);

        if (threads.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(threads.get(0));
    }

    public List<Thread> findForumThreads(String forumSlug, Integer limit, String since, Boolean desc) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("forum", forumSlug);
        params.addValue("limit", limit);
        if (since != null) {
            params.addValue("since", since);
        }

        final String order = desc ? " DESC " : " ASC ";
        final String sign = desc ? " <= " : " >= ";

        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM threads ");
        sql.append("WHERE forum = :forum::citext");
        if (since != null) {
            sql.append(" AND created").append(sign).append(":since::TIMESTAMPTZ");
        }
        sql.append(" ORDER BY created").append(order);
        sql.append("LIMIT :limit");

        return namedTemplate.query(sql.toString(), params, THREAD_ROW_MAPPER);
    }

    public void update(Thread thread) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", thread.getId());
        params.addValue("title", thread.getTitle());
        params.addValue("message", thread.getMessage());

        namedTemplate.update("UPDATE threads SET title=:title, message=:message WHERE id=:id", params);
    }

}
