package com.donel.taskmanager.service;

import com.donel.taskmanager.dto.AddMemberRequest;
import com.donel.taskmanager.dto.CreateProjectRequest;
import com.donel.taskmanager.dto.ProjectMemberResponse;
import com.donel.taskmanager.dto.ProjectResponse;
import com.donel.taskmanager.dto.UpdateProjectRequest;
import com.donel.taskmanager.model.Project;
import com.donel.taskmanager.model.ProjectMembership;
import com.donel.taskmanager.model.ProjectRole;
import com.donel.taskmanager.model.ProjectType;
import com.donel.taskmanager.model.UserAccount;
import com.donel.taskmanager.repository.ProjectMembershipRepository;
import com.donel.taskmanager.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMembershipRepository membershipRepository;
    private final UserService userService;

    public ProjectService(
            ProjectRepository projectRepository,
            ProjectMembershipRepository membershipRepository,
            UserService userService
    ) {
        this.projectRepository = projectRepository;
        this.membershipRepository = membershipRepository;
        this.userService = userService;
    }

    public List<ProjectResponse> findAll() {
        return projectRepository.findAll().stream()
                .map(ProjectService::toResponse)
                .toList();
    }

    public ProjectResponse findOne(Long id) {
        return toResponse(requireProject(id));
    }

    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {
        UserAccount owner = userService.requireUser(request.ownerId());
        Project project = new Project(request.name(), request.description(), request.type(), request.dueDate(), owner);
        Project saved = projectRepository.save(project);
        membershipRepository.save(new ProjectMembership(saved, owner, ProjectRole.OWNER));
        return toResponse(saved);
    }

    @Transactional
    public ProjectResponse update(Long id, UpdateProjectRequest request) {
        Project project = requireProject(id);
        project.setName(request.name());
        project.setDescription(request.description());
        project.setType(request.type());
        project.setStatus(request.status());
        project.setDueDate(request.dueDate());
        return toResponse(project);
    }

    @Transactional
    public ProjectResponse markFinished(Long id) {
        Project project = requireProject(id);
        project.setStatus(com.donel.taskmanager.model.WorkStatus.FINISHED);
        return toResponse(project);
    }

    @Transactional
    public ProjectMemberResponse addMember(Long projectId, AddMemberRequest request) {
        Project project = requireProject(projectId);
        if (project.getType() != ProjectType.GROUP) {
            throw new IllegalArgumentException("Members can only be added to group projects.");
        }
        if (membershipRepository.existsByProjectIdAndUserId(projectId, request.userId())) {
            throw new IllegalArgumentException("This user is already a project member.");
        }
        UserAccount user = userService.requireUser(request.userId());
        ProjectMembership membership = new ProjectMembership(project, user, request.role());
        return toMemberResponse(membershipRepository.save(membership));
    }

    public List<ProjectMemberResponse> findMembers(Long projectId) {
        requireProject(projectId);
        return membershipRepository.findByProjectId(projectId).stream()
                .map(ProjectService::toMemberResponse)
                .toList();
    }

    public Project requireProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + id));
    }

    public static ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getType(),
                project.getStatus(),
                project.getDueDate(),
                project.isPastDue(),
                UserService.toResponse(project.getOwner())
        );
    }

    private static ProjectMemberResponse toMemberResponse(ProjectMembership membership) {
        return new ProjectMemberResponse(
                membership.getId(),
                UserService.toResponse(membership.getUser()),
                membership.getRole()
        );
    }
}
