package ru.mail.park.database.kgulyy.services.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import ru.mail.park.database.kgulyy.domains.Status;
import ru.mail.park.database.kgulyy.services.MyService;

/**
 * @author Konstantin Gulyy
 */
@Service
public class MyServiceDao implements MyService {
    private final JdbcTemplate template;

    public MyServiceDao(JdbcTemplate template) {
        this.template = template;
    }

    private static final RowMapper<Status> STATUS_ROW_MAPPER = (res, num) -> {
        int users = res.getInt("users");
        int forums = res.getInt("forums");
        int threads = res.getInt("threads");
        long posts = res.getLong("posts");

        return new Status(users, forums, threads, posts);
    };

    @Override
    public Status getStatus() {
        final String sql = "SELECT COUNT(*) as users, " +
                "(SELECT COUNT(*) FROM forums) as forums, " +
                "(SELECT COUNT(*) FROM posts) as posts, " +
                "(SELECT COUNT(*) FROM threads) as threads " +
                "FROM users";

        return template.queryForObject(sql, STATUS_ROW_MAPPER);
    }

    @Override
    public void clear() {
        template.execute("TRUNCATE TABLE users CASCADE");
    }
}
