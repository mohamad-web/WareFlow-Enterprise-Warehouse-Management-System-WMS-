package com.wareflow.auth.dto;

import java.util.Set;

public record LoginResponse(
        String username,
        Set<String> roles
) {
}