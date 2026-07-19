package com.wareflow.user.exception;

import com.wareflow.common.exception.ApplicationException;
import com.wareflow.common.exception.ErrorCode;

public class UserMustHaveAtLeastOneRoleException
        extends ApplicationException {

    public UserMustHaveAtLeastOneRoleException() {
        super(
                ErrorCode.USER_MUST_HAVE_AT_LEAST_ONE_ROLE,
                "User must have at least one role"
        );
    }
}