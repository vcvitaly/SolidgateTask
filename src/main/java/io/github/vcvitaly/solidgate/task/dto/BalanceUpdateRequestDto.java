package io.github.vcvitaly.solidgate.task.dto;

import io.github.vcvitaly.solidgate.task.enumeration.BalanceUpdateRequestStatus;
import lombok.Builder;

@Builder
public record BalanceUpdateRequestDto(
        String idempotencyKey,
        BalanceUpdateRequestStatus status,
        String error
) {}
