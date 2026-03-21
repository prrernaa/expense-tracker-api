package com.prerna.expense_tracker.service;

import com.prerna.expense_tracker.dto.*;
import com.prerna.expense_tracker.entity.User;
import com.prerna.expense_tracker.kafka.UserRegisteredEvent;
import com.prerna.expense_tracker.kafka.UserRegisteredProducer;
import com.prerna.expense_tracker.repository.UserRepository;
import com.prerna.expense_tracker.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;
    private final UserRegisteredProducer userRegisteredProducer;


    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser =userRepository.save(user);

        // 👇 publish Kafka event
        userRegisteredProducer.publishUserRegisteredEvent(new UserRegisteredEvent(
                savedUser.getId().toString(),
                savedUser.getEmail(),
                savedUser.getName()
        ));

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getName());
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getName());
    }

    public ApiResponse<Void> logout(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            long expirationTime = jwtUtil.getExpirationTime(token);
            tokenBlacklistService.blacklistToken(token, expirationTime);
        }
        return ApiResponse.success("Logged out successfully", null);
    }
}
