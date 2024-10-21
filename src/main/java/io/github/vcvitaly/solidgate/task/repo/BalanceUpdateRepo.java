package io.github.vcvitaly.solidgate.task.repo;

import io.github.vcvitaly.solidgate.task.model.BalanceUpdateRequest;
import java.util.Map;
import java.util.Set;

public interface BalanceUpdateRepo {

    void createBalanceUpdateRequest(String idempotencyKey, String req);

    Set<Integer> getExistingUserIds(Set<Integer> userIds);

    boolean existsRequest(String idempotencyKey);

    BalanceUpdateRequest selectRequestForUpdate(String idempotencyKey);

    void updateUserBalances(Map<Integer, Integer> req);
}
