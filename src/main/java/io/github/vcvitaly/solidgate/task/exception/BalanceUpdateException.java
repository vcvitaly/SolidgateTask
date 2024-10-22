package io.github.vcvitaly.solidgate.task.exception;

import lombok.Getter;

public class BalanceUpdateException extends RuntimeException {

    @Getter
    private final String idempotencyKey;

    public BalanceUpdateException(String idempotencyKey, Throwable cause) {
        super(
                "Could not update balances for request [%s] due to: %s".formatted(idempotencyKey, cause.getMessage()),
                cause
        );
        this.idempotencyKey = idempotencyKey;
    }
}
