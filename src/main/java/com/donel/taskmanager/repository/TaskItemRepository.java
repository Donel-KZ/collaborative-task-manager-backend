package com.donel.taskmanager.repository;

import com.donel.taskmanager.model.TaskItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskItemRepository extends JpaRepository<TaskItem, Long> {
    List<TaskItem> findByDeliverableId(Long deliverableId);
}
