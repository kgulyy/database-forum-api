package ru.mail.park.database.kgulyy.repositories;

import org.springframework.stereotype.Service;
import ru.mail.park.database.kgulyy.data.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@Service
public class UserRepository {
    private final Map<String, User> users = new HashMap<>();

    public void save(User user) {
        final String nickname = user.getNickname();
        users.put(nickname, user);
    }

    public void update(User user) {
        save(user);
    }

    public Optional<User> findByNickname(String nickname) {
        return Optional.ofNullable(users.get(nickname));
    }
}
