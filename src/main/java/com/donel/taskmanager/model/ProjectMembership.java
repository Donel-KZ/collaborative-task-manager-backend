package com.donel.taskmanager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
        name = "project_memberships",
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "user_id"})
)
public class ProjectMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Project project;

    @ManyToOne(optional = false)
    private UserAccount user;

    @Enumerated(EnumType.STRING)
    private ProjectRole role;

    protected ProjectMembership() {
    }

    public ProjectMembership(Project project, UserAccount user, ProjectRole role) {
        this.project = project;
        this.user = user;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public Project getProject() {
        return project;
    }

    public UserAccount getUser() {
        return user;
    }

    public ProjectRole getRole() {
        return role;
    }
}
