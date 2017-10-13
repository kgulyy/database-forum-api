package ru.mail.park.database.kgulyy.repositories.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import ru.mail.park.database.kgulyy.data.User;
import ru.mail.park.database.kgulyy.repositories.UserService;

import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@Service
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
    public void save(User user) {
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
                " WHERE nickname=:nickname", params);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", nickname);
        final List<User> users = namedTemplate.query("SELECT * FROM users " +
                        "WHERE nickname=:nickname", params, USER_ROW_MAPPER);

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
                        " WHERE nickname=:nickname OR email=:email", params, USER_ROW_MAPPER);
    }

    @Override
    public boolean isExistOtherWithSameEmail(String nickname, String email) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", nickname);
        params.addValue("email", email);

        final List<User> users = namedTemplate.query("SELECT * FROM users" +
                        " WHERE nickname<>:nickname AND email=:email", params, USER_ROW_MAPPER);

        return !users.isEmpty();
    }
}
