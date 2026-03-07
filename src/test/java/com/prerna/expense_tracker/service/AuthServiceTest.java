package com.prerna.expense_tracker.service;

import com.prerna.expense_tracker.dto.LoginRequest;
import com.prerna.expense_tracker.dto.RegisterRequest;
import com.prerna.expense_tracker.dto.AuthResponse;
import com.prerna.expense_tracker.entity.User;
import com.prerna.expense_tracker.repository.UserRepository;
import com.prerna.expense_tracker.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private User mockUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .name("Prerna")
                .email("prerna@gmail.com")
                .password("encodedPassword")
                .build();

        registerRequest = new RegisterRequest();
        registerRequest.setName("Prerna");
        registerRequest.setEmail("prerna@gmail.com");
        registerRequest.setPassword("prerna123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("prerna@gmail.com");
        loginRequest.setPassword("prerna123");
    }

    // ✅ Test 1 - Register success
    @Test
    void register_ShouldReturnToken_WhenEmailNotExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtUtil.generateToken(mockUser.getEmail())).thenReturn("mock.jwt.token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("prerna@gmail.com", response.getEmail());
        assertEquals("Prerna", response.getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    // ✅ Test 2 - Register fails when email already exists
    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.register(registerRequest));

        assertEquals("Email already registered", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // ✅ Test 3 - Login success
    @Test
    void login_ShouldReturnToken_WhenCredentialsAreValid() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateToken(mockUser.getEmail())).thenReturn("mock.jwt.token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("prerna@gmail.com", response.getEmail());
    }

    // ✅ Test 4 - Login fails when user not found
    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(loginRequest));

        assertEquals("User not found", exception.getMessage());
    }
}