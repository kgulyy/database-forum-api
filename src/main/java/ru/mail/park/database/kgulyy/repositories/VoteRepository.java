package ru.mail.park.database.kgulyy.repositories;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.mail.park.database.kgulyy.domains.Thread;
import ru.mail.park.database.kgulyy.domains.Vote;

import java.util.List;

@Repository
public class VoteRepository {
    private final NamedParameterJdbcTemplate namedTemplate;

    public VoteRepository(NamedParameterJdbcTemplate namedTemplate) {
        this.namedTemplate = namedTemplate;
    }

    private static final RowMapper<Short> VOICE_ROW_MAPPER = (res, num) -> res.getShort("voice");

    public Thread vote(Thread thread, Vote vote) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("nickname", vote.getNickname());
        params.addValue("threadId", thread.getId());
        params.addValue("voice", vote.getVoice());

        final List<Short> currentVoice = namedTemplate.query("SELECT voice FROM votes" +
                " WHERE nickname=:nickname AND thread_id=:threadId", params, VOICE_ROW_MAPPER);

        namedTemplate.update("INSERT INTO votes (nickname, thread_id, voice)" +
                " VALUES (:nickname, :threadId, :voice)" +
                " ON CONFLICT (nickname, thread_id) DO UPDATE SET voice=:voice", params);

        int votes = thread.getVotes();
        votes += vote.getVoice();
        if (!currentVoice.isEmpty()) {
            votes -= currentVoice.get(0);
        }

        params.addValue("votes", votes);
        namedTemplate.update("UPDATE threads SET votes=:votes" +
                " WHERE id=:threadId", params);

        thread.setVotes(votes);
        return thread;
    }
}
