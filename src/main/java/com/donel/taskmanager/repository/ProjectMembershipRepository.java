package com.donel.taskmanager.repository;

import com.donel.taskmanager.model.ProjectMembership;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMembershipRepository extends JpaRepository<ProjectMembership, Long> {
    List<ProjectMembership> findByProjectId(Long projectId);

    boolean existsByProjectIdAndUserId(Long projectId, Long userId);
}
