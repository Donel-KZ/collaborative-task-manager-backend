package com.donel.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateTaskItemRequest(
        @NotBlank String title,
        LocalDate dueDate
) {
}
