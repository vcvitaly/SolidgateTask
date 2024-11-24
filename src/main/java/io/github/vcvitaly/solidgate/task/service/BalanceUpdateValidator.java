package io.github.vcvitaly.solidgate.task.service;

import io.github.vcvitaly.solidgate.task.exception.IdempotencyKeyAlreadyExistsException;
import io.github.vcvitaly.solidgate.task.exception.IdempotencyKeyNotFoundException;
import io.github.vcvitaly.solidgate.task.exception.NegativeBalanceException;
import io.github.vcvitaly.solidgate.task.exception.UsersNotFoundException;
import io.github.vcvitaly.solidgate.task.repo.BalanceUpdateRepo;
import io.github.vcvitaly.solidgate.task.repo.UserRepo;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BalanceUpdateValidator {

    private final BalanceUpdateRepo balanceUpdateRepo;

    private final UserRepo userRepo;

    public void validateUpdateRequest(String idempotencyKey, Map<Integer, Integer> req) {
        validateIdempotencyKeyNotExists(idempotencyKey);

        final Set<Integer> existingUserIds = userRepo.getExistingUserIds(req.keySet());

        validateAllUsersExist(req, existingUserIds);

        validateBalancesArePositive(req);
    }

    public void validateIdempotencyKeyExists(String idempotencyKey) {
        if (!balanceUpdateRepo.existsRequest(idempotencyKey)) {
            throw new IdempotencyKeyNotFoundException(idempotencyKey);
        }
    }

    public void validateIdempotencyKeyNotExists(String idempotencyKey) {
        if (balanceUpdateRepo.existsRequest(idempotencyKey)) {
            throw new IdempotencyKeyAlreadyExistsException();
        }
    }

    private void validateAllUsersExist(Map<Integer, Integer> req, Set<Integer> existingUserIds) {
        if (req.size() != existingUserIds.size()) {
            throw new UsersNotFoundException();
        }
    }

    private void validateBalancesArePositive(Map<Integer, Integer> req) {
        req.values().stream()
                .filter(bal -> bal < 0)
                .findFirst()
                .ifPresent(negativeBal -> {
                    throw new NegativeBalanceException();
                });
    }
}
