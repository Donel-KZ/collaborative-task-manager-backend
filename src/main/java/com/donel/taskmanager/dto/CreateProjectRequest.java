package com.donel.taskmanager.dto;

import com.donel.taskmanager.model.ProjectType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateProjectRequest(
        @NotBlank String name,
        String description,
        @NotNull ProjectType type,
        @NotNull @FutureOrPresent LocalDate dueDate,
        @NotNull Long ownerId
) {
}
