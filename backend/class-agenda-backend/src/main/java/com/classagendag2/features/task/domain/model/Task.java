package com.classagendag2.features.task.domain.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class Task {
    private  Long id;
    private  String title;
    private  String description;
    private  TaskStatus status;
    private  TaskPriority priority;
    private final Long ownerId;
    private final LocalDateTime createdAt;
    public void setTitle(String title) {
        validateTitle(title);
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }


    // Constructor 1: Para reconstruir datos que vienen de la Base de Datos
    public Task(Long id, String title, String description, TaskStatus status, TaskPriority priority, Long ownerId, LocalDateTime createdAt) {
        validateTitle(title);

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.ownerId = ownerId;
        this.createdAt = createdAt;
    }

    // Constructor 2: Para Nuevas Tareas que llegan desde Internet (Postman/Frontend)
    public Task(String title, String description, TaskPriority priority, Long ownerId) {
        // Toda tarea nueva nace sin ID, con estado PENDING y con la fecha exacta truncada a segundos.
        this(null, title, description, TaskStatus.PENDING, priority, ownerId, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }

    private void validateTitle(String titleToValidate) {
        if (titleToValidate == null || titleToValidate.isBlank()) {
            throw new IllegalArgumentException("El título de la tarea no puede estar vacío.");
        }
    }

    // --- REGLA DE NEGOCIO Y SEGURIDAD: PERMISOS DE OWNER ---
    public void validateIsOwnedBy(Long requestingUserId) {
        if (requestingUserId == null) {
            throw new SecurityException("Usuario no identificado en la petición.");
        }

        // ¡ATENCIÓN! Como son objetos Long, usamos .equals(), NUNCA usamos '=='
        if (!this.ownerId.equals(requestingUserId)) {
            throw new SecurityException("Acceso denegado: No tienes permisos sobre esta tarea.");
        }
    }

    // GETTERS... (Omitidos por brevedad, debéis generar los getters para todos los atributos)
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public TaskPriority getPriority() { return priority; }
    public Long getOwnerId() { return ownerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}