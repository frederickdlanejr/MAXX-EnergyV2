package com.maxxenergy.profile.service;

import com.maxxenergy.profile.model.Profile;

public record LoginResult(String token, Profile profile) {
}
