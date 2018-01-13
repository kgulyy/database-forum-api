package ru.mail.park.database.kgulyy.services;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.mail.park.database.kgulyy.domains.Forum;
import ru.mail.park.database.kgulyy.domains.Thread;
import ru.mail.park.database.kgulyy.domains.User;
import ru.mail.park.database.kgulyy.domains.Vote;
import ru.mail.park.database.kgulyy.repositories.ForumRepository;
import ru.mail.park.database.kgulyy.repositories.ThreadRepository;
import ru.mail.park.database.kgulyy.repositories.UserRepository;
import ru.mail.park.database.kgulyy.repositories.VoteRepository;
import ru.mail.park.database.kgulyy.services.exceptions.ForumNotFoundException;
import ru.mail.park.database.kgulyy.services.exceptions.ThreadNotFoundException;
import ru.mail.park.database.kgulyy.services.exceptions.UserNotFoundException;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * @author Konstantin Gulyy
 */
@Service
public class ThreadService {
    private UserRepository userRepository;
    private ForumRepository forumRepository;
    private ThreadRepository threadRepository;
    private VoteRepository voteRepository;

    public ThreadService(UserRepository userRepository, ForumRepository forumRepository,
                         ThreadRepository threadRepository, VoteRepository voteRepository) {
        this.userRepository = userRepository;
        this.forumRepository = forumRepository;
        this.threadRepository = threadRepository;
        this.voteRepository = voteRepository;
    }

    public ResponseEntity<Thread> createThread(String forumSlug, Thread thread) {
        final String authorNickname = thread.getAuthor();
        final User foundUser = userRepository.findByNickname(authorNickname)
                .orElseThrow(() -> UserNotFoundException.throwEx(authorNickname));

        final Forum foundForum = forumRepository.findBySlug(forumSlug)
                .orElseThrow(() -> ForumNotFoundException.throwEx(forumSlug));

        thread.setAuthor(foundUser.getNickname());
        thread.setForum(foundForum.getSlug());

        final String threadSlug = thread.getSlug();
        if (threadSlug != null && !threadSlug.isEmpty()) {
            final Optional<Thread> conflictThread = threadRepository.findBySlug(threadSlug);
            if (conflictThread.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(conflictThread.get());
            }
        }

        final Thread createdThread = threadRepository.create(thread);

        final int forumId = foundForum.getId();
        final int userId = foundUser.getId();
        forumRepository.incrementThreadsCounter(forumId);
        forumRepository.addForumUser(forumId, userId);

        final URI uri = ServletUriComponentsBuilder
                .fromCurrentRequestUri()
                .replacePath("/api/thread/{slug_or_id}/details")
                .buildAndExpand(createdThread.getId()).toUri();

        return ResponseEntity.created(uri).body(createdThread);
    }

    public ResponseEntity<Thread> getThreadDetails(String slugOrId) {
        final Optional<Thread> foundThread;
        if (StringUtils.isNumeric(slugOrId)) {
            final int id = Integer.valueOf(slugOrId);
            foundThread = threadRepository.findById(id);
        } else {
            foundThread = threadRepository.findBySlug(slugOrId);
        }

        return foundThread
                .map(ResponseEntity::ok)
                .orElseThrow(() -> ThreadNotFoundException.throwEx(slugOrId));
    }

    public ResponseEntity<Thread> updateThread(String slugOrId, Thread updatedThread) {
        final Optional<Thread> threadOptional;
        if (StringUtils.isNumeric(slugOrId)) {
            final int id = Integer.valueOf(slugOrId);
            threadOptional = threadRepository.findById(id);
        } else {
            threadOptional = threadRepository.findBySlug(slugOrId);
        }
        final Thread thread = threadOptional.
                orElseThrow(() -> ThreadNotFoundException.throwEx(slugOrId));

        final String updatedTitle = updatedThread.getTitle();
        final String updatedMessage = updatedThread.getMessage();

        if (updatedTitle == null && updatedMessage == null) {
            return ResponseEntity.ok(thread);
        }

        if (updatedTitle != null)
            thread.setTitle(updatedTitle);
        if (updatedMessage != null)
            thread.setMessage(updatedMessage);

        threadRepository.update(thread);

        return ResponseEntity.ok(thread);
    }

    public ResponseEntity<List<Thread>> getListOfThreads(String forumSlug, Integer limit, String since, Boolean desc) {
        @SuppressWarnings("unused") final Forum foundForum = forumRepository.findBySlug(forumSlug)
                .orElseThrow(() -> ForumNotFoundException.throwEx(forumSlug));

        final List<Thread> threads = threadRepository.findForumThreads(forumSlug, limit, since, desc);

        return ResponseEntity.ok(threads);
    }

    public ResponseEntity<Thread> voteThread(String slugOrId, Vote vote) {
        final Optional<Thread> threadOptional;
        if (StringUtils.isNumeric(slugOrId)) {
            final int id = Integer.valueOf(slugOrId);
            threadOptional = threadRepository.findById(id);
        } else {
            threadOptional = threadRepository.findBySlug(slugOrId);
        }
        final Thread foundThread = threadOptional
                .orElseThrow(() -> ThreadNotFoundException.throwEx(slugOrId));

        final String voteAuthorNickname = vote.getNickname();
        final Integer userId = userRepository.getIdByNickname(voteAuthorNickname)
                .orElseThrow(() -> UserNotFoundException.throwEx(voteAuthorNickname));

        final int threadId = foundThread.getId();
        Optional<Vote> currentVote = voteRepository.getVoteByThreadAndUser(threadId, userId);
        short newVoice = vote.getVoice();
        if (currentVote.isPresent()) {
            short currentVoice = currentVote.get().getVoice();
            if (currentVoice != newVoice) {
                int voteId = currentVote.get().getId();
                voteRepository.update(voteId, newVoice);
                int updatedVotes = threadRepository.updateVotes(threadId, newVoice, true);
                foundThread.setVotes(updatedVotes);
            }
        } else {
            voteRepository.create(threadId, userId, newVoice);
            int updatedVotes = threadRepository.updateVotes(threadId, newVoice, false);
            foundThread.setVotes(updatedVotes);
        }

        return ResponseEntity.ok(foundThread);
    }
}
