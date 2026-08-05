package com.wareflow.user.controller;

import com.wareflow.user.dto.CreateUserRequest;
import com.wareflow.user.dto.UserResponse;
import com.wareflow.user.service.UserCommandService;
import com.wareflow.user.service.UserQueryService;
import com.wareflow.user.dto.ChangePasswordRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.wareflow.user.dto.UpdateUserRequest;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    public UserController(
            UserCommandService userCommandService,
            UserQueryService userQueryService
    ) {
        this.userCommandService = userCommandService;
        this.userQueryService = userQueryService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {
        UserResponse response =
                userCommandService.createUser(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long userId
    ) {
        UserResponse response =
                userQueryService.getUserById(userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAllUsers(
            @PageableDefault(
                    size = 20,
                    sort = "username"
            )
            Pageable pageable
    ) {
        Page<UserResponse> response =
                userQueryService.getAllUsers(pageable);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UserResponse response =
                userCommandService.updateUser(userId, request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(
            @PathVariable Long userId
    ) {
        UserResponse response =
                userCommandService.deactivateUser(userId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}/activate")
    public ResponseEntity<UserResponse> activateUser(
            @PathVariable Long userId
    ) {
        UserResponse response =
                userCommandService.activateUser(userId);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long userId,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        userCommandService.changePassword(userId, request);

        return ResponseEntity.noContent().build();
    }
}