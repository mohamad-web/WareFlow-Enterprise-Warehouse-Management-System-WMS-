package com.wareflow.auth;

import com.wareflow.auth.dto.LoginRequest;
import com.wareflow.auth.dto.LoginResponse;
import com.wareflow.security.JwtToken;
import com.wareflow.security.JwtTokenService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenService jwtTokenService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                authenticationManager,
                jwtTokenService
        );
    }

    @Test
    void loginShouldNormalizeUsernameBeforeAuthentication() {
        LoginRequest request = new LoginRequest(
                "  MoHaMaD  ",
                "SecurePassword123!"
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "mohamad",
                        null,
                        List.of(
                                new SimpleGrantedAuthority("ROLE_ADMIN")
                        )
                );

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        Instant expiresAt = Instant.now().plusSeconds(900);

        when(jwtTokenService.generateAccessToken(authentication))
                .thenReturn(
                        new JwtToken(
                                "test-token",
                                expiresAt
                        )
                );

        LoginResponse response = authService.login(request);

        ArgumentCaptor<UsernamePasswordAuthenticationToken> captor =
                ArgumentCaptor.forClass(
                        UsernamePasswordAuthenticationToken.class
                );

        verify(authenticationManager)
                .authenticate(captor.capture());

        UsernamePasswordAuthenticationToken authenticationRequest =
                captor.getValue();

        assertEquals(
                "mohamad",
                authenticationRequest.getPrincipal()
        );

        assertEquals(
                "SecurePassword123!",
                authenticationRequest.getCredentials()
        );

        assertEquals("mohamad", response.username());
        assertEquals("test-token", response.accessToken());
        assertEquals("Bearer", response.tokenType());
        assertEquals(expiresAt, response.expiresAt());
        assertTrue(response.roles().contains("ADMIN"));
    }

    @Test
    void loginShouldIncludeOnlyRoleAuthorities() {
        LoginRequest request = new LoginRequest(
                "mohamad",
                "SecurePassword123!"
        );

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "mohamad",
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_WAREHOUSE_WORKER"
                                ),
                                new SimpleGrantedAuthority(
                                        "FACTOR_BEARER"
                                ),
                                new SimpleGrantedAuthority(
                                        "ROLE_ADMIN"
                                )
                        )
                );

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        Instant expiresAt = Instant.now().plusSeconds(900);

        when(jwtTokenService.generateAccessToken(authentication))
                .thenReturn(
                        new JwtToken(
                                "test-token",
                                expiresAt
                        )
                );

        LoginResponse response =
                authService.login(request);

        assertEquals(
                2,
                response.roles().size()
        );

        assertTrue(
                response.roles().contains("ADMIN")
        );

        assertTrue(
                response.roles().contains(
                        "WAREHOUSE_WORKER"
                )
        );

        assertFalse(
                response.roles().contains(
                        "FACTOR_BEARER"
                )
        );
    }

    @Test
    void loginShouldNotGenerateTokenWhenAuthenticationFails() {
        LoginRequest request = new LoginRequest(
                "mohamad",
                "WrongPassword123!"
        );

        when(authenticationManager.authenticate(any()))
                .thenThrow(
                        new BadCredentialsException(
                                "Invalid username or password"
                        )
                );

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verifyNoInteractions(jwtTokenService);
    }
}