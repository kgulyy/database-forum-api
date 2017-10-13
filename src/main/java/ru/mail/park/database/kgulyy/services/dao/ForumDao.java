package ru.mail.park.database.kgulyy.services.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import ru.mail.park.database.kgulyy.domains.Forum;
import ru.mail.park.database.kgulyy.services.ForumService;

import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@Service
public class ForumDao implements ForumService {
    private final NamedParameterJdbcTemplate namedTemplate;

    public ForumDao(NamedParameterJdbcTemplate namedTemplate) {
        this.namedTemplate = namedTemplate;
    }

    private static final RowMapper<Forum> FORUM_ROW_MAPPER = (res, num) -> {
        String slug = res.getString("slug");
        String title = res.getString("title");
        String user = res.getString("author");
        Long posts = res.getLong("posts");
        Integer threads = res.getInt("threads");

        return new Forum(slug, title, user, posts, threads);
    };

    @Override
    public void save(Forum forum) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("slug", forum.getSlug());
        params.addValue("title", forum.getTitle());
        params.addValue("author", forum.getAuthor());
        params.addValue("posts", forum.getPosts());
        params.addValue("threads", forum.getThreads());

        namedTemplate.update("INSERT INTO forums(slug, title, author, posts, threads)" +
                " VALUES(:slug, :title, :author, :posts, :threads)", params);
    }

    @Override
    public Optional<Forum> findBySlug(String slug) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("slug", slug);
        final List<Forum> forums = namedTemplate.query("SELECT * FROM forums" +
                " WHERE LOWER(slug)=LOWER(:slug)", params, FORUM_ROW_MAPPER);

        if (forums.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(forums.get(0));
    }
}
