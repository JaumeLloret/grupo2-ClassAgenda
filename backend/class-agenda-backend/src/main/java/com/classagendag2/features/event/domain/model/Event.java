package com.classagendag2.features.event.domain.model;

import java.time.LocalDateTime;

public final class Event {

    private final Long id;
    private final String title;
    private final String description;
    private final String status;
    private final String priority;
    private final String location;
    private final LocalDateTime start_at;
    private final LocalDateTime end_at;
    private final int owner_id;
    private final LocalDateTime created_at;

    public Event(
            String title,
            String description,
            String location,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Long ownerId,
            LocalDateTime createdAt
    ) {
        validateDates(startAt, endAt);
        validateTitle(title);

        this.id = null; // evento nuevo
        this.title = title;
        this.description = description;
        this.status = null;     // no se usa en Sprint 7
        this.priority = null;   // no se usa en Sprint 7
        this.location = location;
        this.start_at = startAt;
        this.end_at = endAt;
        this.owner_id = ownerId.intValue();
        this.created_at = createdAt;
    }


    public Event(Long id, String title, String description, String status, String priority, String location, LocalDateTime start_at, LocalDateTime end_at, int owner_id, LocalDateTime created_at) {
        validateDates(start_at, end_at); // Validación de intervalo temporal
        validateTitle(title);

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


    private void validateDates(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin son obligatorias.");
        }
        // REGLA DE ORO: El fin no puede ser anterior al inicio.
        if (end.isBefore(start)) {
            throw new IllegalArgumentException("La fecha de fin no puede ser anterior a la de inicio.");
        }
        // REGLA EXTRA: Un evento no puede durar 0 segundos.
        if (end.isEqual(start)) {
            throw new IllegalArgumentException("El evento debe tener una duración mínima.");
        }
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("El título es obligatorio.");
    }

    //geters para que el mapper no reviente
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public String getLocation() { return location; }
    public LocalDateTime getStart_at() { return start_at; }
    public LocalDateTime getEnd_at() { return end_at; }
    public int getOwner_id() { return owner_id; }
    public LocalDateTime getCreated_at() { return created_at; }

    public void validateIsOwnedBy(Long requestingUserId) {
        if (this.owner_id != requestingUserId.intValue()) {
            throw new SecurityException("No eres el dueño del evento");
        }
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



}
