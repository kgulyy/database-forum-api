package ru.mail.park.database.kgulyy.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mail.park.database.kgulyy.domains.User;
import ru.mail.park.database.kgulyy.repositories.ForumRepository;
import ru.mail.park.database.kgulyy.repositories.UserRepository;
import ru.mail.park.database.kgulyy.services.exceptions.ForumNotFoundException;
import ru.mail.park.database.kgulyy.services.exceptions.UserNotFoundException;

import java.net.URI;
import java.util.List;

import static ru.mail.park.database.kgulyy.services.messages.MessageEnum.NEW_USER_PROFILE_CONFLICT;

/**
 * @author Konstantin Gulyy
 */
@Service
public class UserService {
    private UserRepository userRepository;
    private ForumRepository forumRepository;

    public UserService(UserRepository userRepository, ForumRepository forumRepository) {
        this.userRepository = userRepository;
        this.forumRepository = forumRepository;
    }

    public ResponseEntity<?> createUser(String nickname, User user) {
        final List<User> conflictUsers = userRepository.findByNicknameOrEmail(nickname, user.getEmail());
        if (!conflictUsers.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictUsers);
        }

        user.setNickname(nickname);
        userRepository.create(user);

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/api/user/{nickname}/profile")
                .buildAndExpand(nickname).toUri();

        return ResponseEntity.created(uri).body(user);
    }

    public ResponseEntity<User> getUserProfile(String nickname) {
        return userRepository
                .findByNickname(nickname)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> UserNotFoundException.throwEx(nickname));
    }

    public ResponseEntity<?> updateUserProfile(String nickname, User updatedUser) {
        final User user = userRepository.findByNickname(nickname)
                .orElseThrow(() -> UserNotFoundException.throwEx(nickname));

        final String updatedFullname = updatedUser.getFullname();
        final String updatedEmail = updatedUser.getEmail();
        final String updatedAbout = updatedUser.getAbout();

        if (updatedFullname == null && updatedEmail == null && updatedAbout == null) {
            return ResponseEntity.ok(user);
        }

        if (updatedFullname != null)
            user.setFullname(updatedFullname);
        if (updatedEmail != null)
            user.setEmail(updatedEmail);
        if (updatedAbout != null)
            user.setAbout(updatedAbout);

        if (userRepository.isExistOtherWithSameEmail(nickname, updatedEmail)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(NEW_USER_PROFILE_CONFLICT.getMessage());
        }

        userRepository.update(user);

        return ResponseEntity.ok(user);
    }

    public ResponseEntity<List<User>> getForumUsers(String forumSlug, Integer limit, String since, Boolean desc) {
        final Integer forumId = forumRepository.getIdBySlug(forumSlug)
                .orElseThrow(() -> ForumNotFoundException.throwEx(forumSlug));

        final List<User> users = userRepository.findForumUsers(forumId, limit, since, desc);

        return ResponseEntity.ok(users);
    }
}
