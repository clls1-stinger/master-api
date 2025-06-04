package com.life.master_api.controllers;

import com.life.master_api.dto.UserDto;
import com.life.master_api.entities.User;
import com.life.master_api.exceptions.ResourceNotFoundException;
import com.life.master_api.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User API", description = "Endpoints for managing user information")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Get information about the currently authenticated user")
    public ResponseEntity<UserDto> getCurrentUser() {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", authentication.getName()));

        UserDto userDto = new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getLastLogin()
        );

        return ResponseEntity.ok(userDto);
    }

    @PutMapping("/me/password")
    @Operation(summary = "Update password", description = "Update the password of the currently authenticated user")
    public ResponseEntity<String> updatePassword(@RequestParam String currentPassword, @RequestParam String newPassword) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", authentication.getName()));

        // Verify current password
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body("Current password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok("Password updated successfully");
    }

    @PutMapping("/me/email")
    @Operation(summary = "Update email", description = "Update the email of the currently authenticated user")
    public ResponseEntity<?> updateEmail(@RequestParam String password, @RequestParam String newEmail) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", authentication.getName()));

        // Verify password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.badRequest().body("Password is incorrect");
        }

        // Check if email is already taken
        if (userRepository.existsByEmail(newEmail) && !user.getEmail().equals(newEmail)) {
            return ResponseEntity.badRequest().body("Email is already taken");
        }

        // Update email
        user.setEmail(newEmail);
        userRepository.save(user);

        UserDto userDto = new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getLastLogin()
        );

        return ResponseEntity.ok(userDto);
    }
}