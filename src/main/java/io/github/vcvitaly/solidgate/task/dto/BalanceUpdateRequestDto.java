package io.github.vcvitaly.solidgate.task.dto;

import io.github.vcvitaly.solidgate.task.enumeration.BalanceUpdateRequestStatus;

public record BalanceUpdateRequestDto(
        String idempotencyKey,
        BalanceUpdateRequestStatus status,
        String error
) {}
