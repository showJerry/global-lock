package com.cupshe.globallock.exception;

/**
 * TryLockTimeoutException
 *
 * @author zxy
 */
public class TryLockTimeoutException extends RuntimeException {

    private static final String MESSAGE = "Failed to acquire lock.";

    public TryLockTimeoutException() {
        super(MESSAGE);
    }

    public TryLockTimeoutException(Exception e) {
        super(e);
    }
}
