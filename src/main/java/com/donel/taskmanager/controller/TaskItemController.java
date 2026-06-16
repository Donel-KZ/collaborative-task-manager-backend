package com.donel.taskmanager.controller;

import com.donel.taskmanager.dto.CreateTaskItemRequest;
import com.donel.taskmanager.dto.TaskItemResponse;
import com.donel.taskmanager.dto.UpdateTaskItemRequest;
import com.donel.taskmanager.service.TaskItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskItemController {

    private final TaskItemService taskItemService;

    public TaskItemController(TaskItemService taskItemService) {
        this.taskItemService = taskItemService;
    }

    @GetMapping("/deliverables/{deliverableId}/tasks")
    public List<TaskItemResponse> findByDeliverable(@PathVariable Long deliverableId) {
        return taskItemService.findByDeliverable(deliverableId);
    }

    @PostMapping("/deliverables/{deliverableId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskItemResponse create(
            @PathVariable Long deliverableId,
            @Valid @RequestBody CreateTaskItemRequest request
    ) {
        return taskItemService.create(deliverableId, request);
    }

    @PutMapping("/tasks/{id}")
    public TaskItemResponse update(@PathVariable Long id, @Valid @RequestBody UpdateTaskItemRequest request) {
        return taskItemService.update(id, request);
    }

    @PatchMapping("/tasks/{id}/finish")
    public TaskItemResponse markFinished(@PathVariable Long id) {
        return taskItemService.markFinished(id);
    }
}
