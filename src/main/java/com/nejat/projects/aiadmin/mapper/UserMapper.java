package com.nejat.projects.aiadmin.mapper;

import com.nejat.projects.aiadmin.dto.UserDto;
import com.nejat.projects.aiadmin.model.User;

public final class UserMapper {
    private UserMapper() {
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .authProvider(user.getAuthProvider())
                .roles(user.getRoles())
                .build();
    }
}
