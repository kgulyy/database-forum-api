package ru.mail.park.database.kgulyy.services.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.database.kgulyy.domains.Thread;
import ru.mail.park.database.kgulyy.domains.Vote;
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

    private static final RowMapper<Short> VOICE_ROW_MAPPER = (res, num) -> res.getShort("voice");

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
        sql.append("SELECT * FROM threads");
        sql.append(" WHERE LOWER(forum)=LOWER(:forum)");
        if (since != null) {
            sql.append(" AND created").append(sign).append(":since::TIMESTAMPTZ");
        }
        sql.append(" ORDER BY created").append(order);
        sql.append("LIMIT :limit");

        return namedTemplate.query(sql.toString(), params, THREAD_ROW_MAPPER);
    }

    @Override
    public Thread vote(Thread thread, Vote vote) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", vote.getNickname());
        params.addValue("threadId", thread.getId());
        params.addValue("voice", vote.getVoice());

        final List<Short> currentVoice = namedTemplate.query("SELECT voice FROM votes" +
                " WHERE nickname=:nickname AND thread_id=:threadId", params, VOICE_ROW_MAPPER);

        namedTemplate.update("INSERT INTO votes (nickname, thread_id, voice)" +
                " VALUES (:nickname, :threadId, :voice)" +
                " ON CONFLICT (nickname, thread_id) DO UPDATE SET voice=:voice", params);

        int votes = thread.getVotes();
        votes += vote.getVoice();
        if (!currentVoice.isEmpty()) {
            votes -= currentVoice.get(0);
        }

        params.addValue("votes", votes);
        namedTemplate.update("UPDATE threads SET votes=:votes" +
                " WHERE id=:threadId", params);

        thread.setVotes(votes);
        return thread;
    }

    @Override
    public void update(Thread thread) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", thread.getId());
        params.addValue("title", thread.getTitle());
        params.addValue("message", thread.getMessage());

        namedTemplate.update("UPDATE threads SET title=:title, message=:message WHERE id=:id", params);
    }

}
