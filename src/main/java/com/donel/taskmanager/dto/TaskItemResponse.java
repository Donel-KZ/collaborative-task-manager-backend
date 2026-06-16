package com.donel.taskmanager.dto;

import com.donel.taskmanager.model.WorkStatus;

import java.time.LocalDate;

public record TaskItemResponse(
        Long id,
        Long deliverableId,
        String title,
        WorkStatus status,
        LocalDate dueDate,
        boolean pastDue
) {
}
