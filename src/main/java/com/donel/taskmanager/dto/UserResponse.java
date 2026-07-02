package com.donel.taskmanager.dto;

public record UserResponse(
        Long id,
        String displayName,
        String email,
        String profilePictureUrl
) {
}
