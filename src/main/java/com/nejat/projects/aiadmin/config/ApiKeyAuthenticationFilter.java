package com.nejat.projects.aiadmin.config;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final SecurityProperties securityProperties;

    public ApiKeyAuthenticationFilter(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String expectedApiKey = securityProperties.getApiKey();
        if (!StringUtils.hasText(expectedApiKey)) {
            response.sendError(HttpStatus.SERVICE_UNAVAILABLE.value(), "API key not configured");
            return;
        }

        String providedApiKey = request.getHeader("X-API-KEY");
        if (!expectedApiKey.equals(providedApiKey)) {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
