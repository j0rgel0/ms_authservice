// src/main/java/com/lox/authservice/mapper/UserMapper.java

package com.lox.authservice.mapper;

import com.lox.authservice.models.User;
import com.lox.authservice.models.requests.CreateUserRequest;
import com.lox.authservice.models.responses.RegisterResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(CreateUserRequest createUserRequest);
    RegisterResponse toRegisterResponse(User user);
}
