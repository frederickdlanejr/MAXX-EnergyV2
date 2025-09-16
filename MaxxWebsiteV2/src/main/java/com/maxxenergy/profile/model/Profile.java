package com.maxxenergy.profile.model;

import java.time.Instant;

/**
 * Represents a registered user profile within the application.
 */
public class Profile {

    private final long id;
    private final String username;
    private final String passwordHash;
    private final String displayName;
    private final String email;
    private final Instant createdAt;

    public Profile(long id, String username, String passwordHash, String displayName, String email, Instant createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.email = email;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
