package ru.mail.park.database.kgulyy.repositories;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.database.kgulyy.domains.Status;

/**
 * @author Konstantin Gulyy
 */
@Repository
@Transactional
public class StatusRepository {
    private final JdbcTemplate template;

    public StatusRepository(JdbcTemplate template) {
        this.template = template;
    }

    private static final RowMapper<Status> STATUS_ROW_MAPPER = (res, num) -> {
        int users = res.getInt("users");
        int forums = res.getInt("forums");
        int threads = res.getInt("threads");
        long posts = res.getLong("posts");

        return new Status(users, forums, threads, posts);
    };

    public Status getStatus() {
        final String sql = "SELECT COUNT(*) AS users, " +
                "(SELECT COUNT(*) FROM forums) AS forums, " +
                "(SELECT COUNT(*) FROM posts) AS posts, " +
                "(SELECT COUNT(*) FROM threads) AS threads " +
                "FROM users";

        return template.queryForObject(sql, STATUS_ROW_MAPPER);
    }

    public void clear() {
        template.execute("TRUNCATE TABLE users");
        template.execute("TRUNCATE TABLE forums");
        template.execute("TRUNCATE TABLE forum_users");
        template.execute("TRUNCATE TABLE threads");
        template.execute("TRUNCATE TABLE votes");
        template.execute("TRUNCATE TABLE posts");
    }
}
