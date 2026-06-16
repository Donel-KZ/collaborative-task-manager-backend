package com.donel.taskmanager.controller;

import com.donel.taskmanager.dto.AddMemberRequest;
import com.donel.taskmanager.dto.CreateProjectRequest;
import com.donel.taskmanager.dto.ProjectMemberResponse;
import com.donel.taskmanager.dto.ProjectResponse;
import com.donel.taskmanager.dto.UpdateProjectRequest;
import com.donel.taskmanager.service.ProjectService;
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
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping
    public List<ProjectResponse> findAll() {
        return projectService.findAll();
    }

    @GetMapping("/{id}")
    public ProjectResponse findOne(@PathVariable Long id) {
        return projectService.findOne(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(@Valid @RequestBody CreateProjectRequest request) {
        return projectService.create(request);
    }

    @PutMapping("/{id}")
    public ProjectResponse update(@PathVariable Long id, @Valid @RequestBody UpdateProjectRequest request) {
        return projectService.update(id, request);
    }

    @PatchMapping("/{id}/finish")
    public ProjectResponse markFinished(@PathVariable Long id) {
        return projectService.markFinished(id);
    }

    @GetMapping("/{id}/members")
    public List<ProjectMemberResponse> findMembers(@PathVariable Long id) {
        return projectService.findMembers(id);
    }

    @PostMapping("/{id}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectMemberResponse addMember(@PathVariable Long id, @Valid @RequestBody AddMemberRequest request) {
        return projectService.addMember(id, request);
    }
}
