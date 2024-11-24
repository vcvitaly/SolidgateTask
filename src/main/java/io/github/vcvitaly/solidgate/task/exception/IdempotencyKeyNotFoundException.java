package io.github.vcvitaly.solidgate.task.exception;

public class IdempotencyKeyNotFoundException extends EntityNotFoundException {

    public IdempotencyKeyNotFoundException(String key) {
        super("Idempotency key not found: " + key);
    }
}
