package com.payable.demo.service;

import com.payable.demo.dto.AuthResponse;
import com.payable.demo.dto.LoginRequest;
import com.payable.demo.dto.RegisterRequest;
import com.payable.demo.exception.DuplicateRequestException;
import com.payable.demo.model.Role;
import com.payable.demo.model.User;
import com.payable.demo.repository.RoleRepository;
import com.payable.demo.repository.UserRepository;
import com.payable.demo.repository.UserRoleRepository;
import com.payable.demo.security.JwtService;
import com.payable.demo.service.impl.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtService jwtService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserRoleRepository userRoleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest("testuser", "test@example.com", "password");
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        
        role = new Role();
        role.setId(1L);
        role.setName("CUSTOMER");
    }

    @Test
    void register_Success() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(roleRepository.findByName("CUSTOMER")).thenReturn(Optional.of(role));
        
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwtToken");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("jwtToken", response.getToken());
        verify(userRepository).save(any(User.class));
        verify(userRoleRepository).save(any());
    }

    @Test
    void register_DuplicateUsername_ThrowsException() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(DuplicateRequestException.class, () -> authService.register(registerRequest));
    }
    
    @Test
    void login_Success() {
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwtToken");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
        assertEquals("jwtToken", response.getToken());
        verify(authenticationManager).authenticate(any());
    }
}
