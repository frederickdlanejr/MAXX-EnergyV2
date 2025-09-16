package com.maxxenergy.repository;

import com.maxxenergy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);

    @Query("SELECT u FROM User u WHERE u.createdAt >= :since")
    List<User> findRecentUsers(LocalDateTime since);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :since")
    Long countUsersCreatedSince(LocalDateTime since);

    List<User> findByEnabledTrue();

    List<User> findByRole(String role);
}