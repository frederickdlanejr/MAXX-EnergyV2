package com.maxxenergy.service;

import com.maxxenergy.model.User;
import com.maxxenergy.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection instead of @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void createDemoUser() {
        if (!userRepository.existsByEmail("demo@maxxenergy.com")) {
            User demoUser = new User();
            demoUser.setEmail("demo@maxxenergy.com");
            demoUser.setPassword("demo123");
            demoUser.setFirstName("Demo");
            demoUser.setLastName("User");
            demoUser.setPhone("(555) 123-4567");
            demoUser.setMonthlyEnergyBill(250.0);
            demoUser.setPropertyType("Residential");
            demoUser.setRoofType("Asphalt Shingle");
            demoUser.setRoofAge(8);
            demoUser.setEnergyGoals("Reduce electricity bills and help the environment");

            try {
                registerUser(demoUser);
            } catch (Exception e) {
                // Demo user creation failed, that's okay
                System.out.println("Demo user already exists or could not be created");
            }
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Update last login time
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return user;
    }

    public User registerUser(User user) throws Exception {
        // Check if user already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new Exception("An account with this email already exists");
        }

        // Validate required fields
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new Exception("Email is required");
        }

        if (user.getPassword() == null || user.getPassword().length() < 6) {
            throw new Exception("Password must be at least 6 characters");
        }

        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty()) {
            throw new Exception("First name is required");
        }

        if (user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new Exception("Last name is required");
        }

        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setEnabled(true);

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public User updateProfile(User currentUser, User updatedUser) {
        // Update profile information (excluding password and email)
        currentUser.setFirstName(updatedUser.getFirstName());
        currentUser.setLastName(updatedUser.getLastName());
        currentUser.setPhone(updatedUser.getPhone());
        currentUser.setAddress(updatedUser.getAddress());
        currentUser.setCity(updatedUser.getCity());
        currentUser.setState(updatedUser.getState());
        currentUser.setZipCode(updatedUser.getZipCode());
        currentUser.setPropertyType(updatedUser.getPropertyType());
        currentUser.setMonthlyEnergyBill(updatedUser.getMonthlyEnergyBill());
        currentUser.setRoofType(updatedUser.getRoofType());
        currentUser.setRoofAge(updatedUser.getRoofAge());
        currentUser.setEnergyGoals(updatedUser.getEnergyGoals());

        return userRepository.save(currentUser);
    }

    public boolean changePassword(User user, String currentPassword, String newPassword) {
        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }

        // Update with new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getRecentUsers(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        return userRepository.findRecentUsers(since);
    }

    public Long countNewUsersThisMonth() {
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return userRepository.countUsersCreatedSince(startOfMonth);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Calculate potential solar savings based on user's energy bill
    public double calculatePotentialSavings(User user) {
        if (user.getMonthlyEnergyBill() == null || user.getMonthlyEnergyBill() <= 0) {
            return 0;
        }

        double monthlyBill = user.getMonthlyEnergyBill();
        double annualBill = monthlyBill * 12;

        // Assume 70-90% savings with solar
        double savingsPercentage = 0.8; // 80% average

        return annualBill * savingsPercentage;
    }

    // Generate a simple solar assessment based on user's profile
    public String generateSolarAssessment(User user) {
        StringBuilder assessment = new StringBuilder();

        if (user.getMonthlyEnergyBill() != null && user.getMonthlyEnergyBill() > 0) {
            double monthlyBill = user.getMonthlyEnergyBill();
            double potentialSavings = calculatePotentialSavings(user);

            assessment.append("Based on your monthly energy bill of $")
                    .append(String.format("%.0f", monthlyBill))
                    .append(", you could save approximately $")
                    .append(String.format("%.0f", potentialSavings))
                    .append(" annually with solar panels. ");
        }

        if (user.getRoofAge() != null) {
            if (user.getRoofAge() < 10) {
                assessment.append("Your roof age of ")
                        .append(user.getRoofAge())
                        .append(" years is excellent for solar installation. ");
            } else if (user.getRoofAge() < 20) {
                assessment.append("Your roof age of ")
                        .append(user.getRoofAge())
                        .append(" years is suitable for solar installation. ");
            } else {
                assessment.append("Your roof age of ")
                        .append(user.getRoofAge())
                        .append(" years may require inspection before solar installation. ");
            }
        }

        if (user.getPropertyType() != null) {
            if ("Commercial".equalsIgnoreCase(user.getPropertyType())) {
                assessment.append("Commercial properties often see faster ROI with solar installations. ");
            } else {
                assessment.append("Residential solar installations typically pay for themselves within 6-8 years. ");
            }
        }

        if (assessment.length() == 0) {
            assessment.append("Complete your profile to receive a personalized solar assessment!");
        }

        return assessment.toString();
    }
}