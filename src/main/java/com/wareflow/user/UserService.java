package com.wareflow.user;

import com.wareflow.role.Role;
import com.wareflow.role.RoleRepository;
import com.wareflow.role.exception.RoleNotFoundException;
import com.wareflow.user.dto.CreateUserRequest;
import com.wareflow.user.dto.UserResponse;
import com.wareflow.user.exception.EmailAlreadyExistsException;
import com.wareflow.user.exception.UsernameAlreadyExistsException;
import com.wareflow.user.mapper.UserMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        Objects.requireNonNull(
                request,
                "Create user request must not be null"
        );

        String normalizedUsername =
                normalizeUsername(request.username());

        String normalizedEmail =
                normalizeEmail(request.email());

        validateUsernameIsAvailable(normalizedUsername);
        validateEmailIsAvailable(normalizedEmail);

        Set<Role> roles = loadRoles(request.roleIds());

        String passwordHash =
                passwordEncoder.encode(request.password());

        User user = new User(
                normalizedUsername,
                passwordHash,
                request.firstName(),
                request.lastName(),
                normalizedEmail
        );

        user.assignRoles(roles);

        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    private void validateUsernameIsAvailable(String username) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistsException(username);
        }
    }

    private void validateEmailIsAvailable(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(email);
        }
    }

    private Set<Role> loadRoles(Set<Long> requestedRoleIds) {
        List<Role> foundRoles =
                roleRepository.findAllById(requestedRoleIds);

        Set<Long> foundRoleIds = foundRoles.stream()
                .map(Role::getId)
                .collect(Collectors.toSet());

        Set<Long> missingRoleIds =
                new LinkedHashSet<>(requestedRoleIds);

        missingRoleIds.removeAll(foundRoleIds);

        if (!missingRoleIds.isEmpty()) {
            throw new RoleNotFoundException(missingRoleIds);
        }

        return new HashSet<>(foundRoles);
    }

    private String normalizeUsername(String username) {
        return username
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private String normalizeEmail(String email) {
        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }
}