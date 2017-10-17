package ru.mail.park.database.kgulyy.services.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.database.kgulyy.domains.Post;
import ru.mail.park.database.kgulyy.domains.Thread;
import ru.mail.park.database.kgulyy.services.PostService;
import ru.mail.park.database.kgulyy.services.ThreadService;

import java.util.List;

/**
 * @author Konstantin Gulyy
 */
@Service
@Transactional
public class PostDao implements PostService {
    private final NamedParameterJdbcTemplate namedTemplate;
    private ThreadService threadService;

    @Autowired
    public PostDao(NamedParameterJdbcTemplate namedTemplate, ThreadService threadService) {
        this.namedTemplate = namedTemplate;
        this.threadService = threadService;
    }

    @Override
    public List<Post> create(int id, List<Post> posts) {
        for (Post post : posts) {
            post.setThread(id);
            final Thread thread = threadService.findById(id).get();
            post.setForum(thread.getForum());
            post.setCreated(thread.getCreated());

            final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            final MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("parent", post.getParent());
            params.addValue("author", post.getAuthor());
            params.addValue("message", post.getMessage());
            params.addValue("isEdited", post.isEdited());
            params.addValue("forum", post.getForum());
            params.addValue("thread", post.getThread());
            namedTemplate.update("INSERT INTO posts(parent, author, message, isEdited, forum, thread)" +
                    " VALUES(:parent, :author, :message, :isEdited, :forum, :thread) RETURNING id", params, keyHolder);

            post.setId(keyHolder.getKey().intValue());
        }

        return posts;
    }

    @Override
    public List<Post> create(String slug, List<Post> posts) {
        for (Post post : posts) {
            final Thread thread = threadService.findBySlug(slug).get();
            post.setThread(thread.getId());
            post.setForum(thread.getForum());
            post.setCreated(thread.getCreated());

            final GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            final MapSqlParameterSource params = new MapSqlParameterSource();
            params.addValue("parent", post.getParent());
            params.addValue("author", post.getAuthor());
            params.addValue("message", post.getMessage());
            params.addValue("isEdited", post.isEdited());
            params.addValue("forum", post.getForum());
            params.addValue("thread", post.getThread());
            namedTemplate.update("INSERT INTO posts(parent, author, message, isEdited, forum, thread)" +
                    " VALUES(:parent, :author, :message, :isEdited, :forum, :thread) RETURNING id", params, keyHolder);

            post.setId(keyHolder.getKey().intValue());
        }

        return posts;
    }
}
