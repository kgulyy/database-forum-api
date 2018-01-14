package ru.mail.park.database.kgulyy.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.database.kgulyy.domains.Thread;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@Repository
@Transactional
public class ThreadRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedTemplate;

    public ThreadRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
        Date created = res.getTimestamp("created");

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
        final String sql = "SELECT * FROM threads WHERE slug = ?::CITEXT";
        Object[] params = new Object[]{slug};

        final List<Thread> threads = jdbcTemplate.query(sql, params, THREAD_ROW_MAPPER);

        if (threads.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(threads.get(0));
    }

    public Optional<Thread> findById(int id) {
        final String sql = "SELECT * FROM threads WHERE id = ?";
        Object[] params = new Object[]{id};

        final List<Thread> threads = jdbcTemplate.query(sql, params, THREAD_ROW_MAPPER);

        if (threads.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(threads.get(0));
    }

    public List<Thread> findForumThreads(String forumSlug, Integer limit, String since, Boolean desc) {
        final List<Object> params = new ArrayList<>();

        final String order = desc ? " DESC " : " ASC ";
        final String sign = desc ? " <= " : " >= ";

        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM threads ");
        sql.append("WHERE forum = ?::citext ");
        params.add(forumSlug);
        if (since != null) {
            sql.append(" AND created").append(sign).append("?::TIMESTAMPTZ ");
            params.add(since);
        }
        sql.append("ORDER BY created").append(order);
        sql.append("LIMIT ?");
        params.add(limit);

        return jdbcTemplate.query(sql.toString(), params.toArray(), THREAD_ROW_MAPPER);
    }

    public void update(Thread thread) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", thread.getId());
        params.addValue("title", thread.getTitle());
        params.addValue("message", thread.getMessage());

        namedTemplate.update("UPDATE threads SET title=:title, message=:message WHERE id=:id", params);
    }

    private static final RowMapper<Integer> VOTES_ROW_MAPPER = (res, num) -> res.getInt("votes");

    public int updateVotes(int threadId, short voice, boolean doubleUpdate) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", threadId);
        if (doubleUpdate) {
            voice *= 2;
        }
        params.addValue("voice", voice);

        return namedTemplate.queryForObject(
                "UPDATE threads SET votes = votes + :voice WHERE id = :id RETURNING votes", params, VOTES_ROW_MAPPER);
    }

}
