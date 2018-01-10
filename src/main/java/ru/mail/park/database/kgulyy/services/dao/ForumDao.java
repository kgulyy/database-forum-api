package ru.mail.park.database.kgulyy.services.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.database.kgulyy.domains.Forum;
import ru.mail.park.database.kgulyy.services.ForumService;

import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@Service
@Transactional
public class ForumDao implements ForumService {
    private final NamedParameterJdbcTemplate namedTemplate;

    public ForumDao(NamedParameterJdbcTemplate namedTemplate) {
        this.namedTemplate = namedTemplate;
    }

    private static final RowMapper<Forum> FORUM_ROW_MAPPER = (res, num) -> {
        String slug = res.getString("slug");
        String title = res.getString("title");
        int authorId = res.getInt("author_id");
        String authorNickname = res.getString("author_nickname");
        Long posts = res.getLong("posts");
        Integer threads = res.getInt("threads");

        return new Forum(slug, title, authorId, authorNickname, posts, threads);
    };

    @Override
    public void create(Forum forum) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("slug", forum.getSlug());
        params.addValue("title", forum.getTitle());
        params.addValue("author_id", forum.getAuthorId());
        params.addValue("author_nickname", forum.getAuthor());
        params.addValue("posts", forum.getPosts());
        params.addValue("threads", forum.getThreads());

        namedTemplate.update("INSERT INTO forums(slug, title, author_id, author_nickname, posts, threads)" +
                " VALUES(:slug, :title, :author_id, :author_nickname, :posts, :threads)", params);
    }

    @Override
    public Optional<Forum> findBySlug(String slug) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("slug", slug);
        final List<Forum> forums = namedTemplate.query(
                "SELECT * FROM forums WHERE slug = :slug::citext", params, FORUM_ROW_MAPPER);

        if (forums.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(forums.get(0));
    }
}
