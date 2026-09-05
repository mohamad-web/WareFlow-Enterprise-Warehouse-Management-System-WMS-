package com.wareflow.security;

import com.wareflow.user.service.UserCommandService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.wareflow.user.dto.CreateUserRequest;
import com.wareflow.user.dto.UserResponse;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import org.hamcrest.Matchers;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserCommandService userCommandService;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtTokenService jwtTokenService;

    @Test
    void getUsersWithoutTokenShouldReturn401() throws Exception {
        mockMvc.perform(
                        get("/api/v1/users")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUsersWithWarehouseWorkerRoleShouldReturn403() throws Exception {
        mockMvc.perform(
                        get("/api/v1/users")
                                .with(jwt()
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_WAREHOUSE_WORKER"
                                                )
                                        )
                                )
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void getUsersWithWarehouseManagerRoleShouldReturn200() throws Exception {
        mockMvc.perform(
                        get("/api/v1/users")
                                .with(jwt()
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_WAREHOUSE_MANAGER"
                                                )
                                        )
                                )
                )
                .andExpect(status().isOk());
    }

    @Test
    void createUserWithWarehouseWorkerRoleShouldReturn403() throws Exception {
        mockMvc.perform(
                        post("/api/v1/users")
                                .with(jwt()
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_WAREHOUSE_WORKER"
                                                )
                                        )
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "username": "testuser",
                              "password": "SecurePassword123!",
                              "firstName": "Test",
                              "lastName": "User",
                              "email": "testuser@example.com",
                              "roleIds": [1]
                            }
                            """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void createUserWithAdminRoleShouldReturn201() throws Exception {
        UserResponse response = new UserResponse(
                10L,
                "testuser",
                "Test",
                "User",
                "testuser@example.com",
                true,
                Set.of(),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(userCommandService.createUser(any(CreateUserRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/users")
                                .with(jwt()
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_ADMIN"
                                                )
                                        )
                                )
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                              "username": "testuser",
                              "password": "SecurePassword123!",
                              "firstName": "Test",
                              "lastName": "User",
                              "email": "testuser@example.com",
                              "roleIds": [1]
                            }
                            """)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "http://localhost/api/v1/users/10"
                ))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void currentUserWithValidJwtShouldReturn200() throws Exception {
        mockMvc.perform(
                        get("/api/v1/auth/me")
                                .with(jwt()
                                        .jwt(jwt -> jwt.subject("mohamad"))
                                        .authorities(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_WAREHOUSE_MANAGER"
                                                ),
                                                new SimpleGrantedAuthority(
                                                        "ROLE_WAREHOUSE_WORKER"
                                                )
                                        )
                                )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("mohamad"))
                .andExpect(jsonPath("$.authorities")
                        .isArray())
                .andExpect(jsonPath("$.authorities").value(
                        Matchers.hasItems(
                                "ROLE_WAREHOUSE_MANAGER",
                                "ROLE_WAREHOUSE_WORKER"
                        )
                ));
    }

    @Test
    void loginWithValidCredentialsShouldReturn200AndAccessToken()
            throws Exception {

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(
                        "mohamad",
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        "ROLE_WAREHOUSE_MANAGER"
                                ),
                                new SimpleGrantedAuthority(
                                        "ROLE_WAREHOUSE_WORKER"
                                )
                        )
                );

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenReturn(authentication);

        Instant expiresAt =
                Instant.now().plusSeconds(900);

        when(jwtTokenService.generateAccessToken(authentication))
                .thenReturn(
                        new JwtToken(
                                "test-jwt-token",
                                expiresAt
                        )
                );

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "username": "Mohamad",
                                      "password": "SecurePassword123!"
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken")
                        .value("test-jwt-token"))
                .andExpect(jsonPath("$.tokenType")
                        .value("Bearer"))
                .andExpect(jsonPath("$.username")
                        .value("mohamad"))
                .andExpect(jsonPath("$.roles")
                        .value(Matchers.hasItems(
                                "WAREHOUSE_MANAGER",
                                "WAREHOUSE_WORKER"
                        )));
    }

    @Test
    void loginWithInvalidCredentialsShouldReturn401()
            throws Exception {

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)
        )).thenThrow(
                new BadCredentialsException(
                        "Invalid username or password"
                )
        );

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                      "username": "mohamad",
                                      "password": "WrongPassword123!"
                                    }
                                    """)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.code").value("INVALID_CREDENTIALS"))
                .andExpect(jsonPath("$.message")
                        .value("Invalid username or password"));
    }
}