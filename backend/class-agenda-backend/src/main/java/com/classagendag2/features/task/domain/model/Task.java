package com.classagendag2.features.task.domain.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class Task {
    private final Long id;
    private final String title;
    private final String description;
    private final TaskStatus status;
    private final TaskPriority priority;
    private final Long ownerId; // El identificador del creador (Nuestra relación)
    private final LocalDateTime createdAt;
    private final String dueDate;


    // 1. Constructor Completo: Para reconstruir datos que vienen de SQL
    public Task(Long id, String title, String description, TaskStatus status, TaskPriority priority, Long ownerId, String dueDate, LocalDateTime createdAt) {
        // Antes de aceptar los datos, los pasamos por nuestra "aduana" de validación
        validateTitle(title);
        validateOwner(ownerId);
        validateStatusAndPriority(status, priority);

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.ownerId = ownerId;
        this.dueDate = dueDate;
        this.createdAt = createdAt;


    }

    // 2. Constructor para Nuevas Tareas: Usado desde la interfaz de usuario
    public Task(String title, String description, TaskPriority priority, Long ownerId, String dueDate) {
        // La palabra 'this' invoca al constructor principal de arriba.
        // Toda tarea nueva nace sin ID (null) y con estado PENDING.
        // TRUCO PRO: Usamos .truncatedTo(ChronoUnit.SECONDS) para recortar los milisegundos de la fecha.
        // ¿Por qué? Porque Java guarda nanosegundos, pero el tipo DATETIME de SQL Server es menos preciso.
        // Si no los igualamos, las comparaciones de fechas en nuestros tests fallarán incomprensiblemente.
        this(null, title, description, TaskStatus.PENDING, priority, ownerId, dueDate, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }

    private void validateTitle(String titleToValidate) {
        // .isBlank() comprueba si el texto es nulo, está vacío o solo contiene espacios en blanco.
        if (titleToValidate == null || titleToValidate.isBlank()) {
            throw new IllegalArgumentException("El título de la tarea no puede estar vacío.");
        }
        if (titleToValidate.length() > 100) {
            throw new IllegalArgumentException("El título no puede superar los 100 caracteres.");
        }
    }

    private void validateOwner(Long ownerIdToValidate) {
        if (ownerIdToValidate == null) {
            throw new IllegalArgumentException("Toda tarea debe pertenecer a un usuario válido.");
        }
    }

    private void validateStatusAndPriority(TaskStatus statusToValidate, TaskPriority priorityToValidate) {
        if (statusToValidate == null || priorityToValidate == null) {
            throw new IllegalArgumentException("El estado y la prioridad son obligatorios.");
        }
    }

    // --- REGLA DE NEGOCIO: PERMISOS DE OWNER ---
    public void validateIsOwnedBy(Long requestingUserId) {
        if (requestingUserId == null) {
            throw new SecurityException("Usuario no identificado.");
        }

        // CONCEPTO VITAL: La variable ownerId es de tipo Long (con 'L' mayúscula, es decir, es un Objeto).
        // ¡Nunca uséis el operador '==' para comparar Objetos! El '==' comprueba si ocupan el mismo
        // sitio en la memoria RAM. Para saber si el número que llevan dentro es matemáticamente igual, usamos .equals().
        if (!this.ownerId.equals(requestingUserId)) {
            // Lanzamos una SecurityException, que detendrá el programa avisando del intento de intrusión.
            throw new SecurityException("Acceso denegado: No eres el propietario de esta tarea.");
        }
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public TaskPriority getPriority() { return priority; }
    public Long getOwnerId() { return ownerId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getDueDate() { return dueDate; }

}