package com.wareflow.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

        @NotBlank(message = "New password is required")
        @Size(
                min = 12,
                max = 128,
                message = "Password must contain between 12 and 128 characters"
        )
        String newPassword

) {
}