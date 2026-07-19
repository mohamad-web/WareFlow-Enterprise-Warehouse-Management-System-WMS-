package com.wareflow.common.exception;

import java.util.Objects;

public abstract class ApplicationException extends RuntimeException {

    private final ErrorCode errorCode;

    protected ApplicationException(
            ErrorCode errorCode,
            String message
    ) {
        super(message);
        this.errorCode = Objects.requireNonNull(
                errorCode,
                "Error code must not be null"
        );
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}