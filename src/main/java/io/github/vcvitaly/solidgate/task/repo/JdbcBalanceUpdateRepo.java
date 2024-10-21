package io.github.vcvitaly.solidgate.task.repo;

import io.github.vcvitaly.solidgate.task.enumeration.BalanceUpdateRequestStatus;
import io.github.vcvitaly.solidgate.task.model.BalanceUpdateRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JdbcBalanceUpdateRepo implements BalanceUpdateRepo {

    private static final String CREATE_REQ_QUERY = """
            INSERT INTO balance_update_requests(idempotency_key, status, request)
            VALUES (:idempotency_key, :status, :request)
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public void createBalanceUpdateRequest(String idempotencyKey, String req) {
        Map<String, String> params = new HashMap<>();

        params.put("idempotency_key", idempotencyKey);
        params.put("status", BalanceUpdateRequestStatus.NEW.name());
        params.put("request", req);
    }

    @Override
    public Set<Integer> getExistingUserIds(Set<Integer> userIds) {
        return Set.of();
    }

    @Override
    public boolean existsRequest(String idempotencyKey) {
        return false;
    }

    @Override
    public BalanceUpdateRequest selectRequestForUpdate(String idempotencyKey) {
        return null;
    }

    @Override
    public void updateUserBalances(Map<Integer, Integer> req) {

    }
}
