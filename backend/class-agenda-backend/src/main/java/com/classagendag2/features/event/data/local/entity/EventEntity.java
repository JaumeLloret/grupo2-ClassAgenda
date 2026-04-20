package com.classagendag2.features.event.data.local.entity;

import java.time.LocalDateTime;

public final class EventEntity {

    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private int owner_id;
    private LocalDateTime createdAt;

    // Constructor vacío: Absolutamente necesario para que frameworks de BD puedan instanciarlo
    public EventEntity() {}

    // Constructor completo para cuando queremos rellenarlo de golpe
    public EventEntity(Long id, String title, String description, String status, String priority, int owner_id,  LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.owner_id = owner_id;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
