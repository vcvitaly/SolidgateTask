package io.github.vcvitaly.solidgate.task.repo;

import io.github.vcvitaly.solidgate.task.enumeration.BalanceUpdateRequestStatus;
import io.github.vcvitaly.solidgate.task.model.BalanceUpdateRequest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcBalanceUpdateRepo implements BalanceUpdateRepo {

    private static final String CREATE_REQ_QUERY = """
            INSERT INTO balance_update_requests(idempotency_key, status, request)
            VALUES (:idempotency_key, :status, :request)
            """;

    private static final String GET_EXISTING_USERS_QUERY = """
            SELECT id FROM users WHERE id = ANY(ARRAY[:ids])
            """;

    private static final String GET_REQ_QUERY = """
            SELECT * FROM balance_update_requests WHERE idempotency_key = :idempotencyKey
            """;

    private static final String GET_IN_PROGRESS_REQS_QUERY = """
            SELECT * FROM balance_update_requests WHERE status in (:statuses)
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void createBalanceUpdateRequest(String idempotencyKey, String req) {
        final Map<String, Object> params = new HashMap<>();

        params.put("idempotency_key", UUID.fromString(idempotencyKey));
        params.put("status", BalanceUpdateRequestStatus.NEW.name());
        params.put("request", req);

        jdbcTemplate.update(CREATE_REQ_QUERY, params);
    }

    @Override
    public Set<Integer> getExistingUserIds(Set<Integer> userIds) {
        final Map<String, Object> params = Map.of(
                "ids",
                userIds.stream().mapToInt(Integer::intValue).toArray()
        );

        return new HashSet<>(jdbcTemplate.queryForList(GET_EXISTING_USERS_QUERY, params, Integer.class));
    }

    @Override
    public boolean existsRequest(String idempotencyKey) {
        return false;
    }

    @Override
    public Optional<BalanceUpdateRequest> selectRequest(String idempotencyKey) {
        final Map<String, Object> params = Map.of("idempotencyKey", idempotencyKey);

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(GET_REQ_QUERY, params, DataClassRowMapper.newInstance(BalanceUpdateRequest.class))
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public BalanceUpdateRequest selectRequestForUpdate(String idempotencyKey) {
        return null;
    }

    @Override
    public List<BalanceUpdateRequest> selectAllRequestsByStatuses(Set<BalanceUpdateRequestStatus> statuses) {
        final Map<String, Object> params = Map.of(
                "statuses",
                statuses.stream().map(BalanceUpdateRequestStatus::name).collect(Collectors.toSet())
        );

        return jdbcTemplate.query(GET_IN_PROGRESS_REQS_QUERY, params, DataClassRowMapper.newInstance(BalanceUpdateRequest.class));
    }

    @Override
    public void updateUserBalances(Map<Integer, Integer> req) {

    }
}
