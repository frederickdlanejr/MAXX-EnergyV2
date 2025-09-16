package com.maxxenergy.profile.dto;

import com.maxxenergy.profile.model.Profile;

import java.time.Instant;

public record ProfileResponse(
        long id,
        String username,
        String displayName,
        String email,
        Instant createdAt
) {

    public static ProfileResponse from(Profile profile) {
        return new ProfileResponse(
                profile.getId(),
                profile.getUsername(),
                profile.getDisplayName(),
                profile.getEmail(),
                profile.getCreatedAt()
        );
    }
}
