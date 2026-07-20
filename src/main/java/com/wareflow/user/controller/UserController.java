package com.wareflow.user.controller;

import com.wareflow.user.UserService;
import com.wareflow.user.dto.CreateUserRequest;
import com.wareflow.user.dto.UserResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request
    ) {

        UserResponse response =
                userService.createUser(request);

        URI location = URI.create(
                "/api/v1/users/" + response.id()
        );

        return ResponseEntity
                .created(location)
                .body(response);
    }
}