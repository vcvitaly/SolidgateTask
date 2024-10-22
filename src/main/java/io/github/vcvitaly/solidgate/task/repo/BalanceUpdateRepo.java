package io.github.vcvitaly.solidgate.task.repo;

import io.github.vcvitaly.solidgate.task.enumeration.BalanceUpdateRequestStatus;
import io.github.vcvitaly.solidgate.task.model.BalanceUpdateRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface BalanceUpdateRepo {

    void createBalanceUpdateRequest(String idempotencyKey, String req);

    boolean existsRequest(String idempotencyKey);

    Optional<BalanceUpdateRequest> selectRequest(String idempotencyKey);

    Optional<BalanceUpdateRequest> selectRequestForUpdate(String idempotencyKey);

    List<BalanceUpdateRequest> selectAllRequestsByStatuses(Set<BalanceUpdateRequestStatus> statuses);

    void updateUserBalances(Map<Integer, Integer> req);
}
