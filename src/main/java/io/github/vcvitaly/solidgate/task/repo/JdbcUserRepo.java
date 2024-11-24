package io.github.vcvitaly.solidgate.task.repo;

import io.github.vcvitaly.solidgate.task.model.User;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcUserRepo implements UserRepo {

    private static final String GET_EXISTING_USERS_QUERY = """
            SELECT id FROM users WHERE id = ANY(ARRAY[:ids])
            """;

    private static final String GET_USERS_QUERY = """
            SELECT * FROM users WHERE id = ANY(ARRAY[:ids])
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Set<Integer> getExistingUserIds(Set<Integer> ids) {
        final Map<String, Object> params = Map.of(
                "ids",
                ids.stream().mapToInt(Integer::intValue).toArray()
        );

        return new HashSet<>(jdbcTemplate.queryForList(GET_EXISTING_USERS_QUERY, params, Integer.class));
    }

    @Override
    public List<User> selectUsers(Set<Integer> ids) {
        final Map<String, Object> params = Map.of(
                "ids",
                ids.stream().mapToInt(Integer::intValue).toArray()
        );

        return jdbcTemplate.query(GET_USERS_QUERY, params, DataClassRowMapper.newInstance(User.class));
    }
}
