package com.donel.taskmanager.dto;

import com.donel.taskmanager.model.WorkStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateTaskItemRequest(
        @NotBlank String title,
        @NotNull WorkStatus status,
        LocalDate dueDate
) {
}
