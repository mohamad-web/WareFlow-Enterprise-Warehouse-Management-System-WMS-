package com.wareflow.user.dto;

import com.wareflow.role.dto.RoleResponse;

import java.time.OffsetDateTime;
import java.util.Set;

public record UserResponse(
        Long id,
        String username,
        String firstName,
        String lastName,
        String email,
        boolean active,
        Set<RoleResponse> roles,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}