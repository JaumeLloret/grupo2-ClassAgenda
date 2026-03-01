package com.classagendag2.features.user.domain.model;

import java.time.LocalDateTime;

public final class User {

    private final Long id;
    private final String name;
    private final String email;
    private final LocalDateTime createdAt;

    public User(Long id, String name, String email, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
