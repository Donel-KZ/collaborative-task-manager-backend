package com.donel.taskmanager.controller;

import com.donel.taskmanager.dto.CreateDeliverableRequest;
import com.donel.taskmanager.dto.DeliverableResponse;
import com.donel.taskmanager.dto.UpdateDeliverableRequest;
import com.donel.taskmanager.service.DeliverableService;
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
public class DeliverableController {

    private final DeliverableService deliverableService;

    public DeliverableController(DeliverableService deliverableService) {
        this.deliverableService = deliverableService;
    }

    @GetMapping("/projects/{projectId}/deliverables")
    public List<DeliverableResponse> findByProject(@PathVariable Long projectId) {
        return deliverableService.findByProject(projectId);
    }

    @PostMapping("/projects/{projectId}/deliverables")
    @ResponseStatus(HttpStatus.CREATED)
    public DeliverableResponse create(
            @PathVariable Long projectId,
            @Valid @RequestBody CreateDeliverableRequest request
    ) {
        return deliverableService.create(projectId, request);
    }

    @PutMapping("/deliverables/{id}")
    public DeliverableResponse update(@PathVariable Long id, @Valid @RequestBody UpdateDeliverableRequest request) {
        return deliverableService.update(id, request);
    }

    @PatchMapping("/deliverables/{id}/finish")
    public DeliverableResponse markFinished(@PathVariable Long id) {
        return deliverableService.markFinished(id);
    }
}
