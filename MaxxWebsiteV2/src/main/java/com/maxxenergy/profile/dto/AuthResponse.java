package com.maxxenergy.profile.dto;

public record AuthResponse(
        String token,
        ProfileResponse profile
) {
}
