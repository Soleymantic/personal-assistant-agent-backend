package com.nejat.projects.aiadmin.dto;

import com.nejat.projects.aiadmin.model.AuthProvider;
import com.nejat.projects.aiadmin.model.Role;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class UserDto {
    private UUID id;
    private String email;
    private String firstName;
    private String lastName;
    private AuthProvider authProvider;
    private Set<Role> roles;
}
