package com.mercury.discovery.common.error.exception;

public class CaptchaInvalidException extends RuntimeException {

    private static final long serialVersionUID = 5861310537366287163L;

    public CaptchaInvalidException() {
        super();
    }

    public CaptchaInvalidException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public CaptchaInvalidException(final String message) {
        super(message);
    }

    public CaptchaInvalidException(final Throwable cause) {
        super(cause);
    }
}
