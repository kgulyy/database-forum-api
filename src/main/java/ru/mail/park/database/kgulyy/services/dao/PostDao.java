package ru.mail.park.database.kgulyy.services.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.database.kgulyy.domains.Post;
import ru.mail.park.database.kgulyy.domains.Thread;
import ru.mail.park.database.kgulyy.services.PostService;

import java.util.*;

/**
 * @author Konstantin Gulyy
 */
@Service
@Transactional
public class PostDao implements PostService {
    private final JdbcTemplate template;
    private final NamedParameterJdbcTemplate namedTemplate;

    public PostDao(JdbcTemplate template, NamedParameterJdbcTemplate namedTemplate) {
        this.template = template;
        this.namedTemplate = namedTemplate;
    }

    private static final RowMapper<Post> POST_ROW_MAPPER = (res, num) -> {
        Long id = res.getLong("id");
        Long parent = res.getLong("parent");
        String author = res.getString("author");
        String message = res.getString("message");
        Boolean isEdited = res.getBoolean("isEdited");
        String forum = res.getString("forum");
        Integer thread = res.getInt("thread");
        Date created = res.getTimestamp("created");
        if (res.wasNull()) {
            created = null;
        }

        return new Post(id, parent, author, message, isEdited, forum, thread, created);
    };

    @Override
    public List<Post> create(Thread thread, List<Post> posts) {
        final int numberOfPosts = posts.size();
        final List<Long> ids = template.queryForList("SELECT nextval('posts_id_seq') FROM generate_series(1,?)",
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
                "INSERT INTO posts(id, parent, author, message, isEdited, forum, thread, created, path)" +
                        " VALUES(:id, :parent, :author, :message, :isEdited, :forum, :thread, :created, " +
                        "(SELECT path FROM posts p WHERE p.id = :parent) || :id)", postParamsArray);

        template.update("UPDATE forums SET posts = posts + ? WHERE slug = ?", posts.size(), thread.getForum());

        return posts;
    }

    @Override
    public List<Post> findAndFlatSort(Integer threadId, Long limit, Long since, Boolean desc) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("thread", threadId);
        params.addValue("limit", limit);

        final String order = desc ? " DESC " : " ASC ";
        final String sign = desc ? " < " : " > ";

        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM posts WHERE thread = :thread ");
        if (since != null) {
            sql.append("AND id").append(sign).append(since).append(' ');
        }
        sql.append("ORDER BY id").append(order);
        sql.append("LIMIT :limit");

        return namedTemplate.query(sql.toString(), params, POST_ROW_MAPPER);
    }

    @Override
    public List<Post> findAndTreeSort(Integer threadId, Long limit, Long since, Boolean desc) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("thread", threadId);
        params.addValue("limit", limit);

        final String order = desc ? " DESC " : " ASC ";
        final String sign = desc ? " < " : " > ";

        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM posts WHERE thread = :thread ");
        if (since != null) {
            sql.append("AND path").append(sign).append("(SELECT path FROM posts WHERE id = ").append(since).append(") ");
        }
        sql.append("ORDER BY path").append(order);
        sql.append("LIMIT :limit");

        return namedTemplate.query(sql.toString(), params, POST_ROW_MAPPER);
    }

    @Override
    public List<Post> findAndParentTreeSort(Integer threadId, Long limit, Long since, Boolean desc) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("thread", threadId);
        params.addValue("limit", limit);

        final String order = desc ? " DESC " : " ASC ";
        final String sign = desc ? " < " : " > ";

        final StringBuilder sql = new StringBuilder();
        sql.append("WITH sub_table AS (SELECT path FROM posts WHERE thread = :thread AND parent = 0 ");
        if (since != null) {
            sql.append("AND path").append(sign).append("(SELECT path FROM posts WHERE id = ").append(since).append(") ");
        }
        sql.append("ORDER BY id").append(order).append("LIMIT :limit) ");
        sql.append("SELECT * FROM posts p JOIN sub_table s ON (s.path <@ p.path) ");
        sql.append("ORDER BY p.path").append(order);

        return namedTemplate.query(sql.toString(), params, POST_ROW_MAPPER);
    }

    @Override
    public Optional<Post> findById(long id) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);

        final List<Post> posts = namedTemplate
                .query("SELECT * FROM posts WHERE id=:id", params, POST_ROW_MAPPER);

        if (posts.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(posts.get(0));
    }

    @Override
    public Optional<Post> findByIdInThread(long postId, int threadId) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", postId);
        params.addValue("thread", threadId);

        final List<Post> posts = namedTemplate
                .query("SELECT * FROM posts WHERE id=:id AND thread=:thread", params, POST_ROW_MAPPER);

        if (posts.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(posts.get(0));
    }

    @Override
    public void update(Post post) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", post.getId());
        params.addValue("message", post.getMessage());
        params.addValue("isEdited", post.isEdited());

        namedTemplate.update("UPDATE posts SET message=:message, isEdited=:isEdited WHERE id=:id", params);
    }
}
