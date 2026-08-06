package com.wareflow.auth.dto;

import java.time.Instant;
import java.util.Set;

public record LoginResponse(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        String username,
        Set<String> roles
) {
}