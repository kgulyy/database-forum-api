package ru.mail.park.database.kgulyy.repositories;

import org.springframework.stereotype.Service;
import ru.mail.park.database.kgulyy.data.User;

import java.util.*;

/**
 * @author Konstantin Gulyy
 */
@Service
public class UserRepository implements UserService {
    private final Map<String, User> users = new HashMap<>();

    @Override
    public void save(User user) {
        final String nickname = user.getNickname();
        users.put(nickname, user);
    }

    @Override
    public void update(User user) {
        save(user);
    }

    @Override
    public Optional<User> findByNickname(String nickname) {
        return Optional.ofNullable(users.get(nickname));
    }

    @Override
    public List<User> findByNicknameOrEmail(String nickname, String email) {
        final Optional<User> user = findByNickname(nickname);
        return user.map(Collections::singletonList).orElseGet(Collections::emptyList);
    }

    @Override
    public boolean isExistOtherWithSameEmail(String nickname, String email) {
        return false;
    }

}
