package com.donel.taskmanager.dto;

import com.donel.taskmanager.model.ProjectType;
import com.donel.taskmanager.model.WorkStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateProjectRequest(
        @NotBlank String name,
        String description,
        @NotNull ProjectType type,
        @NotNull WorkStatus status,
        @NotNull @FutureOrPresent LocalDate dueDate
) {
}
