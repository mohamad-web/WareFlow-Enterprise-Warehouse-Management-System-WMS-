package com.wareflow.user.service;

import com.wareflow.user.UserRepository;
import com.wareflow.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserQueryService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserQueryService(
            UserRepository userRepository,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
}