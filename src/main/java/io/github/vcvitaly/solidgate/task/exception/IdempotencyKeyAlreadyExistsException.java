package io.github.vcvitaly.solidgate.task.exception;

public class IdempotencyKeyAlreadyExistsException extends RuntimeException {

    public IdempotencyKeyAlreadyExistsException() {
        super("Idempotency key already exists");
    }
}
