package com.maxxenergy.controller;

import com.maxxenergy.model.User;
import com.maxxenergy.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {

        if (error != null) {
            model.addAttribute("error", "Invalid email or password. Please try again.");
        }

        if (logout != null) {
            model.addAttribute("message", "You have been logged out successfully.");
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
                               BindingResult result,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("success",
                    "Registration successful! Please log in with your credentials.");
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/profile")
    public String profilePage(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("activePage", "profile");
        model.addAttribute("potentialSavings", userService.calculatePotentialSavings(user));
        model.addAttribute("solarAssessment", userService.generateSolarAssessment(user));

        return "auth/profile";
    }

    @GetMapping("/profile/edit")
    public String editProfilePage(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("activePage", "profile");

        return "auth/edit-profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute("user") User updatedUser,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User currentUser = userService.findByEmail(authentication.getName()).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        try {
            userService.updateProfile(currentUser, updatedUser);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
        }

        return "redirect:/profile";
    }

    @GetMapping("/profile/change-password")
    public String changePasswordPage(Model model, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        model.addAttribute("activePage", "profile");
        return "auth/change-password";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        User user = userService.findByEmail(authentication.getName()).orElse(null);
        if (user == null) {
            return "redirect:/login";
        }

        // Validate new password
        if (newPassword == null || newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "New password must be at least 6 characters long.");
            return "redirect:/profile/change-password";
        }

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "New passwords do not match.");
            return "redirect:/profile/change-password";
        }

        // Change password
        boolean success = userService.changePassword(user, currentPassword, newPassword);

        if (success) {
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
            return "redirect:/profile";
        } else {
            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
            return "redirect:/profile/change-password";
        }
    }

    // Helper method to get current authenticated user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !authentication.getName().equals("anonymousUser")) {
            return userService.findByEmail(authentication.getName()).orElse(null);
        }
        return null;
    }
}