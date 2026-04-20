package com.classagendag2.features.event.domain.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class Event {

    private final Long id;
    private final String title;
    private final String description;
    private final String status;
    private final String priority;
    private final int owner_id;
    private final LocalDateTime createdAt;

    public Event(Long id, String title, String description, String status, String priority, int owner_id, LocalDateTime createdAt) {
        validateTitle(title);
        validateDescription(description);
        validateStatus(status);
        validatePriority(priority);
        validateOwner_id(owner_id);
        validateCreateAt(createdAt);

        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.owner_id = owner_id;
        this.createdAt = createdAt;
    }

    /* VER QUE NOS HACE FALTA VALIDAR */
    private void validateOwner_id(int ownerId) {
    }

    private void validatePriority(String priority) {
    }

    private void validateStatus(String status) {
    }

    private void validateDescription(String description) {
    }

    private void validateTitle(String title) {
    }


    //2. Constructor PARA NUEVOS USUARIOS: Se usa cuando alguien se registra de cero
    /*public Event(String name, String email) {
        this(null, name, email, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }*/

    /*private void validateName(String nameToValidate) {
        if (nameToValidate == null || nameToValidate.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        if (nameToValidate.length() > 80) {
            throw new IllegalArgumentException("El nombre no puede superar los 80 caracteres");
        }
    }*/

    /*private void validateEmail(String emailToValidate) {
        if (emailToValidate == null || emailToValidate.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacio");
        }
        if (emailToValidate.length() > 255) {
            throw new IllegalArgumentException("El nombre no puede superar los 255 caracteres");
        }
        if (!emailToValidate.contains("@")) {
            throw new IllegalArgumentException("El correo debe tener formato válido (contener '@)");
        }
    }*/


    private void validateCreateAt(LocalDateTime dateToValidate) {
        if (dateToValidate == null) {
            throw new IllegalArgumentException("La fecha de creación no puede ser nula");
        }
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
