package io.github.vcvitaly.solidgate.task.repo;

import io.github.vcvitaly.solidgate.task.enumeration.BalanceUpdateRequestStatus;
import io.github.vcvitaly.solidgate.task.model.BalanceUpdate;
import io.github.vcvitaly.solidgate.task.model.BalanceUpdateRequest;
import java.util.HashMap;
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
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcBalanceUpdateRepo implements BalanceUpdateRepo {

    private static final String CREATE_REQ_QUERY = """
            INSERT INTO balance_update_requests(idempotency_key, status, request)
            VALUES (:idempotency_key, :status, :request)
            """;

    private static final String GET_REQ_QUERY = """
            SELECT * FROM balance_update_requests WHERE idempotency_key = :idempotencyKey
            """;

    private static final String GET_REQ_FOR_UPDATE_QUERY = """
            SELECT * FROM balance_update_requests WHERE idempotency_key = :idempotencyKey
            FOR UPDATE SKIP LOCKED
            """;

    private static final String GET_IN_PROGRESS_REQS_QUERY = """
            SELECT * FROM balance_update_requests WHERE status IN (:statuses) ORDER BY id
            """;

    private static final String UPDATE_BALANCE_QUERY = """
            UPDATE users SET balance = :balance WHERE id = :id
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void createBalanceUpdateRequest(String idempotencyKey, String req) {
        final Map<String, Object> params = new HashMap<>();

        params.put("idempotency_key", UUID.fromString(idempotencyKey));
        params.put("status", BalanceUpdateRequestStatus.IN_PROGRESS.name());
        params.put("request", req);

        jdbcTemplate.update(CREATE_REQ_QUERY, params);
    }

    @Override
    public boolean existsRequest(String idempotencyKey) {
        return false;
    }

    @Override
    public Optional<BalanceUpdateRequest> selectRequest(String idempotencyKey) {
        return getBalanceUpdateRequest(GET_REQ_QUERY, idempotencyKey);
    }

    @Override
    public Optional<BalanceUpdateRequest> selectRequestForUpdate(String idempotencyKey) {
        return getBalanceUpdateRequest(GET_REQ_FOR_UPDATE_QUERY, idempotencyKey);
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

        jdbcTemplate.batchUpdate(
                UPDATE_BALANCE_QUERY,
                SqlParameterSourceUtils.createBatch(
                        req.entrySet().stream()
                                .map(e -> new BalanceUpdate(e.getKey(), e.getValue()))
                                .toList()
                )
        );
    }

    private Optional<BalanceUpdateRequest> getBalanceUpdateRequest(String query, String idempotencyKey) {
        final Map<String, Object> params = Map.of("idempotencyKey", UUID.fromString(idempotencyKey));

        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(query, params, DataClassRowMapper.newInstance(BalanceUpdateRequest.class))
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
