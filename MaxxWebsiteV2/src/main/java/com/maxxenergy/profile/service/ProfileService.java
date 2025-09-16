package com.maxxenergy.profile.service;

import com.maxxenergy.profile.dto.LoginRequest;
import com.maxxenergy.profile.dto.RegisterRequest;
import com.maxxenergy.profile.model.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProfileService {

    private final PasswordEncoder passwordEncoder;
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final Map<String, Profile> profilesByUsername = new ConcurrentHashMap<>();
    private final Map<String, String> activeTokens = new ConcurrentHashMap<>();

    public ProfileService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Profile register(RegisterRequest request) {
        String username = request.username().trim();
        String normalizedUsername = normalizeUsername(username);

        Profile newProfile = new Profile(
                idGenerator.getAndIncrement(),
                username,
                passwordEncoder.encode(request.password()),
                request.displayName().trim(),
                request.email().trim(),
                Instant.now()
        );

        Profile existing = profilesByUsername.putIfAbsent(normalizedUsername, newProfile);
        if (existing != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        return newProfile;
    }

    public LoginResult login(LoginRequest request) {
        String normalizedUsername = normalizeUsername(request.username());
        Profile profile = profilesByUsername.get(normalizedUsername);

        if (profile == null || !passwordEncoder.matches(request.password(), profile.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }

        // Allow only one active session token per profile
        activeTokens.entrySet().removeIf(entry -> Objects.equals(entry.getValue(), normalizedUsername));

        String token = UUID.randomUUID().toString();
        activeTokens.put(token, normalizedUsername);

        return new LoginResult(token, profile);
    }

    public void logout(String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Token is required");
        }

        String removed = activeTokens.remove(token);
        if (removed == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is not active");
        }
    }

    public Profile requireProfileByToken(String token) {
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing bearer token");
        }

        String normalizedUsername = activeTokens.get(token);
        if (normalizedUsername == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        Profile profile = profilesByUsername.get(normalizedUsername);
        if (profile == null) {
            activeTokens.remove(token);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        return profile;
    }

    private String normalizeUsername(String username) {
        return username.trim().toLowerCase(Locale.ROOT);
    }
}
