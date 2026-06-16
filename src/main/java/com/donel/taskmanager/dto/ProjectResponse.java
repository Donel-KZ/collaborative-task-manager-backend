package com.donel.taskmanager.dto;

import com.donel.taskmanager.model.ProjectType;
import com.donel.taskmanager.model.WorkStatus;

import java.time.LocalDate;

public record ProjectResponse(
        Long id,
        String name,
        String description,
        ProjectType type,
        WorkStatus status,
        LocalDate dueDate,
        boolean pastDue,
        UserResponse owner
) {
}
