package io.github.vcvitaly.solidgate.task.model;

import io.github.vcvitaly.solidgate.task.enumeration.BalanceUpdateRequestStatus;
import java.util.UUID;

public record BalanceUpdateRequest(
        Integer id,
        UUID idempotencyKey,
        BalanceUpdateRequestStatus status,
        String error,
        String request
) {}
