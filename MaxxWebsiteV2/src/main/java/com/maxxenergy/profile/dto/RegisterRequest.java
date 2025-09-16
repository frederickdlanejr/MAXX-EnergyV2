package com.maxxenergy.profile.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
        String username,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
        String password,

        @NotBlank(message = "Display name is required")
        @Size(max = 100, message = "Display name cannot exceed 100 characters")
        String displayName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email
) {
}
