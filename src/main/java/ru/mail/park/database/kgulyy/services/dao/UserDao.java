package ru.mail.park.database.kgulyy.services.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.database.kgulyy.domains.User;
import ru.mail.park.database.kgulyy.services.UserService;

import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@Service
@Transactional
public class UserDao implements UserService {
    private final NamedParameterJdbcTemplate namedTemplate;

    public UserDao(NamedParameterJdbcTemplate namedTemplate) {
        this.namedTemplate = namedTemplate;
    }

    private static final RowMapper<User> USER_ROW_MAPPER = (res, num) -> {
        String nickname = res.getString("nickname");
        String fullname = res.getString("fullname");
        String email = res.getString("email");
        String about = res.getString("about");
        if (res.wasNull()) {
            about = null;
        }
        return new User(nickname, fullname, email, about);
    };

    @Override
    public void create(User user) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", user.getNickname());
        params.addValue("fullname", user.getFullname());
        params.addValue("email", user.getEmail());
        params.addValue("about", user.getAbout());

        namedTemplate.update("INSERT INTO users(nickname, fullname, email, about)" +
                " VALUES(:nickname, :fullname, :email, :about)", params);
    }

    @Override
    public void update(User user) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", user.getNickname());
        params.addValue("fullname", user.getFullname());
        params.addValue("email", user.getEmail());
        params.addValue("about", user.getAbout());

        namedTemplate.update("UPDATE users SET fullname=:fullname, email=:email, about=:about" +
                " WHERE LOWER(nickname)=LOWER(:nickname)", params);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", nickname);
        final List<User> users = namedTemplate.query("SELECT * FROM users " +
                "WHERE LOWER(nickname)=LOWER(:nickname)", params, USER_ROW_MAPPER);

        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(0));
    }

    @Override
    public List<User> findByNicknameOrEmail(String nickname, String email) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", nickname);
        params.addValue("email", email);

        return namedTemplate.query("SELECT * FROM users" +
                " WHERE LOWER(nickname)=LOWER(:nickname) OR LOWER(email)=LOWER(:email)", params, USER_ROW_MAPPER);
    }

    @Override
    public boolean isExistOtherWithSameEmail(String nickname, String email) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", nickname);
        params.addValue("email", email);

        final List<User> users = namedTemplate.query("SELECT * FROM users" +
                " WHERE LOWER(nickname)<>LOWER(:nickname) AND LOWER(email)=LOWER(:email)", params, USER_ROW_MAPPER);

        return !users.isEmpty();
    }

    @Override
    public List<User> findForumUsers(String forumSlug, Integer limit, String since, Boolean desc) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("forum", forumSlug);
        params.addValue("limit", limit);
        if (since != null) {
            params.addValue("since", since);
        }

        final String order = desc ? " DESC " : " ASC ";
        final String sign = desc ? " < " : " > ";

        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM (");
        sql.append("(SELECT DISTINCT u.nickname, u.fullname, u.email, u.about ");
        sql.append("FROM users u, threads t ");
        sql.append("WHERE u.nickname = t.author ");
        sql.append("AND LOWER(t.forum) = LOWER(:forum)) ");
        sql.append("UNION ");
        sql.append("(SELECT DISTINCT u.nickname, u.fullname, u.email, u.about ");
        sql.append("FROM users u, posts p ");
        sql.append("WHERE u.nickname = p.author ");
        sql.append("AND LOWER(p.forum) = LOWER(:forum)) ");
        sql.append(") AS u ");
        if (since != null) {
            sql.append("WHERE LOWER(nickname)").append(sign).append("LOWER(:since) ");
        }
        sql.append("ORDER BY 1").append(order);
        sql.append("LIMIT :limit");

        return namedTemplate.query(sql.toString(), params, USER_ROW_MAPPER);
    }
}
