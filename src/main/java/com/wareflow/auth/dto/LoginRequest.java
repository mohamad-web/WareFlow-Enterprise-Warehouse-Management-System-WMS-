package com.wareflow.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(

        @NotBlank(message = "Username is required")
        @Size(
                min = 3,
                max = 50,
                message = "Username must contain between 3 and 50 characters"
        )
        String username,

        @NotBlank(message = "Password is required")
        @Size(
                max = 128,
                message = "Password must not exceed 128 characters"
        )
        String password

) {
}