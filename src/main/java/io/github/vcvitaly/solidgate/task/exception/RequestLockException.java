package io.github.vcvitaly.solidgate.task.exception;

public class RequestLockException extends RuntimeException {

    public RequestLockException(String message) {
        super(message);
    }

  public RequestLockException(String message, Throwable cause) {
    super(message, cause);
  }
}
