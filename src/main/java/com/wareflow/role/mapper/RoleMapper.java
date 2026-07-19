package com.wareflow.role.mapper;

import com.wareflow.role.Role;
import com.wareflow.role.dto.RoleResponse;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RoleMapper {

    public RoleResponse toResponse(Role role) {
        Objects.requireNonNull(role, "Role must not be null");

        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription()
        );
    }
}