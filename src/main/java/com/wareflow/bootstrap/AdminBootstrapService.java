package com.wareflow.bootstrap;

import com.wareflow.role.Role;
import com.wareflow.role.RoleRepository;
import com.wareflow.user.User;
import com.wareflow.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;

@Service
public class AdminBootstrapService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrapService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createAdminIfMissing() {
        String username = "admin";

        if (userRepository.existsByUsername(username)) {
            return;
        }

        List<Role> roles = roleRepository.findAll();

        if (roles.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot create initial admin because no roles exist"
            );
        }

        User admin = new User(
                username,
                passwordEncoder.encode("ChangeMeImmediately123!"),
                "System",
                "Administrator",
                "admin@wareflow.local"
        );

        admin.assignRoles(new HashSet<>(roles));

        userRepository.save(admin);
    }
}