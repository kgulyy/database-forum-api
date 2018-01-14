package ru.mail.park.database.kgulyy.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.database.kgulyy.domains.Post;
import ru.mail.park.database.kgulyy.domains.Thread;

import java.util.*;

/**
 * @author Konstantin Gulyy
 */
@Repository
@Transactional
public class PostRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedTemplate;

    public PostRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedTemplate = namedTemplate;
    }

    private static final RowMapper<Post> POST_ROW_MAPPER = (res, num) -> {
        Long id = res.getLong("id");
        Long parent = res.getLong("parent_id");
        String author = res.getString("author");
        String message = res.getString("message");
        Boolean isEdited = res.getBoolean("is_edited");
        String forum = res.getString("forum");
        Integer thread = res.getInt("thread_id");
        Date created = res.getTimestamp("created");

        return new Post(id, parent, author, message, isEdited, forum, thread, created);
    };

    private static final RowMapper<Integer> POST_ID_MAPPER = (res, num) -> res.getInt("id");

    public List<Post> create(Thread thread, List<Post> posts) {
        final int numberOfPosts = posts.size();
        final List<Long> ids = jdbcTemplate.queryForList("SELECT nextval('posts_id_seq') FROM generate_series(1,?)",
                Long.class, numberOfPosts);

        final ListIterator<Long> idIterator = ids.listIterator();
        final List<MapSqlParameterSource> postParamsList = new ArrayList<>(numberOfPosts);

        for (Post post : posts) {
            post.setId(idIterator.next());
            post.setThread(thread.getId());
            post.setForum(thread.getForum());
            post.setCreated(thread.getCreated());

            final MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("id", post.getId());
            params.addValue("parent", post.getParent());
            params.addValue("author", post.getAuthor());
            params.addValue("message", post.getMessage());
            params.addValue("isEdited", post.isEdited());
            params.addValue("forum", post.getForum());
            params.addValue("thread", post.getThread());
            params.addValue("created", post.getCreated());

            postParamsList.add(params);
        }

        MapSqlParameterSource[] postParamsArray = new MapSqlParameterSource[postParamsList.size()];
        postParamsArray = postParamsList.toArray(postParamsArray);

        namedTemplate.batchUpdate(
                "INSERT INTO posts(id, parent_id, author, message, is_edited, forum, thread_id, created, path)" +
                        " VALUES(:id, :parent, :author, :message, :isEdited, :forum, :thread, :created, " +
                        "(SELECT path FROM posts p WHERE p.id = :parent) || :id)", postParamsArray);

        return posts;
    }

    public List<Post> findAndFlatSort(Integer threadId, Long limit, Long since, Boolean desc) {
        List<Object> params = new ArrayList<>();

        final String order = desc ? " DESC " : " ASC ";
        final String sign = desc ? " < " : " > ";

        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT id, parent_id, author, message, is_edited, forum, thread_id, created ");
        sql.append("FROM posts WHERE thread_id = ? ");
        params.add(threadId);
        if (since != null) {
            sql.append("AND id").append(sign).append("? ");
            params.add(since);
        }
        sql.append("ORDER BY id").append(order);
        sql.append("LIMIT ?");
        params.add(limit);

        return jdbcTemplate.query(sql.toString(), params.toArray(), POST_ROW_MAPPER);
    }

    public List<Post> findAndTreeSort(Integer threadId, Long limit, Long since, Boolean desc) {
        List<Object> params = new ArrayList<>();

        final String order = desc ? " DESC " : " ASC ";
        final String sign = desc ? " < " : " > ";

        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT id, parent_id, author, message, is_edited, forum, thread_id, created ");
        sql.append("FROM posts WHERE thread_id = ? ");
        params.add(threadId);
        if (since != null) {
            sql.append("AND path").append(sign).append("(SELECT path FROM posts WHERE id = ?) ");
            params.add(since);
        }
        sql.append("ORDER BY path").append(order);
        sql.append("LIMIT ?");
        params.add(limit);

        return jdbcTemplate.query(sql.toString(), params.toArray(), POST_ROW_MAPPER);
    }

    public List<Post> findAndParentTreeSort(Integer threadId, Long limit, Long since, Boolean desc) {
        List<Object> params = new ArrayList<>();

        final String order = desc ? " DESC " : " ASC ";
        final String sign = desc ? " < " : " > ";

        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT id, parent_id, author, message, is_edited, forum, thread_id, created ");
        sql.append("FROM posts WHERE thread_id = ? AND path[1] IN ");
        params.add(threadId);
        sql.append("(SELECT id FROM posts WHERE thread_id = ? AND parent_id = 0 ");
        params.add(threadId);
        if (since != null) {
            sql.append("AND path").append(sign).append("(SELECT path FROM posts WHERE id = ?) ");
            params.add(since);
        }
        sql.append("ORDER BY id ").append(order).append("LIMIT ?) ");
        params.add(limit);
        sql.append("ORDER BY path").append(order);

        return jdbcTemplate.query(sql.toString(), params.toArray(), POST_ROW_MAPPER);
    }

    public Optional<Post> findById(long id) {
        final String sql = "SELECT id, parent_id, author, message, is_edited, forum, thread_id, created " +
                "FROM posts WHERE id = ?";
        Object[] params = new Object[]{id};

        final List<Post> posts = jdbcTemplate.query(sql, params, POST_ROW_MAPPER);

        if (posts.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(posts.get(0));
    }

    public Optional<Integer> findByIdInThread(long postId, int threadId) {
        final String sql = "SELECT id FROM posts WHERE id = ? AND thread_id = ?";
        Object[] params = new Object[]{postId, threadId};

        final List<Integer> postIds = jdbcTemplate.query(sql, params, POST_ID_MAPPER);

        if (postIds.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(postIds.get(0));
    }

    public void update(Post post) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", post.getId());
        params.addValue("message", post.getMessage());
        params.addValue("isEdited", post.isEdited());

        namedTemplate.update("UPDATE posts SET message=:message, is_edited=:isEdited WHERE id=:id", params);
    }
}
