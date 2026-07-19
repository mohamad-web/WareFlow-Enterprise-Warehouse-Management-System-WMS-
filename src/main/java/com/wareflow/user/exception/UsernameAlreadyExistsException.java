package com.wareflow.user.exception;

import com.wareflow.common.exception.ApplicationException;
import com.wareflow.common.exception.ErrorCode;

public class UsernameAlreadyExistsException extends ApplicationException {

    public UsernameAlreadyExistsException(String username) {
        super(
                ErrorCode.USERNAME_ALREADY_EXISTS,
                "Username '%s' already exists".formatted(username)
        );
    }
}