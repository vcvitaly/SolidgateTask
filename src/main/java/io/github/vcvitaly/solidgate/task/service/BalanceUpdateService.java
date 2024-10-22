package io.github.vcvitaly.solidgate.task.service;

import io.github.vcvitaly.solidgate.task.dto.BalanceUpdateRequestDto;
import io.github.vcvitaly.solidgate.task.enumeration.BalanceUpdateRequestStatus;
import io.github.vcvitaly.solidgate.task.exception.EntityNotFoundException;
import io.github.vcvitaly.solidgate.task.model.BalanceUpdateRequest;
import io.github.vcvitaly.solidgate.task.repo.BalanceUpdateRepo;
import io.github.vcvitaly.solidgate.task.util.JsonUtil;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BalanceUpdateService {

    private final BalanceUpdateRepo repo;

    public void createUpdateRequest(String idempotencyKey, Map<Integer, Integer> req) {
        repo.createBalanceUpdateRequest(idempotencyKey, JsonUtil.objToString(req));
        log.info("Created update request for idempotency key: " + idempotencyKey);
    }

    public BalanceUpdateRequestDto getUpdateRequest(String idempotencyKey) {
        final BalanceUpdateRequest req = repo.selectRequest(idempotencyKey)
                .orElseThrow(() -> new EntityNotFoundException(idempotencyKey + "not found"));
        return toBalanceUpdReqDto(req);
    }

    public List<BalanceUpdateRequestDto> getInProgressRequests() {
        return repo.selectAllRequestsByStatuses(Set.of(BalanceUpdateRequestStatus.IN_PROGRESS)).stream()
                .map(this::toBalanceUpdReqDto)
                .toList();
    }

    @Transactional
    public void processRequest() {
        final Optional<BalanceUpdateRequest> req = repo.selectAllRequestsByStatuses(Set.of(BalanceUpdateRequestStatus.IN_PROGRESS)).stream()
                .findFirst();

        req.ifPresent(this::processRequest);
    }

    private BalanceUpdateRequestDto toBalanceUpdReqDto(BalanceUpdateRequest req) {
        return BalanceUpdateRequestDto.builder()
                .idempotencyKey(req.idempotencyKey().toString())
                .status(req.status())
                .error(req.error())
                .build();
    }

    private void processRequest(BalanceUpdateRequest req) {
        repo.selectRequestForUpdate(req.idempotencyKey().toString());

        final Map<Integer, Integer> map = JsonUtil.strToMap(req.request(), Integer.class, Integer.class);

        repo.updateUserBalances(map);

        log.info("Updated user balances for idempotency key: " + req.idempotencyKey());
    }
}
