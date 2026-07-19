package com.wareflow.role.exception;

import com.wareflow.common.exception.ApplicationException;
import com.wareflow.common.exception.ErrorCode;

import java.util.Collection;
import java.util.stream.Collectors;

public class RoleNotFoundException extends ApplicationException {

    public RoleNotFoundException(Long roleId) {
        super(
                ErrorCode.ROLE_NOT_FOUND,
                "Role with ID '%d' was not found".formatted(roleId)
        );
    }

    public RoleNotFoundException(Collection<Long> roleIds) {
        super(
                ErrorCode.ROLE_NOT_FOUND,
                createMessage(roleIds)
        );
    }

    private static String createMessage(Collection<Long> roleIds) {
        String ids = roleIds.stream()
                .map(String::valueOf)
                .sorted()
                .collect(Collectors.joining(", "));

        return "Roles with the following IDs were not found: " + ids;
    }
}