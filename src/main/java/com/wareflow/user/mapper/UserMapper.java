package com.wareflow.user.mapper;

import com.wareflow.role.Role;
import com.wareflow.role.dto.RoleResponse;
import com.wareflow.role.mapper.RoleMapper;
import com.wareflow.user.User;
import com.wareflow.user.dto.UserResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserMapper {

    private final RoleMapper roleMapper;

    public UserMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    public UserResponse toResponse(User user) {
        Objects.requireNonNull(user, "User must not be null");

        Set<RoleResponse> roleResponses = user.getRoles()
                .stream()
                .sorted(Comparator.comparing(Role::getName))
                .map(roleMapper::toResponse)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.isActive(),
                Collections.unmodifiableSet(roleResponses),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}