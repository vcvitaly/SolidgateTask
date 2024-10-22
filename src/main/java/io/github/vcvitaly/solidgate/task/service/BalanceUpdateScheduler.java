package io.github.vcvitaly.solidgate.task.service;

import io.github.vcvitaly.solidgate.task.enumeration.BalanceUpdateRequestStatus;
import io.github.vcvitaly.solidgate.task.exception.BalanceUpdateException;
import io.github.vcvitaly.solidgate.task.exception.RequestLockException;
import io.github.vcvitaly.solidgate.task.model.BalanceUpdateRequestUpdate;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class BalanceUpdateScheduler {

    private final BalanceUpdateService service;

    @Scheduled(fixedDelay = 5000)
    public void processRequest() {
        BalanceUpdateRequestUpdate update = null;
        try {
            final Optional<UUID> processed = service.processRequest();
            if (processed.isEmpty()) {
                return;
            }
            final UUID uuid = processed.get();
            log.info("Updated user balances for idempotency key: " + uuid);
            update = new BalanceUpdateRequestUpdate(
                    uuid, BalanceUpdateRequestStatus.COMPLETED.name(), null
            );
        } catch (RequestLockException e) {
            log.error("Could not process request", e);
            return;
        } catch (BalanceUpdateException e) {
            log.error("Error updating user balances", e);
            update = new BalanceUpdateRequestUpdate(
                    e.getIdempotencyKey(), BalanceUpdateRequestStatus.FAILED.name(), e.getMessage()
            );
        }

        try {
            service.updateRequest(update);
        } catch (Exception e) {
            log.error("Error updating request", e);
        }
    }
}
