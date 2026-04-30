package com.classagendag2.features.event.data.local.entity;

import java.time.LocalDateTime;

public final class EventEntity {

    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private String location;
    private LocalDateTime start_at;
    private LocalDateTime end_at;
    private int owner_id;
    private LocalDateTime created_at;

    // Constructor vacío: Absolutamente necesario para que frameworks de BD puedan instanciarlo
    public EventEntity() {}

    // Constructor completo para cuando queremos rellenarlo de golpe
    public EventEntity(Long id, String title, String description, String status, String priority, String location, LocalDateTime start_at, LocalDateTime end_at, int owner_id,  LocalDateTime created_at) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.location = location;
        this.start_at = start_at;
        this.end_at = end_at;
        this.owner_id = owner_id;
        this.created_at = created_at;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getStart_at() {
        return start_at;
    }

    public void setStart_at(LocalDateTime start_at) {
        this.start_at = start_at;
    }

    public LocalDateTime getEnd_at() {
        return end_at;
    }

    public void setEnd_at(LocalDateTime end_at) {
        this.end_at = end_at;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }
}
