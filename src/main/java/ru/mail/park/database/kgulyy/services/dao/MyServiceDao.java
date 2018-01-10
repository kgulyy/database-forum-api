package ru.mail.park.database.kgulyy.services.dao;

import org.springframework.jdbc.core.JdbcTemplate;
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

    @Override
    public Status getStatus() {
        final Integer numberOfUsers = template.queryForObject("SELECT COUNT(*) FROM users", Integer.class);
        final Integer numberOfForums = template.queryForObject("SELECT COUNT(*) FROM forums", Integer.class);
        final Integer numberOfThreads = template.queryForObject("SELECT COUNT(*) FROM threads", Integer.class);
        final Long numberOfPosts = template.queryForObject("SELECT COUNT(*) FROM posts", Long.class);

        return new Status(numberOfUsers, numberOfForums, numberOfThreads, numberOfPosts);
    }

    @Override
    public void clear() {
        template.execute("TRUNCATE TABLE users CASCADE");
    }
}
