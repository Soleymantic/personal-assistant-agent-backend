package com.nejat.projects.dto;

import com.nejat.projects.user.User;

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
