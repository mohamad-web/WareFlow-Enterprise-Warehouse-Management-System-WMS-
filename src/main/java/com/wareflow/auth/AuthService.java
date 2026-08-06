package com.wareflow.auth;

import com.wareflow.auth.dto.LoginRequest;
import com.wareflow.auth.dto.LoginResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import com.wareflow.security.JwtToken;
import com.wareflow.security.JwtTokenService;

@Service
public class AuthService {

    private static final String ROLE_PREFIX = "ROLE_";

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtTokenService jwtTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponse login(LoginRequest request) {
        Objects.requireNonNull(
                request,
                "Login request must not be null"
        );

        String normalizedUsername =
                normalizeUsername(request.username());

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                normalizedUsername,
                                request.password()
                        )
                );

        Set<String> roles = authentication.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .filter(authority ->
                        authority.startsWith(ROLE_PREFIX)
                )
                .map(authority ->
                        authority.substring(ROLE_PREFIX.length())
                )
                .sorted()
                .collect(
                        Collectors.toCollection(
                                LinkedHashSet::new
                        )
                );

        JwtToken jwtToken =
                jwtTokenService.generateAccessToken(authentication);

        return new LoginResponse(
                jwtToken.value(),
                "Bearer",
                jwtToken.expiresAt(),
                authentication.getName(),
                Set.copyOf(roles)
        );
    }

    private String normalizeUsername(String username) {
        return username
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}