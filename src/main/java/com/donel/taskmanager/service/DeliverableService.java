package com.donel.taskmanager.service;

import com.donel.taskmanager.dto.CreateDeliverableRequest;
import com.donel.taskmanager.dto.DeliverableResponse;
import com.donel.taskmanager.dto.UpdateDeliverableRequest;
import com.donel.taskmanager.model.Deliverable;
import com.donel.taskmanager.model.Project;
import com.donel.taskmanager.model.WorkStatus;
import com.donel.taskmanager.repository.DeliverableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeliverableService {

    private final DeliverableRepository deliverableRepository;
    private final ProjectService projectService;

    public DeliverableService(DeliverableRepository deliverableRepository, ProjectService projectService) {
        this.deliverableRepository = deliverableRepository;
        this.projectService = projectService;
    }

    public List<DeliverableResponse> findByProject(Long projectId) {
        projectService.requireProject(projectId);
        return deliverableRepository.findByProjectId(projectId).stream()
                .map(DeliverableService::toResponse)
                .toList();
    }

    @Transactional
    public DeliverableResponse create(Long projectId, CreateDeliverableRequest request) {
        Project project = projectService.requireProject(projectId);
        Deliverable deliverable = new Deliverable(project, request.title(), request.description(), request.dueDate());
        return toResponse(deliverableRepository.save(deliverable));
    }

    @Transactional
    public DeliverableResponse update(Long id, UpdateDeliverableRequest request) {
        Deliverable deliverable = requireDeliverable(id);
        deliverable.setTitle(request.title());
        deliverable.setDescription(request.description());
        deliverable.setStatus(request.status());
        deliverable.setDueDate(request.dueDate());
        return toResponse(deliverable);
    }

    @Transactional
    public DeliverableResponse markFinished(Long id) {
        Deliverable deliverable = requireDeliverable(id);
        deliverable.setStatus(WorkStatus.FINISHED);
        return toResponse(deliverable);
    }

    public Deliverable requireDeliverable(Long id) {
        return deliverableRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deliverable not found: " + id));
    }

    public static DeliverableResponse toResponse(Deliverable deliverable) {
        return new DeliverableResponse(
                deliverable.getId(),
                deliverable.getProject().getId(),
                deliverable.getTitle(),
                deliverable.getDescription(),
                deliverable.getStatus(),
                deliverable.getDueDate(),
                deliverable.isPastDue()
        );
    }
}
