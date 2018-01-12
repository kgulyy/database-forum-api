package ru.mail.park.database.kgulyy.repositories;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.database.kgulyy.domains.User;

import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@Repository
@Transactional
public class UserRepository {
    private final NamedParameterJdbcTemplate namedTemplate;

    public UserRepository(NamedParameterJdbcTemplate namedTemplate) {
        this.namedTemplate = namedTemplate;
    }

    private static final RowMapper<User> USER_ROW_MAPPER = (res, num) -> {
        int id = res.getInt("id");
        String nickname = res.getString("nickname");
        String fullname = res.getString("fullname");
        String email = res.getString("email");
        String about = res.getString("about");
        if (res.wasNull()) {
            about = null;
        }
        return new User(id, nickname, fullname, email, about);
    };

    private static final RowMapper<Integer> USER_ID_MAPPER = (res, num) -> res.getInt("id");

    public void create(User user) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", user.getNickname());
        params.addValue("fullname", user.getFullname());
        params.addValue("email", user.getEmail());
        params.addValue("about", user.getAbout());

        namedTemplate.update("INSERT INTO users(nickname, fullname, email, about)" +
                " VALUES(:nickname, :fullname, :email, :about)", params);
    }

    public void update(User user) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", user.getNickname());
        params.addValue("fullname", user.getFullname());
        params.addValue("email", user.getEmail());
        params.addValue("about", user.getAbout());

        namedTemplate.update("UPDATE users SET fullname=:fullname, email=:email, about=:about " +
                "WHERE nickname = :nickname::CITEXT", params);
    }

    public Optional<User> findByNickname(String nickname) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", nickname);
        final List<User> users = namedTemplate.query("SELECT * FROM users WHERE nickname = :nickname::CITEXT",
                params, USER_ROW_MAPPER);

        if (users.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(0));
    }

    public List<User> findByNicknameOrEmail(String nickname, String email) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", nickname);
        params.addValue("email", email);

        return namedTemplate.query("SELECT * FROM users " +
                "WHERE nickname = :nickname::CITEXT OR email = :email::CITEXT", params, USER_ROW_MAPPER);
    }

    public boolean isExistOtherWithSameEmail(String nickname, String email) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", nickname);
        params.addValue("email", email);

        final List<User> users = namedTemplate.query("SELECT * FROM users " +
                "WHERE nickname <> :nickname::CITEXT AND email = :email::CITEXT", params, USER_ROW_MAPPER);

        return !users.isEmpty();
    }

    public Optional<Integer> getIdByNickname(String nickname) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", nickname);

        final List<Integer> ids = namedTemplate.query(
                "SELECT id FROM users WHERE nickname = :nickname::CITEXT", params, USER_ID_MAPPER);

        if (ids.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(ids.get(0));
    }

    public List<User> findForumUsers(int forumId, Integer limit, String since, Boolean desc) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("forum_id", forumId);
        params.addValue("limit", limit);
        if (since != null) {
            params.addValue("since", since);
        }

        final String order = desc ? " DESC " : " ASC ";
        final String sign = desc ? " < " : " > ";

        final StringBuilder sql = new StringBuilder();
        sql.append("SELECT u.id, u.nickname, u.fullname, u.email, u.about FROM users u ");
        sql.append("JOIN forum_users ON user_id = id ");
        sql.append("WHERE forum_id = :forum_id ");
        if (since != null) {
            sql.append("AND nickname").append(sign).append(":since::CITEXT ");
        }
        sql.append("ORDER BY u.nickname ").append(order);
        sql.append("LIMIT :limit");

        return namedTemplate.query(sql.toString(), params, USER_ROW_MAPPER);
    }
}
