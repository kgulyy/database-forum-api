package ru.mail.park.database.kgulyy.services;

import ru.mail.park.database.kgulyy.domains.Post;

import java.util.List;

/**
 * @author Konstantin Gulyy
 */
public interface PostService {
    List<Post> create(int id, List<Post> posts);

    List<Post> create(String slug, List<Post> posts);
}
