package com.maxxenergy.profile.controller;

import com.maxxenergy.profile.dto.AuthResponse;
import com.maxxenergy.profile.dto.LoginRequest;
import com.maxxenergy.profile.dto.ProfileResponse;
import com.maxxenergy.profile.dto.RegisterRequest;
import com.maxxenergy.profile.model.Profile;
import com.maxxenergy.profile.service.LoginResult;
import com.maxxenergy.profile.service.ProfileService;
import com.maxxenergy.profile.support.AuthorizationHeaderUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ProfileService profileService;

    public AuthController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping("/register")
    public ResponseEntity<ProfileResponse> register(@Valid @RequestBody RegisterRequest request) {
        Profile profile = profileService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProfileResponse.from(profile));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResult result = profileService.login(request);
        return ResponseEntity.ok(new AuthResponse(result.token(), ProfileResponse.from(result.profile())));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(name = "Authorization", required = false) String authorizationHeader) {
        String token = AuthorizationHeaderUtil.extractBearerToken(authorizationHeader);
        profileService.logout(token);
        return ResponseEntity.noContent().build();
    }
}
