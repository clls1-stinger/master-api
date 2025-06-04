package com.life.master_api.controllers;

import com.life.master_api.dto.JwtAuthResponse;
import com.life.master_api.dto.LoginDto;
import com.life.master_api.dto.RegisterDto;
import com.life.master_api.entities.User;
import com.life.master_api.repositories.UserRepository;
import com.life.master_api.security.JwtTokenProvider;
import com.life.master_api.services.SampleDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication API", description = "Endpoints for user authentication and registration")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final SampleDataService sampleDataService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider tokenProvider,
                          SampleDataService sampleDataService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.sampleDataService = sampleDataService;
    }

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate a user and return a JWT token")
    public ResponseEntity<JwtAuthResponse> authenticateUser(@Valid @RequestBody LoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getUsernameOrEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get token from tokenProvider
        String token = tokenProvider.generateToken(authentication);
        
        // Find user to get ID and update last login
        User user = userRepository.findByUsername(loginDto.getUsernameOrEmail())
                .orElseGet(() -> userRepository.findByEmail(loginDto.getUsernameOrEmail())
                        .orElseThrow(() -> new IllegalArgumentException("User not found")));
        
        // Update last login time
        user.setLastLogin(new Date());
        userRepository.save(user);

        return ResponseEntity.ok(new JwtAuthResponse(token, user.getId(), user.getUsername()));
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Register a new user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterDto registerDto) {
        // Check if username exists
        if (userRepository.existsByUsername(registerDto.getUsername())) {
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        // Check if email exists
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        // Create user object
        User user = new User();
        user.setUsername(registerDto.getUsername());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setCreatedAt(new Date());

        User savedUser = userRepository.save(user);
        
        // Crear datos de ejemplo para el nuevo usuario
        try {
            sampleDataService.createSampleDataForUser(savedUser);
        } catch (Exception e) {
            System.err.println("Error creando datos de ejemplo para usuario " + savedUser.getUsername() + ": " + e.getMessage());
            // No fallar el registro si hay error creando datos de ejemplo
        }

        return new ResponseEntity<>("User registered successfully with sample data", HttpStatus.CREATED);
    }
}