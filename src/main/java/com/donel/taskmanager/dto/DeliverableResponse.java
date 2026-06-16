package com.donel.taskmanager.dto;

import com.donel.taskmanager.model.WorkStatus;

import java.time.LocalDate;

public record DeliverableResponse(
        Long id,
        Long projectId,
        String title,
        String description,
        WorkStatus status,
        LocalDate dueDate,
        boolean pastDue
) {
}
