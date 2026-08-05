package com.wareflow.security;

import com.wareflow.role.Role;
import com.wareflow.user.User;
import com.wareflow.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class WareFlowUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public WareFlowUserDetailsService(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        String normalizedUsername = normalizeUsername(username);

        User user = userRepository
                .findWithRolesByUsername(normalizedUsername)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Invalid username or password"
                ));

        String[] authorities = user.getRoles()
                .stream()
                .map(Role::getName)
                .map(roleName -> "ROLE_" + roleName)
                .toArray(String[]::new);

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .disabled(!user.isActive())
                .build();
    }

    private String normalizeUsername(String username) {
        if (username == null) {
            return "";
        }

        return username
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}