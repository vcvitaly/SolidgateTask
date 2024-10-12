package io.github.vcvitaly.solidgate.task.exception;

public class UsersNotFoundException extends RuntimeException {

    public UsersNotFoundException() {
        super("Some of the users were not found");
    }
}
