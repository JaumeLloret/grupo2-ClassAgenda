package com.classagendag2.features.task.data.local.entity;

import java.time.LocalDateTime;

public final class TaskEntity {
    private Long id;
    private String title;
    private String description;
    private String status; // Guardará el texto literal para SQL
    private String priority; // Guardará el texto literal para SQL
    private Long ownerId;
    private LocalDateTime createdAt;

    // Constructor vacío obligatorio para los estándares de serialización de Java
    public TaskEntity() {}

    public TaskEntity(Long id, String title, String description, String status, String priority, Long ownerId, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
    }

    // --- GETTERS Y SETTERS TRADICIONALES ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}