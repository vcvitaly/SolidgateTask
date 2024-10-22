package io.github.vcvitaly.solidgate.task.exception;

public class UsersNotFoundException extends EntityNotFoundException {

    public UsersNotFoundException() {
        super("Some of the users were not found");
    }
}
