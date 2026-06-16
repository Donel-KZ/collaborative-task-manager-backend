package com.donel.taskmanager.dto;

public record AuthResponse(
        String tokenType,
        String accessToken,
        UserResponse user
) {
}
