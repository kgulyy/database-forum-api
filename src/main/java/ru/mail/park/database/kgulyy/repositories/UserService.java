package ru.mail.park.database.kgulyy.repositories;

import ru.mail.park.database.kgulyy.data.User;

import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
public interface UserService {
    void save(User user);

    void update(User user);

    Optional<User> findByNickname(String nickname);

    List<User> findByNicknameOrEmail(String nickname, String email);

    boolean isExistOtherWithSameEmail(String nickname, String email);
}
