package com.wareflow.config;

import com.wareflow.security.JwtAccessDeniedHandler;
import com.wareflow.security.JwtAuthenticationEntryPoint;
import com.wareflow.security.WareFlowUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

@Configuration
public class SecurityConfig {

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            WareFlowUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider authenticationProvider,
            JwtAuthenticationConverter jwtAuthenticationConverter,
            JwtAuthenticationEntryPoint authenticationEntryPoint,
            JwtAccessDeniedHandler accessDeniedHandler
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authenticationProvider(authenticationProvider)

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api/v1/auth/login"
                        ).permitAll()

                        .requestMatchers(
                                "/api/v1/auth/me"
                        ).authenticated()

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/users/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "WAREHOUSE_MANAGER"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/users/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/users/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/users/**"
                        ).hasRole("ADMIN")

                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/warehouses/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "WAREHOUSE_MANAGER",
                                "WAREHOUSE_WORKER"
                        )

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/warehouses/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "WAREHOUSE_MANAGER"
                        )

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/v1/warehouses/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "WAREHOUSE_MANAGER"
                        )

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/v1/warehouses/**"
                        ).hasAnyRole(
                                "ADMIN",
                                "WAREHOUSE_MANAGER"
                        )

                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                );

        return http.build();
    }
}