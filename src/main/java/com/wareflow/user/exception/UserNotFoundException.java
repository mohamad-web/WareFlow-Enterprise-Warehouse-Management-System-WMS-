package com.wareflow.user.exception;

import com.wareflow.common.exception.ApplicationException;
import com.wareflow.common.exception.ErrorCode;

public class UserNotFoundException extends ApplicationException {

    public UserNotFoundException(Long userId) {
        super(
                ErrorCode.USER_NOT_FOUND,
                "User with ID '%d' was not found".formatted(userId)
        );
    }

    public static UserNotFoundException byUsername(String username) {
        return new UserNotFoundException(
                "User with username '%s' was not found".formatted(username)
        );
    }

    private UserNotFoundException(String message) {
        super(ErrorCode.USER_NOT_FOUND, message);
    }
}