package ru.mail.park.database.kgulyy.repositories;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.database.kgulyy.domains.Forum;

import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@Repository
@Transactional
public class ForumRepository {
    private final NamedParameterJdbcTemplate namedTemplate;

    public ForumRepository(NamedParameterJdbcTemplate namedTemplate) {
        this.namedTemplate = namedTemplate;
    }

    private static final RowMapper<Forum> FORUM_ROW_MAPPER = (res, num) -> {
        int id = res.getInt("id");
        String slug = res.getString("slug");
        String title = res.getString("title");
        int authorId = res.getInt("author_id");
        String authorNickname = res.getString("author_nickname");
        long posts = res.getLong("posts");
        int threads = res.getInt("threads");

        return new Forum(id, slug, title, authorId, authorNickname, posts, threads);
    };

    public void create(Forum forum) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("slug", forum.getSlug());
        params.addValue("title", forum.getTitle());
        params.addValue("author_id", forum.getAuthorId());
        params.addValue("author_nickname", forum.getAuthor());

        namedTemplate.update("INSERT INTO forums(slug, title, author_id, author_nickname)" +
                " VALUES(:slug, :title, :author_id, :author_nickname)", params);
    }

    public Optional<Forum> findBySlug(String slug) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("slug", slug);
        final List<Forum> forums = namedTemplate.query(
                "SELECT * FROM forums WHERE slug = :slug::CITEXT", params, FORUM_ROW_MAPPER);

        if (forums.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(forums.get(0));
    }
}
