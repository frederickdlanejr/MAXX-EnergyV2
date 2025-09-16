package com.maxxenergy.profile.controller;

import com.maxxenergy.profile.dto.ProfileResponse;
import com.maxxenergy.profile.model.Profile;
import com.maxxenergy.profile.service.ProfileService;
import com.maxxenergy.profile.support.AuthorizationHeaderUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> me(@RequestHeader(name = "Authorization", required = false) String authorizationHeader) {
        String token = AuthorizationHeaderUtil.extractBearerToken(authorizationHeader);
        Profile profile = profileService.requireProfileByToken(token);
        return ResponseEntity.ok(ProfileResponse.from(profile));
    }
}
