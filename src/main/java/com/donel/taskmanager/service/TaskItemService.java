package com.donel.taskmanager.service;

import com.donel.taskmanager.dto.CreateTaskItemRequest;
import com.donel.taskmanager.dto.TaskItemResponse;
import com.donel.taskmanager.dto.UpdateTaskItemRequest;
import com.donel.taskmanager.model.Deliverable;
import com.donel.taskmanager.model.TaskItem;
import com.donel.taskmanager.model.WorkStatus;
import com.donel.taskmanager.repository.TaskItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskItemService {

    private final TaskItemRepository taskItemRepository;
    private final DeliverableService deliverableService;

    public TaskItemService(TaskItemRepository taskItemRepository, DeliverableService deliverableService) {
        this.taskItemRepository = taskItemRepository;
        this.deliverableService = deliverableService;
    }

    public List<TaskItemResponse> findByDeliverable(Long deliverableId) {
        deliverableService.requireDeliverable(deliverableId);
        return taskItemRepository.findByDeliverableId(deliverableId).stream()
                .map(TaskItemService::toResponse)
                .toList();
    }

    @Transactional
    public TaskItemResponse create(Long deliverableId, CreateTaskItemRequest request) {
        Deliverable deliverable = deliverableService.requireDeliverable(deliverableId);
        TaskItem taskItem = new TaskItem(deliverable, request.title(), request.dueDate());
        return toResponse(taskItemRepository.save(taskItem));
    }

    @Transactional
    public TaskItemResponse update(Long id, UpdateTaskItemRequest request) {
        TaskItem taskItem = requireTaskItem(id);
        taskItem.setTitle(request.title());
        taskItem.setStatus(request.status());
        taskItem.setDueDate(request.dueDate());
        return toResponse(taskItem);
    }

    @Transactional
    public TaskItemResponse markFinished(Long id) {
        TaskItem taskItem = requireTaskItem(id);
        taskItem.setStatus(WorkStatus.FINISHED);
        return toResponse(taskItem);
    }

    private TaskItem requireTaskItem(Long id) {
        return taskItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task item not found: " + id));
    }

    public static TaskItemResponse toResponse(TaskItem taskItem) {
        return new TaskItemResponse(
                taskItem.getId(),
                taskItem.getDeliverable().getId(),
                taskItem.getTitle(),
                taskItem.getStatus(),
                taskItem.getDueDate(),
                taskItem.isPastDue()
        );
    }
}
