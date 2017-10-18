package ru.mail.park.database.kgulyy.services;

import ru.mail.park.database.kgulyy.domains.Post;
import ru.mail.park.database.kgulyy.domains.Thread;

import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
public interface PostService {
    List<Post> create(Thread thread, List<Post> posts);

    List<Post> findAndFlatSort(Integer threadId, Long limit, Long since, Boolean desc);

    List<Post> findAndTreeSort(Integer threadId, Long limit, Long since, Boolean desc);

    List<Post> findAndParentTreeSort(Integer threadId, Long limit, Long since, Boolean desc);

    Optional<Post> findById(long id);

    void update(Post post);
}
