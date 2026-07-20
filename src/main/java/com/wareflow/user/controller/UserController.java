package com.wareflow.user.controller;

import com.wareflow.user.dto.CreateUserRequest;
import com.wareflow.user.dto.UserResponse;
import com.wareflow.user.service.UserCommandService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserCommandService userCommandService;

    public UserController(UserCommandService userCommandService) {
        this.userCommandService = userCommandService;
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
}