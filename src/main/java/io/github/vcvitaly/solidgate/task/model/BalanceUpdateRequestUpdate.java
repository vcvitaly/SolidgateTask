package io.github.vcvitaly.solidgate.task.model;

import java.util.UUID;

public record BalanceUpdateRequestUpdate(UUID idempotencyKey, String status, String error) {
}
