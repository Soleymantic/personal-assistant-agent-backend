package com.nejat.projects.aiadmin.service;

import com.nejat.projects.aiadmin.dto.AuthResponse;
import com.nejat.projects.aiadmin.dto.LoginRequest;
import com.nejat.projects.aiadmin.dto.RefreshTokenRequest;
import com.nejat.projects.aiadmin.dto.RegisterRequest;
import com.nejat.projects.aiadmin.dto.UserDto;
import com.nejat.projects.aiadmin.exception.ApiException;
import com.nejat.projects.aiadmin.security.jwt.JwtTokenProvider;
import com.nejat.projects.aiadmin.mapper.UserMapper;
import com.nejat.projects.aiadmin.model.AuthProvider;
import com.nejat.projects.aiadmin.model.Role;
import com.nejat.projects.aiadmin.model.User;
import com.nejat.projects.aiadmin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public UserDto register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Email already registered");
        }
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .authProvider(AuthProvider.LOCAL)
                .roles(Set.of(Role.ROLE_USER))
                .build();
        return UserMapper.toDto(userRepository.save(user));
    }

    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            User user = userService.findByEmail(authentication.getName());
            if (user.getAuthProvider() != AuthProvider.LOCAL) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "Use OAuth2 login for this account");
            }
            return tokensFor(user);
        } catch (AuthenticationException ex) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())
                || !jwtTokenProvider.isRefreshToken(request.getRefreshToken())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
        String email = jwtTokenProvider.getEmailFromToken(request.getRefreshToken());
        User user = userService.findByEmail(email);
        return tokensFor(user);
    }

    public AuthResponse tokensFor(User user) {
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserMapper.toDto(user))
                .build();
    }
}
