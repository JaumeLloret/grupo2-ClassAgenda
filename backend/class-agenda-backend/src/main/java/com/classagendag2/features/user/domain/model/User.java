package com.classagendag2.features.user.domain.model;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public final class User {

    private final Long id;
    private final String name;
    private final String email;
    private final LocalDateTime createdAt;

    // 1. Constructor COMPLETO: Se usa cuando sacamos datos que ya existen en la base de datos
    public User(Long id, String name, String email, LocalDateTime createdAt) {
        validateName(name);
        validateEmail(email);
        validateCreateAt(createdAt);

        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }

    //2. Constructor PARA NUEVOS USUARIOS: Se usa cuando alguien se registra de cero
    public User (String name, String email) {
        this(null, name, email, LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
    }

    private void validateName(String nameToValidate) {
        if (nameToValidate == null || nameToValidate.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        if (nameToValidate.length() > 80) {
            throw new IllegalArgumentException("El nombre no puede superar los 80 caracteres");
        }
    }

    private void validateEmail(String emailToValidate) {
        if (emailToValidate == null || emailToValidate.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacio");
        }
        if (emailToValidate.length() > 255) {
            throw new IllegalArgumentException("El nombre no puede superar los 255 caracteres");
        }
        if (!emailToValidate.contains("@")) {
            throw new IllegalArgumentException("El correo debe tener formato válido (contener '@)");
        }
    }


    private void validateCreateAt(LocalDateTime dateToValidate) {
        if (dateToValidate == null) {
            throw new IllegalArgumentException("La fecha de creación no puede ser nula");
        }
    }

    public Long getId() {return id;}
    public String getName() {return name;}
    public String getEmail() {return email;}
    public LocalDateTime getCreatedAt() {return createdAt;}
}
