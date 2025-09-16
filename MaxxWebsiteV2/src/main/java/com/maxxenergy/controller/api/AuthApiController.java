package com.maxxenergy.controller.api;

import com.maxxenergy.model.User;
import com.maxxenergy.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        try {
            // Create new user
            User user = new User();
            user.setEmail(registerRequest.getEmail());
            user.setPassword(registerRequest.getPassword());
            user.setFirstName(registerRequest.getFirstName());
            user.setLastName(registerRequest.getLastName());
            user.setPhone(registerRequest.getPhone());

            User savedUser = userService.registerUser(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("userId", savedUser.getId());
            response.put("email", savedUser.getEmail());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user details
            Optional<User> userOpt = userService.findByEmail(loginRequest.getEmail());
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Login successful");
                response.put("user", createUserResponse(user));

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (AuthenticationException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out successfully");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Optional<User> userOpt = userService.findByEmail(authentication.getName());
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", createUserResponse(user));
            response.put("potentialSavings", userService.calculatePotentialSavings(user));
            response.put("solarAssessment", userService.generateSolarAssessment(user));

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody UpdateProfileRequest updateRequest,
                                               Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Optional<User> userOpt = userService.findByEmail(authentication.getName());
            if (userOpt.isPresent()) {
                User currentUser = userOpt.get();

                // Create updated user object
                User updatedUser = new User();
                updatedUser.setFirstName(updateRequest.getFirstName());
                updatedUser.setLastName(updateRequest.getLastName());
                updatedUser.setPhone(updateRequest.getPhone());
                updatedUser.setAddress(updateRequest.getAddress());
                updatedUser.setCity(updateRequest.getCity());
                updatedUser.setState(updateRequest.getState());
                updatedUser.setZipCode(updateRequest.getZipCode());
                updatedUser.setPropertyType(updateRequest.getPropertyType());
                updatedUser.setMonthlyEnergyBill(updateRequest.getMonthlyEnergyBill());
                updatedUser.setRoofType(updateRequest.getRoofType());
                updatedUser.setRoofAge(updateRequest.getRoofAge());
                updatedUser.setEnergyGoals(updateRequest.getEnergyGoals());

                User savedUser = userService.updateProfile(currentUser, updatedUser);

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Profile updated successfully");
                response.put("user", createUserResponse(savedUser));

                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error updating profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest,
                                            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "User not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Optional<User> userOpt = userService.findByEmail(authentication.getName());
            if (userOpt.isPresent()) {
                User user = userOpt.get();

                boolean success = userService.changePassword(
                        user,
                        changePasswordRequest.getCurrentPassword(),
                        changePasswordRequest.getNewPassword()
                );

                Map<String, Object> response = new HashMap<>();
                if (success) {
                    response.put("success", true);
                    response.put("message", "Password changed successfully");
                    return ResponseEntity.ok(response);
                } else {
                    response.put("success", false);
                    response.put("message", "Current password is incorrect");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
                }
            } else {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error changing password: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Helper method to create user response without sensitive data
    private Map<String, Object> createUserResponse(User user) {
        Map<String, Object> userResponse = new HashMap<>();
        userResponse.put("id", user.getId());
        userResponse.put("email", user.getEmail());
        userResponse.put("firstName", user.getFirstName());
        userResponse.put("lastName", user.getLastName());
        userResponse.put("fullName", user.getFullName());
        userResponse.put("phone", user.getPhone());
        userResponse.put("address", user.getAddress());
        userResponse.put("city", user.getCity());
        userResponse.put("state", user.getState());
        userResponse.put("zipCode", user.getZipCode());
        userResponse.put("propertyType", user.getPropertyType());
        userResponse.put("monthlyEnergyBill", user.getMonthlyEnergyBill());
        userResponse.put("roofType", user.getRoofType());
        userResponse.put("roofAge", user.getRoofAge());
        userResponse.put("energyGoals", user.getEnergyGoals());
        userResponse.put("createdAt", user.getCreatedAt());
        userResponse.put("lastLogin", user.getLastLogin());
        return userResponse;
    }

    // Request DTOs
    public static class RegisterRequest {
        private String email;
        private String password;
        private String firstName;
        private String lastName;
        private String phone;

        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        // Getters and Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class UpdateProfileRequest {
        private String firstName;
        private String lastName;
        private String phone;
        private String address;
        private String city;
        private String state;
        private String zipCode;
        private String propertyType;
        private Double monthlyEnergyBill;
        private String roofType;
        private Integer roofAge;
        private String energyGoals;

        // Getters and Setters
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
        public String getPropertyType() { return propertyType; }
        public void setPropertyType(String propertyType) { this.propertyType = propertyType; }
        public Double getMonthlyEnergyBill() { return monthlyEnergyBill; }
        public void setMonthlyEnergyBill(Double monthlyEnergyBill) { this.monthlyEnergyBill = monthlyEnergyBill; }
        public String getRoofType() { return roofType; }
        public void setRoofType(String roofType) { this.roofType = roofType; }
        public Integer getRoofAge() { return roofAge; }
        public void setRoofAge(Integer roofAge) { this.roofAge = roofAge; }
        public String getEnergyGoals() { return energyGoals; }
        public void setEnergyGoals(String energyGoals) { this.energyGoals = energyGoals; }
    }

    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;

        // Getters and Setters
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}