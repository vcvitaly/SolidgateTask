package io.github.vcvitaly.solidgate.task.model;

public record BalanceUpdateRequestUpdate(String idempotencyKey, String status, String error) {
}
