package com.wareflow.user.exception;

import com.wareflow.common.exception.ApplicationException;
import com.wareflow.common.exception.ErrorCode;

public class EmailAlreadyExistsException extends ApplicationException {

    public EmailAlreadyExistsException(String email) {
        super(
                ErrorCode.EMAIL_ALREADY_EXISTS,
                "Email '%s' already exists".formatted(email)
        );
    }
}