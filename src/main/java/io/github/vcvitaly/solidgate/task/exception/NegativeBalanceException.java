package io.github.vcvitaly.solidgate.task.exception;

public class NegativeBalanceException extends RuntimeException {

    public NegativeBalanceException() {
        super("Some of the balances are negative");
    }
}
