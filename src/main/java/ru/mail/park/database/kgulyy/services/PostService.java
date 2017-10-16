package ru.mail.park.database.kgulyy.services;

import ru.mail.park.database.kgulyy.domains.Post;

import java.util.List;

/**
 * @author Konstantin Gulyy
 */
public interface PostService {
    List<Post> save(int id, List<Post> posts);
    List<Post> save(String slug, List<Post> posts);
}
