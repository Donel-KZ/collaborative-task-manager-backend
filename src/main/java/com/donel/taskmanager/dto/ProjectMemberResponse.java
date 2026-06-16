package com.donel.taskmanager.dto;

import com.donel.taskmanager.model.ProjectRole;

public record ProjectMemberResponse(
        Long id,
        UserResponse user,
        ProjectRole role
) {
}
