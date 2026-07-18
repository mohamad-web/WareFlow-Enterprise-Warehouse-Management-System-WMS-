package com.wareflow.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateUserRequest(

        @NotBlank(message = "Username is required")
        @Size(
                min = 3,
                max = 50,
                message = "Username must contain between 3 and 50 characters"
        )
        @Pattern(
                regexp = "^[a-zA-Z0-9._-]+$",
                message = "Username may contain only letters, numbers, dots, underscores, and hyphens"
        )
        String username,

        @NotBlank(message = "Password is required")
        @Size(
                min = 12,
                max = 128,
                message = "Password must contain between 12 and 128 characters"
        )
        String password,

        @NotBlank(message = "First name is required")
        @Size(
                max = 100,
                message = "First name must not exceed 100 characters"
        )
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(
                max = 100,
                message = "Last name must not exceed 100 characters"
        )
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(
                max = 254,
                message = "Email must not exceed 254 characters"
        )
        String email,

        @NotEmpty(message = "At least one role must be assigned")
        Set<@NotNull(message = "Role ID must not be null") Long> roleIds

) {
}