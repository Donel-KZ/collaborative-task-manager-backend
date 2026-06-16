package com.donel.taskmanager.dto;

import com.donel.taskmanager.model.ProjectRole;
import jakarta.validation.constraints.NotNull;

public record AddMemberRequest(
        @NotNull Long userId,
        @NotNull ProjectRole role
) {
}
