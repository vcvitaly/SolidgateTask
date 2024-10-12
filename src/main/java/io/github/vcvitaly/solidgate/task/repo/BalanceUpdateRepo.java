package io.github.vcvitaly.solidgate.task.repo;

import io.github.vcvitaly.solidgate.task.model.BalanceUpdateRequest;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceUpdateRepo {

    void createBalanceUpdateRequest(String idempotencyKey, Map<Integer, Integer> req);

    Set<Integer> getExistingUserIds(Set<Integer> userIds);

    boolean existsRequest(String idempotencyKey);

    BalanceUpdateRequest selectRequestForUpdate(String idempotencyKey);

    void updateUserBalances(Map<Integer, Integer> req);
}
