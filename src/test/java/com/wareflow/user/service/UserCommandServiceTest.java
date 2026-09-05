package com.wareflow.user.service;

import com.wareflow.role.Role;
import com.wareflow.role.RoleRepository;
import com.wareflow.user.User;
import com.wareflow.user.UserRepository;
import com.wareflow.user.dto.ChangePasswordRequest;
import com.wareflow.user.dto.CreateUserRequest;
import com.wareflow.role.dto.RoleResponse;
import com.wareflow.user.dto.UserResponse;
import com.wareflow.user.exception.UsernameAlreadyExistsException;
import com.wareflow.user.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.wareflow.user.dto.UpdateUserRequest;
import com.wareflow.user.exception.EmailAlreadyExistsException;
import com.wareflow.user.exception.UserNotFoundException;
import com.wareflow.role.exception.RoleNotFoundException;

import java.util.Optional;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    private UserCommandService userCommandService;

    @BeforeEach
    void setUp() {
        userCommandService = new UserCommandService(
                userRepository,
                roleRepository,
                passwordEncoder,
                userMapper
        );
    }

    @Test
    void createUserShouldCreateUserSuccessfully() {
        CreateUserRequest request = new CreateUserRequest(
                "Mohamad",
                "VeryStrongPassword123",
                "Mohamad",
                "Alnaser",
                "Mohamad@Example.COM",
                Set.of(1L)
        );

        Role adminRole = mock(Role.class);

        when(adminRole.getId()).thenReturn(1L);


        when(userRepository.existsByUsername("mohamad"))
                .thenReturn(false);

        when(userRepository.existsByEmail("mohamad@example.com"))
                .thenReturn(false);

        when(roleRepository.findAllById(Set.of(1L)))
                .thenReturn(List.of(adminRole));

        when(passwordEncoder.encode("VeryStrongPassword123"))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse expectedResponse = new UserResponse(
                1L,
                "mohamad",
                "Mohamad",
                "Alnaser",
                "mohamad@example.com",
                true,
                Set.of(
                        new RoleResponse(
                                1L,
                                "ADMIN",
                                "Full system administration access"
                        )
                ),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(userMapper.toResponse(any(User.class)))
                .thenReturn(expectedResponse);

        UserResponse response =
                userCommandService.createUser(request);

        assertNotNull(response);
        assertEquals("mohamad", response.username());
        assertEquals(
                "mohamad@example.com",
                response.email()
        );

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository)
                .save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals("mohamad", savedUser.getUsername());
        assertEquals(
                "mohamad@example.com",
                savedUser.getEmail()
        );
        assertEquals(
                "encoded-password",
                savedUser.getPasswordHash()
        );
        assertTrue(savedUser.isActive());
        assertEquals(1, savedUser.getRoles().size());

        verify(passwordEncoder)
                .encode("VeryStrongPassword123");
    }

    @Test
    void createUserShouldThrowWhenUsernameAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest(
                "Mohamad",
                "VeryStrongPassword123",
                "Mohamad",
                "Alnaser",
                "mohamad@example.com",
                Set.of(1L)
        );

        when(userRepository.existsByUsername("mohamad"))
                .thenReturn(true);

        assertThrows(
                UsernameAlreadyExistsException.class,
                () -> userCommandService.createUser(request)
        );

        verify(userRepository, never())
                .save(any(User.class));

        verifyNoInteractions(
                roleRepository,
                passwordEncoder,
                userMapper
        );
    }

    @Test
    void createUserShouldThrowWhenEmailAlreadyExists() {
        CreateUserRequest request = new CreateUserRequest(
                "mohamad",
                "VeryStrongPassword123",
                "Mohamad",
                "Alnaser",
                "mohamad@example.com",
                Set.of(1L)
        );

        when(userRepository.existsByUsername("mohamad"))
                .thenReturn(false);

        when(userRepository.existsByEmail("mohamad@example.com"))
                .thenReturn(true);

        assertThrows(
                com.wareflow.user.exception.EmailAlreadyExistsException.class,
                () -> userCommandService.createUser(request)
        );

        verify(userRepository, never())
                .save(any(User.class));

        verifyNoInteractions(
                roleRepository,
                passwordEncoder,
                userMapper
        );
    }

    @Test
    void createUserShouldThrowWhenRoleDoesNotExist() {
        CreateUserRequest request = new CreateUserRequest(
                "mohamad",
                "VeryStrongPassword123",
                "Mohamad",
                "Alnaser",
                "mohamad@example.com",
                Set.of(1L, 999L)
        );

        Role adminRole = new Role(
                "ADMIN",
                "Full system administration access"
        );

        when(userRepository.existsByUsername("mohamad"))
                .thenReturn(false);

        when(userRepository.existsByEmail("mohamad@example.com"))
                .thenReturn(false);

        when(roleRepository.findAllById(Set.of(1L, 999L)))
                .thenReturn(List.of(adminRole));

        assertThrows(
                com.wareflow.role.exception.RoleNotFoundException.class,
                () -> userCommandService.createUser(request)
        );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void updateUserShouldUpdateUserSuccessfully() {
        Long userId = 5L;

        UpdateUserRequest request = new UpdateUserRequest(
                "Mohamad",
                "Khalaf Alnaser",
                "Updated@Example.COM",
                Set.of(1L, 2L)
        );

        User user = mock(User.class);

        Role adminRole = mock(Role.class);
        Role managerRole = mock(Role.class);

        when(adminRole.getId()).thenReturn(1L);
        when(managerRole.getId()).thenReturn(2L);

        when(userRepository.findWithRolesById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmailAndIdNot(
                "updated@example.com",
                userId
        )).thenReturn(false);

        when(roleRepository.findAllById(Set.of(1L, 2L)))
                .thenReturn(List.of(adminRole, managerRole));

        when(userRepository.save(user))
                .thenReturn(user);

        UserResponse expectedResponse = mock(UserResponse.class);

        when(userMapper.toResponse(user))
                .thenReturn(expectedResponse);

        UserResponse response =
                userCommandService.updateUser(userId, request);

        assertSame(expectedResponse, response);

        verify(user).updateProfile(
                "Mohamad",
                "Khalaf Alnaser",
                "updated@example.com"
        );

        verify(user).assignRoles(
                argThat(roles ->
                        roles.size() == 2
                                && roles.contains(adminRole)
                                && roles.contains(managerRole)
                )
        );

        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    void updateUserShouldThrowWhenUserDoesNotExist() {
        Long userId = 999L;

        UpdateUserRequest request = new UpdateUserRequest(
                "Mohamad",
                "Alnaser",
                "mohamad@example.com",
                Set.of(1L)
        );

        when(userRepository.findWithRolesById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userCommandService.updateUser(
                        userId,
                        request
                )
        );

        verify(userRepository, never())
                .existsByEmailAndIdNot(
                        anyString(),
                        anyLong()
                );

        verifyNoInteractions(roleRepository);

        verify(userRepository, never())
                .save(any(User.class));

        verifyNoInteractions(userMapper);
    }

    @Test
    void updateUserShouldThrowWhenRequestedRoleDoesNotExist() {
        Long userId = 5L;

        UpdateUserRequest request = new UpdateUserRequest(
                "Mohamad",
                "Alnaser",
                "mohamad@example.com",
                Set.of(1L, 999L)
        );

        User user = mock(User.class);
        Role adminRole = mock(Role.class);

        when(adminRole.getId()).thenReturn(1L);

        when(userRepository.findWithRolesById(userId))
                .thenReturn(Optional.of(user));

        when(userRepository.existsByEmailAndIdNot(
                "mohamad@example.com",
                userId
        )).thenReturn(false);

        when(roleRepository.findAllById(Set.of(1L, 999L)))
                .thenReturn(List.of(adminRole));

        assertThrows(
                RoleNotFoundException.class,
                () -> userCommandService.updateUser(
                        userId,
                        request
                )
        );

        verify(user, never()).updateProfile(
                anyString(),
                anyString(),
                anyString()
        );

        verify(user, never())
                .assignRoles(any());

        verify(userRepository, never())
                .save(any(User.class));

        verifyNoInteractions(userMapper);
    }

    @Test
    void deactivateUserShouldDeactivateUserSuccessfully() {
        Long userId = 5L;

        User user = mock(User.class);
        UserResponse expectedResponse = mock(UserResponse.class);

        when(userRepository.findWithRolesById(userId))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(expectedResponse);

        UserResponse response =
                userCommandService.deactivateUser(userId);

        assertSame(expectedResponse, response);

        verify(user).deactivate();
        verify(userMapper).toResponse(user);
    }

    @Test
    void activateUserShouldActivateUserSuccessfully() {
        Long userId = 5L;

        User user = mock(User.class);
        UserResponse expectedResponse = mock(UserResponse.class);

        when(userRepository.findWithRolesById(userId))
                .thenReturn(Optional.of(user));

        when(userMapper.toResponse(user))
                .thenReturn(expectedResponse);

        UserResponse response =
                userCommandService.activateUser(userId);

        assertSame(expectedResponse, response);

        verify(user).activate();
        verify(userMapper).toResponse(user);
    }

    @Test
    void deactivateUserShouldThrowWhenUserDoesNotExist() {
        Long userId = 999L;

        when(userRepository.findWithRolesById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userCommandService.deactivateUser(userId)
        );

        verifyNoInteractions(userMapper);
    }

    @Test
    void changePasswordShouldChangePasswordSuccessfully() {
        Long userId = 5L;

        ChangePasswordRequest request =
                new ChangePasswordRequest(
                        "NewSecurePassword123!"
                );

        User user = mock(User.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode(
                "NewSecurePassword123!"
        )).thenReturn("encoded-new-password");

        userCommandService.changePassword(
                userId,
                request
        );

        verify(passwordEncoder)
                .encode("NewSecurePassword123!");

        verify(user)
                .changePasswordHash(
                        "encoded-new-password"
                );
    }

    @Test
    void changePasswordShouldThrowWhenUserDoesNotExist() {
        Long userId = 999L;

        ChangePasswordRequest request =
                new ChangePasswordRequest(
                        "NewSecurePassword123!"
                );

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userCommandService.changePassword(
                        userId,
                        request
                )
        );

        verifyNoInteractions(passwordEncoder);
    }
}