// src/main/java/com/lox/authservice/mapper/UserMapper.java

package com.lox.authservice.api.mapper;

import com.lox.authservice.api.models.responses.RegisterResponse;
import com.lox.authservice.api.models.User;
import com.lox.authservice.api.models.requests.CreateUserRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toUser(CreateUserRequest createUserRequest);
    RegisterResponse toRegisterResponse(User user);
}
