package io.github.vcvitaly.solidgate.task.service;

import com.google.common.collect.Sets;
import io.github.vcvitaly.solidgate.task.exception.IdempotencyKeyNotFoundException;
import io.github.vcvitaly.solidgate.task.exception.NegativeBalanceException;
import io.github.vcvitaly.solidgate.task.exception.UsersNotFoundException;
import io.github.vcvitaly.solidgate.task.repo.BalanceUpdateRepo;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BalanceUpdateValidator {

    private final BalanceUpdateRepo repo;

    public void validateUpdateRequest(String idempotencyKey, Map<Integer, Integer> req) {
        validateIdempotencyKeyExists(idempotencyKey);

        final Set<Integer> existingUserIds = repo.getExistingUserIds(req.keySet());

        final Sets.SetView<Integer> difference = Sets.difference(req.keySet(), existingUserIds);

        if (!difference.isEmpty()) {
            throw new UsersNotFoundException();
        }

        req.values().stream()
                .filter(bal -> bal < 0)
                .findFirst()
                .ifPresent(negativeBal -> {
                    throw new NegativeBalanceException();
                });
    }

    public void validateIdempotencyKeyExists(String idempotencyKey) {
        if (!repo.existsRequest(idempotencyKey)) {
            throw new IdempotencyKeyNotFoundException(idempotencyKey);
        }
    }
}
