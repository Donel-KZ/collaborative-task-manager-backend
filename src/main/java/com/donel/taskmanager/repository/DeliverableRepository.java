package com.donel.taskmanager.repository;

import com.donel.taskmanager.model.Deliverable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliverableRepository extends JpaRepository<Deliverable, Long> {
    List<Deliverable> findByProjectId(Long projectId);
}
