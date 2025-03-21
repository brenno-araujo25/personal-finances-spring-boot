package io.github.brenno_araujo25.personal_finances.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import io.github.brenno_araujo25.personal_finances.dto.AuthResponse;
import io.github.brenno_araujo25.personal_finances.dto.LoginRequest;
import io.github.brenno_araujo25.personal_finances.security.JwtUtil;

public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private LoginRequest mockRequest;
    private UserDetails mockUserDetails;
    private String mockToken;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockRequest = new LoginRequest();
        mockRequest.setUsername("brenno");
        mockRequest.setPassword("123456");

        mockToken = "token";

        mockUserDetails = mock(UserDetails.class);
        when(mockUserDetails.getUsername()).thenReturn("brenno");
    }

    @Test
    void shouldReturnToken() {
        when(jwtUtil.generateToken(mockRequest.getUsername())).thenReturn(mockToken);
        when(userDetailsService.loadUserByUsername(mockRequest.getUsername())).thenReturn(mockUserDetails);

        AuthResponse response = authService.login(mockRequest);

        assertEquals(mockToken, response.getToken());
    }

    @Test
    void shouldThrowExceptionWhenInvalidCredentials() {
        when(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(mockRequest.getUsername(), mockRequest.getPassword())
        )).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> authService.login(mockRequest));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(mockRequest.getUsername(), mockRequest.getPassword())
        )).thenThrow(new RuntimeException());

        when(userDetailsService.loadUserByUsername(mockRequest.getUsername())).thenThrow(new RuntimeException());

        assertThrows(RuntimeException.class, () -> authService.login(mockRequest));
    }

}
