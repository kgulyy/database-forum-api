package ru.mail.park.database.kgulyy.repositories;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.mail.park.database.kgulyy.domains.Vote;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class VoteRepository {
    private final NamedParameterJdbcTemplate namedTemplate;

    public VoteRepository(NamedParameterJdbcTemplate namedTemplate) {
        this.namedTemplate = namedTemplate;
    }

    private static final RowMapper<Vote> VOTE_ROW_MAPPER = (res, num) -> {
        int id = res.getInt("id");
        short voice = res.getShort("vote_value");

        return new Vote(id, voice);
    };

    private static final RowMapper<Short> VOICE_ROW_MAPPER = (res, num) -> res.getShort("voice");

    public void create(int threadId, int userId, short voice) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("thread_id", threadId);
        params.addValue("user_id", userId);
        params.addValue("voice", voice);

        namedTemplate.update("INSERT INTO votes (thread_id, user_id, vote_value)" +
                " VALUES (:thread_id, :user_id, :voice)", params);
    }

    public Optional<Vote> getVoteByThreadAndUser(int threadId, int userId) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("thread_id", threadId);
        params.addValue("user_id", userId);

        final List<Vote> votes = namedTemplate.query("SELECT id, vote_value FROM votes" +
                " WHERE thread_id=:thread_id AND user_id=:user_id", params, VOTE_ROW_MAPPER);

        if (votes.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(votes.get(0));
    }

    public void update(int id, short voice) {
        final MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("voice", voice);

        namedTemplate.update("UPDATE votes SET vote_value = :voice WHERE id = :id", params);
    }
}
