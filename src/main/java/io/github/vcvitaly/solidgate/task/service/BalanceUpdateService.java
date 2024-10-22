package io.github.vcvitaly.solidgate.task.service;

import io.github.vcvitaly.solidgate.task.dto.BalanceUpdateRequestDto;
import io.github.vcvitaly.solidgate.task.enumeration.BalanceUpdateRequestStatus;
import io.github.vcvitaly.solidgate.task.exception.BalanceUpdateException;
import io.github.vcvitaly.solidgate.task.exception.EntityNotFoundException;
import io.github.vcvitaly.solidgate.task.exception.RequestLockException;
import io.github.vcvitaly.solidgate.task.model.BalanceUpdateRequest;
import io.github.vcvitaly.solidgate.task.model.BalanceUpdateRequestUpdate;
import io.github.vcvitaly.solidgate.task.repo.BalanceUpdateRepo;
import io.github.vcvitaly.solidgate.task.util.JsonUtil;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BalanceUpdateService {

    private final BalanceUpdateRepo balanceUpdateRepo;

    @Transactional
    public void createUpdateRequest(String idempotencyKey, Map<Integer, Integer> req) {
        balanceUpdateRepo.createBalanceUpdateRequest(idempotencyKey, JsonUtil.objToString(req));
        log.info("Created update request for idempotency key: " + idempotencyKey);
    }

    public BalanceUpdateRequestDto getUpdateRequest(String idempotencyKey) {
        final BalanceUpdateRequest req = balanceUpdateRepo.selectRequest(idempotencyKey)
                .orElseThrow(() -> new EntityNotFoundException(idempotencyKey + "not found"));
        return toBalanceUpdReqDto(req);
    }

    public List<BalanceUpdateRequestDto> getInProgressRequests() {
        return balanceUpdateRepo.selectAllRequestsByStatuses(Set.of(BalanceUpdateRequestStatus.IN_PROGRESS)).stream()
                .map(this::toBalanceUpdReqDto)
                .toList();
    }

    @Transactional
    public Optional<UUID> processRequest() {
        Optional<BalanceUpdateRequest> reqForUpdate;

        try {
            reqForUpdate = balanceUpdateRepo.selectRequestForUpdate(Set.of(BalanceUpdateRequestStatus.IN_PROGRESS));
        } catch (Exception e) {
            throw new RequestLockException("Could not lock a request for update", e);
        }

        try {
            reqForUpdate.ifPresent(this::processRequest);
            return reqForUpdate.map(BalanceUpdateRequest::idempotencyKey);
        } catch (Exception e) {
            throw new BalanceUpdateException(reqForUpdate.get().idempotencyKey(), e);
        }
    }

    @Transactional
    public void updateRequest(BalanceUpdateRequestUpdate update) {
        balanceUpdateRepo.updateBalanceUpdateRequest(update);
    }

    private BalanceUpdateRequestDto toBalanceUpdReqDto(BalanceUpdateRequest req) {
        return BalanceUpdateRequestDto.builder()
                .idempotencyKey(req.idempotencyKey().toString())
                .status(req.status())
                .error(req.error())
                .build();
    }

    private void processRequest(BalanceUpdateRequest req) {
        log.info("Processing balance update request");
        final Map<Integer, Integer> map = JsonUtil.strToMap(req.request(), Integer.class, Integer.class);
        balanceUpdateRepo.updateUserBalances(map);
    }
}
